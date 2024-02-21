package com.example.demo.model;

import java.util.Date;
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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Meeting {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	@Column(name = "name") 
	private String name;
	
	@Column(name = "startTime")
	private Date startTime;
	
	@Column(name = "endTime")
	private Date endTime;
	
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "host", nullable = false)
	private Account host;
	
	@OneToMany(mappedBy = "meeting", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JsonIgnore
	private Set<MeetingMember> members = new HashSet<>();
	
	public Meeting() {
		
	}
	
	public Meeting(String _name, Date _startTime, Date _endTime, Account _host) {
		this.name = _name;
		this.startTime = _startTime;
		this.endTime = _endTime;
		this.host = _host;
		this.host.getHostedMeetings().add(this);
		
		new MeetingMember(host, this);	//host is also a member so add a new record to MeetingMember
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Account getHost() {
		return host;
	}

	public void setHost(Account host) {
		this.host = host;
	}

	public Set<MeetingMember> getMembers() {
		return members;
	}

	public void setMembers(Set<MeetingMember> members) {
		this.members = members;
	}

	
	
	
	
	
	
}