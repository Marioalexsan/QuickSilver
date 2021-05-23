package hg.game;

import hg.gamelogic.gamemodes.Deathmatch;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

/** DataManager stores settings and statistics, and manages SQL database connections. */
public class DataManager {
    public static class DeathmatchResult {
        String playerName;
        int score;
        boolean winner;

        public DeathmatchResult(String playerName, int score, boolean winner) {
            this.playerName = playerName;
            this.score = score;
            this.winner = winner;
        }
    }

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("DatabaseManager failed to load JDBC Driver!");
        }
    }

    private final HashMap<String, String> currentSettings = new HashMap<>();
    private final HashMap<Long, ArrayList<DeathmatchResult>> dmResultsToSave = new HashMap<>();

    private Connection openDB(String path) {
        try {
            var settings = DriverManager.getConnection("jdbc:sqlite:" + path);
            settings.setAutoCommit(false);
            return settings;
        }
        catch (Exception ignored) {}
        return null;
    }

    private void closeDB(Connection conn) {
        try {
            if (conn != null && !conn.isClosed())
                conn.close();
        }
        catch (Exception ignored) {}
    }

    public DataManager() {
        try {
            Files.createDirectory(Path.of("UserData"));
        } catch (Exception ignored) {}

        currentSettings.putAll(GameVars.GetAllDefaultSettings());
        readMatchesFromDB();
        readSettingsFromDB();
    }

    public void cleanup() {
        writeSettingsToDB();
        writeMatchesToDB();
    }

    public void addDeathmatchResult(ArrayList<DeathmatchResult> result) {
        dmResultsToSave.put(System.currentTimeMillis(), result);
    }

    public HashMap<Long, ArrayList<DeathmatchResult>> getDeathmatchResults() {
        return new HashMap<>(dmResultsToSave);
    }

    private void readMatchesFromDB() {
        var matches = openDB("UserData/Matches.db");
        if (matches == null) return;
        try {
            DatabaseMetaData meta = matches.getMetaData();
            ResultSet results = meta.getTables(null, null, null, new String[] {"TABLE"});

            while (results.next()) {
                String table = results.getString(3);

                Statement st = matches.createStatement();
                st.execute("SELECT * FROM " + table);

                ResultSet existing = st.getResultSet();
                ArrayList<DeathmatchResult> dmResults = new ArrayList<>();
                dmResultsToSave.put(Long.parseLong(table.substring(5)), dmResults);
                while (existing.next()) {
                    dmResults.add(new DeathmatchResult(existing.getString(1), existing.getInt(2), existing.getString(3).equals("Yes")));
                }
            }
        }
        catch (Exception ignored) {}
        closeDB(matches);
    }

    private void writeMatchesToDB() {
        var matches = openDB("UserData/Matches.db");
        if (matches == null) return;
        try {
            for (var record: dmResultsToSave.entrySet()) {
                String table = "MATCH" + record.getKey();

                DatabaseMetaData meta = matches.getMetaData();
                ResultSet results = meta.getTables(null, null, table, new String[] {"TABLE"});

                if (results.next()) continue;

                matches.createStatement().execute("CREATE TABLE " + table + " (NAME VARCHAR(50), SCORE INT(255), WINNER VARCHAR(10));");

                for (var player: record.getValue()) {
                    PreparedStatement ps = matches.prepareStatement("INSERT INTO " + table + " VALUES (?, ?, ?)");
                    ps.setString(1, player.playerName);
                    ps.setInt(2, player.score);
                    ps.setString(3, player.winner ? "Yes" : "No");
                    ps.execute();
                }
            }
            matches.commit();
        }
        catch (Exception ignored) {
            matches = null;
        }
        closeDB(matches);
    }

    private void readSettingsFromDB() {
        var settings = openDB("UserData/Settings.db");
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
        closeDB(settings);
    }

    private void writeSettingsToDB() {
        var settings = openDB("UserData/Settings.db");
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
        closeDB(settings);
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
