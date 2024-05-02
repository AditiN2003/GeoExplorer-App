package hb403.geoexplore.websocket;

import java.io.IOException;
import java.util.*;

import hb403.geoexplore.UserStorage.entity.User;
import hb403.geoexplore.UserStorage.repository.UserRepository;

import jakarta.websocket.*;


public abstract class WebSocketBase {

	protected class SessionInfo {
		public Session primary;
		public Set<Session> duplicates;

		public SessionInfo() {}
		public SessionInfo(Session s) { this.primary = s; }

		public int size() {
			return (primary == null ? 0 : 1) + (duplicates == null ? 0 : duplicates.size());
		}
		public int addSession(Session s) {
			return this.addSession(s, false);	// default no duplicates
		}
		public int addSession(Session s, boolean use_duplicates) {
			if(this.primary == null) {
				this.primary = s;
			} else if(use_duplicates) {
				if(this.duplicates == null) this.duplicates = new HashSet<>();
				this.duplicates.add(s);
			}
			return this.size();
		}
		public int removeSession(Session s) {
			if(this.primary == s) {
				this.primary = null;
				if(this.duplicates != null) {
					for(final Session _s : this.duplicates) {
						if(_s != null) {
							this.primary = _s;
							break;
						}
						// ideally remove, but that would break the loop
					}
					this.duplicates.remove(this.primary);
				}
			} else if(this.duplicates != null) {
				this.duplicates.remove(s);
			}
			return this.size();
		}
	}
	
	// protected UserRepository users_repo;
	// protected Map<Session, Long> session_user_ids = new HashMap<>();
	// protected Map<Long, SessionInfo> user_id_sessions = new HashMap<>();
	protected String keyname;
	protected boolean enable_duplicate_users;

	protected WebSocketBase(String ws_name, boolean use_duplicates) {
		// this.users_repo = static_autowired;
		this.keyname = ws_name;
		this.enable_duplicate_users = use_duplicates;
	}

	// these all have to be static and thus must be implemented in the child class
	protected abstract UserRepository getUserRepo();
	protected abstract Map<Session, Long> getSessionUserIds();
	protected abstract Map<Long, SessionInfo> getUserIdSessions();


	protected String formatMessage(String msg) {
		return String.format("[%s]: %s", this.keyname, msg);
	}
	protected void printMessage(String msg) {
		System.out.println(this.formatMessage(msg));
	}
	protected void printSessionsStats() {
		final StringBuilder b = new StringBuilder(this.formatMessage(String.format("Users: %d, Sessions: %d, IDs: {\n", this.getUserIdSessions().size(), this.getSessionUserIds().size())));
		this.getUserIdSessions().forEach(
			(Long id, SessionInfo info)->{
				b.append(String.format("\t%d (%d),\n", id, info.size()));
			}
		);
		b.append("}");
		System.out.println(b);
	}


	protected void onOpenBase(Session session, Long id) throws IOException {
		if(session == null || id == null) {
			this.printMessage(String.format("Failed to add session for user id %d - one or more params were null.", id));
			return;
		}
		boolean
			already_present = this.getUserIdSessions().containsKey(id),
			valid_user = this.getUserRepo().findById(id).isPresent();
		if(valid_user && (this.enable_duplicate_users || !already_present)) {
			Long ret = getSessionUserIds().put(session, id);
			System.out.println(String.format("DEBUG: prev session linked id: %d", ret));
			if(!already_present) {
				getUserIdSessions().put(id, new SessionInfo());
				System.out.println("DEBUG: created new session info");
			}
			getUserIdSessions().get(id).addSession(session, this.enable_duplicate_users);
			this.printMessage(String.format("Succesfully added session for user id %d, duplicate: %b.", id, already_present));
		} else {
			this.printMessage(String.format("Failed to add session for user id %d -- duplicate: %b, valid: %b.", id, already_present, valid_user));
		}
		this.printSessionsStats();
	}

	protected User onMessageVerifySession(Session s) throws IOException {
		if(s == null) return null;
		final User u = this.lookupSessionUser(s);
		if(u == null) {
			this.printMessage("Recieved message from invalid user session. Removing session and exiting...");
			s.close();
			return null;
		}
		return u;
	}

	protected void onCloseBase(Session session) throws IOException {
		try {
			final Long id = this.getSessionUserIds().remove(session);
			final SessionInfo s = this.getUserIdSessions().get(id);
			final int num = s.removeSession(session);
			if(num <= 0) {
				this.getUserIdSessions().remove(id);
			}
			this.printMessage(String.format("Successfully closed session for user id %d.", id));
		} catch(Exception e) {
			this.printMessage("Internal error! Failed to close user session.");
		}
		this.printSessionsStats();
	}

	protected void onErrorBase(Session session, Throwable throwable) {
		this.printMessage(String.format("ERROR: %s", throwable.getMessage()));
		System.out.println(throwable.getStackTrace());
		this.printSessionsStats();
	}


	protected User lookupSessionUser(Session s) {
		try {
			return this.getUserRepo().findById( this.getSessionUserIds().get(s) ).get();
		} catch(Exception e) {
			return null;
		}
	}

	protected void broadcastHandleDuplicates(Session src, Long id, SessionInfo info, String msg, StringBuilder log_out, boolean send_duplicates) throws IOException {
		int sent = 0;
		if(info.primary != src) {
			info.primary.getBasicRemote().sendText(msg);
			sent++;
		}
		if(send_duplicates && info.duplicates != null) {
			for(Session s : info.duplicates) {
				if(s != null && s != src) {
					s.getBasicRemote().sendText(msg);
					sent++;
				}
			}
		}
		if(sent > 1) {
			log_out.append(String.format("\t%d (x%d),\n", id, sent));
		} else if(sent > 0) {
			log_out.append(String.format("\t%d,\n", id));
		}
	}

	protected void broadcastAll(String message, Session src, boolean include_duplicates) {
		if(message != null) {
			final StringBuilder b = new StringBuilder(this.formatMessage("Successfully sent message to user ids: {\n"));
			this.getUserIdSessions().forEach(
				(Long id, SessionInfo info)->{
					try {
						this.broadcastHandleDuplicates(src, id, info, message, b, include_duplicates);
					} catch(Exception e) {

					}
				}
			);
			System.out.println(b.append("}"));
		}
	}


}
