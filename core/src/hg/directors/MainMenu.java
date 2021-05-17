package hg.directors;

import hg.drawables.BasicText;
import hg.engine.NetworkEngine;
import hg.game.DataManager;
import hg.game.HgGame;
import hg.libraries.BuilderLibrary;
import hg.networking.NetworkStatus;
import hg.enums.types.DirectorType;
import hg.ui.BasicTextInput;
import hg.ui.BasicUIStates;
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

/** MainMenu handles the code relevant to the main menu, settings, etc. */
public class MainMenu extends Director {
    private static boolean SettingsInit = false;
    private final BasicUIStates menus = new BasicUIStates();

    private final DecimalFormat format1;

    // Shortcuts
    private final BasicTextInput clientIPAdress;
    private final BasicText connectStatus;
    private final BasicText resolutionLabel;
    private final BasicText sensitivityLabel;
    private final BasicText focusLabel;
    private final ToggleButton fullscreenToggle;

    private int waitDuration = 0;
    private int joinState = 0; // 0 - idle, 1 - connecting, 2 - joining lobby

    private int resSelection = 0;
    private float sensSelection = 1.0f;
    private float fovSelection = 0.6f;

    public MainMenu() {

        format1 = new DecimalFormat();
        format1.setMaximumFractionDigits(2);
        format1.setMinimumFractionDigits(2);

        ClickButtonBuilder boxButton = BuilderLibrary.ClickButtonBuilders("silverbox");
        ClickButtonBuilder arrowButton = BuilderLibrary.ClickButtonBuilders("leftarrow");
        ToggleButtonBuilder checkButton = BuilderLibrary.ToggleButtonBuilders("silvercheck");
        TextInputBuilder adress = BuilderLibrary.TextInputBuilders("serverip");
        BasicTextBuilder label = BuilderLibrary.BasicTextBuilders("label");
        BasicTextBuilder title = BuilderLibrary.BasicTextBuilders("title");

        // === Main Menu ===
        menus.addObjects("Main",
                boxButton.copy().position(-660, -200).text("Start Server").onClick(() -> toMenu("StartServer")).build(),
                boxButton.copy().position(-660, -400).text("Start Client").onClick(() -> toMenu("StartClient")).build(),
                boxButton.copy().position(660, -200).text("Settings").onClick(() -> toMenu("Settings")).build(),
                boxButton.copy().position(660, -400).text("Quit").onClick(this::quitGame).build(),
                title.copy().position(0, 440).text("QuickSilver").makeGUI().build()
        );
        // === Settings Menu ===
        menus.addObjects("Settings",
                resolutionLabel = label.copy().position(-660, 300).text("<not_init>").textPos(HPos.Left, VPos.Center).makeGUI().build(),
                sensitivityLabel = label.copy().position(-660, 100).text("<not_init>").textPos(HPos.Left, VPos.Center).makeGUI().build(),
                focusLabel = label.copy().position(-660, 0).text("<not_init>").textPos(HPos.Left, VPos.Center).makeGUI().build(),
                fullscreenToggle = checkButton.copy().position(-860, 200).build(),
                title.copy().position(0, 440).text("Settings").makeGUI().build(),
                label.copy().position(-760, 200).text("Fullscreen?").textPos(HPos.Left, VPos.Center).makeGUI().build(),
                boxButton.copy().position(660, -200).text("Apply").onClick(this::applySettings).build(),
                arrowButton.copy().position(-860, 300).angle(90).onClick(this::downResolution).build(),
                arrowButton.copy().position(-760, 300).angle(-90).onClick(this::upResolution).build(),
                arrowButton.copy().position(-860, 100).angle(90).onClick(this::downSensitivity).build(),
                arrowButton.copy().position(-760, 100).angle(-90).onClick(this::upSensitivity).build(),
                arrowButton.copy().position(-860, 0).angle(90).onClick(this::downFOV).build(),
                arrowButton.copy().position(-760, 0).angle(-90).onClick(this::upFOV).build(),
                boxButton.copy().position(660, -400).text("Go Back").onClick(() -> toMenu("Main")).build()
        );
        // === Start Server Menu ===
        menus.addObjects("StartServer",
                boxButton.copy().position(-660, -400).text("Create Server").onClick(this::tryStartServer).build(),
                boxButton.copy().position(660, -400).text("Go Back").onClick(() -> toMenu("Main")).build(),
                title.copy().position(0, 440).text("QuickSilver").makeGUI().build()
        );
        // === Start Client Menu ===
        menus.addObjects("StartClient",
                clientIPAdress = adress.position(-660, 200).build(),
                boxButton.copy().position(-660, -400).text("Connect").onClick(this::tryConnect).build(),
                boxButton.copy().position(660, -400).text("Go Back").onClick(() -> toMenu("Main")).build(),
                title.copy().position(0, 440).text("QuickSilver").makeGUI().build()
        );
        // === Client Connect screen
        menus.addObjects("ClientConnect", connectStatus = label.copy().text("Connecting...").makeGUI().build());

        menus.scheduleStateSwitch("Main"); // Start menu in this state

        if (!SettingsInit) {
            SettingsInit = true;
            initSettings();
            applySettings();
        }
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
                    ((GameSession) HgGame.Manager().tryAddDirector(DirectorType.GameSession)).startLobby();
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
        ((GameSession) HgGame.Manager().tryAddDirector(DirectorType.GameSession)).startMatch();
        toBeDestroyed = true;
    }

    // Callbacks

    private void toMenu(String menu) {
        menus.scheduleStateSwitch(menu);
    }

    private void quitGame() {
        HgGame.Manager().tryAddDirector(DirectorType.GameQuit);
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
        fovSelection = MathTools.Clamp(fovSelection + 0.05f, 0f, 0.9f);
        updateSettingsLabels();
    }

    private void downFOV() {
        fovSelection = MathTools.Clamp(fovSelection - 0.05f, 0f, 0.9f);
        updateSettingsLabels();
    }

    private void updateSettingsLabels() {
        var resolution = HgGame.Graphics().getSupportedResolutions().get(resSelection);
        resolutionLabel.setText((int) resolution.width + " x " + (int) resolution.height);

        sensitivityLabel.setText("Mouse Speed: " + format1.format(sensSelection));

        focusLabel.setText("FOV Factor: " + format1.format(fovSelection));
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
        fovSelection = MathTools.Clamp(Float.parseFloat(data.getSetting("FOVFactor")), 0f, 0.9f);
    }

    private void applySettings() {
        DataManager data = HgGame.Data();

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
    }

    private void tryStartServer() {
        NetworkEngine network = HgGame.Network();
        try {
            network.startServer();
        }
        catch(IOException ignored) {
            HgGame.Manager().setNotice("Couldn't open server due to an error!\nCheck if ports " + network.getTCPPort() + " (TCP) and " + network.getUDPPort() + " (UDP) are open and available.", 180);
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
