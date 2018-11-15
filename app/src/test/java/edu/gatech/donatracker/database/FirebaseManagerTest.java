package edu.gatech.donatracker.database;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static edu.gatech.donatracker.database.FirebaseManager.getColRef;
import static edu.gatech.donatracker.database.FirebaseManager.getDocRef;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Yuhang Li on 2018/11/06
 *
 * Unit tests for the FirebaseManager class methods.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(FirebaseFirestore.class)
public class FirebaseManagerTest {

    private static final int TIMEOUT = 200;

    // mocked objects
    private FirebaseFirestore mockFireStore;
    private CollectionReference mockColRef;
    private DocumentReference mockDocRef;

    // invocation counter
    private int mockFireStoreCounter;
    private int mockColRefCounter;
    private int mockDocRefCounter;

    // fields
    private String colStr = "locations";
    private String docStr = "0";
    private CollectionReference colRef;
    private DocumentReference docRef;

    @Before
    public void init() {
        // initialize mock objects
        PowerMockito.mockStatic(FirebaseFirestore.class);
        mockFireStore = mock(FirebaseFirestore.class);
        mockColRef = mock(CollectionReference.class);
        mockDocRef = mock(DocumentReference.class);

        // stubbing
        when(FirebaseFirestore.getInstance()).thenReturn(mockFireStore);
        when(mockFireStore.collection(colStr)).thenReturn(mockColRef);
        when(mockColRef.document(docStr)).thenReturn(mockDocRef);
        when(mockDocRef.collection(colStr)).thenReturn(mockColRef);

        // counter initialization
        mockFireStoreCounter = 0;
        mockColRefCounter = 0;
        mockDocRefCounter = 0;
    }

    @Test
    public void testGetDocRef() {
        // test no argument passed in situation
        try {
            getDocRef();
            fail("Exception not thrown when no argument is passed in!");
        } catch (Exception e) {
            assertEquals("No document path passed in!", e.getMessage());
        }

        // test wrong number of arguments passed in situation
        try {
            getDocRef(colStr);
            fail("Exception not thrown when wrong number of arguments are passed in!");
        } catch (Exception e) {
            assertEquals("Number of args must be even!", e.getMessage());
        }
        try {
            getDocRef(colStr, docStr, colStr);
            fail("Exception not thrown when wrong number of arguments are passed in!");
        } catch (Exception e) {
            assertEquals("Number of args must be even!", e.getMessage());
        }

        // test if correct result was returned when querying document with depth of 2
        docRef = getDocRef(colStr, docStr);
        assertEquals(mockDocRef, docRef);

        // verify internal calls were made correctly
        verify(mockFireStore, times(++mockFireStoreCounter)).collection(colStr);
        verify(mockColRef, times(++mockColRefCounter)).document(docStr);

        // test if correct result was returned when querying document with depth of 4
        docRef = getDocRef(colStr, docStr, colStr, docStr);
        assertEquals(mockDocRef, docRef);

        // verify internal calls were made correctly
        verify(mockFireStore, times(++mockFireStoreCounter)).collection(colStr);
        verify(mockColRef, times(mockColRefCounter += 2)).document(docStr);
        verify(mockDocRef, times(++mockDocRefCounter)).collection(colStr);
    }

    @Test
    public void testGetColRef() {
        // test no argument passed in situation
        try {
            getColRef();
            fail("Exception not thrown when no argument is passed in!");
        } catch (Exception e) {
            assertEquals("No collection path passed in!", e.getMessage());
        }

        // test wrong number of arguments passed in situation
        try {
            getColRef(colStr, docStr);
            fail("Exception not thrown when wrong number of arguments are passed in!");
        } catch (Exception e) {
            assertEquals("Number of args must be odd!", e.getMessage());
        }

        // test if correct result was returned when querying document with depth of 2
        colRef = getColRef(colStr);
        assertEquals(mockColRef, colRef);

        // verify internal calls were made correctly
        verify(mockFireStore, times(++mockFireStoreCounter)).collection(colStr);

        // test if correct result was returned when querying document with depth of 4
        colRef = getColRef(colStr, docStr, colStr);
        assertEquals(mockColRef, colRef);

        // verify internal calls were made correctly
        verify(mockFireStore, times(++mockFireStoreCounter)).collection(colStr);
        verify(mockColRef, times(++mockColRefCounter)).document(docStr);
        verify(mockDocRef, times(++mockDocRefCounter)).collection(colStr);
    }
}
