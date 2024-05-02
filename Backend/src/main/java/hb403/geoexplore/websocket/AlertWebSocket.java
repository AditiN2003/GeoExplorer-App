package hb403.geoexplore.websocket;

import java.io.IOException;
import java.util.*;

import hb403.geoexplore.datatype.marker.repository.AlertRepository;
import hb403.geoexplore.UserStorage.entity.User;
import hb403.geoexplore.UserStorage.repository.UserRepository;
import hb403.geoexplore.datatype.marker.AlertMarker;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.*;

import jakarta.websocket.*;
import jakarta.websocket.server.*;


@Controller
@ServerEndpoint(value = "/live/alerts/{user_id}")
public class AlertWebSocket extends WebSocketBase {

	private static UserRepository users_repo;
	private static AlertRepository alert_repo;

	@Autowired
	public void autoAlertRepository(AlertRepository repo) {
		AlertWebSocket.alert_repo = repo;
	}
	@Autowired
	public void autoUsersRepository(UserRepository repo) {
		AlertWebSocket.users_repo = repo;
	}


	protected static Map<Session, Long> session_user_ids = new HashMap<>();
	protected static Map<Long, SessionInfo> user_id_sessions = new HashMap<>();

	public AlertWebSocket() {
		super( "Alert WS", true);
	}

	@Override
	protected UserRepository getUserRepo() {
		return AlertWebSocket.users_repo;
	}
	@Override
	protected Map<Session, Long> getSessionUserIds() {
		return AlertWebSocket.session_user_ids;
	}
	@Override
	protected Map<Long, SessionInfo> getUserIdSessions() {
		return AlertWebSocket.user_id_sessions;
	}


	@OnOpen
	public void onOpen(Session session, @PathParam("user_id") Long id) throws IOException {
		super.onOpenBase(session, id);
	}
	@OnMessage
	public void onMessage(Session session, String message) throws IOException {
		// try to parse the message to an alert -- filter by user type so only admins have this ability
		if(message == null) return;
		final User u = super.onMessageVerifySession(session);
		if(u == null) return;
		super.printMessage("Recieved message: " + message);
		if(u.getIsAdmin()) {
			super.printMessage("Recieved message from admin user. Continuing to parse alert...");
		} else {
			super.printMessage("Recieved message from non-admin user. Discontinuing parsing...");
			return;
		}
		final ObjectMapper mapper = new ObjectMapper();
		String out_message;
		try {
			AlertMarker entity = mapper.readValue(message, AlertMarker.class);
			entity.enforceLocationIO();
			entity.nullifyId();	// only allow new entries for WS
			entity.applyNewTimestamp();
			entity = alert_repo.saveAndFlush(entity);
			entity.enforceLocationTable();
			out_message = mapper.writeValueAsString(entity);
		} catch(Exception e) {	// hard fail - don't continue
			super.printMessage("Failed to parse, save, or serialize alert message.");
			return;
		}
		super.broadcastAll(out_message, session, false);
	}
	@OnClose
	public void onClose(Session session) throws IOException {
		super.onCloseBase(session);
	}
	@OnError
	public void onError(Session session, Throwable throwable) {
		super.onErrorBase(session, throwable);
	}


}
