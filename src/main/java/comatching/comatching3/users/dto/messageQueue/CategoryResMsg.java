package comatching.comatching3.users.dto.messageQueue;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CategoryResMsg {
    private String stateCode;
    private List<String> bigCategory;
    private String message;
}
