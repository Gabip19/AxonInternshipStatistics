package axon.statistics.processor;

import axon.statistics.domain.Applicant;
import axon.statistics.domain.Submission;
import axon.statistics.processor.validator.LineDataValidator;
import axon.statistics.processor.validator.Validator;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class ApplicantsProcessor {
    private final HashMap<String, Submission> submissions = new HashMap<>();
    private final Validator<String> lineDataValidator = new LineDataValidator();

    public String processApplicants(InputStream csvStream) {
        loadSubmissions(csvStream);

        int uniqueApplicants = getUniqueApplicantsNum();
        System.out.println(uniqueApplicants);

        applyScoreAdjustments();
        List<Applicant> topApplicants = getTopApplicants(3);

        return "";
    }

    private List<Applicant> getTopApplicants(int topNumber) {
        topNumber = Math.min(topNumber, submissions.size());

        Comparator<Submission> comparator = (first, second) -> {
            if (first.getCurrentScore() == second.getCurrentScore()) {
                if (first.getInitialScore() == second.getInitialScore()) {
                    if (first.getDeliveryTime().equals(second.getDeliveryTime())) {
                        return second.getApplicant().getEmail().compareTo(first.getApplicant().getEmail());
                    } else return second.getDeliveryTime().compareTo(first.getDeliveryTime());
                } else return Float.compare(first.getInitialScore(), second.getInitialScore());
            } return Float.compare(first.getCurrentScore(), second.getCurrentScore());
        };

        PriorityQueue<Submission> topSubmissions = getTopSubmissions(topNumber, comparator);

        ArrayList<Applicant> topApplicants = new ArrayList<>();
        while (!topSubmissions.isEmpty()) {
            topApplicants.add(topSubmissions.poll().getApplicant());
        }
        Collections.reverse(topApplicants);

        return topApplicants;
    }

    private PriorityQueue<Submission> getTopSubmissions(int topNumber, Comparator<Submission> comparator) {
        PriorityQueue<Submission> topSubmissions = new PriorityQueue<>(topNumber, comparator);

        // Add the first topNumber values to the queue
        Iterator<Submission> iterator = submissions.values().iterator();
        for (int i = 0; i < topNumber && iterator.hasNext(); i++) {
            topSubmissions.offer(iterator.next());
        }

        // Iterate over the rest of the values and add them to the queue if they are larger than the smallest element
        while (iterator.hasNext()) {
            Submission currentValue = iterator.next();
            if (comparator.compare(topSubmissions.peek(), currentValue) < 0) {
                topSubmissions.poll();
                topSubmissions.offer(currentValue);
            }
        }
        return topSubmissions;
    }

    private void applyScoreAdjustments() {
        LocalDate startDate = null;
        LocalDate endDate = null;

        for (Submission submission : submissions.values()) {
            if (startDate == null || endDate == null) {
                startDate = submission.getDeliveryTime().toLocalDate();
                endDate = startDate;
            } else {
                if (submission.getDeliveryTime().toLocalDate().isBefore(startDate)) {
                    startDate = submission.getDeliveryTime().toLocalDate();
                }
                if (submission.getDeliveryTime().toLocalDate().isAfter(endDate)) {
                    endDate = submission.getDeliveryTime().toLocalDate();
                }
            }
        }

        if (startDate != null && endDate != null) {
            if (!startDate.equals(endDate)) {
                applyScoreAdjustmentsBasedOnDate(startDate, endDate);
            }
        }
    }

    private void applyScoreAdjustmentsBasedOnDate(LocalDate startDate, LocalDate endDate) {
        final LocalDate finalStartDate = startDate;
        final LocalDate finalEndDate = endDate;

        submissions.values()
            .forEach(subm -> {
                LocalDate submissionDate = subm.getDeliveryTime().toLocalDate();
                LocalTime submissionTime = subm.getDeliveryTime().toLocalTime();
                if (submissionDate.equals(finalStartDate)) {
                    subm.addBonus(1);
                } else if (submissionDate.equals(finalEndDate) &&
                        (submissionTime.isAfter(LocalTime.NOON) ||
                        submissionTime.equals(LocalTime.NOON))
                ) {
                    subm.addBonus(-1);
                }
        });
    }

    private int getUniqueApplicantsNum() {
        return submissions.size();
    }

    private void loadSubmissions(InputStream csvStream) {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(csvStream))) {
            String currentLine;
            while ((currentLine = bufferedReader.readLine()) != null) {
                String[] lineData = currentLine.split(","); // TODO: 03/31/23 line data separator
//                System.out.println(lineData[0]);
                if (hasValidFormat(lineData)) {
                    Submission submission = extractSubmission(lineData);
                    submissions.put(submission.getApplicant().getEmail(), submission);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean hasValidFormat(String[] lineData) {
        try {
            lineDataValidator.validate(lineData);
            return true;
        } catch (IllegalArgumentException e) { // TODO: 03/31/23 change
            System.err.println("Invalid line format found: " + e.getMessage());
        }
        return false;
    }

    private Submission extractSubmission(String[] lineData) {
        Applicant applicant = new Applicant(lineData[0].split(" "), lineData[1]);
        LocalDateTime deliverTime = LocalDateTime.parse(lineData[2]);
        float score = Float.parseFloat(lineData[3]);
        return new Submission(applicant, deliverTime, score);
    }
}
