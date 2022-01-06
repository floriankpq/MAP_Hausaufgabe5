package Controller;

import Exceptions.NullValueException;
import Model.Course;
import Model.Student;
import Model.Teacher;
import Repository.CourseJdbcRepository;
import Repository.StudentsJdbcRepository;
import Repository.TeachersJdbcRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class RegistrationSystem {

    private StudentsJdbcRepository studRepo;
    private TeachersJdbcRepository teachRepo;
    private CourseJdbcRepository courseRepo;

    public RegistrationSystem(StudentsJdbcRepository studRepo, TeachersJdbcRepository teachRepo, CourseJdbcRepository courseRepo) {
        this.studRepo = studRepo;
        this.teachRepo = teachRepo;
        this.courseRepo = courseRepo;
    }

    public boolean addStudent(Student s) throws Exception {
        if(s.getTotalCredits() > 30)
            throw new Exception("Max number of credits exceeded!");
        this.studRepo.save(s);
        return true;
    }

    public boolean deleteStudent(Long id) throws SQLException, NullValueException {
        this.studRepo.delete(id);
        return true;
    }

    public List<Student> getAllStudents() throws SQLException, NullValueException {
        return this.studRepo.findAll();
    }

    public Student findOne(Long id) throws SQLException, NullValueException {
        return this.studRepo.findOne(id);
    }

    public List<Student> sortStudentsByCredits() throws SQLException, NullValueException {
        List<Student> sortedStudents = this.getAllStudents()
                .stream()
                .sorted(Comparator.comparing(Student::getTotalCredits))
                .collect(Collectors.toList());

        return sortedStudents;
    }

    public List<Student> filterStudentsZeroCredits() throws SQLException, NullValueException {
        List<Student> filteredStudents = this.getAllStudents()
                .stream()
                .filter(s->s.getTotalCredits()==0)
                .collect(Collectors.toList());

        return filteredStudents;
    }

    public List<Student> enrolledStudents(String course) throws SQLException, NullValueException {
        if(course==null)
            return null;
        if(this.courseRepo.findOne(course)!=null){
            List<Student> studsEnrolled = new ArrayList<>();

            for(Student s : this.studRepo.findAll()){
                if(s.getEnrolledCoursesName().contains(course))
                    studsEnrolled.add(s);
            }

            return studsEnrolled;
        }
        return null;
    }

    public boolean addCourse(Course c) throws SQLException, NullValueException {
        this.courseRepo.save(c);
        return true;
    }

    public boolean deleteCourse(String course) throws SQLException, NullValueException {
        this.courseRepo.delete(course);
        return true;
    }

    public List<Course> getAllCourses() throws SQLException, NullValueException {
        return this.courseRepo.findAll();
    }

    public Course findOne(String name) throws SQLException, NullValueException {
        return this.courseRepo.findOne(name);
    }

    public List<Course> sortCoursesByCredits() throws SQLException, NullValueException {
        List<Course> sortedCourses = this.getAllCourses()
                .stream()
                .sorted(Comparator.comparing(Course::getCredits))
                .collect(Collectors.toList());

        return sortedCourses;
    }

    public List<Course> filterCoursesZeroStudents() throws SQLException, NullValueException {
        List<Course> filteredCourses = this.getAllCourses()
                .stream()
                .filter(c->c.getStudentsEnrolled().size() == 0)
                .collect(Collectors.toList());

        return filteredCourses;
    }

    public List<Course> availableCourses() throws SQLException, NullValueException {
        List<Course> availableCourses = this.getAllCourses()
                .stream()
                .filter(c->c.getStudentsEnrolled().size() != c.getMaxEnrollment())
                .collect(Collectors.toList());

        return availableCourses;
    }

    public boolean registerStudentToCourse(Course course, Student student) throws Exception {

        //check if course exists in repo
        if (course == null || courseRepo.findOne(course.getName()) == null) {
            throw new Exception("Non-existing course id!");
        }

        //check if student exists in repo
        if (student == null || studRepo.findOne(student.getStudentId()) == null) {
            throw new Exception("Non-existing student id!");
        }
        List<Student> courseStudents = course.getStudentsEnrolled();
        //check if course has free places
        if (courseStudents.size() == course.getMaxEnrollment()) {
            throw new Exception("Course has no free places!");
        }

        //check if student is already enrolled

        boolean found = courseStudents
                .stream()
                .anyMatch(s -> s.getStudentId() == student.getStudentId());

        if (found)
            throw new Exception("Student already enrolled!");

        //if student has over 30 credits after enrolling to this course
        int studCredits = student.getTotalCredits() + course.getCredits();
        if (studCredits > 30)
            throw new Exception("Total number of credits exceeded!");

        //add student to course
        //update courses repo
        courseStudents.add(student);
        course.setStudentsEnrolled(courseStudents);
        courseRepo.update(course);


        //update enrolled courses of Student
        List<Course> studCourses = student.getEnrolledCourses();
        studCourses.add(course);
        student.setEnrolledCourses(studCourses);

        //update students Repo
        studRepo.update(student);

        return true;
    }

    public void modifyCredits(Course c) throws NullValueException, SQLException {

        this.courseRepo.update(c);
    }

    public Teacher findOneTeach(Long id) throws SQLException, NullValueException {
        return teachRepo.findOne(id);
    }

    public List<Teacher> getAllTeachers() throws SQLException, NullValueException {
        return this.teachRepo.findAll();
    }
}
