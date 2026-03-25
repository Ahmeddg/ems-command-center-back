package com.example.ems_command_center.service;

import com.example.ems_command_center.model.Personnel;
import com.example.ems_command_center.repository.PersonnelRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonnelService {

    private final PersonnelRepository personnelRepository;

    public PersonnelService(PersonnelRepository personnelRepository) {
        this.personnelRepository = personnelRepository;
    }

    public List<Personnel> getAllPersonnel() {
        return personnelRepository.findAll();
    }

    public Personnel createPersonnel(Personnel personnel) {
        return personnelRepository.save(personnel);
    }
}
