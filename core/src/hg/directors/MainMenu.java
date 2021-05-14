package hg.directors;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import hg.drawables.BasicText;
import hg.drawables.DrawLayer;
import hg.engine.AssetEngine;
import hg.engine.NetworkEngine;
import hg.game.GameManager;
import hg.game.HgGame;
import hg.networking.NetworkStatus;
import hg.types.DirectorType;
import hg.ui.BasicTextInput;
import hg.ui.BasicUIStates;
import hg.ui.ToggleButton;
import hg.enums.HPos;
import hg.utils.builders.TextInputMaker;
import hg.utils.builders.BasicTextMaker;
import hg.utils.builders.ClickButtonMaker;
import hg.utils.builders.ToggleButtonMaker;
import hg.utils.HgMathUtils;
import hg.enums.VPos;

import java.io.IOException;
import java.text.DecimalFormat;

/** MainMenu handles the code relevant to the main menu, settings, etc. */
public class MainMenu extends Director {
    private final BasicUIStates menus = new BasicUIStates();

    private final DecimalFormat format1;

    // Client related
    private BasicTextInput clientIPAdress; // Set to the start client menu's input
    private BasicText connectStatus;

    // Settings
    private BasicText resolutionLabel;
    private BasicText sensitivityLabel;
    private ToggleButton fullscreenToggle;

    private boolean tryClientConnect = false;
    private int waitDuration = 0;
    private boolean clientStartLobby = false;

    private int resSelection = 0;
    private float targetSens = 1.0f;

    private void toMenu(String menu) { menus.scheduleStateSwitch(menu); }

    public MainMenu() {
        AssetEngine assets = HgGame.Assets();

        format1 = new DecimalFormat();
        format1.setMaximumFractionDigits(2);
        format1.setMinimumFractionDigits(2);

        Texture bigbox = assets.loadTexture("Assets/GUI/Button.png");
        Texture arrowbox = assets.loadTexture("Assets/GUI/ArrowButtonLeft.png");
        Texture onBox = assets.loadTexture("Assets/GUI/ToggleActive.png");
        Texture offBox = assets.loadTexture("Assets/GUI/ToggleInactive.png");
        BitmapFont couriernew36 = assets.loadFont("Assets/Fonts/CourierNew36.fnt");
        BitmapFont couriernew48 = assets.loadFont("Assets/Fonts/CourierNew48.fnt");
        BitmapFont couriernew72 = assets.loadFont("Assets/Fonts/CourierNew72.fnt");
        BitmapFont couriernew144 = assets.loadFont("Assets/Fonts/CourierNew144.fnt");

        ClickButtonMaker boxButton = new ClickButtonMaker().display(bigbox).font(couriernew36).clickArea(460, 150);
        ClickButtonMaker arrowButton = new ClickButtonMaker().display(arrowbox).clickArea(80, 80);
        ToggleButtonMaker checkButton = new ToggleButtonMaker().display(offBox, onBox).clickArea(80, 80);
        BasicTextMaker labels = new BasicTextMaker().font(couriernew72).textPos(HPos.Center, VPos.Center);
        BasicTextMaker titles = new BasicTextMaker().font(couriernew144).textPos(HPos.Center, VPos.Center);

        // === Main Menu ===
        menus.addObjects("Main",
                new ClickButtonMaker(boxButton).position(-660, -200).text("Start Client").onClick(() -> toMenu("StartServer")).build(),
                new ClickButtonMaker(boxButton).position(-660, -400).text("Start Server").onClick(() -> toMenu("StartClient")).build(),
                new ClickButtonMaker(boxButton).position(660, -200).text("Settings").onClick(() -> toMenu("Settings")).build(),
                new ClickButtonMaker(boxButton).position(660, -400).text("Quit").onClick(() -> {
                    HgGame.Manager().tryAddDirector(DirectorType.GameQuit);
                    toBeDestroyed = true;
                }).build(),
                new BasicTextMaker(titles).position(0, 440).text("QuickSilver").makeGUI().build()
        );

        // === Settings Menu ===
        menus.addObjects("Settings",
                resolutionLabel = new BasicTextMaker(labels).position(-660, 300).text("<not_init>").textPos(HPos.Left, VPos.Center).makeGUI().build(),
                sensitivityLabel = new BasicTextMaker(labels).position(-660, 100).text("<not_init>").textPos(HPos.Left, VPos.Center).makeGUI().build(),
                fullscreenToggle = new ToggleButtonMaker(checkButton).position(-860, 200).build(),
                new BasicTextMaker(titles).position(0, 440).text("Settings").makeGUI().build(),
                new BasicTextMaker(labels).position(-760, 200).text("Fullscreen?").textPos(HPos.Left, VPos.Center).makeGUI().build(),
                new ClickButtonMaker(boxButton).position(660, -200).text("Apply").onClick(this::applySettings).build(),
                new ClickButtonMaker(arrowButton).position(-860, 300).angle(90).onClick(() -> {
                    resSelection++;
                    updateSettingsLabels();
                }).build(),
                new ClickButtonMaker(arrowButton).position(-760, 300).angle(-90).onClick(() -> {
                    resSelection--;
                    updateSettingsLabels();
                }).build(),
                new ClickButtonMaker(arrowButton).position(-860, 100).angle(90).onClick(() -> {
                    HgGame.Input().modifySensitivity(-0.04f);
                    updateSettingsLabels();
                }).build(),
                new ClickButtonMaker(arrowButton).position(-760, 100).angle(-90).onClick(() -> {
                    HgGame.Input().modifySensitivity(+0.04f);
                    updateSettingsLabels();
                }).build(),
                new ClickButtonMaker(boxButton).position(660, -400).text("Go Back").onClick(() -> toMenu("Main")).build()
        );

        // === Start Server Menu ===
        menus.addObjects("StartServer",
                new ClickButtonMaker(boxButton).position(-660, -400).text("Create Server").onClick(this::tryStartServer).build(),
                new ClickButtonMaker(boxButton).position(660, -400).text("Go Back").onClick(() -> toMenu("Main")).build(),
                new BasicTextMaker(titles).position(0, 440).text("QuickSilver").makeGUI().build()
        );


        // === Start Client Menu ===
        menus.addObjects("StartClient",
                clientIPAdress = new TextInputMaker().position(-660, 200).font(couriernew48).emptyText("Enter IP...").maxChars(32).clickArea(350, 48).build(),
                new ClickButtonMaker(boxButton).position(-660, -400).text("Connect").onClick(this::tryConnect).build(),
                new ClickButtonMaker(boxButton).position(660, -400).text("Go Back").onClick(() -> toMenu("Main")).build(),
                new BasicTextMaker(titles).position(0, 440).text("QuickSilver").makeGUI().build()
        );

        // === Client Connect screen
        menus.addObjects("ClientConnect", connectStatus = new BasicTextMaker(labels).text("Connecting...").makeGUI().build());

        // === Generic stuff ===

        menus.scheduleStateSwitch("Main"); // Start menu in this state
        updateSettingsLabels();
    }

