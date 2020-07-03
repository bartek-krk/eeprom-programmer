package gui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import logic.ByteArray;
import logic.EEPROM;
import logic.Transmission;

public class MainFrame extends JFrame
{
	private ArrayList<EEPROM> EEPROMsAvailable;
	private EEPROM selectedEEPROM;
	private ByteArray array;
	private Table table;
	private EEPROMselection es;
	private BottomPanel bottomPanel;
	private int EEPROMselected = 0;
	
	public MainFrame()
	{
		EEPROMsAvailable = EEPROM.getEEPROMsList();
		this.setTitle("EEPROM programmer");
		this.setLayout(new GridBagLayout());
		es = new EEPROMselection();
		this.add(es, new GBC(0,0));
		table = new Table();
		this.add(table, new GBC(0,1));
		bottomPanel = new BottomPanel();
		this.add(bottomPanel, new GBC(0,2));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
	
	private class EEPROMselection extends JPanel
	{
		public EEPROMselection()
		{
			this.add(makeList());
			this.add(makeURLButton());
			this.add(makeArduinoButton());
			this.add(makeAddEEPROMButton());
		}
		
		private JComboBox makeList()
		{
			JComboBox<String> returnList = new JComboBox<>();
			for (EEPROM element : EEPROMsAvailable) returnList.addItem(element.getName());
			if(returnList.getItemCount() > 0)
			{
				selectedEEPROM = EEPROMsAvailable.get(EEPROMselected);
				returnList.setSelectedIndex(EEPROMselected);
				array = new ByteArray(selectedEEPROM);
			}
			returnList.addActionListener(event -> {
				selectedEEPROM = EEPROMsAvailable.get(returnList.getSelectedIndex());
				EEPROMselected = returnList.getSelectedIndex();
				array = new ByteArray(selectedEEPROM);
				ByteArray buffer = null;
				if(array != null) buffer = array;
				EEPROMsAvailable = EEPROM.getEEPROMsList();
				MainFrame.this.remove(table);
				MainFrame.this.remove(es);
				table = new Table();
				es = new EEPROMselection();
				MainFrame.this.add(es, new GBC(0,0));
				MainFrame.this.add(table, new GBC(0,1));
				MainFrame.this.revalidate();
				MainFrame.this.pack();
				if(buffer != null) MainFrame.this.array = buffer;
				buffer = null;
			});
			return returnList;
		}
		
		private JButton makeURLButton()
		{
			JButton returnButton = new JButton("See datasheet");
			returnButton.addActionListener(event -> {
				Desktop desktop = Desktop.getDesktop();
				try{desktop.browse(new URI(selectedEEPROM.getURL()));}
				catch(IOException|URISyntaxException ex) {ex.printStackTrace();}
			});
			return returnButton;
		}
		
		private JButton makeAddEEPROMButton()
		{
			JButton returnButton = new JButton("New EEPROM");
			returnButton.addActionListener(event -> {
				new AddEEPROMFrame();
				});
			return returnButton;
		}
		
		private JButton makeArduinoButton()
		{
			JButton returnButton = new JButton("Arduino sketch");
			returnButton.addActionListener(event -> {
				String pathStr = MainFrame.this.selectedEEPROM.getArduinoPath();
				if(Paths.get(pathStr).toFile().exists())
				{
					try {Desktop.getDesktop().open(Paths.get(pathStr).toFile());}
					catch(IOException ex) {ex.printStackTrace();}
				}
				else
				{
					Toolkit.getDefaultToolkit().beep();
					JOptionPane.showMessageDialog(null, "File does not exists!", "Error", JOptionPane.WARNING_MESSAGE);
				}
			});
			return returnButton;
		}
		
	}
	
	private class Table extends JPanel
	{
		private String[] labels = new String [] {"Address (0x___)", "Data(DEC / BIN:0b__ / HEX:0x__)"};
		
		public Table()
		{
			add(new JScrollPane(getTable()));
		}
		
		private JTable getTable()
		{
			JTable returnTable = new JTable() {
			    @Override
			    public boolean isCellEditable(int row, int column) {
			        if(column == 0) return false;
			        else return true;
			    }
			};
			returnTable.getTableHeader().setReorderingAllowed(false);
			DefaultTableModel model = new DefaultTableModel();
			model.setColumnIdentifiers(labels);
			for(int key : array.getArray().keySet()) model.addRow(new String[] {Integer.toHexString(key).toUpperCase(),Integer.toHexString(array.getArray().get(key)).toUpperCase()});
			model.addTableModelListener(l -> {
				int column = l.getColumn();
				int row = l.getFirstRow();
				
				String updateString = model.getValueAt(row, column).toString();
				int radix = 10;
				if(updateString.contains("0x")) radix = 16;
				if(updateString.contains("0b")) radix = 2;
				int update = 0;
				if(radix != 10) update = Integer.parseInt(updateString.substring(2),radix);
				else update = Integer.parseInt(updateString);
				array.setData(Integer.parseInt(model.getValueAt(row, column-1).toString(),16), update);
			});
			returnTable.setModel(model);
			
			return returnTable;
		}
	}
	
	private class BottomPanel extends JPanel
	{
		public BottomPanel()
		{
			add(makeSaveToCSVButton());
			add(makeOpenButton());
			add(makeSendButton());
		}
		
		private JButton makeSaveToCSVButton()
		{
			JButton returnButton = new JButton("Save as CSV");
			returnButton.addActionListener(event -> {
				array.saveAsCSV();
			});
			return returnButton;
		}
		
		private JButton makeOpenButton()
		{
			JButton returnButton = new JButton("Open from CSV");
			returnButton.addActionListener(event -> {
				MainFrame.this.array = ByteArray.openCSV();
				ByteArray buffer = null;
				if(array != null) buffer = array;
				EEPROMsAvailable = EEPROM.getEEPROMsList();
				MainFrame.this.remove(table);
				MainFrame.this.remove(es);
				table = new Table();
				es = new EEPROMselection();
				MainFrame.this.add(es, new GBC(0,0));
				MainFrame.this.add(table, new GBC(0,1));
				MainFrame.this.revalidate();
				MainFrame.this.pack();
				if(buffer != null) MainFrame.this.array = buffer;
				buffer = null;
			});
			return returnButton;
		}
		
		private JButton makeSendButton()
		{
			JButton returnButton = new JButton("Send");
			returnButton.addActionListener(event -> {
				Transmission.writeData(Transmission.makeStatements(array.getArray()));
			});
			return returnButton;
		}
	}
}
