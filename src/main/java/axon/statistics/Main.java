package axon.statistics;

import axon.statistics.processor.ApplicantsProcessor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) {
        ApplicantsProcessor processor = new ApplicantsProcessor();
        String filePath = Main.class.getResource("/submissions.csv").getFile();
        filePath = filePath.replace("%20", " ");
        try {
            processor.processApplicants(new FileInputStream(filePath));
        } catch (FileNotFoundException e) {
            System.out.println("File not found. Try placing it in the resources folder.");
        }
    }
}