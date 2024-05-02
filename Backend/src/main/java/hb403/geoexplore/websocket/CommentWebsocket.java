package hb403.geoexplore.websocket;

import java.io.IOException;
import java.util.*;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import hb403.geoexplore.UserStorage.entity.User;
import hb403.geoexplore.UserStorage.repository.UserRepository;
import hb403.geoexplore.comments.CommentRepo.CommentRepository;
import hb403.geoexplore.comments.Entity.CommentEntity;
import hb403.geoexplore.datatype.marker.EventMarker;
import hb403.geoexplore.datatype.marker.ObservationMarker;
import hb403.geoexplore.datatype.marker.ReportMarker;
import hb403.geoexplore.datatype.marker.repository.EventRepository;
import hb403.geoexplore.datatype.marker.repository.ObservationRepository;
import hb403.geoexplore.datatype.marker.repository.ReportRepository;
import jakarta.websocket.*;
import jakarta.websocket.server.*;


@ServerEndpoint("/comments/{user_id}")
@Controller
public class CommentWebsocket extends WebSocketBase {

    private static UserRepository users_repo;
    private static CommentRepository commentRepository;
    public static ObservationRepository observationRepository;
    private static EventRepository eventRepository;
    private static ReportRepository reportRepository;


    @Autowired
    public void autoUserRepository(UserRepository repo) {
        CommentWebsocket.users_repo = repo;
    }
    @Autowired
    public void autoCommentRepository(CommentRepository repo) {
        CommentWebsocket.commentRepository = repo;
    }
    @Autowired
    public void autoObservationRepository(ObservationRepository repo) {
        CommentWebsocket.observationRepository = repo;
    }
    @Autowired
    public void autoEventRepository(EventRepository repo){
        CommentWebsocket.eventRepository = repo;
    }
    @Autowired
    public void autoReportRepository(ReportRepository repo){
        CommentWebsocket.reportRepository = repo;
    }


    protected static Map<Session, Long> session_user_ids = new HashMap<>();
	protected static Map<Long, SessionInfo> user_id_sessions = new HashMap<>();

    public CommentWebsocket() {
        super("Comment WS", true);
    }

    @Override
	protected UserRepository getUserRepo() {
		return CommentWebsocket.users_repo;
	}
    @Override
	protected Map<Session, Long> getSessionUserIds() {
		return CommentWebsocket.session_user_ids;
	}
	@Override
	protected Map<Long, SessionInfo> getUserIdSessions() {
		return CommentWebsocket.user_id_sessions;
	}


    @OnOpen
    public void onOpenSession (Session session, @PathParam("user_id") Long id) throws IOException {
        super.onOpenBase(session, id);
    }
    @OnClose
    public void OnClose(Session session) throws IOException{
        super.onCloseBase(session);
    }
    @OnMessage
    public void OnMessage(Session session, String message){
        if (session == null || message == null || users_repo.findById(session_user_ids.get(session)).isEmpty()) {
            System.out.println("[Comment WS]: Invalid message, session, or user!");
            return;
        }
        final ObjectMapper mapperForComments = new ObjectMapper();
        String message_out;
        try {
            CommentEntity temp = mapperForComments.readValue(message, CommentEntity.class);
            temp.setUserId(session_user_ids.get(session));
            if(temp.getPostType().equals("Observation")){
                temp = commentRepository.saveAndFlush(temp);
                ObservationMarker tempObs = observationRepository.findById(temp.getPostId()).get();
                tempObs.getComments().add(temp);
                temp.setPertainsObservationMarker(tempObs);
                observationRepository.saveAndFlush(tempObs);
                System.out.println("[Comment WS]: Successfully saved observation, and tags for comment message:\n" + temp.toString());
                
            }
            else if (temp.getPostType().equals("Event")){
                temp = commentRepository.saveAndFlush(temp);
                EventMarker tempEvent = eventRepository.findById(temp.getPostId()).get();
                tempEvent.getComments().add(temp);
                temp.setPertainsEventMarker(tempEvent);
                eventRepository.saveAndFlush(tempEvent);
                System.out.println("[Comment WS]: Successfully saved event, and tags for comment message:\n" + temp.toString());
            }
            else if (temp.getPostType().equals("Report")){
                temp = commentRepository.saveAndFlush(temp);
                ReportMarker tempReport = reportRepository.findById(temp.getPostId()).get();
                tempReport.getComments().add(temp);
                temp.setPertainsReportMarker(tempReport);
                reportRepository.saveAndFlush(tempReport);
                System.out.println("[Comment WS]: Successfully saved report, and tags for comment message:\n" + temp.toString());
            }
            else {
                System.out.println("[Comment WS]: Failed to parse message due to invalid post type.");
                return;
            }
            User tempUser = users_repo.findById(temp.getUserId()).get();
            tempUser.getComments().add(temp);
            temp.setPertainsUser(tempUser);
            users_repo.saveAndFlush(tempUser);
            message_out = mapperForComments.writeValueAsString(temp);
        } catch (Exception e) {
            System.out.println(e);
            return;
        }
        System.out.println("[Comment WS]: Proceeding to send message: " + message_out);
        if(message_out != null){
            session_user_ids.forEach(
                (Session tempSess, Long user_id)-> {
                    // if (tempSess == session) return;
                    try {
                        tempSess.getBasicRemote().sendText(message_out);
                        System.out.println("[Comment WS]: Successfully sent message to user id " + user_id);
                    }catch (Exception e){
                        System.out.println(e);
                    }
                }
            );
        }
    }
    @OnError
    public void OnError(Session session, Throwable throwable){
        super.onErrorBase(session, throwable);
    }


}
