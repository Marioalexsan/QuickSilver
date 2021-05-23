package hg.networking.packets;

import hg.directors.GameSession;
import hg.directors.MainMenu;
import hg.enums.types.DirectorType;
import hg.enums.types.TargetType;
import hg.game.GameManager;
import hg.game.HgGame;
import hg.networking.Packet;
import hg.networking.PlayerView;

import java.util.ArrayList;

/** Server message that initializes a client. */
public class ClientInitResponse extends Packet {
    public int clientViewID;
    public PlayerView[] allViews;
    public GameSession.SessionOptions sessionOptions;
    public EntityAdded[] currentEntities;
    public boolean matchStarted;

    public void copyServerEntities() {
        GameManager manager = HgGame.Manager();
        ArrayList<EntityAdded> entityAdded = new ArrayList<>();
        for (var actor: manager.getAllActors()) {
            entityAdded.add(new EntityAdded(TargetType.Actors, actor.getType(), actor.getID(), actor.getPosition().x, actor.getPosition().y, actor.getAngle().getDeg()));
        }
        currentEntities = entityAdded.toArray(new EntityAdded[0]);
    }

    public void copyServerEntitiesExcept(int ID) {
        GameManager manager = HgGame.Manager();
        ArrayList<EntityAdded> entityAdded = new ArrayList<>();
        for (var actor: manager.getAllActors()) {
            if (actor.getID() == ID) continue;
            entityAdded.add(new EntityAdded(TargetType.Actors, actor.getType(), actor.getID(), actor.getPosition().x, actor.getPosition().y, actor.getAngle().getDeg()));
        }
        currentEntities = entityAdded.toArray(new EntityAdded[0]);
    }

    private void addEntitiesToManager() {
        if (currentEntities != null) {
            for (var entityAdded: currentEntities) {
                entityAdded.parseOnClient();
            }
        }
    }

    public ClientInitResponse(int clientViewID, PlayerView[] allViews, GameSession.SessionOptions sessionOptions, boolean matchStarted) {
        this.clientViewID = clientViewID;
        this.allViews = allViews;
        this.sessionOptions = sessionOptions;
        this.matchStarted = matchStarted;
    }

    @Override
    public void parseOnClient() {
        GameManager manager = HgGame.Manager();

        for (var view : allViews) {
            manager.addPlayerView(view);
            if (view.uniqueID == clientViewID)
                manager.localView = view;
        }

        addEntitiesToManager();

        MainMenu main = (MainMenu) HgGame.Manager().getDirector(DirectorType.MainMenu);
        if (main != null) {
            main.receiveOptions(sessionOptions);
            if (matchStarted) main.signalEarlyStart();
        }

        manager.onInitializedByServer();
    }

    public ClientInitResponse() {} // For Kryonet
}
