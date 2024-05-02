package hb403.geoexplore.UserStorage.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import hb403.geoexplore.comments.Entity.CommentEntity;
import hb403.geoexplore.datatype.marker.ObservationMarker;
import hb403.geoexplore.datatype.marker.ReportMarker;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "usergroups")
@Getter
@Setter
public class UserGroup {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "group_id")
	private Long id;

	private String title;

	private Boolean share_locations = false;

	@ManyToMany(
		fetch = FetchType.EAGER,
		cascade = { CascadeType.PERSIST, CascadeType.MERGE }
	)
	@JoinTable(
		name = "group_members",		// the name of the intermediate table that links users and groups (NEW)
		joinColumns = {
			@JoinColumn(
				name = "group_id_linked",		// the name of the column in the intermediate table that links to the primary key (NEW)
				referencedColumnName="group_id"	// the name of the column in the owning entity table that this column links to (REFERENCED)
			)
		},
		inverseJoinColumns = {
			@JoinColumn(
				name = "member_id_linked",		// the name of the column in the intermediate table that links to the non-owning key (NEW)
				referencedColumnName="user_id"	// the name of the column in the non-owning entity table for which this column links to (REFERENCED)
			)
		}
	)
	private Set<User> members = new HashSet<>();

	// group owner? -- this will make the User class a little messy
	// tags for filtering?

	/*what it looks like in user
	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE }, mappedBy = "members")
    @JsonIgnore
    private Set<UserGroup> groups = new HashSet<>();
	 */
	@Getter
	@Setter
	@ManyToMany(mappedBy = "pertainsGroup", fetch = FetchType.EAGER)
	private Set<ObservationMarker> observations = new HashSet<>();

	public void addToObservations(ObservationMarker observation_to_add){
		observations.add(observation_to_add);
	}

	public UserGroup() {}


	public Set<Long> getMemberIds() {
		final Set<Long> uids = new HashSet<>();
		this.members.forEach(
			(User u)->{
				uids.add(u.getId());
			}
		);
		return uids;
	}


}

