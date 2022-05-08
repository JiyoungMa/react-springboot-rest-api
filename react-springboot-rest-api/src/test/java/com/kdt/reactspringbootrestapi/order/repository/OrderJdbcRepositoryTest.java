package com.kdt.reactspringbootrestapi.order.repository;

import com.kdt.reactspringbootrestapi.author.Author;
import com.kdt.reactspringbootrestapi.author.repository.AuthorRepository;
import com.kdt.reactspringbootrestapi.book.Book;
import com.kdt.reactspringbootrestapi.book.Genre;
import com.kdt.reactspringbootrestapi.book.repository.BookRepository;
import com.kdt.reactspringbootrestapi.order.Email;
import com.kdt.reactspringbootrestapi.order.Order;
import com.kdt.reactspringbootrestapi.order.OrderBook;
import com.kdt.reactspringbootrestapi.order.OrderStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "classpath:application-test.yml")
class OrderJdbcRepositoryTest {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    BookRepository bookRepository;

    @Autowired
    AuthorRepository authorRepository;

    Author author = new Author(UUID.randomUUID(),"author");
    Book book = new Book(UUID.randomUUID(),"book",author.getAuthorId(), Genre.COMIC_BOOK,10000);
    List<OrderBook> orderBookList = new ArrayList<>();
    Order order = new Order(UUID.randomUUID(), new Email("tester@gmail.com"), "Address Example", "Postcode Example", orderBookList, OrderStatus.ACCEPTED);

    @BeforeEach
    void setup() {
        authorRepository.insert(author);
        bookRepository.insert(book);
        orderBookList.add(new OrderBook(order.getOrderId(), book.getBookId(), 1, book.getPrice()));
        orderBookList.add(new OrderBook(order.getOrderId(), book.getBookId(), 10, book.getPrice()*10));
        orderRepository.insert(order);

    }

    @AfterEach
    void cleanup(){
        authorRepository.deleteAll();
        bookRepository.deleteAll();
    }

    @Test
    void findAll() {
        List<Order> orderList = new ArrayList<>();
        orderList.add(order);
        for (int i = 0; i<9; i++){
            List<OrderBook> newOrderBookList = new ArrayList<>();
            Order newOrder = new Order(UUID.randomUUID(), new Email(MessageFormat.format("tester{0}@gmail.com", i)), MessageFormat.format("Address Example {0}", i), MessageFormat.format("Postcode Example {0}", i), newOrderBookList, OrderStatus.ACCEPTED);
            for (int j = 0; j<3; j++){
                newOrderBookList.add(new OrderBook(newOrder.getOrderId(), book.getBookId(), i*10 + j, book.getPrice() * (i*10 + j)));
            }
            orderList.add(newOrder);
            orderRepository.insert(newOrder);
        }

        var foundList = orderRepository.findAll();

        assertThat(foundList).usingRecursiveFieldByFieldElementComparator()
                .hasSameElementsAs(orderList);
    }

    @Test
    void insert() {
        Order newOrder = new Order(UUID.randomUUID(), new Email("newTester@gmail.com"), "new Address", "new Postcode", new ArrayList<>(), OrderStatus.ACCEPTED);
        newOrder.getOrderBooks().add(new OrderBook(newOrder.getOrderId(), book.getBookId(), 3 , book.getPrice()*3));

        var insertResult = orderRepository.insert(newOrder);

        assertThat(insertResult).as("Order").usingRecursiveComparison().isEqualTo(newOrder);

    }

    @Test
    void update() {
        order.setOrderStatus(OrderStatus.CANCELLED);

        var updatedResult = orderRepository.update(order);

        assertThat(updatedResult).as("Order").usingRecursiveComparison().isEqualTo(order);
    }

    @Test
    void findById() {
        var findResult = orderRepository.findById(order.getOrderId());

        assertThat(findResult.isEmpty()).isFalse();
        assertThat(findResult.get()).as("Order").usingRecursiveComparison().isEqualTo(order);
    }

    @Test
    void findByEmail() {
        List<Order> orderList = new ArrayList<>();
        orderList.add(order);

        Order newOrder = new Order(UUID.randomUUID(), new Email("tester@gmail.com"), "Address Example", "Postcode Example", new ArrayList<>(), OrderStatus.ACCEPTED);
        newOrder.getOrderBooks().add(new OrderBook(newOrder.getOrderId(), book.getBookId(), 1, book.getPrice()));
        newOrder.getOrderBooks().add(new OrderBook(newOrder.getOrderId(), book.getBookId(), 10, book.getPrice()*10));
        orderList.add(newOrder);

        orderRepository.insert(newOrder);

        var findResult = orderRepository.findByEmail(order.getEmail());

        assertThat(findResult).usingRecursiveFieldByFieldElementComparator()
                .hasSameElementsAs(orderList);

    }

    @Test
    void deleteAll() {
        orderRepository.deleteAll();

        var findResult = orderRepository.findAll();

        assertThat(findResult.size()).isEqualTo(0);
    }

    @Test
    void deleteOrder() {
        var findResult = orderRepository.findAll();

        var deleteResult = orderRepository.deleteOrder(order.getOrderId());
        findResult.remove(findResult.stream().filter(a -> a.getOrderId().equals(order.getOrderId())).collect(Collectors.toList()).get(0));
        var deletedFindResult = orderRepository.findAll();

        assertThat(deleteResult).isTrue();
        assertThat(deletedFindResult).usingRecursiveFieldByFieldElementComparator()
                .hasSameElementsAs(findResult);
    }
}