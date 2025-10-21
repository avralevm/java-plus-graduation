package ru.practicum.compilation.service;

import ru.practicum.compilation.dto.in.CompilationPublicParam;
import ru.practicum.compilation.dto.in.NewCompilationDto;
import ru.practicum.compilation.dto.in.UpdateCompilationRequest;
import ru.practicum.compilation.dto.output.CompilationDto;

import java.util.List;

public interface CompilationService {

    List<CompilationDto> findBy(CompilationPublicParam param);

    CompilationDto findById(Long compId);

    CompilationDto add(NewCompilationDto dto);

    void delete(Long compId);

    CompilationDto update(Long compId, UpdateCompilationRequest updateCompilationRequest);
}
