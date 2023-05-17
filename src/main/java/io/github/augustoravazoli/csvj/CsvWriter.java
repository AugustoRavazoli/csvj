package io.github.augustoravazoli.csvj;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static java.util.stream.Collectors.joining;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.APPEND;

public class CsvWriter<T> implements Flushable, Closeable {
	
  private final BufferedWriter buffer;
	
  public CsvWriter(Path output, Class<T> clazz) throws IOException {
    if (!clazz.isAnnotationPresent(CsvSerializable.class)) {
      throw new IllegalArgumentException("The given class is not a csv serializable class");
    }
    var header = Files.notExists(output) ? getHeader(clazz) : "";
    buffer = Files.newBufferedWriter(output, CREATE, APPEND);
    buffer.write(header);
  }

  public void write(Iterable<T> objects) throws IOException {
    for (var object : objects) {
      buffer.write(getRow(object));
    }
  }

  @Override
  public void flush() throws IOException {
    buffer.flush();
  }

  @Override
  public void close() throws IOException {
    buffer.close();
  }

  private String getHeader(Class<?> clazz) {
    return getFields(clazz)
      .stream()
      .map(field -> field.getName())
      .collect(joining(","))
      .concat("\n");
  }

  private String getRow(Object object) {
    var fields = getFields(object.getClass());
    var values = getFieldValues(fields, object);
    return values.stream().collect(joining(",")).concat("\n");
  }

  private List<Field> getFields(Class<?> clazz) {
    return Arrays
      .stream(clazz.getDeclaredFields())
      .filter(field -> field.isAnnotationPresent(CsvColumn.class))
      .peek(field -> field.setAccessible(true))
      .toList();
  }
	
  private List<String> getFieldValues(List<Field> fields, Object object) {
    var values = new ArrayList<String>();
    for (var field : fields) {
      try {
        values.add(field.get(object).toString());
      } catch (IllegalArgumentException | IllegalAccessException ex) {
        throw new IllegalStateException(ex);
      }
    }
    return values;
  }

}
