package com.example.demo.controller;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

import com.example.demo.model.Account;
import com.example.demo.model.AccountRepository;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api")
public class AccountController {
	
	@Autowired
	AccountRepository accountRepository;
	
	@GetMapping("/accounts/{accountId}")
	public ResponseEntity<Account> getAccountInfo(@PathVariable("accountId") long id) {
		Optional<Account> account = accountRepository.findById(id);
		if (account.isPresent()) {
			return new ResponseEntity<>(account.get(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	@PutMapping("/accounts/{accountId}")
	public ResponseEntity<Account> editAccountInfo(@PathVariable("accountId") long id, @RequestBody Account newAccountInfo) {
		if (newAccountInfo.getUsername() == null || newAccountInfo.getPassword() == null || newAccountInfo.getEmail() == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);	//no null input
		}

		newAccountInfo.setUsername(HtmlUtils.htmlEscape(newAccountInfo.getUsername().strip()));
		newAccountInfo.setPassword(HtmlUtils.htmlEscape(newAccountInfo.getPassword().strip()));	//trim trailing white spaces & prevent html tags
		newAccountInfo.setEmail(HtmlUtils.htmlEscape(newAccountInfo.getEmail().strip()));
		
		if (newAccountInfo.getUsername().equals("") || newAccountInfo.getPassword().equals("") || newAccountInfo.getEmail().equals("")) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);	//no empty input
		}
		
		String emailRegex = "^(?=.{1,}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{1,})$";
		Boolean validEmail = Pattern.compile(emailRegex).matcher(newAccountInfo.getEmail()).matches();	//validate email pattern
		if (!validEmail) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		Optional<Account> accountToEdit = accountRepository.findById(id);
		if (accountToEdit.isPresent()) {
			List<Account> accounts = accountRepository.findAll();
			
			Boolean duplicate = false;
			
			for (Account account : accounts) {
				if (id == account.getId()) {		
					continue;
				}										//check for duplicate email or username in the database
				if (account.getUsername().equals(newAccountInfo.getUsername()) || account.getEmail().equals(newAccountInfo.getEmail())) {	
					duplicate = true;
				}
			}
			
			if (duplicate) {
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			} else {
				accountToEdit.get().setUsername(newAccountInfo.getUsername());
				accountToEdit.get().setPassword(newAccountInfo.getPassword());
				accountToEdit.get().setEmail(newAccountInfo.getEmail());
				return new ResponseEntity<>(accountRepository.save(accountToEdit.get()), HttpStatus.OK);
			}
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
}