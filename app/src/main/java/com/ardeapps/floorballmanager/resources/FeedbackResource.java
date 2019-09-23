package com.ardeapps.floorballmanager.resources;

import com.ardeapps.floorballmanager.objects.Feedback;
import com.ardeapps.floorballmanager.services.FirebaseDatabaseService;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by Arttu on 19.1.2018.
 */

public class FeedbackResource extends FirebaseDatabaseService {
    private static FeedbackResource instance;
    private static DatabaseReference database;

    public static FeedbackResource getInstance() {
        if (instance == null) {
            instance = new FeedbackResource();
        }
        database = getDatabase().child(FEEDBACKS);
        return instance;
    }

    public void addFeedback(Feedback feedback, final AddDataSuccessListener handler) {
        feedback.setFeedbackId(database.push().getKey());
        addData(database.child(feedback.getFeedbackId()), feedback, handler);
    }

    public void editFeedback(Feedback feedback) {
        editData(database.child(feedback.getFeedbackId()), feedback);
    }

    public void removeFeedback(String feedbackId, final DeleteDataSuccessListener handler) {
        deleteData(database.child(feedbackId), handler);
    }
}
