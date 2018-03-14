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

import com.judge40.automaticsymlinkutility.SymlinkCreationResult.Status;

import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * The unit tests for {@link AutomaticSymlinkUtility}.
 */
public class AutomaticSymlinkUtilityTest {

  @Mocked
  @Tested
  private AutomaticSymlinkUtility symlinkUtility;

  /**
   * Test that an IllegalArgumentException is thrown when no arguments are given.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testMain_zeroArgs_exception() throws Exception {
    // Call the method under test.
    AutomaticSymlinkUtility.main(new String[0]);
  }

  /**
   * Test that an IllegalArgumentException is thrown when the argument is a file which does not
   * exist.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testMain_singleArgFileNotExists_exception() throws Exception {
    // Call the method under test.
    AutomaticSymlinkUtility.main(new String[] {"notExists"});
  }

  /**
   * Test that the definition file is parsed when the argument is a file which does exist.
   */
  @Test
  public void testMain_singleArgFileExists_fileParsed() throws Exception {
    // Set up test data.
    Path tempFile = Files.createTempFile("testMain_singleArgFileExists_exception", null);
    tempFile.toFile().deleteOnExit();

    try (OutputStream tempOutput = Files.newOutputStream(tempFile)) {
      StringBuilder xmlBuilder = new StringBuilder();
      xmlBuilder.append("<symlinks>");
      xmlBuilder.append("<symlink>");
      xmlBuilder.append("<linkPath>linkPath</linkPath>");
      xmlBuilder.append("<targetPath>targetPath</targetPath>");
      xmlBuilder.append("</symlink>");
      xmlBuilder.append("</symlinks>");

      tempOutput.write(xmlBuilder.toString().getBytes(StandardCharsets.UTF_8));
    }

    // Call the method under test.
    AutomaticSymlinkUtility.main(new String[] {tempFile.toString()});

    new Verifications() {
      {
        AutomaticSymlinkUtility.createSymbolicLink(Paths.get("linkPath"), Paths.get("targetPath"));
      }
    };
  }

  /**
   * Test that an IllegalArgumentException is thrown when multiple arguments are given.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testMain_multipleArgs_exception() throws Exception {
    // Call the method under test.
    AutomaticSymlinkUtility.main(new String[] {"", ""});
  }

  /**
   * Test that the link is skipped when the link path does not exist and the target path does not
   * exist.
   */
  @Test
  public void testCreateSymbolicLink_linkNotExistsTargetNotExists_skipped() throws IOException {
    // Set up test data.
    Path testDirectory =
        Files.createTempDirectory("testCreateSymbolicLink_linkNotExistsTargetNotExists_skipped");
    testDirectory.toFile().deleteOnExit();

    Path link = testDirectory.resolve("linkNotExists");
    Path target = testDirectory.resolve("targetNotExists");

    // Call the method under test.
    SymlinkCreationResult result = AutomaticSymlinkUtility.createSymbolicLink(link, target);

    // Perform assertions.
    Assert.assertThat("The result's status did not match the expected value.", result.getStatus(),
        CoreMatchers.is(Status.SKIPPED));

    String expectedMessage =
        String.format("A link was not created because neither '%s' or '%s' exist.", link, target);
    Assert.assertThat("The result's message did not match the expected value.", result.getMessage(),
        CoreMatchers.is(expectedMessage));

    Assert.assertThat("The link path was expected to not exist.", Files.exists(link),
        CoreMatchers.is(false));
    Assert.assertThat("The target path was expected to not exist.", Files.exists(target),
        CoreMatchers.is(false));
  }

  /**
   * Test that the link is created when the link path does not exist and the target path is a file.
   */
  @Test
  public void testCreateSymbolicLink_linkNotExistsTargetIsFile_created() throws IOException {
    // Set up test data.
    Path testDirectory =
        Files.createTempDirectory("testCreateSymbolicLink_linkNotExistsTargetIsFile_created");
    testDirectory.toFile().deleteOnExit();

    Path link = testDirectory.resolve("linkNotExists");

    Path target = Files.createTempFile(testDirectory, "targetFile", null);
    target.toFile().deleteOnExit();

    // Call the method under test.
    SymlinkCreationResult result = AutomaticSymlinkUtility.createSymbolicLink(link, target);
    link.toFile().deleteOnExit();

    // Perform assertions.
    Assert.assertThat("The result's status did not match the expected value.", result.getStatus(),
        CoreMatchers.is(Status.CREATED));

    String expectedMessage =
        String.format("A link was created between '%s' and '%s'.", link, target);
    Assert.assertThat("The result's message did not match the expected value.", result.getMessage(),
        CoreMatchers.is(expectedMessage));

    Assert.assertThat("The link path was expected to be a symbolic link.",
        Files.isSymbolicLink(link), CoreMatchers.is(true));
    Assert.assertThat("The link path did not point to the expected file.",
        Files.readSymbolicLink(link), CoreMatchers.is(target));
  }

