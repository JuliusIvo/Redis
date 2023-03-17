package com.example.demo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;
import java.util.UUID;

enum Currency{
    EUR, USD
}

enum Type{
    PRIVATE, JURIDICAL
}
@Data
@RedisHash("Account")
public class Account implements Serializable {
    @Id
    @Indexed
    private String number;

    private String name;

    private int balance;

    private Currency currency;

    private Type type;

    public Account(String number, String name, int balance, Currency currency, Type type){
        super();
        this.number = number;
        this.name = name;
        this.currency = currency;
        this.balance = balance;
        this.type = type;
    }
}
