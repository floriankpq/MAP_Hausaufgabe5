package Model;

import java.util.List;

/**
 * Ein Kurs hat einen eindeutigen Name @name, ein Lehrer @teacher, ein maxEnrollment Nummer @maxEnrollment,
 * eine liste von Studenten @studentsEnrolled und eine CreditAnzahl @credits
 */
public class Course {
    private String name;
    private Teacher teacher;
    private int maxEnrollment;
    private List<Student> studentsEnrolled;
    private int credits;

    public Course(String name, Teacher teacher, int maxEnrollment, List<Student> studentsEnrolled, int credits) {
        this.name = name;
        this.teacher = teacher;
        this.maxEnrollment = maxEnrollment;
        this.studentsEnrolled = studentsEnrolled;
        this.credits = credits;
    }

    public Course(String name, Teacher teacher, int maxEnrollment, int credits) {
        this.name = name;
        this.teacher = teacher;
        this.maxEnrollment = maxEnrollment;
        this.credits = credits;
    }

    public Course(String name, int maxEnrollment, int credits) {
        this.name = name;
        this.maxEnrollment = maxEnrollment;
        this.credits = credits;
    }

    public Course() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public int getMaxEnrollment() {
        return maxEnrollment;
    }

    public void setMaxEnrollment(int maxEnrollment) {
        this.maxEnrollment = maxEnrollment;
    }

    public List<Student> getStudentsEnrolled() {
        return studentsEnrolled;
    }

    public void setStudentsEnrolled(List<Student> studentsEnrolled) {
        this.studentsEnrolled = studentsEnrolled;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    @Override
    public String toString() {
        boolean first = true;
        String students = "[";
        if(studentsEnrolled!=null){
            for(Student s : studentsEnrolled){
                if(first){
                    students = students + s.getStudentId() + " : " + s.getFirstName() + " " + s.getLastName();
                    first = false;
                }
                else{
                    students = students + "," + s.getStudentId() + " : " + s.getFirstName() + " " + s.getLastName();
                }
            }
        }
        students += "]";

        return "Course{" +
                "name='" + name + '\'' +
                ", teacher = " + teacher.getTeacherId() + " : " +teacher.getFirstName() + " " + teacher.getLastName()+
                ", maxEnrollment=" + maxEnrollment +
                ", studentsEnrolled=" + students +
                ", credits=" + credits +
                '}';
    }
}
