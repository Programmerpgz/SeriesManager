package hr.algebra.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

public class FileService {
    private FileService() {
    }
    private static final Logger log = LoggerFactory.getLogger(FileService.class);
    private static final Set<String> PROTECTED_IMAGES = Set.of(
            "1775827426087_noimageavaliable.jpg",
            "last_of_us.jpg",
            "stranger_things.jpg",
            "succession.jpg",
            "the_bear.jpg"
    );

    public static void deleteImageFile(String path) {
        if (path == null || path.isEmpty()) {
            return;
        }

        boolean isProtected = PROTECTED_IMAGES.stream()
                .anyMatch(protectedName -> path.toLowerCase().contains(protectedName.toLowerCase()));

        if (isProtected) {
            log.warn("Delete skipped because this poster photo is protected and cannot be deleted: {}", path);
            return;
        }
        try {
            Path fileToDelete = Paths.get(path);
            Files.deleteIfExists(fileToDelete);
            log.info("Successfully deleted poster photo: {}", path);
        } catch (IOException e) {
            log.error("Cannot delete poster photo: {}", path, e);
        }
    }
}
