import Controller.RegistrationSystem;

import Model.Student;
import Repository.CourseJdbcRepository;
import Repository.StudentsJdbcRepository;
import Repository.TeachersJdbcRepository;

import View.ConsoleView;

import java.sql.*;
import java.util.List;

public class Main {
    public static void main(String[] args) throws SQLException {
        //creez repo-uri
        StudentsJdbcRepository sjr = new StudentsJdbcRepository();
        TeachersJdbcRepository tjr = new TeachersJdbcRepository();
        CourseJdbcRepository cjr = new CourseJdbcRepository();

        //creez controller-ul
        RegistrationSystem rs = new RegistrationSystem(sjr,tjr,cjr);
        //creez view-ul
        ConsoleView view = new ConsoleView(rs);
        view.menu();
    }
}
