package logic;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;

import com.fazecast.jSerialComm.*;

public class Transmission
{
	private static SerialPort port;
	
	public static synchronized void writeData(ArrayList<String> input)
	{
		SerialPort[] portList = SerialPort.getCommPorts();
		ArrayList<String> portNames = new ArrayList<String>();
		
		for(SerialPort p : portList) portNames.add(p.getSystemPortName());
		
		if(!portNames.isEmpty()) port = SerialPort.getCommPort(portNames.get(0).toString());
		
		port.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
		if(port.openPort())
		{
			Thread loadingData = new Thread() {
				@Override
				public void run()
				{
					try{Thread.sleep(100);} catch(InterruptedException ex) {ex.printStackTrace();}
					PrintWriter outputStream = new PrintWriter(port.getOutputStream());
					outputStream.write("TRANSMITTING");
					outputStream.flush();
					int i = 0;
					for(String statement : input)
					{
						outputStream.write(statement);
						outputStream.flush();
						try{Thread.sleep(10);} catch(InterruptedException ex) {ex.printStackTrace();}
						System.out.println("data sent" + Integer.toString(i));
						i++;
					}
					outputStream.write("EOT");
					outputStream.flush();
				}
			};
			loadingData.start();
		}
	}
	
	public static ArrayList<String> makeStatements(Hashtable<Integer,Integer> input )
	{
		ArrayList<String> output = new ArrayList<String>();
		
		for(Integer key : input.keySet())
		{
			String keyBIN = Integer.toBinaryString(key);
			String keyPrefix = "";
			while(keyBIN.length() + keyPrefix.length() != 11) keyPrefix = keyPrefix + "0";
			keyBIN = keyBIN + keyPrefix;
			
			String dataBIN = Integer.toBinaryString(input.get(key));
			String dataPrefix = "";
			while(dataBIN.length() + dataPrefix.length() != 8) dataPrefix = dataPrefix + "0";
			dataBIN = dataBIN + dataPrefix;
			
			output.add(keyBIN+dataBIN);
		}
		
		return output;
	}
}