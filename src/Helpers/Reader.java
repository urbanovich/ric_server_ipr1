/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Helpers;

import Entities.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;

/**
 *
 * @author dzmitry
 */
public class Reader {

    private org.w3c.dom.Document doc;
    
    protected List<Document> list = new ArrayList<>();

    public Reader() {
        try {

            URL xml = getClass().getClassLoader().getResource("resources/documents.xml");
            URL xsd = getClass().getClassLoader().getResource("resources/documents.xsd");

            if (Reader.validateXMLSchema(xsd.getPath(), xml.getPath())) {
                File file = new File(xml.getPath());

                DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance()
                        .newDocumentBuilder();

                this.doc = dBuilder.parse(file);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    /**
     * 
     */
    public void read() {
        NodeList documents = this.doc.getElementsByTagName("document");

        for (int count = 0; count < documents.getLength(); count++) {

            Node document = documents.item(count);

            // make sure it's element node.
            if (document.getNodeType() == Node.ELEMENT_NODE) {

                Element eElement = (Element) document;

                String id = eElement.getElementsByTagName("id").item(0).getTextContent();
                String title = eElement.getElementsByTagName("title").item(0).getTextContent();
                String content = eElement.getElementsByTagName("content").item(0).getTextContent();
                                
                this.list.add(new Document(Integer.parseInt(id), title, content));
            }

        }
    }
    
    /**
     * 
     * @param title
     * @return 
     */
    public String search(String title) {
        
        String result = "";
        for(Document doc: this.list) {
            if (doc.getTitle().toLowerCase().contains(title.toLowerCase())) {
                result += doc.toString();
            }
        }
        
        if (result.isEmpty()) {
            result += "Not found...";
        }
        
        return result;
    }
    
    /**
     * 
     * @param title
     * @return 
     */
    public String delete(String title) {
        
        String result = "";
        
        Iterator itr = this.list.iterator(); 
        while (itr.hasNext()) 
        { 
            Document doc = (Document)itr.next();
            
            if (doc.getTitle().toLowerCase().contains(title.toLowerCase())) {
                result += doc.toString();
                result += "\ndeleted...\n";
                itr.remove(); 
            }
                
        } 
        
        if (result.isEmpty()) {
            result += "Not found...";
        }
        
        return result;
    }

    /**
     * 
     * @param xsdPath
     * @param xmlPath
     * @return 
     */
    public static boolean validateXMLSchema(String xsdPath, String xmlPath) {

        try {
            SchemaFactory factory
                    = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new File(xsdPath));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new File(xmlPath)));
        } catch (IOException | SAXException e) {
            System.out.println("Exception: " + e.getMessage());
            return false;
        }
        return true;
    }
    
    public String displayAll() {

        String result = "";

        for(Document doc: this.list) {
            result += doc.toString() + "\n";
        }

        if (result.isEmpty()) {
            result += "Not found...";
        }

        return result;
    }
    
    /**
     * 
     */
    public void save() {
        
        try {
        
            this.doc.getDocumentElement().setTextContent("");
            
            Element root = this.doc.getDocumentElement();
        
            for(Document doc: this.list) {

                Element d = this.doc.createElement("document");
                root.appendChild(d);

                Element id = this.doc.createElement("id");
                id.appendChild(this.doc.createTextNode(Integer.toString(doc.getId())));
                d.appendChild(id);

                Element title = this.doc.createElement("title");
                title.appendChild(this.doc.createTextNode(doc.getTitle()));
                d.appendChild(title);

                Element content = this.doc.createElement("content");
                content.appendChild(this.doc.createTextNode(doc.getContent()));
                d.appendChild(content);
            }

            DOMSource source = new DOMSource(this.doc);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            URL xml = getClass().getClassLoader().getResource("resources/documents.xml");
            StreamResult result = new StreamResult(xml.getPath());

            transformer.transform(source, result);
        } catch (TransformerException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * 
     * @param id
     * @param title
     * @param content 
     */
    public void add(int id, String title, String content) {
        this.list.add(new Document(id, title, content));
    }
}
