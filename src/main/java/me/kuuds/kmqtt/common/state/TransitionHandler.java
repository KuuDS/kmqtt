package me.kuuds.kmqtt.common.state;

@FunctionalInterface
public interface TransitionHandler<E extends Event> {

    void transit(E e);

}