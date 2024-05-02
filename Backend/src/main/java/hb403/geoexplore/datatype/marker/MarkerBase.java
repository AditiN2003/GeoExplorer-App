package hb403.geoexplore.datatype.marker;

import hb403.geoexplore.datatype.EntityBase;
import hb403.geoexplore.datatype.MarkerTag;
import hb403.geoexplore.util.GeometryUtil;

import java.util.*;

import org.locationtech.jts.geom.*;

import com.bedatadriven.jackson.datatype.jts.serialization.GeometryDeserializer;
import com.bedatadriven.jackson.datatype.jts.serialization.GeometrySerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import jakarta.persistence.*;
import lombok.*;


/** MarkerBase represents the base data used in all marker types */
@MappedSuperclass
@Getter
@Setter
public abstract class MarkerBase extends EntityBase {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "marker_id")
	protected Long id = -1L;

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

	@ManyToMany(
		fetch = FetchType.EAGER,
		cascade = { CascadeType.PERSIST, CascadeType.MERGE }
	)
	@JoinTable(
		// name = "marker_linked_tags",		// the name of the intermediate table that links this entity and the target entity (NEW)
		joinColumns = {
			@JoinColumn(
				name = "marker_id_linked",		// the name of the column in the intermediate table that links to the primary key (NEW)
				referencedColumnName="marker_id"	// the name of the column in the owning entity table that this column links to (REFERENCED)
			)
		},
		inverseJoinColumns = {
			@JoinColumn(
				name = "tag_id_linked",		// the name of the column in the intermediate table that links to the non-owning key (NEW)
				referencedColumnName="tag_id"	// the name of the column in the non-owning entity table for which this column links to (REFERENCED)
			)
		}
	)
	protected Set<MarkerTag> tags;



	public void nullifyId() {
		this.id = -1L;
	}

	/** Synchronize the stored table location and IO lat/long values (copies from the IO variables */
	public void enforceLocationIO() {
		this.location = GeometryUtil.makePoint(new Coordinate(this.io_latitude, this.io_longitude));
	}
	/** Synchronize the stored table location and IO lat/long values (copies from the table entry) */
	public void enforceLocationTable() {
		if(this.location != null) {
			this.io_latitude = this.location.getX();
			this.io_longitude = this.location.getY();
		}
	}

	public double rawDotWith(double lat, double lon) {
		return GeometryUtil.arcdotGlobal(this.io_latitude, this.io_longitude, lat, lon);
	}

	public double distanceTo(double lat, double lon) {
		return GeometryUtil.arcdistanceGlobal(this.io_latitude, this.io_longitude, lat, lon);
	}


	public static void sortByProximityAsc(List<? extends MarkerBase> markers, double lat, double lon, boolean enforce_from_table) {

		markers.sort(
			(MarkerBase a, MarkerBase b)->{
				if(enforce_from_table) {
					a.enforceLocationTable();
					b.enforceLocationTable();
				}
				final double
					da = a.rawDotWith(lat, lon),
					db = b.rawDotWith(lat, lon);
				return da < db ? 1 : (da > db ? -1 : 0);	// inverted since we want closest to be first
			}
		);

	}


}
