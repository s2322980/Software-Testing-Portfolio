package uk.ac.ed.inf.ilp_coursework.service;

import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class SystemClock implements Clock {
    @Override
    public LocalDate today() {
        return LocalDate.now();
    }
}
