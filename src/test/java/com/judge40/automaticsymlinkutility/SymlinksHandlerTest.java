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

import mockit.Deencapsulation;
import mockit.Expectations;
import mockit.Mocked;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.AttributesImpl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * The unit tests for {@link SymlinksHandler}.
 */
public class SymlinksHandlerTest {

  private SymlinksHandler handler;

  private ByteArrayOutputStream out = new ByteArrayOutputStream();

  @Before
  public void setUp() throws UnsupportedEncodingException {
    handler = new SymlinksHandler();
    System.setOut(new PrintStream(out, false, StandardCharsets.UTF_8.name()));
  }

  @After
  public void tearDown() {
    System.setOut(System.out);
  }

  /**
   * Test that a message is output.
   */
  @Test
  public void testStartDocument_messageOut() throws UnsupportedEncodingException {
    // Call the method under test.
    handler.startDocument();

    // Perform assertions.
    Assert.assertThat("The expected message was not output.",
        out.toString(StandardCharsets.UTF_8.name()),
        CoreMatchers.is("Automatic Symlink Utility started..." + System.lineSeparator()));
  }

  /**
   * Test that no action is performed when the element is symlinks and skipElements is false.
   */
  @Test
  public void testStartElement_symlinksSkipFalse_noAction() throws UnsupportedEncodingException {
    // Call the method under test.
    handler.startElement(null, null, "symlinks", null);

    // Perform assertions.
    boolean skipElements = Deencapsulation.getField(handler, "skipElements");
    Assert.assertThat("The skipElements flag did not match the expected value.", skipElements,
        CoreMatchers.is(false));

    boolean resetSkipAfterLink = Deencapsulation.getField(handler, "resetSkipAfterLink");
    Assert.assertThat("The resetSkipAfterLink flag did not match the expected value.",
        resetSkipAfterLink, CoreMatchers.is(false));

    boolean captureCharacters = Deencapsulation.getField(handler, "captureCharacters");
    Assert.assertThat("The captureCharacters flag did not match the expected value.",
        captureCharacters, CoreMatchers.is(false));

    Assert.assertThat("The expected message was not output.",
        out.toString(StandardCharsets.UTF_8.name()), CoreMatchers.is(""));
  }

  /**
   * Test that no action is performed when the element is symlinks and skipElements is true.
   */
  @Test
  public void testStartElement_symlinksSkipTrue_noAction() throws UnsupportedEncodingException {
    // Set up test data.
    Deencapsulation.setField(handler, "skipElements", true);

    // Call the method under test.
    handler.startElement(null, null, "symlinks", null);

    // Perform assertions.
    boolean skipElements = Deencapsulation.getField(handler, "skipElements");
    Assert.assertThat("The skipElements flag did not match the expected value.", skipElements,
        CoreMatchers.is(true));

    boolean resetSkipAfterLink = Deencapsulation.getField(handler, "resetSkipAfterLink");
    Assert.assertThat("The resetSkipAfterLink flag did not match the expected value.",
        resetSkipAfterLink, CoreMatchers.is(false));

    boolean captureCharacters = Deencapsulation.getField(handler, "captureCharacters");
    Assert.assertThat("The captureCharacters flag did not match the expected value.",
        captureCharacters, CoreMatchers.is(false));

    Assert.assertThat("The expected message was not output.",
        out.toString(StandardCharsets.UTF_8.name()), CoreMatchers.is(""));
  }

  /**
   * Test that a message is output when the element is group, skipElements is false and there is no
   * context path.
   */
  @Test
  public void testStartElement_groupSkipFalseNoContextPath_messageOut()
      throws UnsupportedEncodingException {
    // Call the method under test.
    AttributesImpl attributes = new AttributesImpl();
    attributes.addAttribute(null, null, "name", null, "group1");
    handler.startElement(null, null, "group", attributes);

    // Perform assertions.
    boolean skipElements = Deencapsulation.getField(handler, "skipElements");
    Assert.assertThat("The skipElements flag did not match the expected value.", skipElements,
        CoreMatchers.is(false));

    boolean resetSkipAfterLink = Deencapsulation.getField(handler, "resetSkipAfterLink");
    Assert.assertThat("The resetSkipAfterLink flag did not match the expected value.",
        resetSkipAfterLink, CoreMatchers.is(false));

    boolean captureCharacters = Deencapsulation.getField(handler, "captureCharacters");
    Assert.assertThat("The captureCharacters flag did not match the expected value.",
        captureCharacters, CoreMatchers.is(false));

    String groupName = Deencapsulation.getField(handler, "groupName");
    Assert.assertThat("The group name did not match the expected value.", groupName,
        CoreMatchers.is("group1"));

    Assert.assertThat("The expected message was not output.",
        out.toString(StandardCharsets.UTF_8.name()), CoreMatchers.is(System.lineSeparator()
            + "Managing symlinks for 'group1' group..." + System.lineSeparator()));
  }

