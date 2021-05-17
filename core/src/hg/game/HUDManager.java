package hg.game;

import com.badlogic.gdx.graphics.Texture;
import hg.drawables.BasicSprite;
import hg.drawables.BasicText;
import hg.drawables.DrawLayer;
import hg.drawables.FillBar;
import hg.engine.AssetEngine;
import hg.entities.PlayerEntity;
import hg.enums.HPos;
import hg.enums.VPos;
import hg.gamelogic.BaseStats;
import hg.interfaces.IWeapon;
import hg.libraries.BuilderLibrary;
import hg.enums.types.WeaponType;
import hg.utils.builders.BasicSpriteBuilder;
import hg.utils.builders.BasicTextBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class HUDManager {
    private static class WeaponInfo {
        public BasicSprite display;
        public BasicText ammo;
    }

    private BasicSprite HPBox;
    private FillBar HPBar;

    private BasicSprite APBox;
    private FillBar APBar;

    private BasicSprite KevlarIcon;
    private BasicSpriteBuilder plateBuilder;
    private final ArrayList<BasicSprite> plates = new ArrayList<>();
    private final HashMap<Integer, WeaponInfo> weaponList = new HashMap<>();

    private BasicSpriteBuilder weaponBuilder;
    private BasicTextBuilder ammoBuilder;

    public HUDManager() {
        int Width = HgGame.WorldWidth;
        int Height = HgGame.WorldHeight;

        int XOffset = -HgGame.WorldWidth / 2;
        int YOffset = -HgGame.WorldHeight / 2;
        AssetEngine assets = HgGame.Assets();

        Texture HPSlice = assets.loadTexture("Assets/GUI/HPSlice.png");
        Texture APSlice = assets.loadTexture("Assets/GUI/APSlice.png");

        weaponBuilder = new BasicSpriteBuilder().centerToW(true).makeGUI();
        ammoBuilder = BuilderLibrary.BasicTextBuilders("smalllabel").textPos(HPos.Left, VPos.Center).makeGUI();

        HPBox = new BasicSpriteBuilder().texture(assets.loadTexture("Assets/GUI/HPBox.png")).centerToNW(true).position(XOffset + 120, YOffset + Height - 50).makeGUI().build();
        HPBox.setLayer(HPBox.getLayer());
        APBox = new BasicSpriteBuilder().texture(assets.loadTexture("Assets/GUI/APBox.png")).centerToNW(true).position(XOffset + 190, YOffset + Height - 80).makeGUI().build();
        APBox.setLayer(HPBox.getLayer() + 2);
        KevlarIcon = new BasicSpriteBuilder().texture(assets.loadTexture("Assets/GUI/KevlarVestIcon.png")).centerToRegion(true).position(XOffset + 90, YOffset + Height - 79).makeGUI().build();
        KevlarIcon.setLayer(KevlarIcon.getLayer() + 1);
        plateBuilder = new BasicSpriteBuilder().texture(assets.loadTexture("Assets/GUI/PlateIcon.png")).centerToNW(true).makeGUI();

        HPBar = new FillBar(HPSlice, 100, 400);
        HPBar.getPosition().set(XOffset + 120 + 4, YOffset + Height - 50 - 4);
        HPBar.getCenterOffset().set(0, HPSlice.getHeight());
        HPBar.setCameraUse(false);
        HPBar.setLayer(DrawLayer.GUIDefault + 1);
        HPBar.registerToEngine();

        APBar = new FillBar(APSlice, 100, 300);
        APBar.getPosition().set(XOffset + 190 + 4, YOffset + Height - 80 - 4);
        APBar.getCenterOffset().set(0, APSlice.getHeight());
        APBar.setCameraUse(false);
        APBar.setLayer(DrawLayer.GUIDefault + 3);
        APBar.registerToEngine();
    }

    private void updatePlates() {
        GameManager manager = HgGame.Manager();
        PlayerEntity localPlayer = manager.localView != null ? manager.localView.playerEntity : null;
        int currentPlates = localPlayer == null ? 0 : localPlayer.getStats().armorPlates;

        while (currentPlates < plates.size()) {
            BasicSprite plate = plates.remove(plates.size() - 1);
            plate.unregisterFromEngine();
        }

        int Width = HgGame.WorldWidth;
        int Height = HgGame.WorldHeight;

        int XOffset = -HgGame.WorldWidth / 2;
        int YOffset = -HgGame.WorldHeight / 2;

        while (currentPlates > plates.size()) {
            plates.add(plateBuilder.copy().position(XOffset + 540 + 22 * plates.size(), YOffset + Height - 50).build());
        }
    }

    public void updateWeapons() {
        // TODO Improve this
        GameManager manager = HgGame.Manager();
        PlayerEntity localPlayer = manager.localView != null ? manager.localView.playerEntity : null;
        HashMap<Integer, IWeapon> playerWeapons = localPlayer != null ? localPlayer.viewWeapons() : new HashMap<>();

        // Add obtained weapons
        for (var owned: playerWeapons.entrySet()) {
            if (!weaponList.containsKey(owned.getKey())) {
                IWeapon weapon = owned.getValue();

                WeaponInfo info = new WeaponInfo();
                info.ammo = ammoBuilder.build();

                String which = weapon.getWeaponDisplay();
                if (which == null) which = "Assets/Sprites/Pickups/RifleSilhouette.png";

                info.display = weaponBuilder.texture(HgGame.Assets().loadTexture(which)).build();
                info.ammo.registerToEngine();
                info.display.registerToEngine();
                weaponList.put(owned.getKey(), info);
            }
        }

        // Remove lost weapons
        for (var displayed: new HashSet<>(weaponList.keySet())) {
            if (!playerWeapons.containsKey(displayed)) {
                WeaponInfo info = weaponList.remove(displayed);
                info.display.unregisterFromEngine();
                info.ammo.unregisterFromEngine();
            }
        }

        // Update ammo
        for (var displayed: weaponList.entrySet()) {
            IWeapon weapon = playerWeapons.get(displayed.getKey());
            displayed.getValue().ammo.setText(weapon.getAmmoDisplay());
        }

        // Update positions
        float currentY = 60 - HgGame.WorldHeight / 2f;
        float currentX = 35 - HgGame.WorldWidth / 2f;
        int preferredWeapon = localPlayer != null ? localPlayer.viewSelectedWeapon() : WeaponType.Revolver;
        boolean gotPreferred = false;
        for (var displayed: weaponList.entrySet()) {
            if (displayed.getKey() == preferredWeapon) {
                WeaponInfo info = displayed.getValue();
                float displacement = info.display.getRegion().height / 2f + 10;

                updateInfo(info, currentX + 50, currentY);
                currentY += displacement;
                gotPreferred = true;
                break;
            }
        }

        for (var displayed: weaponList.entrySet()) {
            WeaponInfo info = displayed.getValue();
            if (gotPreferred && displayed.getKey() == preferredWeapon) continue;
            float displacement = info.display.getRegion().height / 2f + 20;

            currentY += displacement;
            updateInfo(info, currentX, currentY);
            currentY += displacement;
        }
    }

    private void updateInfo(WeaponInfo info, float currentX, float currentY) {
        float textDistance = info.display.getRegion().width + 20f;
        info.display.getPosition().set(currentX, currentY);
        info.ammo.getPosition().set(currentX + textDistance, currentY);
    }

    public void update() {
        GameManager manager = HgGame.Manager();
        PlayerEntity localPlayer = manager.localView != null ? manager.localView.playerEntity : null;

        updatePlates();

        if (localPlayer == null) {
            HPBar.setEnabled(false);
            HPBox.setEnabled(false);
            APBar.setEnabled(false);
            APBox.setEnabled(false);
            KevlarIcon.setEnabled(false);
        }
        else {
            BaseStats stats = localPlayer.getStats();
            HPBox.setEnabled(true);
            HPBar.setEnabled(true);
            HPBar.setProperties(stats.maxHealth, 400);
            HPBar.updateFill(stats.health);

            if (stats.heavyArmor > 0f) {
                APBox.setEnabled(true);
                APBar.setEnabled(true);
                APBar.setProperties(stats.maxHeavyArmor, 300);
                APBar.updateFill(stats.heavyArmor);
            }
            else {
                APBox.setEnabled(false);
                APBar.setEnabled(false);
            }

            KevlarIcon.setEnabled(stats.hasKevlarVest);
        }

        updateWeapons();
    }

    public void cleanup() {
        HPBar.unregisterFromEngine();
        HPBox.unregisterFromEngine();
        APBar.unregisterFromEngine();
        APBox.unregisterFromEngine();
        KevlarIcon.unregisterFromEngine();
        for (var plate: plates) plate.unregisterFromEngine();
    }
}
