package com.example.demo.controller;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.HtmlUtils;

import com.example.demo.model.Account;
import com.example.demo.model.AccountRepository;
import com.example.demo.model.Email;
import com.example.demo.model.Meeting;
import com.example.demo.model.MeetingMember;
import com.example.demo.model.MeetingMemberRepository;
import com.example.demo.model.MeetingRepository;
import com.example.demo.request.CreateMeetingRequest;
import com.example.demo.response.MessageResponse;
import com.example.demo.service.EmailService;

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

	@Autowired
	private PasswordEncoder passwordEncoder;

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

			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.ENGLISH);

			Meeting _meeting = meetingRepository.save(new Meeting(request.getName(),
					formatter.parse(request.getStartTime()), formatter.parse(request.getEndTime()), host));
			return new ResponseEntity<>(_meeting, HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/meetings/{meetingId}/send_email")
	public ResponseEntity<?> sendEmail(@PathVariable("meetingId") long meetingId, @RequestBody Email email) {
		try {
			String hash = passwordEncoder.encode(email.getTo());
			Optional<Account> account = accountRepository.findByEmail(email.getTo());
			if (account.isEmpty()) {
				MessageResponse msg = new MessageResponse(email.getTo() + " is not registered to Whenby");
				return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
			} else {
				String username = account.get().getUsername();
				long uid = account.get().getId();
				emailSender = new EmailService();
				String body = String.format("Hello %s,<br><br>" + "This email is confirmation of your meeting %s.<br><br>"
						+ "To confirm the meeting, please click on the following link: <a href='http://localhost:8080/api/meetings/%s?uid=%d&hash=%s'>Click here!</a><br><br>"
						+ "Best regards,<br><br>" + "Whenby Team<br><br>"
						+ "<small><em>This is an automatically generated email - please do not reply to it.</em></small><br><br>",
						username, email.getName(), meetingId, uid, hash);
				email.setBody(body);
				emailSender.sendEmail(email);
				return new ResponseEntity<>(HttpStatus.CREATED);
			}
		} catch (MessagingException | UnsupportedEncodingException e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/meetings/{meetingId}")
	public RedirectView confirmMeeting(@PathVariable("meetingId") long meetingId, @RequestParam("uid") long uid,
			@RequestParam("hash") String hash) {
		Optional<Account> foundAccount = accountRepository.findById(uid);
		if (passwordEncoder.matches(foundAccount.get().getEmail(), hash)) {
			Optional<Meeting> meetingData = meetingRepository.findById(meetingId);
			Meeting _meeting = meetingData.get();
			Optional<Account> accountData = accountRepository.findById(uid);
			Account _account = accountData.get();
			MeetingMember meetingMember = new MeetingMember(_account, _meeting);
			meetingMemberRepository.save(meetingMember);
			return new RedirectView("http://localhost:8081/meetings?token=" + hash, true);
		} else
			return null;
	}

	@GetMapping("/accounts/{userId}/hostedMeetings")
	public ResponseEntity<List<Meeting>> getMeetingsHostedByUser(@PathVariable Long userId) {

		List<Meeting> allMeetings = meetingRepository.findAll();
		List<Meeting> meetingsHostedByUser = allMeetings.stream().filter(meeting -> meeting.getHost().getId() == userId)
				.collect(Collectors.toList());

		if (meetingsHostedByUser.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(meetingsHostedByUser, HttpStatus.OK);
	}

	@GetMapping("/accounts/{userId}/joinedMeetings")
	public ResponseEntity<List<Meeting>> getAllMeetings(@PathVariable Long userId) {

		try {
			Account user = accountRepository.findById(userId).orElse(null);

			if (user == null) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			} else {
				Set<MeetingMember> joinedMeetings = user.getJoinedMeetings();
				List<Meeting> meetings = joinedMeetings.stream().map(MeetingMember::getMeeting)
						.collect(Collectors.toList());
				if (meetings.isEmpty()) {
					return new ResponseEntity<>(HttpStatus.NO_CONTENT);
				}
				return new ResponseEntity<>(meetings, HttpStatus.OK);
			}
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/meetings/{meetingId}")
	public ResponseEntity<?> updateMeeting(@PathVariable Long meetingId, @RequestBody CreateMeetingRequest request) {
		try {
			if (request.getName() == null || request.getStartTime() == null || request.getEndTime() == null) {
				MessageResponse msg = new MessageResponse("Please enter all the fields");
				return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
			}
			request.setName(HtmlUtils.htmlEscape(request.getName().strip())); // validate the RequestBody
			if (request.getName().equals("") || request.getStartTime().equals("") || request.getEndTime().equals("")) {
				MessageResponse msg = new MessageResponse("Please enter all the fields");
				return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
			}
			Optional<Meeting> meetingToEdit = meetingRepository.findById(meetingId);
			if (meetingToEdit.isPresent()) {
				meetingToEdit.get().setName(request.getName());
				SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a", Locale.ENGLISH);
				meetingToEdit.get().setStartTime(formatter.parse(request.getStartTime()));
				meetingToEdit.get().setEndTime(formatter.parse(request.getEndTime()));

				return new ResponseEntity<>(meetingRepository.save(meetingToEdit.get()), HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}

		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/accounts/{userId}/meetings/{meetingId}")
	public ResponseEntity<Meeting> getMeetingInfo(@PathVariable Long meetingId, @PathVariable Long userId) {
		Optional<Meeting> meeting = meetingRepository.findById(meetingId);
		if (meeting.isPresent()) {
			return new ResponseEntity<>(meeting.get(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

}