  /**
   * Test that skipElements is set to true and a message is output when the element is group, skip
   * elements is false and the context path does not exist.
   */
  @Test
  public void testStartElement_groupSkipFalseContextPathNotExists_skipTrueMessageOut()
      throws UnsupportedEncodingException {
    // Call the method under test.
    AttributesImpl attributes = new AttributesImpl();
    attributes.addAttribute(null, null, "name", null, "group1");
    attributes.addAttribute(null, null, "contextPath", null, "notExists");
    handler.startElement(null, null, "group", attributes);

    // Perform assertions.
    boolean skipElements = Deencapsulation.getField(handler, "skipElements");
    Assert.assertThat("The skipElements flag did not match the expected value.", skipElements,
        CoreMatchers.is(true));

    boolean resetSkipAfterLink = Deencapsulation.getField(handler, "resetSkipAfterLink");
    Assert.assertThat("The resetSkipAfterLink flag did not match the expected value.",
        resetSkipAfterLink, CoreMatchers.is(false));

    boolean captureCharacters = Deencapsulation.getField(handler, "captureCharacters");
    Assert.assertThat("The captureCharacters flag did not match the expected value.",
        captureCharacters, CoreMatchers.is(false));

    String groupName = Deencapsulation.getField(handler, "groupName");
    Assert.assertThat("The group name did not match the expected value.", groupName,
        CoreMatchers.is("group1"));

    Assert.assertThat("The expected message was not output.",
        out.toString(StandardCharsets.UTF_8.name()),
        CoreMatchers.is(System.lineSeparator()
            + "Skipping the 'group1' group because its context 'notExists' did not exist."
            + System.lineSeparator()));
  }

  /**
   * Test that a message is output when the element is group, skipElements is false and the context
   * path does exist.
   */
  @Test
  public void testStartElement_groupSkipFalseContextPathExists_messageOut() throws IOException {
    // Call the method under test.
    AttributesImpl attributes = new AttributesImpl();
    attributes.addAttribute(null, null, "name", null, "group1");
    Path tempFile =
        Files.createTempFile("testStartElement_groupSkipFalseContextPathExists_messageOut", null);
    tempFile.toFile().deleteOnExit();
    attributes.addAttribute(null, null, "contextPath", null, tempFile.toString());
    handler.startElement(null, null, "group", attributes);

    // Perform assertions.
    boolean skipElements = Deencapsulation.getField(handler, "skipElements");
    Assert.assertThat("The skipElements flag did not match the expected value.", skipElements,
        CoreMatchers.is(false));

    boolean resetSkipAfterLink = Deencapsulation.getField(handler, "resetSkipAfterLink");
    Assert.assertThat("The resetSkipAfterLink flag did not match the expected value.",
        resetSkipAfterLink, CoreMatchers.is(false));

    boolean captureCharacters = Deencapsulation.getField(handler, "captureCharacters");
    Assert.assertThat("The captureCharacters flag did not match the expected value.",
        captureCharacters, CoreMatchers.is(false));

    String groupName = Deencapsulation.getField(handler, "groupName");
    Assert.assertThat("The group name did not match the expected value.", groupName,
        CoreMatchers.is("group1"));

    Assert.assertThat("The expected message was not output.",
        out.toString(StandardCharsets.UTF_8.name()), CoreMatchers.is(System.lineSeparator()
            + "Managing symlinks for 'group1' group..." + System.lineSeparator()));
  }

  /**
   * Test that no action is performed when the element is group and skipElements is true.
   */
  @Test
  public void testStartElemeSkipTrue_noAction() throws UnsupportedEncodingException {
    // Set up test data.
    Deencapsulation.setField(handler, "skipElements", true);

    // Call the method under test.
    handler.startElement(null, null, "group", null);

    // Perform assertions.
    boolean skipElements = Deencapsulation.getField(handler, "skipElements");
    Assert.assertThat("The skipElements flag did not match the expected value.", skipElements,
        CoreMatchers.is(true));

    boolean resetSkipAfterLink = Deencapsulation.getField(handler, "resetSkipAfterLink");
    Assert.assertThat("The resetSkipAfterLink flag did not match the expected value.",
        resetSkipAfterLink, CoreMatchers.is(false));

    boolean captureCharacters = Deencapsulation.getField(handler, "captureCharacters");
    Assert.assertThat("The captureCharacters flag did not match the expected value.",
        captureCharacters, CoreMatchers.is(false));

    Assert.assertThat("The expected message was not output.",
        out.toString(StandardCharsets.UTF_8.name()), CoreMatchers.is(""));
  }

