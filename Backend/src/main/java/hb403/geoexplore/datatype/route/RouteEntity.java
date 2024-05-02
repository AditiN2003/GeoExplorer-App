package hb403.geoexplore.datatype.route;

import hb403.geoexplore.datatype.EntityBase;

import java.util.*;

import org.locationtech.jts.geom.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "geomap_routes")
@Getter
@Setter
public class RouteEntity extends EntityBase {

	class JsonPoint {

		public Double latitude;
		public Double longitude;


		public Point createGeom() {
			return new Point(new Coordinate(this.latitude, this.longitude), new PrecisionModel(), 0);
		}
		public JsonPoint fromGeom(Point p) {
			if(p != null) {
				this.latitude = p.getX();
				this.longitude = p.getY();
			}
			return this;
		}

	}


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "route_id")
	protected Long id = -1L;

	@Lob
	@JsonIgnore
	@Column()
	protected List<Point> route_points;

	@Transient
	protected List<JsonPoint> io_route_points;



	public void nullifyId() {
		this.id = -1L;
	}

	public void enforceRouteIO() {
		if(this.route_points != null) this.route_points.clear();
		else this.route_points = new ArrayList<>();
		for(final JsonPoint p : this.io_route_points) {
			this.route_points.add(p.createGeom());
		}
	}
	public void enforceRouteTable() {
		if(this.io_route_points != null) this.io_route_points.clear();
		else this.io_route_points = new ArrayList<>();
		for(final Point p : this.route_points) {
			this.io_route_points.add(new JsonPoint().fromGeom(p));
		}
	}


}
