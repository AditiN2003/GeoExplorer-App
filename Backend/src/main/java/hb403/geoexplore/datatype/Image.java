package hb403.geoexplore.datatype;

import hb403.geoexplore.UserStorage.entity.User;
import hb403.geoexplore.datatype.marker.ObservationMarker;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Observation_images")
public class Image {


    public enum Type {
        PROFILE        ("PROFILE"),
        OBSERVATION   ("OBSERVATION");

        public String value;
        private Type(String v) { this.value = v; }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    @Getter
    @Setter
    private Long id;

    @Column(name = "file_path")
    @Getter
    @Setter
    private String filePath;

    @Getter
    @Setter
    @OneToOne
    @JoinColumn(name = "marker_id")
    private ObservationMarker observation;

    @Getter
    @Setter
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Getter
    @Setter
    @Enumerated(value = EnumType.STRING)
    private Image.Type imageType;

}
