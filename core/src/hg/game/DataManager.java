package hg.game;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.HashMap;

public class DataManager {
    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("DatabaseManager failed to load JDBC Driver!");
        }
    }

    private static final HashMap<String, String> currentSettings = new HashMap<>();

    private Connection openSettingsDB() {
        try {
            var settings = DriverManager.getConnection("jdbc:sqlite:UserData/Settings.db");
            settings.setAutoCommit(false);
            return settings;
        }
        catch (Exception ignored) {}
        return null;
    }

    private void closeSettingsDB(Connection settings) {
        try {
            if (settings != null && !settings.isClosed()) {
                settings.close();
                settings = null;
            }
        }
        catch (Exception ignored) {}
    }

    public DataManager() {
        try {
            Files.createDirectory(Path.of("UserData"));
        } catch (Exception ignored) {}

        currentSettings.putAll(GameVars.GetAllDefaultSettings());
        readSettingsFromDB();
    }

    public void cleanup() {
        writeSettingsToDB();
    }

    private void readSettingsFromDB() {
        var settings = openSettingsDB();
        if (settings == null) return;
        try {
            for (var id: GameVars.GetAllDefaultSettings().keySet()) {
                PreparedStatement find = settings.prepareStatement(
                        "SELECT * FROM SETTINGS WHERE ID = ?"
                );
                find.setString(1, id);
                ResultSet existing = find.executeQuery();
                if (existing.next())
                    updateSetting(id, existing.getString(2));
            }
        }
        catch (Exception ignored) {}
        closeSettingsDB(settings);
    }

    private void writeSettingsToDB() {
        var settings = openSettingsDB();
        if (settings == null) return;
        try {
            DatabaseMetaData meta = settings.getMetaData();
            ResultSet results = meta.getTables(null, null, "SETTINGS", new String[] {"TABLE"});

            if (!results.next())
                settings.createStatement().execute("CREATE TABLE SETTINGS (ID VARCHAR(50), VALUE VARCHAR(1337));");

            for (var opt: currentSettings.entrySet()) {
                PreparedStatement find = settings.prepareStatement(
                        "SELECT * FROM SETTINGS WHERE ID = ?"
                );
                find.setString(1, opt.getKey());
                ResultSet existing = find.executeQuery();

                PreparedStatement ps;
                if (existing.next()) {
                    ps = settings.prepareStatement("UPDATE SETTINGS SET VALUE = ? WHERE ID = ?");
                    ps.setString(1, opt.getValue());
                    ps.setString(2, opt.getKey());
                }
                else {
                    ps = settings.prepareStatement("INSERT INTO SETTINGS VALUES (?, ?)");
                    ps.setString(1, opt.getKey());
                    ps.setString(2, opt.getValue());
                }
                ps.execute();
            }
            settings.commit();
        }
        catch (Exception ignored) {}
        closeSettingsDB(settings);
    }

    public String getSetting(String id) {
        String which = currentSettings.get(id);
        return which != null ? which : "";
    }

    public void updateSetting(String id, String value) {
        if (currentSettings.containsKey(id))
            currentSettings.put(id, value);
    }
}
