package ru.practicum.events.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.event.state.State;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventAdminParam {
    List<Long> users;

    List<State> states;

    List<Long> categories;

    LocalDateTime start;

    LocalDateTime end;

    @Builder.Default
    Integer from = 0;

    @Builder.Default
    Integer size = 10;
}