  /**
   * Test that no action is performed when the element is symlink, skipElements is false and there
   * is no context path.
   */
  @Test
  public void testStartElement_symlinkSkipFalseNoContextPath_noAction()
      throws UnsupportedEncodingException {
    // Call the method under test.
    AttributesImpl attributes = new AttributesImpl();
    handler.startElement(null, null, "symlink", attributes);

    // Perform assertions.
    boolean skipElements = Deencapsulation.getField(handler, "skipElements");
    Assert.assertThat("The skipElements flag did not match the expected value.", skipElements,
        CoreMatchers.is(false));

    boolean resetSkipAfterLink = Deencapsulation.getField(handler, "resetSkipAfterLink");
    Assert.assertThat("The resetSkipAfterLink flag did not match the expected value.",
        resetSkipAfterLink, CoreMatchers.is(false));

    boolean captureCharacters = Deencapsulation.getField(handler, "captureCharacters");
    Assert.assertThat("The captureCharacters flag did not match the expected value.",
        captureCharacters, CoreMatchers.is(false));

    Assert.assertThat("The expected message was not output.",
        out.toString(StandardCharsets.UTF_8.name()), CoreMatchers.is(System.lineSeparator()));
  }

  /**
   * Test that skipElements is set to true, resetSkipAfterLink is set to true and a message is
   * output when the element is symlink, skipElements is false and the context path does not exist.
   */
  @Test
  public void testStartElement_symlinkSkipFalseContextPathNotExists_skipTrueResetTrueMessageOut()
      throws UnsupportedEncodingException {
    // Call the method under test.
    AttributesImpl attributes = new AttributesImpl();
    attributes.addAttribute(null, null, "contextPath", null, "notExists");
    handler.startElement(null, null, "symlink", attributes);

    // Perform assertions.
    boolean skipElements = Deencapsulation.getField(handler, "skipElements");
    Assert.assertThat("The skipElements flag did not match the expected value.", skipElements,
        CoreMatchers.is(true));

    boolean resetSkipAfterLink = Deencapsulation.getField(handler, "resetSkipAfterLink");
    Assert.assertThat("The resetSkipAfterLink flag did not match the expected value.",
        resetSkipAfterLink, CoreMatchers.is(true));

    boolean captureCharacters = Deencapsulation.getField(handler, "captureCharacters");
    Assert.assertThat("The captureCharacters flag did not match the expected value.",
        captureCharacters, CoreMatchers.is(false));

    Assert.assertThat("The expected message was not output.",
        out.toString(StandardCharsets.UTF_8.name()),
        CoreMatchers.is(System.lineSeparator()
            + "Skipping a symlink because its context 'notExists' did not exist."
            + System.lineSeparator()));
  }

  /**
   * Test that no action is performed when the element is symlink, skipElements is false and the
   * context path does exist.
   */
  @Test
  public void testStartElement_symlinkSkipFalseContextPathExists_noAction() throws IOException {
    // Call the method under test.
    AttributesImpl attributes = new AttributesImpl();
    Path tempFile =
        Files.createTempFile("testStartElement_symlinkSkipFalseContextPathExists_noAction", null);
    tempFile.toFile().deleteOnExit();
    attributes.addAttribute(null, null, "contextPath", null, tempFile.toString());
    handler.startElement(null, null, "symlink", attributes);

    // Perform assertions.
    boolean skipElements = Deencapsulation.getField(handler, "skipElements");
    Assert.assertThat("The skipElements flag did not match the expected value.", skipElements,
        CoreMatchers.is(false));

    boolean resetSkipAfterLink = Deencapsulation.getField(handler, "resetSkipAfterLink");
    Assert.assertThat("The resetSkipAfterLink flag did not match the expected value.",
        resetSkipAfterLink, CoreMatchers.is(false));

    boolean captureCharacters = Deencapsulation.getField(handler, "captureCharacters");
    Assert.assertThat("The captureCharacters flag did not match the expected value.",
        captureCharacters, CoreMatchers.is(false));

    Assert.assertThat("The expected message was not output.",
        out.toString(StandardCharsets.UTF_8.name()), CoreMatchers.is(System.lineSeparator()));
  }

  /**
   * Test that no action is performed when the element is symlink and skipElements is true.
   */
  @Test
  public void testStartElement_symlinkSkipTrue_noAction() throws UnsupportedEncodingException {
    // Set up test data.
    Deencapsulation.setField(handler, "skipElements", true);

    // Call the method under test.
    handler.startElement(null, null, "symlink", null);

    // Perform assertions.
    boolean skipElements = Deencapsulation.getField(handler, "skipElements");
    Assert.assertThat("The skipElements flag did not match the expected value.", skipElements,
        CoreMatchers.is(true));

    boolean resetSkipAfterLink = Deencapsulation.getField(handler, "resetSkipAfterLink");
    Assert.assertThat("The resetSkipAfterLink flag did not match the expected value.",
        resetSkipAfterLink, CoreMatchers.is(false));

    boolean captureCharacters = Deencapsulation.getField(handler, "captureCharacters");
    Assert.assertThat("The captureCharacters flag did not match the expected value.",
        captureCharacters, CoreMatchers.is(false));

    Assert.assertThat("The expected message was not output.",
        out.toString(StandardCharsets.UTF_8.name()), CoreMatchers.is(""));
  }

