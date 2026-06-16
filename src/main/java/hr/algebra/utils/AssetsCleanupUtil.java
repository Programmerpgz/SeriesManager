package hr.algebra.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Stream;

public class AssetsCleanupUtil {
    private AssetsCleanupUtil() {
    }
    private static final Logger log = LoggerFactory.getLogger(AssetsCleanupUtil.class);
    public static void deleteAllPostersExceptDefault(Path assetsFolder, Set<String> allowedPosters) {
        if (!Files.exists(assetsFolder)) {
            return;
        }
        try (Stream<Path> paths = Files.walk(assetsFolder)) {
            paths.filter(Files::isRegularFile)
                    .filter(path -> allowedPosters.stream()
                            .noneMatch(allowedName -> allowedName.equalsIgnoreCase(path.getFileName().toString())))
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                            log.info("Deleted asset: {}", path.getFileName());
                        } catch (IOException e) {
                            log.warn("Failed to delete file: {}",path.getFileName(), e);
                        }
                    });
        } catch (IOException e) {
            log.error("Failed to cleanup assets folder", e);
        }
    }
}