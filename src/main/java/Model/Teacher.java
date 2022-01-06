package Model;

import java.util.List;

/**
 * ist eine Person mit @teacherID und @courses
 */
public class Teacher extends Person{
    private List<Course> courses;
    private Long teacherId;

    public Teacher(Long teacherId, String firstName, String lastName, List<Course> courses) {
        super(firstName,lastName);
        this.teacherId = teacherId;
        this.courses = courses;
    }

    public Teacher( Long teacherId, String firstName, String lastName) {
        super(firstName, lastName);
        this.teacherId = teacherId;
    }

    public Teacher(String firstName, String lastName) {
        super(firstName, lastName);
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    @Override
    public String toString() {
        boolean first = true;
        String teacherCourses = "[";
        if(courses!=null){
            for(Course c : courses){
                if(first){
                    teacherCourses += c.getName();
                    first = false;
                }
                else{
                    teacherCourses += "," + c.getName();
                }
            }
        }
        teacherCourses += "]";

        return "Teacher{" +
                "teacherId=" + teacherId +
                ", vorname = " + this.getFirstName() +
                ", nachname = " + this.getLastName() +
                ", courses=" + teacherCourses +
                '}';
    }
}
