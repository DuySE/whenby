package com.example.demo.model;

public class Email {
	private String from, subject, body;
	private String[] tos;

	public Email(String _from, String[] _tos, String _subject, String _body) {
		super();
		this.from = _from;
		this.tos = _tos;
		this.subject = _subject;
		this.body = _body;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String _from) {
		this.from = _from;
	}

	public String[] getTos() {
		return tos;
	}

	public void setTo(String[] _tos) {
		this.tos = _tos;
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
