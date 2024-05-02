package test.connect.geoexploreapp;

import java.util.List;

import test.connect.geoexploreapp.model.Comment;

public interface CommentActionListener {
    void onEditComment(Comment comment, String newComment, int position);
}
