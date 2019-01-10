import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Iterator;
import java.util.TreeMap;

import static java.nio.file.FileVisitResult.*;

public class SearchFiles
        extends SimpleFileVisitor<Path> {

    public static TreeMap<String, FileInfo> currentFiles = new TreeMap<String, FileInfo>();
    public static TreeMap<String, FileInfo> currentDirectory = new TreeMap<String, FileInfo>();
    public static TreeMap<String, Long> directorySizes = new TreeMap<String, Long>();
    public static TreeMap<String, Integer> fileExtensionStatistics = new TreeMap<String, Integer>();

    @Override
    public FileVisitResult visitFile(Path file,
                                     BasicFileAttributes attr) {
        FileInfo fileToInsert = new FileInfo(file.getFileName().toString(), attr.size(), attr.lastModifiedTime());
        String path = file.toAbsolutePath().toString();
        currentFiles.put(path, fileToInsert);
        currentDirectory.put(path, fileToInsert);
        String extension = getExtension(file.getFileName().toString());
        if (fileExtensionStatistics.containsKey(extension)) {
            int count = fileExtensionStatistics.get(extension);
            fileExtensionStatistics.replace(extension, ++count);
        }
        else fileExtensionStatistics.put(extension, 1);
        return CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir,
                                              IOException exc) {
        long directorySize = 0;
        Iterator<String> its= currentDirectory.keySet().iterator();
        while (its.hasNext()) {
            directorySize += currentDirectory.get(its.next()).getSize();
        }
        directorySizes.put(dir.toString(), directorySize);
        currentDirectory.clear();
        return CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file,
                                           IOException exc) throws IOException {
        throw exc;
    }

    private static String getExtension(String name) {
        String extension = "";
        int i = name.lastIndexOf('.');
        if (i > 0) {
            extension = name.substring(i+1);
        }
        return extension;
    }
}