package edu.vccs.email.dmk2303.CopyRIGHT.Page;

/**
 * A choice one can make on a Page.
 *
 * @author David Keaton (dmk2303)
 * @author Michael Simonetti
 */
public class Choice
{
	// holds the page this belongs to
	private Page parent = null;
	// text to display regarding this choice
	private String text;
	// the weight of the text
	private double weight = 0.0;
	// does this redirect to another page?
	private String redirect = null;
	// does this have a license or end-note to show?
	// NOTE: this will stop the weights from being counted, if it is chosen!
	private String licenseType = null;

	Choice(String s)
	{
		this.text = s;
	}

	Choice(String s, double w)
	{
		this.text = s;
		this.weight   = w;
	}

	Choice(String s, double w, String r)
	{
		this.text = s;
		this.weight   = w;
		this.redirect = r;
	}

	Choice(String s, String l)
	{
		this.text = s;
		this.licenseType = l;
	}

	Choice(String s, double w, String l, String r)
	{
		this.text = s;
		this.weight = w;
		this.licenseType = l;
		this.redirect = r;
	}

	// xxx: mark for demonlition
	public void choose()
	{
	    // tell the man-in-charge that a choice HAS BEEN CHOSEN!
        this.parent.getParent().tattle(this);
		this.parent.getParent().next();
	}

	protected Choice clone()
	{
	    return this.clone();
	}

	/**
	 * Returns the variables of this class as an Object array.
	 *
	 * @return      class vars as {String, double}
	 */
	public Object[] get()
	{
		return (redirect == null)
				? new Object[]{this.text, this.weight}
				: new Object[]{this.text, this.weight, this.redirect};
	}

	public String getRedirect()
	{
		return redirect;
	}

	public double getWeight()
	{
		return weight;
	}

	public String getText()
	{
		return text;
	}

	public String getLicenseType()
	{
		return licenseType;
	}

	/**
	 * Sets the variables of this class.
	 *
	 * @param text      the text to be shown as a string
	 * @param weight        the weight of the text
	 */
	public void set(String text, double weight)
	{
		this.text = text;
		this.weight   = weight;
	}

	protected void setParent(Page p)
	{
		this.parent = p;
	}

}
