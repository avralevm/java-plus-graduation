package ru.practicum.compilation.controller;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.in.CompilationPublicParam;
import ru.practicum.compilation.output.CompilationDto;
import ru.practicum.compilation.service.CompilationServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CompilationPublicController {
    private final CompilationServiceImpl compilationService;

    @GetMapping
    public List<CompilationDto> getCompilations(
            @RequestParam(required = false) Boolean pinned,
            @RequestParam(defaultValue = "0") @Min(0) Integer from,
            @RequestParam(defaultValue = "10") @Min(0) Integer size) {
        CompilationPublicParam param = new CompilationPublicParam(pinned, from, size);
        return compilationService.findBy(param);
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilationById(@PathVariable Long compId) {
        return compilationService.findById(compId);
    }
}
