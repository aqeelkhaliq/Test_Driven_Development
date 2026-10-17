package org.example.tdd.gpasystem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class GradingPolicyManager {
    private final List<GradeBand> gradeBands;

    public GradingPolicyManager(List<GradeBand> gradeBands) {
        Objects.requireNonNull(gradeBands, "gradeBands must not be null");
        if (gradeBands.isEmpty()) {
            throw new IllegalArgumentException("At least one grade band is required");
        }
        List<GradeBand> copy = new ArrayList<>(gradeBands);
        Set<Double> thresholds = new HashSet<>();
        for (GradeBand band : copy) {
            Objects.requireNonNull(band, "grade bands must not contain null");
            if (!thresholds.add(band.getMinimumPercentage())) {
                throw new IllegalArgumentException("Grade band thresholds must be unique");
            }
        }
        copy.sort(Comparator.comparingDouble(GradeBand::getMinimumPercentage).reversed());
        if (copy.get(copy.size() - 1).getMinimumPercentage() != 0.0) {
            throw new IllegalArgumentException("A grade band starting at 0 is required");
        }
        this.gradeBands = java.util.Collections.unmodifiableList(copy);
    }

    public static GradingPolicyManager standardFourPointScale() {
        return new GradingPolicyManager(Arrays.asList(
                new GradeBand(90.0, new Grade("A", 4.0)),
                new GradeBand(80.0, new Grade("B", 3.0)),
                new GradeBand(70.0, new Grade("C", 2.0)),
                new GradeBand(60.0, new Grade("D", 1.0)),
                new GradeBand(0.0, new Grade("F", 0.0))));
    }

    public Grade getGradeForPercentage(double percentage) {
        if (!Double.isFinite(percentage) || percentage < 0.0 || percentage > 100.0) {
            throw new IllegalArgumentException("Percentage must be between 0 and 100");
        }
        for (GradeBand band : gradeBands) {
            if (percentage >= band.getMinimumPercentage()) {
                return band.getGrade();
            }
        }
        throw new IllegalStateException("Grading policy does not cover percentage " + percentage);
    }

    public CourseResult createCourseResult(Course course, double percentage) {
        return new CourseResult(course, percentage, getGradeForPercentage(percentage));
    }
}
