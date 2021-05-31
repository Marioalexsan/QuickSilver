package hg.libraries;

import hg.engine.AssetEngine;
import hg.enums.HPos;
import hg.enums.VPos;
import hg.game.HgGame;
import hg.utils.builders.*;

/** BuilderLibary houses a collection of builders used throughout the game. */
public class BuilderLibrary {
    public static BasicTextBuilder BasicTextBuilders(String which) {
        AssetEngine assets = HgGame.Assets();
        BasicTextBuilder builder;
        switch (which) {
            case "tinylabel" -> builder = new BasicTextBuilder().font(assets.loadFont("Assets/Fonts/CourierNew24.fnt")).textPos(HPos.Center, VPos.Center);
            case "smalllabel" -> builder = new BasicTextBuilder().font(assets.loadFont("Assets/Fonts/CourierNew36.fnt")).textPos(HPos.Center, VPos.Center);
            case "label" -> builder = new BasicTextBuilder().font(assets.loadFont("Assets/Fonts/CourierNew72.fnt")).textPos(HPos.Center, VPos.Center);
            case "title" -> builder = new BasicTextBuilder().font(assets.loadFont("Assets/Fonts/CourierNew144.fnt")).textPos(HPos.Center, VPos.Center);
            default -> builder = new BasicTextBuilder(); // Failsafe case
        }
        return builder;
    }

    public static ClickButtonBuilder ClickButtonBuilders(String which) {
        AssetEngine assets = HgGame.Assets();
        ClickButtonBuilder builder;
        switch (which) {
            case "silverbox" -> builder = new ClickButtonBuilder().display(assets.loadTexture("Assets/GUI/Button.png")).font(assets.loadFont("Assets/Fonts/CourierNew48.fnt")).clickArea(460, 150);
            case "silverboxsmall" -> builder = new ClickButtonBuilder().display(assets.loadTexture("Assets/GUI/ButtonSmall.png")).font(assets.loadFont("Assets/Fonts/CourierNew48.fnt")).clickArea(460, 100);
            case "silverboxsmallshort" -> builder = new ClickButtonBuilder().display(assets.loadTexture("Assets/GUI/ButtonSmallShort.png")).font(assets.loadFont("Assets/Fonts/CourierNew48.fnt")).clickArea(330, 100);
            case "leftarrow" -> builder = new ClickButtonBuilder().display(assets.loadTexture("Assets/GUI/ArrowButtonLeft.png")).clickArea(80, 80);
            case "leftarrowdisabled" -> builder = new ClickButtonBuilder().display(assets.loadTexture("Assets/GUI/ArrowButtonLeftUnclickable.png")).clickArea(80, 80);
            default -> builder = new ClickButtonBuilder(); // Failsafe case
        }
        return builder;
    }

    public static ToggleButtonBuilder ToggleButtonBuilders(String which) {
        AssetEngine assets = HgGame.Assets();
        ToggleButtonBuilder builder;
        switch (which) {
            case "silvercheck" -> builder = new ToggleButtonBuilder().display(assets.loadTexture("Assets/GUI/ToggleInactive.png"), assets.loadTexture("Assets/GUI/ToggleActive.png")).clickArea(80, 80);
            case "silverdisabled" -> builder = new ToggleButtonBuilder().display(assets.loadTexture("Assets/GUI/ToggleInactiveUnclickable.png"), assets.loadTexture("Assets/GUI/ToggleActiveUnclickable.png")).clickArea(80, 80);
            default -> builder = new ToggleButtonBuilder(); // Failsafe case
        }
        return builder;
    }

    public static TextInputBuilder TextInputBuilders(String which) {
        AssetEngine assets = HgGame.Assets();
        TextInputBuilder builder;
        switch (which) {
            case "default" -> builder = new TextInputBuilder().font(assets.loadFont("Assets/Fonts/CourierNew48.fnt")).emptyText("Enter IP...").maxChars(32).clickArea(350, 48);
            case "username" -> builder = new TextInputBuilder().font(assets.loadFont("Assets/Fonts/CourierNew72.fnt")).emptyText("Enter IP...").maxChars(32).clickArea(400, 72);
            default -> builder = new TextInputBuilder(); // Failsafe case
        }
        return builder;
    }
}
