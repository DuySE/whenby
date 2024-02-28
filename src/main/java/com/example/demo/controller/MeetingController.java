package com.example.demo.controller;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Optional;

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
import org.springframework.web.util.HtmlUtils;

import com.example.demo.model.Account;
import com.example.demo.model.AccountRepository;
import com.example.demo.model.Email;
import com.example.demo.model.Meeting;
import com.example.demo.model.MeetingMember;
import com.example.demo.model.MeetingMemberRepository;
import com.example.demo.model.MeetingRepository;
import com.example.demo.request.CreateMeetingRequest;
import com.example.service.EmailService;

import jakarta.mail.MessagingException;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api")
public class MeetingController {

	@Autowired
	MeetingRepository meetingRepository;

	@Autowired
	AccountRepository accountRepository;

	@Autowired
	MeetingMemberRepository meetingMemberRepository;

	EmailService emailSender;

	@DeleteMapping("/meetings/{meetingId}")
	public ResponseEntity<HttpStatus> deleteMeeting(@PathVariable("meetingId") long id) {
		try {
			meetingRepository.deleteById(id);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/accounts/{userId}/meetings")
	public ResponseEntity<Meeting> createMeeting(@PathVariable Long userId, @RequestBody CreateMeetingRequest request) {

		try {
			if (request.getName() == null || request.getStartTime() == null || request.getEndTime() == null) {
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
			request.setName(HtmlUtils.htmlEscape(request.getName().strip())); // validate the RequestBody
			if (request.getName().equals("") || request.getStartTime().equals("") || request.getEndTime().equals("")) {
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}

			Account host = accountRepository.findById(userId).orElse(null);

			if (host == null) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}

			SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy hh:mm:ss a");

			Meeting _meeting = meetingRepository.save(new Meeting(request.getName(),
					formatter.parse(request.getStartTime()), formatter.parse(request.getEndTime()), host));
			return new ResponseEntity<>(_meeting, HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/meetings/{meetingId}/send_email")
	public ResponseEntity<Meeting> sendEmail(@PathVariable("meetingId") long meetingId, @RequestBody Email email) {
		try {
			emailSender = new EmailService();
			emailSender.sendEmail(email);
			return new ResponseEntity<>(HttpStatus.CREATED);
		} catch (MessagingException | UnsupportedEncodingException e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/meetings/{meetingId}")
	public ResponseEntity<Meeting> confirmMeeting(@PathVariable("meetingId") long meetingId,
			@RequestParam("member") long member) {
		try {
			Optional<Meeting> meetingData = meetingRepository.findById(meetingId);
			Meeting _meeting = meetingData.get();
			Optional<Account> accountData = accountRepository.findById(member);
			Account _account = accountData.get();
			MeetingMember meetingMember = new MeetingMember(_account, _meeting);
			meetingMemberRepository.save(meetingMember);
			return new ResponseEntity<>(HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}