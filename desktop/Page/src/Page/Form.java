package edu.vccs.email.dmk2303.CopyRIGHT.Page;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * A Form is a collection of Pages. The class itself extends off of a
 *      linked list, and holds the Pages internally. The Form can be
 *      initialized one of two (general) ways. You can build it from scratch,
 *      or load the entire thing from an XML file (see flow.xml for details).
 *
 * @author David Keaton (dmk2303)
 * @author Michael Simonetti
 */
public class Form extends LinkedList<Page> {
	/*
	enum {
	};
	*/
	// special Page IDs
	public final String EUA_PAGE = "EUA";
	public final String START_PAGE = "S0";
	public final String SUM_PAGE = "S1";
	public final String KILL_SCREEN = "S2";

	// letter part of page ids'
	private String id = null;
	// DOM model for a given xml file
	private Document dom = null;
	// root level node list/element
	private Object form = null;
	// cached Page object
	private Page cachedPage = null;
	// holds the stream/file used to open the form
	private static Object cachedStream = null;
	// current Page
	private String currentPage = null;
	// current running sum of weights
	private double totalWeight = 0.0;
	// indicates whether weights are still counted, like if a license is needed
	private boolean done = false, stopCounting = false;
	private String licenseType = null;
	// THE ONE CHOSEN, HE WHO TATTLES
	private Choice chosenOne = null;
	// error vars
	private boolean hasError = false;
	private String errorMessage = null;

	/**
	 * A chapter in need of words.
	 */
	public Form()
	{
		this.clear();
	}

	/**
	 * Constructs a chapter with an array of the given pages.
	 *
	 * @param pages pages to insert into this chapter
	 */
	public Form(Page[] pages)
	{
		this.clear();
		this.addAll(pages);
		this.currentPage = pages[0].getId();
		this.id = strip(currentPage);
	}

	/**
	 * Opens @ref f (either InputStream or File) as an XML file.
	 * Any unhandled exceptions get rethrown back into the mix as an Exception.
	 *
	 * @param o    the XML to parse either as a @ref File or a @ref InputStream
	 * @param form the specific Form to get, or null if all Forms needed
	 * @throws Exception anytime parsing throws an error, it gets shoved here
	 */
	// TODO: possible memory leaks? (from...? I forgot...)
	public Form(Object o, String form) throws Exception
	{
		// debug string
		final String TAG = "Form(Object, String):";

		// short circuit fail test
		if(o == null || form == null) {
			// error occurred
			hasError = true;
			// build the error string
			 errorMessage = "o = " + ((o == null) ? "(null)" : o)
			                + ", form = " + ((form == null) ? "(null)" : form);
			throw new Exception("Form(Object, String): (" + errorMessage + ")");
		}
		// create the Form
		try {
			// DOM classes
			DocumentBuilderFactory factory = null;
			DocumentBuilder builder = null;
			// are we using a cache?
			if(cachedStream == null) {
				// cache the stream used to open the Form
				// bug: pretty sure if we cloned this, we would have a memory leak later
				Form.cachedStream = o;
			}
			// set the ID of this form
			this.id = strip(form);
			// generate new factory object from a blank instance
			factory = DocumentBuilderFactory.newInstance();
			// now, obtain a DocumentBuilder from said factory...
			builder = factory.newDocumentBuilder();
			// now, extract the Document model from the given input...
			if (cachedStream instanceof File) {                    // File
				dom = builder.parse((File)cachedStream);
			} else if (cachedStream instanceof InputStream) {    // InputStream
				dom = builder.parse((InputStream)cachedStream);
			} else {                                // ERROR
				// neither File nor InputStream - for shame
				throw new IOException(TAG + " bad arg(1): " + cachedStream);
			}
			parse(form);
		// error encountered during processing
		} catch (NullPointerException
				| ParserConfigurationException
				| SAXException
				| IOException e) {
			// pass the error back up as a regular ol' Exception
			throw new Exception(e);
		}
	}

	/**
	 * Add all the objects, in order of array, to the end of the internal list.
	 *
	 * @param pages the things to be added to the end of the list
	 */
	public void addAll(Page[] pages)
	{
		for (Page page : pages) {
			page.setParent(this);
			this.addLast(page);
		}
	}

