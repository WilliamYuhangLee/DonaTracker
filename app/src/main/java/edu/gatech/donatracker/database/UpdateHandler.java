package edu.gatech.donatracker.database;

import com.google.firebase.firestore.DocumentChange;

public interface UpdateHandler {
    void onAdded(DocumentChange change);

    void onModified(DocumentChange change);

    void onRemoved(DocumentChange change);
}
