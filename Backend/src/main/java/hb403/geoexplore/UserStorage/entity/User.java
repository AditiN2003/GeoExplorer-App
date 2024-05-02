package hb403.geoexplore.UserStorage.entity;

import hb403.geoexplore.UserStorage.LocationSharing;
import hb403.geoexplore.UserStorage.repository.UserRepository;
import hb403.geoexplore.comments.Entity.CommentEntity;
import hb403.geoexplore.datatype.Image;
import hb403.geoexplore.util.GeometryUtil;

import org.locationtech.jts.geom.*;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

import com.bedatadriven.jackson.datatype.jts.serialization.GeometryDeserializer;
import com.bedatadriven.jackson.datatype.jts.serialization.GeometrySerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.springframework.beans.factory.annotation.Autowired;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    public enum Role {
        USER        ("USER"),
        BANNED   ("BANNED"),
        ADMIN       ("ADMIN");

        public String value;
        private Role(String v) { this.value = v; }
    }


    @Basic
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)     // probably use UUID after we are done testing
    @Column(name = "user_id")
    private Long id;

    private String name;

    @Column(name = "email_id")
    private String emailId;

    //String[] adminList = {"emessmer@iastate.edu","aditin@iastate.edu" ,"samr888@iastate.edu","yharb@iastate.edu"};
    private String password;

    @JsonIgnore
    private String encryptedPassword;

    // @JsonIgnore
    // private boolean isAdmin;

    @Enumerated(value = EnumType.STRING)
    private Role role = Role.USER;

    @Enumerated(value = EnumType.STRING)
    private LocationSharing location_privacy = LocationSharing.DISABLED;

    @Lob
	@JsonSerialize(using = GeometrySerializer.class)
	@JsonDeserialize(using = GeometryDeserializer.class)
	@JsonIgnore
	@Column()
	protected Point location;			// lat/long as stored in the tables -- not serialized to json (@JsonIgnore)

	@Transient
	protected Double io_latitude = 0.0;		// lat as serialized/deserialized -- not stored in the tables (@Transient)
	@Transient
	protected Double io_longitude = 0.0;		// long as serialize/deserialized -- not stored in the tables (@Transient)

    @Temporal(value = TemporalType.TIMESTAMP)
    @Column()
    protected Date last_location_update;

    /*@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE }, mappedBy = "posts")
    @JsonIgnore
    private Set<CommentEntity> comments = new HashSet<>();*/

    @ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE }, mappedBy = "members")
    @JsonIgnore
    private Set<UserGroup> groups = new HashSet<>();


    @Getter
    @Setter
    @JsonIgnore
    @OneToMany(mappedBy = "pertainsUser", fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
    private List<CommentEntity> Comments;

    @Getter
    @Setter
    @JsonIgnore
    @OneToOne(mappedBy = "reportedUser", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
    private ReportedUser user;

    /*@Getter
    @Setter
    @JsonIgnore
    @OneToOne(mappedBy = "user", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
    private Image image;*/

    public User(Long id, String name, String emailId, String password) {
        this.id = id;
        this.name = name;
        this.emailId = emailId;
        this.password = password;

        checkIfAdmin();
        encryptPassword();
    }

    public User(String name, String emailId, String password) {
//        this.id = id;
        this.name = name;
        this.emailId = emailId;
        this.password = password;
        checkIfAdmin();
        encryptPassword();
    }

    public User() {

    }


    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getEmailId(){
        return emailId;
    }

    public void setEmailId(String emailId){
        this.emailId = emailId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public void encryptPassword(){
        ArrayList<Character> encryption = new ArrayList<Character>();
        for (int i = 0; i < password.length();i++){
            encryption.add('*');
        }
        encryptedPassword = encryption.toString();
    }


    @JsonIgnore
    public boolean getIsAdmin(){
        // return isAdmin;
        return this.role == Role.ADMIN;
    }

    public void checkIfAdmin(){
        ArrayList<String> adminList = new ArrayList<>();
        adminList.add("emessmer@iastate.edu");
        adminList.add("samr888@iastate.edu");
        adminList.add("aditin@iastate.edu");
        adminList.add("yharb@iastate.edu");
        //for (String s : adminList) {
        if (adminList.contains(emailId)) {
            this.setIsAdmin(true);
        }

    }
    @JsonIgnore
    public void setIsAdmin(boolean isAdmin){    // not technically implemented correctly but the method is only used above so its fine
        // this.isAdmin = isAdmin;
        if(isAdmin) this.role = Role.ADMIN;
    }

    @JsonIgnore
    public void ban(){
        this.role = Role.BANNED;
    }



    /** Synchronize the stored table location and IO lat/long values (copies from the IO variables */
	public void enforceLocationIO() {
		this.location = GeometryUtil.makePoint(new Coordinate(this.io_latitude, this.io_longitude));
        this.last_location_update = new Date();     // could be wrong? eh who cares
	}
	/** Synchronize the stored table location and IO lat/long values (copies from the table entry) */
	public void enforceLocationTable() {
		if(this.location != null && this.location_privacy != LocationSharing.DISABLED) {
			this.io_latitude = this.location.getX();
			this.io_longitude = this.location.getY();
		}
	}

    @Getter
    @Setter
    @JsonIgnore
    @OneToOne(mappedBy = "user", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
    private Image image;


    @Override
    public String toString(){
        return "Name: " + this.name +
                "\nusername: " + this.emailId +
                "\nPassword: " + this.encryptedPassword +
                "\nSuccessfully created";
    }


}
