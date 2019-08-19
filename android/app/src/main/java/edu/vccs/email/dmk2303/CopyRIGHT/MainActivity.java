package edu.vccs.email.dmk2303.CopyRIGHT;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

import edu.vccs.email.dmk2303.CopyRIGHT.Page.*;

public class MainActivity extends AppCompatActivity
{
	// TAG that the choices info will be stored under
	private final static int TAG_CHOICE = 1337;
	// *FINAL* APK name
	private final static String APK_NAME = "CopyRIGHT.apk";
	// put it in some-form-that-is-a-standard-I-can't-recall.
	private final static String XML_PATH = "file:/" + APK_NAME + "!/res/xml";

	// the layout the buttons will be added to
	private LinearLayout buttonLayout;
	// internal variables
	// stream from XML file, KEEP OPEN UNTIL YOU DESTROY THIS
	InputStream xmlStream;
	Form form = null;
	Page page = null;
	// once a choice is chosen, this is set to true
	boolean hasChosen = false;

	/**
	 * TODO:
	 * [Button]
	 * - padding [optional]
	 * - (?)getTag(key, object) [hold weights / redir(?)]
	 * <p>
	 * [LinearLayout]
	 * - add Button(s) to the layout
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// get resource handles
		buttonLayout = findViewById(R.id.buttonLayout);
		// open and parse XML file for the EUA
		this.form = loadForm("S");
		// set the main question view
		setContentView(R.layout.activity_main);
		// TODO: mind case redir=S2->kill screen, S3+ used for special cases
		do {
			// wait on a choice
			if(!hasChosen) {
				page = form.get(form.getPageAsIndex());
				// display the question for the given Page
				displayText(form.get(form.getPageAsIndex(form.getPageId())).getText());
				// add the buttons to the display
				addButtons();
				// so we don't skip our loop
				hasChosen = false;
			}
		} while(!form.isDone());
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		// release the xmlStream
		try {
			xmlStream.close();
		} catch(IOException e) {
			// guess we just log it
			Log.d("onDestroy()", e.toString());
		}
	}

	// TODO: after loading Form, set up all the buttons
	private void addButtons()
	{
		buttonLayout.getChildCount();
		// TODO: clear the old buttons from the layout
		// add the buttons and proper attributes to said button
		Choice[] choices = form.get().get();
		for(Choice choice : choices) {
			((LinearLayout)findViewById(R.id.buttonLayout)).addView(createButton(choice));
		}
	}

	// TODO: display the text in the question region
	private void displayText(final String msg)
	{
		((TextView)findViewById(R.id.textQuestion)).setText(msg);
	}

	/**
	 * Loads the given Form and returns its reference.
	 *
	 * @param form the form to load
	 * @return a reference to the form loaded
	 */
	private Form loadForm(String form)
	{
		// use Form.load() and pass stream reference from resources
		try {
			return Form.load(getClass().getResourceAsStream(XML_PATH + "/flow.xml", form));
		} catch(Exception e) {
			Log.d("onCreate()", e.toString());
		}
		return null;
	}

	/*
	    android:layout_width="match_parent"
	    android:layout_height="wrap_content"
	    android:layout_marginLeft="5dp"
	    android:layout_marginRight="5dp"
	    android:layout_marginTop="5dp"
	    android:layout_marginBottom="5dp"
	    android:textAllCaps="false"
	    android:textSize="@dimen/text_size"
    */
	// TODO: create a button that stores information about the given choice
	private Button createButton(Choice choice)
	{
		// create new button in current instance of app
		Button b = new Button(this.getApplicationContext());
		// save our Choice argument for use in callback
		b.setTag(TAG_CHOICE, choice);
		// words to show
		b.setText(choice.getText());
		// add the button callback
		b.setOnClickListener((e) -> {
			((Choice)b.getTag(TAG_CHOICE)).choose();
			hasChosen = true;
		});
		// get text size from resource file
		b.setTextSize(getResources().getDimension(R.dimen.text_size));
		// TODO: set margins
		// set ALLCAPS TO FALSE LIKE GOD INTENDED
		b.setAllCaps(false);
		// TODO: set the width to the size of the layout
		//b.setWidth(0);
		return b;
	}
}
