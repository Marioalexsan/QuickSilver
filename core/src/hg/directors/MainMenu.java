package hg.directors;

import com.badlogic.gdx.graphics.Texture;
import hg.drawables.BasicText;
import hg.engine.NetworkEngine;
import hg.game.DataManager;
import hg.game.HgGame;
import hg.libraries.BuilderLibrary;
import hg.networking.NetworkStatus;
import hg.enums.DirectorType;
import hg.ui.BasicTextInput;
import hg.ui.CardMenu;
import hg.ui.ToggleButton;
import hg.enums.HPos;
import hg.utils.builders.TextInputBuilder;
import hg.utils.builders.BasicTextBuilder;
import hg.utils.builders.ClickButtonBuilder;
import hg.utils.builders.ToggleButtonBuilder;
import hg.utils.MathTools;
import hg.enums.VPos;

import java.io.IOException;
import java.text.DecimalFormat;

/** MainMenu handles the main menu logic. This includes the start menu, settings, connect, etc. */
public class MainMenu extends Director {
    private static boolean SettingsInit = false;
    private final CardMenu menus = new CardMenu();

    private final float NO_FOV = 0f;
    private final float REDUCED_FOV = 0.25f;
    private final float FULL_FOV = 0.42f;

    private final DecimalFormat format1;
    private final DecimalFormat noDigits;

    private GameSession.SessionOptions optionsToForward;
    private boolean startNow = false;

    public void receiveOptions(GameSession.SessionOptions options) {
        optionsToForward = options;
    }

    // Shortcuts
    private final BasicTextInput clientIPAdress;
    private final BasicText connectStatus;
    private final BasicText resolutionLabel;
    private final BasicText sensitivityLabel;
    private final BasicText soundLabel;
    private final BasicText musicLabel;
    private final BasicText fovLabel;
    private final ToggleButton fullscreenToggle;
    private final BasicTextInput userNameInput;

    private int waitDuration = 0;
    private int joinState = 0; // 0 - idle, 1 - connecting, 2 - joining lobby

    private int resSelection = 0;
    private float sensSelection = 1.0f;
    private float fovSelection = 0.6f;
    private float soundSelection = 1.0f;
    private float musicSelection = 0.6f;

