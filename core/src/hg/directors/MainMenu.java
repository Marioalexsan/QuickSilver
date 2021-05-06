package hg.directors;

import com.badlogic.gdx.math.Vector2;
import hg.drawables.BasicText;
import hg.drawables.DrawLayer;
import hg.engine.NetworkEngine;
import hg.game.HgGame;
import hg.networking.NetworkStatus;
import hg.ui.BasicTextInput;
import hg.ui.BasicUIStates;
import hg.ui.ClickButton;

/** MainMenu handles the code relevant to the main menu, settings, etc. */
public class MainMenu extends Director {
    private final BasicUIStates menus = new BasicUIStates();

    // Client related
    private BasicTextInput clientIPAdress; // Set to the start client menu's input
    private BasicText connectStatus;
    private boolean tryClientConnect = false;
    private int waitDuration = 0;
    private boolean clientStartLobby = false;

    public MainMenu() {

        // === Main Menu ===

        menus.addState("MainMenu");

        ClickButton mainMenu_startServer = new ClickButton(HgGame.Assets().loadTexture("Assets/GUI/Button.png"), 460, 150, HgGame.Assets().loadFont("Assets/Fonts/CourierNew36.fnt"), "Start Server");
        mainMenu_startServer.setPosition(-660, -200);
        mainMenu_startServer.setCallback(() -> menus.scheduleSwitchState("StartServerMenu"));
        menus.addStateElement("MainMenu", "StartServerButton", mainMenu_startServer);

        ClickButton mainMenu_startClient = new ClickButton(HgGame.Assets().loadTexture("Assets/GUI/Button.png"), 460, 150, HgGame.Assets().loadFont("Assets/Fonts/CourierNew36.fnt"), "Start Client");
        mainMenu_startClient.setPosition(-660, -400);
        mainMenu_startClient.setCallback(() -> menus.scheduleSwitchState("StartClientMenu"));
        menus.addStateElement("MainMenu", "StartClientButton", mainMenu_startClient);

        ClickButton mainMenu_quitButton = new ClickButton(HgGame.Assets().loadTexture("Assets/GUI/Button.png"), 460, 150, HgGame.Assets().loadFont("Assets/Fonts/CourierNew36.fnt"), "Quit");
        mainMenu_quitButton.setPosition(660, -400);
        mainMenu_quitButton.setCallback(() -> {
            HgGame.Manager().addDirector(DirectorTypes.QuitDirector);
            toBeDestroyed = true;
        });
        menus.addStateElement("MainMenu", "QuitButton", mainMenu_quitButton);

        // === Start Server Menu ===

        menus.addState("StartServerMenu");

        ClickButton startServer_startGame = new ClickButton(HgGame.Assets().loadTexture("Assets/GUI/Button.png"), 460, 150, HgGame.Assets().loadFont("Assets/Fonts/CourierNew36.fnt"), "Create Server");
        startServer_startGame.setPosition(-660, -400);
        startServer_startGame.setCallback(() -> {
            MatchDirector match = (MatchDirector) HgGame.Manager().addAndGetDirector(DirectorTypes.MatchDirector);
            match.startAsServer();
            toBeDestroyed = true;
        });
        menus.addStateElement("StartServerMenu", "CreateServerButton", startServer_startGame);

        // === Start Client Menu ===

        menus.addState("StartClientMenu");

        BasicTextInput startClient_IPInput = new BasicTextInput(HgGame.Assets().loadFont("Assets/Fonts/CourierNew48.fnt"), 10, 350, 48);
        startClient_IPInput.setPosition(-660, 200);
        startClient_IPInput.setEmptyText("Enter IP...");
        startClient_IPInput.setMaxLength(32);
        menus.addStateElement("StartClientMenu", "IPInput", startClient_IPInput);
        clientIPAdress = startClient_IPInput;

        ClickButton startClient_connect = new ClickButton(HgGame.Assets().loadTexture("Assets/GUI/Button.png"), 460, 150, HgGame.Assets().loadFont("Assets/Fonts/CourierNew36.fnt"), "Connect");
        startClient_connect.setPosition(-660, -400);
        startClient_connect.setCallback(() -> {
            menus.scheduleSwitchState("ClientConnect");
            tryClientConnect = true;
            waitDuration = (int) (NetworkEngine.ConnectionTimeoutInMilli / 16.66f); // about 2.5 seconds
            connectStatus.setText("Connecting...");
            HgGame.Network().tryStartClient(clientIPAdress.getText());
        });
        menus.addStateElement("StartClientMenu", "Connect", startClient_connect);

        // === Client Connect stuff

        menus.addState("ClientConnect");

        BasicText clientConnect_status = new BasicText(HgGame.Assets().loadFont("Assets/Fonts/CourierNew48.fnt"), "Connecting...");
        clientConnect_status.setCameraUse(false);
        clientConnect_status.setPosition(new Vector2(0, 0));
        clientConnect_status.setConstraints(BasicText.HPos.Center, BasicText.VPos.Center, 0f);
        clientConnect_status.setLayer(DrawLayer.GUIDefault);
        clientConnect_status.registerToEngine();
        menus.addStateElement("ClientConnect", "Text", clientConnect_status);
        connectStatus = clientConnect_status;

        // === Generic stuff ===

        ClickButton generic_goBack = new ClickButton(HgGame.Assets().loadTexture("Assets/GUI/Button.png"), 460, 150, HgGame.Assets().loadFont("Assets/Fonts/CourierNew36.fnt"), "Go Back");
        generic_goBack.setPosition(660, -400);
        generic_goBack.setCallback(() -> {
            switch (menus.getCurrentState()) {
                case "StartClientMenu", "StartServerMenu" -> menus.scheduleSwitchState("MainMenu");
            }
        });
        menus.addStateElement("StartServerMenu", "GoBackButton", generic_goBack);
        menus.addStateElement("StartClientMenu", "GoBackButton", generic_goBack);

        BasicText generic_gameTitle = new BasicText(HgGame.Assets().loadFont("Assets/Fonts/CourierNew144.fnt"), "QuickSilver");
        generic_gameTitle.setCameraUse(false);
        generic_gameTitle.setPosition(new Vector2(0, 440));
        generic_gameTitle.setConstraints(BasicText.HPos.Center, BasicText.VPos.Center, 0f);
        generic_gameTitle.setLayer(DrawLayer.GUIDefault);
        generic_gameTitle.registerToEngine();

        menus.addStateElement("MainMenu", "GameTitle", generic_gameTitle);
        menus.addStateElement("StartServerMenu", "GameTitle", generic_gameTitle);
        menus.addStateElement("StartClientMenu", "GameTitle", generic_gameTitle);

        menus.scheduleSwitchState("MainMenu"); // Start menu in this state
    }

    @Override
    public void destroy() {
        menus.destroy();
    }

    @Override
    public void localUpdate() {
        NetworkEngine network = HgGame.Network();

        if (waitDuration <= 0 && clientStartLobby) {
            MatchDirector match = (MatchDirector) HgGame.Manager().addAndGetDirector(DirectorTypes.MatchDirector);
            match.startAsClient();
            toBeDestroyed = true;
        }

        if (waitDuration > 0) waitDuration--;

        if (!tryClientConnect && waitDuration <= 0 && menus.getCurrentState().equals("ClientConnect")) {
            menus.scheduleSwitchState("StartClientMenu");
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
}
