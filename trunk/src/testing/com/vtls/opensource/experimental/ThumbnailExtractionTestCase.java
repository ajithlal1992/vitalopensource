/*
Core SWING Advanced Programming 
By Kim Topley
ISBN: 0 13 083292 8       
Publisher: Prentice Hall  
*/

import java.awt.Color;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.StringTokenizer;

import java.awt.event.MouseEvent;
import java.awt.Point;

import java.io.Serializable;

import java.net.MalformedURLException;

import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.ChangedCharSetException;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.html.HTML;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.MouseInputAdapter;

public class ThumbnailExtractionTestCase extends JFrame {
  public ThumbnailExtractionTestCase() {
    super("JEditorPane Example 9");
    pane = new JEditorPane();
    pane.setEditable(false); // Read-only
    getContentPane().add(new JScrollPane(pane), "Center");

    // Build the panel of controls
    JPanel panel = new JPanel();

    panel.setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    c.gridwidth = 1;
    c.gridheight = 1;
    c.anchor = GridBagConstraints.EAST;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0.0;
    c.weighty = 0.0;

    JLabel urlLabel = new JLabel("URL: ", SwingConstants.RIGHT);
    panel.add(urlLabel, c);
    JLabel loadingLabel = new JLabel("State: ", SwingConstants.RIGHT);
    c.gridy = 1;
    panel.add(loadingLabel, c);
    JLabel typeLabel = new JLabel("Type: ", SwingConstants.RIGHT);
    c.gridy = 2;
    panel.add(typeLabel, c);
    c.gridy = 3;
    panel.add(new JLabel(LOAD_TIME), c);

    c.gridy = 4;
    c.gridwidth = 2;
    c.weightx = 1.0;
    c.anchor = GridBagConstraints.WEST;
    onlineLoad = new JCheckBox("Online Load");
    panel.add(onlineLoad, c);
    onlineLoad.setSelected(true);
    onlineLoad.setForeground(typeLabel.getForeground());

    c.gridx = 1;
    c.gridy = 0;
    c.anchor = GridBagConstraints.EAST;
    c.fill = GridBagConstraints.HORIZONTAL;

    textField = new JTextField(32);
    panel.add(textField, c);
    loadingState = new JLabel(spaces, SwingConstants.LEFT);
    loadingState.setForeground(Color.black);
    c.gridy = 1;
    panel.add(loadingState, c);
    loadedType = new JLabel(spaces, SwingConstants.LEFT);
    loadedType.setForeground(Color.black);
    c.gridy = 2;
    panel.add(loadedType, c);
    timeLabel = new JLabel("");
    c.gridy = 3;
    panel.add(timeLabel, c);

    getContentPane().add(panel, "South");

    // Change page based on text field
    textField.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        String url = textField.getText();

        try {
          // Check if the new page and the old
          // page are the same.
          URL newURL = new URL(url);
          URL loadedURL = pane.getPage();
          if (loadedURL != null && loadedURL.sameFile(newURL)) {
            return;
          }

          // Try to display the page
          textField.setEnabled(false); // Disable input
          textField.paintImmediately(0, 0, textField.getSize().width,
              textField.getSize().height);
          setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
          // Busy cursor
          loadingState.setText("Loading...");
          loadingState.paintImmediately(0, 0,
              loadingState.getSize().width, loadingState
                  .getSize().height);
          loadedType.setText("");
          loadedType.paintImmediately(0, 0,
              loadedType.getSize().width,
              loadedType.getSize().height);

          timeLabel.setText("");
          timeLabel.paintImmediately(0, 0, timeLabel.getSize().width,
              timeLabel.getSize().height);

          startTime = System.currentTimeMillis();

          // Choose the loading method
          if (onlineLoad.isSelected()) {
            // Usual load via setPage
            pane.setPage(url);
            loadedType.setText(pane.getContentType());
          } else {
            pane.setContentType("text/html");
            loadedType.setText(pane.getContentType());
            if (loader == null) {
              loader = new HTMLDocumentLoader();
            }
            HTMLDocument doc = loader.loadDocument(new URL(url));
            loadComplete();
            pane.setDocument(doc);
            displayLoadTime();
          }
        } catch (Exception e) {
          System.out.println(e);
          JOptionPane.showMessageDialog(pane, new String[] {
              "Unable to open file", url }, "File Open Error",
              JOptionPane.ERROR_MESSAGE);
          loadingState.setText("Failed");
          textField.setEnabled(true);
          setCursor(Cursor.getDefaultCursor());
        }
      }
    });

    // Listen for page load to complete
    pane.addPropertyChangeListener(new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("page")) {
          loadComplete();
          displayLoadTime();
        }
      }
    });
  }

  public void loadComplete() {
    loadingState.setText("Page loaded.");
    textField.setEnabled(true); // Allow entry of new URL
    setCursor(Cursor.getDefaultCursor());
  }

  public void displayLoadTime() {
    double loadingTime = ((System.currentTimeMillis() - startTime)) / 1000d;
    timeLabel.setText(loadingTime + " seconds");
  }

  public static void main(String[] args) {
    try {
        UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
    } catch (Exception evt) {}
    JFrame f = new ThumbnailExtractionTestCase();
    f.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent evt) {
        System.exit(0);
      }
    });
    f.setSize(500, 400);
    f.setVisible(true);
  }

  static final String spaces = "                    ";

  static final String LOAD_TIME = "Load time: ";

  private JCheckBox onlineLoad;

  private HTMLDocumentLoader loader;

  private JLabel loadingState;

  private JLabel timeLabel;

  private JLabel loadedType;

  private JTextField textField;

  private JEditorPane pane;

  private long startTime;
}

