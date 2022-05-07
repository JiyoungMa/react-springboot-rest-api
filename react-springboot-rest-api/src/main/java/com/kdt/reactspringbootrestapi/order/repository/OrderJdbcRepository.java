package com.kdt.reactspringbootrestapi.order.repository;

import com.kdt.reactspringbootrestapi.JdbcUtils;
import com.kdt.reactspringbootrestapi.exception.JdbcFailException;
import com.kdt.reactspringbootrestapi.order.Email;
import com.kdt.reactspringbootrestapi.order.Order;
import com.kdt.reactspringbootrestapi.order.OrderBook;
import com.kdt.reactspringbootrestapi.order.OrderStatus;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Repository;

import org.simpleflatmapper.jdbc.spring.JdbcTemplateMapperFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class OrderJdbcRepository implements OrderRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public OrderJdbcRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public List<Order> findAll() {
        List<Order> orderList = jdbcTemplate.query("select * from orders", orderRowMapper );
        Map<UUID, Order> orderMap = new HashMap<>();
        orderList.forEach(o -> orderMap.put(o.getOrderId(),o));
        List<OrderBook> orderBookList = jdbcTemplate.query("select * from order_books",orderBookRowMapper);
        orderBookList.forEach(orderBook -> orderMap.get(orderBook.orderId()).getOrderBooks().add(orderBook));

        return orderList;
    }

    @Override
    public Order insert(Order order) {
        var insertOrderResult = jdbcTemplate.update("insert into orders(order_id, email, address, postcode, order_status, created_at, updated_at) Values(UNHEX(REPLACE( :order_id, '-', '')), :email, :address, :postcode, :order_status, :created_at, :updated_at)",
                toParamMap(order));

        if (insertOrderResult != 1)
            throw new JdbcFailException("Order의 저장이 실패했습니다");

        var insertOrderBookResult = jdbcTemplate.getJdbcTemplate().batchUpdate("insert into order_books(order_id, book_id, price, quantity) " +
                                        "VALUES (UNHEX(REPLACE(?, '-', '')), UNHEX(REPLACE( ?, '-', '')), ?, ?)",
                                        new BatchPreparedStatementSetter() {
                                            @Override
                                            public void setValues(PreparedStatement ps, int i) throws SQLException {
                                                OrderBook orderBook = order.getOrderBooks().get(i);
                                                ps.setBytes(1,orderBook.orderId().toString().getBytes());
                                                ps.setBytes(2,orderBook.bookId().toString().getBytes());
                                                ps.setLong(3,orderBook.price());
                                                ps.setInt(4,orderBook.quantity());
                                            }

                                            @Override
                                            public int getBatchSize() {
                                                return order.getOrderBooks().size();
                                            }
                                        });
        int totalOrderBookResult = 0;
        for (int i = 0; i<insertOrderBookResult.length; i++)
            totalOrderBookResult += insertOrderBookResult[i];

        if (totalOrderBookResult != order.getOrderBooks().size())
            throw new JdbcFailException("OrderBook의 저장이 실패했습니다");

        return order;
    }

    @Override
    public Order update(Order order) {
        var updateResult = jdbcTemplate.update(
                "UPDATE orders SET email = :email, address = :address, postcode = :postcode, order_status = :order_status, updated_at = :updated_at where order_id = UNHEX(REPLACE( :order_id, '-', ''))",
                toParamMap(order));
        if (updateResult != 1)
            throw new JdbcFailException("Order의 Update가 실패했습니다.");

        return order;
    }

    @Override
    public Optional<Order> findById(UUID orderId) {
        try{
            var order = jdbcTemplate.queryForObject("select * from orders where order_id = UNHEX(REPLACE( :order_id, '-', ''))",
                    Collections.singletonMap("order_id", orderId.toString().getBytes()),orderRowMapper);
            order.setOrderBooks(jdbcTemplate.query("select * from order_books where order_id = UNHEX(REPLACE( :order_id, '-', ''))",Collections.singletonMap("order_id", orderId.toString().getBytes()),orderBookRowMapper));
            return Optional.of(order);
        }catch (EmptyResultDataAccessException e){
            return Optional.empty();
        }
    }

    @Override
    public List<Order> findByEmail(Email email) {
        List<Order> orderList = jdbcTemplate.query("select * from orders where email = :email", Collections.singletonMap("email", email.getEmail()),orderRowMapper);
        Map<UUID, Order> orderMap = new HashMap<>();
        orderList.forEach(o -> orderMap.put(o.getOrderId(),o));
        List<OrderBook> orderBookList = jdbcTemplate.query("select * from order_books where order_id in (select order_id from orders where email = :email)", Collections.singletonMap("email", email.getEmail()) ,orderBookRowMapper);
        orderBookList.forEach(orderBook -> orderMap.get(orderBook.orderId()).getOrderBooks().add(orderBook));
        return orderList;
    }

    @Override
    public void deleteAll() {
        jdbcTemplate.update("delete from orders",Collections.emptyMap());
    }

    @Override
    public boolean deleteOrder(Order order) {
        var deleteResult = jdbcTemplate.update("delete from orders where order_id = UNHEX(REPLACE( :order_id, '-', ''))",
                Collections.singletonMap("order_id", order.getOrderId().toString().getBytes()));

        if (deleteResult != 1)
            return false;
        return true;
    }

    private static final RowMapper<Order> orderRowMapper = (resultSet, i) -> {
        var orderId = JdbcUtils.toUUID(resultSet.getBytes("order_id"));
        var email = new Email(resultSet.getString("email"));
        var address = resultSet.getString("address");
        var postcode = resultSet.getString("postcode");
        var orderStatus = OrderStatus.valueOf(resultSet.getString("order_status"));
        var createdAt = JdbcUtils.toLocalDateTime(resultSet.getTimestamp("created_at"));
        var updatedAt = JdbcUtils.toLocalDateTime(resultSet.getTimestamp("updated_at"));

        return new Order(orderId, email,address, postcode, new ArrayList<OrderBook>(), orderStatus, createdAt, updatedAt );
    };

    private static final RowMapper<OrderBook> orderBookRowMapper = (resultSet, i) -> {
        var orderId = JdbcUtils.toUUID(resultSet.getBytes("order_id"));
        var book_id = JdbcUtils.toUUID(resultSet.getBytes("book_id"));
        var price = resultSet.getLong("price");
        var quantity = resultSet.getInt("quantity");

        return new OrderBook(orderId, book_id, quantity, price);
    };

    private Map<String, Object> toParamMap(Order order){
        var paramMap = new HashMap<String, Object>();
        paramMap.put("order_id", order.getOrderId().toString().getBytes());
        paramMap.put("email", order.getEmail().getEmail());
        paramMap.put("address", order.getAddress());
        paramMap.put("postcode", order.getPostcode());
        paramMap.put("order_status", order.getOrderStatus().toString());
        paramMap.put("created_at", order.getCreatedAt());
        paramMap.put("updated_at", order.getUpdatedAt());

        return paramMap;
    }



}
