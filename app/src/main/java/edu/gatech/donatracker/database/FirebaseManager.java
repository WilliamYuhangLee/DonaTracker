package edu.gatech.donatracker.database;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by Yuhang Li on 2018/10/29
 *
 * Handles all operations on Firebase
 */
public class FirebaseManager {

    private static final String TAG = FirebaseManager.class.getSimpleName();

    public static <T> void getObject(Activity activity, Class<T> type, QueryResultHandler<T> handler,
                                     DocumentReference docRef) {
        docRef.get()
                .addOnSuccessListener(activity, documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        T object = documentSnapshot.toObject(type);
                        Log.d(activity.getClass().getSimpleName(), type.getSimpleName() + " fetched!");
                        handler.onQuerySuccess(object);
                    } else {
                        Log.e(activity.getClass().getSimpleName(), type.getSimpleName() + " fetch failed: the " +
                                "document does not exist!");
                    }
                })
                .addOnFailureListener(activity, e -> {
                    Log.e(activity.getClass().getSimpleName(), type.getSimpleName() + " fetch failed!", e);
                });
    }

    public static <T> void getObject(Activity activity, Class<T> type, QueryResultHandler<T> handler, String... path) {
        getObject(activity, type, handler, getDocRef(path));
    }

    public static <T> void getObject(Activity activity, Function<Map<String, Object>, T> unwrapper,
                                     QueryResultHandler<T> handler, DocumentReference docRef) {
        docRef.get()
                .addOnSuccessListener(activity, documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        T object = unwrapper.apply(documentSnapshot.getData());
                        Log.d(activity.getClass().getSimpleName(), object.getClass().getSimpleName() + " fetched!");
                        handler.onQuerySuccess(object);
                    } else {
                        Log.e(activity.getClass().getSimpleName(), "Object fetch failed: the document does not exist!");
                    }
                })
                .addOnFailureListener(activity, e -> {
                    Log.e(activity.getClass().getSimpleName(), "Object fetch failed!", e);
                });
    }

    public static <T> void getObject(Activity activity, Function<Map<String, Object>, T> unwrapper,
                                     QueryResultHandler<T> handler, String... path) {
        getObject(activity, unwrapper, handler, getDocRef(path));
    }

    public static <T> void getObjects(Activity activity, Class<T> type, List<T> listToFillObjects,
                                      CollectionReference colRef) {
        if (!listToFillObjects.isEmpty()) {
            Log.w(activity.getClass().getSimpleName(), "Warning: Adding objects into a non-empty list!");
        }
        colRef.get()
                .addOnSuccessListener(activity, queryDocumentSnapshots -> {
                    Log.d(activity.getClass().getSimpleName(), "Collection fetched!");
                    for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                        listToFillObjects.add(queryDocumentSnapshot.toObject(type));
                    }
                })
                .addOnFailureListener(activity, e -> {
                    Log.e(activity.getClass().getSimpleName(), "Collection fetch failed!", e);
                });
    }

    public static <T> void getObjects(Activity activity, Class<T> type, List<T> listToFillObjects, String... path) {
        getObjects(activity, type, listToFillObjects, getColRef(path));
    }

    public static <T> void getObjects(Activity activity, Function<Map<String, Object>, T> unwrapper, List<T>
            listToFillObjects, CollectionReference colRef) {
        if (!listToFillObjects.isEmpty()) {
            Log.w(activity.getClass().getSimpleName(), "Warning: Adding objects into a non-empty list!");
        }
        colRef.get()
                .addOnSuccessListener(activity, queryDocumentSnapshots -> {
                    Log.d(activity.getClass().getSimpleName(), "Collection fetched!");
                    for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                        listToFillObjects.add(unwrapper.apply(queryDocumentSnapshot.getData()));
                    }
                })
                .addOnFailureListener(activity, e -> {
                    Log.e(activity.getClass().getSimpleName(), "Collection fetch failed!", e);
                });
    }

    public static <T> void getObjects(Activity activity, Function<Map<String, Object>, T> unwrapper, List<T>
            listToFillObjects, String... path) {
        getObjects(activity, unwrapper, listToFillObjects, getColRef(path));
    }

    public static <T> void updateObject(Activity activity, Class<T> type, QueryResultHandler<T> handler,
                                        DocumentReference docRef) {
        docRef.addSnapshotListener(activity, (documentSnapshot, e) -> {
            if (e != null) {
                Log.e(activity.getClass().getSimpleName(), type.getSimpleName() + " update failed!", e);
                return;
            }
            if (documentSnapshot != null && documentSnapshot.exists()) {
                Log.d(activity.getClass().getSimpleName(), type.getSimpleName() + " update fetched!");
                handler.onQuerySuccess(documentSnapshot.toObject(type));
            } else {
                Log.d(activity.getClass().getSimpleName(), "Current snapshot for " + type.getSimpleName() + " is " +
                        "empty.");
            }
        });
    }

    public static <T> void updateObject(Activity activity, Class<T> type, QueryResultHandler<T> handler,
                                        String... path) {
        updateObject(activity, type, handler, getDocRef(path));
    }

    public static <T> void updateObject(Activity activity, Function<Map<String, Object>, T> unwrapper,
                                        QueryResultHandler<T> handler,
                                        DocumentReference docRef) {
        docRef.addSnapshotListener(activity, (documentSnapshot, e) -> {
            if (e != null) {
                Log.e(activity.getClass().getSimpleName(), "Update failed!", e);
                return;
            }
            if (documentSnapshot != null && documentSnapshot.exists()) {
                T object = unwrapper.apply(documentSnapshot.getData());
                Log.d(activity.getClass().getSimpleName(),  object.getClass().getSimpleName() + " update fetched!");
                handler.onQuerySuccess(object);
            } else {
                Log.d(activity.getClass().getSimpleName(), "Current snapshot is empty.");
            }
        });
    }

    public static <T> void updateObject(Activity activity, Function<Map<String, Object>, T> unwrapper,
                                        QueryResultHandler<T> handler,
                                        String... path) {
        updateObject(activity, unwrapper, handler, getDocRef(path));
    }

    public static <T> void updateObjects(Activity activity, Class<T> type, List<T> listToUpdate, UpdateHandler
            handler, CollectionReference colRef) {
        colRef.addSnapshotListener(activity, (queryDocumentSnapshots, e) -> {
            if (e != null) {
                Log.e(activity.getClass().getSimpleName(), type.getSimpleName() + " update failed", e);
                return;
            }
            for (DocumentChange change : queryDocumentSnapshots.getDocumentChanges()) {
                T updatedObject = change.getDocument().toObject(type);
                switch (change.getType()) {
                    case ADDED:
                        listToUpdate.add(updatedObject);
                        Log.d(activity.getClass().getSimpleName(), type.getSimpleName() + " added!");
                        handler.onAdded(change);
                        break;
                    case MODIFIED:
                        if (change.getOldIndex() == change.getNewIndex()) {
                            // object updated but remained in the same position
                            listToUpdate.set(change.getOldIndex(), updatedObject);
                        } else {
                            // object updated and changed position
                            listToUpdate.remove(change.getOldIndex());
                            listToUpdate.add(change.getNewIndex(), updatedObject);
                        }
                        Log.d(activity.getClass().getSimpleName(), type.getSimpleName() + " modified!");
                        handler.onModified(change);
                        break;
                    case REMOVED:
                        listToUpdate.remove(change.getOldIndex());
                        Log.d(activity.getClass().getSimpleName(), type.getSimpleName() + " removed!");
                        handler.onRemoved(change);
                        break;
                }
            }
        });
    }

    public static <T> void updateObjects(Activity activity, Class<T> type, List<T> listToUpdate, UpdateHandler
            handler, String... path) {
        updateObjects(activity, type, listToUpdate, handler, getColRef(path));
    }

    public static DocumentReference getDocRef(@NonNull String... path) {
        if (path.length % 2 == 1) {
            Exception e = new IllegalArgumentException("Number of args must be odd");
            Log.e(TAG, "Illegal document path", e);
        }
        CollectionReference collection = FirebaseFirestore.getInstance().collection(path[0]);
        DocumentReference document = collection.document(path[1]);
        for (int i = 2; i < path.length; i++) {
            if (i % 2 == 0) { // collections
                collection = document.collection(path[i]);
            } else { // documents
                document = collection.document(path[i]);
            }
        }
        return document;
    }

    public static CollectionReference getColRef(@NonNull String... path) {
        if (path.length % 2 == 0) {
            Exception e = new IllegalArgumentException("Number of args must be even");
            Log.e(TAG, "Illegal collection path", e);
        }
        CollectionReference collection = FirebaseFirestore.getInstance().collection(path[0]);
        if (path.length > 1) {
            DocumentReference document = null;
            for (int i = 1; i < path.length; i++) {
                if (i % 2 == 1) { // documents
                    document = collection.document(path[i]);
                } else {
                    collection = document.collection(path[i]);
                }
            }
        }
        return collection;
    }
}
