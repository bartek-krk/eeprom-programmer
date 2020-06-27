package logic;

import java.awt.Toolkit;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
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
	
	public Hashtable<Integer,Integer> getArray() {return this.array;}
}
