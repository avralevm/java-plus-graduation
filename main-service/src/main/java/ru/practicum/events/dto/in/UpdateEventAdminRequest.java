package ru.practicum.events.dto.in;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import ru.practicum.events.model.Location;
import ru.practicum.events.model.StateActionForAdmin;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventAdminRequest {
    @Length(min = 20, max = 2000)
    String annotation;
    @PositiveOrZero
    Long category;
    @Length(min = 20, max = 7000)
    String description;
    @Future
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;
    Location location;
    Boolean paid;
    @PositiveOrZero
    Integer participantLimit;
    Boolean requestModeration;
    StateActionForAdmin stateAction;
    @Length(min = 3, max = 120)
    String title;
}