  /**
   * Test that captureCharacters is set to true when the element is linkPath and skipElements is
   * false.
   */
  @Test
  public void testStartElement_linkPathSkipFalse_captureCharactersTrue()
      throws UnsupportedEncodingException {
    // Call the method under test.
    handler.startElement(null, null, "linkPath", null);

    // Perform assertions.
    boolean skipElements = Deencapsulation.getField(handler, "skipElements");
    Assert.assertThat("The skipElements flag did not match the expected value.", skipElements,
        CoreMatchers.is(false));

    boolean resetSkipAfterLink = Deencapsulation.getField(handler, "resetSkipAfterLink");
    Assert.assertThat("The resetSkipAfterLink flag did not match the expected value.",
        resetSkipAfterLink, CoreMatchers.is(false));

    boolean captureCharacters = Deencapsulation.getField(handler, "captureCharacters");
    Assert.assertThat("The captureCharacters flag did not match the expected value.",
        captureCharacters, CoreMatchers.is(true));

    Assert.assertThat("The expected message was not output.",
        out.toString(StandardCharsets.UTF_8.name()), CoreMatchers.is(""));
  }

  /**
   * Test that no action is performed when the element is linkPath and skipElements is true.
   */
  @Test
  public void testStartElement_linkPathSkipTrue_noAction() throws UnsupportedEncodingException {
    // Set up test data.
    Deencapsulation.setField(handler, "skipElements", true);

    // Call the method under test.
    handler.startElement(null, null, "linkPath", null);

    // Perform assertions.
    boolean skipElements = Deencapsulation.getField(handler, "skipElements");
    Assert.assertThat("The skipElements flag did not match the expected value.", skipElements,
        CoreMatchers.is(true));

    boolean resetSkipAfterLink = Deencapsulation.getField(handler, "resetSkipAfterLink");
    Assert.assertThat("The resetSkipAfterLink flag did not match the expected value.",
        resetSkipAfterLink, CoreMatchers.is(false));

    boolean captureCharacters = Deencapsulation.getField(handler, "captureCharacters");
    Assert.assertThat("The captureCharacters flag did not match the expected value.",
        captureCharacters, CoreMatchers.is(false));

    Assert.assertThat("The expected message was not output.",
        out.toString(StandardCharsets.UTF_8.name()), CoreMatchers.is(""));
  }

  /**
   * Test that captureCharacters is set to true when the element is targetPath and skipElements is
   * false.
   */
  @Test
  public void testStartElement_targetPathSkipFalse_captureCharactersTrue()
      throws UnsupportedEncodingException {
    // Call the method under test.
    handler.startElement(null, null, "targetPath", null);

    // Perform assertions.
    boolean skipElements = Deencapsulation.getField(handler, "skipElements");
    Assert.assertThat("The skipElements flag did not match the expected value.", skipElements,
        CoreMatchers.is(false));

    boolean resetSkipAfterLink = Deencapsulation.getField(handler, "resetSkipAfterLink");
    Assert.assertThat("The resetSkipAfterLink flag did not match the expected value.",
        resetSkipAfterLink, CoreMatchers.is(false));

    boolean captureCharacters = Deencapsulation.getField(handler, "captureCharacters");
    Assert.assertThat("The captureCharacters flag did not match the expected value.",
        captureCharacters, CoreMatchers.is(true));

    Assert.assertThat("The expected message was not output.",
        out.toString(StandardCharsets.UTF_8.name()), CoreMatchers.is(""));
  }

  /**
   * Test that no action is performed when the element is targetPath and skipElements is true.
   */
  @Test
  public void testStartElement_targetPathSkipTrue_noAction() throws UnsupportedEncodingException {
    // Set up test data.
    Deencapsulation.setField(handler, "skipElements", true);

    // Call the method under test.
    handler.startElement(null, null, "targetPath", null);

    // Perform assertions.
    boolean skipElements = Deencapsulation.getField(handler, "skipElements");
    Assert.assertThat("The skipElements flag did not match the expected value.", skipElements,
        CoreMatchers.is(true));

    boolean resetSkipAfterLink = Deencapsulation.getField(handler, "resetSkipAfterLink");
    Assert.assertThat("The resetSkipAfterLink flag did not match the expected value.",
        resetSkipAfterLink, CoreMatchers.is(false));

    boolean captureCharacters = Deencapsulation.getField(handler, "captureCharacters");
    Assert.assertThat("The captureCharacters flag did not match the expected value.",
        captureCharacters, CoreMatchers.is(false));

    Assert.assertThat("The expected message was not output.",
        out.toString(StandardCharsets.UTF_8.name()), CoreMatchers.is(""));
  }

