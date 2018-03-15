/*
 * Automatic Symlink Utility Copyright (c) 2018 Judge40
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.judge40.automaticsymlinkutility;

import mockit.Expectations;
import mockit.Verifications;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The unit tests for {@link MoveDirectoryVisitor}.
 */
public class MoveDirectoryVisitorTest {

  /**
   * Test that the destination directory is created when it does not already exist.
   */
  @Test
  public void testPreVisitDirectory_destinationNotExists_directoryCreated() throws IOException {
    // Set up test data.
    Path testDirectory =
        Files.createTempDirectory("testPreVisitDirectory_destinationNotExists_directoryCreated");
    testDirectory.toFile().deleteOnExit();

    Path source = Files.createTempDirectory(testDirectory, "source");
    source.toFile().deleteOnExit();

    Path destination = testDirectory.resolve("destinationNotExists");

    // Call the method under test.
    MoveDirectoryVisitor visitor = new MoveDirectoryVisitor(source, destination);
    FileVisitResult result = visitor.preVisitDirectory(source, null);
    destination.toFile().deleteOnExit();

    // Perform assertions.
    Assert.assertThat("The visitor result did not match the expected value.", result,
        CoreMatchers.is(FileVisitResult.CONTINUE));

    Assert.assertThat("The source path was expected to be a directory.", Files.isDirectory(source),
        CoreMatchers.is(true));
    Assert.assertThat("The destination path was expected to be a directory.",
        Files.isDirectory(destination), CoreMatchers.is(true));
  }

  /**
   * Test that the destination directory is not created when it already exists.
   */
  @Test
  public void testPreVisitDirectory_destinationExists_directoryNotCreated() throws IOException {
    // Set up test data.
    Path testDirectory =
        Files.createTempDirectory("testPreVisitDirectory_destinationNotExists_directoryCreated");
    testDirectory.toFile().deleteOnExit();

    Path source = Files.createTempDirectory(testDirectory, "source");
    source.toFile().deleteOnExit();

    Path destination = Files.createTempDirectory(testDirectory, "destination");
    destination.toFile().deleteOnExit();

    // Record expectations.
    new Expectations(Files.class) {};

    // Call the method under test.
    MoveDirectoryVisitor visitor = new MoveDirectoryVisitor(source, destination);
    FileVisitResult result = visitor.preVisitDirectory(source, null);

    // Perform assertions.
    Assert.assertThat("The visitor result did not match the expected value.", result,
        CoreMatchers.is(FileVisitResult.CONTINUE));

    Assert.assertThat("The source path was expected to be a directory.", Files.isDirectory(source),
        CoreMatchers.is(true));
    Assert.assertThat("The destination path was expected to be a directory.",
        Files.isDirectory(destination), CoreMatchers.is(true));

    // Verify expectations.
    new Verifications() {
      {
        Files.createDirectory((Path) any);
        times = 0;
      }
    };
  }

  /**
   * Test that the file is moved when it does not already exist.
   */
  @Test
  public void testPreVisitDirectory_destinationFileNotExists_fileMoved() throws IOException {
    // Set up test data.
    Path testDirectory =
        Files.createTempDirectory("testPreVisitDirectory_destinationFileNotExists_fileMoved");
    testDirectory.toFile().deleteOnExit();

    Path source = Files.createTempDirectory(testDirectory, "source");
    source.toFile().deleteOnExit();
    Path sourceFile = Files.createTempFile(source, "file", null);
    sourceFile.toFile().deleteOnExit();

    Path destination = Files.createTempDirectory(testDirectory, "destination");
    destination.toFile().deleteOnExit();
    Path destinationFile = destination.resolve(source.relativize(sourceFile));

    // Call the method under test.
    MoveDirectoryVisitor visitor = new MoveDirectoryVisitor(source, destination);
    FileVisitResult result = visitor.visitFile(sourceFile, null);
    destinationFile.toFile().deleteOnExit();

    // Perform assertions.
    Assert.assertThat("The visitor result did not match the expected value.", result,
        CoreMatchers.is(FileVisitResult.CONTINUE));

    Assert.assertThat("The source path was expected to be a directory.", Files.isDirectory(source),
        CoreMatchers.is(true));
    Assert.assertThat("The source file path was not expected to exist.", Files.exists(sourceFile),
        CoreMatchers.is(false));

    Assert.assertThat("The destination path was expected to be a directory.",
        Files.isDirectory(destination), CoreMatchers.is(true));
    Assert.assertThat("The destination file path was expected to be a file.",
        Files.isRegularFile(destinationFile), CoreMatchers.is(true));
  }

