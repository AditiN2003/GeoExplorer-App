package hb403.geoexplore.websocket;

import java.io.IOException;
import java.util.*;

import hb403.geoexplore.UserStorage.LocationSharing;
import hb403.geoexplore.UserStorage.entity.*;
import hb403.geoexplore.UserStorage.repository.*;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.*;

import jakarta.websocket.*;
import jakarta.websocket.server.*;


@Controller
@ServerEndpoint(value = "/live/location/{user_id}")
public class LocationWebSocket extends WebSocketBase {
	
	private static UserRepository users_repo;
	// private static UserGroupRepository user_groups_repo;

	// @Autowired
	// public void autoUserGroupsRepository(UserGroupRepository repo) {
	// 	LocationWebSocket.user_groups_repo = repo;
	// }
	@Autowired
	public void autoUsersRepository(UserRepository repo) {
		LocationWebSocket.users_repo = repo;
	}


	protected static Map<Session, Long> session_user_ids = new HashMap<>();
	protected static Map<Long, SessionInfo> user_id_sessions = new HashMap<>();

	public LocationWebSocket() {
		super("Location WS", true);
	}

	@Override
	protected UserRepository getUserRepo() {
		return LocationWebSocket.users_repo;
	}
	@Override
	protected Map<Session, Long> getSessionUserIds() {
		return LocationWebSocket.session_user_ids;
	}
	@Override
	protected Map<Long, SessionInfo> getUserIdSessions() {
		return LocationWebSocket.user_id_sessions;
	}


	@OnOpen
	public void onOpen(Session session, @PathParam("user_id") Long id) throws IOException {
		super.onOpenBase(session, id);
	}
	@OnMessage
	public void onMessage(Session session, String message) throws IOException {
		if(message == null) return;
		final User u = super.onMessageVerifySession(session);
		if(u == null) return;
		super.printMessage("Recieved message: " + message);
		final LocationSharing p = u.getLocation_privacy();
		if(p.value > 0) {
			super.printMessage(String.format("Recieved message from user sharing their location (%s). Continueing to parse...", p.toString()));
		} else {
			super.printMessage(String.format("Recieved message from user NOT sharing their location (%s). Discontinuing parsing...", p.toString()));
			return;
		}
		final ObjectMapper mapper = new ObjectMapper();
		final JsonNode node = mapper.readTree(message);
		Double lat, lon;
		Boolean emergency = false;
		try {
			lat = node.get("latitude").asDouble();
			lon = node.get("longitude").asDouble();
		} catch(Exception e) {
			super.printMessage("Failed to parse latitude or longitude from message.");
			return;
		}
		try{ emergency = node.get("emergency").asBoolean(); } catch(Exception e) {}
		if(lat != null && lon != null) {
			u.setIo_latitude(lat);
			u.setIo_longitude(lon);
			u.enforceLocationIO();
			LocationWebSocket.users_repo.save(u);
			final Long src_id = u.getId();
			final LocationSharing share_level = emergency ? LocationSharing.EMERGENCY : u.getLocation_privacy();	// json param overrides static config
			final String
				out_message_f = String.format(
					"{ \"user_id\": %d, \"latitude\":%f, \"longitude\":%f, \"emergency\":%%b, \"group_id\":%%d }",
					src_id, lat, lon
				);
			final StringBuilder b = new StringBuilder(this.formatMessage("Successfully sent message to user ids: {\n"));
			switch(share_level) {
				case EMERGENCY: {
					final String out_message = String.format(out_message_f, true, -1);
					// broadcast to any connected admins
					final List<User> admins = LocationWebSocket.users_repo.findSubsetAdminUsers(user_id_sessions.keySet());
					for(final User a : admins) {
						try {
							final Long _id = a.getId();
							this.broadcastHandleDuplicates(session, _id, user_id_sessions.get(_id), out_message, b, false);
						} catch(Exception e) {

						}
					}
					System.out.println(b.append("}\t<< EMERGENCY MESSAGE <<"));
					break;
				}
				case GROUP: {
					// broadcast to all users that are in groups that the user is in and have sharing enabled
					final Map<Long, Long> unique_output_ids = new HashMap<>();
					for(final UserGroup g : u.getGroups()) {
						Boolean share = g.getShare_locations();
						if(share != null && share) {
							for(final User _u : g.getMembers()) {
								final Long _id = _u.getId();
								if(user_id_sessions.containsKey(_id)) {
									unique_output_ids.put(_id, g.getId());	// in the case that the source user is in two or more groups with another user, this will not be "completely correct" but rather refer to the last searched group id
								}
							}
						}
					}
					unique_output_ids.forEach(
						(Long uid, Long gid)->{
							if(uid == src_id) return;
							try {
								this.broadcastHandleDuplicates(session, uid, user_id_sessions.get(uid), String.format(out_message_f, false, gid), b, false);
							} catch(Exception e) {
	
							}
						}
					);
					System.out.println(b.append("}\t << GROUP-ONLY MESSAGE <<"));
					break;
				}
				case PUBLIC: {
					// broadcast to all connected sessions
					super.broadcastAll(String.format(out_message_f, false, -1), session, false);
					break;
				}
				case DISABLED:
				default:
				{
					super.printMessage("Disbanned message broadcase due to user privacy settings!\n");
				}
			}
		}

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
