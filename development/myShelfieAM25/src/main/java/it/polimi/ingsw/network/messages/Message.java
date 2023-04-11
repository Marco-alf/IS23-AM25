package it.polimi.ingsw.network.messages;


import java.io.Serializable;

/**
 * Message is the standard message that the server sends to a client
 */
public class Message implements Serializable {
    /**
     * Type of the message
     */
    protected String type;
    /**
     * serialVersionUID is the version used by the serialization/deserialization protocol
     */
    static final long serialVersionUID = 1L;

    /**
     * getter of the type of the message
     * @return a string with representing the message class
     */
    public String getType() {
        return "Message";
    }
}
