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

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

/**
 * A utility for managing Symbolic links automatically based on pre-defined values.
 */
public class AutomaticSymlinkUtility {

  private static final String LINK_CREATED = "A link was created between '%s' and '%s'.";
  private static final String LINK_NOT_CREATED_ALREADY_LINK =
      "A link was not created because '%s' is already a symbolic link.";
  private static final String LINK_NOT_CREATED_BOTH_EXIST =
      "A link was not created because both '%s' and '%s' exist.";
  private static final String LINK_NOT_CREATED_NEITHER_EXIST =
      "A link was not created because neither '%s' or '%s' exist.";
  private static final String LINK_NOT_CREATED_UNKNOWN_STATE =
      "A link was not created because '%s' and '%s' were in an unknown state.";

  /**
   * Parses the symlink definition file and creates symbolic links as needed.
   * 
   * @param args Should contain the path to the symlinks definition files as the only argument.
   * @throws IOException An IO exception from the parser, possibly from a byte stream or character
   *         stream supplied by the application.
   * @throws ParserConfigurationException If a parser cannot be created which satisfies the
   *         requested configuration.
   * @throws SAXException Any SAX exception, possibly wrapping another exception.
   * @throws URISyntaxException When the symlinks schema can not be loaded.
   */
  public static void main(String[] args)
      throws IOException, ParserConfigurationException, SAXException, URISyntaxException {
    // Verify that the arguments are correct and point to an existing file.
    if (args.length != 1) {
      throw new IllegalArgumentException("Wrong number of arguments, one expected.");
    }

    Path definitions = Paths.get(args[0]);

    if (Files.notExists(definitions)) {
      throw new IllegalArgumentException(
          String.format("The symlink definition file '%s' does not exist.", definitions));
    }

    // Set the parser to use the symlinks.xsd schema.
    URL symlinksResource =
        AutomaticSymlinkUtility.class.getClassLoader().getResource("symlinks.xsd");
    Path schemaPath = Paths.get(symlinksResource.toURI());
    SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    Schema schema = schemaFactory.newSchema(schemaPath.toFile());

    SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
    saxParserFactory.setSchema(schema);

    // Get the XMLReader.
    SAXParser saxParser = saxParserFactory.newSAXParser();
    XMLReader xmlReader = saxParser.getXMLReader();

    // Set the content and error handlers.
    SymlinksHandler symlinksHandler = new SymlinksHandler();
    xmlReader.setContentHandler(symlinksHandler);
    xmlReader.setErrorHandler(symlinksHandler);

    // Parse the definition file.
    xmlReader.parse(definitions.toString());
  }

  /**
   * Create a symbolic link between the given paths, if the link path is an existing file then it
   * will be moved to the target path, if the target path does not already exist, and linked back
   * to.
   * 
   * @param link The path to create the symbolic link at.
   * @param target The path to the file to link to.
   * @return A {@link SymlinkCreationResult} with a status and message based on the actions taken.
   */
  protected static SymlinkCreationResult createSymbolicLink(Path link, Path target) {
    Status status = Status.FAILED;
    String message = String.format(LINK_NOT_CREATED_UNKNOWN_STATE, link, target);

    boolean createLink = false;

    try {
      if (Files.exists(link)) {
        if (Files.isSymbolicLink(link)) {
          status = Status.SKIPPED;
          message = String.format(LINK_NOT_CREATED_ALREADY_LINK, link);
        } else if (Files.exists(target)) {
          status = Status.SKIPPED;
          message = String.format(LINK_NOT_CREATED_BOTH_EXIST, link, target);
        } else if (Files.isRegularFile(link)) {
          Files.move(link, target);
          createLink = true;
        } else if (Files.isDirectory(link)) {
          MoveDirectoryVisitor moveVisitor = new MoveDirectoryVisitor(link, target);
          Files.walkFileTree(link, moveVisitor);
          createLink = true;
        }
      } else {
        if (Files.exists(target)) {
          createLink = true;
        } else {
          status = Status.SKIPPED;
          message = String.format(LINK_NOT_CREATED_NEITHER_EXIST, link, target);
        }
      }

      if (createLink) {
        Files.createSymbolicLink(link, target);
        status = Status.CREATED;
        message = String.format(LINK_CREATED, link, target);
      }
    } catch (IOException ioe) {
      status = Status.FAILED;
      message = ioe.getLocalizedMessage();
    }

    return new SymlinkCreationResult(status, message);
  }
}
