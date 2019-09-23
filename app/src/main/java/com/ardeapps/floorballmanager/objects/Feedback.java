package com.ardeapps.floorballmanager.objects;

public class Feedback {

    private String feedbackId;
    private String userId;
    private String feedback;
    private long time;

    public Feedback() {
    }

    public Feedback clone() {
        Feedback clone = new Feedback();
        clone.feedbackId = this.feedbackId;
        clone.userId = this.userId;
        clone.feedback = this.feedback;
        clone.time = this.time;
        return clone;
    }

    public String getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(String feedbackId) {
        this.feedbackId = feedbackId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
