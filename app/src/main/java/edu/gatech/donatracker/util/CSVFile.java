package edu.gatech.donatracker.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Yuhang Li on 2018/10/11
 *
 * A utility class that parses a CSV file into a list of its rows.
 */
public class CSVFile {

    private InputStream inputStream;

    /**
     * Constructor.
     *
     * Refer to the following lines for usage of this class:
     *
     * InputStream inputStream = getResources().openRawResource(R.raw.CSV_FILE_NAME);
     * CSVFile csvFile = new CSVFile(inputStream);
     * List<String[]> list = csvFile.read();
     *
     * @param inputStream denotes the file to be parsed.
     */
    public CSVFile(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    /**
     * Separate the CSV file into a List of rows, with each row being a String[]
     * comprised of several String values (split by commas from the original file)
     *
     * @return a List<String[]> that contains rows of elements from a CSV file.
     */
    public List<String[]> read() {
        List<String[]> result = new ArrayList<>();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String csvLine;
            if ((csvLine = bufferedReader.readLine()) != null) {
                String row[] = csvLine.replace("\uFEFF", "").trim().split(",");
                result.add(row);
            }
            while ((csvLine = bufferedReader.readLine()) != null) {
                String[] row = csvLine.trim().split(",");
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
