package com.example.ems_command_center.repository;

import com.example.ems_command_center.model.Personnel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonnelRepository extends MongoRepository<Personnel, String> {
}
