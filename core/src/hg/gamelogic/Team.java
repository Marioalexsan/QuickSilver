package hg.gamelogic;

import hg.entities.Entity;
import hg.entities.Player;

import java.util.ArrayList;

public class Team {
    public final ArrayList<Entity> players = new ArrayList<>();
    public final String teamName = "The Dudes";
    public int teamScore = 0;
}
