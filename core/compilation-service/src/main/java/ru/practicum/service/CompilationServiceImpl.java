package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.client.event.EventFeignClient;
import ru.practicum.compilation.in.CompilationPublicParam;
import ru.practicum.compilation.in.NewCompilationDto;
import ru.practicum.compilation.in.UpdateCompilationRequest;
import ru.practicum.compilation.output.CompilationDto;
import ru.practicum.event.output.EventShortDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.model.Compilation;
import ru.practicum.storage.CompilationRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final EventFeignClient eventFeignClient;

    public List<CompilationDto> findBy(CompilationPublicParam param) {
        List<Compilation> compilations = loadCompilations(param);
        if (compilations.isEmpty()) {
            return List.of();
        }

        List<Long> allEventIds = compilations.stream()
                .flatMap(c -> c.getEvents().stream())
                .distinct()
                .toList();

        Map<Long, EventShortDto> eventMap = eventFeignClient.getEventByIds(allEventIds)
                .stream()
                .collect(Collectors.toMap(EventShortDto::getId, e -> e));

        return compilations.stream()
                .map(comp -> {
                    List<EventShortDto> events = comp.getEvents().stream()
                            .map(eventMap::get)
                            .toList();
                    return compilationMapper.toCompilationDto(comp, events);
                }).toList();
    }

    public CompilationDto findById(Long compId) {
        Compilation compilation = findCompById(compId);
        List<EventShortDto> events = List.of();
        if (compilation.getEvents() != null && !compilation.getEvents().isEmpty()) {
            events = eventFeignClient.getEventByIds(compilation.getEvents());
        }
        return compilationMapper.toCompilationDto(compilation, events);
    }

    public CompilationDto add(NewCompilationDto dto) {
        List<EventShortDto> events = List.of();
        if (dto.getEvents() != null && !dto.getEvents().isEmpty()) {
            events = eventFeignClient.getEventByIds(dto.getEvents());
        }

        Compilation savedCompilation = compilationRepository.save(compilationMapper.toCompilation(dto));
        log.info("Compilation with id: {} was created", savedCompilation.getId());

        return compilationMapper.toCompilationDto(savedCompilation, events);
    }

    public void delete(Long compId) {
        findCompById(compId);

        compilationRepository.deleteById(compId);
        log.info("Deleted compilation with id: {}", compId);
    }

    public CompilationDto update(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilation = findCompById(compId);

        if (updateCompilationRequest.getPinned() != null) {
            compilation.setPinned(updateCompilationRequest.getPinned());
        }
        if (updateCompilationRequest.getTitle() != null) {
            compilation.setTitle(updateCompilationRequest.getTitle());
        }

        List<EventShortDto> events = new ArrayList<>();
        if (updateCompilationRequest.getEvents() != null && !updateCompilationRequest.getEvents().isEmpty()) {
            events = eventFeignClient.getEventByIds(updateCompilationRequest.getEvents());
            compilation.setEvents(events.stream().map(EventShortDto::getId).collect(Collectors.toList()));
        }

        Compilation updatedCompilation = compilationRepository.save(compilation);

        log.info("Compilation with id: {} was updated", updatedCompilation.getId());
        return compilationMapper.toCompilationDto(updatedCompilation, events);
    }

    private List<Compilation> loadCompilations(CompilationPublicParam param) {
        if (param.getSize() == 0) {
            return findCompilationsFiltered(param);
        }

        if (param.getFrom() >= param.getSize()) return List.of();

        PageRequest pageRequest = PageRequest.of(param.getFrom() / param.getSize(), param.getSize());
        return (param.getPinned() != null)
                ? compilationRepository.findByPinned(param.getPinned(), pageRequest).getContent()
                : compilationRepository.findAll(pageRequest).getContent();
    }

    private List<Compilation> findCompilationsFiltered(CompilationPublicParam param) {
        List<Compilation> list;
        if (param.getPinned() != null) {
            list = compilationRepository.findByPinned(param.getPinned());
        } else {
            list = compilationRepository.findAll();
        }

        return list.stream()
                .skip(param.getFrom())
                .toList();
    }

    private Compilation findCompById(Long compId) {
        return compilationRepository.findById(compId)
                .orElseThrow(() -> {
                    log.warn("Compilation with id={} not found", compId);
                    return new NotFoundException("Compilation with id=" + compId + " was not found");
                });
    }
}