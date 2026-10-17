package org.example.tdd.gpasystem;

import java.util.Objects;

public final class GradeBand {
    private final double minimumPercentage;
    private final Grade grade;

    public GradeBand(double minimumPercentage, Grade grade) {
        if (!Double.isFinite(minimumPercentage) || minimumPercentage < 0.0 || minimumPercentage > 100.0) {
            throw new IllegalArgumentException("Minimum percentage must be between 0 and 100");
        }
        this.minimumPercentage = minimumPercentage;
        this.grade = Objects.requireNonNull(grade, "grade must not be null");
    }

    public double getMinimumPercentage() {
        return minimumPercentage;
    }

    public Grade getGrade() {
        return grade;
    }
}
