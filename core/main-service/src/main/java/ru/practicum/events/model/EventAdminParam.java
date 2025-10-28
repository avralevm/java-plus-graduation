package ru.practicum.events.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventAdminParam {
    List<Long> users;
    List<State> states;
    List<Long> categories;
    LocalDateTime start;
    LocalDateTime end;
    Integer from = 0;
    Integer size = 10;
}
