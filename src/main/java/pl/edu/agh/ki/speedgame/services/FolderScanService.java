package pl.edu.agh.ki.speedgame.services;

import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class FolderScanService implements IFolderScanService {

    private final String CONFIG_FILE_NAME = "config.json";

    @PostConstruct
    public void init() {
        scanFolder();
    }

    @Override
    public List<String> scanFolder() {
        List<String> result = new ArrayList<>();
        try {
            if (ResourceUtils.getFile("classpath:tasks").isDirectory() && ResourceUtils.getFile("classpath:tasks").listFiles() != null)
                for (File file : ResourceUtils.getFile("classpath:tasks").listFiles()) {
                    for (File infile : file.listFiles()) {
                        if (infile.isFile() && infile.getName().equals(CONFIG_FILE_NAME)) {
                            try {
                                String content = new String(Files.readAllBytes(Paths.get(infile.getAbsolutePath())));
                                result.add(content);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return result;
    }

}