  /**
   * Test that the characters build is null when capture characters is false.
   */
  @Test
  public void testCharacters_captureCharactersFalse_null() {
    // Set up test data.
    Deencapsulation.setField(handler, "captureCharacters", false);

    // Call the method under test.
    handler.characters("input".toCharArray(), 2, 3);

    // Perform assertions.
    StringBuilder characters = Deencapsulation.getField(handler, "characters");
    Assert.assertThat("The characters did not match the expected value.", characters,
        CoreMatchers.nullValue());
  }

  /**
   * Test that the expected characters are populated when capture characters is true and a single
   * call is made.
   */
  @Test
  public void testCharacters_captureCharactersTrueSingleCall_expectedCharacters() {
    // Set up test data.
    Deencapsulation.setField(handler, "captureCharacters", true);

    // Call the method under test.
    handler.characters("input".toCharArray(), 2, 3);

    // Perform assertions.
    StringBuilder characters = Deencapsulation.getField(handler, "characters");
    Assert.assertThat("The characters did not match the expected value.", characters.toString(),
        CoreMatchers.is("put"));
  }

  /**
   * Test that the expected characters are populated when capture characters is true and multiple
   * calls are made.
   */
  @Test
  public void testCharacters_captureCharactersTrueMultipleCalls_expectedCharacters() {
    // Set up test data.
    Deencapsulation.setField(handler, "captureCharacters", true);

    // Call the method under test.
    handler.characters("firstInput".toCharArray(), 7, 3);
    handler.characters("secondInput".toCharArray(), 3, 4);

    // Perform assertions.
    StringBuilder characters = Deencapsulation.getField(handler, "characters");
    Assert.assertThat("The characters did not match the expected value.", characters.toString(),
        CoreMatchers.is("putondI"));
  }

  /**
   * Test that characters is set to null when the element is symlinks and skipElements is false.
   */
  @Test
  public void testEndElement_symlinksSkipFalse_charactersNull() {
    // Set up test data.
    Deencapsulation.setField(handler, "characters", new StringBuilder());

    // Call the method under test.
    handler.endElement(null, null, "symlinks");

    // Perform assertions.
    boolean skipElements = Deencapsulation.getField(handler, "skipElements");
    Assert.assertThat("The skipElements flag did not match the expected value.", skipElements,
        CoreMatchers.is(false));

    boolean resetSkipAfterLink = Deencapsulation.getField(handler, "resetSkipAfterLink");
    Assert.assertThat("The resetSkipAfterLink flag did not match the expected value.",
        resetSkipAfterLink, CoreMatchers.is(false));

    StringBuilder characters = Deencapsulation.getField(handler, "characters");
    Assert.assertThat("The characters did not match the expected value.", characters,
        CoreMatchers.nullValue());

    Path link = Deencapsulation.getField(handler, "link");
    Assert.assertThat("The link path did not match the expected value.", link,
        CoreMatchers.nullValue());

    Path target = Deencapsulation.getField(handler, "target");
    Assert.assertThat("The target path did not match the expected value.", target,
        CoreMatchers.nullValue());
  }

  /**
   * Test that characters is set to null when the element is symlinks and skipElements is true.
   */
  @Test
  public void testEndElement_symlinksSkipTrue_charactersNull() {
    // Set up test data.
    Deencapsulation.setField(handler, "skipElements", true);
    Deencapsulation.setField(handler, "characters", new StringBuilder());

    // Call the method under test.
    handler.endElement(null, null, "symlinks");

    // Perform assertions.
    boolean skipElements = Deencapsulation.getField(handler, "skipElements");
    Assert.assertThat("The skipElements flag did not match the expected value.", skipElements,
        CoreMatchers.is(true));

    boolean resetSkipAfterLink = Deencapsulation.getField(handler, "resetSkipAfterLink");
    Assert.assertThat("The resetSkipAfterLink flag did not match the expected value.",
        resetSkipAfterLink, CoreMatchers.is(false));

    StringBuilder characters = Deencapsulation.getField(handler, "characters");
    Assert.assertThat("The characters did not match the expected value.", characters,
        CoreMatchers.nullValue());

    Path link = Deencapsulation.getField(handler, "link");
    Assert.assertThat("The link path did not match the expected value.", link,
        CoreMatchers.nullValue());

    Path target = Deencapsulation.getField(handler, "target");
    Assert.assertThat("The target path did not match the expected value.", target,
        CoreMatchers.nullValue());
  }

