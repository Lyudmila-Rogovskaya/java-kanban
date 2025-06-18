package server;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public void write(JsonWriter out, LocalDateTime date) throws IOException { // конвертируем дату в строку (JSON)
        if (date == null) {
            out.nullValue();
        } else {
            out.value(FORMATTER.format(date));
        }
    }

    @Override
    public LocalDateTime read(JsonReader in) throws IOException { // конвертируем строку в дату
        if (in.hasNext()) {
            return LocalDateTime.parse(in.nextString(), FORMATTER);
        }
        return null;
    }

}
