package hg.interfaces;

/** ICopy is an interface that defines a method for deep copying objects,
 * because being bound by Cloneable's crap is not fun. */
public interface ICopy {
    ICopy copy();
}
