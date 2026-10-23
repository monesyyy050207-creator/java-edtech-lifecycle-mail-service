package com.example.edtech;

import java.util.Map;

public final class TemplateCatalog {
    public static final String DEADLINE_NAME = "edtech-lifecycle/deadline-v1";

    private TemplateCatalog() { }

    public static String deadlineHtml(Map<String, String> template_vars) {
        return "<h1>Course deadline</h1><p>" + template_vars.get("learner_name")
                + ", your course <strong>" + template_vars.get("course_name")
                + "</strong> is due on " + template_vars.get("deadline") + ".</p>";
    }
}
