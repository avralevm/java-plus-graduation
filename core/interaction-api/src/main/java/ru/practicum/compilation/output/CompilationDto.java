package ru.practicum.compilation.output;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.event.output.EventFullDto;

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
