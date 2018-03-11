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

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

/**
 * The units tests for {@link SymlinkCreationResult}.
 */
public class SymlinkCreationResultTest {

  /**
   * Test that CREATED is returned when the result is initialised with CREATED.
   */
  @Test
  public void testGetStatus_created_created() {
    // Set up test data.
    SymlinkCreationResult result = new SymlinkCreationResult(Status.CREATED, null);

    // Call the method under test.
    Status status = result.getStatus();

    // Perform assertions.
    Assert.assertThat("The result's status did not match the expected value.", status,
        CoreMatchers.is(Status.CREATED));
  }

  /**
   * Test that FAILED is returned when the result is initialised with FAILED.
   */
  @Test
  public void testGetStatus_failed_failed() {
    // Set up test data.
    SymlinkCreationResult result = new SymlinkCreationResult(Status.FAILED, null);

    // Call the method under test.
    Status status = result.getStatus();

    // Perform assertions.
    Assert.assertThat("The result's status did not match the expected value.", status,
        CoreMatchers.is(Status.FAILED));
  }

  /**
   * Test that SKIPPED is returned when the result is initialised with SKIPPED.
   */
  @Test
  public void testGetStatus_skipped_skipped() {
    // Set up test data.
    SymlinkCreationResult result = new SymlinkCreationResult(Status.SKIPPED, null);

    // Call the method under test.
    Status status = result.getStatus();

    // Perform assertions.
    Assert.assertThat("The result's status did not match the expected value.", status,
        CoreMatchers.is(Status.SKIPPED));
  }

  /**
   * Test that the message is returned when the result is initialised with a message.
   */
  @Test
  public void testGetMessage_message_message() {
    // Set up test data.
    SymlinkCreationResult result = new SymlinkCreationResult(null, "Initialized message.");

    // Call the method under test.
    String message = result.getMessage();

    // Perform assertions.
    Assert.assertThat("The result's message did not match the expected value.", message,
        CoreMatchers.is("Initialized message."));
  }
}
