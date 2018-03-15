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

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * A file visitor for moving a directory and all of its files and sub-directories to another
 * location.
 */
public class MoveDirectoryVisitor extends SimpleFileVisitor<Path> {

  private final Path source;
  private final Path destination;

  /**
   * Constructs a MoveDirectoryVisitor with the source path and destination path.
   * 
   * @param source The path of the directory to be moved.
   * @param destination The path the directory will be moved to.
   */
  public MoveDirectoryVisitor(Path source, Path destination) {
    this.source = source;
    this.destination = destination;
  }

  /**
   * Creates the destination sub-directory if it does not already exist.
   * 
   * @param dir The destination directory.
   * @param attrs Not used.
   * @return {@link FileVisitResult#CONTINUE}.
   * @throws IOException If an error occurs creating the directory.
   */
  @Override
  public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
    Path directoryDestination = destination.resolve(source.relativize(dir));

    if (Files.notExists(directoryDestination)) {
      Files.createDirectory(directoryDestination);
    }

    return FileVisitResult.CONTINUE;
  }

  /**
   * Moves the file to the destination directory.
   * 
   * @param file The file to move.
   * @param attrs Not used.
   * @return {@link FileVisitResult#CONTINUE}.
   * @throws IOException If an error occurs trying to move the file.
   */
  @Override
  public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
    Path relativeSource = source.relativize(file);
    Path fileDestination = destination.resolve(relativeSource);

    if (Files.notExists(fileDestination)) {
      Files.move(file, fileDestination);
    }

    return FileVisitResult.CONTINUE;
  }

  /**
   * Deletes the directory once all of its children have been moved.
   * 
   * @param dir The directory to delete.
   * @param exc Any exception thrown processing the directory's children.
   * @return {@link FileVisitResult#CONTINUE}.
   * @throws IOException If an error occurs trying to delete the directory, or an exception was
   *         thrown processing its children.
   */
  @Override
  public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
    if (exc != null) {
      throw exc;
    }

    Files.delete(dir);
    return FileVisitResult.CONTINUE;
  }
}
