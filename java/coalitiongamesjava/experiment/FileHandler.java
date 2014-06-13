package experiment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public abstract class FileHandler {
    
    public static final String TEXT_EXTENSION = ".txt";
    
    private static File getFileAndCreateIfNeeded(
        final String fileName
    ) {
        final File outputFile = new File(fileName);
        try {
            outputFile.createNewFile();
        } catch (final IOException e) {
            e.printStackTrace();
            return null;
        }
        
        return outputFile;
    }
    
    public static void writeToFile(
        final String fileName,
        final String text
    ) {
        Writer output = null;
        try {
            output = new BufferedWriter(
                new FileWriter(getFileAndCreateIfNeeded(fileName)));
            output.write(text + "\n");
            output.close();
        } catch (IOException e) {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            e.printStackTrace();

            return;
        }
    }
    
    private static <T> String getSeparatedList(
        final List<T> input,
        final char separator
    ) {
        final StringBuilder builder = new StringBuilder();

        for (int i = 0; i < input.size(); i++) {
            builder.append(input.get(i));
            if (i < input.size() - 1) {
                builder.append(separator);
            }
        }

        return builder.toString();
    }
    
    public static void printInputFile(
        final String fileName,
        final List<Integer> rsdOrder,
        final List<Double> budgets,
        final List<List<Double>> valueMatrix
    ) {
        Writer output = null;
        try {
            output = new BufferedWriter(
                new FileWriter(getFileAndCreateIfNeeded(fileName)));
            output.write(getSeparatedList(rsdOrder, ' ') + '\n');
            output.write(getSeparatedList(budgets, ' ') + '\n');
            for (List<Double> row: valueMatrix) {
                output.write(getSeparatedList(row, ' ') + '\n');
            }
            output.write('\n');
            output.flush();
            output.close();
        } catch (IOException e) {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            e.printStackTrace();

            return;
        }
    }
    
    public static List<Integer> getSpaceSeparatedIntegerRow(
        final String line
    ) {
        final String[] tokens = line.trim().split("\\s+");
        final List<Integer> row = new ArrayList<Integer>();
        for (int i = 0; i < tokens.length; i++) {
            row.add(Integer.parseInt(tokens[i]));
        }
        return row;
    }
    
    public static List<Double> getSpaceSeparatedDoubleRow(
        final String line
    ) {
        final String[] tokens = line.trim().split("\\s+");
        final List<Double> row = new ArrayList<Double>();
        for (int i = 0; i < tokens.length; i++) {
            row.add(Double.parseDouble(tokens[i]));
        }
        return row;
    }
    
    public static List<List<Integer>> getSpaceSeparatedIntegerRows(
        final List<String> lines
    ) {
        final List<List<Integer>> result = new ArrayList<List<Integer>>();
        for (final String line: lines) {
            if (line.isEmpty()) {
                continue;
            }
            final String[] tokens = line.trim().split("\\s+");
            final List<Integer> row = new ArrayList<Integer>();
            for (int i = 0; i < tokens.length; i++) {
                row.add(Integer.parseInt(tokens[i]));
            }
            result.add(row);
        }
        
        return result;
    }
    
    public static List<List<Double>> getSpaceSeparatedDoubleRows(
        final List<String> lines
    ) {
        final List<List<Double>> result = new ArrayList<List<Double>>();
        for (final String line: lines) {
            if (line.isEmpty()) {
                continue;
            }
            final String[] tokens = line.trim().split("\\s+");
            List<Double> row = new ArrayList<Double>();
            for (int i = 0; i < tokens.length; i++) {
                row.add(Double.parseDouble(tokens[i]));
            }
            result.add(row);
        }
        
        return result;
    }

    public static List<String> getLines(final String fileName) {
        final File file = new File(fileName);
        
        List<String> result = new ArrayList<String>();
        
        try {
            BufferedReader input =  new BufferedReader(new FileReader(file));
          
            try {
                String line = null;
                /*
                 * readLine is a bit quirky :
                 * it returns the content of a line MINUS the newline.
                 * it returns null only for the END of the stream.
                 * it returns an empty String if two newlines appear in a row.
                 */
                while ((line = input.readLine()) != null) {
                    result.add(line);
                }
            } finally {
                input.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        return result;
    }
}
