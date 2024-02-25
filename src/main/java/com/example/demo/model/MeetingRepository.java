package com.example.demo.model;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingRepository extends JpaRepository<Meeting, Long>{
	
	List<Meeting> findByName(String name);
	List<Meeting> findById(long id);
	List<Meeting> findByHostId(Long hostId);
}