package ru.practicum.events.storage;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.EventPublicParam;
import ru.practicum.events.model.QEvent;

public interface EventPublicRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {

    interface Predicates {
        static BooleanExpression byParam(EventPublicParam params) {

            BooleanExpression conditions = Expressions.asBoolean(true).isTrue(); // Начинаем с TRUE

            if (params.getText() != null) {
                BooleanBuilder textConditions = new BooleanBuilder();
                textConditions.or(QEvent.event.annotation.containsIgnoreCase(params.getText()));
                textConditions.or(QEvent.event.description.containsIgnoreCase(params.getText()));
                conditions.and(textConditions);
            }

            if (params.getPaid() != null) {
                conditions.and(QEvent.event.paid.eq(params.getPaid()));
            }

            if (params.getRangeStart() != null) {
                conditions.and(QEvent.event.eventDate.goe(params.getRangeStart()));
            }

            if (params.getRangeEnd() != null) {
                conditions.and(QEvent.event.eventDate.loe(params.getRangeEnd()));
            }

            if (params.getOnlyAvailable() != null && params.getOnlyAvailable()) {
                conditions.and(QEvent.event.participantLimit.gt(0));
            }

            return conditions;
        }
    }
}

