package axon.statistics.processor;

import axon.statistics.domain.Applicant;
import axon.statistics.domain.JSONDataHolder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

public class JSONFormatter {
    public static String formatToJson(int uniqueApplicants, List<Applicant> topApplicants, double averageScore) {
        List<String> topApplicantsLastNames = topApplicants.stream()
                .map(Applicant::getLastName).toList();

        // Using google gson for printing and creating the json format string

        JSONDataHolder jsonData = new JSONDataHolder(uniqueApplicants, topApplicantsLastNames, averageScore);
        Gson gsonPretty = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gsonPretty.toJson(jsonData));

        Gson gson = new Gson();
        return gson.toJson(jsonData);
    }
}
