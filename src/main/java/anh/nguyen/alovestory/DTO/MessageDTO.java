package anh.nguyen.alovestory.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageDTO {
    private String messageText;
    // private String messageMedia;
    private Long senderId;
    private Long recipientId;
    private String messageMediaType;
    // private String messageMediaType;
}
