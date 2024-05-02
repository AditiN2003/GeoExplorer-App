package test.connect.geoexploreapp.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReportMarker implements FeedItem{

    private long id;
    private String title;
    private String description;
    private double io_latitude;
    private double io_longitude;
    private User creator;
    private Date time_created;
    private Date time_updated;
    private String meta;
    private List<MarkerTag> tags;
    private List<User> confirmed_by;
    private List<Comment> comments;

    public ReportMarker() {
        this.tags = new ArrayList<>();
        this.comments = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getIo_longitude() {
        return io_longitude;
    }

    public void setIo_longitude(double io_longitude) {
        this.io_longitude = io_longitude;
    }

    public double getIo_latitude() {
        return io_latitude;
    }

    public void setIo_latitude(double io_latitude) {
        this.io_latitude = io_latitude;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getType() {
        return "Report";
    }


    @Override
    public Date getTime_created() {
        return time_created;
    }

    @Override
    public Long getPostID() {
        return id;
    }

    public void setTime_created(Date time_created) {
        this.time_created = time_created;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    @Override
    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments=comments;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public Date getTime_updated() {
        return time_updated;
    }

    public void setTime_updated(Date time_updated) {
        this.time_updated = time_updated;
    }

    public String getMeta() {
        return meta;
    }

    public void setMeta(String meta) {
        this.meta = meta;
    }

    public List<MarkerTag> getTags() {
        return tags;
    }

    public void setTags(List<MarkerTag> tags) {
        this.tags = tags;
    }

    public List<User> getConfirmed_by() {
        return confirmed_by;
    }

    public void setConfirmed_by(List<User> confirmed_by) {
        this.confirmed_by = confirmed_by;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}
