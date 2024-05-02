package test.connect.geoexploreapp.model;

import java.util.HashSet;
import java.util.Set;

public class UserGroup {
    private Long id;
    private String title;
    private Set<User> members;

    public UserGroup() {
        this.members = new HashSet<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Set<User> getMembers() {
        return members;
    }

    public void setMembers(Set<User> members) {
        this.members = members;
    }

}
