package onevnl.ru.elytrya.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import onevnl.ru.elytrya.models.BoostyUser;

public class MySQL implements Database {

    private final JavaPlugin plugin;
    private Connection connection;

    public MySQL(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void connect() {
        try {
            FileConfiguration config = plugin.getConfig();
            String host = config.getString("database.mysql.host");
            int port = config.getInt("database.mysql.port");
            String database = config.getString("database.mysql.database");
            String username = config.getString("database.mysql.username");
            String password = config.getString("database.mysql.password");

            String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true&useSSL=false";
            connection = DriverManager.getConnection(url, username, password);
            createTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createTable() {
        try (PreparedStatement statement = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS boosty_links (" +
                        "uuid VARCHAR(36) PRIMARY KEY, " +
                        "player_name VARCHAR(16), " +
                        "boosty_name VARCHAR(255), " +
                        "level_name VARCHAR(255))")) {
            statement.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveLink(UUID uuid, String playerName, String boostyName, String levelName) {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO boosty_links (uuid, player_name, boosty_name, level_name) VALUES (?, ?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE player_name = ?, boosty_name = ?, level_name = ?")) {
            statement.setString(1, uuid.toString());
            statement.setString(2, playerName);
            statement.setString(3, boostyName);
            statement.setString(4, levelName);
            statement.setString(5, playerName);
            statement.setString(6, boostyName);
            statement.setString(7, levelName);
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBoostyName(UUID uuid) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT boosty_name FROM boosty_links WHERE uuid = ?")) {
            statement.setString(1, uuid.toString());
            ResultSet rs = statement.executeQuery();
            if (rs.next()) return rs.getString("boosty_name");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean isBoostyNameLinked(String boostyName) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT uuid FROM boosty_links WHERE boosty_name = ?")) {
            statement.setString(1, boostyName);
            ResultSet rs = statement.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    @Override
        public BoostyUser getUser(UUID uuid) {
            String sql = "SELECT * FROM boosty_links WHERE uuid = ?";
            try (java.sql.PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, uuid.toString());
                try (java.sql.ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return new BoostyUser(
                            uuid,
                            rs.getString("player_name"),
                            rs.getString("boosty_name"),
                            rs.getString("level_name")
                        );
                    }
                }
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

    @Override
        public int getActiveSubscribersCount() {
            String sql = "SELECT COUNT(*) FROM boosty_links WHERE level_name != 'none'";
            try (java.sql.PreparedStatement stmt = connection.prepareStatement(sql);
                java.sql.ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1); 
                }
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
            return 0;
        }
    @Override
    public List<BoostyUser> getAllUsers() {
        List<BoostyUser> list = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM boosty_links")) {
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                list.add(new BoostyUser(
                        UUID.fromString(rs.getString("uuid")),
                        rs.getString("player_name"),
                        rs.getString("boosty_name"),
                        rs.getString("level_name")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void removeLink(UUID uuid) {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM boosty_links WHERE uuid = ?")) {
            statement.setString(1, uuid.toString());
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateLevel(UUID uuid, String levelName) {
        try (PreparedStatement statement = connection.prepareStatement("UPDATE boosty_links SET level_name = ? WHERE uuid = ?")) {
            statement.setString(1, levelName);
            statement.setString(2, uuid.toString());
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}