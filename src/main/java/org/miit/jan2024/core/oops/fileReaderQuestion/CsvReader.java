package org.miit.jan2024.core.oops.fileReaderQuestion;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class CsvReader {
    
    private static final int NUM_THREADS = 2;

    public static void main(String[] args) {
        
        String fileNameString = "data.csv"; 
        
        try (BufferedReader reader = new BufferedReader(new FileReader(fileNameString))) {
            String lineString;
            StringBuilder[] dataChunks = new StringBuilder[NUM_THREADS];
            for (int i = 0; i < NUM_THREADS; i++) {
                dataChunks[i] = new StringBuilder();
            }

            int currentThreadIndex = 0;
            while ((lineString = reader.readLine()) != null) {
                dataChunks[currentThreadIndex].append(lineString).append("\n");
                currentThreadIndex = (currentThreadIndex + 1) % NUM_THREADS;
            }

            Thread[] threads = new Thread[NUM_THREADS];
            for (int i = 0; i < NUM_THREADS; i++) {
                final int threadIndex = i;
                threads[i] = new Thread(() -> {
                    writeDataToFile(dataChunks[threadIndex].toString());
                });
                threads[i].start();
            }

            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeDataToFile(String data) {
        String[] rows = data.split("\n");
        for (String row : rows) {
            String[] values = row.split(",");
            String type = values[1].trim(); // Because type is 2nd column

            try {
                FileWriter fileWriter;
                if (type.equals("c")) {
                    fileWriter = new FileWriter("credit.csv", true); // Append mode
                } else if (type.equals("d")) {
                    fileWriter = new FileWriter("deposit.csv", true); // Append mode
                } else {
                    // If type is neither "c" nor "d", skip writing
                    continue;
                }

                // Write the row to the corresponding file
                for (String value : values) {
                    fileWriter.append(value).append(",");
                }
                fileWriter.append("\n");
                fileWriter.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
