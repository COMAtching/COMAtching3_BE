package comatching.comatching3.users.dto.messageQueue;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CategoryReqMsg {
    private List<String> smallCategory;
    private String uuid;
}
