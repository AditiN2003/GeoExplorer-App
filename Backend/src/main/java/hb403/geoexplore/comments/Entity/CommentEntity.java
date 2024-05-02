package hb403.geoexplore.comments.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import hb403.geoexplore.UserStorage.entity.User;
import hb403.geoexplore.datatype.marker.EventMarker;
import hb403.geoexplore.datatype.marker.ObservationMarker;
import hb403.geoexplore.datatype.marker.ReportMarker;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


import java.util.*;

@Entity
@Table(name = "CommentEntity")
@Getter
@Setter
public class CommentEntity {

        @Setter
        @Getter
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)     // probably use UUID after we are done testing
        @Column(name = "comment_id")
        private Long id;
        /*@ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE }, mappedBy = "PostIds")
        @JsonIgnore
        private Set<ObservationMarker> observations = new HashSet<>();*/
        /*
       @JsonIgnore
        @Column(name = "post_Id_linker")
        @OneToMany(mappedBy = "ObservationMarker")
        protected ArrayList<ObservationMarker> postLinker;*/
    @Getter
    @Setter
    @JsonIgnore
       @ManyToOne
       @JoinTable(
               name = "Observation_pertains",
               joinColumns = @JoinColumn(name = "comment_id"),
               inverseJoinColumns = @JoinColumn(name = "marker_id"))
       ObservationMarker pertainsObservationMarker;
    @Getter
    @Setter
    @JsonIgnore
    @ManyToOne
    @JoinTable(
            name = "Event_pertains",
            joinColumns = @JoinColumn(name = "comment_id"),
            inverseJoinColumns = @JoinColumn(name = "marker_id"))
    EventMarker pertainsEventMarker;
    @Getter
    @Setter
    @JsonIgnore
    @ManyToOne
    @JoinTable(
            name = "Report_pertains",
            joinColumns = @JoinColumn(name = "comment_id"),
            inverseJoinColumns = @JoinColumn(name = "marker_id"))
    ReportMarker pertainsReportMarker;
    @JsonIgnore
    @ManyToOne
    @JoinTable(
            name = "User_pertains",
            joinColumns = @JoinColumn(name = "comment_id"),
            inverseJoinColumns = @JoinColumn(name = "user_table_id"))
    User pertainsUser;


        @Getter
        @Setter
        //@Column(name = "post_id_linked")
        private Long postId;
        @Setter
        @Getter
        @Column
        private Long userId;
        
        @Getter
        @Setter
        @Column
        private String comment;

        @Setter
        @Column
        
        private String postType;


    public CommentEntity(Long Commentid, Long PostID, Long userId, String type, String comment) {
        this.id = Commentid;
        this.postId = PostID;
        this.postType = type;
        this.userId = userId;
        this.comment= comment;
    }
    public CommentEntity(Long userId, Long PostID, String type, String comment) {
        this.postId = PostID;
        this.postType = type;
        this.userId = userId;
        this.comment = comment;
    }


    public CommentEntity() {

    }

    public String getPostType() {
        if (postType == null){
            return "Not in post";
    }
        else {
            return postType;
        }
    }

    @Override
    public String toString(){
        return "CommentId: " + this.id +
                "\nUserId " + this.userId +
                "\nPostId: " + this.postId +
                "\nComment: " + this.comment;
                
    }


}


