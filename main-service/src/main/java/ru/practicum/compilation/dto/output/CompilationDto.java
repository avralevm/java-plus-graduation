package ru.practicum.compilation.dto.output;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.events.dto.output.EventFullDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompilationDto {
    List<EventFullDto> events;
    Long id;
    Boolean pinned;
    String title;
}
