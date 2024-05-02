package hb403.geoexplore.datatype;

import hb403.geoexplore.UserStorage.entity.User;

import java.util.*;

import jakarta.persistence.*;
import lombok.*;


@MappedSuperclass
@Getter
@Setter
public abstract class EntityBase {
	
	@Column()
	protected String title;
	@Column()
	protected String description;

	@ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE })	// caused an error - might have to implement per-entity
	@JoinColumn(name = "creator_user_id", referencedColumnName = "user_id")
	protected User creator;

	@Temporal(value = TemporalType.TIMESTAMP)
	@Column()
	protected Date time_created = new Date();	// default ts of whenever constructed
	@Temporal(value = TemporalType.TIMESTAMP)
	@Column()
	protected Date time_updated = new Date();

	@Column()
	protected String meta;


	public void applyNewTimestamp() {
		this.time_created = new Date();
		this.applyUpdatedTimestamp();
	}
	public void applyUpdatedTimestamp() {
		this.time_updated = new Date();
	}


}
