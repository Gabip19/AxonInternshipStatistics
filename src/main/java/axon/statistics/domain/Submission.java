package axon.statistics.domain;

import java.time.LocalDateTime;

public class Submission {
    private Applicant applicant;
    private LocalDateTime deliveryTime;
    private float initialScore;
    private float currentScore;

    public Submission(Applicant applicant, LocalDateTime deliveryTime, float initialScore) {
        this.applicant = applicant;
        this.deliveryTime = deliveryTime;
        this.initialScore = initialScore;
        this.currentScore = initialScore;
    }

    public void addBonus(float bonus) {
        // ?? should the score stay in the [0 10] interval after applying the bonus ??
        currentScore += bonus;
    }

    public Applicant getApplicant() {
        return applicant;
    }

    public void setApplicant(Applicant applicant) {
        this.applicant = applicant;
    }

    public LocalDateTime getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(LocalDateTime deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public float getInitialScore() {
        return initialScore;
    }

    public void setInitialScore(float initialScore) {
        this.initialScore = initialScore;
    }

    public float getCurrentScore() {
        return currentScore;
    }

}
