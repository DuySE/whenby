package com.example.demo.service;

import java.security.SecureRandom;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class Encoder {
	private static final int STRENGTH = 10; // Strength of the encryption (default is 10)
	private static final String SALT = "whenby"; // Salt

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(STRENGTH, new SecureRandom(SALT.getBytes()));
	}
}
