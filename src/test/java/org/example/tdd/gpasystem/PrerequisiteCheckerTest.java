
package org.example.tdd.gpasystem;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PrerequisiteCheckerTest {

    private final PrerequisiteChecker checker = new PrerequisiteChecker();
    private final GradingPolicyManager policy =
            GradingPolicyManager.standardFourPointScale();

    @Test
    void courseWithNoPrerequisitesIsAlwaysAllowed() {
        Student student = new Student("S1", "Aqeel khaliq");
        Course targetCourse = new Course("CS102", "Data Structures", 3);

        assertTrue(checker.canRegister(student, targetCourse));
    }

    @Test
    void returnsFalseWhenPrerequisiteIsMissing() {
        Student student = new Student("S1", "Hermain Shokat");
        Course prerequisite = new Course("CS101", "Programming", 3);
        Course targetCourse = new Course("CS102", "Data Structures", 3);
        targetCourse.addPrerequisite(prerequisite);

        assertFalse(checker.canRegister(student, targetCourse));
    }

    @Test
    void returnsTrueWhenPrerequisiteIsPassed() {
        Student student = new Student("S1", "Aqeel Khaliq");
        Course prerequisite = new Course("CS101", "Programming", 3);
        Course targetCourse = new Course("CS102", "Data Structures", 3);
        targetCourse.addPrerequisite(prerequisite);

        SemesterRecord semester = new SemesterRecord(1);
      semester.addCourseResult(
        policy.createCourseResult(prerequisite, 85));

        addRemainingCourses(semester);
        semester.complete();
        student.addSemester(semester);

        assertTrue(checker.canRegister(student, targetCourse));
    }

    @Test
    void returnsFalseWhenPrerequisiteIsFailed() {
        Student student = new Student("S1", "Aqeel Khaliq");
        Course prerequisite = new Course("CS101", "Programming", 3);
        Course targetCourse = new Course("CS102", "Data Structures", 3);
        targetCourse.addPrerequisite(prerequisite);

        SemesterRecord semester = new SemesterRecord(1);
        semester.addCourseResult(
        policy.createCourseResult(prerequisite, 50));

        addRemainingCourses(semester);
        semester.complete();
        student.addSemester(semester);

        assertFalse(checker.canRegister(student, targetCourse));
    }

    @Test
    void returnsFalseWhenPrerequisiteIsInProgress() {
        Student student = new Student("S1", "Aqeel khaliq");
        Course prerequisite = new Course("CS101", "Programming", 3);
        Course targetCourse = new Course("CS102", "Data Structures", 3);
        targetCourse.addPrerequisite(prerequisite);

        SemesterRecord semester = new SemesterRecord(1);
        semester.addCourseResult(
                policy.createCourseResult(prerequisite, 85));
        student.addSemester(semester);

        assertFalse(checker.canRegister(student, targetCourse));
    }

    @Test
    void returnsFalseWhenOneOfMultiplePrerequisitesIsMissing() {
        Student student = new Student("S1", "Aqeel khaliq");

        Course firstPrerequisite =
                new Course("CS101", "Programming", 3);
        Course secondPrerequisite =
                new Course("CS102", "Data Structures", 3);
        Course targetCourse =
                new Course("CS201", "Algorithms", 3);

        targetCourse.addPrerequisite(firstPrerequisite);
        targetCourse.addPrerequisite(secondPrerequisite);

        SemesterRecord semester = new SemesterRecord(1);
        semester.addCourseResult(
        policy.createCourseResult(firstPrerequisite, 85));

        addRemainingCourses(semester);
        semester.complete();
        student.addSemester(semester);

        assertFalse(checker.canRegister(student, targetCourse));
    }

    @Test
    void returnsTrueWhenAllPrerequisitesArePassed() {
        Student student = new Student("S1", "Uzair Ali");

        Course firstPrerequisite =
                new Course("CS101", "Programming", 3);
        Course secondPrerequisite =
                new Course("CS102", "Data Structures", 3);
        Course targetCourse =
                new Course("CS201", "Algorithms", 3);

        targetCourse.addPrerequisite(firstPrerequisite);
        targetCourse.addPrerequisite(secondPrerequisite);

        SemesterRecord semester = new SemesterRecord(1);
        semester.addCourseResult(
                policy.createCourseResult(firstPrerequisite, 85));
        semester.addCourseResult(
                policy.createCourseResult(secondPrerequisite, 90));

        // A completed semester must contain 4 or 5 courses.
        semester.addCourseResult(policy.createCourseResult(
                new Course("EL101", "Elective One", 3), 75));
        semester.addCourseResult(policy.createCourseResult(
                new Course("EL102", "Elective Two", 3), 80));

        semester.complete();
        student.addSemester(semester);

        assertTrue(checker.canRegister(student, targetCourse));
    }
    
    private void addRemainingCourses(SemesterRecord semester) {
    for (int i = 1; semester.getCourseResults().size() < 4; i++) {
        semester.addCourseResult(
                policy.createCourseResult(
                        new Course("EXTRA" + i, "Extra Course " + i, 3),
                        75
                )
        );
    }
}

}
