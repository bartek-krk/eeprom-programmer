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
	
	private static final String ROW_SEPARATOR = "\n";
	private static final String FIELD_SEPARATOR = ";";
	
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
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int result = chooser.showOpenDialog(null);
		String directory = "";
		if(result == JFileChooser.APPROVE_OPTION)
		{
			directory = chooser.getSelectedFile().getPath();
			directory = directory + ".csv";
			
			String output = "";
			for(int key : array.keySet()) output += key + FIELD_SEPARATOR + array.get(key) + ROW_SEPARATOR;
			
			try (PrintWriter out = new PrintWriter(directory)) {out.print(output);}
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
								
				
				String[] rows = incomingFile.split(ROW_SEPARATOR);
				
				for(String row : rows)
				{
					String[] bufferedFields = row.split(FIELD_SEPARATOR);
					keys.add(bufferedFields[0]);
					values.add(bufferedFields[1]);
				}
								
				ArrayList<Integer> addresses = new ArrayList<Integer>();
				for(String currentKey : keys) addresses.add(Integer.parseInt(currentKey));
				keys = null;
				
				ArrayList<Integer> data = new ArrayList<Integer>();
				for(String currentValue : values) data.add(Integer.parseInt(currentValue));
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
