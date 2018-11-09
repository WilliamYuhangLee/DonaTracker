package edu.gatech.donatracker.database;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
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

    // REFERENCE GENERATION

    /**
     * Get a Reference for a Document (a single object)
     *
     * Firebase FireStore database is organized according to the following structure:
     * -> Collection A
     *    -> Document a
     *    -> Document b
     *    -> Document c
     *       -> Collection cA
     *          -> Document cAa
     *             -> ...
     *    -> ...
     * -> Collection B
     * -> ...
     *
     * So basically a Collection contains multiple Documents and a Document may
     * contain multiple Collections. A Document also represents an object which
     * can have its fields.
     *
     * To get a Document Reference, put in the parameters each level of its path
     * starting from the highest hierarchy, for example:
     *
     * <code>DocumentReference docRef = getDocRef("locations", "1");</code>
     *
     * where "locations" is the top level Collection, and "1" is the id of the Document.
     *
     * The parameter list can extend infinitely as long as the number of arguments is EVEN.
     *
     * @param path a number of Strings that contain IDs of Collections/Documents in each level of the path
     * @return a Document Reference
     */
    public static DocumentReference getDocRef(@NonNull String... path) {
        if (path.length % 2 == 1) {
            Exception e = new IllegalArgumentException("Number of args must be even!");
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

    /**
     * Get a Reference for a Collection (a folder that contains Documents)
     *
     * Firebase FireStore database is organized according to the following structure:
     * -> Collection A
     *    -> Document a
     *    -> Document b
     *    -> Document c
     *       -> Collection cA
     *          -> Document cAa
     *             -> ...
     *    -> ...
     * -> Collection B
     * -> ...
     *
     * So basically a Collection contains multiple Documents and a Document may
     * contain multiple Collections. A Document also represents an object which
     * can have its fields.
     *
     * To get a Collection Reference, put in the parameters each level of its path
     * starting from the highest hierarchy, for example:
     *
     * <code>CollectionReference colRef = getColRef("locations", "1", "inventory");</code>
     *
     * The parameter list can extend infinitely as long as the number of arguments is ODD.
     *
     * @param path a number of Strings that contain IDs of Collections/Documents in each level of the path
     * @return a Collection Reference
     */
    public static CollectionReference getColRef(@NonNull String... path) {
        if (path.length % 2 == 0) {
            Exception e = new IllegalArgumentException("Number of args must be odd!");
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

    // DOWNLOADING OPERATIONS

    /**
     * Get an object of type T from FireStore using the given
     * DocumentReference docRef.
     *
     * Upon completion the result is handled by the given QueryResultHandler.
     *
     * The task listener is attached to the given Activity, and will be detached
     * when either the task or the Activity is finished.
     *
     * @param activity where the task listener is attached to
     * @param type the Class model of the type into which object is to be converted
     * @param handler how the result is going to be handled upon completion
     * @param docRef what document needs to be retrieved
     * @param <T> the type of object to be converted into
     */
    public static <T> void getObject(Activity activity, Class<T> type, QueryResultHandler<T> handler,
                                     DocumentReference docRef) {
        docRef.get()
                .addOnSuccessListener(activity, documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        T object = documentSnapshot.toObject(type);
                        Log.d(activity.getClass().getSimpleName(),
                                String.format("%s[%s] fetch successful.", docRef.getParent().getId(), docRef.getId()));
                        handler.onQuerySuccess(object);
                    } else {
                        Log.e(activity.getClass().getSimpleName(), String.format("%s[%s] fetch failed: document doesn't exist!",
                                docRef.getParent().getId(), docRef.getId()));
                    }
                })
                .addOnFailureListener(activity, e -> {
                    Log.e(activity.getClass().getSimpleName(),
                            String.format("%s[%s] fetch failed!", docRef.getParent().getId(), docRef.getId()), e);
                });
    }

    /**
     * Get an object of type T from FireStore using the given Document path.
     *
     * Upon completion the result is handled by the given QueryResultHandler.
     *
     * The task listener is attached to the given Activity, and will be detached
     * when either the task or the Activity is finished.
     *
     * @param activity where the task listener is attached to
     * @param type the Class model of the type into which object is to be converted
     * @param handler how the result is going to be handled upon completion
     * @param path the path of the Document in FireStore
     * @param <T> the type of the object
     */
    public static <T> void getObject(Activity activity, Class<T> type, QueryResultHandler<T> handler, String... path) {
        getObject(activity, type, handler, getDocRef(path));
    }

    /**
     * Get an object of type T from FireStore using the given DocumentReference.
     *
     * The object is converted into T using the given Function unwrapper.
     *
     * Upon completion the result is handled by the given QueryResultHandler.
     *
     * The task listener is attached to the given Activity, and will be detached
     * when either the task or the Activity is finished.
     *
     * @param activity where the task listener is attached to
     * @param unwrapper a converter Function that converts a Map into a T object
     * @param handler how the result is going to be handled upon completion
     * @param docRef what document needs to be retrieved
     * @param <T> the type of the object
     */
    public static <T> void getObject(Activity activity, Function<Map<String, Object>, T> unwrapper,
                                     QueryResultHandler<T> handler, DocumentReference docRef) {
        docRef.get()
                .addOnSuccessListener(activity, documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        T object = unwrapper.apply(documentSnapshot.getData());
                        Log.d(activity.getClass().getSimpleName(),
                                String.format("%s[%s] fetch successful.", docRef.getParent().getId(), docRef.getId()));
                        handler.onQuerySuccess(object);
                    } else {
                        Log.e(activity.getClass().getSimpleName(), String.format("%s[%s] fetch failed: document doesn't exist!",
                                docRef.getParent().getId(), docRef.getId()));
                    }
                })
                .addOnFailureListener(activity, e -> {
                    Log.e(activity.getClass().getSimpleName(),
                            String.format("%s[%s] fetch failed!", docRef.getParent().getId(), docRef.getId()), e);
                });
    }

    /**
     * Get an object of type T from FireStore using the given Document path.
     *
     * The object is converted into T using the given Function unwrapper.
     *
     * Upon completion the result is handled by the given QueryResultHandler.
     *
     * The task listener is attached to the given Activity, and will be detached
     * when either the task or the Activity is finished.
     *
     * @param activity where the task listener is attached to
     * @param unwrapper a converter Function that converts a Map into a T object
     * @param handler how the result is going to be handled upon completion
     * @param path the path of the Document in FireStore
     * @param <T> the type of the object
     */
    public static <T> void getObject(Activity activity, Function<Map<String, Object>, T> unwrapper,
                                     QueryResultHandler<T> handler, String... path) {
        getObject(activity, unwrapper, handler, getDocRef(path));
    }

    /**
     * Get objects of type T from a FireStore Collection (its Reference colRef).
     *
     * The objects are converted into type T using the FireStore native conversion method
     * that requires a no-arg constructor in the T class.
     *
     * The objects retrieved are added into the given list;
     * an empty list is recommended, otherwise a warning will be issued.
     *
     * The task listener is attached to the given Activity, and will be detached
     * when either the task or the Activity is finished.
     *
     * @param activity where the task listener is attached to
     * @param type the Class model of the type into which the objects are to be converted
     * @param listToFillObjects a list to fill in the retrieved objects
     * @param colRef the Reference of the Collection from which objects are to be retrieved
     * @param <T> the type of the objects
     */
    public static <T> void getObjects(Activity activity, Class<T> type, List<T> listToFillObjects,
                                      CollectionReference colRef) {
        if (!listToFillObjects.isEmpty()) {
            Log.w(activity.getClass().getSimpleName(), "Warning: Adding objects into a non-empty list!");
        }
        colRef.get()
                .addOnSuccessListener(activity, queryDocumentSnapshots -> {
                    Log.d(activity.getClass().getSimpleName(), "Collection [" + colRef.getId() + "] fetch successful.");
                    for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                        listToFillObjects.add(queryDocumentSnapshot.toObject(type));
                    }
                })
                .addOnFailureListener(activity, e -> {
                    Log.e(activity.getClass().getSimpleName(), "Collection [" + colRef.getId() + "] fetch failed!", e);
                });
    }

    /**
     * Get objects of type T from a FireStore Collection path.
     *
     * The objects are converted into type T using the FireStore native conversion method
     * that requires a no-arg constructor in the T class.
     *
     * The objects retrieved are added into the given list;
     * an empty list is recommended, otherwise a warning will be issued.
     *
     * The task listener is attached to the given Activity, and will be detached
     * when either the task or the Activity is finished.
     *
     * @param activity where the task listener is attached to
     * @param type the Class model of the type into which the objects are to be converted
     * @param listToFillObjects a list to fill in the retrieved objects
     * @param path the path of the Collection in FireStore
     * @param <T> the type of the objects
     */
    public static <T> void getObjects(Activity activity, Class<T> type, List<T> listToFillObjects, String... path) {
        getObjects(activity, type, listToFillObjects, getColRef(path));
    }

    /**
     * Get objects of type T from a FireStore Collection (its Reference colRef).
     *
     * The objects are converted into type T using the given Function unwrapper
     *
     * The objects retrieved are added into the given list;
     * an empty list is recommended, otherwise a warning will be issued.
     *
     * The task listener is attached to the given Activity, and will be detached
     * when either the task or the Activity is finished.
     *
     * @param activity where the task listener is attached to
     * @param unwrapper a converter Function that converts a Map into a T object
     * @param listToFillObjects a list to fill in the retrieved objects
     * @param colRef the Reference of the Collection from which objects are to be retrieved
     * @param <T> the type of the objects
     */
    public static <T> void getObjects(Activity activity, Function<Map<String, Object>, T> unwrapper, List<T>
            listToFillObjects, CollectionReference colRef) {
        if (!listToFillObjects.isEmpty()) {
            Log.w(activity.getClass().getSimpleName(), "Warning: Adding objects into a non-empty list!");
        }
        colRef.get()
                .addOnSuccessListener(activity, queryDocumentSnapshots -> {
                    Log.d(activity.getClass().getSimpleName(), "Collection [" + colRef.getId() + "] fetch successful.");
                    for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                        listToFillObjects.add(unwrapper.apply(queryDocumentSnapshot.getData()));
                    }
                })
                .addOnFailureListener(activity, e -> {
                    Log.e(activity.getClass().getSimpleName(), "Collection [" + colRef.getId() + "] fetch failed!", e);
                });
    }

    /**
     * Get objects of type T from a FireStore Collection path.
     *
     * The objects are converted into type T using the FireStore native conversion method
     * that requires a no-arg constructor in the T class.
     *
     * The objects retrieved are added into the given list;
     * an empty list is recommended, otherwise a warning will be issued.
     *
     * The task listener is attached to the given Activity, and will be detached
     * when either the task or the Activity is finished.
     *
     * @param activity where the task listener is attached to
     * @param unwrapper a converter Function that converts a Map into a T object
     * @param listToFillObjects a list to fill in the retrieved objects
     * @param path the path of the Collection in FireStore
     * @param <T> the type of the objects
     */
    public static <T> void getObjects(Activity activity, Function<Map<String, Object>, T> unwrapper, List<T>
            listToFillObjects, String... path) {
        getObjects(activity, unwrapper, listToFillObjects, getColRef(path));
    }

    /**
     * Listen to a Document on FireStore for changes.
     *
     * When a change occurs, a snapshot is received and converted into type T by
     * the Firebase native converter which requires a no-arg constructor in class T.
     *
     * Then the resulting object is sent as a parameter into the handler's method,
     * which decides what needs to be done with it.
     *
     * The listener is attached to the given Activity, and will only be detached
     * when the Activity stops. So it is recommended to only call this method in
     * an Activity's onStart method to prevent memory leak.
     *
     * @param activity where the listener is attached to
     * @param type the Class model of the type into which the object is to be converted
     * @param handler what needs to be done with the resulting object
     * @param docRef the Reference of the Document whose changes are listened for
     * @param <T> the type of the object
     */
    public static <T> void updateObject(Activity activity, Class<T> type, QueryResultHandler<T> handler,
                                        DocumentReference docRef) {
        docRef.addSnapshotListener(activity, (documentSnapshot, e) -> {
            if (e != null) {
                Log.e(activity.getClass().getSimpleName(),
                        String.format("%s[%s] update failed!", docRef.getParent().getId(), docRef.getId()), e);
                return;
            }
            if (documentSnapshot != null && documentSnapshot.exists()) {
                Log.d(activity.getClass().getSimpleName(),
                        String.format("%s[%s] update successful.", docRef.getParent().getId(), docRef.getId()));
                handler.onQuerySuccess(documentSnapshot.toObject(type));
            } else {
                Log.d(activity.getClass().getSimpleName(),
                        String.format("%s[%s] updated: empty snapshot!", docRef.getParent().getId(), docRef.getId()));
            }
        });
    }

    /**
     * Listen to a Document on FireStore for changes.
     *
     * When a change occurs, a snapshot is received and converted into type T by
     * the Firebase native converter which requires a no-arg constructor in class T.
     *
     * Then the resulting object is sent as a parameter into the handler's method,
     * which decides what needs to be done with it.
     *
     * The listener is attached to the given Activity, and will only be detached
     * when the Activity stops. So it is recommended to only call this method in
     * an Activity's onStart method to prevent memory leak.
     *
     * @param activity where the listener is attached to
     * @param type the Class model of the type into which the object is to be converted
     * @param handler what needs to be done with the resulting object
     * @param path the path of the Document whose changes are listened for
     * @param <T> the type of the object
     */
    public static <T> void updateObject(Activity activity, Class<T> type, QueryResultHandler<T> handler,
                                        String... path) {
        updateObject(activity, type, handler, getDocRef(path));
    }

    /**
     * Listen to a Document on FireStore for changes.
     *
     * When a change occurs, a snapshot is received and converted into type T by
     * the given Function unwrapper.
     *
     * Then the resulting object is sent as a parameter into the handler's method,
     * which decides what needs to be done with it.
     *
     * The listener is attached to the given Activity, and will only be detached
     * when the Activity stops. So it is recommended to only call this method in
     * an Activity's onStart method to prevent memory leak.
     *
     * @param activity where the listener is attached to
     * @param unwrapper a Function that converts a Map into a T object
     * @param handler what needs to be done with the resulting object
     * @param docRef the Reference of the Document whose changes are listened for
     * @param <T> the type of the object
     */
    public static <T> void updateObject(Activity activity, Function<Map<String, Object>, T> unwrapper,
                                        QueryResultHandler<T> handler, DocumentReference docRef) {
        docRef.addSnapshotListener(activity, (documentSnapshot, e) -> {
            if (e != null) {
                Log.e(activity.getClass().getSimpleName(),
                        String.format("%s[%s] update failed!", docRef.getParent().getId(), docRef.getId()), e);
                return;
            }
            if (documentSnapshot != null && documentSnapshot.exists()) {
                T object = unwrapper.apply(documentSnapshot.getData());
                Log.d(activity.getClass().getSimpleName(),
                        String.format("%s[%s] update successful.", docRef.getParent().getId(), docRef.getId()));
                handler.onQuerySuccess(object);
            } else {
                Log.d(activity.getClass().getSimpleName(),
                        String.format("%s[%s] updated: empty snapshot!", docRef.getParent().getId(), docRef.getId()));
            }
        });
    }

    /**
     * Listen to a Document on FireStore for changes.
     *
     * When a change occurs, a snapshot is received and converted into type T by
     * the given Function unwrapper.
     *
     * Then the resulting object is sent as a parameter into the handler's method,
     * which decides what needs to be done with it.
     *
     * The listener is attached to the given Activity, and will only be detached
     * when the Activity stops. So it is recommended to only call this method in
     * an Activity's onStart method to prevent memory leak.
     *
     * @param activity where the listener is attached to
     * @param unwrapper a Function that converts a Map into a T object
     * @param handler what needs to be done with the resulting object
     * @param path the path of the Document whose changes are listened for
     * @param <T> the type of the object
     */
    public static <T> void updateObject(Activity activity, Function<Map<String, Object>, T> unwrapper,
                                        QueryResultHandler<T> handler, String... path) {
        updateObject(activity, unwrapper, handler, getDocRef(path));
    }

    /**
     * Listen to a Collection on FireStore for changes.
     *
     * When a change occurs, a QuerySnapshot is received that contains one or more
     * DocumentSnapshots that represent changes on the Documents in the Collection.
     *
     * The DocumentSnapshots can have three different types: ADDED, MODIFIED or REMOVED.
     *
     * Depending on the change types, objects that are converted from the DocumentSnapshots
     * are added, modified or removed from the listToUpdate, and subsequent calls
     * to the corresponding methods in the UpdateHandler follow to deal with UI
     * updates and so on.
     *
     * In this method, the objects are converted using the FireStore native converter
     * which requires a no-arg constructor in the class T.
     *
     * The listener is attached to the given Activity, and will only be detached
     * when the Activity stops. So it is recommended to only call this method in
     * an Activity's onStart method to prevent memory leak.
     *
     * @param activity where the listener is attached to
     * @param type the Class model of the type into which the objects are to be converted
     * @param listToUpdate the list to update
     * @param handler what needs to be done when the list is updated
     * @param colRef the Reference of the Collection whose changes are listened for
     * @param <T> the type of the objects
     */
    public static <T> void updateObjects(Activity activity, Class<T> type, List<T> listToUpdate, UpdateHandler
            handler, CollectionReference colRef) {
        colRef.addSnapshotListener(activity, (queryDocumentSnapshots, e) -> {
            if (e != null) {
                Log.e(activity.getClass().getSimpleName(), String.format("Collection [%s] update failed!", colRef.getId()), e);
                return;
            }
            assert queryDocumentSnapshots != null;
            for (DocumentChange change : queryDocumentSnapshots.getDocumentChanges()) {
                T updatedObject = change.getDocument().toObject(type);
                switch (change.getType()) {
                    case ADDED:
                        listToUpdate.add(updatedObject);
                        Log.d(activity.getClass().getSimpleName(),
                                String.format("%s[%s] added!", colRef.getId(), change.getDocument().getId()));
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
                        Log.d(activity.getClass().getSimpleName(),
                                String.format("%s[%s] modified!", colRef.getId(), change.getDocument().getId()));
                        handler.onModified(change);
                        break;
                    case REMOVED:
                        listToUpdate.remove(change.getOldIndex());
                        Log.d(activity.getClass().getSimpleName(),
                                String.format("%s[%s] removed!", colRef.getId(), change.getDocument().getId()));
                        handler.onRemoved(change);
                        break;
                }
            }
            Log.d(activity.getClass().getSimpleName(), String.format("Collection [%s] update successful.", colRef.getId()));
        });
    }

    /**
     * Listen to a Collection on FireStore for changes.
     *
     * When a change occurs, a QuerySnapshot is received that contains one or more
     * DocumentSnapshots that represent changes on the Documents in the Collection.
     *
     * The DocumentSnapshots can have three different types: ADDED, MODIFIED or REMOVED.
     *
     * Depending on the change types, objects that are converted from the DocumentSnapshots
     * are added, modified or removed from the listToUpdate, and subsequent calls
     * to the corresponding methods in the UpdateHandler follow to deal with UI
     * updates and so on.
     *
     * In this method, the objects are converted using the FireStore native converter
     * which requires a no-arg constructor in the class T.
     *
     * The listener is attached to the given Activity, and will only be detached
     * when the Activity stops. So it is recommended to only call this method in
     * an Activity's onStart method to prevent memory leak.
     *
     * @param activity where the listener is attached to
     * @param type the Class model of the type into which the objects are to be converted
     * @param listToUpdate the list to update
     * @param handler what needs to be done when the list is updated
     * @param path the path of the Collection whose changes are listened for
     * @param <T> the type of the objects
     */
    public static <T> void updateObjects(Activity activity, Class<T> type, List<T> listToUpdate, UpdateHandler
            handler, String... path) {
        updateObjects(activity, type, listToUpdate, handler, getColRef(path));
    }

    /**
     * Listen to a Collection on FireStore for changes.
     *
     * When a change occurs, a QuerySnapshot is received that contains one or more
     * DocumentSnapshots that represent changes on the Documents in the Collection.
     *
     * The DocumentSnapshots can have three different types: ADDED, MODIFIED or REMOVED.
     *
     * Depending on the change types, objects that are converted from the DocumentSnapshots
     * are added, modified or removed from the listToUpdate, and subsequent calls
     * to the corresponding methods in the UpdateHandler follow to deal with UI
     * updates and so on.
     *
     * In this method, the objects are converted using the given unwrapper.
     *
     * The listener is attached to the given Activity, and will only be detached
     * when the Activity stops. So it is recommended to only call this method in
     * an Activity's onStart method to prevent memory leak.
     *
     * @param activity where the listener is attached to
     * @param unwrapper a Function that converts received data into T object
     * @param listToUpdate the list to update
     * @param handler what needs to be done when the list is updated
     * @param colRef the Reference of the Collection whose changes are listened for
     * @param <T> the type of the objects
     */
    public static <T> void updateObjects(Activity activity, Function<Map<String, Object>, T> unwrapper, List<T>
            listToUpdate, UpdateHandler
                                                 handler, CollectionReference colRef) {
        colRef.addSnapshotListener(activity, (queryDocumentSnapshots, e) -> {
            if (e != null) {
                Log.e(activity.getClass().getSimpleName(), String.format("Collection [%s] update failed!", colRef.getId()), e);
                return;
            }
            assert queryDocumentSnapshots != null;
            for (DocumentChange change : queryDocumentSnapshots.getDocumentChanges()) {
                T updatedObject = unwrapper.apply(change.getDocument().getData());
                switch (change.getType()) {
                    case ADDED:
                        listToUpdate.add(updatedObject);
                        Log.d(activity.getClass().getSimpleName(),
                                String.format("%s[%s] added!", colRef.getId(), change.getDocument().getId()));
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
                        Log.d(activity.getClass().getSimpleName(),
                                String.format("%s[%s] modified!", colRef.getId(), change.getDocument().getId()));
                        handler.onModified(change);
                        break;
                    case REMOVED:
                        listToUpdate.remove(change.getOldIndex());
                        Log.d(activity.getClass().getSimpleName(),
                                String.format("%s[%s] removed!", colRef.getId(), change.getDocument().getId()));
                        handler.onRemoved(change);
                        break;
                }
            }
            Log.d(activity.getClass().getSimpleName(), String.format("Collection [%s] update successful.", colRef.getId()));
        });
    }

    /**
     * Listen to a Collection on FireStore for changes.
     *
     * When a change occurs, a QuerySnapshot is received that contains one or more
     * DocumentSnapshots that represent changes on the Documents in the Collection.
     *
     * The DocumentSnapshots can have three different types: ADDED, MODIFIED or REMOVED.
     *
     * Depending on the change types, objects that are converted from the DocumentSnapshots
     * are added, modified or removed from the listToUpdate, and subsequent calls
     * to the corresponding methods in the UpdateHandler follow to deal with UI
     * updates and so on.
     *
     * In this method, the objects are converted using the given unwrapper.
     *
     * The listener is attached to the given Activity, and will only be detached
     * when the Activity stops. So it is recommended to only call this method in
     * an Activity's onStart method to prevent memory leak.
     *
     * @param activity where the listener is attached to
     * @param unwrapper a Function that converts received data into T object
     * @param listToUpdate the list to update
     * @param handler what needs to be done when the list is updated
     * @param path the path of the Collection whose changes are listened for
     * @param <T> the type of the objects
     */
    public static <T> void updateObjects(Activity activity, Function<Map<String, Object>, T> unwrapper,
                                         List<T> listToUpdate, UpdateHandler handler, String... path) {
        updateObjects(activity, unwrapper, listToUpdate, handler, getColRef(path));
    }

    // UPLOADING OPERATIONS

    /**
     * Upload an Object onto FireStore using a Map.
     *
     * The Map contains fields of the object.
     * The Map keys are the names of the fields, and Map values are the values of the fields.
     *
     * The upload destination is given by the DocumentReference docRef.
     * If the destination already stores data, it will be overwritten. For different
     * upload settings, pass in a SetOptions. See {@link #uploadObject(Activity, Map, SetOptions, DocumentReference)}
     *
     * The task is attached to the given Activity, and is detached when either
     * the task or the Activity is finished.
     *
     * @param activity where the task is attached to
     * @param map represents an object, keys = field names, values = field values
     * @param docRef the destination of this upload
     */
    public static void uploadObject(Activity activity, Map<String, Object> map, DocumentReference docRef) {
        docRef.set(map).addOnSuccessListener(activity, v -> {
            Log.d(activity.getClass().getSimpleName(),
                    String.format("%s[%s] upload successful.", docRef.getParent().getId(), docRef.getId()));
        }).addOnFailureListener(activity, e -> {
            Log.e(activity.getClass().getSimpleName(),
                    String.format("%s[%s] upload failed!", docRef.getParent().getId(), docRef.getId()), e);
        });
    }

    /**
     * Upload an Object onto FireStore using a Map.
     *
     * The Map contains fields of the object.
     * The Map keys are the names of the fields, and Map values are the values of the fields.
     *
     * The upload destination is given by the Document path.
     * If the destination already stores data, it will be overwritten. For different
     * upload settings, pass in a SetOptions. See {@link #uploadObject(Activity, Map, SetOptions, String...)}
     *
     * The task is attached to the given Activity, and is detached when either
     * the task or the Activity is finished.
     *
     * @param activity where the task is attached to
     * @param map represents an object, keys = field names, values = field values
     * @param path the destination of this upload
     */
    public static void uploadObject(Activity activity, Map<String, Object> map, String... path) {
        uploadObject(activity, map, getDocRef(path));
    }

    /**
     * Upload an Object onto FireStore using a Map.
     *
     * The Map contains fields of the object.
     * The Map keys are the names of the fields, and Map values are the values of the fields.
     *
     * The upload destination is given by the DocumentReference docRef.
     *
     * The setOptions are retrieved from calling static methods of the SetOptions class.
     * @see <a href="https://firebase.google.com/docs/reference/android/com/google/firebase/firestore/SetOptions">Firebase Documentation - SetOptions</a>
     *
     * The task is attached to the given Activity, and is detached when either
     * the task or the Activity is finished.
     *
     * @param activity where the task is attached to
     * @param map represents an object, keys = field names, values = field values
     * @param setOption a SetOptions that determine the upload settings
     * @param docRef the destination of this upload
     */
    public static void uploadObject(Activity activity, Map<String, Object> map,
                                    SetOptions setOption, DocumentReference docRef) {
        docRef.set(map, setOption).addOnSuccessListener(activity, v -> {
            Log.d(activity.getClass().getSimpleName(),
                    String.format("%s[%s] upload successful.", docRef.getParent().getId(), docRef.getId()));
        }).addOnFailureListener(activity, e -> {
            Log.e(activity.getClass().getSimpleName(),
                    String.format("%s[%s] upload failed!", docRef.getParent().getId(), docRef.getId()), e);
        });
    }

    /**
     * Upload an Object onto FireStore using a Map.
     *
     * The Map contains fields of the object.
     * The Map keys are the names of the fields, and Map values are the values of the fields.
     *
     * The upload destination is given by the Document path.
     *
     * The setOptions are retrieved from calling static methods of the SetOptions class.
     * @see <a href="https://firebase.google.com/docs/reference/android/com/google/firebase/firestore/SetOptions">Firebase Documentation - SetOptions</a>
     *
     * The task is attached to the given Activity, and is detached when either
     * the task or the Activity is finished.
     *
     * @param activity where the task is attached to
     * @param map represents an object, keys = field names, values = field values
     * @param setOption a SetOptions that determine the upload settings
     * @param path the destination of this upload
     */
    public static void uploadObject(Activity activity, Map<String, Object> map, SetOptions setOption, String... path) {
        uploadObject(activity, map, setOption, getDocRef(path));
    }

    /**
     * Upload an Object onto FireStore.
     *
     * The object needs to be converted into a Map before uploading, and this is
     * done by the passed-in wrapper Function.
     *
     * The upload destination is given by the DocumentReference docRef.
     * If the destination already stores data, it will be overwritten. For different
     * upload settings, pass in a SetOptions. See {@link #uploadObject(Activity, Object, Function, SetOptions, DocumentReference)}
     *
     * The task is attached to the given Activity, and is detached when either
     * the task or the Activity is finished.
     *
     * @param activity where the task is attached to
     * @param object the object to be uploaded
     * @param wrapper a Function that converts the object into a Map
     * @param docRef the destination of this upload
     * @param <T> the type of the object
     */
    public static <T> void uploadObject(Activity activity, T object,
                                        Function<T, Map<String, Object>> wrapper, DocumentReference docRef) {
        uploadObject(activity, wrapper.apply(object), docRef);
    }

    /**
     * Upload an Object onto FireStore.
     *
     * The object needs to be converted into a Map before uploading, and this is
     * done by the passed-in wrapper Function.
     *
     * The upload destination is given by the Document path.
     * If the destination already stores data, it will be overwritten. For different
     * upload settings, pass in a SetOptions. See
     * {@link #uploadObject(Activity, Object, Function, SetOptions, String...)}
     *
     * The task is attached to the given Activity, and is detached when either
     * the task or the Activity is finished.
     *
     * @param activity where the task is attached to
     * @param object the object to be uploaded
     * @param wrapper a Function that converts the object into a Map
     * @param path the destination of this upload
     * @param <T> the type of the object
     */
    public static <T> void uploadObject(Activity activity, T object,
                                        Function<T, Map<String, Object>> wrapper, String... path) {
        uploadObject(activity, object, wrapper, getDocRef(path));
    }

    /**
     * Upload an Object onto FireStore.
     *
     * The object needs to be converted into a Map before uploading, and this is
     * done by the passed-in wrapper Function.
     *
     * The upload destination is given by the DocumentReference docRef.
     *
     * The setOptions are retrieved from calling static methods of the SetOptions class.
     * @see <a href="https://firebase.google.com/docs/reference/android/com/google/firebase/firestore/SetOptions">Firebase Documentation - SetOptions</a>
     *
     * The task is attached to the given Activity, and is detached when either
     * the task or the Activity is finished.
     *
     * @param activity where the task is attached to
     * @param object the object to be uploaded
     * @param wrapper a Function that converts the object into a Map
     * @param setOption a SetOptions that determine the upload settings
     * @param docRef the destination of this upload
     * @param <T> the type of the object
     */
    public static <T> void uploadObject(Activity activity, T object, Function<T, Map<String, Object>> wrapper,
                                        SetOptions setOption, DocumentReference docRef) {
        uploadObject(activity, wrapper.apply(object), setOption, docRef);
    }

    /**
     * Upload an Object onto FireStore.
     *
     * The object needs to be converted into a Map before uploading, and this is
     * done by the passed-in wrapper Function.
     *
     * The upload destination is given by the Document path.
     *
     * The setOptions are retrieved from calling static methods of the SetOptions class.
     * @see <a href="https://firebase.google.com/docs/reference/android/com/google/firebase/firestore/SetOptions">Firebase Documentation - SetOptions</a>
     *
     * The task is attached to the given Activity, and is detached when either
     * the task or the Activity is finished.
     *
     * @param activity where the task is attached to
     * @param object the object to be uploaded
     * @param wrapper a Function that converts the object into a Map
     * @param setOption a SetOptions that determine the upload settings
     * @param path the destination of this upload
     * @param <T> the type of the object
     */
    public static <T> void uploadObject(Activity activity, T object, Function<T, Map<String, Object>> wrapper,
                                        SetOptions setOption, String... path) {
        uploadObject(activity, object, wrapper, setOption, getDocRef(path));
    }

    /**
     * Add an object to a Collection on FireStore.
     *
     * This will make a Document under the Collection with an auto-generated ID.
     * i.e. equivalent to uploading an object onto <code>colRef.document()</code>.
     *
     * See {@link #uploadObject(Activity, Object, Function, DocumentReference)}
     *
     * @param activity where the task is attached to
     * @param object the object to be uploaded
     * @param wrapper a Function that converts the object into a Map
     * @param colRef the Collection into which the object is to be added
     * @param <T> the type of the object
     */
    public static <T> void addObject(Activity activity, T object,
                                     Function<T, Map<String, Object>> wrapper, CollectionReference colRef) {
        uploadObject(activity, object, wrapper, colRef.document());
    }

    /**
     * Add an object to a Collection on FireStore.
     *
     * This will make a Document under the Collection with an auto-generated ID.
     * i.e. equivalent to uploading an object onto <code>getColRef(path).document()</code>.
     *
     * See {@link #uploadObject(Activity, Object, Function, String...)}
     *
     * @param activity where the task is attached to
     * @param object the object to be uploaded
     * @param wrapper a Function that converts the object into a Map
     * @param path the path of the Collection into which the object is to be added
     * @param <T> the type of the object
     */
    public static <T> void addObject(Activity activity, T object,
                                     Function<T, Map<String, Object>> wrapper, String... path) {
        uploadObject(activity, object, wrapper, getColRef(path).document());
    }

    /**
     * Add an object to a Collection on FireStore.
     *
     * This will make a Document under the Collection with an auto-generated ID.
     * i.e. equivalent to uploading an object onto <code>colRef.document()</code>.
     *
     * See {@link #uploadObject(Activity, Object, Function, SetOptions, DocumentReference)}
     *
     * @param activity where the task is attached to
     * @param object the object to be uploaded
     * @param wrapper a Function that converts the object into a Map
     * @param setOption a SetOptions that determine the upload settings
     * @param colRef the Collection into which the object is to be added
     * @param <T> the type of the object
     */
    public static <T> void addObject(Activity activity, T object, Function<T, Map<String, Object>> wrapper,
                                     SetOptions setOption, CollectionReference colRef) {
        uploadObject(activity, object, wrapper, setOption, colRef.document());
    }

    /**
     * Add an object to a Collection on FireStore.
     *
     * This will make a Document under the Collection with an auto-generated ID.
     * i.e. equivalent to uploading an object onto <code>getColRef(path).document()</code>.
     *
     * See {@link #uploadObject(Activity, Object, Function, SetOptions, String...)}
     *
     * @param activity where the task is attached to
     * @param object the object to be uploaded
     * @param wrapper a Function that converts the object into a Map
     * @param setOption a SetOptions that determine the upload settings
     * @param path the path of the Collection into which the object is to be added
     * @param <T> the type of the object
     */
    public static <T> void addObject(Activity activity, T object, Function<T, Map<String, Object>> wrapper,
                                     SetOptions setOption, String... path) {
        uploadObject(activity, object, wrapper, setOption, getColRef(path).document());
    }

    /**
     * Add an object to a Collection on FireStore using a Map.
     *
     * This will make a Document under the Collection with an auto-generated ID.
     * i.e. equivalent to uploading an object onto <code>colRef.document()</code>.
     *
     * See {@link #uploadObject(Activity, Map, DocumentReference)}
     *
     * @param activity where the task is attached to
     * @param map represents an object, keys = field names, values = field values
     * @param colRef the Collection into which the object is to be added
     */
    public static void addObject(Activity activity, Map<String, Object> map, CollectionReference colRef) {
        uploadObject(activity, map, colRef.document());
    }

    /**
     * Add an object to a Collection on FireStore using a Map.
     *
     * This will make a Document under the Collection with an auto-generated ID.
     * i.e. equivalent to uploading an object onto <code>getColRef(path).document()</code>.
     *
     * See {@link #uploadObject(Activity, Map, String...)}
     *
     * @param activity where the task is attached to
     * @param map represents an object, keys = field names, values = field values
     * @param path the path of the Collection into which the object is to be added
     */
    public static void addObject(Activity activity, Map<String, Object> map, String... path) {
        uploadObject(activity, map, getColRef(path).document());
    }

    /**
     * Add an object to a Collection on FireStore using a Map.
     *
     * This will make a Document under the Collection with an auto-generated ID.
     * i.e. equivalent to uploading an object onto <code>colRef.document()</code>.
     *
     * See {@link #uploadObject(Activity, Map, SetOptions, DocumentReference)}
     *
     * @param activity where the task is attached to
     * @param map represents an object, keys = field names, values = field values
     * @param setOption a SetOptions that determine the upload settings
     * @param colRef the Collection into which the object is to be added
     */
    public static void addObject(Activity activity, Map<String, Object> map, SetOptions setOption, CollectionReference colRef) {
        uploadObject(activity, map, setOption, colRef.document());
    }

    /**
     * Add an object to a Collection on FireStore using a Map.
     *
     * This will make a Document under the Collection with an auto-generated ID.
     * i.e. equivalent to uploading an object onto <code>getColRef(path).document()</code>.
     *
     * See {@link #uploadObject(Activity, Map, SetOptions, String...)}
     *
     * @param activity where the task is attached to
     * @param map represents an object, keys = field names, values = field values
     * @param setOption a SetOptions that determine the upload settings
     * @param path the path of the Collection into which the object is to be added
     */
    public static void addObject(Activity activity, Map<String, Object> map, SetOptions setOption, String... path) {
        uploadObject(activity, map, setOption, getColRef(path).document());
    }

    /**
     * Delete a Document from FireStore.
     *
     * The task is attached to the given Activity, and is detached when either
     * the task or the Activity is finished.
     *
     * @param activity where the task is attached to
     * @param docRef the Document to be deleted
     */
    public static void deleteDocument(Activity activity, DocumentReference docRef) {
        docRef.delete().addOnSuccessListener(activity, aVoid -> {
            Log.d(activity.getClass().getSimpleName(),
                    String.format("%s[%s] delete successful.", docRef.getParent().getId(), docRef.getId()));
        }).addOnFailureListener(activity, e -> {
            Log.e(activity.getClass().getSimpleName(),
                    String.format("%s[%s] delete failed!", docRef.getParent().getId(), docRef.getId()), e);
        });
    }

    /**
     * Delete a Document from FireStore.
     *
     * The task is attached to the given Activity, and is detached when either
     * the task or the Activity is finished.
     *
     * @param activity where the task is attached to
     * @param path the path of the Document to be deleted
     */
    public static void deleteDocument(Activity activity, String... path) {
        deleteDocument(activity, getDocRef(path));
    }

    /**
     * Upload a list of objects into a Collection on FireStore.
     *
     * The objects are converted into Maps using the given wrapper Function.
     *
     * For each object, a Document ID is generated by the given idGenerator Function.
     * This can be achieved using a lambda expression, for example:
     * <code>object -> object.getID()</code>
     *
     * Notice that if a Document is already present in the Collection, then it
     * will be overwritten by the new Object uploaded.
     *
     * The task is attached to the given Activity, and is detached when either
     * the task or the Activity is finished.
     *
     * @param activity where the task is attached to
     * @param listToUpload a list of Objects to upload
     * @param wrapper a Function that converts the object into Maps
     * @param idGenerator a Function that generates a Document ID for each object
     * @param colRef the Collection to upload objects into
     * @param <T> the type of the objects
     */
    public static <T> void uploadObjects(Activity activity, List<T> listToUpload, Function<T, Map<String, Object>>
            wrapper, Function<T, String> idGenerator, CollectionReference colRef) {
        WriteBatch batch = FirebaseFirestore.getInstance().batch();
        for (T object : listToUpload) {
            DocumentReference docRef = colRef.document(idGenerator.apply(object));
            batch.set(docRef, wrapper.apply(object));
        }
        batch.commit().addOnSuccessListener(activity, aVoid -> {
            Log.d(activity.getClass().getSimpleName(), String.format("Collection [%s] upload successful.", colRef.getId()));
        }).addOnFailureListener(activity, e -> {
            Log.e(activity.getClass().getSimpleName(), String.format("Collection [%s] upload failed!", colRef.getId()), e);
        });
    }

    /**
     * Upload a list of objects into a Collection on FireStore.
     *
     * The objects are converted into Maps using the given wrapper Function.
     *
     * A Document ID is auto-generated for each object. (This is equivalent to
     * using <code>colRef.document()</code>)
     *
     * Notice that if a Document is already present in the Collection, then it
     * will be overwritten by the new Object uploaded.
     *
     * The task is attached to the given Activity, and is detached when either
     * the task or the Activity is finished.
     *
     * @param activity where the task is attached to
     * @param listToUpload a list of Objects to upload
     * @param wrapper a Function that converts the object into Maps
     * @param colRef the Collection to upload objects into
     * @param <T> the type of the objects
     */
    public static <T> void uploadObjects(Activity activity, List<T> listToUpload, Function<T, Map<String, Object>>
            wrapper, CollectionReference colRef) {
        WriteBatch batch = FirebaseFirestore.getInstance().batch();
        for (T object : listToUpload) {
            batch.set(colRef.document(), wrapper.apply(object));
        }
        batch.commit().addOnSuccessListener(activity, aVoid -> {
            Log.d(activity.getClass().getSimpleName(), String.format("Collection [%s] upload successful.", colRef.getId()));
        }).addOnFailureListener(activity, e -> {
            Log.e(activity.getClass().getSimpleName(), String.format("Collection [%s] upload failed!", colRef.getId()), e);
        });
    }

    // MODIFYING OPERATIONS

    /**
     * Add fields to a Document on FireStore.
     *
     * The new fields are represented by a Map whose keys are the field names and
     * values are the field values.
     *
     * Notice that if the fields contain those that are already present in the
     * Document, then the old values will be replaced by their new values.
     *
     * The task is attached to the given Activity, and is detached when either
     * the task or the Activity is finished.
     *
     * @param activity where the task is attached to
     * @param docRef the Document to add fields into
     * @param fieldsMap a Map that represents the fields to be added
     */
    public static void addFields(Activity activity, DocumentReference docRef, Map<String, Object> fieldsMap) {
        uploadObject(activity, fieldsMap, SetOptions.merge(), docRef);
    }

    /**
     * Add fields to a Document on FireStore.
     *
     * At least one field name and value needs to be passed in. More fields can
     * be passed in as varargs.
     *
     * Notice that if the fields contain those that are already present in the
     * Document, then the old values will be replaced by their new values.
     *
     * The task is attached to the given Activity, and is detached when either
     * the task or the Activity is finished.
     *
     * @param activity where the task is attached to
     * @param docRef the Document to add fields into
     * @param fieldName the name of the first field added
     * @param fieldValue the value of the first field added
     * @param moreFields more fields to be added
     */
    public static void addFields(Activity activity, DocumentReference docRef, String fieldName, Object fieldValue, Object... moreFields) {
        if (moreFields.length % 2 != 0) {
            throw new IllegalArgumentException("All but the first two arguments must be in (fieldName, fieldValue) pairs!");
        }
        Map<String, Object> fieldsMap = new HashMap<>(1 + moreFields.length / 2);
        String name = fieldName;
        Object value = fieldValue;
        fieldsMap.put(name, value);
        for (int i = 0; i < moreFields.length; i++) {
            if (i % 2 == 0) {
                if (moreFields[i] == null || !(moreFields[i] instanceof String)) {
                    throw new IllegalArgumentException("Field names must be Strings!");
                } else {
                    name = (String) moreFields[i];
                }
            } else {
                if (moreFields[i] == null) {
                    throw new IllegalArgumentException("Field values must not be null!");
                } else {
                    value = moreFields[i];
                    fieldsMap.put(name, value);
                }
            }
        }
        addFields(activity, docRef, fieldsMap);
    }

    /**
     * Update fields of a Document on FireStore.
     *
     * The fields are represented by a Map with its keys being the field names
     * and its values being the field values.
     *
     * The task is attached to the given Activity, and is detached when either
     * the task or the Activity is finished.
     *
     * @param activity where the task is attached to
     * @param docRef the destination of this update
     * @param fieldsMap represents the fields to update, keys = field names, values = field values
     */
    public static void updateFields(Activity activity, DocumentReference docRef, Map<String, Object> fieldsMap) {
        docRef.update(fieldsMap).addOnSuccessListener(activity, v -> {
            Log.d(activity.getClass().getSimpleName(),
                    String.format("%s[%s] fields update successful.", docRef.getParent().getId(), docRef.getId()));
        }).addOnFailureListener(e -> {
            Log.e(activity.getClass().getSimpleName(),
                    String.format("%s[%s] fields update failed!", docRef.getParent().getId(), docRef.getId()), e);
        });
    }

    /**
     * Update fields of a Document on FireStore.
     *
     * At least one field's name and value needs to be passed in.
     * More field name-value pairs can be passed in as varargs.
     *
     * The task is attached to the given Activity, and is detached when either
     * the task or the Activity is finished.
     *
     * @param activity where the task is attached to
     * @param docRef the destination of this update
     * @param fieldName the name of the first field to update
     * @param fieldValue the value of the first field to update
     * @param moreFields more fields to update in name-value pairs
     */
    public static void updateFields(Activity activity, DocumentReference docRef, String fieldName, Object fieldValue,
                                    Object... moreFields) {
        if (moreFields.length % 2 != 0) {
            throw new IllegalArgumentException("All but the first two arguments must be in (fieldName, fieldValue) pairs!");
        }
        Map<String, Object> fieldsMap = new HashMap<>(1 + moreFields.length / 2);
        String name = fieldName;
        Object value = fieldValue;
        fieldsMap.put(name, value);
        for (int i = 0; i < moreFields.length; i++) {
            if (i % 2 == 0) {
                if (moreFields[i] == null || !(moreFields[i] instanceof String)) {
                    throw new IllegalArgumentException("Field names must be Strings!");
                } else {
                    name = (String) moreFields[i];
                }
            } else {
                if (moreFields[i] == null) {
                    throw new IllegalArgumentException("Field values must not be null!");
                } else {
                    value = moreFields[i];
                    fieldsMap.put(name, value);
                }
            }
        }
        updateFields(activity, docRef, fieldsMap);
    }

    /**
     * Delete fields of a Document on FireStore.
     *
     * Only the names of the fields are required to delete them.
     *
     * The task is attached to the given Activity, and is detached when either
     * the task or the Activity is finished.
     *
     * @param activity where the task is attached to
     * @param docRef the Document from which fields are deleted
     * @param fieldNames the names of the fields to delete
     */
    public static void deleteFields(Activity activity, DocumentReference docRef, String... fieldNames) {
        Map<String, Object> fieldsToDelete = new HashMap<>(fieldNames.length);
        for (String fieldName : fieldNames) {
            fieldsToDelete.put(fieldName, FieldValue.delete());
        }
        docRef.update(fieldsToDelete).addOnSuccessListener(activity, aVoid -> {
            Log.d(activity.getClass().getSimpleName(),
                    String.format("%s[%s] fields delete successful.", docRef.getParent().getId(), docRef.getId()));
        }).addOnFailureListener(activity, e -> {
            Log.e(activity.getClass().getSimpleName(),
                    String.format("%s[%s] fields delete failed!", docRef.getParent().getId(), docRef.getId()), e);
        });
    }

    /**
     * Add elements to an Array field of a Document on FireStore.
     *
     * Notice that only elements not already present in the array will be added.
     *
     * The task is attached to the given Activity, and is detached when either
     * the task or the Activity is finished.
     *
     * @param activity where the task is attached to
     * @param docRef the Document whose array field is to be augmented
     * @param arrayFieldName the name of the array field
     * @param elements the elements to be added into the array field
     */
    public static void addElementsToArrayField(Activity activity, DocumentReference docRef, String arrayFieldName,
                                               @NonNull Object... elements) {
        docRef.update(arrayFieldName, FieldValue.arrayUnion(elements))
                .addOnSuccessListener(activity, v -> {
                    Log.d(activity.getClass().getSimpleName(), String.format("%s[%s].%s update successful.",
                            docRef.getParent().getId(), docRef.getId(), arrayFieldName));
                }).addOnFailureListener(activity, e -> {
            Log.e(activity.getClass().getSimpleName(), String.format("%s[%s].%s update failed!",
                    docRef.getParent().getId(), docRef.getId(), arrayFieldName), e);
        });
    }

    /**
     * Remove elements from an Array field of a Document on FireStore.
     *
     * Notice that all instances of the given elements in the array will be removed.
     *
     * The task is attached to the given Activity, and is detached when either
     * the task or the Activity is finished.
     *
     * @param activity where the task is attached to
     * @param docRef the Document whose array field is to be shortened
     * @param arrayFieldName the name of the array field
     * @param elements the elements to be deleted from the array field
     */
    public static void removeElementsFromArrayField(Activity activity, DocumentReference docRef,
                                                    String arrayFieldName, @NonNull Object... elements) {
        docRef.update(arrayFieldName, FieldValue.arrayRemove(elements))
                .addOnSuccessListener(activity, v -> {
                    Log.d(activity.getClass().getSimpleName(), String.format("%s[%s].%s update successful.",
                            docRef.getParent().getId(), docRef.getId(), arrayFieldName));
                }).addOnFailureListener(activity, e -> {
                    Log.e(activity.getClass().getSimpleName(), String.format("%s[%s].%s update failed!",
                    docRef.getParent().getId(), docRef.getId(), arrayFieldName), e);
        });
    }
}
