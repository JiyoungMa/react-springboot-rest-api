package com.kdt.reactspringbootrestapi.order;

import org.springframework.util.Assert;

import java.util.Objects;
import java.util.regex.Pattern;

public class Email {
    private final String email;

    public Email(String email) {
        Assert.notNull(email, "email should not be null");
        Assert.isTrue(email.length()>=4 && email.length()<=50, "address length must be between 4 and 50 characters");
        Assert.isTrue(checkAddress(email), "Invalid email address");
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    private static boolean checkAddress(String address) {
        return Pattern.matches("\\b[\\w\\.-]+@[\\w\\.-]+\\.\\w{2,4}\\b", address);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Email email1 = (Email) o;
        return email.equals(email1.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    @Override
    public String toString() {
        return "Email{" +
                "email='" + email + '\'' +
                '}';
    }
}