  /**
   * Test that the link is created when the link path does not exist and the target path is a
   * directory.
   */
  @Test
  public void testCreateSymbolicLink_linkNotExistsTargetIsDirectory_created() throws IOException {
    // Set up test data.
    Path testDirectory =
        Files.createTempDirectory("testCreateSymbolicLink_linkNotExistsTargetIsDirectory_created");
    testDirectory.toFile().deleteOnExit();

    Path link = testDirectory.resolve("linkNotExists");

    Path target = Files.createTempDirectory(testDirectory, "targetDirectory");
    target.toFile().deleteOnExit();

    // Call the method under test.
    SymlinkCreationResult result = AutomaticSymlinkUtility.createSymbolicLink(link, target);
    link.toFile().deleteOnExit();

    // Perform assertions.
    Assert.assertThat("The result's status did not match the expected value.", result.getStatus(),
        CoreMatchers.is(Status.CREATED));

    String expectedMessage =
        String.format("A link was created between '%s' and '%s'.", link, target);
    Assert.assertThat("The result's message did not match the expected value.", result.getMessage(),
        CoreMatchers.is(expectedMessage));

    Assert.assertThat("The link path was expected to be a symbolic link.",
        Files.isSymbolicLink(link), CoreMatchers.is(true));
    Assert.assertThat("The link path did not point to the expected file.",
        Files.readSymbolicLink(link), CoreMatchers.is(target));
  }

  /**
   * Test that the link is created when the link path does not exist and the target path is a
   * symbolic link.
   */
  @Test
  public void testCreateSymbolicLink_linkNotExistsTargetIsSymbolicLink_created()
      throws IOException {
    // Set up test data.
    Path testDirectory = Files
        .createTempDirectory("testCreateSymbolicLink_linkNotExistsTargetIsSymbolicLink_created");
    testDirectory.toFile().deleteOnExit();

    Path link = testDirectory.resolve("linkNotExists");

    Path targetLinkTarget = Files.createTempFile(testDirectory, "targetLinkFile", null);
    targetLinkTarget.toFile().deleteOnExit();
    Path target =
        Files.createSymbolicLink(testDirectory.resolve("targetSymbolicLink"), targetLinkTarget);
    target.toFile().deleteOnExit();

    // Call the method under test.
    SymlinkCreationResult result = AutomaticSymlinkUtility.createSymbolicLink(link, target);
    link.toFile().deleteOnExit();

    // Perform assertions.
    Assert.assertThat("The result's status did not match the expected value.", result.getStatus(),
        CoreMatchers.is(Status.CREATED));

    String expectedMessage =
        String.format("A link was created between '%s' and '%s'.", link, target);
    Assert.assertThat("The result's message did not match the expected value.", result.getMessage(),
        CoreMatchers.is(expectedMessage));

    Assert.assertThat("The link path was expected to be a symbolic link.",
        Files.isSymbolicLink(link), CoreMatchers.is(true));
    Assert.assertThat("The link path did not point to the expected file.",
        Files.readSymbolicLink(link), CoreMatchers.is(target));
  }

  /**
   * Test that the link file is moved and the link created when the link path is a file and the
   * target path does not exist.
   */
  @Test
  public void testCreateSymbolicLink_linkIsFileTargetNotExists_created() throws IOException {
    // Set up test data.
    Path testDirectory =
        Files.createTempDirectory("testCreateSymbolicLink_linkIsFileTargetNotExists_created");
    testDirectory.toFile().deleteOnExit();

    Path link = Files.createTempFile(testDirectory, "linkFile", null);
    link.toFile().deleteOnExit();

    Path target = testDirectory.resolve("targetNotExists");

    // Call the method under test.
    SymlinkCreationResult result = AutomaticSymlinkUtility.createSymbolicLink(link, target);
    target.toFile().deleteOnExit();

    // Perform assertions.
    Assert.assertThat("The result's status did not match the expected value.", result.getStatus(),
        CoreMatchers.is(Status.CREATED));

    String expectedMessage =
        String.format("A link was created between '%s' and '%s'.", link, target);
    Assert.assertThat("The result's message did not match the expected value.", result.getMessage(),
        CoreMatchers.is(expectedMessage));

    Assert.assertThat("The link path was expected to be a symbolic link.",
        Files.isSymbolicLink(link), CoreMatchers.is(true));
    Assert.assertThat("The link path did not point to the expected file.",
        Files.readSymbolicLink(link), CoreMatchers.is(target));
    Assert.assertThat("The target path was expected to be a file.", Files.isRegularFile(target),
        CoreMatchers.is(true));
  }