class HTMLDocumentLoader {
  public HTMLDocument loadDocument(HTMLDocument doc, URL url, String charSet)
      throws IOException {
    doc.putProperty(Document.StreamDescriptionProperty, url);

    /*
     * This loop allows the document read to be retried if the character
     * encoding changes during processing.
     */
    InputStream in = null;
    boolean ignoreCharSet = false;

    for (;;) {
      try {
        // Remove any document content
        doc.remove(0, doc.getLength());

        URLConnection urlc = url.openConnection();
        in = urlc.getInputStream();
        Reader reader = (charSet == null) ? new InputStreamReader(in)
            : new InputStreamReader(in, charSet);

        HTMLEditorKit.Parser parser = getParser();
        HTMLEditorKit.ParserCallback htmlReader = getParserCallback(doc);
        parser.parse(reader, htmlReader, ignoreCharSet);
        htmlReader.flush();

        // All done
        break;
      } catch (BadLocationException ex) {
        // Should not happen - throw an IOException
        throw new IOException(ex.getMessage());
      } catch (ChangedCharSetException e) {
        // The character set has changed - restart
        charSet = getNewCharSet(e);

        // Prevent recursion by suppressing further exceptions
        ignoreCharSet = true;

        // Close original input stream
        in.close();

        // Continue the loop to read with the correct encoding
      }
    }

    return doc;
  }

  public HTMLDocument loadDocument(URL url, String charSet)
      throws IOException {
    return loadDocument((HTMLDocument) kit.createDefaultDocument(), url,
        charSet);
  }

  public HTMLDocument loadDocument(URL url) throws IOException {
    return loadDocument(url, null);
  }

  // Methods that allow customization of the parser and the callback
  public synchronized HTMLEditorKit.Parser getParser() {
    if (parser == null) {
      try {
        Class c = Class
            .forName("javax.swing.text.html.parser.ParserDelegator");
        parser = (HTMLEditorKit.Parser) c.newInstance();
      } catch (Throwable e) {
      }
    }
    return parser;
  }

  public synchronized HTMLEditorKit.ParserCallback getParserCallback(
      HTMLDocument doc) {
    return doc.getReader(0);
  }

