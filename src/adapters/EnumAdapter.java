package adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import util.TaskProgress;

import java.io.IOException;

public class EnumAdapter extends TypeAdapter<TaskProgress> {
    @Override
    public void write(JsonWriter jsonWriter, TaskProgress taskProgress) throws IOException {
        if (taskProgress == null) {
            jsonWriter.nullValue();
        } else {
            jsonWriter.value(taskProgress.name());
        }
    }

    @Override
    public TaskProgress read(JsonReader jsonReader) throws IOException {
        return TaskProgress.valueOf(jsonReader.nextString());
    }
}
