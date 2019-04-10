package pl.edu.agh.ki.speedgame.services;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IFolderScanService {
    List<String> scanFolder();
    List<String> getTaskNames();
}
