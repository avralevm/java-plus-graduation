package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.compilation.dto.in.CompilationPublicParam;
import ru.practicum.compilation.dto.in.NewCompilationDto;
import ru.practicum.compilation.dto.in.UpdateCompilationRequest;
import ru.practicum.compilation.dto.output.CompilationDto;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.storage.CompilationRepository;
import ru.practicum.events.model.Event;
import ru.practicum.events.storage.EventRepository;
import ru.practicum.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final EventRepository eventRepository;

    public List<CompilationDto> findBy(CompilationPublicParam param) {
        List<Compilation> compilations;
        if (param.getSize() == 0) {
            if (param.getPinned() != null) {
                compilations = compilationRepository.findByPinned(param.getPinned()).stream()
                        .skip(param.getFrom())
                        .toList();
            } else {
                compilations = compilationRepository.findAll().stream()
                        .skip(param.getFrom())
                        .toList();
            }
        } else if (param.getFrom() < param.getSize() && param.getSize() > 0) {
            PageRequest pageRequest = PageRequest.of(param.getFrom() / param.getSize(), param.getSize());

            Page<Compilation> compilationPage;
            if (param.getPinned() != null) {
                compilationPage = compilationRepository.findByPinned(param.getPinned(), pageRequest);
            } else {
                compilationPage = compilationRepository.findAll(pageRequest);
            }
            compilations = compilationPage.getContent();
        } else {
            return List.of();
        }


        return compilations.stream()
                .map(compilationMapper::toDto)
                .toList();
    }

    public CompilationDto findById(Long compId) {

        Compilation compilation = findCompById(compId);

        return compilationMapper.toDto(compilation);
    }

    public CompilationDto add(NewCompilationDto dto) {
        List<Event> events = List.of();
        if (dto.getEvents() != null) {
            events = eventRepository.findAllById(dto.getEvents());
        }

        Compilation compilationToSave = compilationMapper.toEntity(dto, events);
        Compilation savedCompilation = compilationRepository.save(compilationToSave);

        log.info("Compilation with id: {} was created", savedCompilation.getId());
        return compilationMapper.toDto(savedCompilation);
    }

    public void delete(Long compId) {
        findCompById(compId);

        compilationRepository.deleteById(compId);
        log.info("Deleted compilation with id: {}", compId);
    }

    public CompilationDto update(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation findedCompilation = findCompById(compId);

        boolean pinned = findedCompilation.getPinned();
        String title = findedCompilation.getTitle();

        List<Event> events = new ArrayList<>();
        if (updateCompilationRequest.getEvents() != null) {
            events = eventRepository.findAllById(updateCompilationRequest.getEvents());
        }

        Compilation compilationToUpdate = compilationMapper.updateEntity(updateCompilationRequest, findedCompilation, events);

        if (updateCompilationRequest.getPinned() == null) {
            compilationToUpdate.setPinned(pinned);
        }
        if (updateCompilationRequest.getTitle() == null) {
            compilationToUpdate.setTitle(title);
        }
        Compilation updatedCompilation = compilationRepository.save(compilationToUpdate);

        log.info("Compilation with id: {} was updated", updatedCompilation.getId());
        return compilationMapper.toDto(updatedCompilation);
    }

    private Compilation findCompById(Long compId) {
        return compilationRepository.findById(compId)
                .orElseThrow(() -> {
                    log.warn("Compilation with id={} not found", compId);
                    return new NotFoundException("Compilation with id=" + compId + " was not found");
                });
    }
}
