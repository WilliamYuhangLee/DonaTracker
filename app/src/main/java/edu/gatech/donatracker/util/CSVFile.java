package edu.gatech.donatracker.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CSVFile {

    InputStream inputStream;

    public CSVFile(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public List read() {
        List result = new ArrayList();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String csvLine;
            while ((csvLine = bufferedReader.readLine()) != null) {
                String[] row = csvLine.split(",");
                result.add(row);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error in reading CSV file: " + e);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                throw new RuntimeException("Error in closing input stream: " + e);
            }
        }
        return result;
    }
}
