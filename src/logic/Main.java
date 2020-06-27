package logic;

import java.awt.EventQueue;

import gui.MainFrame;

public class Main
{
	public static final void main(String[] args)
	{
		EventQueue.invokeLater(() -> {
			new MainFrame();
		});
	}
}
