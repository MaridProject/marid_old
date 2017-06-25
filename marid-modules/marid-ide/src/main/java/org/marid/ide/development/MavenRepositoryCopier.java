package org.marid.ide.development;

import org.marid.ide.common.Directories;
import org.marid.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.logging.Level;

/**
 * @author Dmitry Ovchinnikov
 */
@Profile("development")
@Component
public class MavenRepositoryCopier {

    @Autowired
    public void init(Directories directories) throws IOException {
        final Path m2Repo = directories.getUserHome().resolve(".m2").resolve("repository");
        if (Files.isDirectory(m2Repo)) {
            final Path orgMarid = Paths.get("org", "marid");
            Files.walkFileTree(m2Repo, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    if (!dir.equals(m2Repo)) {
                        final Path relative = m2Repo.relativize(dir);
                        if (relative.startsWith(orgMarid)) {
                            final Path dest = directories.getRepo().resolve(relative);
                            Files.createDirectories(dest);
                        } else if (relative.getNameCount() >= 2) {
                            return FileVisitResult.SKIP_SUBTREE;
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    final Path relative = m2Repo.relativize(file);
                    final Path dest = directories.getRepo().resolve(relative);
                    Files.copy(file, dest, StandardCopyOption.REPLACE_EXISTING);
                    Log.log(Level.INFO, "Copied {0} to {1}", file.getFileName(), dest);
                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }
}
