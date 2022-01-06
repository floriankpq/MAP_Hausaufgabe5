package Repository;

import Model.Course;
import Model.Student;
import Model.Teacher;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseJdbcRepository implements ICrudRepo<Course, String> {

    String url;
    String user;
    String pwd;
    Connection connection;

    public CourseJdbcRepository() throws SQLException {
        this.url = "jdbc:sqlserver://desktop-uk1s2ub\\sqlexpress;databaseName=MAP";
        this.user = "k";
        this.pwd = "12345";
        this.connection = DriverManager.getConnection(url,user,pwd);
    }

    /**
     * Searches a Course based on it's primary key
     * @param name - unique identifier of a Course
     * @return Course if found, null else
     * @throws SQLException connection couldn't be established
     */
    public Course findOne(String name) throws SQLException {

        Statement statement = connection.createStatement();

        Course someCourse = new Course();
        List<Student> students = new ArrayList<>();

        String sqlCourse = "select c.name as 'coursename', c.credits, c.maxEnrollment, t.id, t.name, t.vorname\n" +
                "from courses c\n" +
                "inner join teachers t\n" +
                "on t.id = c.teacherId\n" +
                "where c.name = " + "'" + name + "'";

        ResultSet result = statement.executeQuery(sqlCourse);

        if(result.next()){
            someCourse.setName(result.getString("coursename"));
            someCourse.setCredits(result.getInt("credits"));
            someCourse.setMaxEnrollment(result.getInt("maxEnrollment"));
            someCourse.setTeacher(new Teacher(result.getLong("id"), result.getString("vorname"),result.getString("name") ));

            String sqlStudents = "select s.id, s.name, s.vorname from students s\n" +
                    "inner join Students_Courses_Enrolled sce \n" +
                    "on s.id = sce.id\n" +
                    "where sce.name = " + "'" + name + "'";

            ResultSet resultStudents = statement.executeQuery(sqlStudents);

            while(resultStudents.next()){
                Student student = new Student(resultStudents.getLong("id"),
                        resultStudents.getString("vorname"),
                        resultStudents.getString("name"));

                students.add(student);
            }

            someCourse.setStudentsEnrolled(students);

            return someCourse;
        }
        else
            return null;
    }

    /**
     * finds all Courses
     * @return all Courses
     * @throws SQLException connection couldn't be established
     */
    @Override
    public List<Course> findAll() throws SQLException {
        Statement statement = connection.createStatement();

        List<String> names = new ArrayList<>();
        List<Course> courses = new ArrayList<>();

        String sqlCourseNames = "select name from Courses";
        ResultSet result = statement.executeQuery(sqlCourseNames);

        while(result.next()){
            names.add(result.getString("name"));
        }

        for(String name : names){
            Course someCourse = findOne(name);
            courses.add(someCourse);
        }

        return courses;
    }

    /**
     * Adds a Course to the database
     * @param obj the Course we want to add
     * @return null when the Teacher is invalid or the name already exists, else obj
     * @throws SQLException connection couldn't be established
     */
    @Override
    public Course save(Course obj) throws SQLException {

        Statement statement = connection.createStatement();

        String sqlToDeleteCourse = "select count(*)\n" +
                "from Teachers \n" +
                "where id = " + obj.getTeacher().getTeacherId();

        ResultSet rs = statement.executeQuery(sqlToDeleteCourse);
        rs.next();
        int count = rs.getInt(1);

        if(findOne(obj.getName())!=null)
            return null;
        else
            if(count == 0){
                System.out.println("invalid teacher!");
                return null;
            }
        else{
            String sqlInsertCourse = "insert into Courses (name, credits, maxEnrollment, teacherId) values (" +
                    "'" + obj.getName() + "'" + "," +
                    obj.getCredits() + "," +
                    obj.getMaxEnrollment() + "," +
                    obj.getTeacher().getTeacherId() + ")";

            statement.executeUpdate(sqlInsertCourse);

            if(obj.getStudentsEnrolled()!=null){
                for(Student s : obj.getStudentsEnrolled()){
                    String sqlInsertStudentsEnrolled = "insert into Students_Courses_Enrolled (name, id) values (" +
                            "'" + obj.getName() + "'" + "," +
                            s.getStudentId()+")";

                    statement.executeUpdate(sqlInsertStudentsEnrolled);
                }
                return new Course(obj.getName(), obj.getTeacher(), obj.getMaxEnrollment(), obj.getStudentsEnrolled(), obj.getCredits());
            }
            return new Course(obj.getName(), obj.getTeacher(), obj.getMaxEnrollment(), obj.getCredits());
        }
    }

    /**
     * Updates all attributes of the course with the same name as the param obj
     * @param obj Course
     * @return the updated object or null if it already exists
     * @throws SQLException connection couldn't be established
     */
    @Override
    public Course update(Course obj) throws SQLException {

        if(findOne(obj.getName())==null)
            return null;
        else{
            String sqlUpdateCourse = "update Courses set credits = " + obj.getCredits() + "," +
                    " maxEnrollment = " + obj.getMaxEnrollment() + "," +
                    " teacherId =" + obj.getTeacher().getTeacherId() +
                    " where name="+ "'" + obj.getName() + "'";

            Statement statement = connection.createStatement();
            statement.executeUpdate(sqlUpdateCourse);

            String deleteEnrolled  = "delete from Students_Courses_Enrolled where name = " +
                    "'" + obj.getName() + "'";

            statement.executeUpdate(deleteEnrolled);

            for(Student s : obj.getStudentsEnrolled()){

                String sqlInsertEnrolled = "insert into Students_Courses_Enrolled (name,id) values (" +
                        "'" + obj.getName() + "'" + "," +
                        s.getStudentId() + ")";

                statement.executeUpdate(sqlInsertEnrolled);
            }

            return obj;
        }
    }

    /**
     * Deletes the Course with the same name as the parameter
     * @param name primary key of the Course we want to delete
     * @return true if deleted, false otherwise
     * @throws SQLException connection couldn't be established
     */
    @Override
    public boolean delete(String name) throws SQLException {

        Statement statement = connection.createStatement();

        String sqlToDeleteCourse = "select count(*)\n" +
                "from Courses \n" +
                "where name = " + "'" + name + "'";

        ResultSet rs = statement.executeQuery(sqlToDeleteCourse);
        rs.next();
        int count = rs.getInt(1);

        if(count == 0){
            return false;
        }
        else{
            String sqlDeleteCoursesEnrolled = "delete from Students_Courses_Enrolled\n" +
                    "where name = " + "'" + name + "'";

            statement.executeUpdate(sqlDeleteCoursesEnrolled);

            String sqlDeleteCourse = "delete from Courses\n" +
                    "where name = " + "'" + name + "'";

            statement.executeUpdate(sqlDeleteCourse);

            return true;
        }
    }
}