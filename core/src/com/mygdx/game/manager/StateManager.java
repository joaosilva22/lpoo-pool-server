package com.mygdx.game.manager;

import com.mygdx.game.manager.states.State;
import com.mygdx.game.network.Message;

/**
 * Created by joaopsilva on 04-06-2016.
 */
// TODO: remover as dependencies do build.gradle
public class StateManager<E, S extends State<E>> {
    private E owner;
    private S state;

    /**
     * Creates a StateManager object.
     * Works as a state machine, to control an object's behaviour.
     * @param owner The owner object of this state manager.
     * @param state The initial state.
     */
    public StateManager(E owner, S state) {
        this.owner = owner;
        this.state = state;
        this.state.enter(this.owner);
    }


    /**
     * Sets the the manager's state.
     * @param state The new state.
     */
    public void setState(S state) {
        this.state.exit(owner);
        this.state = state;
        this.state.enter(owner);
    }

    /**
     * Updates the current state.
     * @param delta The time interval between update calls.
     */
    public void update(float delta) {
        state.update(owner, delta);
    }

    /**
     * Handles incoming messages.
     * @param message The message to handle.
     */
    public void handleMessage(Message message) {
        state.handleMessage(owner, message);
    }

    /**
     * Returns the current state.
     * @return The current state.
     */
    public S getState() {
        return state;
    }
}
