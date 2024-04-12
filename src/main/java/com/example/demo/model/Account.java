package com.example.demo.model;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Account {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	@Column(name = "username")
	private String username;
	
	@Column(name = "email")
	private String email;
	
	@Column(name = "password")
	private String password;
	
	@OneToMany(mappedBy = "host", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JsonIgnore
	private Set<Meeting> hostedMeetings = new HashSet<>();
						
	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JsonIgnore
	private Set<MeetingMember> joinedMeetings = new HashSet<>();
	
	public Account() {
		
	}
	
	public Account(String _username, String _email, String _password) {
		this.username = _username;
		this.email = _email;
		this.password = _password;
	}

	public void addMeeting(Meeting meeting) {
		meeting.setHost(this);
		this.hostedMeetings.add(meeting);
		
		new MeetingMember(this, meeting);	//create new MeetingMember record because the host is also a member of the meeting
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Set<Meeting> getHostedMeetings() {
		return hostedMeetings;
	}

	public void setHostedMeetings(Set<Meeting> hostedMeetings) {
		this.hostedMeetings = hostedMeetings;
	}

	public Set<MeetingMember> getJoinedMeetings() {
		return joinedMeetings;
	}

	public void setJoinedMeetings(Set<MeetingMember> joinedMeetings) {
		this.joinedMeetings = joinedMeetings;
	}

	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}