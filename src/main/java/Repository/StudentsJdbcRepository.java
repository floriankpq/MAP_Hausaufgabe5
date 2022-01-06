package Repository;

import Model.Course;
import Model.Student;
import Model.Teacher;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentsJdbcRepository implements ICrudRepo<Student, Long>{

    String url;
    String user;
    String pwd;
    Connection connection;

    public StudentsJdbcRepository() throws SQLException {
        this.url = "jdbc:sqlserver://desktop-uk1s2ub\\sqlexpress;databaseName=MAP";
        this.user = "k";
        this.pwd = "12345";
        this.connection = DriverManager.getConnection(url,user,pwd);
    }

    /**
     * Searches a Student based on it's primary key
     * @param id - unique identifier of a Student
     * @return Student if found, null else
     * @throws SQLException connection couldn't be established
     */
    @Override
    public Student findOne(Long id) throws SQLException {

        Statement statement = connection.createStatement();

        Student someStud = new Student("","");
        List<Course> courses = new ArrayList<>();

        String sqlStudent = "select *\n" +
                "from students\n" +
                "where id =" + id;

        ResultSet result = statement.executeQuery(sqlStudent);

        if(result.next()){
            someStud.setStudentId(result.getLong("id"));
            someStud.setLastName(result.getString("name"));
            someStud.setFirstName(result.getString("vorname"));
        }
        else
            return null;

        String sqlIfCoursesExist = "select c.name as 'coursename',c.credits,c.maxEnrollment,c.teacherId,t.name, t.vorname\n" +
                "from Students_Courses_Enrolled sce\n" +
                "inner join Courses c\n" +
                "on sce.name = c.name\n" +
                "inner join Teachers t\n" +
                "on c.teacherId = t.id\n" +
                "where sce.id = " + id;

        result = statement.executeQuery(sqlIfCoursesExist);

        while(result.next()){
            Course someCourse = new Course();
            someCourse.setName(result.getString("coursename"));
            someCourse.setCredits(result.getInt("credits"));
            someCourse.setMaxEnrollment(result.getInt("maxEnrollment"));

            someCourse.setTeacher(new Teacher(
                    result.getLong("teacherId"),
                    result.getString("name"),
                    result.getString("vorname")
            ));

            courses.add(someCourse);
        }

        someStud.setEnrolledCourses(courses);
        return someStud;

    }

    /**
     * finds all Students
     * @return all Students
     * @throws SQLException connection couldn't be established @throws SQLException
     */
    @Override
    public List<Student> findAll() throws SQLException {

        Statement statement = connection.createStatement();

        List<Long> ids = new ArrayList<>();
        List<Student> students = new ArrayList<>();

        String sqlStudentIds = "select id from Students";
        ResultSet result = statement.executeQuery(sqlStudentIds);

        while(result.next()){
            ids.add(result.getLong("id"));
        }

        for(Long id : ids){
            Student someStud = findOne(id);
            students.add(someStud);
        }

        return students;
    }

    /**
     * Adds a Student to the database
     * @param obj the Student we want to add
     * @return  null when the Student is invalid or the id already exists, else obj
     * @throws SQLException connection couldn't be established
     */
    @Override
    public Student save(Student obj) throws SQLException {

        //daca findOne nu e null inseamna ca exista deja acel student
        if(findOne(obj.getStudentId())!=null)
            return null;
        else{
            Statement statement = connection.createStatement();

            String sqlInsertStud = "insert into Students (id,name,vorname) values (" +
                    obj.getStudentId() + "," +
                    "'" + obj.getLastName() + "'" + "," +
                    "'" + obj.getFirstName() + "'" + ")";

            statement.executeUpdate(sqlInsertStud);

            if(obj.getEnrolledCourses()!=null){
                for(Course c : obj.getEnrolledCourses()){
                    String sqlInsertEnrolled = "insert into Students_Courses_Enrolled (name,id) values (" +
                            "'" + c.getName() + "'" + "," +
                            obj.getStudentId() + ")";

                    statement.executeUpdate(sqlInsertEnrolled);
                }
                return new Student(obj.getStudentId(),obj.getFirstName(),obj.getLastName(),obj.getEnrolledCourses());
            }
            return new Student(obj.getStudentId(),obj.getFirstName(),obj.getLastName());
        }
    }

    /**
     * Updates all attributes of the Student with the same id as the param obj
     * @param obj Student
     * @return the updated object or null if it already exists
     * @throws SQLException connection couldn't be established
     */
    @Override
    public Student update(Student obj) throws SQLException {

        //daca findOne e null inseamna ca nu exista acel student
        if(findOne(obj.getStudentId())==null)
            return null;
        else{
            String sqlUpdateStudent = "update Students set vorname = " + " ' " + obj.getFirstName() + " ' " + "," +
                    " name = " + "'" + obj.getLastName() + "'" + " where id = " +
                    "'" + obj.getStudentId() + "'";

            Statement statement = connection.createStatement();
            statement.executeUpdate(sqlUpdateStudent);

            String deleteEnrolled  = "delete from Students_Courses_Enrolled where id = " +
                    "'" + obj.getStudentId() + "'";

            statement.executeUpdate(deleteEnrolled);

            for(Course c : obj.getEnrolledCourses()){

                String sqlInsertEnrolled = "insert into Students_Courses_Enrolled (name,id) values (" +
                        "'" + c.getName() + "'" + "," +
                        obj.getStudentId() + ")";

                statement.executeUpdate(sqlInsertEnrolled);
            }

            return obj;
        }
    }

    /**
     * Deletes the Student with the same id as the parameter
     * @param id primary key of the Student we want to delete
     * @return true if deleted, false otherwise
     * @throws SQLException connection couldn't be established
     */
    @Override
    public boolean delete(Long id) throws SQLException {

        Statement statement = connection.createStatement();

        String sqlToDeleteStudent = "select count(*)\n" +
                "from students \n" +
                "where id = " + id;

        ResultSet rs = statement.executeQuery(sqlToDeleteStudent);
        rs.next();
        int count = rs.getInt(1);

        if(count == 0){
            return false;
        }
        else{
            String sqlDeleteStudentEnrolled = "delete from Students_Courses_Enrolled\n" +
                    "where id = " + id;

            statement.executeUpdate(sqlDeleteStudentEnrolled);

            String sqlDeleteStudent = "delete from Students\n" +
                    "where id = " + id;

            statement.executeUpdate(sqlDeleteStudent);

            return true;
        }
    }
}