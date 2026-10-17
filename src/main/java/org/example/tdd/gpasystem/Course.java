package org.example.tdd.gpasystem;

import java.util.Locale;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class Course {
    private final String code;
    private final String title;
    private final double credits;

    public Course(String code, String title, double credits) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Course code must not be blank");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Course title must not be blank");
        }
        if (!Double.isFinite(credits) || credits <= 0.0) {
            throw new IllegalArgumentException("Course credits must be greater than zero");
        }
        this.code = code.trim().toUpperCase(Locale.ROOT);
        this.title = title.trim();
        this.credits = credits;
    }

    public String getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    public double getCredits() {
        return credits;
    }
    private final List<Course> prerequisites = new ArrayList<>();

public void addPrerequisite(Course prerequisite) {
    Objects.requireNonNull(prerequisite, "prerequisite must not be null");

    if (prerequisite.getCode().equals(this.code)) {
        throw new IllegalArgumentException("A course cannot be its own prerequisite");
    }

    if (prerequisites.stream().anyMatch(
            existing -> existing.getCode().equals(prerequisite.getCode()))) {
        throw new IllegalArgumentException("Prerequisite already exists");
    }

    prerequisites.add(prerequisite);
}

public List<Course> getPrerequisites() {
    return Collections.unmodifiableList(prerequisites);
}
}
