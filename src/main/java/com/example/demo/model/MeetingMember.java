package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class MeetingMember {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "member", nullable = false)
	@JsonIgnore
	@JsonProperty("member")
	private Account member;
	
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "meeting", nullable = false)
	@JsonIgnore
	@JsonProperty("meeting")
	private Meeting meeting;

	public MeetingMember() {
		
	}
	
	public MeetingMember(Account _member, Meeting _meeting) {
		this.member = _member;
		this.meeting = _meeting;
		this.member.getJoinedMeetings().add(this);
		this.meeting.getMembers().add(this);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Account getMember() {
		return member;
	}

	public void setMember(Account member) {
		this.member = member;
	}

	public Meeting getMeeting() {
		return meeting;
	}

	public void setMeeting(Meeting meeting) {
		this.meeting = meeting;
	}
	
	
	
}