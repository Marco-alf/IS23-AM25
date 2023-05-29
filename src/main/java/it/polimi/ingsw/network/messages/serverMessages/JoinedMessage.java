package it.polimi.ingsw.network.messages.serverMessages;

public class JoinedMessage extends ServerMessage{
    private String type = "JoinedMessage";
    private String name;
    private String lobbyName;

    @Override
    public String getType() {
        return type;
    }

    public String getLobbyName() {
        return lobbyName;
    }

    public String getName() {
        return name;
    }

    public void setLobbyName(String lobbyName) {
        this.lobbyName = lobbyName;
    }

    public void setName(String name) {
        this.name = name;
    }
}
