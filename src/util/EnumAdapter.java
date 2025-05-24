package util;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class EnumAdapter extends TypeAdapter<TaskProgress> {
    @Override
    public void write(JsonWriter jsonWriter, TaskProgress taskProgress) throws IOException {
        jsonWriter.value(String.valueOf(TaskProgress.NEW));
    }

    @Override
    public TaskProgress read(JsonReader jsonReader) throws IOException {
        return TaskProgress.valueOf(jsonReader.nextString());
    }
}
