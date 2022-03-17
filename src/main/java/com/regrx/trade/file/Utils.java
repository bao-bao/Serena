package com.regrx.trade.file;

import org.apache.commons.io.input.ReversedLinesFileReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static List<String> readLastLine(File file, int numLastLineToRead) {

        List<String> result = new ArrayList<>();

        try (ReversedLinesFileReader reader = new ReversedLinesFileReader(file, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null && result.size() < numLastLineToRead) {
                result.add(line);
            }
        } catch (FileNotFoundException e) {
            CsvWriter.newFile(file.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;

    }
}
