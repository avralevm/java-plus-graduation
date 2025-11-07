package ru.practicum.comment.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentPublicParam {
    Long eventId;
    Integer from = 0;
    Integer size = 10;
}

