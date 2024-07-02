package com.example.demo;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

@Service

public class SMSDatasetService {
	private List<String> messages;
    private List<String> labels;

    public SMSDatasetService() {
        messages = new ArrayList<>();
        labels = new ArrayList<>();
}
    public void loadDataset(String filePath) throws IOException {
        try (Reader reader = Files.newBufferedReader(Paths.get(filePath))) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .parse(reader);

            for (CSVRecord record : records) {
                labels.add(record.get("v1"));
                messages.add(record.get("v2"));
            }
        }
    }

    public List<String> getMessages() {
        return messages;
    }

    public List<String> getLabels() {
        return labels;
    }
}
