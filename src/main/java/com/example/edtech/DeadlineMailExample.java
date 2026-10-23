package com.example.edtech;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneOffset;

public final class DeadlineMailExample {
    public static void main(String[] args) throws Exception {
        String key = System.getenv("INFRAI_API_KEY");
        if (key == null || key.isBlank() || args.length != 1) throw new IllegalArgumentException("set INFRAI_API_KEY and pass a recipient");
        CourseDelivery delivery = new CourseDelivery(args[0], "Learner", "Risk Controls 101",
                LocalDate.now(ZoneOffset.UTC).plusDays(1), false);
        LifecycleMailService service = new LifecycleMailService(new InfraiClient(key), Clock.systemUTC(), 3);
        System.out.println("decision=" + service.decide(delivery));
        System.out.println("message_id=" + service.sendDeadlineNotice(delivery));
    }
}
