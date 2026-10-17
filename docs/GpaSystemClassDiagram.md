# GPA System UML Class Diagram

The diagram shows production types in `org.example.tdd.gpasystem` and test support types in the test source set. Solid diamonds indicate composition, open diamonds aggregation, solid arrows association, dashed arrows dependency, and hollow triangles interface inheritance or realization.

```mermaid
classDiagram
    direction TB

    class IPerson {
        <<interface>>
        +getId() String
        +getName() String
    }

    class IStudent {
        <<interface>>
        +getSemester(number) SemesterRecord
        +getSemesters() Map
    }

    class Student {
        -id String
        -name String
        -semesters Map
        +addSemester(semester) void
        +getSemester(number) SemesterRecord
        +getSemesters() Map
    }

    class SemesterRecord {
        -semesterNumber int
        -courseResults List
        -completed boolean
        +addCourseResult(result) void
        +complete() void
        +isCompleted() boolean
        +getCourseResults() List
    }

    class Course {
        -code String
        -title String
        -credits double
        -prerequisites List
        +addPrerequisite(prerequisite) void
        +getPrerequisites() List
    }

    class CourseResult {
        -percentage double
        -course Course
        -grade Grade
        +getPercentage() double
        +getCourse() Course
        +getGrade() Grade
        +isPassed() boolean
    }

    class Grade {
        -letter String
        -points double
        +getLetter() String
        +getPoints() double
    }

    class GradeBand {
        -minimumPercentage double
        -grade Grade
        +getMinimumPercentage() double
        +getGrade() Grade
    }

    class GradingPolicyManager {
        -gradeBands List
        +standardFourPointScale() GradingPolicyManager
        +getGradeForPercentage(percentage) Grade
        +createCourseResult(course, percentage) CourseResult
    }

    class GpaCalculator {
        +calculateSemesterGpa(student, semesterNumber) double
        +calculateCumulativeGpa(student) double
        +semesterPerformance(students, semesterNumber) StudentPerformanceSummary
        +coursePerformance(students, courseCode) StudentPerformanceSummary
    }

    class StudentGpa {
        -student IStudent
        -gpa double
        +getStudent() IStudent
        +getGpa() double
    }

    class StudentPerformanceSummary {
        -performances List
        -averageGpa double
        -best StudentGpa
        -worst StudentGpa
        -averagePerformer StudentGpa
        +getPerformances() List
        +getAverageGpa() double
        +getBest() StudentGpa
        +getWorst() StudentGpa
        +getAveragePerformer() StudentGpa
    }

    class PrerequisiteChecker {
        +canRegister(student, course) boolean
        -hasPassedPrerequisite(student, prerequisite) boolean
    }

    IPerson <|-- IStudent : extends
    IStudent <|.. Student : implements

    Student "1" o-- "0..8" SemesterRecord : aggregates
    SemesterRecord "1" *-- "0..5" CourseResult : owns
    CourseResult "1" --> "1" Course : result for
    CourseResult "1" --> "1" Grade : awarded

    Course "1" --> "0..*" Course : prerequisite courses

    GradingPolicyManager "1" o-- "1..*" GradeBand : applies
    GradeBand "1" --> "1" Grade : maps to

    GpaCalculator ..> IStudent : accepts injected student(s)
    GpaCalculator ..> CourseResult : calculates from
    GpaCalculator ..> StudentGpa : creates rankings
    GpaCalculator ..> StudentPerformanceSummary : returns

    StudentGpa "0..*" --> "1" IStudent : describes
    StudentPerformanceSummary "1" *-- "1..*" StudentGpa : contains

    PrerequisiteChecker ..> IStudent : checks student history
    PrerequisiteChecker ..> Course : checks prerequisites
    PrerequisiteChecker ..> SemesterRecord : checks completion
    PrerequisiteChecker ..> CourseResult : checks passing result

    namespace test {
        class FakeStudent {
            -id String
            -name String
            -semesters Map
            +addSemester(semester) void
            +getId() String
            +getName() String
            +getSemester(number) SemesterRecord
            +getSemesters() Map
        }

        class TestDataGenerator {
            -gradingPolicy GradingPolicyManager
            +createCompletedSemesterCohort() List
            +createStudent(id, name, percentage) Student
            +createStudentWithSemesters(id, name, count, percentage) Student
        }

        class GpaCalculatorTest {
            +calculatesCreditWeightedSemesterGpa() void
            +calculatesGpaForFakeStudentWithoutValidatingItsIdentity() void
            +calculatesCoursePerformanceFromFakeStudentsWithInvalidIds() void
        }

        class PrerequisiteCheckerTest {
            +allowsCourseWithoutPrerequisites() void
            +rejectsMissingPrerequisite() void
            +allowsPassedPrerequisite() void
            +rejectsFailedPrerequisite() void
            +rejectsPrerequisiteInIncompleteSemester() void
            +rejectsWhenOnePrerequisiteIsMissing() void
            +allowsWhenAllPrerequisitesArePassed() void
        }
    }

    IStudent <|.. FakeStudent : implements
    FakeStudent "1" o-- "0..8" SemesterRecord : supplies test data

    TestDataGenerator ..> Student : creates
    TestDataGenerator ..> GradingPolicyManager : uses

    GpaCalculatorTest ..> GpaCalculator : unit tests
    GpaCalculatorTest ..> FakeStudent : injects fake
    GpaCalculatorTest ..> TestDataGenerator : builds fixtures

    PrerequisiteCheckerTest ..> PrerequisiteChecker : unit tests
    PrerequisiteCheckerTest ..> Student : creates test students
    PrerequisiteCheckerTest ..> Course : creates courses
    PrerequisiteCheckerTest ..> GradingPolicyManager : creates course results
```

## Behavior and constraints

- **Student and semester records:** `Student` implements `IStudent`, which extends `IPerson`. A student can have semester records numbered 1 through 8.
- **Semester records and results:** A semester owns its course results. An in-progress semester may contain fewer courses, but `complete()` requires four or five courses and prevents further additions.
- **Courses and prerequisites:** A `Course` can have zero or more prerequisite courses. `addPrerequisite()` rejects a null prerequisite, a course that refers to itself, and duplicate prerequisites.
- **Prerequisite checking:** `PrerequisiteChecker.canRegister(student, course)` returns `true` if all prerequisites have been passed in completed semesters. It returns `false` if any prerequisite is missing, failed, or appears only in an incomplete semester. A course with no prerequisites is eligible for registration.
- **Grading:** `GradingPolicyManager` maps percentages to grades. `CourseResult` stores the course, percentage, and grade and determines whether the result is a pass.
- **GPA calculation:** `GpaCalculator` calculates credit-weighted semester and cumulative GPAs. Cumulative GPA and course-performance comparisons use completed semesters only.
- **Performance summaries:** `StudentPerformanceSummary` contains student GPA results, the best and worst performers, the arithmetic average GPA, and the student whose GPA is closest to the average.
- **Test support:** `FakeStudent` allows tests to isolate calculator behavior from production student validation. `PrerequisiteCheckerTest` verifies the seven prerequisite-registration scenarios.

## Test-Driven Development

The prerequisite module tests follow the Red-Green-Refactor cycle:

1. **Red:** Write a test for the expected prerequisite behavior and confirm it fails.
2. **Green:** Implement the minimum functionality to make the test pass.
3. **Refactor:** Improve the implementation while keeping all tests passing.

Run the test suite from the project root:

```bash
mvn test
```