	public boolean isDone()
	{
		return done;
	}

	public boolean hasError()
	{
		return this.hasError;
	}

	public String getError()
	{
		return this.errorMessage;
	}

	private void setError(String s)
	{
		this.hasError = true;
		this.errorMessage = s;
	}

	/**
	 * Static method to load a form into memory and cache it.
	 *
	 * @param id            the ID of the Form to get
	 * @return              a reference to the new Form
	 * @throws Exception    catch-all for any Exception that could occur in loading
	 */
	public static Form load(String id) throws Exception
	{
		final String TAG = "Form load(String):";
		// no form given, bail
		assert(id != null);
		// attempt to load the given form, first from cache
		if(cachedStream != null) {
			// load the Form and return it
			return new Form(cachedStream, strip(id));
		}
		return null;
	}

	// xxx: mark for deletion
	/**
	 * Tells the Form what information the choice had stored.
	 *
	 * @param child the child who tattled
	 */
	protected void tattle(Choice child)
	{
	    // go the safe route, don't want the reference dying later
		this.chosenOne = child.clone();
	}

	public boolean hasNext()
	{
		// has the end been reached?
		return !(currentPage == null || yoink(currentPage) > this.size());
	}

	public double getTotal()
	{
		return (totalWeight > 100.0) ? 100.0 : totalWeight;
	}

	/**
	 *
	 * @return		current page the form is on
	 */
	public Page get()
	{
		Page page = null;
		// is there a cached Page present? release & return it
		if(cachedPage != null) {
		    page = cachedPage;
		    cachedPage = null;
		} else {
			// safe copy
			page = cachedPage = (Page)this.get(getPageAsIndex()).clone();
		}
	    return page;
	}

	public Page get(String id)
	{
		return this.get(getPageAsIndex(id));
	}

	// return current page's id
	public String getPageId()
	{
		return currentPage;
	}

	public String getNextPage()
	{
		if(isDone()) {
			return KILL_SCREEN;
		}
	    // display info from CHOSEN ONE
        if(chosenOne != null) {
			return chosenOne.getRedirect();
		}
	    // if there is another page, flip to it
		if(hasNext()) {
		    // + 2 accounts for 1->0 shift and increments
			return strip(currentPage) + (getPageAsIndex() + 2);
		}
		// determination screen
		return "S1";
	}

	/**
	 * Strips off the rest of the ID and leaves the categorical letter.
	 *
	 * @param id        the Page ID to strip trailing
	 * @return          the first character of the ID (category) as a String
	 */
	protected static String strip(String id)
	{
		return "" + id.charAt(0);
	}

	// yoinks just the numbers after the category part
	protected static int yoink(String id)
	{
		try {
			return Integer.parseInt(id.substring(1));
		} catch(NumberFormatException e) {
			return -1;
		}
	}

	private int getPageAsIndex(String id)
	{
		// no argument passed? return this Page as an index
	    if(id == null) {
	    	return getPageAsIndex();
		}
		// yoink off the numerical part of ID, account for 1->0
		//int index = ((Integer)yoink(id)).toString().equals("") ? yoink(id) - 1 : ;
		int index = (id.equals(START_PAGE)) ? 0 : yoink(id) - 1;
		// account for (index - 1) in "S"
		return (id.equals("S")) ? ++index : index;
	}

	private int getPageAsIndex()
	{
		return getPageAsIndex(currentPage);
	}