  /**
   * Test that characters is set to null when the element is group and skipElements is false.
   */
  @Test
  public void testEndElement_groupSkipFalse_charactersNull() {
    // Set up test data.
    Deencapsulation.setField(handler, "characters", new StringBuilder());
    Deencapsulation.setField(handler, "groupName", "group1");

    // Call the method under test.
    handler.endElement(null, null, "group");

    // Perform assertions.
    boolean skipElements = Deencapsulation.getField(handler, "skipElements");
    Assert.assertThat("The skipElements flag did not match the expected value.", skipElements,
        CoreMatchers.is(false));

    boolean resetSkipAfterLink = Deencapsulation.getField(handler, "resetSkipAfterLink");
    Assert.assertThat("The resetSkipAfterLink flag did not match the expected value.",
        resetSkipAfterLink, CoreMatchers.is(false));

    StringBuilder characters = Deencapsulation.getField(handler, "characters");
    Assert.assertThat("The characters did not match the expected value.", characters,
        CoreMatchers.nullValue());

    String groupName = Deencapsulation.getField(handler, "groupName");
    Assert.assertThat("The group name did not match the expected value.", groupName,
        CoreMatchers.nullValue());

    Path link = Deencapsulation.getField(handler, "link");
    Assert.assertThat("The link path did not match the expected value.", link,
        CoreMatchers.nullValue());

    Path target = Deencapsulation.getField(handler, "target");
    Assert.assertThat("The target path did not match the expected value.", target,
        CoreMatchers.nullValue());
  }

  /**
   * Test that skipElements is set to false and characters is set to null when the element is group
   * and skipElements is true.
   */
  @Test
  public void testEndElement_groupSkipTrue_skipFalseCharactersNull() {
    // Set up test data.
    Deencapsulation.setField(handler, "skipElements", true);
    Deencapsulation.setField(handler, "characters", new StringBuilder());
    Deencapsulation.setField(handler, "groupName", "group1");

    // Call the method under test.
    handler.endElement(null, null, "group");

    // Perform assertions.
    boolean skipElements = Deencapsulation.getField(handler, "skipElements");
    Assert.assertThat("The skipElements flag did not match the expected value.", skipElements,
        CoreMatchers.is(false));

    boolean resetSkipAfterLink = Deencapsulation.getField(handler, "resetSkipAfterLink");
    Assert.assertThat("The resetSkipAfterLink flag did not match the expected value.",
        resetSkipAfterLink, CoreMatchers.is(false));

    StringBuilder characters = Deencapsulation.getField(handler, "characters");
    Assert.assertThat("The characters did not match the expected value.", characters,
        CoreMatchers.nullValue());

    String groupName = Deencapsulation.getField(handler, "groupName");
    Assert.assertThat("The group name did not match the expected value.", groupName,
        CoreMatchers.nullValue());

    Path link = Deencapsulation.getField(handler, "link");
    Assert.assertThat("The link path did not match the expected value.", link,
        CoreMatchers.nullValue());

    Path target = Deencapsulation.getField(handler, "target");
    Assert.assertThat("The target path did not match the expected value.", target,
        CoreMatchers.nullValue());
  }

  /**
   * Test that a symbolic link is created and characters, link and target are set to null when the
   * element is symlink and skipElements is false.
   */
  @Test
  public void testEndElement_symlinkSkipFalse_symlinkCreatedLinkTargetAndCharactersNull(
      @Mocked AutomaticSymlinkUtility symlinkUtility) throws UnsupportedEncodingException {
    // Set up test data.
    Deencapsulation.setField(handler, "characters", new StringBuilder());
    Path initialLink = Paths.get("linkPath");
    Deencapsulation.setField(handler, "link", initialLink);
    Path initialTarget = Paths.get("targetPath");
    Deencapsulation.setField(handler, "target", initialTarget);

    // Record expectations.
    new Expectations() {
      {
        AutomaticSymlinkUtility.createSymbolicLink(initialLink, initialTarget);
        result = new SymlinkCreationResult(Status.CREATED, "Creation message.");
      }
    };

    // Call the method under test.
    handler.endElement(null, null, "symlink");

    // Perform assertions.
    boolean skipElements = Deencapsulation.getField(handler, "skipElements");
    Assert.assertThat("The skipElements flag did not match the expected value.", skipElements,
        CoreMatchers.is(false));

    boolean resetSkipAfterLink = Deencapsulation.getField(handler, "resetSkipAfterLink");
    Assert.assertThat("The resetSkipAfterLink flag did not match the expected value.",
        resetSkipAfterLink, CoreMatchers.is(false));

    StringBuilder characters = Deencapsulation.getField(handler, "characters");
    Assert.assertThat("The characters did not match the expected value.", characters,
        CoreMatchers.nullValue());

    Path link = Deencapsulation.getField(handler, "link");
    Assert.assertThat("The link path did not match the expected value.", link,
        CoreMatchers.nullValue());

    Path target = Deencapsulation.getField(handler, "target");
    Assert.assertThat("The target path did not match the expected value.", target,
        CoreMatchers.nullValue());

    Assert.assertThat("The expected message was not output.",
        out.toString(StandardCharsets.UTF_8.name()),
        CoreMatchers.is("Creation message." + System.lineSeparator()));
  }

