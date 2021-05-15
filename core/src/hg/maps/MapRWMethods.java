package hg.maps;

import com.badlogic.gdx.math.Vector2;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;

/**
 * Class that holds static methods for reading and writing map files (and other map related stuff).
 * TODO Implement map loading in AssetEngine
 */
public class MapRWMethods {
    public static final int CurrentMapVersion = 2;

    /** This function allows a prototype to be saved to a file. */
    public static void WriteMapToFile(MapPrototype proto, String path) {
        if (proto == null || Files.exists(Path.of(path))) return;

        try (DataOutputStream file = new DataOutputStream(new FileOutputStream(path))) {
            file.writeInt(CurrentMapVersion);

            // Write environments
            file.writeInt(proto.environments.size());
            for (var env : proto.environments) {
                file.writeInt(env.objectType);
                file.writeFloat(env.position.x);
                file.writeFloat(env.position.y);
                file.writeFloat(env.angle);
            }

            // Write random spawnpoints
            file.writeInt(proto.randomSpawnpoints.size());
            for (var point: proto.randomSpawnpoints) {
                file.writeFloat(point.x);
                file.writeFloat(point.y);
            }

            // Write spawnpoints for each team index
            file.writeInt(proto.teamSpawnpoints.size());
            for (var thisTeamPoints: proto.teamSpawnpoints) {
                file.writeInt(thisTeamPoints.size());
                for (var point: thisTeamPoints) {
                    file.writeFloat(point.x);
                    file.writeFloat(point.y);
                }
            }
        }
        catch (Exception ignored) { }
    }

    /** Loads a map from a file */
    public static MapPrototype LoadMapFromFile(String path) {
        if (!Files.exists(Path.of(path))) return null;

        try (DataInputStream file = new DataInputStream(new FileInputStream(path))) {
            MapPrototype proto = new MapPrototype();

            int mapVersion = file.readInt();

            // Read environments
            int envCount = file.readInt();
            while (envCount-- > 0) {
                var env = new Description(file.readInt(), new Vector2(file.readFloat(), file.readFloat()), file.readFloat());
                proto.environments.add(env);
            }

            // Read random spawnpoints
            if (mapVersion >= 2) {
                int randomCount = file.readInt();
                while (randomCount-- > 0) {
                    proto.randomSpawnpoints.add(new Vector2(file.readFloat(), file.readFloat()));
                }
            }

            // Read team spawnpoints
            if (mapVersion >= 2) {
                int teamCount = file.readInt();
                while (teamCount-- > 0) {
                    int teamPointCount = file.readInt();

                    var teamPointList = new LinkedList<Vector2>();
                    proto.teamSpawnpoints.add(teamPointList);

                    while (teamPointCount-- > 0)
                        teamPointList.add(new Vector2(file.readFloat(), file.readFloat()));
                }
            }

            return proto;
        }
        catch (Exception ignored) {
            return null;
        }
    }
}
