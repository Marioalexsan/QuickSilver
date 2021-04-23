package hg.maps;

import com.badlogic.gdx.math.Vector2;
import hg.maps.EnvironmentDescription;
import hg.maps.MapPrototype;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Class that holds static methods for reading and writing map files (and other map related stuff).
 * TODO Implement map loading in AssetEngine
 */
public class MapRWMethods {
    public static final int CurrentMapVersion = 1;

    /** This function allows a prototype to be saved to a file. */
    public static void WriteMapToFile(MapPrototype proto, String path) {
        if (proto == null || Files.exists(Path.of(path))) return;

        try (DataOutputStream file = new DataOutputStream(new FileOutputStream(path))) {
            file.writeInt(CurrentMapVersion);

            file.writeInt(proto.environments.size());
            for (var env : proto.environments) {
                file.writeInt(env.ID);
                file.writeFloat(env.position.x);
                file.writeFloat(env.position.y);
                file.writeFloat(env.angle);
            }
        }
        catch (Exception ignored) { }
    }

    /** Loads a map from a file */
    public static MapPrototype LoadMapFromFile(String path) {
        if (!Files.exists(Path.of(path))) return null;

        try (DataInputStream file = new DataInputStream(new FileInputStream(path))) {
            MapPrototype proto = new MapPrototype();

            int version = file.readInt();

            int envCount = file.readInt();
            while (envCount-- > 0) {
                var env = new EnvironmentDescription(file.readInt(), new Vector2(file.readFloat(), file.readFloat()), file.readFloat());
                proto.environments.add(env);
            }
            return proto;
        }
        catch (Exception ignored) {
            return null;
        }
    }
}
