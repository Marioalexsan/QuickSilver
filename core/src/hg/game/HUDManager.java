package hg.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import hg.drawables.BasicSprite;
import hg.drawables.BasicText;
import hg.drawables.DrawLayer;
import hg.drawables.ValueBar;
import hg.engine.AssetEngine;
import hg.entities.PlayerEntity;
import hg.enums.HPos;
import hg.enums.VPos;
import hg.gamelogic.BaseStats;
import hg.interfaces.IWeapon;
import hg.libraries.BuilderLibrary;
import hg.enums.WeaponType;
import hg.utils.builders.BasicSpriteBuilder;
import hg.utils.builders.BasicTextBuilder;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/** HUDManager manages the in-game Heads-Up Display */
public class HUDManager {
    private static class WeaponInfo {
        public BasicSprite display;
        public BasicText ammo;
    }

    private boolean inited = false; // Bad code looool

    private ValueBar HPBar;
    private ValueBar APBar;
    private ValueBar StaminaBar;

    private BasicSprite KevlarIcon;
    private final ArrayList<BasicSprite> plates = new ArrayList<>();
    private final HashMap<Integer, WeaponInfo> weaponList = new HashMap<>();

    private BasicSpriteBuilder plateBuilder;
    private BasicSpriteBuilder weaponBuilder;
    private BasicTextBuilder ammoBuilder;

    // The following 4 things were in GameManager before
    private ChatSystem chatSystem;
    private BasicText debug_mouseWorldPosition;
    private BasicText debug_FPSValue;

    private BasicText notice;
    private int noticeTimeLeft;

    private final static float HUD_ALPHA = 1f;

    // Initialized late due to dependency on assets, which are preloaded by GameInit
    // Probably bad?
    public void initialize() {
        if (inited) return;
        inited = true;

        int Width = HgGame.WorldWidth;
        int Height = HgGame.WorldHeight;

        int XOffset = -HgGame.WorldWidth / 2;
        int YOffset = -HgGame.WorldHeight / 2;
        AssetEngine assets = HgGame.Assets();

        Texture HPSlice = assets.loadTexture("Assets/GUI/HPSlice.png");
        Texture APSlice = assets.loadTexture("Assets/GUI/APSlice.png");
        Texture StaminaSlice = assets.loadTexture("Assets/GUI/StaminaSlice.png");

        BitmapFont barValueFont = assets.getFont("BarValueDefault");

        weaponBuilder = new BasicSpriteBuilder().centerToW(true).makeGUI();
        ammoBuilder = BuilderLibrary.BasicTextBuilders("smalllabel").textPos(HPos.Left, VPos.Center).makeGUI();

        KevlarIcon = new BasicSpriteBuilder().texture(assets.loadTexture("Assets/GUI/KevlarVestIcon.png")).centerToNW(true).position(XOffset + 10, YOffset + Height - 32).makeGUI().build();
        KevlarIcon.setLayer(KevlarIcon.getLayer() + 1);
        KevlarIcon.setAlpha(HUD_ALPHA);
        plateBuilder = new BasicSpriteBuilder().texture(assets.loadTexture("Assets/GUI/PlateIcon.png")).centerToNW(true).makeGUI();

        HPBar = new ValueBar(HPSlice, 100, 370);
        HPBar.getPosition().set(XOffset + 30 + 4, YOffset + Height - 20 - 4);
        HPBar.getCenterOffset().set(0, HPSlice.getHeight());
        HPBar.setCameraUse(false);
        HPBar.setLayer(DrawLayer.GUIDefault + 1);
        HPBar.registerToEngine();
        HPBar.setAlpha(HUD_ALPHA);

        HPBar.setContainerTex(assets.loadTexture("Assets/GUI/HPBox.png"));
        //HPBar.setValueFont(barValueFont);
        HPBar.setContainerOffset(-4, -4);

        APBar = new ValueBar(APSlice, 100, 230);
        APBar.getPosition().set(XOffset + 170 + 4, YOffset + Height - 65 - 4);
        APBar.getCenterOffset().set(0, APSlice.getHeight());
        APBar.setCameraUse(false);
        APBar.setLayer(DrawLayer.GUIDefault + 2);
        APBar.registerToEngine();
        APBar.setAlpha(HUD_ALPHA);

        APBar.setContainerTex(assets.loadTexture("Assets/GUI/APBox.png"));
        //APBar.setValueFont(barValueFont);
        APBar.setContainerOffset(-4, -4);

        StaminaBar = new ValueBar(StaminaSlice, 100, 230);
        StaminaBar.getPosition().set(XOffset + 10, YOffset + 10);
        StaminaBar.setCameraUse(false);
        StaminaBar.setLayer(DrawLayer.GUIDefault + 2);
        StaminaBar.registerToEngine();
        StaminaBar.setAlpha(HUD_ALPHA);

        StaminaBar.setContainerTex(assets.loadTexture("Assets/GUI/APBox.png"));
        //APBar.setValueFont(barValueFont);
        StaminaBar.setContainerOffset(-4, -4);


        // Previous GameManager stuff

        chatSystem = new ChatSystem();
        chatSystem.setEnabled(false);
        chatSystem.getPosition().set(-400, -520);

        debug_mouseWorldPosition = BuilderLibrary.BasicTextBuilders("label").textPos(HPos.Left, VPos.Top).makeGUI().build();
        debug_mouseWorldPosition.setPositionOffset(new Vector2(80, -80));
        debug_mouseWorldPosition.setEnabled(false);
        debug_mouseWorldPosition.registerToEngine();

        debug_FPSValue = BuilderLibrary.BasicTextBuilders("smalllabel").textPos(HPos.Left, VPos.Top).makeGUI().build();
        debug_FPSValue.setPositionOffset(new Vector2(-940, 300));
        debug_FPSValue.setEnabled(false);
        debug_FPSValue.registerToEngine();

        // Notice - used for error messages

        notice = new BasicText(HgGame.Assets().getFont("Text36"), "");
        notice.setPosition(new Vector2(960 - 20, -540 + 20));
        notice.setConstraints(HPos.Right, VPos.Bottom, 0f);
        notice.setAlpha(0f);
        notice.setCameraUse(false);
        notice.setLayer(DrawLayer.GUIDefault);
        notice.registerToEngine();
        noticeTimeLeft = 0;
    }

