package hg.gamelogic.states;

/** Base class for all structures which deal with saving / restoring entity / game / whatever state.
 * Ideally, State should only hold primitive data, since State objects may also be sent over the network.
 * States should be registered in NetworkHelper if sent over the network, along with any other non-primitive objects held. */
public abstract class State {
}
