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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by Yuhang Li on 2018/10/29
 * <p>
 * Handles all operations on Firebase
 */
public class FirebaseManager {

    private static final String TAG = FirebaseManager.class.getSimpleName();

    // REFERENCE GENERATION

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

    // DOWNLOADING OPERATIONS

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

    public static <T> void getObject(Activity activity, Class<T> type, QueryResultHandler<T> handler, String... path) {
        getObject(activity, type, handler, getDocRef(path));
    }

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
                    Log.d(activity.getClass().getSimpleName(), "Collection [" + colRef.getId() + "] fetch successful.");
                    for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                        listToFillObjects.add(queryDocumentSnapshot.toObject(type));
                    }
                })
                .addOnFailureListener(activity, e -> {
                    Log.e(activity.getClass().getSimpleName(), "Collection [" + colRef.getId() + "] fetch failed!", e);
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
                    Log.d(activity.getClass().getSimpleName(), "Collection [" + colRef.getId() + "] fetch successful.");
                    for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                        listToFillObjects.add(unwrapper.apply(queryDocumentSnapshot.getData()));
                    }
                })
                .addOnFailureListener(activity, e -> {
                    Log.e(activity.getClass().getSimpleName(), "Collection [" + colRef.getId() + "] fetch failed!", e);
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

    public static <T> void updateObject(Activity activity, Class<T> type, QueryResultHandler<T> handler,
                                        String... path) {
        updateObject(activity, type, handler, getDocRef(path));
    }

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

    public static <T> void updateObject(Activity activity, Function<Map<String, Object>, T> unwrapper,
                                        QueryResultHandler<T> handler, String... path) {
        updateObject(activity, unwrapper, handler, getDocRef(path));
    }

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

    public static <T> void updateObjects(Activity activity, Class<T> type, List<T> listToUpdate, UpdateHandler
            handler, String... path) {
        updateObjects(activity, type, listToUpdate, handler, getColRef(path));
    }

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

    public static <T> void updateObjects(Activity activity, Function<Map<String, Object>, T> unwrapper,
                                         List<T> listToUpdate, UpdateHandler handler, String... path) {
        updateObjects(activity, unwrapper, listToUpdate, handler, getColRef(path));
    }

    // UPLOADING OPERATIONS

    public static void uploadMap(Activity activity, Map<String, Object> map, DocumentReference docRef) {
        docRef.set(map).addOnSuccessListener(activity, v -> {
            Log.d(activity.getClass().getSimpleName(),
                    String.format("%s[%s] upload successful.", docRef.getParent().getId(), docRef.getId()));
        }).addOnFailureListener(activity, e -> {
            Log.e(activity.getClass().getSimpleName(),
                    String.format("%s[%s] upload failed!", docRef.getParent().getId(), docRef.getId()), e);
        });
    }

    public static void uploadMap(Activity activity, Map<String, Object> map, String... path) {
        uploadMap(activity, map, getDocRef(path));
    }

    public static void uploadMap(Activity activity, Map<String, Object> map,
                                 SetOptions setOption, DocumentReference docRef) {
        docRef.set(map, setOption).addOnSuccessListener(activity, v -> {
            Log.d(activity.getClass().getSimpleName(),
                    String.format("%s[%s] upload successful.", docRef.getParent().getId(), docRef.getId()));
        }).addOnFailureListener(activity, e -> {
            Log.e(activity.getClass().getSimpleName(),
                    String.format("%s[%s] upload failed!", docRef.getParent().getId(), docRef.getId()), e);
        });
    }

    public static void uploadMap(Activity activity, Map<String, Object> map, SetOptions setOption, String... path) {
        uploadMap(activity, map, setOption, getDocRef(path));
    }

    public static <T> void uploadObject(Activity activity, T object,
                                        Function<T, Map<String, Object>> wrapper, DocumentReference docRef) {
        uploadMap(activity, wrapper.apply(object), docRef);
    }

    public static <T> void uploadObject(Activity activity, T object,
                                        Function<T, Map<String, Object>> wrapper, String... path) {
        uploadObject(activity, object, wrapper, getDocRef(path));
    }

    public static <T> void uploadObject(Activity activity, T object, Function<T, Map<String, Object>> wrapper,
                                        SetOptions setOption, DocumentReference docRef) {
        uploadMap(activity, wrapper.apply(object), setOption, docRef);
    }

    public static <T> void uploadObject(Activity activity, T object, Function<T, Map<String, Object>> wrapper,
                                        SetOptions setOption, String... path) {
        uploadObject(activity, object, wrapper, setOption, getDocRef(path));
    }

    public static <T> void addObject(Activity activity, T object,
                                     Function<T, Map<String, Object>> wrapper, CollectionReference colRef) {
        uploadObject(activity, object, wrapper, colRef.document());
    }

    public static <T> void addObject(Activity activity, T object,
                                     Function<T, Map<String, Object>> wrapper, String... path) {
        uploadObject(activity, object, wrapper, getColRef(path).document());
    }

    public static <T> void addObject(Activity activity, T object, Function<T, Map<String, Object>> wrapper,
                                     SetOptions setOption, CollectionReference colRef) {
        uploadObject(activity, object, wrapper, setOption, colRef.document());
    }

    public static <T> void addObject(Activity activity, T object, Function<T, Map<String, Object>> wrapper,
                                     SetOptions setOption, String... path) {
        uploadObject(activity, object, wrapper, setOption, getColRef(path).document());
    }

    public static <T> void addMap(Activity activity, Map<String, Object> map, CollectionReference colRef) {
        uploadMap(activity, map, colRef.document());
    }

    public static <T> void addMap(Activity activity, Map<String, Object> map, String... path) {
        uploadMap(activity, map, getColRef(path).document());
    }

    public static <T> void addMap(Activity activity, Map<String, Object> map, SetOptions setOption,
                                  CollectionReference colRef) {
        uploadMap(activity, map, setOption, colRef.document());
    }

    public static <T> void addMap(Activity activity, Map<String, Object> map, SetOptions setOption, String... path) {
        uploadMap(activity, map, setOption, getColRef(path).document());
    }

    public static void updateFields(Activity activity, DocumentReference docRef, Map<String, Object> fieldsMap) {
        docRef.update(fieldsMap).addOnSuccessListener(activity, v -> {
            Log.d(activity.getClass().getSimpleName(),
                    String.format("%s[%s] fields update successful.", docRef.getParent().getId(), docRef.getId()));
        }).addOnFailureListener(e -> {
            Log.e(activity.getClass().getSimpleName(),
                    String.format("%s[%s] fields update failed!", docRef.getParent().getId(), docRef.getId()), e);
        });
    }

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

    public static void deleteDocument(Activity activity, DocumentReference docRef) {
        docRef.delete().addOnSuccessListener(activity, aVoid -> {
            Log.d(activity.getClass().getSimpleName(),
                    String.format("%s[%s] delete successful.", docRef.getParent().getId(), docRef.getId()));
        }).addOnFailureListener(activity, e -> {
            Log.e(activity.getClass().getSimpleName(),
                    String.format("%s[%s] delete failed!", docRef.getParent().getId(), docRef.getId()), e);
        });
    }
}
