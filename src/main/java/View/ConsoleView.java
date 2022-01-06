package View;

import Controller.RegistrationSystem;
import Exceptions.NullValueException;
import Model.Course;
import Model.Student;
import Model.Teacher;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class ConsoleView {

    private RegistrationSystem controller;

    public ConsoleView(RegistrationSystem controller) {
        this.controller = controller;
    }

    void printStudentMenu(){
        System.out.println("1. Add Student");
        System.out.println("2. Delete Student");
        System.out.println("3. Show Students enrolled to a course");
        System.out.println("4. Find all students");
        System.out.println("5. Find Student by Id");
        System.out.println("6. Sort Students by the number of EnrolledCourses");
        System.out.println("7. Filter Students that attend no course");
        System.out.println("8. Exit");
    }

    void studentMenu(){

        printStudentMenu();
        while (true)
            try {

                Scanner scanner = new Scanner(System.in);
                System.out.println("Choose option: ");
                int option = Integer.parseInt(scanner.nextLine());

                if (option < 1 || option > 9){
                    //throw new InvalidMenuOptionException("Invalid value");
                    System.out.println("X");
                }


                if (option == 8)
                    break;
                else if (option == 1)
                    addStudent();
                else if (option == 2)
                    deleteStudent();
                else if (option == 3)
                    enrolledStudents();
                else if (option == 4)
                    findAllStudents();
                else if (option == 5)
                    findStudentById();
                else if (option == 6)
                    sortByNumberOfCredits();
                else if (option == 7)
                    filterStudentsAttendingNoCourse();

            } catch (Exception e) {
                System.out.println(e);
            }
    }

    public void addStudent() throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter studentId: ");
        long id = Long.parseLong(scanner.nextLine());
        System.out.println("Enter firstName: ");
        String firstName = scanner.nextLine();
        System.out.println("Enter lastName: ");
        String lastName = scanner.nextLine();
        System.out.println("Enter the size of enrolledCourses: ");
        int size = Integer.parseInt(scanner.nextLine());

        List<Course> courseList = new ArrayList<>();

        if (size != 0) {
            System.out.println("Enter courses name: ");

            for (int i = 0; i < size; i++) {
                Course c = this.chooseCourse();
                courseList.add(c);
            }
        }
        Student newStudent = new Student(id, firstName, lastName, courseList);
        try {
            controller.addStudent(newStudent);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void deleteStudent() throws SQLException, NullValueException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter a studentId: ");
        this.findAllStudents();
        long id = Long.parseLong(scanner.nextLine());
        controller.deleteStudent(id);
    }

    public void enrolledStudents() throws SQLException, NullValueException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter courseName: ");
        String courseName = scanner.nextLine();
        if(controller.enrolledStudents(courseName)!=null)
            controller.enrolledStudents(courseName).forEach(System.out::println);
        else
            System.out.println("Course does not exist");
    }

    public void findAllStudents() throws SQLException, NullValueException {
        this.controller.getAllStudents().forEach(System.out::println);
    }

    public void findAllTeachers() throws SQLException, NullValueException {
        this.controller.getAllTeachers().forEach(System.out::println);
    }

    public void findStudentById() throws SQLException, NullValueException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter studentId: ");
        long id = Long.parseLong(scanner.nextLine());

        System.out.println(controller.findOne(id));
    }

    public void filterStudentsAttendingNoCourse() throws SQLException, NullValueException {
        this.controller.filterStudentsZeroCredits().forEach(System.out::println);
    }

    public void sortByNumberOfCredits() throws SQLException, NullValueException {
        this.controller.sortStudentsByCredits().forEach(System.out::println);
    }

    void printCourseMenu(){
        System.out.println("1. Enroll a Student to a Course");
        System.out.println("2. Add Course");
        System.out.println("3. Delete Course");
        System.out.println("4. Find all Courses");
        System.out.println("5. Find all Courses with free places");
        System.out.println("6. Filter Courses with 0 Students");
        System.out.println("7. Sort Courses by the number of credits");
        System.out.println("8. Modify the credits of a course");
        System.out.println("9. Exit");
    }

    void courseMenu(){
        printCourseMenu();
        while (true)
            try {

                Scanner scanner = new Scanner(System.in);
                System.out.println("Choose option: ");
                int option = Integer.parseInt(scanner.nextLine());

                if (option == 9)
                    break;
                else if (option == 1)
                    enroll();
                else if (option == 2)
                    addCourse();
                else if (option == 3)
                    deleteCourse();
                else if (option == 4)
                    findAllCourses();
                else if(option == 5)
                    findAllAvailableCourses();
                else if(option == 6)
                    filterCoursesZeroStudents();
                else if (option == 7)
                    sortByNumberOfEnrolledStudents();
                else if (option == 8)
                    modifyCredits();
            } catch (Exception e) {
                System.out.println(e);
            }
    }

    public void modifyCredits() throws Exception {
        System.out.println("Choose a Course by name :");
        Course c = this.chooseCourse();

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter new credits: ");
        int credits = Integer.parseInt(scanner.nextLine());

        c.setCredits(credits);
        this.controller.modifyCredits(c);
    }

    public void enroll() throws Exception {
        System.out.println("Choose a Student by id :");
        Student s = this.chooseStudent();

        System.out.println("Choose a Course by name :");
        Course c = this.chooseCourse();

        controller.registerStudentToCourse(c,s);
    }

    public Student chooseStudent() throws Exception {
        this.findAllStudents();

        //lista cu id urile studentilor
        List<Long> ids = this.controller.getAllStudents().stream()
                .map(Student::getStudentId)
                .collect(Collectors.toList());

        System.out.println("Enter studentId : ");
        Scanner scanner = new Scanner(System.in);
        long id = Long.parseLong(scanner.nextLine());

        if(!ids.contains(id))
            throw new Exception("Invalid id!");
        else
            return controller.findOne(id);
    }

    public Course chooseCourse() throws Exception {
        this.findAllAvailableCourses();

        //lista cu numele cursurilor
        List<String> names = this.controller.getAllCourses().stream()
                .map(Course::getName)
                .collect(Collectors.toList());

        System.out.println("Enter CourseName : ");
        Scanner scanner = new Scanner(System.in);
        String name = scanner.nextLine();

        if(!names.contains(name))
            throw new Exception("Invalid name!");
        else
            return controller.findOne(name);
    }

    public Teacher chooseTeacher() throws Exception {
        this.findAllTeachers();

        List<Long> ids = this.controller.getAllTeachers().stream()
                .map(Teacher::getTeacherId)
                .collect(Collectors.toList());

        System.out.println("Enter teacherId : ");
        Scanner scanner = new Scanner(System.in);
        long id = Long.parseLong(scanner.nextLine());

        if(!ids.contains(id))
            throw new Exception("Invalid id!");
        else
            return controller.findOneTeach(id);
    }

    public void addCourse() throws Exception {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter courseName: ");
        String courseName = scanner.nextLine();
        System.out.println("Enter maxEnrollment: ");
        int maxEnrollment = Integer.parseInt(scanner.nextLine());
        System.out.println("Enter credits: ");
        int credits = Integer.parseInt(scanner.nextLine());
        System.out.println("Choose a Teacher: ");
        Teacher newTeach = this.chooseTeacher();


        System.out.println("Enter the size of enrolledStudents: ");
        int size = Integer.parseInt(scanner.nextLine());


        List<Student> studList = new ArrayList<>();

        if (size != 0) {
            System.out.println("Enter studentId: ");

            for (int i = 0; i < size; i++) {
                Student s = this.chooseStudent();
                if(s.getTotalCredits()+credits>30){
                    throw new Exception("Max number of credits exceeded!");
                }

                studList.add(s);
            }
        }


        Course newCourse = new Course(courseName,newTeach,maxEnrollment,studList,credits);
            try {
            controller.addCourse(newCourse);
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public void deleteCourse() throws SQLException, NullValueException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter courseName: ");
        this.findAllAvailableCourses();
        String courseName = scanner.nextLine();
        controller.deleteCourse(courseName);
    }

    public void findAllCourses() throws SQLException, NullValueException {
        this.controller.getAllCourses().forEach(System.out::println);
    }

    public void findAllAvailableCourses() throws SQLException, NullValueException {
        this.controller.availableCourses().forEach(System.out::println);
    }

    public void filterCoursesZeroStudents() throws SQLException, NullValueException {
        this.controller.filterCoursesZeroStudents().forEach(System.out::println);
    }

    public void sortByNumberOfEnrolledStudents() throws SQLException, NullValueException {
        this.controller.sortCoursesByCredits().forEach(System.out::println);
    }

    public void menu() {
        System.out.println("Menu:");
        System.out.println('\t' + "1. Student Menu");
        System.out.println('\t' + "2. Course Menu");
        System.out.println('\t' + "3. Exit");

        Scanner scanner = new Scanner(System.in);

        while(true) {

            System.out.println("Choose option 1, 2 or 3: ");
            int option = scanner.nextInt();

            if (option == 1) {
                studentMenu();
            } else if (option == 2) {
                System.out.println(2);
                courseMenu();
            }
            else if(option == 3){
                break;
            }
        }
    }
}
