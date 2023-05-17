package io.github.augustoravazoli.csvj;

import java.util.List;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardOpenOption.CREATE;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CsvWriterTest {

  private static final String DESKTOP_PATH = System.getProperty("user.home") + File.separator;
  private static final Path PATH = Paths.get(DESKTOP_PATH + "output.csv");
  private CsvWriter<Person> csvWriter;
  
  @AfterEach
  void tearDown() throws IOException {
    Files.delete(PATH);
  }

  @Test
  void givenNonExistentFile_whenWriteObjects_thenCreateFileWithObjectsWritten() throws IOException {
    // given
    var objectsToWrite = List.of(
      new Person("Clark", 23, 2342),
      new Person("Bruce", 34, 1_243_234),
      new Person("Barry", 45, 234)
    );
    var expectedFileContent = "name,age\nClark,23\nBruce,34\nBarry,45\n";
    // when
    csvWriter = new CsvWriter<>(PATH, Person.class);
    csvWriter.write(objectsToWrite);
    csvWriter.flush();
    csvWriter.close();
    var actualFileContent = Files.readString(PATH);
    // then
    assertTrue(actualFileContent.equals(expectedFileContent));
  }

  @Test
  void givenExistentFile_whenWriteObjects_thenAppendNewObjectsToTheEndOfFile() throws IOException {
    // given
    var objectsToWrite = List.of(
      new Person("Diana", 100, 3_000),
      new Person("Alan", 50, 2_000),
      new Person("Arthur", 25, 1_000)
    );
    var expectedFileContent = "name,age\nClark,23\nBruce,34\nBarry,45\nDiana,100\nAlan,50\nArthur,25\n";
    // and
    var writer = Files.newBufferedWriter(PATH, CREATE);
    writer.write("name,age\nClark,23\nBruce,34\nBarry,45\n");
    writer.flush();
    writer.close();
    // when
    csvWriter = new CsvWriter<>(PATH, Person.class);
    csvWriter.write(objectsToWrite);
    csvWriter.flush();
    csvWriter.close();
    var actualFileContent = Files.readString(PATH);
    // then
    assertTrue(actualFileContent.equals(expectedFileContent));
  }

}
