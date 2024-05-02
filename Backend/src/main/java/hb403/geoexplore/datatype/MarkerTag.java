package hb403.geoexplore.datatype;

import hb403.geoexplore.datatype.marker.*;

import java.util.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;


/** Tag represents a tag that can be applied to any marker type (used for filtering). TODO: finish this when we have all *revamped* marker types */
@Entity
@Table(name = "marker_tags")
@Getter
@Setter
public class MarkerTag {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "tag_id")
	private Long id = -1L;

	@Column(unique = true)
	private String name;


	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE }, mappedBy = "tags")
	@JsonIgnore
	private Set<AlertMarker> tagged_alerts;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE }, mappedBy = "tags")
	@JsonIgnore
	private Set<EventMarker> tagged_events;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE }, mappedBy = "tags")
	@JsonIgnore
	private Set<ObservationMarker> tagged_observations;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE }, mappedBy = "tags")
	@JsonIgnore
	private Set<ReportMarker> tagged_reports;


}
