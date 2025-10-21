package ru.practicum.events.dto.output;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.requests.dto.ParticipationRequestDtoOut;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SwitchRequestsStatus {
    List<ParticipationRequestDtoOut> confirmedRequests;
    List<ParticipationRequestDtoOut> rejectedRequests;
}
