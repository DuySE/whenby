package com.example.demo;

import java.util.Date;
import java.util.Locale;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

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
		
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a", Locale.ENGLISH);
		ArrayList<String> strdates = new ArrayList<>(Arrays.asList("16-03-2024 3:30:00 PM","16-03-2024 5:30:00 PM",
															"29-02-2024 12:00:00 PM","29-02-2024 4:00:00 PM",
															"02-03-2024 4:30:00 PM","02-03-2024 5:30:00 PM",
															"27-02-2024 9:30:00 AM","27-02-2024 11:30:00 PM"));
		ArrayList<Date> dates = new ArrayList<>();
		for (String strdate : strdates) {
			Date date = new Date();
			try {
				date = formatter.parse(strdate);
				dates.add(date);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
		ArrayList<Meeting> meetings = new ArrayList<>();
		meetings.add(new Meeting("Discussing the Project", dates.get(0), dates.get(1), accounts.get(0)));
		meetings.add(new Meeting("Prepare for the Festival", dates.get(2), dates.get(3), accounts.get(0)));		//add meetings
		meetings.add(new Meeting("Practice Mock Exam", dates.get(4), dates.get(5), accounts.get(1)));
		meetings.add(new Meeting("Workshop: Communicate", dates.get(6), dates.get(7), accounts.get(2)));
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
