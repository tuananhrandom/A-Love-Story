package anh.nguyen.alovestory.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentDTOAlter {
    private String commentText;
    private String userName;
    private String userAvatar;
    private String dateCreate;
    private String timeCreate;

    // Constructor
    public CommentDTOAlter(String commentText, String userName, String userAvatar, String dateCreate,
            String timeCreate) {
        this.commentText = commentText;
        this.userName = userName;
        this.userAvatar = userAvatar;
        this.dateCreate = dateCreate;
        this.timeCreate = timeCreate;
    }

    // Getters and Setters
    // ...
}
