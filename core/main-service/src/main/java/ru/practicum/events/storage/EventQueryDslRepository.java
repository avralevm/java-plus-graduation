package ru.practicum.events.storage;

import org.springframework.data.domain.Pageable;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.EventAdminParam;
import ru.practicum.events.model.EventPublicParam;

import java.util.List;

public interface EventQueryDslRepository {
    List<Event> findEventsByParam(EventAdminParam param, Pageable pageable);

    List<Event> findEventsByParam(EventAdminParam param, int offset);

    List<Event> findEventsByParam(EventPublicParam param, Pageable pageable);

    List<Event> findEventsByParam(EventPublicParam param, int offset);
}
