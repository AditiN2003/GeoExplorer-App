package test.connect.geoexploreapp.model;

public class  Comment {
    private Long id;
    private Long userId;
    private String comment;

    private Long postId;
    private String postType;
    public Comment(Long Commentid,Long PostID,  Long userId,String type, String comment) {
        this.id = Commentid;
        this.postId = PostID;
        this.postType = type;
        this.userId = userId;
        this.comment= comment;
    }

    public Comment() {

    }



    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getPostid() {
        return postId;
    }

    public void setPostid(Long postid) {
        this.postId = postid;
    }

    public String getPostType() {
        return postType;
    }

    public void setPostType(String postType) {
        this.postType = postType;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", postId=" + postId +
                ", userId='" + userId + '\'' +
                ", comment='" + comment + '\'' +
                ", postType='" + postType + '\'' +
                '}';
    }
}
