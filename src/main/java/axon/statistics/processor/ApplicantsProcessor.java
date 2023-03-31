package axon.statistics.processor;

import axon.statistics.domain.Applicant;
import axon.statistics.domain.BonusSubmissionComparator;
import axon.statistics.domain.JSONDataHolder;
import axon.statistics.domain.Submission;
import axon.statistics.processor.dataloader.CsvSubmissionLoader;
import axon.statistics.processor.dataloader.SubmissionLoader;
import axon.statistics.processor.validator.LineDataValidator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class ApplicantsProcessor {
    private HashMap<String, Submission> submissions = new HashMap<>();
    private final SubmissionLoader submissionLoader;
    private final Comparator<Submission> topSubmissionComparator;

    public ApplicantsProcessor() {
        submissionLoader = new CsvSubmissionLoader(new LineDataValidator());
        topSubmissionComparator = new BonusSubmissionComparator();
    }

    public ApplicantsProcessor(SubmissionLoader submissionLoader, Comparator<Submission> topSubmissionComparator) {
        this.submissionLoader = submissionLoader;
        this.topSubmissionComparator = topSubmissionComparator;
    }

    public String processApplicants(InputStream csvStream) {
        submissions = submissionLoader.loadSubmissions(csvStream);

        int uniqueApplicants = getUniqueApplicantsNum();

        applyScoreAdjustments();
        List<Applicant> topApplicants = getTopApplicants(3);

        int topNumber = submissions.size() / 2;
        if (submissions.size() % 2 == 1) {
            topNumber += 1;
        }

        double averageScore = getTopAverageScore(topNumber);

        return formatForJson(uniqueApplicants, topApplicants, averageScore);
    }

    private String formatForJson(int uniqueApplicants, List<Applicant> topApplicants, double averageScore) {
        List<String> topApplicantsLastNames = topApplicants.stream()
                .map(Applicant::getLastName).toList();

        JSONDataHolder jsonData = new JSONDataHolder(uniqueApplicants, topApplicantsLastNames, averageScore);
        Gson gsonPretty = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gsonPretty.toJson(jsonData));

        Gson gson = new Gson();
        return gson.toJson(jsonData);
    }

    private double getTopAverageScore(int topNumber) {
        PriorityQueue<Submission> topSubmissions = getTopSubmissions(topNumber, (o1, o2) ->
                Float.compare(o1.getInitialScore(), o2.getInitialScore()));

        double averageScore = topSubmissions.stream()
                .mapToDouble(Submission::getInitialScore)
                .average()
                .orElse(0);

        BigDecimal bd = new BigDecimal(averageScore).setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private List<Applicant> getTopApplicants(int topNumber) {
        topNumber = Math.min(topNumber, submissions.size());

        PriorityQueue<Submission> topSubmissions = getTopSubmissions(topNumber, topSubmissionComparator);

        ArrayList<Applicant> topApplicants = new ArrayList<>();
        while (!topSubmissions.isEmpty()) {
            topApplicants.add(topSubmissions.poll().getApplicant());
        }
        Collections.reverse(topApplicants);

        return topApplicants;
    }

    private PriorityQueue<Submission> getTopSubmissions(int topNumber, Comparator<Submission> comparator) {
        if (topNumber < 1) {
            return new PriorityQueue<>();
        }
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
}
