package hb403.geoexplore.UserStorage.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reported_users")
@Getter
@Setter
public class ReportedUser {
    //A reported user should basically just have a relationship that can delete a user from this table
    // , and it will have the same list of comments that show up under any user it will also have a counter
    // for how many times they have been reported and the reason they were reported.
    //onetoone userid -> reported user id
    //onetoone usercomments -> reportedusercomments JsonIgnore
    //number of reports (controlled by controller) JsonIgnore
    //Reason for report -> (controlled by controller) ((could be a list of strings or booleans for multiple reasons))
    /*
    Example Json format for post
    {
    "reported_user_id" : 1,
    "Harrassment" : true,
    "Misinformation" : false,
    "Spamming" : false,
    "InappropriateContent" : true
    }
     */

    @Basic
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)     // probably use UUID after we are done testing
    @Column(name = "mod_report_id")
    private Long id;


    @Column(name = "reported_user_id")
    private Long reportedUserId;

    @Getter
    @Setter
    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "user_id")
    private User reportedUser;

   // private List<Stringstuff> Reasons = new ArrayList<Stringstuff>();

    //hopefully temporary solution for Reasons
    @Column
    private Boolean Harassment;//Harassing/Bullying another user

    @Column
    private Boolean Misinformation;//Provably inaccurate or false information

    @Column
    private Boolean Spamming;//Repeated Messages much more than necessary

    @Column
    private Boolean InappropriateContent;//Curse Words, Slurs, or Sexual content

    @Getter
    @Setter
    @JsonIgnore
    private int numReports;//however many times that user has been reported


    //for post, put will just edit the original and resave it
    public ReportedUser(Long userId, Boolean harass, Boolean misInfo, Boolean spam, Boolean inappropriate){

        this.reportedUserId = userId;
        this.Harassment = harass;
        this.Misinformation = misInfo;
        this.Spamming = spam;
        this.InappropriateContent = inappropriate;

    }

    public ReportedUser(){} //no arg constructor



}
