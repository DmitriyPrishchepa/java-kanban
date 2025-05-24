package util;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class StringToIntAdapter extends TypeAdapter<Integer> {
    @Override
    public void write(JsonWriter jsonWriter, Integer integer) throws IOException {
        jsonWriter.value(String.valueOf(integer));
    }

    @Override
    public Integer read(JsonReader jsonReader) throws IOException {
        return Integer.parseInt(jsonReader.nextString());
    }
}
