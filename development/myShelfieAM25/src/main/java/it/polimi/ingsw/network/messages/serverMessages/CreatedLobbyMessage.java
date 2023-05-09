package it.polimi.ingsw.network.messages.serverMessages;

public class CreatedLobbyMessage extends ServerMessage {
    @Override
    public String getType() {
        return "CreatedLobbyMessage";
    }
    private String name;
    private String lobbyName;

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
