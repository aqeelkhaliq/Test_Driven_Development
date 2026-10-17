# GPA System UML Class Diagram

The diagram shows production types in `org.example.tdd.gpasystem` and test
support types in the test source set. Solid diamonds indicate composition,
open diamonds aggregation, solid arrows association, dashed arrows dependency,
and hollow triangles interface inheritance/realization.

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
    }

    class CourseResult {
        -percentage double
        -course Course
        -grade Grade
        +getPercentage() double
        +getCourse() Course
        +getGrade() Grade
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

    IPerson <|-- IStudent : extends
    IStudent <|.. Student : implements
    Student "1" o-- "0..8" SemesterRecord : aggregates
    SemesterRecord "1" *-- "0..5" CourseResult : owns
    CourseResult "1" --> "1" Course : result for
    CourseResult "1" --> "1" Grade : awarded
    GradingPolicyManager "1" o-- "1..*" GradeBand : applies
    GradeBand "1" --> "1" Grade : maps to

    GpaCalculator ..> IStudent : accepts injected student(s)
    GpaCalculator ..> CourseResult : calculates from
    GpaCalculator ..> StudentGpa : creates rankings
    GpaCalculator ..> StudentPerformanceSummary : returns
    StudentGpa "0..*" --> "1" IStudent : describes
    StudentPerformanceSummary "1" *-- "1..*" StudentGpa : contains

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
    }

    IStudent <|.. FakeStudent : implements
    FakeStudent "1" o-- "0..8" SemesterRecord : supplies test data
    TestDataGenerator ..> Student : creates
    TestDataGenerator ..> GradingPolicyManager : uses
    GpaCalculatorTest ..> GpaCalculator : unit tests
    GpaCalculatorTest ..> FakeStudent : injects fake
    GpaCalculatorTest ..> TestDataGenerator : builds fixtures
```

## Behavior and constraints

- `IStudent` is the injected boundary for `GpaCalculator`. The calculator reads
  semesters and identity details through the interface; it does not require the
  concrete `Student` constructor or validate student IDs.
- A semester is numbered 1 through 8. It can be built with fewer courses while
  in progress, but `complete()` requires four or five courses and prevents
  further additions.
- `GradingPolicyManager` maps percentages to a `Grade`; `CourseResult` retains
  that grade with the course and earned percentage.
- Semester and cumulative GPA are weighted by course credits. Cumulative GPA
  and comparisons only use completed semesters.
- Course performance combines a student's completed attempts at the requested
  course. A performance summary gives the ordered student results, best, worst,
  arithmetic average GPA, and the student closest to that average.
- `FakeStudent` is deliberately permissive for unit tests (for example, it may
  return a null or blank ID). This isolates calculator behavior from production
  entity validation.