    public void onDisconnectFromServer() {
        menus.scheduleStateSwitch("MainMenu");
        waitDuration = 0;
        clientStartLobby = false;
        tryClientConnect = false;
    }

    @Override
    public void destroy() {
        menus.destroy();
    }

    @Override
    public void update() {
        NetworkEngine network = HgGame.Network();

        if (waitDuration <= 0 && clientStartLobby) {
            GameSession match = (GameSession) HgGame.Manager().tryAddDirector(DirectorType.GameSession);
            match.startLobby();
            toBeDestroyed = true;
        }

        if (waitDuration > 0) waitDuration--;

        if (!tryClientConnect && waitDuration <= 0 && menus.getCurrentState().equals("ClientConnect")) {
            menus.scheduleStateSwitch("StartClientMenu");
        }

        if (tryClientConnect) {
            var status = network.getNetStatus();
            if (status == NetworkStatus.Ready) {
                connectStatus.setText("Connected!");
                waitDuration = 150;
                clientStartLobby = true;
                tryClientConnect = false;
            }
            else if (waitDuration <= 0 || status == NetworkStatus.ConnectionFailed) {
                tryClientConnect = false;
                connectStatus.setText(status == NetworkStatus.ConnectionFailed ? "Connection failed for some reason!" : "Connection timed out!");
                waitDuration = 150;
                network.stopNetwork();
            }
        }
        menus.onUpdate();
    }

    public void signalEarlyStart() {
        GameSession match = (GameSession) HgGame.Manager().tryAddDirector(DirectorType.GameSession);
        if (match != null) match.startMatch();
        toBeDestroyed = true;
    }

    private void updateSettingsLabels() {
        var selections = HgGame.Graphics().getSupportedResolutions();
        resSelection = HgMathUtils.ClampValue(resSelection, 0, selections.size() - 1);
        var resolution = selections.get(resSelection);
        resolutionLabel.setText((int) resolution.width + " x " + (int) resolution.height);

        float sens = HgGame.Input().getMouseSensitivity();
        sensitivityLabel.setText("Mouse Speed: " + format1.format(sens));
    }

    private void applySettings() {
        var selections = HgGame.Graphics().getSupportedResolutions();
        var resolution = selections.get(resSelection);

        HgGame.Graphics().setVideoMode((int) resolution.width, (int) resolution.height, fullscreenToggle.isActive());
    }

    private void tryStartServer() {
        NetworkEngine network = HgGame.Network();
        GameManager manager = HgGame.Manager();
        boolean succeeded = false;
        try {
            network.startServer();
            succeeded = true;
        }
        catch(IOException ignored) {
            manager.setNotice("Couldn't open server due to an error!\nCheck if ports " + network.getTCPPort() + " (TCP) and " + network.getUDPPort() + " (UDP) are open and available.", 180);
        }
        if (succeeded) {
            GameSession match = (GameSession) HgGame.Manager().tryAddDirector(DirectorType.GameSession);
            match.startLobby();
            toBeDestroyed = true;
        }
    }

    private void tryConnect() {
        menus.scheduleStateSwitch("ClientConnect");
        tryClientConnect = true;
        waitDuration = (int) (NetworkEngine.ConnectionTimeoutInMilli / 16.66f); // about 2.5 seconds
        connectStatus.setText("Connecting...");
        HgGame.Network().tryStartClient(clientIPAdress.getText());
    }
}
