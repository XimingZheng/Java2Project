package cs209a.finalproject_demo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import cs209a.finalproject_demo.model.StackOverflowThread;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
@Service
public class DataLoaderService {
    private static final Logger logger = LoggerFactory.getLogger(DataLoaderService.class);
    private final ObjectMapper objectMapper;
    private List<StackOverflowThread> threads;

    @Value("${data.file.path:data/stackoverflow_threads.jsonl}")
    private String dataFilePath;

    public DataLoaderService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.threads = new ArrayList<>();
    }

    @PostConstruct
    public void loadData() {
        logger.info("Start loading data from " + dataFilePath);
        File file = new File(dataFilePath);
        if (!file.exists()) {
            logger.error("File does not exists: {}", dataFilePath);
            return;
        }

        int successCount = 0;
        int failCount = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                try {
                    StackOverflowThread thread = objectMapper.readValue(line, StackOverflowThread.class);
                    threads.add(thread);
                    successCount++;
                } catch (Exception e) {
                    failCount++;
                    logger.warn("parsing failed: {}", e.getMessage());
                }
            }

            logger.info("Data loading finished! success: {}, failed: {}, total: {}",
                    successCount, failCount, threads.size());

        } catch (IOException e) {
            logger.error("Failed to read data: {}", e.getMessage(), e);
        }
    }

    public List<StackOverflowThread> getAllThreads() {
        return new ArrayList<>(threads);
    }
}
