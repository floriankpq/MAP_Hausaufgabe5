package Model;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ist eine Person mit @studentId, @totalCredits und @enrolledCourses
 */
public class Student extends Person{

    private long studentId;
    private int totalCredits;
    private List<Course> enrolledCourses;

    public Student(long studentId, String firstName, String lastName, List<Course> enrolledCourses) {

        super(firstName, lastName);

        this.studentId = studentId;
        this.enrolledCourses = enrolledCourses;

        int credits = 0;
        for(Course c : this.enrolledCourses){
            credits += c.getCredits();
        }

        this.totalCredits = credits;
    }

    public Student(long studentId, String firstName, String lastName) {
        super(firstName, lastName);
        this.studentId = studentId;
    }

    public Student(String firstName, String lastName) {
        super(firstName, lastName);
    }

    public long getStudentId() {
        return studentId;
    }

    public void setStudentId(long studentId) {
        this.studentId = studentId;
    }

    public int getTotalCredits() {
        return totalCredits;
    }

    public void setTotalCredits(int totalCredits) {
        this.totalCredits = totalCredits;
    }

    public List<Course> getEnrolledCourses() {
        return enrolledCourses;
    }

    public List<String> getEnrolledCoursesName(){
        return enrolledCourses.stream()
                .map(Course::getName)
                .collect(Collectors.toList());
    }

    public void setEnrolledCourses(List<Course> enrolledCourses) {
        this.enrolledCourses = enrolledCourses;

        int credits = 0;
        for(Course c : this.enrolledCourses){
            credits += c.getCredits();
        }
        this.setTotalCredits(credits);
    }

    @Override
    public String toString() {
        boolean first = true;
        String courses = "[";
        if(enrolledCourses!=null){
            for(Course c : enrolledCourses){
                if(first){
                    courses += c.getName();
                    first = false;
                }
                else{
                    courses += "," + c.getName();
                }
            }
        }
        courses += "]";

        return "Student{" +
                "studentId = " + studentId +
                ", vorname = " + this.getFirstName() +
                ", nachname = " + this.getLastName() +
                ", totalCredits = " + totalCredits +
                ", enrolledCourses = " + courses +
                "}";
    }
}
