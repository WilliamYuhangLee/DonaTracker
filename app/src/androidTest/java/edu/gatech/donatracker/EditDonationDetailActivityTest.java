package edu.gatech.donatracker;

import android.app.Activity;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import android.support.test.rule.ActivityTestRule;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import edu.gatech.donatracker.controller.EditDonationDetailActivity;
import edu.gatech.donatracker.model.Donation;

@RunWith(AndroidJUnit4.class)
public class EditDonationDetailActivityTest {

    @Mock
    private EditText editTextShortDescription;
    @Mock
    private EditText editTextFullDescription;
    @Mock
    private EditText editTextValueInUSD;
    @Mock
    private EditText editTextComment;
    @Mock
    private EditText editTextCategory;
    @Spy
    private Donation currentDonation = new Donation();
    @Mock
    private FirebaseFirestore firebaseFirestore;

    @Rule
    @InjectMocks
    public ActivityTestRule<EditDonationDetailActivity> mActivityRule =
            new ActivityTestRule<>(EditDonationDetailActivity.class);

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
//        Context appContext = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void onClickSaveDonationCorrectTest() {
        String emptyString = "";
        Mockito.when(editTextShortDescription.getText().toString()).thenReturn(emptyString);
        Mockito.when(editTextFullDescription.getText().toString()).thenReturn(emptyString);
        Mockito.when(editTextValueInUSD.getText().toString()).thenReturn(emptyString);
        Mockito.when(editTextComment.getText().toString()).thenReturn(emptyString);
        Mockito.when(editTextCategory.getText().toString()).thenReturn("0");
        View view = Mockito.mock(View.class);
        DocumentReference donationRef = Mockito.mock(DocumentReference.class);
        Mockito.when(firebaseFirestore.collection(Mockito.anyString()).document(Mockito.anyString()))
                .thenReturn(donationRef);
        Mockito.doNothing().when(donationRef.set(currentDonation).addOnSuccessListener(Mockito.any()));
        Assert.assertEquals(currentDonation.getCategory(), emptyString);
        Assert.assertEquals(currentDonation.getComments(), emptyString);
        Assert.assertEquals(currentDonation.getFullDescription(), emptyString);
        Assert.assertEquals(currentDonation.getShortDescription(), emptyString);
        Assert.assertEquals(currentDonation.getValueInUSD(), 0,0);


        EditDonationDetailActivity editDonationDetailActivity = mActivityRule.getActivity();
        editDonationDetailActivity.onClickSaveDonation(view);
    }
}
