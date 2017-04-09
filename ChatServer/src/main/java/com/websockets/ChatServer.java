package com.websockets;

import com.websockets.data.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.server.ServerEndpointConfig;
import java.io.IOException;
import java.util.*;

@Service
@ServerEndpoint(
        value = "/chat",
        subprotocols = {"chat"},
        decoders = {ChatDecoder.class},
        encoders = {ChatEncoder.class},
        configurator = ChatServerConfigurator.class
)
public class ChatServer {
    private static Logger LOG = LoggerFactory.getLogger(ChatServer.class);

    private static final String USERNAME_KEY = "username";
    private static final String USERNAMES_KEY = "usernames";

    private Session session;
    private static final Set<Session> sessions = Collections.synchronizedSet(new HashSet<Session>());

    private ServerEndpointConfig endpointConfig;
    private Transcript transcript;

    @OnOpen
    public void startChatChannel(EndpointConfig config, Session session) {
        endpointConfig = (ServerEndpointConfig) config;
        ChatServerConfigurator chatServerConfigurator =
                (ChatServerConfigurator) endpointConfig.getConfigurator();
        transcript = chatServerConfigurator.getTranscript();
        this.session = session;
        sessions.add(session);
    }

    @OnMessage
    public void handleChatMessage(ChatMessage message) {
        switch (message.getType()) {
            case NewUserMessage.USERNAME_MESSAGE:
                this.processNewUser((NewUserMessage) message);
                break;
            case ChatMessage.CHAT_DATA_MESSAGE:
                this.processChatUpdate((ChatUpdateMessage) message);
                break;
            case ChatMessage.SIGNOFF_REQUEST:
                this.processSignoffRequest((UserSignoffMessage) message);
                break;
        }
    }

    @OnError
    public void errorHandle(Throwable err) {
        LOG.error("Error in processing WebSocket request!", err);
    }

    @OnClose
    public void endChatChannel() {
        if (this.getCurrentUsername() != null) {
            this.removeUser();
            this.addMessage(" just left...without even signing out !");
        }
    }

    private void processNewUser(NewUserMessage message) {
        String newUsername = this.validateUsername(message.getUsername());
        NewUserMessage newUserMessage = new NewUserMessage(newUsername);

        try {
            session.getBasicRemote().sendObject(newUserMessage);
        } catch (EncodeException | IOException e) {
            LOG.error("Error signing " + message.getUsername() + " into chat: " + e.getMessage());
        }

        this.registerUser(newUsername);
        this.broadcastUserListUpdate();
        this.addMessage(" just joined.");
    }

    private void processChatUpdate(ChatUpdateMessage message) {
        this.addMessage(message.getMessage());
    }

    private void processSignoffRequest(UserSignoffMessage message) {
        this.addMessage(" just left.");
        this.removeUser();
    }

    private String getCurrentUsername() {
        return (String) session.getUserProperties().get(USERNAME_KEY);
    }

    private void registerUser(String username) {
        session.getUserProperties().put(USERNAME_KEY, username);
        this.updateUserList();
    }

    private void updateUserList() {
        List<String> usernames = new ArrayList<>();

        for (Session s : sessions) {
            String username = (String) s.getUserProperties().get(USERNAME_KEY);
            usernames.add(username);
        }

        this.endpointConfig.getUserProperties().put(USERNAMES_KEY, usernames);
    }


    @SuppressWarnings("unchecked")
    private List<String> getUserList() {
        List<String> userList = (List<String>) this.endpointConfig.getUserProperties().get(USERNAMES_KEY);
        return (userList == null) ? Collections.emptyList() : userList;
    }

    private String validateUsername(String newUsername) {
        if (this.getUserList().contains(newUsername)) {
            return this.validateUsername(newUsername + "1");
        }

        return newUsername;
    }

    private void broadcastUserListUpdate() {
        UserListUpdateMessage userListUpdateMessage = new UserListUpdateMessage(this.getUserList());

        for (Session nextSession : sessions) {
            try {
                if (nextSession.isOpen()) {
                    nextSession.getBasicRemote().sendObject(userListUpdateMessage);
                }
            } catch (EncodeException | IOException e) {
                LOG.error("Error updating a client: " + e.getMessage(), e);
                sessions.remove(nextSession);
            }
        }
    }

    private void removeUser() {
        try {
            this.updateUserList();
            this.broadcastUserListUpdate();
            this.session.getUserProperties();
            this.session.getUserProperties().remove(USERNAME_KEY);
            this.session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "User logged off"));
            sessions.remove(session);
        } catch (IOException e) {
            LOG.error("Error removing user", e);
        }
    }

    private void broadcastTranscriptUpdate() {
        for (Session nextSession : sessions) {
            ChatUpdateMessage chatUpdateMessage = new ChatUpdateMessage(
                    this.transcript.getLastUsername(),
                    this.transcript.getLastMessage()
            );

            try {
                nextSession.getBasicRemote().sendObject(chatUpdateMessage);
            } catch (EncodeException | IOException e) {
                LOG.error("Error updating a client: " + e.getMessage(), e);
                sessions.remove(session);
            }
        }
    }

    private void addMessage(String message) {
        this.transcript.addEntry(this.getCurrentUsername(), message);
        this.broadcastTranscriptUpdate();
    }
}