    public MainMenu() {
        HgGame.Audio().playMusic("Assets/Audio/QuickSilver - Imbalance.ogg", 1f);

        format1 = new DecimalFormat();
        format1.setMaximumFractionDigits(2);
        format1.setMinimumFractionDigits(2);

        noDigits = new DecimalFormat();
        noDigits.setMaximumFractionDigits(0);
        noDigits.setMinimumFractionDigits(0);

        ClickButtonBuilder smallBox = BuilderLibrary.ClickButtonBuilders("silverboxsmall");
        ClickButtonBuilder smallShortBox = BuilderLibrary.ClickButtonBuilders("silverboxsmallshort");
        ClickButtonBuilder arrowButton = BuilderLibrary.ClickButtonBuilders("leftarrow");
        ToggleButtonBuilder checkButton = BuilderLibrary.ToggleButtonBuilders("silvercheck");
        TextInputBuilder input = BuilderLibrary.TextInputBuilders("default");
        TextInputBuilder username = BuilderLibrary.TextInputBuilders("username");
        BasicTextBuilder label = BuilderLibrary.BasicTextBuilders("label");
        BasicTextBuilder title = BuilderLibrary.BasicTextBuilders("title");

        Texture upDown = HgGame.Assets().loadTexture("Assets/GUI/UpDownButton.png");

        // === Main Menu ===
        menus.addObjects("Main",
                smallBox.copy().position(-660, -280).text("Start Server").onClick(() -> toMenu("StartServer")).build(),
                smallBox.copy().position(-660, -400).text("Start Client").onClick(() -> toMenu("StartClient")).build(),
                smallBox.copy().position(660, -280).text("Settings").onClick(() -> toMenu("Settings")).build(),
                smallBox.copy().position(660, -400).text("Quit").onClick(this::quitGame).build(),
                title.copy().position(0, 440).text("QuickSilver").makeGUI().build()
        );
        // === Settings Menu ===
        menus.addObjects("Settings",
                resolutionLabel = label.copy().position(-660, 300).text("<not_init>").textPos(HPos.Left, VPos.Center).makeGUI().build(),
                sensitivityLabel = label.copy().position(-660, 100).text("<not_init>").textPos(HPos.Left, VPos.Center).makeGUI().build(),
                fovLabel = label.copy().position(-760, 0).text("<not_init>").textPos(HPos.Left, VPos.Center).makeGUI().build(),
                soundLabel = label.copy().position(-660, -100).text("<not_init>").textPos(HPos.Left, VPos.Center).makeGUI().build(),
                musicLabel = label.copy().position(-660, -200).text("<not_init>").textPos(HPos.Left, VPos.Center).makeGUI().build(),
                fullscreenToggle = checkButton.copy().position(-860, 200).build(),
                label.copy().position(-860, -300).text("User Name: ").textPos(HPos.Left, VPos.Center).build(),
                userNameInput = username.copy().position(-820, -380).emptyText("(empty!)").textPos(HPos.Left,VPos.Center).build(),
                title.copy().position(0, 440).text("Settings").makeGUI().build(),
                label.copy().position(-760, 200).text("Fullscreen?").textPos(HPos.Left, VPos.Center).makeGUI().build(),
                smallBox.copy().position(660, -200).text("Apply").onClick(this::applySettings).build(),
                arrowButton.copy().position(-860, 300).angle(90).onClick(this::downResolution).build(),
                arrowButton.copy().position(-760, 300).angle(-90).onClick(this::upResolution).build(),
                arrowButton.copy().position(-860, 100).angle(90).onClick(this::downSensitivity).build(),
                arrowButton.copy().position(-760, 100).angle(-90).onClick(this::upSensitivity).build(),
                arrowButton.copy().display(upDown).copy().position(-860, 0).onClick(this::downFOV).build(),
                arrowButton.copy().position(-860, -100).angle(90).onClick(this::downSound).build(),
                arrowButton.copy().position(-760, -100).angle(-90).onClick(this::upSound).build(),
                arrowButton.copy().position(-860, -200).angle(90).onClick(this::downMusic).build(),
                arrowButton.copy().position(-760, -200).angle(-90).onClick(this::upMusic).build(),
                smallBox.copy().position(660, -400).text("Go Back").onClick(() -> toMenu("Main")).build()
        );
        // === Start Server Menu ===
        menus.addObjects("StartServer",
                smallBox.copy().position(-660, -400).text("Create Server").onClick(this::tryStartServer).build(),
                smallBox.copy().position(660, -400).text("Go Back").onClick(() -> toMenu("Main")).build(),
                title.copy().position(0, 440).text("QuickSilver").makeGUI().build()
        );
        // === Start Client Menu ===
        menus.addObjects("StartClient",
                clientIPAdress = input.position(-660, 200).build(),
                smallBox.copy().position(-660, -400).text("Connect").onClick(this::tryConnect).build(),
                smallBox.copy().position(660, -400).text("Go Back").onClick(() -> toMenu("Main")).build(),
                title.copy().position(0, 440).text("QuickSilver").makeGUI().build()
        );
        // === Client Connect screen
        menus.addObjects("ClientConnect", connectStatus = label.copy().text("Connecting...").makeGUI().build());

        menus.scheduleStateSwitch("Main"); // Start menu in this state

        initSettings();
        applySettings();
        updateSettingsLabels();
    }

    public void onDisconnectFromServer() {
        menus.scheduleStateSwitch("MainMenu");
        waitDuration = 0;
        joinState = 0;
    }

    @Override
    public void destroy() {
        menus.destroy();
    }

