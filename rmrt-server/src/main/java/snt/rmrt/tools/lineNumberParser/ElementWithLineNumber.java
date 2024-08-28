package snt.rmrt.tools.lineNumberParser;

import org.dom4j.Namespace;
import org.dom4j.QName;
import org.dom4j.tree.DefaultElement;

public class ElementWithLineNumber extends DefaultElement {

    private int lineNum = 0, colNum = 0;

    public ElementWithLineNumber(QName qname) {
        super(qname);
        // TODO Auto-generated constructor stub
    }

    public ElementWithLineNumber(QName qname, int attrCount) {
        super(qname, attrCount);
    }

    public ElementWithLineNumber(String name) {
        super(name);
    }

    public ElementWithLineNumber(String name, Namespace namespace) {
        super(name, namespace);
    }

    public int getColumnNumber() {
        return this.colNum;
    }

    public int getLineNumber() {
        return this.lineNum;
    }

    public void setLocation(int lineNum, int colNum) {
        this.lineNum = lineNum;
        this.colNum = colNum;
    }
}
