package hb403.geoexplore.datatype;

import org.springframework.core.style.ToStringCreator;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.bedatadriven.jackson.datatype.jts.serialization.GeometryDeserializer;
import com.bedatadriven.jackson.datatype.jts.serialization.GeometrySerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.vividsolutions.jts.geom.Point;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;


@Entity
@Table(name = "points")
public class PointEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Lob
	@JsonSerialize(using = GeometrySerializer.class)
	@JsonDeserialize(using = GeometryDeserializer.class)
	private Point point;

	// @NotFound(action = NotFoundAction.IGNORE)
	// private Object data;


	public PointEntity() {}
	public PointEntity(Point pt) {
		this.point = pt;
	}
	public PointEntity(Long id, Point pt) {
		this.id = id;
		this.point = pt;
	}

	public Point getPoint() { return this.point; }
	public void updatePoint(Point pt) { if(pt != null) this.point = pt; }

	@Override
	public String toString() {
		return new ToStringCreator(this)
			.append("id", this.id)
			.append("point", this.point.toString())
			// .append("data", this.data)
		.toString();
	}


}
