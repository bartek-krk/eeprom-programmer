package logic;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.concurrent.TimeUnit;

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
			try {TimeUnit.SECONDS.sleep(5);}
			catch (InterruptedException e) {e.printStackTrace();}
			Thread loadingData = new Thread() {
				@Override
				public void run()
				{
					try{Thread.sleep(100);} catch(InterruptedException ex) {ex.printStackTrace();}
					PrintWriter outputStream = new PrintWriter(port.getOutputStream(),true);
					outputStream.print("TRANSMITTING");
					outputStream.flush();
					int i = 2047;
					for(String statement : input)
					{
						outputStream.print(statement);
						System.out.println(statement);
						outputStream.flush();
						try{Thread.sleep(100);} catch(InterruptedException ex) {ex.printStackTrace();}
						System.out.println("data sent" + Integer.toString(i));
						i--;
					}
					outputStream.print("EOT");
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
			while(keyBIN.length() + keyPrefix.length() != 11) keyPrefix =keyPrefix + "0";
			keyBIN = keyPrefix + keyBIN;
			
			String dataBIN = Integer.toBinaryString(input.get(key));
			String dataPrefix = "";
			while(dataBIN.length() + dataPrefix.length() != 8) dataPrefix = dataPrefix + "0";
			dataBIN = dataPrefix + dataBIN;
			
			output.add(keyBIN+dataBIN);
		}
		
		return output;
	}
}