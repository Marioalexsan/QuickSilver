package hg.utils;

import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Vector2;
import hg.drawables.Drawable;

import java.util.Comparator;

public class GFXTools {

    /** Comparator for sorting drawables by height */
    public static final Comparator<Drawable> DrawableLayerComparator = Comparator.comparingInt(Drawable::getLayer);

    /** Calculates an Affine2 transform from the given position, center, angle, and their respective offsets. */
    public static Affine2 GetAffineForPCAO(Vector2 position, Vector2 center, Angle angle,
                                           Vector2 pOffset, Vector2 cOffset, Angle aOffset) {
        return new Affine2()
                .translate(new Vector2(position).add(pOffset))
                .rotate(angle.getDeg() + aOffset.getDeg())
                .translate(new Vector2(center).add(cOffset).scl(-1));
    }

}
