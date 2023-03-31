package axon.statistics.domain;

import java.util.List;

public class JSONDataHolder {
    private int uniqueApplicants;
    private List<String> topApplicants;
    private double averageScore;

    public JSONDataHolder(int uniqueApplicants, List<String> topApplicants, double averageScore) {
        this.uniqueApplicants = uniqueApplicants;
        this.topApplicants = topApplicants;
        this.averageScore = averageScore;
    }
}
