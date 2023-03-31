package axon.statistics.processor.dataloader;

import axon.statistics.domain.Applicant;
import axon.statistics.domain.Submission;
import axon.statistics.processor.dataloader.SubmissionLoader;
import axon.statistics.processor.validator.Validator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;

public class CsvSubmissionLoader implements SubmissionLoader {
    private final HashMap<String, Submission> submissions = new HashMap<>();
    private final Validator<String> lineDataValidator;

    public CsvSubmissionLoader(Validator<String> lineDataValidator) {
        this.lineDataValidator = lineDataValidator;
    }

    @Override
    public HashMap<String, Submission> loadSubmissions(InputStream csvStream) {
        try (BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(csvStream, StandardCharsets.UTF_8))
        ) {
            String currentLine;
            while ((currentLine = bufferedReader.readLine()) != null) {
                String[] lineData = currentLine.split(",");
                if (hasValidFormat(lineData)) {
                    Submission submission = extractSubmission(lineData);
                    submissions.put(submission.getApplicant().getEmail(), submission);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return submissions;
    }

    private Submission extractSubmission(String[] lineData) {
        Applicant applicant = new Applicant(lineData[0].split(" "), lineData[1]);
        LocalDateTime deliverTime = LocalDateTime.parse(lineData[2]);
        float score = Float.parseFloat(lineData[3]);
        return new Submission(applicant, deliverTime, score);
    }

    private boolean hasValidFormat(String[] lineData) {
        try {
            lineDataValidator.validate(lineData);
            return true;
        } catch (IllegalArgumentException e) {
//            System.err.println("Invalid line format found: " + e.getMessage());
        }
        return false;
    }
}
