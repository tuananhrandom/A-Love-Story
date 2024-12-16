package anh.nguyen.alovestory.DTO;

import anh.nguyen.alovestory.entities.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostDTO {
    private String postCaption;
    private String postMedia;
    private String postMediaType;
    private String dateCreate;
    private String timeCreate;
    private User user;
    private Integer postLiked = 0;
    private Integer commentCount;
}