    @Override
    public void update() {
        NetworkEngine network = HgGame.Network();

        if (waitDuration > 0) waitDuration--;

        switch (joinState) {
            case 2 -> {
                if (waitDuration <= 0) {
                    GameSession match = (GameSession) HgGame.Manager().tryAddDirector(DirectorType.GameSession);
                    if (optionsToForward != null) {
                        match.updateSettings(optionsToForward);
                    }
                    match.startLobby();
                    toBeDestroyed = true;
                }
            }
            case 1 -> {
                var status = network.getNetStatus();
                if (status == NetworkStatus.Ready) {
                    connectStatus.setText("Connected!");
                    waitDuration = 150;
                    joinState = 2;
                }
                else if (waitDuration <= 0 || status == NetworkStatus.ConnectionFailed) {
                    joinState = 0;
                    connectStatus.setText(status == NetworkStatus.ConnectionFailed ? "Connection failed for some reason!" : "Connection timed out!");
                    waitDuration = 150;
                    network.stopNetwork();
                    optionsToForward = null;
                }
            }
            case 0 -> {
                if (waitDuration <= 0 && menus.getCurrentState().equals("ClientConnect")) {
                    menus.scheduleStateSwitch("StartClient");
                }
            }
        }

        menus.onUpdate();
    }

    public void signalEarlyStart() {
        GameSession session = (GameSession) HgGame.Manager().tryAddDirector(DirectorType.GameSession);
        session.updateSettings(optionsToForward);
        session.startMatch();

        toBeDestroyed = true;
    }

    // Callbacks

    private void toMenu(String menu) {
        if (menus.getCurrentState().equals("Settings")) {
            // If settings weren't applied with the Apply button, this will reset the labels
            initSettings();
            updateSettingsLabels();
        }
        menus.scheduleStateSwitch(menu);
    }

    private void quitGame() {
        HgGame.Manager().tryAddDirector(DirectorType.Janitor);
        toBeDestroyed = true;
    }

    private void upResolution() {
        var selections = HgGame.Graphics().getSupportedResolutions();
        resSelection = MathTools.Clamp(resSelection - 1, 0, selections.size() - 1);
        updateSettingsLabels();
    }

    private void downResolution() {
        var selections = HgGame.Graphics().getSupportedResolutions();
        resSelection = MathTools.Clamp(resSelection + 1, 0, selections.size() - 1);
        updateSettingsLabels();
    }

    private void upSensitivity() {
        sensSelection = MathTools.Clamp(sensSelection + 0.04f, 0.6f, 1.6f);
        updateSettingsLabels();
    }

    private void downSensitivity() {
        sensSelection = MathTools.Clamp(sensSelection - 0.04f, 0.6f, 1.6f);
        updateSettingsLabels();
    }

    private void upFOV() {
        fovSelection = (fovSelection == NO_FOV) ? REDUCED_FOV : (fovSelection == REDUCED_FOV ? FULL_FOV : NO_FOV);
        updateSettingsLabels();
    }

    private void downFOV() {
        fovSelection = (fovSelection == FULL_FOV) ? REDUCED_FOV : (fovSelection == REDUCED_FOV ? NO_FOV : FULL_FOV);
        updateSettingsLabels();
    }

    private void upSound() {
        soundSelection = MathTools.Clamp(soundSelection + 0.05f, 0f, 1f);
        updateSettingsLabels();
    }

    private void downSound() {
        soundSelection = MathTools.Clamp(soundSelection - 0.05f, 0f, 1f);
        updateSettingsLabels();
    }

    private void upMusic() {
        musicSelection = MathTools.Clamp(musicSelection + 0.05f, 0f, 1f);
        updateSettingsLabels();
    }

    private void downMusic() {
        musicSelection = MathTools.Clamp(musicSelection - 0.05f, 0f, 1f);
        updateSettingsLabels();
    }

    private void updateSettingsLabels() {
        var resolution = HgGame.Graphics().getSupportedResolutions().get(resSelection);
        resolutionLabel.setText((int) resolution.width + " x " + (int) resolution.height);

        sensitivityLabel.setText("Mouse Speed: " + format1.format(sensSelection));

        String text = "None";
        if (fovSelection == REDUCED_FOV) text = "Reduced";
        if (fovSelection == FULL_FOV) text = "Full";
        fovLabel.setText("Look ahead: " + text);

        soundLabel.setText("Sound Volume: " + noDigits.format(soundSelection * 100));
        musicLabel.setText("Music Volume: " + noDigits.format(musicSelection * 100));
    }