  /**
   * Test that skipElements is set to true and characters is set to null when the element is
   * symlink, skipElements is true and resetSkipAfterLink is false.
   */
  @Test
  public void testEndElement_symlinkSkipTrueResetFalse_skipTrueResetFalseCharactersNull() {
    // Set up test data.
    Deencapsulation.setField(handler, "skipElements", true);
    Deencapsulation.setField(handler, "resetSkipAfterLink", false);
    Deencapsulation.setField(handler, "characters", new StringBuilder());

    // Call the method under test.
    handler.endElement(null, null, "symlink");

    // Perform assertions.
    boolean skipElements = Deencapsulation.getField(handler, "skipElements");
    Assert.assertThat("The skipElements flag did not match the expected value.", skipElements,
        CoreMatchers.is(true));

    boolean resetSkipAfterLink = Deencapsulation.getField(handler, "resetSkipAfterLink");
    Assert.assertThat("The resetSkipAfterLink flag did not match the expected value.",
        resetSkipAfterLink, CoreMatchers.is(false));

    StringBuilder characters = Deencapsulation.getField(handler, "characters");
    Assert.assertThat("The characters did not match the expected value.", characters,
        CoreMatchers.nullValue());

    Path link = Deencapsulation.getField(handler, "link");
    Assert.assertThat("The link path did not match the expected value.", link,
        CoreMatchers.nullValue());

    Path target = Deencapsulation.getField(handler, "target");
    Assert.assertThat("The target path did not match the expected value.", target,
        CoreMatchers.nullValue());
  }

  /**
   * Test that skipElements and resetSkipAfterLink are set to false and characters is set to null
   * when the element is symlink, skipElements is true and resetSkipAfterLink is true.
   */
  @Test
  public void testEndElement_symlinkSkipTrueResetFalse_skipFalseResetFalseCharactersNull() {
    // Set up test data.
    Deencapsulation.setField(handler, "skipElements", true);
    Deencapsulation.setField(handler, "resetSkipAfterLink", true);
    Deencapsulation.setField(handler, "characters", new StringBuilder());

    // Call the method under test.
    handler.endElement(null, null, "symlink");

    // Perform assertions.
    boolean skipElements = Deencapsulation.getField(handler, "skipElements");
    Assert.assertThat("The skipElements flag did not match the expected value.", skipElements,
        CoreMatchers.is(false));

    boolean resetSkipAfterLink = Deencapsulation.getField(handler, "resetSkipAfterLink");
    Assert.assertThat("The resetSkipAfterLink flag did not match the expected value.",
        resetSkipAfterLink, CoreMatchers.is(false));

    StringBuilder characters = Deencapsulation.getField(handler, "characters");
    Assert.assertThat("The characters did not match the expected value.", characters,
        CoreMatchers.nullValue());

    Path link = Deencapsulation.getField(handler, "link");
    Assert.assertThat("The link path did not match the expected value.", link,
        CoreMatchers.nullValue());

    Path target = Deencapsulation.getField(handler, "target");
    Assert.assertThat("The target path did not match the expected value.", target,
        CoreMatchers.nullValue());
  }

  /**
   * Test that link is populated and characters is set to null when the element is linkPath and
   * skipElements is false.
   */
  @Test
  public void testEndElement_linkPathSkipFalse_linkPopulatedCharactersNull() {
    // Set up test data.
    Deencapsulation.setField(handler, "characters", new StringBuilder("linkCharacters"));

    // Call the method under test.
    handler.endElement(null, null, "linkPath");

    // Perform assertions.
    boolean skipElements = Deencapsulation.getField(handler, "skipElements");
    Assert.assertThat("The skipElements flag did not match the expected value.", skipElements,
        CoreMatchers.is(false));

    boolean resetSkipAfterLink = Deencapsulation.getField(handler, "resetSkipAfterLink");
    Assert.assertThat("The resetSkipAfterLink flag did not match the expected value.",
        resetSkipAfterLink, CoreMatchers.is(false));

    StringBuilder characters = Deencapsulation.getField(handler, "characters");
    Assert.assertThat("The characters did not match the expected value.", characters,
        CoreMatchers.nullValue());

    Path link = Deencapsulation.getField(handler, "link");
    Assert.assertThat("The link path did not match the expected value.", link,
        CoreMatchers.is(Paths.get("linkCharacters")));

    Path target = Deencapsulation.getField(handler, "target");
    Assert.assertThat("The target path did not match the expected value.", target,
        CoreMatchers.nullValue());
  }

