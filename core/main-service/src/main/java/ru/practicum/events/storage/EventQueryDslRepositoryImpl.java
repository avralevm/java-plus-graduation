package ru.practicum.events.storage;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ru.practicum.event.state.State;
import ru.practicum.events.model.*;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class EventQueryDslRepositoryImpl implements EventQueryDslRepository {
    private final QEvent event = QEvent.event;
    private final JPAQueryFactory queryFactory;

    public EventQueryDslRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Event> findEventsByParam(EventAdminParam param, Pageable pageable) {

        BooleanBuilder predicate = new BooleanBuilder();
        if (param.getUsers() != null && !param.getUsers().isEmpty()) {
            predicate.and(event.initiatorId.in(param.getUsers()));
        }
        if (param.getStates() != null && !param.getStates().isEmpty()) {
            predicate.and(event.state.in(param.getStates()));
        }
        if (param.getCategories() != null && !param.getCategories().isEmpty()) {
            predicate.and(event.category.id.in(param.getCategories()));
        }
        if (param.getStart() != null) {
            predicate.and(event.eventDate.after(param.getStart()));
        }
        if (param.getEnd() != null) {
            predicate.and(event.eventDate.before(param.getEnd()));
        }

        JPAQuery<Event> query = queryFactory
                .selectFrom(event)
                .where(predicate)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(event.eventDate.desc());
        return query.fetch();
    }

    @Override
    public List<Event> findEventsByParam(EventAdminParam param, int offset) {

        BooleanBuilder predicate = new BooleanBuilder();
        if (param.getUsers() != null && !param.getUsers().isEmpty()) {
            predicate.and(event.initiatorId.in(param.getUsers()));
        }
        if (param.getStates() != null && !param.getStates().isEmpty()) {
            predicate.and(event.state.in(param.getStates()));
        }
        if (param.getCategories() != null && !param.getCategories().isEmpty()) {
            predicate.and(event.category.id.in(param.getCategories()));
        }
        if (param.getStart() != null) {
            predicate.and(event.eventDate.after(param.getStart()));
        }
        if (param.getEnd() != null) {
            predicate.and(event.eventDate.before(param.getEnd()));
        }

        JPAQuery<Event> query = queryFactory
                .selectFrom(event)
                .where(predicate)
                .offset(offset)
                .orderBy(event.eventDate.desc());
        return query.fetch();
    }

    @Override
    public List<Event> findEventsByParam(EventPublicParam param, Pageable pageable) {

        BooleanBuilder predicate = new BooleanBuilder();

        predicate.and(QEvent.event.state.eq(State.PUBLISHED));

        if (param.getText() != null && !param.getText().isEmpty()) {
            BooleanBuilder textConditions = new BooleanBuilder();
            textConditions.or(QEvent.event.annotation.containsIgnoreCase(param.getText()));
            textConditions.or(QEvent.event.description.containsIgnoreCase(param.getText()));
            predicate.and(textConditions);
        }

        if (param.getPaid() != null) {
            predicate.and(QEvent.event.paid.eq(param.getPaid()));
        }

        if (param.getRangeStart() != null) {
            predicate.and(QEvent.event.eventDate.goe(param.getRangeStart()));
        }

        if (param.getRangeEnd() != null) {
            predicate.and(QEvent.event.eventDate.loe(param.getRangeEnd()));
        }

        if (param.getRangeStart() == null && param.getRangeEnd() == null) {
            predicate.and(QEvent.event.eventDate.goe(LocalDateTime.now()));

        }

        if (param.getOnlyAvailable() != null && param.getOnlyAvailable()) {
            predicate.and(QEvent.event.participantLimit.gt(0));
        }

        JPAQuery<Event> query = queryFactory
                .selectFrom(event)
                .where(predicate)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(event.eventDate.desc());
        return query.fetch();
    }

    @Override
    public List<Event> findEventsByParam(EventPublicParam param, int offset) {

        BooleanBuilder predicate = new BooleanBuilder();

        predicate.and(QEvent.event.state.eq(State.PUBLISHED));

        if (param.getText() != null && !param.getText().isEmpty()) {
            BooleanBuilder textConditions = new BooleanBuilder();
            textConditions.or(QEvent.event.annotation.containsIgnoreCase(param.getText()));
            textConditions.or(QEvent.event.description.containsIgnoreCase(param.getText()));
            predicate.and(textConditions);
        }

        if (param.getPaid() != null) {
            predicate.and(QEvent.event.paid.eq(param.getPaid()));
        }

        if (param.getRangeStart() != null) {
            predicate.and(QEvent.event.eventDate.goe(param.getRangeStart()));
        }

        if (param.getRangeEnd() != null) {
            predicate.and(QEvent.event.eventDate.loe(param.getRangeEnd()));
        }

        if (param.getRangeStart() == null && param.getRangeEnd() == null) {
            predicate.and(QEvent.event.eventDate.goe(LocalDateTime.now()));

        }

        if (param.getOnlyAvailable() != null && param.getOnlyAvailable()) {
            predicate.and(QEvent.event.participantLimit.gt(0));
        }

        JPAQuery<Event> query = queryFactory
                .selectFrom(event)
                .where(predicate)
                .offset(offset)
                .orderBy(event.eventDate.desc());
        return query.fetch();
    }
}
