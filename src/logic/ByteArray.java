package logic;

import java.awt.Toolkit;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class ByteArray
{
	private final int addressLength;
	private final int dataLength;
	private final int maxAddress;
	private final int maxData;
	
	public Hashtable<Integer,Integer> array;
	
	public ByteArray(EEPROM eeprom)
	{
		this.addressLength = eeprom.getAddressLength();
		this.dataLength = eeprom.getDataLength();
		
		String maxAddressString = "";
		for(int i=1;i<=this.addressLength;i++) {maxAddressString+="1";}
		this.maxAddress = Integer.parseInt(maxAddressString, 2);
		
		String maxDataString = "";
		for(int i=1;i<=this.dataLength;i++) {maxDataString+="1";}
		this.maxData = Integer.parseInt(maxDataString, 2);
		
		array = new Hashtable<Integer,Integer>(addressLength);
		for(int key=0;key<=maxAddress;key++) {array.put(key, 0);}
	}
	
	public void setData(int address,int value)
	{
		if(address<=this.maxAddress && value<=this.maxData) this.array.replace(address, value);
		else if(address>this.maxAddress)
		{
			Toolkit.getDefaultToolkit().beep();
			JOptionPane.showMessageDialog(null, "Address exceeded!", "Overflow error", JOptionPane.WARNING_MESSAGE);
		}
		else if(value>this.maxData)
		{
			Toolkit.getDefaultToolkit().beep();
			JOptionPane.showMessageDialog(null, "Data exceeded!", "Overflow error", JOptionPane.WARNING_MESSAGE);
		}
	}
	
	public void saveAsCSV()
	{
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int result = chooser.showOpenDialog(null);
		String directory = "";
		if(result == JFileChooser.APPROVE_OPTION)
		{
			directory = chooser.getSelectedFile().getPath();
			String filename = JOptionPane.showInputDialog(null, "Enter filename:", "EEPROMdata");
			directory = directory + "\\" + filename + ".csv";
			
			String output = "";
			for(int key : array.keySet()) output += key + ";" + array.get(key) + "\n";
			
			try (PrintWriter out = new PrintWriter(directory)) {out.println(output);}
			catch(FileNotFoundException ex) {ex.printStackTrace();}
		}
	}
	
	public static ByteArray openCSV()
	{
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int result = chooser.showOpenDialog(null);
		String pathToFile = "";
		if(result == JFileChooser.APPROVE_OPTION)
		{
			pathToFile = chooser.getSelectedFile().getPath();
			if(pathToFile.contains(".csv"))
			{
				String incomingFile = "";
				try
				{
					incomingFile = new String(Files.readAllBytes(Paths.get(pathToFile)));
				}
				catch(IOException ex) {ex.printStackTrace();}
								
				ArrayList<String> keys = new ArrayList<String>();
				ArrayList<String> values = new ArrayList<String>();
				
				ArrayList<Integer> separators = new ArrayList<Integer>();
				
				separators.add(0);				
				for(int i=0;i<incomingFile.length();i++) if(incomingFile.charAt(i) == ';' || incomingFile.charAt(i) == '\n') separators.add(i);
				separators.add(incomingFile.length());
				
				ArrayList<String> results = new ArrayList<String>();
				for(int i=0;i<separators.size()-1;i++) results.add(incomingFile.substring(separators.get(i), separators.get(i+1)));
				
				separators = null;
				incomingFile = null;
				System.gc();
				
				for(int i=0;i<results.size();i+=2) keys.add(results.get(i));
				for(int i=1;i<results.size();i+=2) values.add(results.get(i));
				
				for(int i=0;i<keys.size();i++)
				{
					if(keys.get(i).contains("\n"))
					{
						String s = keys.get(i).substring(0,keys.get(i).indexOf('\n')) + keys.get(i).substring(keys.get(i).indexOf('\n')+1);
						keys.set(i, s);
					}
				}
				
				for(int i=0;i<values.size();i++)
				{
					if(values.get(i).contains(";"))
					{
						String s = values.get(i).substring(0,values.get(i).indexOf(';')) + values.get(i).substring(values.get(i).indexOf(';')+1);
						values.set(i, s);
					}
				}
				
				ArrayList<Integer> addresses = new ArrayList<Integer>();
				keys.remove(keys.size()-1);
				for(String s : keys) addresses.add(Integer.parseInt(s));
				keys = null;
				
				ArrayList<Integer> data = new ArrayList<Integer>();
				values.remove(values.size()-1);
				for(String s : values) data.add(Integer.parseInt(s));
				values = null;
				System.gc();
				
				int maxAddress = Integer.toBinaryString(Collections.max(addresses)).length();
				int maxData = Integer.toBinaryString(Collections.max(data)).length();
				
				ByteArray returnArray = new ByteArray(new EEPROM("sampleName",maxAddress,maxData,"www.google.com"));
				
				Hashtable<Integer,Integer> dict = new Hashtable<Integer,Integer>();
				
				for(int i=0;i<addresses.size();i++)
				{
					dict.put(addresses.get(i), data.get(i));
				}
				
				returnArray.array = dict;
				
				return returnArray;
			}
			else
			{
				Toolkit.getDefaultToolkit().beep();
				JOptionPane.showMessageDialog(null, "Only *.csv files allowed!", "Invalid input format", JOptionPane.WARNING_MESSAGE);
				return null;
			}
		}
		else return null;
	}
	
	public Hashtable<Integer,Integer> getArray() {return this.array;}
}