  /**
   * Test that characters is set to null when the element is linkPath and skipElements is true.
   */
  @Test
  public void testEndElement_linkPathSkipTrue_charactersNull() {
    // Set up test data.
    Deencapsulation.setField(handler, "skipElements", true);
    Deencapsulation.setField(handler, "characters", new StringBuilder());

    // Call the method under test.
    handler.endElement(null, null, "linkPath");

    // Perform assertions.
    boolean skipElements = Deencapsulation.getField(handler, "skipElements");
    Assert.assertThat("The skipElements flag did not match the expected value.", skipElements,
        CoreMatchers.is(true));

    boolean resetSkipAfterLink = Deencapsulation.getField(handler, "resetSkipAfterLink");
    Assert.assertThat("The resetSkipAfterLink flag did not match the expected value.",
        resetSkipAfterLink, CoreMatchers.is(false));

    StringBuilder characters = Deencapsulation.getField(handler, "characters");
    Assert.assertThat("The characters did not match the expected value.", characters,
        CoreMatchers.nullValue());

    Path link = Deencapsulation.getField(handler, "link");
    Assert.assertThat("The link path did not match the expected value.", link,
        CoreMatchers.nullValue());

    Path target = Deencapsulation.getField(handler, "target");
    Assert.assertThat("The target path did not match the expected value.", target,
        CoreMatchers.nullValue());
  }

  /**
   * Test that target is populated and characters is set to null when the element is targetPath and
   * skipElements is false.
   */
  @Test
  public void testEndElement_targetPathSkipFalse_targetPopulatedCharactersNull() {
    // Set up test data.
    Deencapsulation.setField(handler, "characters", new StringBuilder("targetCharacters"));

    // Call the method under test.
    handler.endElement(null, null, "targetPath");

    // Perform assertions.
    boolean skipElements = Deencapsulation.getField(handler, "skipElements");
    Assert.assertThat("The skipElements flag did not match the expected value.", skipElements,
        CoreMatchers.is(false));

    boolean resetSkipAfterLink = Deencapsulation.getField(handler, "resetSkipAfterLink");
    Assert.assertThat("The resetSkipAfterLink flag did not match the expected value.",
        resetSkipAfterLink, CoreMatchers.is(false));

    StringBuilder characters = Deencapsulation.getField(handler, "characters");
    Assert.assertThat("The characters did not match the expected value.", characters,
        CoreMatchers.nullValue());

    Path link = Deencapsulation.getField(handler, "link");
    Assert.assertThat("The link path did not match the expected value.", link,
        CoreMatchers.nullValue());

    Path target = Deencapsulation.getField(handler, "target");
    Assert.assertThat("The target path did not match the expected value.", target,
        CoreMatchers.is(Paths.get("targetCharacters")));
  }

  /**
   * Test that characters is set to null when the element is targetPath and skipElements is true.
   */
  @Test
  public void testEndElement_targetPathSkipTrue_charactersNull() {
    // Set up test data.
    Deencapsulation.setField(handler, "skipElements", true);
    Deencapsulation.setField(handler, "characters", new StringBuilder());

    // Call the method under test.
    handler.endElement(null, null, "targetPath");

    // Perform assertions.
    boolean skipElements = Deencapsulation.getField(handler, "skipElements");
    Assert.assertThat("The skipElements flag did not match the expected value.", skipElements,
        CoreMatchers.is(true));

    boolean resetSkipAfterLink = Deencapsulation.getField(handler, "resetSkipAfterLink");
    Assert.assertThat("The resetSkipAfterLink flag did not match the expected value.",
        resetSkipAfterLink, CoreMatchers.is(false));

    StringBuilder characters = Deencapsulation.getField(handler, "characters");
    Assert.assertThat("The characters did not match the expected value.", characters,
        CoreMatchers.nullValue());

    Path link = Deencapsulation.getField(handler, "link");
    Assert.assertThat("The link path did not match the expected value.", link,
        CoreMatchers.nullValue());

    Path target = Deencapsulation.getField(handler, "target");
    Assert.assertThat("The target path did not match the expected value.", target,
        CoreMatchers.nullValue());
  }

  /**
   * Test that a message is output.
   */
  @Test
  public void testEndDocument_messageOut() throws UnsupportedEncodingException {
    // Call the method under test.
    handler.endDocument();

    // Perform assertions.
    Assert.assertThat("The expected message was not output.",
        out.toString(StandardCharsets.UTF_8.name()), CoreMatchers.is(System.lineSeparator()
            + "Automatic Symlink Utility finished." + System.lineSeparator()));
  }

  /**
   * Test that the SAXParseException is re-thrown.
   */
  @Test(expected = SAXParseException.class)
  public void testWarning_exception() throws SAXException {
    // Set up test data.
    SAXParseException exception = new SAXParseException("Expected exception.", null);

    // Call the method under test.
    handler.warning(exception);
  }

  /**
   * Test that the SAXParseException is re-thrown.
   */
  @Test(expected = SAXParseException.class)
  public void testError_exception() throws SAXException {
    // Set up test data.
    SAXParseException exception = new SAXParseException("Expected exception.", null);

    // Call the method under test.
    handler.error(exception);
  }

  /**
   * Test that the SAXParseException is re-thrown.
   */
  @Test(expected = SAXParseException.class)
  public void testFatalError_exception() throws SAXException {
    // Set up test data.
    SAXParseException exception = new SAXParseException("Expected exception.", null);

    // Call the method under test.
    handler.fatalError(exception);
  }
}
