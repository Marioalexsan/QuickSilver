package hg.libraries;

import com.badlogic.gdx.math.Vector2;
import hg.drawables.BasicSprite;
import hg.drawables.DrawLayer;
import hg.drawables.Drawable;
import hg.engine.AssetEngine;
import hg.entities.Entity;
import hg.entities.Environment;
import hg.game.HgGame;
import hg.types.EnvType;
import hg.physics.*;
import hg.utils.Angle;

import java.util.LinkedList;

/**
 * Holds descriptions of entities that are related to the world / map.
 */
public class EnvironmentLibrary {
    public static final String envPath = "Assets/Sprites/Environment/";

    public static Entity CreateEnvironment(int ID) {
        AssetEngine assets = HgGame.Assets(); // Extra dependency

        LinkedList<Drawable> drawableList = new LinkedList<>();
        LinkedList<Collider> colliderList = new LinkedList<>();

        // Colliders will automatically be made heavy by the constructor!
        switch(ID) {
            case EnvType.BrickDefault -> {
                BasicSprite draw1 = new BasicSprite(assets.loadTexture(envPath + "BrickDefault.png"));
                draw1.centerToRegion();
                draw1.setLayer(DrawLayer.Default);
                drawableList.add(draw1);

                BoxCollider col1 = new BoxCollider(100, 100);
                col1.group = ColliderGroup.Environment;
                colliderList.add(col1);

                return new Environment(drawableList, colliderList);
            }
            case EnvType.BrickHalf -> {
                BasicSprite draw1 = new BasicSprite(assets.loadTexture(envPath + "BrickHalf.png"));
                draw1.centerToRegion();
                draw1.setLayer(DrawLayer.Default);
                drawableList.add(draw1);

                BoxCollider col1 = new BoxCollider(50, 100);
                col1.group = ColliderGroup.Environment;
                colliderList.add(col1);

                return new Environment(drawableList, colliderList);
            }
            case EnvType.BrickSlashedLeft -> {
                BasicSprite draw1 = new BasicSprite(assets.loadTexture(envPath + "BrickSlashed.png"));
                draw1.centerToRegion();
                draw1.setLayer(DrawLayer.Default);
                drawableList.add(draw1);

                RTriangleCollider col1 = new RTriangleCollider(100, 50);
                col1.group = ColliderGroup.Environment;
                colliderList.add(col1);

                return new Environment(drawableList, colliderList);
            }
            case EnvType.BrickSlashedRight -> {
                BasicSprite draw1 = new BasicSprite(assets.loadTexture(envPath + "BrickSlashed.png"));
                draw1.centerToRegion();
                draw1.setLayer(DrawLayer.Default);
                draw1.setMirrored(true);
                drawableList.add(draw1);

                RTriangleCollider col1 = new RTriangleCollider(50, 100);
                col1.setAngleOffset(new Angle(-90f));
                col1.group = ColliderGroup.Environment;
                colliderList.add(col1);

                return new Environment(drawableList, colliderList);
            }
            case EnvType.BrickLShape -> {
                BasicSprite draw1 = new BasicSprite(assets.loadTexture(envPath + "BrickLShape.png"));
                draw1.centerToRegion();
                draw1.setLayer(DrawLayer.Default);
                drawableList.add(draw1);

                BoxCollider col1 = new BoxCollider(50, 50); // NW
                col1.setCenterOffset(new Vector2(25, -25));
                col1.group = ColliderGroup.Environment;
                colliderList.add(col1);
                BoxCollider col2 = new BoxCollider(50, 50); // NE
                col2.setCenterOffset(new Vector2(-25, -25));
                col2.group = ColliderGroup.Environment;
                colliderList.add(col2);
                BoxCollider col3 = new BoxCollider(50, 50); // SW
                col3.setCenterOffset(new Vector2(25, 25));
                col3.group = ColliderGroup.Environment;
                colliderList.add(col3);

                return new Environment(drawableList, colliderList);
            }
            case EnvType.BrickTriangle -> {
                BasicSprite draw1 = new BasicSprite(assets.loadTexture(envPath + "BrickTriangle.png"));
                draw1.centerToRegion();
                draw1.setLayer(DrawLayer.Default);
                drawableList.add(draw1);

                RTriangleCollider col1 = new RTriangleCollider(100, 100);
                col1.group = ColliderGroup.Environment;
                colliderList.add(col1);

                return new Environment(drawableList, colliderList);
            }
            case EnvType.BrickPillarSmall -> {
                BasicSprite draw1 = new BasicSprite(assets.loadTexture(envPath + "BrickPillarSmall.png"));
                draw1.centerToRegion();
                draw1.setLayer(DrawLayer.Default);
                drawableList.add(draw1);

                SphereCollider col1 = new SphereCollider(25);
                col1.group = ColliderGroup.Environment;
                colliderList.add(col1);

                return new Environment(drawableList, colliderList);
            }
            case EnvType.BrickPillarMedium -> {
                BasicSprite draw1 = new BasicSprite(assets.loadTexture(envPath + "BrickPillarMedium.png"));
                draw1.centerToRegion();
                draw1.setLayer(DrawLayer.Default);
                drawableList.add(draw1);

                SphereCollider col1 = new SphereCollider(50);
                col1.group = ColliderGroup.Environment;
                colliderList.add(col1);

                return new Environment(drawableList, colliderList);
            }
            case EnvType.BrickPillarBig -> {
                BasicSprite draw1 = new BasicSprite(assets.loadTexture(envPath + "BrickPillarBig.png"));
                draw1.centerToRegion();
                draw1.setLayer(DrawLayer.Default);
                drawableList.add(draw1);

                SphereCollider col1 = new SphereCollider(100);
                col1.group = ColliderGroup.Environment;
                colliderList.add(col1);

                return new Environment(drawableList, colliderList);
            }
            case EnvType.ConcreteFloor -> {
                BasicSprite draw1 = new BasicSprite(assets.loadTexture(envPath + "ConcreteFloor.png"));
                draw1.centerToRegion();
                draw1.setLayer(DrawLayer.Floor);
                drawableList.add(draw1);

                return new Environment(drawableList, colliderList);
            }
            case EnvType.BoxMedium -> {
                BasicSprite draw1 = new BasicSprite(assets.loadTexture(envPath + "BoxMedium.png"));
                draw1.centerToRegion();
                draw1.setLayer(DrawLayer.Default);
                drawableList.add(draw1);

                BoxCollider col1 = new BoxCollider(80, 80);
                col1.group = ColliderGroup.Environment;
                colliderList.add(col1);

                return new Environment(drawableList, colliderList);
            }
            case EnvType.BoxSmall -> {
                BasicSprite draw1 = new BasicSprite(assets.loadTexture(envPath + "BoxSmall.png"));
                draw1.centerToRegion();
                draw1.setLayer(DrawLayer.Default);
                drawableList.add(draw1);

                BoxCollider col1 = new BoxCollider(60, 60);
                col1.group = ColliderGroup.Environment;
                colliderList.add(col1);

                return new Environment(drawableList, colliderList);
            }
            case EnvType.BrickMetalBars -> {
                BasicSprite draw1 = new BasicSprite(assets.loadTexture(envPath + "MetalBars.png"));
                draw1.centerToRegion();
                draw1.setLayer(DrawLayer.Default);
                drawableList.add(draw1);

                BoxCollider col1 = new BoxCollider(100, 20);
                col1.group = ColliderGroup.Environment_ShootThrough;
                colliderList.add(col1);

                return new Environment(drawableList, colliderList);
            }
            default -> throw new RuntimeException("Tried to create unknown Environment of type " + ID);
        }
    }
}