  /**
   * Test that the link is skipped when the link path is a file and the target path is a file.
   */
  @Test
  public void testCreateSymbolicLink_linkIsFileTargetIsFile_skipped() throws IOException {
    // Set up test data.
    Path testDirectory =
        Files.createTempDirectory("testCreateSymbolicLink_linkIsFileTargetIsFile_skipped");
    testDirectory.toFile().deleteOnExit();

    Path link = Files.createTempFile(testDirectory, "linkFile", null);
    link.toFile().deleteOnExit();

    Path target = Files.createTempFile(testDirectory, "targetFile", null);
    target.toFile().deleteOnExit();

    // Call the method under test.
    SymlinkCreationResult result = AutomaticSymlinkUtility.createSymbolicLink(link, target);
    link.toFile().deleteOnExit();

    // Perform assertions.
    Assert.assertThat("The result's status did not match the expected value.", result.getStatus(),
        CoreMatchers.is(Status.SKIPPED));

    String expectedMessage =
        String.format("A link was not created because both '%s' and '%s' exist.", link, target);
    Assert.assertThat("The result's message did not match the expected value.", result.getMessage(),
        CoreMatchers.is(expectedMessage));

    Assert.assertThat("The link path was expected to be a file.",
        Files.isRegularFile(link, LinkOption.NOFOLLOW_LINKS), CoreMatchers.is(true));
    Assert.assertThat("The target path was expected to be a file.",
        Files.isRegularFile(target, LinkOption.NOFOLLOW_LINKS), CoreMatchers.is(true));
  }

  /**
   * Test that the link is skipped when the link path is a file and the target path is a directory.
   */
  @Test
  public void testCreateSymbolicLink_linkIsFileTargetIsDirectory_skipped() throws IOException {
    // Set up test data.
    Path testDirectory =
        Files.createTempDirectory("testCreateSymbolicLink_linkIsFileTargetIsDirectory_skipped");
    testDirectory.toFile().deleteOnExit();

    Path link = Files.createTempFile(testDirectory, "linkFile", null);
    link.toFile().deleteOnExit();

    Path target = Files.createTempDirectory(testDirectory, "targetDirectory");
    target.toFile().deleteOnExit();

    // Call the method under test.
    SymlinkCreationResult result = AutomaticSymlinkUtility.createSymbolicLink(link, target);
    link.toFile().deleteOnExit();

    // Perform assertions.
    Assert.assertThat("The result's status did not match the expected value.", result.getStatus(),
        CoreMatchers.is(Status.SKIPPED));

    String expectedMessage =
        String.format("A link was not created because both '%s' and '%s' exist.", link, target);
    Assert.assertThat("The result's message did not match the expected value.", result.getMessage(),
        CoreMatchers.is(expectedMessage));

    Assert.assertThat("The link path was expected to be a file.",
        Files.isRegularFile(link, LinkOption.NOFOLLOW_LINKS), CoreMatchers.is(true));
    Assert.assertThat("The target path was expected to be a directory.",
        Files.isDirectory(target, LinkOption.NOFOLLOW_LINKS), CoreMatchers.is(true));
  }

  /**
   * Test that the link is skipped when the link path is a file and the target path is a symbolic
   * link.
   */
  @Test
  public void testCreateSymbolicLink_linkIsFileTargetIsSymbolicLink_skipped() throws IOException {
    // Set up test data.
    Path testDirectory =
        Files.createTempDirectory("testCreateSymbolicLink_linkIsFileTargetIsSymbolicLink_skipped");
    testDirectory.toFile().deleteOnExit();

    Path link = Files.createTempFile(testDirectory, "linkFile", null);
    link.toFile().deleteOnExit();

    Path targetLinkTarget = Files.createTempFile(testDirectory, "targetLinkFile", null);
    targetLinkTarget.toFile().deleteOnExit();
    Path target =
        Files.createSymbolicLink(testDirectory.resolve("targetSymbolicLink"), targetLinkTarget);
    target.toFile().deleteOnExit();

    // Call the method under test.
    SymlinkCreationResult result = AutomaticSymlinkUtility.createSymbolicLink(link, target);
    link.toFile().deleteOnExit();

    // Perform assertions.
    Assert.assertThat("The result's status did not match the expected value.", result.getStatus(),
        CoreMatchers.is(Status.SKIPPED));

    String expectedMessage =
        String.format("A link was not created because both '%s' and '%s' exist.", link, target);
    Assert.assertThat("The result's message did not match the expected value.", result.getMessage(),
        CoreMatchers.is(expectedMessage));

    Assert.assertThat("The link path was expected to be a file.",
        Files.isRegularFile(link, LinkOption.NOFOLLOW_LINKS), CoreMatchers.is(true));
    Assert.assertThat("The target path was expected to be a symbolic link.",
        Files.isSymbolicLink(target), CoreMatchers.is(true));
  }
}

