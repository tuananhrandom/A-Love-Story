package anh.nguyen.alovestory.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentDTO {
    private String commentText;
    private Long userId;
    private Long postId;
}
