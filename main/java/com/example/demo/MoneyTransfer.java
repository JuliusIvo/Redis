package com.example.demo;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MoneyTransfer implements SessionCallback<List<Object>> {
    public static final String ACCOUNT = "Account";
    private final String fromAccountId;
    private final String toAccountId;
    private final int amount;

    public MoneyTransfer(String fromAccountId, String toAccountId, int amount) {
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
    }

    @Override
    public <K, V> List<Object> execute(RedisOperations<K,V> redisOperations) throws DataAccessException{
        var operations = (RedisTemplate<String, Object>) redisOperations;
        var hashOperations = operations.opsForHash();
        var fromAccount = (Account) hashOperations.get(ACCOUNT, fromAccountId);
        var toAccount = (Account) hashOperations.get(ACCOUNT, toAccountId);
        operations.watch(fromAccountId);
        if(Objects.nonNull(fromAccount) && Objects.nonNull(toAccount) && fromAccount.getBalance() >= amount && fromAccount.getCurrency() == toAccount.getCurrency()){
            try{
                operations.multi();
                fromAccount.setBalance((fromAccount.getBalance()) - amount);
                toAccount.setBalance(toAccount.getBalance() + amount);
                hashOperations.put(ACCOUNT, fromAccountId, fromAccount);
                hashOperations.put(ACCOUNT,toAccountId,toAccount );
                return operations.exec();
            }catch (Exception e){
                System.out.println("discarding");
                operations.discard();
            }
        }
        else {
            System.out.println("objects null / not enough balance / currency does not match");
        }
        operations.unwatch();
        return Collections.emptyList();
    }
}
