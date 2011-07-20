package org.apache.tika.parser.csv;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.XHTMLContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException; 

import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVReader;

public class CSVTikaParser implements Parser {
    private XHTMLContentHandler fXHTML;
    
    private static final Set<MediaType> SUPPORTED_TYPES = 
        Collections.singleton(MediaType.text("csv"));

    public static final String HELLO_MIME_TYPE = "application/hello";

    public Set<MediaType> getSupportedTypes(ParseContext context) {
        return SUPPORTED_TYPES;
    }

    public void parse(
            InputStream stream, ContentHandler handler,
            Metadata metadata, ParseContext context)
    throws IOException, SAXException, TikaException {      
        fXHTML = new XHTMLContentHandler(handler, metadata);
        fXHTML.startDocument();
        fXHTML.startElement("table");
       
        CSVReader reader = new CSVReader(new FileReader("/tmp/foobar.csv"));
        String [] nextLine;
        while ((nextLine = reader.readNext()) != null) {
            fXHTML.startElement("tr");
            fXHTML.startElement("td");
            fXHTML.characters(nextLine[0]);
            fXHTML.endElement("td");
            fXHTML.startElement("td");
            fXHTML.characters(nextLine[1]);
            fXHTML.endElement("td");
            fXHTML.endElement("tr");
        }
        fXHTML.endElement("table");
        fXHTML.endDocument();

        metadata.set("hello","world");
        metadata.set("title","Hello World!");
    }

    /**
     * @deprecated This method will be removed in Apache Tika 1.0.
     */
    public void parse(
            InputStream stream, ContentHandler handler, Metadata metadata)
    throws IOException, SAXException, TikaException {
        parse(stream, handler, metadata, new ParseContext());
    }
}
