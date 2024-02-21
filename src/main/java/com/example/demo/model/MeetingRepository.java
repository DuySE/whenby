package com.example.demo.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingRepository extends JpaRepository<Meeting, Long>{
	
}