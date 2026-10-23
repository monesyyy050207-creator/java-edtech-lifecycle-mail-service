package com.example.edtech;

import java.time.LocalDate;

public record CourseDelivery(String learnerEmail, String learnerName, String courseName,
                             LocalDate deadline, boolean completed) { }
