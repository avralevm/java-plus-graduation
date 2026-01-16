package ru.practicum.service;

import ru.practicum.compilation.in.CompilationPublicParam;
import ru.practicum.compilation.in.NewCompilationDto;
import ru.practicum.compilation.in.UpdateCompilationRequest;
import ru.practicum.compilation.output.CompilationDto;

import java.util.List;

public interface CompilationService {
    List<CompilationDto> findBy(CompilationPublicParam param);

    CompilationDto findById(Long compId);

    CompilationDto add(NewCompilationDto dto);

    void delete(Long compId);

    CompilationDto update(Long compId, UpdateCompilationRequest updateCompilationRequest);
}