    private void initSettings() {
        DataManager data = HgGame.Data();

        // Resolution
        resSelection = 0;
        int width = Integer.parseInt(data.getSetting("ResWidth"));
        int height = Integer.parseInt(data.getSetting("ResHeight"));
        var selections = HgGame.Graphics().getSupportedResolutions();
        for (int i = 0; i < selections.size(); i++) {
            var resolution = selections.get(i);
            if (resolution.width == width && resolution.height == height) {
                resSelection = i;
                break;
            }
        }

        // Fullscreen
        boolean fullscreen = Boolean.parseBoolean(data.getSetting("Fullscreen"));
        if (fullscreen ^ fullscreenToggle.isActive()) fullscreenToggle.toggle();

        // Sensitivity
        sensSelection = MathTools.Clamp(Float.parseFloat(data.getSetting("MouseSens")), 0.6f, 1.6f);

        // FOV
        fovSelection = Float.parseFloat(data.getSetting("FOVFactor"));
        if (fovSelection != NO_FOV && fovSelection != REDUCED_FOV && fovSelection != FULL_FOV)
            fovSelection = NO_FOV;

        // User Name
        userNameInput.setText(data.getSetting("UserName"));

        // Audio
        soundSelection = MathTools.Clamp(Float.parseFloat(data.getSetting("SoundVol")), 0f, 1f);
        musicSelection = MathTools.Clamp(Float.parseFloat(data.getSetting("MusicVol")), 0f, 1f);
    }

    private void applySettings() {
        DataManager data = HgGame.Data();

        String userName = userNameInput.getText().trim();
        int userNameLength = userName.length();
        if (userNameLength < 3 || userNameLength > 32) {
            HgGame.SetNotice("User name needs to be between 3 and 32 characters!", 300);
            return;
        }

        // Resolution & Fullscreen
        var selections = HgGame.Graphics().getSupportedResolutions();
        var resolution = selections.get(resSelection);

        HgGame.Graphics().setVideoMode((int) resolution.width, (int) resolution.height, fullscreenToggle.isActive());
        data.updateSetting("ResWidth", Integer.toString((int) resolution.width));
        data.updateSetting("ResHeight", Integer.toString((int) resolution.height));
        data.updateSetting("Fullscreen", Boolean.toString(fullscreenToggle.isActive()));

        // Sensitivity
        HgGame.Input().setMouseSensitivity(sensSelection);
        data.updateSetting("MouseSens", Float.toString(sensSelection));

        // FOV
        HgGame.Game().setFOVFactor(fovSelection);
        data.updateSetting("FOVFactor", Float.toString(fovSelection));

        // User Name
        data.updateSetting("UserName", userName);

        // Audio
        data.updateSetting("SoundVol", Float.toString(soundSelection));
        data.updateSetting("MusicVol", Float.toString(musicSelection));
        HgGame.Audio().setGlobalMusicVolume(musicSelection);
        HgGame.Audio().setGlobalSoundVolume(soundSelection);

        updateSettingsLabels();
    }

    private void tryStartServer() {
        NetworkEngine network = HgGame.Network();
        try {
            network.startServer();
        }
        catch(IOException ignored) {
            HgGame.SetNotice("Couldn't open server due to an error!\nCheck if ports " + network.getTCPPort() + " (TCP) and " + network.getUDPPort() + " (UDP) are open and available.", 180);
            return;
        }
        ((GameSession) HgGame.Manager().tryAddDirector(DirectorType.GameSession)).startLobby();
        toBeDestroyed = true;
    }

    private void tryConnect() {
        menus.scheduleStateSwitch("ClientConnect");
        joinState = 1;
        waitDuration = (int) (NetworkEngine.ConnectionTimeoutInMilli / 16.66f); // about 2.5 seconds
        connectStatus.setText("Connecting...");
        HgGame.Network().tryStartClient(clientIPAdress.getText());
    }
}
