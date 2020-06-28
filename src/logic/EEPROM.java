package logic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class EEPROM
{
	private String name;
	private int numberOfAddressBits;
	private int numberOfDataBits;
	private String datasheetURL;
	
	public EEPROM() {}
	public EEPROM(String aName, int aNOAB, int aNODB, String aURL)
	{
		this.name = aName;
		this.numberOfAddressBits = aNOAB;
		this.numberOfDataBits = aNODB;
		this.datasheetURL = aURL;
	}
	
	public static ArrayList<EEPROM> getEEPROMsList()
	{
		File directory = new File(System.getProperty("user.dir") + "/eeproms/");
		String[] buffer = directory.list();
		ArrayList<String> files = new ArrayList<String>();
		for(String name : buffer) if(name.contains("xml")) files.add(name);
		buffer = null;
		ArrayList<EEPROM> returnList = new ArrayList<EEPROM>();
		for(String i : files) returnList.add(EEPROM.getFromXML(i));
		return returnList;
	}
	
	public static EEPROM getFromXML(String filename)
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newDefaultInstance();
		Document sample = null;
		NodeList nodes = null;
		
		try
		{
			DocumentBuilder builder = factory.newDocumentBuilder();
			sample = builder.parse(new File(System.getProperty("user.dir") + "/eeproms/" + filename +"/"));
			nodes = sample.getDocumentElement().getChildNodes();
		}
		catch(SAXException|IOException|ParserConfigurationException ex) {ex.printStackTrace();}
		
		String newName = null;
		int newNOAB = 0;
		int newNODB = 0;
		String newURL = null;
		
		for(int i=0;i<nodes.getLength();i++) if(nodes.item(i).getNodeName().equals("name")) newName = nodes.item(i).getTextContent();
		for(int i=0;i<nodes.getLength();i++) if(nodes.item(i).getNodeName().equals("addressBits")) newNOAB = Integer.parseInt(nodes.item(i).getTextContent());
		for(int i=0;i<nodes.getLength();i++) if(nodes.item(i).getNodeName().equals("dataBits")) newNODB = Integer.parseInt(nodes.item(i).getTextContent());
		for(int i=0;i<nodes.getLength();i++) if(nodes.item(i).getNodeName().equals("url")) newURL = nodes.item(i).getTextContent();
		
		return new EEPROM(newName, newNOAB, newNODB, newURL);
	}
	
	public void save()
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newDefaultInstance();
		Document sample = null;
		NodeList nodes = null;
		
		try
		{
			DocumentBuilder builder = factory.newDocumentBuilder();
			sample = builder.parse(new File(System.getProperty("user.dir") + "/eeproms/sample/sample.xml/"));
			nodes = sample.getDocumentElement().getChildNodes();
		}
		catch(SAXException|IOException|ParserConfigurationException ex) {ex.printStackTrace();}
		
		for(int i=0;i<nodes.getLength();i++) if(nodes.item(i).getNodeName().equals("name")) nodes.item(i).setTextContent(this.name);
		for(int i=0;i<nodes.getLength();i++) if(nodes.item(i).getNodeName().equals("addressBits")) nodes.item(i).setTextContent(Integer.toString(this.numberOfAddressBits));
		for(int i=0;i<nodes.getLength();i++) if(nodes.item(i).getNodeName().equals("dataBits")) nodes.item(i).setTextContent(Integer.toString(this.numberOfDataBits));
		for(int i=0;i<nodes.getLength();i++) if(nodes.item(i).getNodeName().equals("url")) nodes.item(i).setTextContent(this.datasheetURL);
		
		try
		{
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(sample);
			StreamResult result = new StreamResult(new File(System.getProperty("user.dir") + "/eeproms/" + this.name.replaceAll(" ", "_") + ".xml"));
			transformer.transform(source, result);
		} catch (TransformerException ex) {ex.printStackTrace();}
	}
	
	public String getName() {return this.name;}
	public int getAddressLength() {return this.numberOfAddressBits;}
	public int getDataLength() {return this.numberOfDataBits;}
	public String getURL() {return this.datasheetURL;}
	
}
