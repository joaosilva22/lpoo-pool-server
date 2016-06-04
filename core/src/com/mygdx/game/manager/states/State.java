package com.mygdx.game.manager.states;

import com.mygdx.game.network.Message;

/**
 * Created by joaopsilva on 04-06-2016.
 */
public interface State<E> {
    void enter(E entity);

    void update(E entity, float delta);

    void exit(E entity);

    void handleMessage(E entity, Message message);

}
