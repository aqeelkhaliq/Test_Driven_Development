
package org.example.tdd.gpasystem;

import java.util.Objects;

public final class PrerequisiteChecker {

    public boolean canRegister(IStudent student, Course course) {
        Objects.requireNonNull(student, "student must not be null");
        Objects.requireNonNull(course, "course must not be null");

        for (Course prerequisite : course.getPrerequisites()) {
            if (!hasPassedPrerequisite(student, prerequisite)) {
                return false;
            }
        }

        return true;
    }

    private boolean hasPassedPrerequisite(
            IStudent student, Course prerequisite) {

        for (SemesterRecord semester : student.getSemesters().values()) {

            // An unfinished semester cannot satisfy a prerequisite.
            if (!semester.isCompleted()) {
                continue;
            }

            for (CourseResult result : semester.getCourseResults()) {

                boolean sameCourse = result.getCourse()
                        .getCode()
                        .equals(prerequisite.getCode());

                if (sameCourse && result.isPassed()) {
                    return true;
                }
            }
        }

        return false;
    }
}
