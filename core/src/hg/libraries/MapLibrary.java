package hg.libraries;

import com.badlogic.gdx.math.Vector2;
import hg.enums.ActorType;
import hg.enums.EnvType;
import hg.maps.Description;
import hg.maps.MapPrototype;
import hg.enums.MapType;

// TODO Make a Tiler object to deal with the axis-aligned environments
// TODO Rename rooms

/** Holds maps used in the game. */
public class MapLibrary {
    public static MapPrototype CreatePrototype(int map) {
        switch (map) {
            case MapType.Grinder -> {
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

                onLoadActors.add(new Description(ActorType.ArmorPlateSpawner, new Vector2(1270, 2300), 0f));
                onLoadActors.add(new Description(ActorType.AmmoPackSpawner, new Vector2(1170, 2300), 0f));
                onLoadActors.add(new Description(ActorType.ArmorPlateSpawner, new Vector2(1070, 2300), 0f));


                onLoadActors.add(new Description(ActorType.AssaultRifleSpawner, new Vector2(5000, 1250), 0f));

                onLoadActors.add(new Description(ActorType.AmmoPackSpawner, new Vector2(3940, 3750), 90f));
                onLoadActors.add(new Description(ActorType.AmmoPackSpawner, new Vector2(4030, 3750), 90f));
                onLoadActors.add(new Description(ActorType.AmmoPackSpawner, new Vector2(4120, 3750), 90f));

                onLoadActors.add(new Description(ActorType.KevlarVestSpawner, new Vector2(3910, 4640), 90f));

                onLoadActors.add(new Description(ActorType.ArmorPlateSpawner, new Vector2(5860, 400), 90f));

                onLoadActors.add(new Description(ActorType.ArmorPlateSpawner, new Vector2(185, 2220), 90f));
                onLoadActors.add(new Description(ActorType.MedkitSpawner, new Vector2(185, 2050), 90f));
                onLoadActors.add(new Description(ActorType.AmmoPackSpawner, new Vector2(185, 1900), 90f));
                onLoadActors.add(new Description(ActorType.MedkitSpawner, new Vector2(185, 1750), 90f));
                onLoadActors.add(new Description(ActorType.ArmorPlateSpawner, new Vector2(185, 1600), 90f));

                // Center spawners
                onLoadActors.add(new Description(ActorType.HeavyArmorSpawner, new Vector2(3300, 3000), 0f));
                onLoadActors.add(new Description(ActorType.DBShotgunSpawner, new Vector2(3100, 2700), 0f));
                onLoadActors.add(new Description(ActorType.MedkitSpawner, new Vector2(3535, 2555), 0f));
                onLoadActors.add(new Description(ActorType.MedkitSpawner, new Vector2(3640, 2740), 0f));
                onLoadActors.add(new Description(ActorType.MedkitSpawner, new Vector2(3300, 2510), 0f));
                onLoadActors.add(new Description(ActorType.DBShotgunSpawner, new Vector2(3100, 2700), 0f));
                onLoadActors.add(new Description(ActorType.DBShotgunSpawner, new Vector2(3100, 2700), 0f));


                onLoadActors.add(new Description(ActorType.MedkitSpawner, new Vector2(4250, 1150), 0f));
                onLoadActors.add(new Description(ActorType.MedkitSpawner, new Vector2(4350, 1150), 0f));
                onLoadActors.add(new Description(ActorType.AmmoPackSpawner, new Vector2(3600, 1150), 0f));
                onLoadActors.add(new Description(ActorType.AmmoPackSpawner, new Vector2(3500, 1150), 0f));

                onLoadActors.add(new Description(ActorType.AmmoPackSpawner, new Vector2(1310, 2600), 0f));
                onLoadActors.add(new Description(ActorType.AmmoPackSpawner, new Vector2(1410, 2600), 0f));
                onLoadActors.add(new Description(ActorType.ArmorPlateSpawner, new Vector2(1360, 2670), 0f));


                onLoadActors.add(new Description(ActorType.AmmoPackSpawner, new Vector2(1460, 4350), 0f));
                onLoadActors.add(new Description(ActorType.AmmoPackSpawner, new Vector2(1560, 4350), 0f));
                onLoadActors.add(new Description(ActorType.AmmoPackSpawner, new Vector2(1660, 4350), 0f));

                onLoadActors.add(new Description(ActorType.DBShotgunSpawner, new Vector2(6320, 2780), 0f));

                onLoadActors.add(new Description(ActorType.DBShotgunSpawner, new Vector2(4320, 5360), 90f));


                // Sniper Hallway spawners
                onLoadActors.add(new Description(ActorType.AmmoPackSpawner, new Vector2(6200, 1950), 0f));
                onLoadActors.add(new Description(ActorType.AmmoPackSpawner, new Vector2(6350, 1950), 0f));

                onLoadActors.add(new Description(ActorType.AmmoPackSpawner, new Vector2(5460, 1400), 0f));
                onLoadActors.add(new Description(ActorType.AmmoPackSpawner, new Vector2(3500, 150), 0f));
                onLoadActors.add(new Description(ActorType.AmmoPackSpawner, new Vector2(3600, 150), 0f));
                onLoadActors.add(new Description(ActorType.MedkitSpawner, new Vector2(3750, 150), 0f));
                onLoadActors.add(new Description(ActorType.MedkitSpawner, new Vector2(3850, 150), 0f));

                onLoadActors.add(new Description(ActorType.KevlarVestSpawner, new Vector2(3500, 900), 0f));

                // Backroom spawners
                onLoadActors.add(new Description(ActorType.AssaultRifleSpawner, new Vector2(250, 5450), 0f));
                onLoadActors.add(new Description(ActorType.AmmoPackSpawner, new Vector2(450, 5450), 0f));
                onLoadActors.add(new Description(ActorType.AmmoPackSpawner, new Vector2(600, 5450), 0f));
                onLoadActors.add(new Description(ActorType.AmmoPackSpawner, new Vector2(750, 5450), 0f));

                onLoadActors.add(new Description(ActorType.KevlarVestSpawner, new Vector2(1650, 5400), 0f));
                onLoadActors.add(new Description(ActorType.ArmorPlateSpawner, new Vector2(1340, 4550), 0f));
                onLoadActors.add(new Description(ActorType.ArmorPlateSpawner, new Vector2(1440, 4550), 0f));
                onLoadActors.add(new Description(ActorType.ArmorPlateSpawner, new Vector2(1540, 4550), 0f));
                onLoadActors.add(new Description(ActorType.MedkitSpawner, new Vector2(200, 4550), 0f));

                // Storage spawners
                onLoadActors.add(new Description(ActorType.AssaultRifleSpawner, new Vector2(4700, 4050), 0f));

                onLoadActors.add(new Description(ActorType.MedkitSpawner, new Vector2(6300, 4050), 0f));
                onLoadActors.add(new Description(ActorType.MedkitSpawner, new Vector2(6400, 4050), 0f));

                onLoadActors.add(new Description(ActorType.ArmorPlateSpawner, new Vector2(5350, 4650), 0f));
                onLoadActors.add(new Description(ActorType.ArmorPlateSpawner, new Vector2(5450, 4650), 0f));
                onLoadActors.add(new Description(ActorType.ArmorPlateSpawner, new Vector2(5400, 4720), 0f));

                // Peekers spawners
                onLoadActors.add(new Description(ActorType.AmmoPackSpawner, new Vector2(2000, 950), 0f));
                onLoadActors.add(new Description(ActorType.AssaultRifleSpawner, new Vector2(1800, 950), 0f));
                onLoadActors.add(new Description(ActorType.MedkitSpawner, new Vector2(1600, 950), 0f));

                onLoadActors.add(new Description(ActorType.ArmorPlateSpawner, new Vector2(1800, 150), 0f));
                onLoadActors.add(new Description(ActorType.AmmoPackSpawner, new Vector2(1890, 150), 0f));
                onLoadActors.add(new Description(ActorType.ArmorPlateSpawner, new Vector2(1980, 150), 0f));


                // === Level Boundary Walls and Flooring ===



                var environments = proto.environments;

                // Center envs

                environments.add(new Description(EnvType.BrickPillarBig, new Vector2(2350, 1900), 0f));
                environments.add(new Description(EnvType.BrickPillarBig, new Vector2(3400, 2730), 0f));
                environments.add(new Description(EnvType.BrickPillarBig, new Vector2(2360, 3600), 0f));
                environments.add(new Description(EnvType.BrickPillarBig, new Vector2(3760, 3760), 0f));
                environments.add(new Description(EnvType.BrickPillarBig, new Vector2(3900, 1770), 0f));

                environments.add(new Description(EnvType.BoxMedium, new Vector2(3920, 3600), 0f));
                environments.add(new Description(EnvType.BoxMedium, new Vector2(4020, 3600), 0f));
                environments.add(new Description(EnvType.BoxMedium, new Vector2(4120, 3600), 0f));
                environments.add(new Description(EnvType.BoxMedium, new Vector2(4220, 3600), 0f));
                environments.add(new Description(EnvType.BoxMedium, new Vector2(4170, 3500), 0f));
                environments.add(new Description(EnvType.BoxMedium, new Vector2(4170, 3400), 0f));
                environments.add(new Description(EnvType.BoxMedium, new Vector2(3580, 3900), 0f));
                environments.add(new Description(EnvType.BoxMedium, new Vector2(3590, 4000), 0f));
                environments.add(new Description(EnvType.BoxMedium, new Vector2(3600, 4100), 0f));

                environments.add(new Description(EnvType.BoxMedium, new Vector2(5100, 2600), 0f));
                environments.add(new Description(EnvType.BoxMedium, new Vector2(5200, 2600), 0f));
                environments.add(new Description(EnvType.BoxMedium, new Vector2(5150, 2690), 0f));

                environments.add(new Description(EnvType.BoxMedium, new Vector2(6200, 2600), 0f));
                environments.add(new Description(EnvType.BoxMedium, new Vector2(6300, 2600), 0f));
                environments.add(new Description(EnvType.BoxMedium, new Vector2(6130, 2690), 0f));

                environments.add(new Description(EnvType.BoxMedium, new Vector2(6400, 3790), 0f));
                environments.add(new Description(EnvType.BoxMedium, new Vector2(6400, 3690), 0f));
                environments.add(new Description(EnvType.BoxMedium, new Vector2(6310, 3810), 0f));


                environments.add(new Description(EnvType.BoxMedium, new Vector2(5890, 550), 0f));
                environments.add(new Description(EnvType.BoxMedium, new Vector2(5800, 540), 0f));
                environments.add(new Description(EnvType.BoxMedium, new Vector2(5710, 520), 0f));


                environments.add(new Description(EnvType.BoxMedium, new Vector2(3700, 4590), 0f));
                environments.add(new Description(EnvType.BoxMedium, new Vector2(3715, 4685), -15f));
                environments.add(new Description(EnvType.BoxMedium, new Vector2(3735, 4780), -30f));

                environments.add(new Description(EnvType.BoxMedium, new Vector2(590, 3770), 0f));
                environments.add(new Description(EnvType.BoxMedium, new Vector2(590, 3680), 0f));
                environments.add(new Description(EnvType.BoxMedium, new Vector2(590, 3590), 0f));
                environments.add(new Description(EnvType.BoxMedium, new Vector2(590, 3500), 0f));
                environments.add(new Description(EnvType.BoxMedium, new Vector2(620, 3200), 0f));


                environments.add(new Description(EnvType.BoxMedium, new Vector2(950, 5010), -42f));
                environments.add(new Description(EnvType.BoxMedium, new Vector2(750, 4920), -67f));
                environments.add(new Description(EnvType.BoxMedium, new Vector2(850, 5200), -116f));

                environments.add(new Description(EnvType.BoxMedium, new Vector2(2020, 2830), 45f));
                environments.add(new Description(EnvType.BoxMedium, new Vector2(2090, 2900), 45f));
                environments.add(new Description(EnvType.BoxMedium, new Vector2(2160, 2970), 45f));

                environments.add(new Description(EnvType.BoxMedium, new Vector2(2580, 1900), 0f));
                environments.add(new Description(EnvType.BoxMedium, new Vector2(2680, 1840), 45f));
                environments.add(new Description(EnvType.BoxMedium, new Vector2(2740, 1720), 90f));


                // Outside walls

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
                        environments.add(new Description(EnvType.ConcreteFloorTwo, new Vector2(sectionX, sectionY), 0f));
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
            case MapType.Duel -> {
                MapPrototype proto = new MapPrototype();

                var randoms = proto.randomSpawnpoints;

                randoms.add(new Vector2(150, 150));
                randoms.add(new Vector2(150, 1850));
                randoms.add(new Vector2(1850, 150));
                randoms.add(new Vector2(1850, 1850));

                var onLoadActors = proto.onLoadActors;

                onLoadActors.add(new Description(ActorType.AmmoPackSpawner, new Vector2(150, 250), 0).setInts(180));
                onLoadActors.add(new Description(ActorType.AmmoPackSpawner, new Vector2(250, 150), 0).setInts(180));

                onLoadActors.add(new Description(ActorType.AmmoPackSpawner, new Vector2(1850, 1750), 0).setInts(180));
                onLoadActors.add(new Description(ActorType.AmmoPackSpawner, new Vector2(1750, 1850), 0).setInts(180));

                onLoadActors.add(new Description(ActorType.AmmoPackSpawner, new Vector2(1850, 250), 0).setInts(180));
                onLoadActors.add(new Description(ActorType.AmmoPackSpawner, new Vector2(1750, 150), 0).setInts(180));

                onLoadActors.add(new Description(ActorType.AmmoPackSpawner, new Vector2(150, 1750), 0).setInts(180));
                onLoadActors.add(new Description(ActorType.AmmoPackSpawner, new Vector2(250, 1850), 0).setInts(180));

                onLoadActors.add(new Description(ActorType.ArmorPlateSpawner, new Vector2(750, 1000), 0).setInts(120));
                onLoadActors.add(new Description(ActorType.ArmorPlateSpawner, new Vector2(1250, 1000), 0).setInts(120));
                onLoadActors.add(new Description(ActorType.ArmorPlateSpawner, new Vector2(1000, 750), 0).setInts(120));
                onLoadActors.add(new Description(ActorType.ArmorPlateSpawner, new Vector2(1000, 1250), 0).setInts(120));

                onLoadActors.add(new Description(ActorType.DBShotgunSpawner, new Vector2(350, 350), -45).setInts(300));
                onLoadActors.add(new Description(ActorType.DBShotgunSpawner, new Vector2(350, 1650), -135).setInts(300));
                onLoadActors.add(new Description(ActorType.DBShotgunSpawner, new Vector2(1650, 1650), 135).setInts(300));
                onLoadActors.add(new Description(ActorType.DBShotgunSpawner, new Vector2(1650, 350), 45).setInts(300));

                var environments = proto.environments;

                environments.add(new Description(EnvType.BoxMedium, new Vector2(400, 400), 45f));
                environments.add(new Description(EnvType.BoxMedium, new Vector2(490, 350), -22.5f));
                environments.add(new Description(EnvType.BoxMedium, new Vector2(350, 490), 22.5f));
                environments.add(new Description(EnvType.BoxMedium, new Vector2(590, 330), 0f));
                environments.add(new Description(EnvType.BoxMedium, new Vector2(330, 590), 0f));

                environments.add(new Description(EnvType.BoxMedium, new Vector2(1600, 1600), 45f));
                environments.add(new Description(EnvType.BoxMedium, new Vector2(1510, 1650), -22.5f));
                environments.add(new Description(EnvType.BoxMedium, new Vector2(1650, 1510), 22.5f));
                environments.add(new Description(EnvType.BoxMedium, new Vector2(1410, 1670), 0f));
                environments.add(new Description(EnvType.BoxMedium, new Vector2(1670, 1410), 0f));

                environments.add(new Description(EnvType.BoxMedium, new Vector2(400, 1600), 45f));
                environments.add(new Description(EnvType.BoxMedium, new Vector2(490, 1650), 22.5f));
                environments.add(new Description(EnvType.BoxMedium, new Vector2(350, 1510), -22.5f));
                environments.add(new Description(EnvType.BoxMedium, new Vector2(590, 1670), 0f));
                environments.add(new Description(EnvType.BoxMedium, new Vector2(330, 1410), 0f));

                environments.add(new Description(EnvType.BoxMedium, new Vector2(1600, 400), 45f));
                environments.add(new Description(EnvType.BoxMedium, new Vector2(1510, 350), 22.5f));
                environments.add(new Description(EnvType.BoxMedium, new Vector2(1650, 490), -22.5f));
                environments.add(new Description(EnvType.BoxMedium, new Vector2(1410, 330), 0f));
                environments.add(new Description(EnvType.BoxMedium, new Vector2(1670, 590), 0f));

                environments.add(new Description(EnvType.BrickPillarBig, new Vector2(1000, 1000), 0f));

                environments.add(new Description(EnvType.BoxMedium, new Vector2(1550, 1050), 0f));
                environments.add(new Description(EnvType.BoxMedium, new Vector2(1450, 1000), 0f));
                environments.add(new Description(EnvType.BoxMedium, new Vector2(1050, 1550), 0f));
                environments.add(new Description(EnvType.BoxMedium, new Vector2(1000, 1450), 0f));
                environments.add(new Description(EnvType.BoxMedium, new Vector2(550, 1050), 0f));
                environments.add(new Description(EnvType.BoxMedium, new Vector2(450, 1000), 0f));
                environments.add(new Description(EnvType.BoxMedium, new Vector2(1050, 550), 0f));
                environments.add(new Description(EnvType.BoxMedium, new Vector2(1000, 450), 0f));


                for (int sectionX = 50; sectionX <= 1950; sectionX += 100) {
                    environments.add(new Description(EnvType.BrickDefault, new Vector2(sectionX, 50), 0f));
                    environments.add(new Description(EnvType.BrickDefault, new Vector2(sectionX, 1950), 0f));
                }

                for (int sectionY = 150; sectionY <= 1850; sectionY += 100) {
                    environments.add(new Description(EnvType.BrickDefault, new Vector2(50, sectionY), 0f));
                    environments.add(new Description(EnvType.BrickDefault, new Vector2(1950, sectionY), 0f));
                }

                for (int sectionX = 150; sectionX <= 1850; sectionX += 100) {
                    for (int sectionY = 150; sectionY <= 1850; sectionY += 100) {
                        environments.add(new Description(EnvType.ConcreteFloorTwo, new Vector2(sectionX, sectionY), 0f));
                    }
                }

                return proto;
            }
            default -> throw new RuntimeException("Tried to get a map that does not exist! Map type: " + map);
        }
    }
}
