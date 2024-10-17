package anh.nguyen.alovestory.DTO;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class PostDTO {
    private String postCaption;
    private String postMedia;
    private String postMediaType;
    private LocalDateTime createdAt;
    private Long userID;
    private Integer postLiked = 0;
    private Integer commentCount; 
}
