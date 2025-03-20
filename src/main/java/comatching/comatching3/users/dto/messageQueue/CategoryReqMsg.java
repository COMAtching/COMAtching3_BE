package comatching.comatching3.users.dto.messageQueue;

import java.util.List;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CategoryReqMsg {
    private List<String> smallCategory;
    private String uuid;
}
