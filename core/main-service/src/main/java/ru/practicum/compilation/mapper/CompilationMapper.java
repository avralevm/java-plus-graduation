package ru.practicum.compilation.mapper;

import org.mapstruct.*;
import ru.practicum.compilation.dto.in.NewCompilationDto;
import ru.practicum.compilation.dto.in.UpdateCompilationRequest;
import ru.practicum.compilation.dto.output.CompilationDto;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.events.model.Event;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CompilationMapper {

    @Mapping(target = "events", source = "compilation.events")
    CompilationDto toDto(Compilation compilation);

    @Mapping(target = "events", ignore = true) // Игнорируем поле events при маппинге
    Compilation toEntity(NewCompilationDto newCompilationDto, @Context List<Event> events);

    @Mapping(target = "events", ignore = true) // Игнорируем поле events при маппинге
    Compilation updateEntity(UpdateCompilationRequest updateRequest,
                             @MappingTarget Compilation compilation,
                             @Context List<Event> events);

    // Добавляем метод для ручной установки списка событий после маппинга NewCompilationDto -> Compilation
    @AfterMapping
    default void setEventsToEntity(NewCompilationDto newCompilationDto,
                                   @MappingTarget Compilation compilation,
                                   @Context List<Event> events) {
        compilation.setEvents(events);
    }

    // Добавляем метод для ручной установки списка событий после маппинга UpdateCompilationRequest -> Compilation
    @AfterMapping
    default void setEventsToEntity(UpdateCompilationRequest updateRequest,
                                   @MappingTarget Compilation compilation,
                                   @Context List<Event> events) {
        compilation.setEvents(events);
    }
}