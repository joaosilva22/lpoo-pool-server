package com.mygdx.game.manager.states;

import com.mygdx.game.network.Message;

/**
 * Created by joaopsilva on 04-06-2016.
 */
public interface State<E> {
    /**
     * Executes only once on state entry.
     * @param entity The object associated with the state.
     */
    void enter(E entity);

    /**
     * Updates the current state.
     * @param entity The object associated with the state.
     * @param delta The time interval between update calls.
     */
    void update(E entity, float delta);

    /**
     * Executes once when the state exits.
     * @param entity The object associated with the state.
     */
    void exit(E entity);

    /**
     * Handles incoming messages.
     * @param entity The object associated with the state.
     * @param message The message to handle.
     */
    void handleMessage(E entity, Message message);

}
