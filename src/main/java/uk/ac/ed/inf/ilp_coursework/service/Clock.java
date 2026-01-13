package uk.ac.ed.inf.ilp_coursework.service;

import org.springframework.stereotype.Component;

import java.time.LocalDate;

public interface Clock {
    LocalDate today();
}

