package com.example.demo.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Account;
import com.example.demo.model.AccountRepository;
import com.example.demo.model.Meeting;
import com.example.demo.model.MeetingRepository;
import request.CreateMeetingRequest;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api")
public class MeetingController {
	
	@Autowired
	MeetingRepository meetingRepository;
	
	@Autowired
	AccountRepository accountRepository;
	
	
	
	@PostMapping("/accounts/{userId}/meetings")
	public ResponseEntity<Meeting> createMeeting(@PathVariable Long userId, 
											@RequestBody CreateMeetingRequest request){
		
		try {
			Account host = accountRepository.findById(userId).orElse(null);
			
			if (host == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
			
			SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy hh:mm:ss a");
            
			Meeting _meeting = meetingRepository.save(
					new Meeting(request.getName(), formatter.parse(request.getStartTime()), 
							formatter.parse(request.getEndTime()), host));
			return new ResponseEntity<>(_meeting, HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

		
		@GetMapping("/accounts/{userId}/joinedMeetings")
		public ResponseEntity<List<Meeting>> getAllMeetings(@RequestParam(name = "id", required = false) Long id){
			
			try {
				List<Meeting> meetings = new ArrayList<>();
				
				if(id == null) {
					meetingRepository.findAll().forEach(meetings::add); 
				} else {
					Optional<Meeting> optionalMeeting = meetingRepository.findById(id);
					optionalMeeting.ifPresent(meetings::add);
				}
				
				if(meetings.isEmpty()) {
					return new ResponseEntity<>(HttpStatus.NO_CONTENT);
				}
				return new ResponseEntity<>(meetings, HttpStatus.OK);
			} catch (Exception e) {
				return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}		
	}
		
		@GetMapping("/accounts/{userId}/hostedMeetings")
		public ResponseEntity<List<Meeting>> getMeetingsHostedByUser(@PathVariable Long userId) {
			
			List<Meeting> allMeetings = meetingRepository.findAll();
			List<Meeting> meetingsHostedByUser = allMeetings.stream()
					.filter(meeting -> meeting.getHost().getId() == userId)
					.collect(Collectors.toList());
			
			if(meetingsHostedByUser.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}
			return new ResponseEntity<>(meetingsHostedByUser, HttpStatus.OK);
		}
}
