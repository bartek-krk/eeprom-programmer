package logic;

import java.awt.EventQueue;

import gui.MainFrame;

public class Main
{
    public static void main ( String[] args )
    {
        EventQueue.invokeLater(() -> {
        	new MainFrame();
        });
    }
}