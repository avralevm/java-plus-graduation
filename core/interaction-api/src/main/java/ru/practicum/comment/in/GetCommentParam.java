package ru.practicum.comment.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.comment.StateFilter;

@Data
@AllArgsConstructor
public class GetCommentParam {
    Long userId;
    Integer from;
    Integer size;
    StateFilter status;
}