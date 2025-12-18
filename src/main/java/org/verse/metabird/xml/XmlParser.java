package org.verse.metabird.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;

public final class XmlParser {

    private XmlParser() {
    }

    public static Document parse(String xml) {
        try {
            var factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            return factory.newDocumentBuilder()
                    .parse(new ByteArrayInputStream(xml.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String text(Element parent, String tag) {
        NodeList list = parent.getElementsByTagNameNS("*", tag);
        return list.getLength() == 0 ? null : list.item(0).getTextContent();
    }
}
