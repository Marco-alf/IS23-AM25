package it.polimi.ingsw.model;

import it.polimi.ingsw.model.data.Message;

import java.util.ArrayList;
import java.util.List;

public class Chat {
    private final List<Message> history;
    public Chat() {
        history = new ArrayList<>();
    }

    public List<Message> getHistory(){
        List<Message> historyCopy = new ArrayList<>();
        for(Message m: history) historyCopy.add(m);
        return historyCopy;
    }
    public void updateData(Message m){
        history.add(m);
    }
}
