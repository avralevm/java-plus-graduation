package ru.practicum.comments.dto.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.comments.model.StateFilter;

@Data
@AllArgsConstructor
public class GetCommentParam {
    Long userId;
    Integer from;
    Integer size;
    StateFilter status;
}