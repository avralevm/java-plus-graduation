package ru.practicum.mapper;

import org.mapstruct.*;
import ru.practicum.compilation.in.NewCompilationDto;
import ru.practicum.compilation.in.UpdateCompilationRequest;
import ru.practicum.compilation.output.CompilationDto;
import ru.practicum.event.output.EventShortDto;
import ru.practicum.model.Compilation;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CompilationMapper {
    @Mapping(target = "events", source = "events")
    CompilationDto toCompilationDto(Compilation compilation,  List<EventShortDto> events);

    Compilation toCompilation(NewCompilationDto newCompilationDto);

    Compilation toCompilation(UpdateCompilationRequest request);
}