    public void enableChatSystem() {
        chatSystem.setEnabled(true);
    }

    public void disableChatSystem() {
        chatSystem.setEnabled(false);
        chatSystem.clear();
    }

    public void toggleMouseDebug() {
        debug_mouseWorldPosition.setEnabled(!debug_mouseWorldPosition.isActive());
    }

    public void toggleFPS() {
        debug_FPSValue.setEnabled(!debug_FPSValue.isActive());
    }

    public void setNotice(String text, int noticeTime) {
        notice.setText(text);
        noticeTimeLeft = noticeTime;
    }

    public ChatSystem getChatSystem() {
        return chatSystem;
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
            plates.add(plateBuilder.copy().position(XOffset + 410 + 15 * plates.size(), YOffset + Height - 20).build());
            plates.get(plates.size() - 1).setAlpha(HUD_ALPHA);
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
                info.display.setScale(0.8f);
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
        float currentY = 75 - HgGame.WorldHeight / 2f;
        float currentX = 20 - HgGame.WorldWidth / 2f;
        int preferredWeapon = localPlayer != null ? localPlayer.viewSelectedWeapon() : WeaponType.Revolver;
        boolean cancelFirst = true;

        for (var displayed: weaponList.entrySet()) {
            WeaponInfo info = displayed.getValue();
            float displacement = info.display.getRegion().height / 2f + 10;

            if (cancelFirst) cancelFirst = false;
            else currentY += displacement;

            updateInfo(info, currentX + (displayed.getKey() == preferredWeapon ? 20 : 0), currentY);
            currentY += displacement;
        }
    }

    private void updateInfo(WeaponInfo info, float currentX, float currentY) {
        float textDistance = info.display.getRegion().width + 4f;
        info.display.getPosition().set(currentX, currentY);
        info.ammo.getPosition().set(currentX + textDistance, currentY);
    }

    public void update() {
        GameManager manager = HgGame.Manager();
        PlayerEntity localPlayer = manager.localView != null ? manager.localView.playerEntity : null;


        DecimalFormat noDigits = new DecimalFormat();
        noDigits.setMaximumFractionDigits(0);

        chatSystem.onUpdate();
        if (noticeTimeLeft > -60) {
            notice.setAlpha(Math.min(noticeTimeLeft + 60, 60) / 60f);
            noticeTimeLeft--;
        }
        else notice.setAlpha(0f);

        debug_mouseWorldPosition.setPosition(HgGame.Input().getMouse());
        var pos = HgGame.Input().getFOVWorldMouse(HgGame.Game().getFOVFactor());
        debug_mouseWorldPosition.setText(noDigits.format(pos.x) + " " + noDigits.format(pos.y));

        debug_FPSValue.setText("FPS: " + Gdx.graphics.getFramesPerSecond());

        updatePlates();

        if (localPlayer == null) {
            HPBar.setEnabled(false);
            APBar.setEnabled(false);
            StaminaBar.setEnabled(false);
            KevlarIcon.setEnabled(false);
        }
        else {
            BaseStats stats = localPlayer.getStats();
            HPBar.setEnabled(true);
            HPBar.setProperties(stats.maxHealth, 370);
            HPBar.updateFill(stats.health);
            StaminaBar.setEnabled(true);
            StaminaBar.setProperties(stats.maxStamina, 230);
            StaminaBar.updateFill(stats.stamina);

            if (stats.heavyArmor > 0f) {
                APBar.setEnabled(true);
                APBar.setProperties(stats.maxHeavyArmor, 230);
                APBar.updateFill(stats.heavyArmor);
            }
            else {
                APBar.setEnabled(false);
            }

            KevlarIcon.setEnabled(stats.hasKevlarVest);
        }

        updateWeapons();
    }

    public void cleanup() {
        HPBar.unregisterFromEngine();
        APBar.unregisterFromEngine();
        KevlarIcon.unregisterFromEngine();

        for (var plate: plates)
            plate.unregisterFromEngine();

        notice.unregisterFromEngine();
        debug_mouseWorldPosition.unregisterFromEngine();
        debug_FPSValue.unregisterFromEngine();
    }
}
