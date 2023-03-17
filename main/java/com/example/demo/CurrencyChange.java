package com.example.demo;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CurrencyChange implements SessionCallback<List<Object>> {
    public static final String ACCOUNT = "Account";
    private final String accountId;
    private final Currency newCurrency;


    public CurrencyChange(String accountId, Currency currency) {
        this.accountId = accountId;
        this.newCurrency = currency;
    }

    @Override
    public <K, V> List<Object> execute(RedisOperations<K,V> redisOperations) throws DataAccessException {
        var operations = (RedisTemplate<String, Object>) redisOperations;
        var hashOperations = operations.opsForHash();
        var account = (Account) hashOperations.get(ACCOUNT, accountId);
        if(Objects.nonNull(account) && account.getCurrency()!=newCurrency){
            try{
                operations.multi();
                if(account.getCurrency() == Currency.USD && newCurrency == Currency.EUR){
                    account.setBalance((int) (account.getBalance()*0.96));
                }
                if(account.getCurrency() == Currency.EUR && newCurrency == Currency.USD){
                    account.setBalance((int) (account.getBalance()*1.04));
                }
                account.setCurrency(newCurrency);
                hashOperations.put(ACCOUNT,accountId,account );
                return operations.exec();
            }catch (Exception e){
                System.out.println("discarding");
                operations.discard();
            }
        }
        else {
            System.out.println("object null || transaction is redundant");
        }
        return Collections.emptyList();
    }
}
