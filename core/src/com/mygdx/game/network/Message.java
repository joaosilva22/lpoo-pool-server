package com.mygdx.game.network;

import com.badlogic.gdx.utils.Json;

import java.util.ArrayList;

/**
 * Created by joaopsilva on 04-06-2016.
 */
public class Message {
    private String tag;
    ArrayList<MessageArgument> args;

    /**
     * Creates a Message object.
     */
    public Message() {
        tag = null;
        args = new ArrayList<MessageArgument>();
    }

    /**
     * Creates a message object with the specified tag.
     * @param tag The message's tag.
     */
    public Message(String tag) {
        this.tag = tag;
        args = new ArrayList<MessageArgument>();
    }

    /**
     * Adds an argument to the message.
     * @param name The name of the argument.
     * @param value The value of the argument.
     */
    public void addArgument(String name, Object value) {
        args.add(new MessageArgument(name, value));
    }

    /**
     * Converts message to JSON format.
     * @return Message in JSON format.
     */
    public String toJson() {
        Json json = new Json();
        return json.toJson(this);
    }

    /**
     * Returns the message tag.
     * @return The message tag.
     */
    public String getTag() {
        return tag;
    }

    /**
     * Returns the message argument list.
     * @return The message arguments.
     */
    public ArrayList<MessageArgument> getArgs() {
        return args;
    }

    /**
     * Returns the value of the argument with the given name.
     * @param name The argument's name.
     * @return The argument's value.
     */
    public Object getValue(String name) {
        for (MessageArgument arg : args)
            if (arg.getName().equals(name))
                return arg.getValue();
        return null;
    }

    static private class MessageArgument {
        private String name;
        private Object value;

        /**
         * Creates a MessageArgument object.
         */
        public MessageArgument() {
            name = null;
            value = null;
        }

        /**
         * Creates a MessageArgument object with the given name and value.
         * @param name The message argument's name.
         * @param value The message argument's value.
         */
        public MessageArgument(String name, Object value) {
            this.name = name;
            this.value = value;
        }

        /**
         * Returns the argument's name.
         * @return The argument's name.
         */
        public String getName() {
            return name;
        }

        /**
         * Returns the argument's value.
         * @return The argument's value.
         */
        public Object getValue() {
            return value;
        }
    }
}
