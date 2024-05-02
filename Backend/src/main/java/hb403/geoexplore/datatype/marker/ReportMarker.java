package hb403.geoexplore.datatype.marker;

import hb403.geoexplore.UserStorage.entity.User;
// import hb403.geoexplore.datatype.MarkerTag;

import java.util.*;

import hb403.geoexplore.comments.Entity.CommentEntity;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "geomap_reports")
@Getter
@Setter
public class ReportMarker extends MarkerBase {

	// @Column()
	// private Boolean verified;

	@ManyToMany(
		fetch = FetchType.EAGER,
		cascade = { CascadeType.PERSIST, CascadeType.MERGE }
	)
	@JoinTable(
		name = "report_confirmations",		// the name of the intermediate table that links this entity and the target entity (NEW)
		joinColumns = {
			@JoinColumn(
				name = "report_id_linked",		// the name of the column in the intermediate table that links to the primary key (NEW)
				referencedColumnName="marker_id"	// the name of the column in the owning entity table that this column links to (REFERENCED)
			)
		},
		inverseJoinColumns = {
			@JoinColumn(
				name = "user_id_linked",		// the name of the column in the intermediate table that links to the non-owning key (NEW)
				referencedColumnName="user_id"	// the name of the column in the non-owning entity table for which this column links to (REFERENCED)
			)
		}
	)
	private Set<User> confirmed_by = new HashSet<>();

	@OneToMany(mappedBy = "pertainsReportMarker", fetch = FetchType.EAGER , cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
	private List<CommentEntity> Comments;
}
