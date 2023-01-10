package ir.alirezaalijani.news.application.initializers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;

@Slf4j
@Component("destroyInit")
public class ApplicationDestroyInitializerImpl implements ApplicationInitializer {

    @Override
    public void init() {
    }

    public void listFilesForFolder(final File folder, List<File> files) {
        if (folder != null && folder.listFiles() != null) {
            for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {
                if (fileEntry.isDirectory()) {
                    listFilesForFolder(fileEntry, files);
                } else {
                    System.out.println(fileEntry.getName());
                    files.add(fileEntry);
                }
            }
        }
    }
}
