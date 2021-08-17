package me.kuuds.kmqtt.common.state;

/**
 * State Machine
 *
 * @author kuuds
 */
public interface StateMachine<S extends Enum<S>> {

    /**
     * get current state
     *
     * @return state
     */
    S getCurrentState();

    /**
     * do transition
     */
    void doTransition();

}
