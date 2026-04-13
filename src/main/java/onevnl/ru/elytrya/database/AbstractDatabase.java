package onevnl.ru.elytrya.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.plugin.java.JavaPlugin;

import onevnl.ru.elytrya.models.BoostyUser;

public abstract class AbstractDatabase implements Database {
    protected final JavaPlugin plugin;
    protected Connection connection;

    protected AbstractDatabase(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS boosty_links (uuid VARCHAR(36) PRIMARY KEY, player_name VARCHAR(16), boosty_name VARCHAR(255), level_name VARCHAR(255))";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public BoostyUser getUser(UUID uuid) {
        String sql = "SELECT * FROM boosty_links WHERE uuid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getActiveSubscribersCount() {
        String sql = "SELECT COUNT(*) FROM boosty_links WHERE level_name != 'none'";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public String getBoostyName(UUID uuid) {
        String sql = "SELECT boosty_name FROM boosty_links WHERE uuid = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, uuid.toString());
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) return rs.getString("boosty_name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean isBoostyNameLinked(String boostyName) {
        String sql = "SELECT 1 FROM boosty_links WHERE boosty_name = ? LIMIT 1";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, boostyName);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<BoostyUser> getAllUsers() {
        List<BoostyUser> list = new ArrayList<>();
        String sql = "SELECT * FROM boosty_links";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) list.add(mapUser(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void removeLink(UUID uuid) {
        String sql = "DELETE FROM boosty_links WHERE uuid = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, uuid.toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateLevel(UUID uuid, String levelName) {
        String sql = "UPDATE boosty_links SET level_name = ? WHERE uuid = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, levelName);
            statement.setString(2, uuid.toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected BoostyUser mapUser(ResultSet rs) throws SQLException {
        return new BoostyUser(UUID.fromString(rs.getString("uuid")), rs.getString("player_name"), rs.getString("boosty_name"), rs.getString("level_name"));
    }
}