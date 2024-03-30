package com.example.demo.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

import com.example.demo.model.Account;
import com.example.demo.model.AccountRepository;
import com.example.demo.request.LoginRequest;
import com.example.demo.response.MessageResponse;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api")
public class LoginController {

	@Autowired
	AccountRepository accountRepository;

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {

		try {
			if (loginRequest.getUsername() == null || loginRequest.getPassword() == null) {
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);	//no null input
			}

			loginRequest.setUsername(HtmlUtils.htmlEscape(loginRequest.getUsername().strip()));
			loginRequest.setPassword(HtmlUtils.htmlEscape(loginRequest.getPassword().strip()));	//trim trailing white spaces & prevent html tags
			
			if (loginRequest.getUsername().equals("") || loginRequest.getPassword().equals("")) {
				MessageResponse msg = new MessageResponse("Please enter all the fields");
				return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);	//no empty input
			}
			
			Optional<Account> account = accountRepository.findByUsername(loginRequest.getUsername());
			if (account.isPresent()) {
				String password = account.get().getPassword();
				if (password.equals(loginRequest.getPassword())) {
					return new ResponseEntity<>(account.get(), HttpStatus.OK);
				}
				MessageResponse msg = new MessageResponse("Incorrect username or password");
				return new ResponseEntity<>(msg, HttpStatus.FORBIDDEN);
			}
			MessageResponse msg = new MessageResponse("This username does not exist");
			return new ResponseEntity<>(msg, HttpStatus.FORBIDDEN);
		} catch (Exception e) {
			MessageResponse msg = new MessageResponse("Server Error");
			return new ResponseEntity<>(msg, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

}
