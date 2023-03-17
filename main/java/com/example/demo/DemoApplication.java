package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner{

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
	@Autowired private RedisTemplate<String, Object> redisTemplate;

	@Override
	public void run(String... args) throws Exception{

			redisTemplate.opsForHash().put(MoneyTransfer.ACCOUNT, "LT1234",new Account("LT1234", "John Travolta",1000 , Currency.EUR, Type.PRIVATE));
			redisTemplate.opsForHash().put(MoneyTransfer.ACCOUNT, "LT1245",new Account("LT1245", "John Travolta",100 , Currency.USD, Type.PRIVATE));
			redisTemplate.execute( new CurrencyChange("LT1234", Currency.USD));
			redisTemplate.execute(new MoneyTransfer("LT1234","LT1245", 10));
			System.out.println(redisTemplate.opsForHash().get(MoneyTransfer.ACCOUNT, "LT1234"));
			System.out.println(redisTemplate.opsForHash().get(MoneyTransfer.ACCOUNT, "LT1245"));
	}
}
