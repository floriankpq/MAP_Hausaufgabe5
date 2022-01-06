package Repository;

import Exceptions.NullValueException;

import java.sql.SQLException;
import java.util.List;

public interface ICrudRepo<T,E> {

    T findOne(E id) throws NullValueException, SQLException;

    List<T> findAll() throws SQLException, NullValueException;

    T save(T obj) throws NullValueException, SQLException;

    T update (T obj) throws NullValueException, SQLException;

    boolean delete(E id) throws NullValueException, SQLException;
}
