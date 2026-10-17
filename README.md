# GPA System TDD Template

The sample domain lives in `org.example.tdd.gpasystem`; its calculator tests, `FakeStudent`, and `TestDataGenerator` are under `src/test/java`. See the [UML class diagram](docs/GpaSystemClassDiagram.md) for class relationships and calculator behavior.

## Model and Relationships

- `Student` implements `IStudent`, which extends `IPerson`, and aggregates semester records, indexed from 1 through 8.
- A `SemesterRecord` owns its course results. A completed semester must contain four or five courses; a semester still in progress may have fewer.
- Each `CourseResult` associates a course with a percentage and its policy grade. `GradingPolicyManager` creates results so letter grades and points are kept in sync.
- `GpaCalculator` depends on these domain types to calculate weighted GPA and summarize performance. Course credits weight grade points in semester and cumulative GPA.
- `Course` represents a course and its details, including its code, title, credits, and prerequisite courses. A course can have multiple prerequisites.
- `PrerequisiteChecker` determines whether a student is eligible to register for a course based on the student's completed academic history and the course's prerequisites.

## GPA Calculation and Dependency Injection

`GpaCalculator` receives its student dependency through method parameters typed as `IStudent` (or a collection of `IStudent`s). This is parameter injection: production callers pass `Student`, while unit tests can pass `FakeStudent` without constructing a validated production entity.

`IStudent` defines only the data the calculator needs; names and IDs are reporting details, and GPA calculation does not reject a student because its ID is blank or missing. The tests demonstrate both an individual semester calculation and course rankings using fake students.

For example, a unit test can supply the fake directly:

```java
IStudent fake = new FakeStudent(null, "Ada Lovelace");
fake.addSemester(completedSemesterWithKnownCourseResults);
double gpa = new GpaCalculator().calculateSemesterGpa(fake, 1);
```

## Prerequisite Checking Module

The `PrerequisiteChecker` module checks whether a student has passed every prerequisite required for a course.

Its main operation is:

```java
boolean canRegister(IStudent student, Course course);
```

The method returns:

- `true` when the course has no prerequisites.
- `true` when the student has passed all required prerequisites in completed semesters.
- `false` when a required prerequisite has not been completed or passed.
- `false` when a prerequisite appears only in a semester that is still in progress.

The checker uses the `IStudent` interface instead of depending directly on the concrete `Student` class. This reduces coupling and makes the module easier to test.

`Course` provides `addPrerequisite()` to add prerequisite courses and `getPrerequisites()` to retrieve the list. A course cannot be its own prerequisite, and duplicate prerequisites are rejected.

## Prerequisite Module Tests

`PrerequisiteCheckerTest` contains seven test scenarios:

1. A course with no prerequisites allows registration.
2. Registration is rejected when a prerequisite is missing.
3. Registration is allowed when the prerequisite has been passed.
4. Registration is rejected when the prerequisite has been failed.
5. Registration is rejected when the prerequisite exists only in an incomplete semester.
6. Registration is rejected when one of multiple prerequisites is missing.
7. Registration is allowed when all prerequisites have been passed.

These tests verify the module's expected behavior, its interaction with student academic records, and the rules governing course registration.

Completed-semester test data must contain four or five course results before the semester is marked complete. Helper methods can supply additional courses so that each test follows the domain rules.

## Grading Policy

The default grading policy maps 90, 80, 70, and 60 percent to 4, 3, 2, and 1 grade points, respectively, with lower percentages earning 0 points. Supply custom `GradeBand`s to use a different policy.

Cumulative GPA and cohort comparisons include completed semesters only. Course comparisons combine completed attempts of that course. The reported average performer is the student whose GPA is closest to the cohort's arithmetic mean.

## Test-Driven Development

The project follows the Red-Green-Refactor approach:

1. **Red:** Write a test that describes the expected behavior and confirm that it fails.
2. **Green:** Implement the minimum code needed to make the test pass.
3. **Refactor:** Improve the code while keeping all tests passing.

The prerequisite module follows this approach by testing registration rules for courses with no prerequisites, missing prerequisites, passed prerequisites, failed prerequisites, incomplete semesters, and multiple prerequisites.

## Running the Tests

Run the JUnit 5 test suite from the project root:

```bash
mvn test
```

The current test suite includes the GPA calculator, course result, and prerequisite checker tests. All 27 tests passed in the latest successful run.
