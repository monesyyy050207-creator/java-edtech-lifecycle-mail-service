package com.example.edtech;

import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;

public final class LifecycleMailService {
    public enum Decision { DEADLINE_NOTICE, NO_MESSAGE }
    private final InfraiClient client;
    private final Clock clock;
    private final int noticeDays;

    public LifecycleMailService(InfraiClient client, Clock clock, int noticeDays) {
        this.client = client; this.clock = clock; this.noticeDays = noticeDays;
    }

    public Decision decide(CourseDelivery delivery) {
        long days = ChronoUnit.DAYS.between(LocalDate.now(clock), delivery.deadline());
        return !delivery.completed() && days >= 0 && days <= noticeDays
                ? Decision.DEADLINE_NOTICE : Decision.NO_MESSAGE;
    }

    public String sendDeadlineNotice(CourseDelivery delivery) throws InfraiException {
        if (decide(delivery) != Decision.DEADLINE_NOTICE) return null;
        Map<String, String> template_vars = Map.of("learner_name", delivery.learnerName(),
                "course_name", delivery.courseName(), "deadline", delivery.deadline().toString());
        String html = TemplateCatalog.deadlineHtml(template_vars);
        return client.sendEmail(delivery.learnerEmail(), "Course deadline: " + delivery.courseName(),
                html, TemplateCatalog.DEADLINE_NAME + ":" + delivery.learnerEmail() + ":" + delivery.deadline());
    }
}
