package edu.vccs.email.dmk2303.CopyRIGHT.Page;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A Page holds a list of choices. The choices are held internally in
 *      the ArrayList.
 *
 * @author David Keaton (dmk2303)
 */
public class Page extends ArrayList<Choice>
{
	// holds the Form this belongs to
    private Form parent = null;
	// ID of the given page
	private String id;
	// text to display above the choices
	private String text;

	public String getText()
	{
		return text;
	}

	public void setText(String text)
	{
		this.text = text;
	}

	/**
	 * A Page in need of words.
	 * @param id    the page's id
	 */
	public Page(String id)
	{
		this.id = id;
		this.clear();
	}

	public Page(String id, String text, String[] choices, double[] weights)
	{
		// setup the numeric id, if valid...
		this.id = (Integer.parseInt(id.substring(1)) < 0) ? "[null]" : id;
		this.text = text;
		// setup the choices
		this.clear();
		// add to the internal list
		addAll(choices, weights);
	}

	/**
	 * Initialize choices for the form with the given choices.
	 *
	 * @param id            the Page ID
	 * @param choices       choices one can make
	 */
	public Page(String id, String text, Choice[] choices)
	{
		this.id = id;
		this.text = text;
		addAll(choices);
	}

	public void addAll(Choice[] choices)
	{
		for(Choice choice : choices) {
			choice.setParent(this);
		}
		addAll(Arrays.asList(choices));
	}

	public void addAll(String[] choices, double[] weights)
	{
		// get the larger of the two sizes, to be safe
		int size = (choices.length > weights.length)
		          ? choices.length : weights.length;
		Choice[] q = new Choice[size];

		// create array of choices from the given choices & weights
		for(int i = 0; i < size; ++i) {
			q[i] = new Choice(choices[i], weights[i]);
			q[i].setParent(this);
		}
		// let the class handle the rest
		addAll(q);
	}

	/**
	 * Returns the id of the given form.
	 *
	 * @return      the id of the form
	 */
	public String getId()
	{
		return this.id;
	}

	/**
	 * Add the question formed from the arguments to the end of the array.
	 *
	 * @param question
	 * @param weight
	 */
	public void add(String question, double weight)
	{
		this.add(new Choice(question, weight));
	}

	/**
	 * Returns the current question.
	 *
	 * @return          the current question
	 */
	public Choice[] get()
	{
		return (Choice[])this.toArray();
	}

	/**
	 * Get the parent Form this page belongs to.
	 *
	 * @return		a reference to the Form
	 */
	protected Form getParent()
	{
		return this.parent;
	}

	/**
	 * Sets the parent of this Page.
	 *
	 * @param f		a reference to the Form this Page belongs to
	 */
	protected void setParent(Form f)
	{
		this.parent = f;
	}

	/**
	 * Returns the next Page to be shown.
	 *
	 * @return		the next Page's ID
	 */
	protected String getNext()
	{
		return "" + id.charAt(0) + (Integer.parseInt("" + id.charAt(1)) + 1);
	}

	/**
	 * Sets the value @ `index' to the given arguments.
	 *
	 * @param index         index to overwrite
	 * @param question      String describing the question
	 * @param weight        double detailing the question weights
	 */
	protected void set(int index, String question, double weight)
	{
		// make sure we are staying within array boundaries
		if(index > this.size() || index < 0) {
			// if there is no more room, at to the end..
			add(question, weight);
		// does not exceed `forms' array size
		} else {
			set(index, new Choice(question, weight));
		}
	}

	/**
	 * Set internal choices array to the given choices & weights.
	 * Sets the weight of a question with NO weight, to 0%.
	 *
	 * @param index         starting index to begin from
	 * @param choices     choices as an array of strings
	 * @param weights       weights as an array of doubles
	 */
	protected void set(int index, String[] choices, double[] weights)
	{
		// short circuit check
		if(index > this.size() || index < 0) {
			addAll(choices, weights);
		} else {
			// begin overwriting from `index'
			for(int i = index; (i - index) <= choices.length; ++i) {
				// make sure there is a valid weight - if it exceeds the weight array size, put 0%
				set(i, choices[i], ((i - index) > weights.length) ? 0.0 : weights[i]);
			}
		}
	}

	public Page clone()
	{
		// create a copy of `this'
		Page p = (Page)super.clone();
		// TODO: any touch ups here?
		return p;
	}
}