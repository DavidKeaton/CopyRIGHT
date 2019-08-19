package edu.vccs.email.dmk2303.CopyRIGHT.UI.Desktop;

import edu.vccs.email.dmk2303.CopyRIGHT.Page.Choice;
import edu.vccs.email.dmk2303.CopyRIGHT.UI.AbstractGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Implementation of abstracted UI for Windows desktop.
 *
 * @author David Keaton (dmk2303)
 * @author Michael Simonetti
 */
public class GUI extends AbstractGUI
{
	public GUI()
	{
		super();
	}

	// TODO: create the view for the text and a layout for the buttons
	protected Object createWindow()
	{
		// create the dialog box
		this.self = new JDialog();
		// windowLayout -> BorderLayout
		this.windowLayout = new BorderLayout();
		/*      *** TEXT AREA ***       */
		// textLayout -> BorderLayout
		this.textLayout = new BorderLayout();
		// questionText -> JTextArea
		questionText = new JTextArea();
		// JTextArea -> textLayout
		((BorderLayout)this.textLayout).addLayoutComponent((Component)questionText,
		                                                   BorderLayout.CENTER);
		// textLayout -> windowLayout
		((BorderLayout)this.windowLayout).addLayoutComponent((Component)this.textLayout,
		                                                     BorderLayout.NORTH);
		/*      *** BUTTONS AREA ***    */
		// buttonLayout -> JList<E>
		this.buttonLayout = new JList<JButton>();
		// allow only single selections
		((JList)this.buttonLayout).getSelectionModel()
		                          .setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// buttonLayout -> windowLayout
		((BorderLayout)this.windowLayout).addLayoutComponent((Component)this.buttonLayout,
		                                                     BorderLayout.SOUTH);
		/*      *** MAIN WINDOW ***     */
		// windowLayout -> window
		((JDialog)this.self).setLayout((LayoutManager)windowLayout);
		// pack the window
		((JDialog)this.self).pack();
		// make the window resizable
		((JDialog)this.self).setResizable(true);
		// enable the window
		((JDialog)this.self).setEnabled(true);
		// create new thread for UI
		SwingUtilities.invokeLater(()->{
			// make the window visible
			((JDialog)this.self).setVisible(true);
		});

		// return reference to self
		return this.self;
	}

	@Override
	protected void setText(String text)
	{
		((JTextArea)this.questionText).setText(text);
	}

	@Override
	protected void add(Object elem, Object o)
	{
		// add a button to the button layout
		if(elem instanceof JButton) {
			((JList)this.buttonLayout).add((Component)elem);
		} else {
			// push element to dialog as 'Component'.
			((JDialog)this.self).add((Component)elem);
		}
	}

	/**
	 * Adds the buttons to the current GUI.
	 *
	 * @param array informational array to be passed to button creation routines
	 */
	@Override
	protected void addButtons(Object[] array)
	{
		// do the dew
		super.addButtons(array);
		// now that the buttons are in the entries array...
		for(Object entry : this.entries) {
			// ...we can add them to the JList
			((JList)this.buttonLayout).add((Component)entry);
		}
	}

	/**
	 * Creates a button.
	 *
	 * @param o Object to be passed to the button creation routine
	 * @return a new button
	 */
	@Override
	protected Object createButton(Object o)
	{
		// make sure a choice was passed to us
		if(!(o instanceof Choice)) {
			return null;
		}
		// choice's text -> button's text
		String text = ((Choice)o).getText();
		// create button
		JButton button = new JButton(text);
		// finalize the button
		finalizeButton(button, o);
		// pass the button back
		return button;
	}

	/**
	 * Make sure the name and ActionListener are set up.
	 *
	 * @param elem  the JButton object (hopefully) to work on
	 * @param o     the object that
	 */
	protected void finalizeButton(Object elem, Object o) throws IllegalArgumentException
	{
		final String TAG = "finalizeButton(Object, Object):";
		if(elem == null || o == null) {
			throw new IllegalArgumentException(TAG + " (elem || o) == (null)");
		}
		// Question? Retrieve the weight
		if(o instanceof Choice) {
			// set the name to the weight
			((JButton)elem).setName((String)(((Choice)o).get()[1]));
		// String? Probably license information or closing statement
		} else if(o instanceof String) {
			// set the name to the licenseType
			((JButton)elem).setName((String)o);
		}
		// if a button is being passed, add a listener for clicks
		((JButton)elem).addActionListener(new Listener());
	}

	protected void next()
	{
		// load the next Page
	}

	/**
	 * Dedicated listener class used to extract pertinent information.
	 */
	private class Listener implements ActionListener
	{
		/**
		 * Invoked when an action occurs.
		 *
		 * @param e the event to be processed
		 */
		@Override
		public void actionPerformed(ActionEvent e)
		{
			// push choices made onto a stack, remove when processed
			GUI.choices.push((Choice)((Object)((JButton)e.getSource()).getName()));
		}
	}

	// TODO: update()
	protected void update()
	{
		((JDialog)this.self).validate();
	}
}
