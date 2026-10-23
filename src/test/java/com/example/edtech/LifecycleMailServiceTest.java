package com.example.edtech;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

public final class LifecycleMailServiceTest {
    public static void main(String[] args) {
        Clock clock = Clock.fixed(Instant.parse("2026-10-23T00:00:00Z"), ZoneOffset.UTC);
        LifecycleMailService service = new LifecycleMailService(null, clock, 3);
        CourseDelivery dueTomorrow = new CourseDelivery("learner@example.com", "Ari", "Fraud Basics",
                LocalDate.of(2026, 10, 24), false);
        if (service.decide(dueTomorrow) != LifecycleMailService.Decision.DEADLINE_NOTICE) throw new AssertionError("expected deadline notice");
        CourseDelivery complete = new CourseDelivery("learner@example.com", "Ari", "Fraud Basics",
                LocalDate.of(2026, 10, 24), true);
        if (service.decide(complete) != LifecycleMailService.Decision.NO_MESSAGE) throw new AssertionError("completed course must stay quiet");
        System.out.println("lifecycle decision test passed");
    }
}
