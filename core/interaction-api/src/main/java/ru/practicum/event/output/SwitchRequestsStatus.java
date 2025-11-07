package ru.practicum.event.output;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.request.output.ParticipationRequestDtoOut;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SwitchRequestsStatus {
    List<ParticipationRequestDtoOut> confirmedRequests;
    List<ParticipationRequestDtoOut> rejectedRequests;
}
