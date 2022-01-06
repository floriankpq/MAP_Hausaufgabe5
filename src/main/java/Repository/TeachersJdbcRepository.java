package Repository;

import Model.Course;
import Model.Teacher;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TeachersJdbcRepository implements ICrudRepo<Teacher, Long> {

    String url;
    String user;
    String pwd;
    Connection connection;

    public TeachersJdbcRepository() throws SQLException {
        this.url = "jdbc:sqlserver://desktop-uk1s2ub\\sqlexpress;databaseName=MAP";
        this.user = "k";
        this.pwd = "12345";
        this.connection = DriverManager.getConnection(url,user,pwd);
    }

    /**
     * Searches a Teacher based on it's primary key
     * @param id - unique identifier of a Teacher
     * @return Teacher if found, null else
     * @throws SQLException connection couldn't be established
     */
    @Override
    public Teacher findOne(Long id) throws SQLException {

        Statement statement = connection.createStatement();

        Teacher someTeach = new Teacher("","");
        List<Course> courses = new ArrayList<>();

        String sqlTeacher = "select *\n" +
                "from teachers\n" +
                "where id =" + id;

        ResultSet result = statement.executeQuery(sqlTeacher);

        if(result.next()){
            someTeach.setTeacherId(result.getLong("id"));
            someTeach.setLastName(result.getString("name"));
            someTeach.setFirstName(result.getString("vorname"));
        }
        else
            return null;

        String sqlIfCoursesExist = "select * from courses\n" +
                "where teacherId = " + id;

        result = statement.executeQuery(sqlIfCoursesExist);

        while(result.next()){
            Course someCourse = new Course();
            someCourse.setName(result.getString("name"));
            someCourse.setCredits(result.getInt("credits"));
            someCourse.setMaxEnrollment(result.getInt("maxEnrollment"));

            someCourse.setTeacher(someTeach);

            courses.add(someCourse);
        }
        someTeach.setCourses(courses);
        return someTeach;
    }

    /**
     * finds all Teachers
     * @return all Teachers
     * @throws SQLException connection couldn't be established @throws SQLException
     */
    @Override
    public List<Teacher> findAll() throws SQLException {
        Statement statement = connection.createStatement();

        List<Long> ids = new ArrayList<>();
        List<Teacher> teachers = new ArrayList<>();

        String sqlTeacherIds = "select id from Teachers";
        ResultSet result = statement.executeQuery(sqlTeacherIds);

        while(result.next()){
            ids.add(result.getLong("id"));
        }

        for(Long id : ids){
            Teacher someTeach = findOne(id);
            teachers.add(someTeach);
        }

        return teachers;
    }

    /**
     * Adds a Teacher to the database
     * @param obj the Teacher we want to add
     * @return  null when the Teacher is invalid or the id already exists, else obj
     * @throws SQLException connection couldn't be established
     */
    @Override
    public Teacher save(Teacher obj) throws SQLException {
        //daca findOne nu e null inseamna ca exista deja acest Teacher
        if(findOne(obj.getTeacherId())!=null)
            return null;
        else{
            Statement statement = connection.createStatement();

            String sqlInsertTeach = "insert into Teachers (id,name,vorname) values (" +
                    obj.getTeacherId() + "," +
                    "'" + obj.getLastName() + "'" + "," +
                    "'" + obj.getFirstName() + "'" + ")";

            statement.executeUpdate(sqlInsertTeach);

            if(obj.getCourses()!=null){
                for(Course c : obj.getCourses()){
                    String sqlInsertCourse = "insert into Courses (name, credits, maxEnrollment, teacherId) values (" +
                            "'" + c.getName() + "'" + "," +
                            c.getCredits() + "," +
                            c.getMaxEnrollment() + "," +
                            obj.getTeacherId() + ")";

                    statement.executeUpdate(sqlInsertCourse);
                }
                return new Teacher(obj.getTeacherId(),obj.getFirstName(),obj.getLastName(),obj.getCourses());
            }
            return new Teacher(obj.getTeacherId(),obj.getFirstName(),obj.getLastName());
        }
    }

    /**
     * Updates all attributes of the Teacher with the same id as the param obj
     * @param obj Teacher
     * @return the updated object or null if it already exists
     * @throws SQLException connection couldn't be established
     */
    @Override
    public Teacher update(Teacher obj) throws SQLException {
        if(findOne(obj.getTeacherId())==null)
            return null;
        else{
            String sqlUpdateTeacher = "update Teachers set vorname = " + " ' " + obj.getFirstName() + " ' " + "," +
                    " name = " + " ' " + obj.getLastName() + " ' " + " where id = " +
                    " ' " + obj.getTeacherId() + " ' ";

            Statement statement = connection.createStatement();
            statement.executeUpdate(sqlUpdateTeacher);

            return obj;
        }
    }

    /**
     * Deletes the Teacher with the same id as the parameter
     * @param id primary key of the Teacher we want to delete
     * @return true if deleted, false otherwise
     * @throws SQLException connection couldn't be established
     */
    @Override
    public boolean delete(Long id) throws SQLException {
        Statement statement = connection.createStatement();

        String sqlToDeleteTeacher = "select count(*)\n" +
                "from Teachers \n" +
                "where id = " + id;

        ResultSet rs = statement.executeQuery(sqlToDeleteTeacher);
        rs.next();
        int count = rs.getInt(1);

        if(count == 0){
            return false;
        }
        else{
            String sqlGetCoursesName = "select name from courses\n" +
                    "where teacherId = " + id;

            ResultSet result = statement.executeQuery(sqlGetCoursesName);

            List<String> courses = new ArrayList<>();

            while(result.next()){
                courses.add(result.getString("name"));
            }

            for(String course : courses){

                PreparedStatement st = connection.prepareStatement("DELETE FROM Students_Courses_Enrolled WHERE name = ?");
                st.setString(1,course);
                st.executeUpdate();
            }

            String sqlDeleteCourse = "delete from Courses\n" +
                    "where teacherId = " + id;

            statement.executeUpdate(sqlDeleteCourse);

            String sqlDeleteTeacher = "delete from Teachers\n" +
                    "where id = " + id;

            statement.executeUpdate(sqlDeleteTeacher);

            return true;
        }
    }

    /**
     * gets all Students enrolled to one-of-many courses a Teacher teaches
     * @param teachId Teachers id
     * @return id's of the enrolled Students
     * @throws SQLException connection couldn't be established
     */
    public List<Long> getEnrolledStuds(Long teachId) throws SQLException {
        Statement statement = connection.createStatement();

        List<Long> studs = new ArrayList<>();
        List<String> courses = new ArrayList<>();

        String sqlTeacherCourses = "select name\n" +
                "from Courses \n" +
                "where teacherId = " + teachId;

        ResultSet result = statement.executeQuery(sqlTeacherCourses);
        while(result.next()){
            courses.add(result.getString("name"));
        }

        for(String c : courses){

            String sqlStudentsEnrolled = "select id\n" +
                    "from Students_Courses_Enrolled \n" +
                    "where name = " + "'" + c + "'";

            result = statement.executeQuery(sqlStudentsEnrolled);

            while(result.next()){
                studs.add(result.getLong("id"));
            }
        }

        return studs.stream()
                        .distinct()
                        .collect(Collectors.toList());
    }
}