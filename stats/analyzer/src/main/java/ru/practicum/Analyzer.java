package ru.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ConfigurableApplicationContext;
import ru.practicum.processor.EventSimilarityProcessor;
import ru.practicum.processor.UserActionProcessor;

@SpringBootApplication
@ConfigurationPropertiesScan
public class Analyzer {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Analyzer.class, args);

        final EventSimilarityProcessor  eventSimilarityProcessor = context.getBean(EventSimilarityProcessor.class);
        UserActionProcessor userActionProcessor = context.getBean(UserActionProcessor.class);

        Thread eventSimilarityThread = new Thread(eventSimilarityProcessor, "eventSimilarityThread");
        eventSimilarityThread.start();

        Thread userActionThread = new Thread(userActionProcessor, "userActionThread");
        userActionThread.start();
    }
}