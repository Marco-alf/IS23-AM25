package it.polimi.ingsw.view;

import it.polimi.ingsw.model.data.GameInfo;
import it.polimi.ingsw.network.messages.serverMessages.*;

import java.util.List;

/**
 * ViewInterface is the interface that every view need to implement in order to be compatible with the model and the controller.
 * All the following methods needs to be implemented accordingly to the server expected behaviour. This means that wrong
 * implementation of the following methods may result in bugs on the client side.
 */
public interface ViewInterface {
    /**
     receiveCreatedLobbyMsg is a method used to manage the response of the view to a CreatedLobbyMessage.
     @param msg the CreatedLobbyMessage received
     */
    void receiveCreatedLobbyMsg(CreatedLobbyMessage msg);

    /**
     receiveJoinedMsg is a method used to manage the response of the view to a JoinedMessage.
     @param msg the JoinedMessage received
     */
    void receiveJoinedMsg(JoinedMessage msg);

    /**
     receiveExistingLobbyMsg is a method used to manage the response of the view to an ExistingLobbyMessage.
     @param msg the ExistingLobbyMessage received
     */
    void receiveExistingLobbyMsg(ExistingLobbyMessage msg);

    /**
     receiveLobbyNotCreatedMsg is a method used to manage the response of the view to a LobbyNotCreatedMessage.
     @param msg the LobbyNotCreatedMessage received
     */
    void receiveLobbyNotCreatedMsg(LobbyNotCreatedMessage msg);

    /**
     receiveNameTakenMsg is a method used to manage the response of the view to a NameTakenMessage.
     @param msg the NameTakenMessage received
     */
    void receiveNameTakenMsg(NameTakenMessage msg);

    /**
     receiveNotExistingLobbyMsg is a method used to manage the response of the view to a NotExistingLobbyMessage.
     @param msg the NotExistingLobbyMessage received
     */
    void receiveNotExistingLobbyMsg(NotExistingLobbyMessage msg);

    /**
     receiveFullLobbyMsg is a method used to manage the response of the view to a FullLobbyMessage.
     @param msg the FullLobbyMessage received
     */
    void receiveFullLobbyMsg(FullLobbyMessage msg);

    /**
     receiveRetrievedLobbiesMsg is a method used to manage the response of the view to a RetrievedLobbiesMessage.
     @param msg the RetrievedLobbiesMessage received
     */
    void receiveRetrievedLobbiesMsg(RetrievedLobbiesMessage msg);

    /**
     receiveChatUpdateMsg is a method used to manage the response of the view to a ChatUpdateMessage.
     @param msg the ChatUpdateMessage received
     */
    void receiveChatUpdateMsg(ChatUpdateMessage msg);

    /**
     receivePrivateChatUpdateMsg is a method used to manage the response of the view to a PrivateChatUpdateMessage.
     @param msg the PrivateChatUpdateMessage received
     */
    void receivePrivateChatUpdateMsg(PrivateChatUpdateMessage msg);

    /**
     receiveGameCreatedMsg is a method used to manage the response of the view to a GameCreatedMessage.
     @param msg the GameCreatedMessage received
     */
    void receiveGameCreatedMsg(GameCreatedMessage msg);

    /**
     receiveGameUpdatedMsg is a method used to manage the response of the view to a GameUpdatedMessage.
     @param msg the GameUpdatedMessage received
     */
    void receiveGameUpdatedMsg(GameUpdatedMessage msg);

    /**
     receiveGameEndedMsg is a method used to manage the response of the view to a GameEndedMessage.
     @param msg the GameEndedMessage received
     */
    void receiveGameEndedMsg(GameEndedMessage msg);

    /**
     receiveUpdatedPlayerMsg is a method used to manage the response of the view to an UpdatedPlayerMessage.
     @param msg the UpdatedPlayerMessage received
     */
    void receiveUpdatedPlayerMsg(UpdatedPlayerMessage msg);

    /**
     receiveInvalidMoveMsg is a method used to manage the response of the view to an InvalidMoveMessage.
     @param msg the InvalidMoveMessage received
     */
    void receiveInvalidMoveMsg(InvalidMoveMessage msg);

    /**
     receiveInsufficientPlayersMsg is a method used to manage the response of the view to an InsufficientPlayersMessage.
     @param msg the InsufficientPlayersMessage received
     */
    void receiveInsufficientPlayersMsg(InsufficientPlayersMessage msg);

    /**
     receiveLobbyClosedMsg is a method used to manage the response of the view to a LobbyClosedMessage.
     @param msg the LobbyClosedMessage received
     */
    void receiveLobbyClosedMsg(LobbyClosedMessage msg);

    /**
     receiveUserDisconnectedMsg is a method used to manage the response of the view to a UserDisconnectedMessage.
     @param msg the UserDisconnectedMessage received
     */
    void receiveUserDisconnectedMsg(UserDisconnectedMessage msg);

    /**
     receiveInvalidCommandMsg is a method used to manage the response of the view to an InvalidCommandMessage.
     @param msg the InvalidCommandMessage received
     */
    void receiveInvalidCommandMsg(InvalidCommandMessage msg);

    /**
     receiveConnectionErrorMsg is a method used to manage the response of the view to a ConnectionErrorMessage.
     @param msg the ConnectionErrorMessage received
     */
    void receiveConnectionErrorMsg(ConnectionErrorMessage msg);

    /**
     receiveInvalidLobbyNameMsg is a method used to manage the response of the view to an InvalidLobbyNameMessage.
     @param msg the InvalidLobbyNameMessage received
     */
    void receiveInvalidLobbyNameMsg(InvalidLobbyNameMessage msg);

    /**
     receiveIllegalPlayerNameMsg is a method used to manage the response of the view to an IllegalPlayerNameMessage.
     @param msg the IllegalPlayerNameMessage received
     */
    void receiveIllegalPlayerNameMsg(IllegalPlayerNameMessage msg);
}
