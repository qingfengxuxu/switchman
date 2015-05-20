package de.is24.common.infrastructure.config.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.module.SimpleSerializers;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.stereotype.Component;
import java.io.IOException;


@Component
public class JsonMarshallingConfigModule extends SimpleModule {
  public JsonMarshallingConfigModule() {
    super("JsonMarshallingConfigModule", Version.unknownVersion());
  }

  @Override
  public void setupModule(SetupContext context) {
    SimpleSerializers serializers = new SimpleSerializers();
    SimpleDeserializers deserializers = new SimpleDeserializers();

    serializers.addSerializer(DateTime.class, new DateTimeSerializer());
    deserializers.addDeserializer(DateTime.class, new DateTimeDeserializer());

    context.addSerializers(serializers);
    context.addDeserializers(deserializers);
  }


  public static class DateTimeSerializer extends JsonSerializer<DateTime> {
    private static DateTimeFormatter formatter = ISODateTimeFormat.dateTime();

    @Override
    public void serialize(DateTime value, JsonGenerator gen,
                          SerializerProvider arg2) throws IOException {
      gen.writeString(formatter.print(value));
    }
  }

  public static class DateTimeDeserializer extends JsonDeserializer<DateTime> {
    private static DateTimeFormatter formatter = ISODateTimeFormat.dateTime();

    @Override
    public DateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
                         throws IOException {
      return formatter.parseDateTime(jsonParser.getValueAsString());
    }
  }
}
