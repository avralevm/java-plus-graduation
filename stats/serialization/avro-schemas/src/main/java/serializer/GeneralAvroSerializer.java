package serializer;

import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class GeneralAvroSerializer implements Serializer<SpecificRecordBase> {
    private final EncoderFactory encoderFactory = EncoderFactory.get();
    private BinaryEncoder encoder;

    @Override
    public byte[] serialize(String topic, SpecificRecordBase data) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            if (data != null) {
                DatumWriter<SpecificRecordBase> datumWriter = new SpecificDatumWriter<>(data.getSchema());
                encoder =  encoderFactory.binaryEncoder(outputStream, encoder);
                datumWriter.write(data, encoder);
                encoder.flush();
            }
            return outputStream.toByteArray();
        } catch (IOException ex) {
            throw new SerializationException("Ошибка сериализация данных для топика [" + topic + "]", ex);
        }
    }
}