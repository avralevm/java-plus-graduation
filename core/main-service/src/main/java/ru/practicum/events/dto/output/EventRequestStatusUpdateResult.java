package ru.practicum.events.dto.output;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventRequestStatusUpdateResult {
    String description;
    Long event;
    Long id;
    Long requester;
    String status;
}
