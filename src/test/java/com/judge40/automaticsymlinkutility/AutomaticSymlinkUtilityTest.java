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

import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import org.junit.Test;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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
}
