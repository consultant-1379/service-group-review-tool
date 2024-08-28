package snt.rmrt.tools.lineNumberParser;

import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.QName;
import org.xml.sax.Locator;

public class DocumentFactoryWithLocator extends DocumentFactory {

    private Locator locator;

    public DocumentFactoryWithLocator(Locator locator) {
        super();
        this.locator = locator;
    }

    @Override
    public Element createElement(QName qname) {
        ElementWithLineNumber element = new ElementWithLineNumber(qname);
        element.setLocation(this.locator.getLineNumber(), this.locator.getColumnNumber());
        return element;
    }

    @Override
    public Element createElement(String name) {
        ElementWithLineNumber element = new ElementWithLineNumber(name);
        element.setLocation(this.locator.getLineNumber(), this.locator.getColumnNumber());
        return element;
    }

    public void setLocator(Locator locator) {
        this.locator = locator;
    }
}
