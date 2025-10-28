package ru.practicum.events.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.requests.dto.ParticipationRequestDtoOut;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SwitchRequestsStatus {
    List<ParticipationRequestDtoOut> confirmedRequests;
    List<ParticipationRequestDtoOut> rejectedRequests;
}
