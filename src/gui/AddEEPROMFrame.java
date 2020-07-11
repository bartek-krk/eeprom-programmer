package gui;

import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import logic.EEPROM;

public class AddEEPROMFrame extends JFrame
{
	private String name;
	private int NOAB;
	private int NODB;
	private String URL;
	
	private JTextField nameInput;
	private JTextField NOABinput;
	private JTextField NODBinput;
	private JTextField URLinput;
	
	private boolean isFillingFinished = false;
	
	private JButton confirm;
	
	public AddEEPROMFrame()
	{
		this.setTitle("Add EEPROM");
		this.setLayout(new GridBagLayout());
		
		this.nameInput = new JTextField(30);
		nameInput.addCaretListener(event -> {this.name = nameInput.getText();});
		this.NOABinput = new JTextField(30);
		NOABinput.addCaretListener(event -> {if(NOABinput.getText().length() > 0) this.NOAB = Integer.parseInt(NOABinput.getText());});
		this.NODBinput = new JTextField(30);
		NODBinput.addCaretListener(event -> {if(NODBinput.getText().length() > 0) this.NODB = Integer.parseInt(NODBinput.getText());});
		this.URLinput = new JTextField(30);
		URLinput.addCaretListener(event -> {this.URL = URLinput.getText();});
		this.add(new JLabel("EEPROM name:"), new GBC(0,0));
		this.add(nameInput, new GBC(1,0));
		this.add(new JLabel("Number of address inputs:"), new GBC(0,1));
		this.add(NOABinput, new GBC(1,1));
		this.add(new JLabel("Number of data I/O:"), new GBC(0,2));
		this.add(NODBinput, new GBC(1,2));
		this.add(new JLabel("Datasheet URL:"), new GBC(0,3));
		this.add(URLinput, new GBC(1,3));
		
		this.confirm = new JButton("Save");
		confirm.addActionListener(event -> {new EEPROM(this.name,this.NOAB,this.NODB,this.URL).save(); isFillingFinished = true; this.setVisible(false);});
		this.add(confirm, new GBC(0,4,2,1));
		
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
	
	public boolean getCompletionStatus() {return this.isFillingFinished;}
}
