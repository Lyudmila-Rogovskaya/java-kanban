package server;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

public class DurationAdapter extends TypeAdapter<Duration> {
    @Override
    public void write(JsonWriter out, Duration duration) throws IOException { // конвертируем Duration в минуты (JSON)
        if (duration == null) {
            out.nullValue();
        } else {
            out.value(duration.toMinutes());
        }
    }

    @Override
    public Duration read(JsonReader in) throws IOException { // конвертируем минуты в Duration
        if (in.hasNext()) {
            return Duration.ofMinutes(in.nextLong());
        }
        return null;
    }

}
