# GPA system TDD template

The sample domain lives in `org.example.tdd.gpasystem`; its calculator tests,
`FakeStudent`, and `TestDataGenerator` are under `src/test/java`.
See [the UML class diagram](docs/GpaSystemClassDiagram.md) for class
relationships and calculator behavior.

## Model and relationships

- `Student` implements `IStudent`, which extends `IPerson`, and aggregates
  semester records, indexed from 1 through 8.
- A `SemesterRecord` owns its course results. A completed semester must contain
  four or five courses; a semester still in progress may have fewer.
- Each `CourseResult` associates a course with a percentage and its policy grade.
  `GradingPolicyManager` creates results so letter grades and points are kept in
  sync.
- `GpaCalculator` depends on these domain types to calculate weighted GPA and
  summarize performance. Course credits weight grade points in semester and
  cumulative GPA.

`GpaCalculator` receives its student dependency through method parameters typed
as `IStudent` (or a collection of `IStudent`s). This is parameter injection:
production callers pass `Student`, while unit tests can pass `FakeStudent`
without constructing a validated production entity. `IStudent` defines only
the data the calculator needs; names and IDs are reporting details, and GPA
calculation does not reject a student because its ID is blank or missing. The
tests demonstrate both an individual semester calculation and course rankings
using fake students.

For example, a unit test can supply the fake directly:

```java
IStudent fake = new FakeStudent(null, "Ada Lovelace");
fake.addSemester(completedSemesterWithKnownCourseResults);
double gpa = new GpaCalculator().calculateSemesterGpa(fake, 1);
```

The default grading policy maps 90/80/70/60 percent to 4/3/2/1 points, with
lower percentages earning 0 points. Supply custom `GradeBand`s to use a
different policy. Cumulative GPA and cohort comparisons include completed
semesters only. Course comparisons combine completed attempts of that course.
The reported average performer is the student whose GPA is closest to the
cohort's arithmetic mean.

Run the JUnit 5 examples with `mvn test`.
