package it.polimi.ingsw.model.data;

import java.time.LocalTime;

public class Message extends Data{
    private final LocalTime timestamp;
    private final String author;
    private final String text;

    public Message(LocalTime timestamp, String author, String text) {
        this.timestamp = timestamp;
        this.author = author;
        this.text = text;
    }

    public LocalTime getTimestamp() {
        return timestamp;
    }

    public String getAuthor() {
        return author;
    }

    public String getText() {
        return text;
    }
}
