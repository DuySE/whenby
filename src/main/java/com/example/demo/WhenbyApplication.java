package com.example.demo;

import java.util.Date;
import java.util.Locale;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.example.demo.model.Account;
import com.example.demo.model.AccountRepository;
import com.example.demo.model.Meeting;
import com.example.demo.model.MeetingMember;
import com.example.demo.model.MeetingMemberRepository;
import com.example.demo.model.MeetingRepository;

@SpringBootApplication
public class WhenbyApplication {

	public static void main(String[] args) {
		SpringApplication.run(WhenbyApplication.class, args);
	}
	
    private void loadData(AccountRepository accountRepository, MeetingRepository meetingRepository, MeetingMemberRepository meetingMemberRepository) { //load data into database when launching server
		ArrayList<Account> accounts = new ArrayList<>();	
		accounts.add(new Account("minh", "minh@gmail.com", "abcdef"));
		accounts.add(new Account("duy", "duy@outlook.com", "123456"));	//add accounts
		accounts.add(new Account("simone", "simone@yahoo.com", "567890"));
		accountRepository.saveAll(accounts);
		
		SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy hh:mm:ss a", Locale.ENGLISH);
		String strdate = "24-02-2024 4:30:00 PM";
		Date date = new Date();
		try {
			date = formatter.parse(strdate);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		ArrayList<Meeting> meetings = new ArrayList<>();
		meetings.add(new Meeting("Discussing the Project", date, date, accounts.get(0)));
		meetings.add(new Meeting("Prepare for the Festival", date, date, accounts.get(0)));		//add meetings
		meetings.add(new Meeting("Practice Mock Exam", date, date, accounts.get(1)));
		meetings.add(new Meeting("Workshop: Communicate", date, date, accounts.get(2)));
		meetingRepository.saveAll(meetings);
		
		Account mem = accounts.get(2);
		Meeting meet = meetings.get(0);
		meetingMemberRepository.save(new MeetingMember(mem, meet));	//add meetingMember
	}
	
	@Bean
	ApplicationRunner init(AccountRepository accountRepository, MeetingRepository meetingRepository, MeetingMemberRepository meetingMemberRepository) {
		return args -> {
			loadData(accountRepository, meetingRepository, meetingMemberRepository);
		};
	}

}
