package com.agri.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.agri.model.State;
import com.agri.repository.StateRepository;

import jakarta.persistence.*;

import java.util.List;

@Service
public class StateService {

    @Autowired
    private StateRepository stateRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public List<State> getAllStates() {
        return stateRepository.findAll();
    }

    public State getStateById(Long id) {
        return stateRepository.findById(id).orElse(null);
    }

    @Transactional
    public State saveState(State state) {
        return stateRepository.save(state);
    }

    @Transactional
    public void deleteState(Long id) {
        stateRepository.deleteById(id);
        resetAutoIncrement();
    }

    private void resetAutoIncrement() {
        entityManager.createNativeQuery("ALTER TABLE state AUTO_INCREMENT = 1").executeUpdate();
    }
    
        }
