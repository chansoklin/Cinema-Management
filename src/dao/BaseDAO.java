package dao;

import util.DBConnection;
import exception.DatabaseException;
import java.sql.*;

public abstract class BaseDAO {

    protected Connection getConnection() throws DatabaseException {
        try {
            return DBConnection.getConnection();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to get database connection", e);
        }
    }

    protected void closeResources(ResultSet rs, PreparedStatement ps) {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void handleException(String message, Exception e) throws DatabaseException {
        throw new DatabaseException(message + ": " + e.getMessage(), e);
    }
}