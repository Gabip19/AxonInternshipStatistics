package axon.statistics;

import axon.statistics.domain.BonusSubmissionComparator;
import axon.statistics.processor.ApplicantsProcessor;
import axon.statistics.processor.dataloader.CsvSubmissionLoader;
import axon.statistics.processor.validator.LineDataValidator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) {
        // !!! PRINTING NAMES TO CONSOLE REPLACES UNICODE CHARACTERS WITH ? BUT STRINGS ARE STILL INTACT !!!
        // DON'T KNOW HOW TO FIX IT YET :(

        ApplicantsProcessor processor = new ApplicantsProcessor(
                new CsvSubmissionLoader(new LineDataValidator()),
                new BonusSubmissionComparator()
        );

        String filePath = Main.class.getResource("/submissions.csv").getFile();
        filePath = filePath.replace("%20", " ");

        try {
            String jsonResult = processor.processApplicants(new FileInputStream(filePath));
            System.out.println(jsonResult);
        } catch (FileNotFoundException e) {
            System.out.println("File not found. Try placing it in the resources folder.");
        }
    }
}