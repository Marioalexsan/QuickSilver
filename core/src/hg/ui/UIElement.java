package hg.ui;

import hg.engine.MappedAction;
import hg.interfaces.IDestroyable;

public abstract class UIElement implements IDestroyable {
    void onLMBDown(float x, float y) {}
    void onLMBUp(float x, float y) {}
    void onLMBDragged(float x, float y) {}
    void onMouseMove(float dx, float dy) {}
    void onUpdate() {}
}
