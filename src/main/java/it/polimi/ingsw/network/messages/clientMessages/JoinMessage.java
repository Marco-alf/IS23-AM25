package it.polimi.ingsw.network.messages.clientMessages;

public class JoinMessage extends ClientMessage{
    @Override
    public String getType() {
        return "JoinMessage";
    }
    private String name;
    private String lobbyName;

    public void setLobbyName(String lobbyName) {
        this.lobbyName = lobbyName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLobbyName() {
        return lobbyName;
    }

    public String getName() {
        return name;
    }
}
