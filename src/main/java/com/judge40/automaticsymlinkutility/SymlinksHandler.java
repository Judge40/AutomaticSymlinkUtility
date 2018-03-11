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

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * A SAX event handler which reads symlink definition files and creates the defined links.
 */
public class SymlinksHandler extends DefaultHandler {

  private static final String GROUP_ELEMENT = "group";
  private static final String SYMLINK_ELEMENT = "symlink";
  private static final String LINK_PATH_ELEMENT = "linkPath";
  private static final String TARGET_PATH_ELEMENT = "targetPath";

  private boolean skipElements = false;
  private boolean resetSkipAfterLink = false;

  private boolean captureCharacters = false;
  private StringBuilder characters = null;

  private String groupName = null;
  private Path link = null;
  private Path target = null;

  /**
   * Notify that the document has begun.
   */
  @Override
  public void startDocument() {
    System.out.println("Automatic Symlink Utility started...");
  }

  /**
   * Processes element start tags, checks whether groups or symlinks should be skipped based on the
   * context attribute and sets characters to be captured if the element is a symlink component.
   * 
   * @param uri Not used.
   * @param localName Not used.
   * @param qualifiedName The qualified name.
   * @param attributes The element's attributes.
   */
  @Override
  public void startElement(String uri, String localName, String qualifiedName,
      Attributes attributes) {
    if (!skipElements) {
      if (qualifiedName.equals(GROUP_ELEMENT) || qualifiedName.equals(SYMLINK_ELEMENT)) {
        String contextPath = attributes.getValue("contextPath");

        // If the context path is specified and does not exist then set the group or link to be
        // skipped.
        if (contextPath != null) {
          Path context = Paths.get(contextPath);

          skipElements = Files.notExists(context);
          resetSkipAfterLink = skipElements && qualifiedName.equals(SYMLINK_ELEMENT);
        }

        // Output a message indicating whether a group is being skipped or not and when symlinks are
        // being skipped.
        if (groupName == null) {
          System.out.println();
        }

        if (qualifiedName.equals(GROUP_ELEMENT)) {
          groupName = attributes.getValue("name");

          if (skipElements) {
            System.out.println(
                String.format("Skipping the '%s' group because its context '%s' did not exist.",
                    groupName, contextPath));
          } else {
            System.out.println(String.format("Managing symlinks for '%s' group...", groupName));
          }
        } else if (skipElements) {
          System.out.println(String
              .format("Skipping a symlink because its context '%s' did not exist.", contextPath));
        }
      } else {
        // Characters only need capturing for linkPath and targetPath elements.
        captureCharacters =
            qualifiedName.equals(LINK_PATH_ELEMENT) || qualifiedName.equals(TARGET_PATH_ELEMENT);
      }
    }
  }

  /**
   * Captures the characters in to a {@link StringBuilder} if the element is one to be processed.
   * 
   * @param ch The characters.
   * @param start The start position in the character array.
   * @param length The number of characters to use from the character array.
   */
  @Override
  public void characters(char[] ch, int start, int length) {
    if (captureCharacters) {
      if (characters == null) {
        characters = new StringBuilder();
      }

      characters.append(ch, start, length);
    }
  }

  /**
   * Processes element end tags, stores the target if the element is a symlink component or calls
   * the symbolic link creation if the element is a symlink. Element skipping will be reset when the
   * element is the end of the region to be skipped.
   * 
   * @param uri Not used.
   * @param localName Not used.
   * @param qualifiedName The qualified name.
   */
  @Override
  public void endElement(String uri, String localName, String qualifiedName) {
    // If elements are not being skipped then process then depending on the element name, otherwise
    // reset the skip flags based on whether a whole group or single symlink is being skipped.
    if (!skipElements) {
      if (qualifiedName.equals(LINK_PATH_ELEMENT)) {
        link = Paths.get(characters.toString());
      } else if (qualifiedName.equals(TARGET_PATH_ELEMENT)) {
        target = Paths.get(characters.toString());
      } else if (qualifiedName.equals(SYMLINK_ELEMENT)) {
        SymlinkCreationResult result = AutomaticSymlinkUtility.createSymbolicLink(link, target);
        System.out.println(result.getMessage());
        link = null;
        target = null;
      }
    } else if (qualifiedName.equals(SYMLINK_ELEMENT)) {
      skipElements = !resetSkipAfterLink;
      resetSkipAfterLink = false;
    } else if (qualifiedName.equals(GROUP_ELEMENT)) {
      skipElements = false;
    }

    // Reset group name and characters.
    if (qualifiedName.equals(GROUP_ELEMENT)) {
      System.out.println(String.format("Group '%s' finished.", groupName));
      groupName = null;
    }
    characters = null;
  }

  /**
   * Notify that the end of the document has been reached.
   */
  @Override
  public void endDocument() {
    System.out.println();
    System.out.println("Automatic Symlink Utility finished.");
  }

  @Override
  public void warning(SAXParseException spe) throws SAXException {
    // TODO: Try to continue processing other groups/links.
    throw spe;
  }

  @Override
  public void error(SAXParseException spe) throws SAXException {
    // TODO: Try to continue processing other groups/links.
    throw spe;
  }

  @Override
  public void fatalError(SAXParseException spe) throws SAXException {
    // TODO: Try to continue processing other groups/links.
    throw spe;
  }
}
