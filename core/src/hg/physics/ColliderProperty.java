package hg.physics;

public enum ColliderProperty {
    CollideNotify, // If both A and B have this, perform pushback and notification
    Notify, // If A has this, notify if it collides with B
    DoNothing // If one collider has this property, A and B do not interact
}
