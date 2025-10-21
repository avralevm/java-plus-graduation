package ru.practicum.comments.dto.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.comments.model.StateFilter;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentAdminParam {
    Integer from = 0;
    Integer size = 10;
    StateFilter status;
    LocalDateTime start;
    LocalDateTime end;
}