/*
 * Copyright 2011 Junar SpA.
 * 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tika.parser.csv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Iterator;
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

import au.com.bytecode.opencsv.CSVReader;

/**
 * A simple CSV parser implementation for Tika to convert a CSV 
 * on a basic HTML table using opencsv (http://opencsv.sourceforge.net). 
 * 
 * By default use:
 *  - Value separator: ,
 *  - Value delimiter: " 
 * 
 * @author Marco Salgado A. < marco.salgado[at]junar.com >
 */
public class CSVTikaParser implements Parser {
    private XHTMLContentHandler fXHTML;
    
    private static final Set<MediaType> SUPPORTED_TYPES = 
        Collections.singleton(MediaType.text("csv"));

    public static final String CSV_MIME_TYPE = "text/csv";

    public Set<MediaType> getSupportedTypes(ParseContext context) {
        return SUPPORTED_TYPES;
    }

    public void parse(
            InputStream stream, ContentHandler handler,
            Metadata metadata, ParseContext context)
    throws IOException, SAXException, TikaException {      
        
        fXHTML = new XHTMLContentHandler(handler, metadata);
        
        BufferedReader lBufferedReader = new BufferedReader(new InputStreamReader(stream));
        CSVReader lReader = new CSVReader(lBufferedReader);
        List<String[]> lCSVEntries = lReader.readAll();
        
        getTable(lCSVEntries);
        
        metadata.set("Author","Junar");
        metadata.set("Website","www.junar.com");
        
        lBufferedReader.close();
    }
    
    public void getTable(List<String[]> pCSVContent) throws SAXException 
    {
        Iterator<String[]> lIterator = pCSVContent.iterator();
        
        fXHTML.startDocument();
        fXHTML.startElement("table");
        while (lIterator.hasNext()) {
            fXHTML.startElement("tr");
            getRows(lIterator.next());
            fXHTML.endElement("tr");
        }
        fXHTML.endElement("table");
        fXHTML.endDocument();
    }
    
    public void getRows(String[] pRow) throws SAXException
    {
        for (String lRowValue: pRow) {
            fXHTML.startElement("td");
            fXHTML.characters(lRowValue);
            fXHTML.endElement("td");
        }
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
