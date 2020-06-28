package gui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.GridBagLayout;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import logic.ByteArray;
import logic.EEPROM;

public class MainFrame extends JFrame
{
	ArrayList<EEPROM> EEPROMsAvailable;
	EEPROM selectedEEPROM;
	ByteArray array;
	Table table;
	
	JButton refresh;
	EEPROMselection es;
	BottomPanel bottomPanel;

	
	public MainFrame()
	{
		EEPROMsAvailable = EEPROM.getEEPROMsList();
		this.setTitle("EEPROM programmer");
		this.setLayout(new GridBagLayout());
		es = new EEPROMselection();
		this.add(es, new GBC(0,0,1,1));
		refresh = new JButton("Refresh");
		this.add(refresh, new GBC(1,0,1,1));
		refresh.addActionListener(event -> {
			EEPROMsAvailable = EEPROM.getEEPROMsList();
			this.remove(table);
			this.remove(es);
			table = new Table();
			es = new EEPROMselection();
			this.add(es, new GBC(0,0,1,1));
			this.add(table, new GBC(0,1,2,1));
			this.revalidate();
		});
		table = new Table();
		this.add(table, new GBC(0,1,2,1));
		bottomPanel = new BottomPanel();
		this.add(bottomPanel, new GBC(0,2,2,1));
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
			this.add(makeAddEEPROMButton());
		}
		
		private JComboBox makeList()
		{
			JComboBox<String> returnList = new JComboBox<>();
			for (EEPROM element : EEPROMsAvailable) returnList.addItem(element.getName());
			if(returnList.getItemCount() > 0)
			{
				selectedEEPROM = EEPROMsAvailable.get(0);
				returnList.setSelectedIndex(0);
				array = new ByteArray(selectedEEPROM);
			}
			returnList.addActionListener(event -> {
				selectedEEPROM = EEPROMsAvailable.get(returnList.getSelectedIndex());
				array = new ByteArray(selectedEEPROM);
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
			for(int key : array.getArray().keySet()) model.addRow(new String[] {Integer.toHexString(key),Integer.toHexString(array.getArray().get(key))});
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
				array.getArray().replace(Integer.parseInt(model.getValueAt(row, column-1).toString(),16), update);
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
			});
			return returnButton;
		}
	}
}