  protected String getNewCharSet(ChangedCharSetException e) {
    String spec = e.getCharSetSpec();
    if (e.keyEqualsCharSet()) {
      // The event contains the new CharSet
      return spec;
    }

    // The event contains the content type
    // plus ";" plus qualifiers which may
    // contain a "charset" directive. First
    // remove the content type.
    int index = spec.indexOf(";");
    if (index != -1) {
      spec = spec.substring(index + 1);
    }

    // Force the string to lower case
    spec = spec.toLowerCase();

    StringTokenizer st = new StringTokenizer(spec, " \t=", true);
    boolean foundCharSet = false;
    boolean foundEquals = false;
    while (st.hasMoreTokens()) {
      String token = st.nextToken();
      if (token.equals(" ") || token.equals("\t")) {
        continue;
      }
      if (foundCharSet == false && foundEquals == false
          && token.equals("charset")) {
        foundCharSet = true;
        continue;
      } else if (foundEquals == false && token.equals("=")) {
        foundEquals = true;
        continue;
      } else if (foundEquals == true && foundCharSet == true) {
        return token;
      }

      // Not recognized
      foundCharSet = false;
      foundEquals = false;
    }

    // No charset found - return a guess
    return "8859_1";
  }

  protected static HTMLEditorKit kit;

  protected static HTMLEditorKit.Parser parser;

  static {
    kit = new HTMLEditorKit();
  }
  

/*
public class MyHTMLEditorKit extends HTMLEditorKit {

    public static final int JUMP = 0;
    public static final int MOVE = 1;

    LinkController myController = new LinkController();

    public void install(JEditorPane c) {
	c.addMouseListener(myController);
	c.addMouseMotionListener(myController);
    }

    public static class LinkController extends MouseInputAdapter 
	implements Serializable 
    {

	URL currentUrl = null;

	public void mouseClicked(MouseEvent e) {
	    JEditorPane editor = (JEditorPane) e.getSource();

	    if (! editor.isEditable()) {
		Point pt = new Point(e.getX(), e.getY());
		try {
		    int pos = editor.viewToModel(pt);
		    if (pos >= 0) {
			activateLink(pos, editor, JUMP);
		    }
		} catch (IllegalArgumentException iae) {}
	    }
	}

	public void mouseMoved(MouseEvent e) {
	    JEditorPane editor = (JEditorPane) e.getSource();

	    if (! editor.isEditable()) {
		Point pt = new Point(e.getX(), e.getY());
		try {
		    int pos = editor.viewToModel(pt);
		    if (pos >= 0) {
			activateLink(pos, editor, MOVE);
		    }
		} catch (IllegalArgumentException iae) {}
	    }
	}

	protected void activateLink(int pos, JEditorPane html, int type) {
	    Document doc = html.getDocument();
	    if (doc instanceof HTMLDocument) {
		HTMLDocument hdoc = (HTMLDocument) doc;
		Element e = hdoc.getCharacterElement(pos);
		AttributeSet a = e.getAttributes();
		AttributeSet anchor = 
		    (AttributeSet) a.getAttribute(HTML.Tag.A);
		String href = (anchor != null) ? 
		    (String) anchor.getAttribute(HTML.Attribute.HREF) : null;
		boolean shouldExit = false;

		HyperlinkEvent linkEvent = null;
		if (href != null) {
		    URL u;
		    try {
			u = new URL(hdoc.getBase(), href);
		    } catch (MalformedURLException m) {
			u = null;
		    }

		    if ((type == MOVE) && (!u.equals(currentUrl))) {
			linkEvent =  new HyperlinkEvent(html, 
					HyperlinkEvent.EventType.ENTERED, 
							u, href);
			currentUrl = u;
		    }
		    else if (type == JUMP) {
			linkEvent = new HyperlinkEvent(html, 
				       HyperlinkEvent.EventType.ACTIVATED, 
						       u, href);
			shouldExit = true;
		    }
		    else {
			return;
		    }
		    html.fireHyperlinkUpdate(linkEvent);
		}
		else if (currentUrl != null) {
		    shouldExit = true;
		}
		if (shouldExit) {
		    linkEvent = new HyperlinkEvent(html,
					   HyperlinkEvent.EventType.EXITED,
						   currentUrl, null);
		    html.fireHyperlinkUpdate(linkEvent);
		    currentUrl = null;
		}
	    }
	}
    }
}
*/  
}
