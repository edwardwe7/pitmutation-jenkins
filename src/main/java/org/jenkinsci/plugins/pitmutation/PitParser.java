package org.jenkinsci.plugins.pitmutation;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import hudson.FilePath;

/**
 * @author edward
 */
public class PitParser {

  public PitParser(FilePath report) throws ParserConfigurationException, IOException,
          SAXException {

    DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    Document doc = parser.parse(report.read());

    String generated = doc.getElementsByTagName("generated").item(0).getTextContent();
    String killed = doc.getElementsByTagName("killed").item(0).getTextContent();

    killRatio_ = new Ratio(Float.parseFloat(killed), Float.parseFloat(generated));
  }

  public Ratio getKillRatio() {
    return killRatio_;
  }

  private Ratio killRatio_;
}
