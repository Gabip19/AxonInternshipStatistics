package axon.statistics.domain;

public class BonusSubmissionComparator implements SubmissionComparator {
    @Override
    public int compare(Submission first, Submission second) {
        if (first.getCurrentScore() == second.getCurrentScore()) {
            if (first.getInitialScore() == second.getInitialScore()) {
                if (first.getDeliveryTime().equals(second.getDeliveryTime())) {
                    return second.getApplicant().getEmail().compareTo(first.getApplicant().getEmail());
                } else return second.getDeliveryTime().compareTo(first.getDeliveryTime());
            } else return Float.compare(first.getInitialScore(), second.getInitialScore());
        } return Float.compare(first.getCurrentScore(), second.getCurrentScore());
    }
}
