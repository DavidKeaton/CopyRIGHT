package edu.vccs.email.dmk2303.CopyRIGHT.UI;

import edu.vccs.email.dmk2303.CopyRIGHT.Page.Choice;
import java.util.ArrayList;
import java.util.Stack;

/**
 * Abstraction of the key methods a class needs to fill out to run a UI based on Form.
 *      All one needs to do is detail the abstracted methods to
 *      cater to your platform, and the rest is taken care of.
 *
 * @author David Keaton (dmk2303)
 * @author Michael Simonetti
 */
public abstract class AbstractGUI
{
	// window handle
	protected Object self;
	// the main window layout
	protected Object windowLayout;
	// the question text layout
	protected Object textLayout;
	// the button(s) list layout
	protected Object buttonLayout;
	// handle to the area to write the question text to
	protected Object questionText;
	// handles to the buttons or "entries"
	protected ArrayList<Object> entries;
	// holds the choices made by the user
	protected static Stack<Choice> choices = new Stack<>();


	/**
	 * Creates a default window for the given platform.
	 */
	public AbstractGUI()
	{
		self = createWindow();
		entries = new ArrayList<>();
	}

	public void run()
	{
		this.update();
	}

	/**         [[WINDOW CREATION]]             **/
	/**
	 * Creates the window to display the forms.
	 *
	 * @return      the window object
	 */
	abstract protected Object createWindow();

	//abstract protected Object createLayout(int area);


	/**         [[ELEMENT CREATION]]            **/
	/**
	 * Creates a button.
	 *
	 * @param o     Object to be passed to the button creation routine
	 * @return      a new button
	 */
	abstract protected Object createButton(Object o);

	private void clearButtons()
	{
		this.entries.clear();
	}

	/**
	 * Adds the buttons to the current GUI.
	 *
	 * @param array     informational array to be passed to button creation routines
	 */
	protected void addButtons(Object[] array)
	{
		// if the array has entries already, remove them
		clearButtons();
		// for each button given, create/finalize/add
		for(int i = 0; i < array.length; ++i) {
			// create the button
			entries.add(createButton(array[i]));
			// finalize attributes of said button
			finalizeButton(entries.get(i), array[i]);
			// add button to GUI
			add(entries.get(i), array[i]);
		}
	}

	/**         [[ELEMENT HANDLING]]            **/
	/**
	 * Adds an element to the window.
	 *
	 * @param elem      element to add as an entry
	 * @param o         an object that is linked to the element
	 */
	abstract protected void add(Object elem, Object o);

	/**
	 * Sets the question text area's text.
	 *
	 * @param text      the string to display
	 */
	abstract protected void setText(String text);

	/**
	 * Updates the contents of the window.
	 */
	abstract protected void update();

	/**
	 * Performs small additions to the button to make it al-dente.
	 *
	 * @param elem      the button to work magic on
	 * @param o         an object that is linked to the element
	 */
	abstract protected void finalizeButton(Object elem, Object o);

	/**         [[FORM HANDLING]]           **/
	/**
	 * Displays the next page of the chapter.
	 */
	abstract protected void next();

	/**
	 * Calculate the total of all the weights selected.
	 *
	 * @return  the total weight accrued, > 100% -> = 100%
	 */
	protected double calculateTotal()
	{
		double d = 0.0;
		for(Choice choice : choices) {
			d += choice.getWeight();
		}
		return (d > 100.0) ? 100.0 : d;
	}

	protected void choose(Choice choice)
	{
		// add the choice to the top of the stack
		choices.push(choice);
		// move to the next Page
		next();
	}
}
