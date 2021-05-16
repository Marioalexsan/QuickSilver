package hg.libraries;

import com.badlogic.gdx.math.Vector2;
import hg.types.ActorType;
import hg.types.EnvType;
import hg.maps.Description;
import hg.maps.MapPrototype;
import hg.types.MapType;

// TODO Make a Tiler object to deal with the axis-aligned environments
// TODO Rename rooms

public class MapLibrary {
    public static MapPrototype CreatePrototype(int map) {
        switch (map) {
            case MapType.TestArea01 -> {
                MapPrototype proto = new MapPrototype();

                var randoms = proto.randomSpawnpoints;

                randoms.add(new Vector2(1650, 4600));
                randoms.add(new Vector2(260, 4250));
                randoms.add(new Vector2(250, 2280));
                randoms.add(new Vector2(1260, 210));
                randoms.add(new Vector2(2370, 210));
                randoms.add(new Vector2(4920, 1890));
                randoms.add(new Vector2(6340, 200));
                randoms.add(new Vector2(6370, 2860));
                randoms.add(new Vector2(4370, 5360));
                randoms.add(new Vector2(2030, 5350));

                var onLoadActors = proto.onLoadActors;

                // Grind center spawners
                onLoadActors.add(new Description(ActorType.HeavyArmorSpawner, new Vector2(3300, 3000), 0f));

                // Sniper hallway spawners
                onLoadActors.add(new Description(ActorType.AmmoPackSpawner, new Vector2(6200, 1950), 0f));
                onLoadActors.add(new Description(ActorType.AmmoPackSpawner, new Vector2(6350, 1950), 0f));

                onLoadActors.add(new Description(ActorType.AmmoPackSpawner, new Vector2(5460, 1400), 0f));
                onLoadActors.add(new Description(ActorType.AmmoPackSpawner, new Vector2(3500, 150), 0f));
                onLoadActors.add(new Description(ActorType.AmmoPackSpawner, new Vector2(3600, 150), 0f));
                onLoadActors.add(new Description(ActorType.MedkitSpawner, new Vector2(3750, 150), 0f));
                onLoadActors.add(new Description(ActorType.MedkitSpawner, new Vector2(3850, 150), 0f));

                onLoadActors.add(new Description(ActorType.KevlarVestSpawner, new Vector2(3500, 900), 0f));


                // Backroom spawns
                onLoadActors.add(new Description(ActorType.AssaultRifleSpawner, new Vector2(250, 5450), 0f));
                onLoadActors.add(new Description(ActorType.AmmoPackSpawner, new Vector2(450, 5450), 0f));
                onLoadActors.add(new Description(ActorType.AmmoPackSpawner, new Vector2(600, 5450), 0f));
                onLoadActors.add(new Description(ActorType.AmmoPackSpawner, new Vector2(750, 5450), 0f));


                // Storage spawns
                onLoadActors.add(new Description(ActorType.AssaultRifleSpawner, new Vector2(4700, 4050), 0f));

                onLoadActors.add(new Description(ActorType.MedkitSpawner, new Vector2(6300, 4050), 0f));
                onLoadActors.add(new Description(ActorType.MedkitSpawner, new Vector2(6400, 4050), 0f));

                onLoadActors.add(new Description(ActorType.ArmorPlateSpawner, new Vector2(5350, 4650), 0f));
                onLoadActors.add(new Description(ActorType.ArmorPlateSpawner, new Vector2(5450, 4650), 0f));
                onLoadActors.add(new Description(ActorType.ArmorPlateSpawner, new Vector2(5400, 4720), 0f));


                // Bottom room spawns, dunno
                onLoadActors.add(new Description(ActorType.AmmoPackSpawner, new Vector2(2000, 950), 0f));
                onLoadActors.add(new Description(ActorType.AssaultRifleSpawner, new Vector2(1800, 950), 0f));
                onLoadActors.add(new Description(ActorType.MedkitSpawner, new Vector2(1600, 950), 0f));

                onLoadActors.add(new Description(ActorType.ArmorPlateSpawner, new Vector2(1800, 150), 0f));
                onLoadActors.add(new Description(ActorType.AmmoPackSpawner, new Vector2(1890, 150), 0f));
                onLoadActors.add(new Description(ActorType.ArmorPlateSpawner, new Vector2(1980, 150), 0f));

                var environments = proto.environments;

                // === Level Boundary Walls and Flooring ===

                for (int sectionY = 50; sectionY <= 5550; sectionY += 100)
                    environments.add(new Description(EnvType.BrickDefault, new Vector2(50, sectionY), 0f));

                for (int sectionY = 50; sectionY <= 5550; sectionY += 100)
                    environments.add(new Description(EnvType.BrickDefault, new Vector2(6550, sectionY), 0f));

                for (int sectionX = 150; sectionX <= 6550; sectionX += 100) {
                    environments.add(new Description(EnvType.BrickDefault, new Vector2(sectionX, 50), 0f));
                    environments.add(new Description(EnvType.BrickDefault, new Vector2(sectionX, 5550), 0f));
                }

                // * Big Floor

                for (int sectionX = 150; sectionX <= 6450; sectionX += 100) {
                    for (int sectionY = 150; sectionY <= 5450; sectionY += 100) {
                        environments.add(new Description(EnvType.ConcreteFloor, new Vector2(sectionX, sectionY), 0f));
                    }
                }

                // === "Corner Room" ===

                for (int sectionX = 150; sectionX <= 1450; sectionX += 100) {
                    if (sectionX >= 650 && sectionX <= 850) continue;
                    environments.add(new Description(EnvType.BrickDefault, new Vector2(sectionX, 2450), 0f));
                }

                for (int sectionY = 150; sectionY <= 2350; sectionY += 100) {
                    if (sectionY >= 450 && sectionY <= 650) continue;
                    environments.add(new Description(EnvType.BrickDefault, new Vector2(1450, sectionY), 0f));
                }

                // === "Peekers" && "Sniper Hallway" ===

                for (int sectionX = 1550; sectionX <= 4750; sectionX += 100) {
                    if (sectionX >= 2250 && sectionX <= 2450) continue;
                    environments.add(new Description(EnvType.BrickDefault, new Vector2(sectionX, 1050), 0f));
                }

                for (int sectionY = 1150; sectionY <= 1950; sectionY += 100) {
                    environments.add(new Description(EnvType.BrickDefault, new Vector2(4750, sectionY), 0f));
                }

                for (int sectionX = 4750; sectionX <= 6550; sectionX += 100) {
                    if (sectionX >= 5750 && sectionX <= 6050) continue;
                    environments.add(new Description(EnvType.BrickDefault, new Vector2(sectionX, 2050), 0f));
                }

                // * Metal Bars
                for (int sectionY = 150; sectionY <= 950; sectionY += 100) {
                    environments.add(new Description(EnvType.BrickMetalBars, new Vector2(3350, sectionY), 90f));
                }
                
                // * Peekers Boxes Up
                environments.add(new Description(EnvType.BoxMedium, new Vector2(2150, 940), 0f));
                environments.add(new Description(EnvType.BoxMedium, new Vector2(2135, 825), -15f));
                environments.add(new Description(EnvType.BoxMedium, new Vector2(2075, 730), -30f));

                // * Peekers Boxes Down
                environments.add(new Description(EnvType.BoxMedium, new Vector2(2180, 170), 0f));
                environments.add(new Description(EnvType.BoxMedium, new Vector2(2200, 285), -15f));
                environments.add(new Description(EnvType.BoxMedium, new Vector2(2230, 400), -30f));

                // * Peekers Boxes Right
                environments.add(new Description(EnvType.BoxMedium, new Vector2(2730, 410), 0));
                environments.add(new Description(EnvType.BoxMedium, new Vector2(2750, 515), 0));

                environments.add(new Description(EnvType.BoxMedium, new Vector2(2790, 720), 0));
                environments.add(new Description(EnvType.BoxMedium, new Vector2(2840, 810), 0));

                environments.add(new Description(EnvType.BoxMedium, new Vector2(2985, 530), 0));
                environments.add(new Description(EnvType.BoxMedium, new Vector2(3000, 620), 0));


                // * Sniper Hallway Stuff
                environments.add(new Description(EnvType.BoxMedium, new Vector2(4850, 1050), 0f));
                environments.add(new Description(EnvType.BoxMedium, new Vector2(4950, 1050), 0f));
                environments.add(new Description(EnvType.BoxMedium, new Vector2(5090, 1060), 45f));
                environments.add(new Description(EnvType.BoxMedium, new Vector2(5190, 1125), 45f));
                environments.add(new Description(EnvType.BoxMedium, new Vector2(5245, 1245), 0f));
                environments.add(new Description(EnvType.BoxMedium, new Vector2(5245, 1330), 0f));
                environments.add(new Description(EnvType.BoxMedium, new Vector2(5240, 1445), 0f));
                environments.add(new Description(EnvType.BoxMedium, new Vector2(5345, 1255), 0f));
                environments.add(new Description(EnvType.BoxMedium, new Vector2(5345, 1340), 0f));
                environments.add(new Description(EnvType.BoxMedium, new Vector2(5340, 1435), 0f));
                environments.add(new Description(EnvType.BrickPillarBig, new Vector2(5230, 1625), 0f));

                // === "Side Hallway" ===

                for (int sectionY = 2550; sectionY <= 4350; sectionY += 100) {
                    if (sectionY >= 3350 && sectionY <= 3550) continue;
                    environments.add(new Description(EnvType.BrickDefault, new Vector2(1150, sectionY), 0f));
                }

                // === "Back Room", "Lookers" ===

                for (int sectionY = 4450; sectionY <= 5450; sectionY += 100) {
                    environments.add(new Description(EnvType.BrickDefault, new Vector2(1850, sectionY), 0f));
                }

                for (int sectionX = 150; sectionX <= 4550; sectionX += 100) {
                    if (sectionX >= 650 && sectionX <= 850) continue;
                    if (sectionX >= 2650 && sectionX <= 2850) continue;
                    environments.add(new Description(EnvType.BrickDefault, new Vector2(sectionX, 4450), 0f));
                }

                // === "Storage" ===

                for (int sectionY = 3950; sectionY <= 5450; sectionY += 100) {
                    if (sectionY >= 4850 && sectionY <= 5050) continue;
                    environments.add(new Description(EnvType.BrickDefault, new Vector2(4550, sectionY), 0f));
                }

                for (int sectionX = 4650; sectionX <= 6450; sectionX += 100) {
                    if (sectionX >= 5350 && sectionX <= 6050) continue;
                    environments.add(new Description(EnvType.BrickDefault, new Vector2(sectionX, 3950), 0f));
                }

                for (int sectionY = 3050; sectionY <= 3850; sectionY += 100) {
                    environments.add(new Description(EnvType.BrickDefault, new Vector2(4850, sectionY), 0f));
                }

                for (int sectionX = 4950; sectionX <= 6450; sectionX += 100) {
                    if (sectionX >= 5350 && sectionX <= 6050) continue;
                    environments.add(new Description(EnvType.BrickDefault, new Vector2(sectionX, 3050), 0f));
                }

                return proto;
            }
            default -> throw new RuntimeException("Tried to get a map that does not exist! Map type: " + map);
        }
    }
}
