package edu.gatech.donatracker.database;

import android.widget.Adapter;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public interface UpdateHandler {
    void onAdded(DocumentChange change);
    void onModified(DocumentChange change);
    void onRemoved(DocumentChange change);
}