  /**
   * Test that the file is not moved when it already exists.
   */
  @Test
  public void testPreVisitDirectory_destinationFileExists_fileNotCreated() throws IOException {
    // Set up test data.
    Path testDirectory =
        Files.createTempDirectory("testPreVisitDirectory_destinationFileExists_fileNotCreated");
    testDirectory.toFile().deleteOnExit();

    Path source = Files.createTempDirectory(testDirectory, "source");
    source.toFile().deleteOnExit();
    Path sourceFile = Files.createTempFile(source, "file", null);
    sourceFile.toFile().deleteOnExit();

    Path destination = Files.createTempDirectory(testDirectory, "destination");
    destination.toFile().deleteOnExit();
    Path destinationFile = destination.resolve(source.relativize(sourceFile));
    Files.createFile(destinationFile);
    destinationFile.toFile().deleteOnExit();

    // Record expectations.
    new Expectations(Files.class) {};

    // Call the method under test.
    MoveDirectoryVisitor visitor = new MoveDirectoryVisitor(source, destination);
    FileVisitResult result = visitor.visitFile(source, null);

    // Perform assertions.
    Assert.assertThat("The visitor result did not match the expected value.", result,
        CoreMatchers.is(FileVisitResult.CONTINUE));

    Assert.assertThat("The source path was expected to be a directory.", Files.isDirectory(source),
        CoreMatchers.is(true));
    Assert.assertThat("The source file path was expected to be a file.",
        Files.isRegularFile(sourceFile), CoreMatchers.is(true));

    Assert.assertThat("The destination path was expected to be a directory.",
        Files.isDirectory(destination), CoreMatchers.is(true));
    Assert.assertThat("The destination file path was expected to be a file.",
        Files.isRegularFile(destinationFile), CoreMatchers.is(true));

    // Verify expectations.
    new Verifications() {
      {
        Files.move((Path) any, (Path) any);
        times = 0;
      }
    };
  }

  /**
   * Test that an exception is thrown when an exception is given.
   */
  @Test(expected = IOException.class)
  public void testPostVisitDirectory_exception_exception() throws IOException {
    // Call the method under test.
    MoveDirectoryVisitor visitor = new MoveDirectoryVisitor(null, null);
    visitor.postVisitDirectory(null, new IOException("Expected exception."));
  }

  /**
   * Test that the directory is deleted when no exception is given.
   */
  @Test(expected = IOException.class)
  public void testPostVisitDirectory_noException_directoryDeleted() throws IOException {
    // Set up test data.
    Path testDirectory =
        Files.createTempDirectory("testPostVisitDirectory_noException_directoryDeleted");
    testDirectory.toFile().deleteOnExit();

    Path directory = Files.createTempDirectory(testDirectory, "directory");
    directory.toFile().deleteOnExit();

    // Call the method under test.
    MoveDirectoryVisitor visitor = new MoveDirectoryVisitor(null, null);
    FileVisitResult result =
        visitor.postVisitDirectory(directory, new IOException("Expected exception."));

    // Perform assertions.
    Assert.assertThat("The visitor result did not match the expected value.", result,
        CoreMatchers.is(FileVisitResult.CONTINUE));
    Assert.assertThat("The directory path was expected to be a directory.",
        Files.isDirectory(directory), CoreMatchers.is(true));
  }
}
