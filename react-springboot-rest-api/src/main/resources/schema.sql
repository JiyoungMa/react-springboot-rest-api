drop database dev_firstproject;
create database dev_firstproject;
use dev_firstproject;

CREATE TABLE authors(
                        author_id BINARY(16) PRIMARY KEY,
                        author_name VARCHAR(50) NOT NULL,
                        created_at datetime(6) NOT NULL,
                        updated_at datetime(6) DEFAULT NULL
);

CREATE TABLE books(
                      book_id BINARY(16) PRIMARY KEY,
                      title VARCHAR(200) NOT NULL,
                      author_id BINARY(16) NOT NULL,
                      genre VARCHAR(50) NOT NULL,
                      price bigint NOT NULL,
                      created_at datetime(6) NOT NULL,
                      updated_at datetime(6) DEFAULT NULL,
                      FOREIGN KEY (author_id) REFERENCES authors(author_id) ON DELETE CASCADE
);

CREATE TABLE orders
(
    order_id        binary(16)      PRIMARY Key,
    email           VARCHAR(50)     NOT NULL,
    address         VARCHAR(200)    NOT NULL,
    postcode        VARCHAR(200)    NOT NULL,
    order_status    VARCHAR(50)     NOT NULL,
    created_at      datetime(6)     NOT NULL,
    updated_at      datetime(6)     DEFAULT  NULL
);

CREATE TABLE order_books
(
    order_books_id         bigint          NOT NULL PRIMARY KEY AUTO_INCREMENT,
    order_id    binary(16)      NOT NULL,
    book_id  binary(16)      NOT NULL,
    price       bigint          NOT NULL,
    quantity    int             NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders (order_id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES books (book_id) ON DELETE CASCADE
);