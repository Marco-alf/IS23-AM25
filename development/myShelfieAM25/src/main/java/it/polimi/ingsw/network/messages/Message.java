package it.polimi.ingsw.network.messages;


import java.io.Serializable;

public class Message implements Serializable {

    protected String type;
    static final long serialVersionUID = 1L;
    public String getType() {
        return "Message";
    }
}
