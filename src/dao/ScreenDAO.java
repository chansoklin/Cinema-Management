package dao;

import model.Screen;
import exception.DatabaseException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ScreenDAO extends BaseDAO {

    public List<Screen> getAllScreens() throws DatabaseException {
        List<Screen> screens = new ArrayList<>();
        String sql = "SELECT * FROM screens ORDER BY id";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                screens.add(extractScreenFromResultSet(rs));
            }
        } catch (SQLException e) {
            handleException("Error getting all screens", e);
        }
        return screens;
    }

    public Screen getScreenById(int id) throws DatabaseException {
        String sql = "SELECT * FROM screens WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return extractScreenFromResultSet(rs);
            }
            return null;
        } catch (SQLException e) {
            handleException("Error getting screen by id", e);
            return null;
        }
    }

    public boolean createScreen(Screen screen) throws DatabaseException {
        String sql = "INSERT INTO screens (name, capacity, rows, cols) VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, screen.getName());
            ps.setInt(2, screen.getCapacity());
            ps.setInt(3, screen.getRows());
            ps.setInt(4, screen.getCols());

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    screen.setId(rs.getInt(1));
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            handleException("Error creating screen", e);
            return false;
        }
    }

    public boolean updateScreen(Screen screen) throws DatabaseException {
        String sql = "UPDATE screens SET name = ?, capacity = ?, rows = ?, cols = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, screen.getName());
            ps.setInt(2, screen.getCapacity());
            ps.setInt(3, screen.getRows());
            ps.setInt(4, screen.getCols());
            ps.setInt(5, screen.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            handleException("Error updating screen", e);
            return false;
        }
    }

    public boolean deleteScreen(int id) throws DatabaseException {
        String sql = "DELETE FROM screens WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            handleException("Error deleting screen", e);
            return false;
        }
    }

    private Screen extractScreenFromResultSet(ResultSet rs) throws SQLException {
        Screen screen = new Screen();
        screen.setId(rs.getInt("id"));
        screen.setName(rs.getString("name"));
        screen.setCapacity(rs.getInt("capacity"));

        // Check if rows and cols columns exist
        try {
            screen.setRows(rs.getInt("rows"));
        } catch (SQLException e) {
            screen.setRows(0);
        }

        try {
            screen.setCols(rs.getInt("cols"));
        } catch (SQLException e) {
            screen.setCols(0);
        }

        return screen;
    }
}