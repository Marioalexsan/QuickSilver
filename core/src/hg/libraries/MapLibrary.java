package hg.libraries;

import com.badlogic.gdx.math.Vector2;
import hg.id.EnvID;
import hg.maps.EnvironmentDescription;
import hg.maps.MapPrototype;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class MapLibrary {
    public enum StaticMaps {
        TestArea01
    }

    public static MapPrototype CreatePrototype(StaticMaps map) {
        switch (map) {
            case TestArea01 -> {
                MapPrototype proto = new MapPrototype();

                var environments = proto.environments;

                // === Level Boundary Walls and Flooring ===

                for (int sectionY = 50; sectionY <= 5550; sectionY += 100)
                    environments.add(new EnvironmentDescription(EnvID.BrickDefault, new Vector2(50, sectionY), 0f));

                for (int sectionY = 50; sectionY <= 5550; sectionY += 100)
                    environments.add(new EnvironmentDescription(EnvID.BrickDefault, new Vector2(6550, sectionY), 0f));

                for (int sectionX = 150; sectionX <= 6550; sectionX += 100) {
                    environments.add(new EnvironmentDescription(EnvID.BrickDefault, new Vector2(sectionX, 50), 0f));
                    environments.add(new EnvironmentDescription(EnvID.BrickDefault, new Vector2(sectionX, 5550), 0f));
                }

                // * Big Floor

                for (int sectionX = 150; sectionX <= 6450; sectionX += 100) {
                    for (int sectionY = 150; sectionY <= 5450; sectionY += 100) {
                        environments.add(new EnvironmentDescription(EnvID.ConcreteFloor, new Vector2(sectionX, sectionY), 0f));
                    }
                }

                // === "Corner Room" ===

                for (int sectionX = 150; sectionX <= 1450; sectionX += 100) {
                    if (sectionX >= 650 && sectionX <= 850) continue;
                    environments.add(new EnvironmentDescription(EnvID.BrickDefault, new Vector2(sectionX, 2450), 0f));
                }

                for (int sectionY = 150; sectionY <= 2350; sectionY += 100) {
                    if (sectionY >= 450 && sectionY <= 650) continue;
                    environments.add(new EnvironmentDescription(EnvID.BrickDefault, new Vector2(1450, sectionY), 0f));
                }

                // === "Peekers" && "Sniper Hallway" ===

                for (int sectionX = 1550; sectionX <= 4750; sectionX += 100) {
                    if (sectionX >= 2250 && sectionX <= 2450) continue;
                    environments.add(new EnvironmentDescription(EnvID.BrickDefault, new Vector2(sectionX, 1050), 0f));
                }

                for (int sectionY = 1150; sectionY <= 1950; sectionY += 100) {
                    environments.add(new EnvironmentDescription(EnvID.BrickDefault, new Vector2(4750, sectionY), 0f));
                }

                for (int sectionX = 4750; sectionX <= 6550; sectionX += 100) {
                    if (sectionX >= 5750 && sectionX <= 6050) continue;
                    environments.add(new EnvironmentDescription(EnvID.BrickDefault, new Vector2(sectionX, 2050), 0f));
                }

                // * Metal Bars
                for (int sectionY = 150; sectionY <= 950; sectionY += 100) {
                    environments.add(new EnvironmentDescription(EnvID.BrickMetalBars, new Vector2(3350, sectionY), 90f));
                }
                
                // * Peekers Boxes Up
                environments.add(new EnvironmentDescription(EnvID.BoxMedium, new Vector2(2150, 940), 0f));
                environments.add(new EnvironmentDescription(EnvID.BoxMedium, new Vector2(2135, 825), -15f));
                environments.add(new EnvironmentDescription(EnvID.BoxMedium, new Vector2(2075, 730), -30f));

                // * Peekers Boxes Down
                environments.add(new EnvironmentDescription(EnvID.BoxMedium, new Vector2(2180, 170), 0f));
                environments.add(new EnvironmentDescription(EnvID.BoxMedium, new Vector2(2200, 285), -15f));
                environments.add(new EnvironmentDescription(EnvID.BoxMedium, new Vector2(2230, 400), -30f));

                // * Peekers Boxes Right
                environments.add(new EnvironmentDescription(EnvID.BoxMedium, new Vector2(2730, 410), 0));
                environments.add(new EnvironmentDescription(EnvID.BoxMedium, new Vector2(2750, 515), 0));

                environments.add(new EnvironmentDescription(EnvID.BoxMedium, new Vector2(2790, 720), 0));
                environments.add(new EnvironmentDescription(EnvID.BoxMedium, new Vector2(2840, 810), 0));

                environments.add(new EnvironmentDescription(EnvID.BoxMedium, new Vector2(2985, 530), 0));
                environments.add(new EnvironmentDescription(EnvID.BoxMedium, new Vector2(3000, 620), 0));


                // * Sniper Hallway Stuff
                environments.add(new EnvironmentDescription(EnvID.BoxMedium, new Vector2(4850, 1050), 0f));
                environments.add(new EnvironmentDescription(EnvID.BoxMedium, new Vector2(4950, 1050), 0f));
                environments.add(new EnvironmentDescription(EnvID.BoxMedium, new Vector2(5090, 1060), 45f));
                environments.add(new EnvironmentDescription(EnvID.BoxMedium, new Vector2(5190, 1125), 45f));
                environments.add(new EnvironmentDescription(EnvID.BoxMedium, new Vector2(5245, 1245), 0f));
                environments.add(new EnvironmentDescription(EnvID.BoxMedium, new Vector2(5245, 1330), 0f));
                environments.add(new EnvironmentDescription(EnvID.BoxMedium, new Vector2(5240, 1445), 0f));
                environments.add(new EnvironmentDescription(EnvID.BoxMedium, new Vector2(5345, 1255), 0f));
                environments.add(new EnvironmentDescription(EnvID.BoxMedium, new Vector2(5345, 1340), 0f));
                environments.add(new EnvironmentDescription(EnvID.BoxMedium, new Vector2(5340, 1435), 0f));
                environments.add(new EnvironmentDescription(EnvID.BrickPillarBig, new Vector2(5230, 1625), 0f));

                // === "Side Hallway" ===

                for (int sectionY = 2550; sectionY <= 4350; sectionY += 100) {
                    if (sectionY >= 3350 && sectionY <= 3550) continue;
                    environments.add(new EnvironmentDescription(EnvID.BrickDefault, new Vector2(1150, sectionY), 0f));
                }

                // === "Back Room", "Lookers" ===

                for (int sectionY = 4450; sectionY <= 5450; sectionY += 100) {
                    environments.add(new EnvironmentDescription(EnvID.BrickDefault, new Vector2(1850, sectionY), 0f));
                }

                for (int sectionX = 150; sectionX <= 4550; sectionX += 100) {
                    if (sectionX >= 650 && sectionX <= 850) continue;
                    if (sectionX >= 2650 && sectionX <= 2850) continue;
                    environments.add(new EnvironmentDescription(EnvID.BrickDefault, new Vector2(sectionX, 4450), 0f));
                }

                // === "Storage" ===

                for (int sectionY = 3950; sectionY <= 5450; sectionY += 100) {
                    if (sectionY >= 4850 && sectionY <= 5050) continue;
                    environments.add(new EnvironmentDescription(EnvID.BrickDefault, new Vector2(4550, sectionY), 0f));
                }

                for (int sectionX = 4650; sectionX <= 6450; sectionX += 100) {
                    if (sectionX >= 5350 && sectionX <= 6050) continue;
                    environments.add(new EnvironmentDescription(EnvID.BrickDefault, new Vector2(sectionX, 3950), 0f));
                }

                for (int sectionY = 3050; sectionY <= 3850; sectionY += 100) {
                    environments.add(new EnvironmentDescription(EnvID.BrickDefault, new Vector2(4850, sectionY), 0f));
                }

                for (int sectionX = 4950; sectionX <= 6450; sectionX += 100) {
                    if (sectionX >= 5350 && sectionX <= 6050) continue;
                    environments.add(new EnvironmentDescription(EnvID.BrickDefault, new Vector2(sectionX, 3050), 0f));
                }

                return proto;
            }
            default -> throw new RuntimeException("Tried to get a map that does not exist! Map type: " + map.toString());
        }
    }
}
