package deserializer;

import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

public class EventSimilarityDeserializer extends BaseAvroDeserializer<EventSimilarityAvro> {
    EventSimilarityDeserializer() {
        super(EventSimilarityAvro.getClassSchema());
    }
}