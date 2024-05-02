package test.connect.geoexploreapp.model;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public interface FeedItem {
    double getIo_latitude();
    double getIo_longitude();
    String getTitle();
    String getDescription();
    String getType();
    Date getTime_created();
    Long getPostID();
    List<Comment> getComments();

}
