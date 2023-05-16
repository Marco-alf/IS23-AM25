package it.polimi.ingsw.view;

import it.polimi.ingsw.model.data.GameInfo;
import it.polimi.ingsw.network.messages.serverMessages.*;

import java.util.List;

public interface ViewInterface {
    void receiveCreatedLobbyMsg (CreatedLobbyMessage msg);

    void receiveJoinedMsg (JoinedMessage msg);

    void receiveExistingLobbyMsg (ExistingLobbyMessage msg);

    void receiveLobbyNotCreatedMsg (LobbyNotCreatedMessage msg);

    void receiveNameTakenMsg (NameTakenMessage msg);

    void receiveNotExistingLobbyMsg (NotExistingLobbyMessage msg);

    void receiveFullLobbyMsg(FullLobbyMessage msg);

    void receiveRetrievedLobbiesMsg (RetrievedLobbiesMessage msg);

    void receiveChatUpdateMsg (ChatUpdateMessage msg);

    void receiveGameCreatedMsg (GameCreatedMessage msg);

    void receiveGameUpdatedMsg (GameUpdatedMessage msg);

    void receiveUpdatedPlayerMsg (UpdatedPlayerMessage msg);

    void receiveInvalidMoveMsg (InvalidMoveMessage msg);

    void receiveInsufficientPlayersMsg (InsufficientPlayersMessage msg);

    void receiveLobbyClosedMsg (LobbyClosedMessage msg);

    void receiveUserDisconnectedMsg (UserDisconnectedMessage msg);

    void receiveInvalidCommandMsg (InvalidCommandMessage msg);

    void receiveConnectionErrorMsg(ConnectionErrorMessage msg);
}
