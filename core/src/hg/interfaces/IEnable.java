package hg.interfaces;

/** Basic interface for things that can be enabled / disabled */
public interface IEnable {
    void setEnabled(boolean enabled);
    boolean isActive();
}
