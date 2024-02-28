package com.example.demo.model;

public class Email {
	private String from, to, subject, body;

	public Email(String _from, String _to, String _subject, String _body) {
		super();
		this.from = _from;
		this.to = _to;
		this.subject = _subject;
		this.body = _body;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String _from) {
		this.from = _from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String _to) {
		this.to = _to;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String _subject) {
		this.subject = _subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String _body) {
		this.body = _body;
	}

}