	// xxx: fix so it's not so spaghetti
	protected void next()
	{
		// no need to go to the next Page if we are done!
		if(isDone()) {
			return;
		}
		try {
			// is there a redirect to handle?
			String redirect = chosenOne.getRedirect();
			// no redirect found
			if(redirect == null) {
				// get current page number, make sure we can continue
				int index = getPageAsIndex();
				// end of form?
				if(index >= this.size()) {
					// need to load another Form, cuz this won is don
					done = true;
					// still can keep going
				} else {
					// get next page, store index,
					cachedPage = this.get(++index).clone();
					currentPage = id + index;
				}
				// need to redirect
			} else {
				// handle special cases
				if(redirect.charAt(0) == 'S') {
					// kill screens and license screens ahead
					done = (yoink(redirect) >= 1);
				}
				currentPage = redirect;
				// within the same form
				if(this.id.equals(strip(redirect))) {
					cachedPage = this.get(getPageAsIndex(redirect)).clone();
					// to a different form
				} else {
					// load the other form, and continue to process
					parse(strip(redirect));
					// cache the page
					cachedPage = this.get(getPageAsIndex(redirect)).clone();
				}
			}
			// no kill reasons...yet...
			if(!stopCounting) {
				// add in the weight
				totalWeight += chosenOne.getWeight();
				// does it have a license attached?
				licenseType = chosenOne.getRedirect();
				// if so, stop counting
				stopCounting = (licenseType != null);
			}
			// RELEASE THE CHOSEN ONE
			chosenOne = null;
		// an error occurred during parsing
		} catch(NumberFormatException e) {  // NumberFormatException
		} catch(NullPointerException e) {   // NullPointerException
		} catch(Exception e) {              // Exception
		}
	}

	/**
	 * Parse the Form of the given @ref name.
	 *
	 * @param id		    name of the Form to parse
	 * @throws Exception    an error occurred during parsing
	 */
	private void parse(String id) throws Exception
	{
		// debug TAG
		final String TAG = "parse(id):";
		// internal vars
		NodeList nodes;
		Node current, node;
		NamedNodeMap attr;
		String text, redir, licenseType;
		Choice[] choices;
		double weight;

		// short-circuit on invalid argument
		if(id == null) {
			throw new IllegalArgumentException(TAG + " id == (null)");
		}
        form = dom.getElementById(id);
        // grab the text from the node
        nodes = ((Element) form).getElementsByTagName("Text");
		// get the first (and hopefully only) node
		current = nodes.item(0);
		// get the Page's text
		text = (nodes.getLength() <= 0) ? "(null)" : current.getFirstChild().getTextContent();
		// get the choices
		nodes = ((Element) form).getElementsByTagName("Choice");
		choices = new Choice[nodes.getLength()];
		// create each of the choices
		for(int i = 0; i < choices.length; ++i) {
			// set these on each iteration
			weight = 0.0;
			redir = null;
			licenseType = null;
			// get i-th node
			current = nodes.item(i);
			// grab the attributes from the node
			if(current.hasAttributes()) {
				attr = current.getAttributes();
				// get the weight
				node = attr.getNamedItem("weight");
				if(node != null) {
					try {
						weight = Double.parseDouble(node.getNodeValue());
					// error parsing double, set weight to 0
					} catch(NumberFormatException e) {
						// just in case weight was borked
						weight = 0.0;
					}
				}
				// get redirect (if exists)
				node = attr.getNamedItem("redir");
				if(node != null) {
					redir = node.getNodeValue();
				}
				// get licenseType, if present
				node = attr.getNamedItem("perms");
				if(node != null) {
					licenseType = node.getNodeValue();
				}
			}
			// create the choice
			choices[i] = new Choice(current.getNodeValue(),
					weight, licenseType, redir);
		}
		// build and add the page
		this.addLast(new Page(((Element)form).getAttribute("id"), text, choices));
	}

	private void parse() throws Exception
	{
        // grab all the Form elements
        form = dom.getElementsByTagName("Form");
        // send each Form grabbed to parse(id)
        for (int i = 0; i < ((NodeList)form).getLength(); ++i) {
            Node n = ((NodeList)form).item(i);
            // pipe through the ID of the Form
	        parse(n.getAttributes().getNamedItem("id").getNodeValue());
		}
	}

	public Form clone()
	{
		// let LinkedList<>.clone() take care of the legwork
		Form f = (Form)super.clone();
		// make sure we clone the cachedPage, in case the reference dies
		f.cachedPage = this.cachedPage.clone();
		// TODO: clone(dom, form)
		// set specific vars
		f.dom = this.dom;
		f.id = this.id;
		f.currentPage = this.currentPage;
		f.totalWeight = this.totalWeight;
		f.done = this.done;
		f.stopCounting = this.stopCounting;
		// return newly created copy
		return f;
	}
}
