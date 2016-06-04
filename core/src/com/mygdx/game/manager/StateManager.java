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

    public StateManager(E owner, S state) {
        this.owner = owner;
        this.state = state;
        this.state.enter(this.owner);
    }

    public void setState(S state) {
        this.state.exit(owner);
        this.state = state;
        this.state.enter(owner);
    }

    public void update(float delta) {
        state.update(owner, delta);
    }

    public void handleMessage(Message message) {
        state.handleMessage(owner, message);
    }

    public S getState() {
        return state;
    }
}
