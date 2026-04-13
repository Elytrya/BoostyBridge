package onevnl.ru.elytrya.database;

import java.io.File;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.UUID;

import org.bukkit.plugin.java.JavaPlugin;

public class SQLite extends AbstractDatabase {

    public SQLite(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public void connect() {
        try {
            File folder = new File(plugin.getDataFolder(), "database");
            if (!folder.exists()) {
                folder.mkdirs();
            }
            File file = new File(folder, "database.db");
            connection = DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());
            createTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveLink(UUID uuid, String playerName, String boostyName, String levelName) {
        try (PreparedStatement statement = connection.prepareStatement(
                "REPLACE INTO boosty_links (uuid, player_name, boosty_name, level_name) VALUES (?, ?, ?, ?)")) {
            statement.setString(1, uuid.toString());
            statement.setString(2, playerName);
            statement.setString(3, boostyName);
            statement.setString(4, levelName);
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}