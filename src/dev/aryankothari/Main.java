package dev.aryankothari;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class Main {
    public static Config config = new Config("config");
    public static Calculator aCalc;

    static {
        try {
            aCalc = new Calculator(config.break_point);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public Main() throws AWTException {

    }

    public static void main(String[] args) throws Exception {

        ActionListener exitListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        };

        ActionListener setBPListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // show input prompt
                try{
                    config.set_break_point(Integer.parseInt(JOptionPane.showInputDialog("Enter a new break point. This will require a restart of the program.")));
                    System.exit(0);
                }catch(NumberFormatException numformatexception){

                }
            }
        };

        if (!SystemTray.isSupported()){
            System.out.println("System Tray not supported.");
            return;
        }
        Image image = Toolkit.getDefaultToolkit().getImage("icon.png");
        final PopupMenu popup = new PopupMenu();
        final TrayIcon trayIcon = new TrayIcon(image, "Microphone Breaker", popup);
        final SystemTray tray = SystemTray.getSystemTray();

        MenuItem setBreakPointItem = new MenuItem("Set Break Point");
        setBreakPointItem.addActionListener(setBPListener);
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.addActionListener(exitListener);

        popup.add(setBreakPointItem);
        popup.addSeparator();
        popup.add(exitItem);
        tray.add(trayIcon);
        aCalc.startListening();
    }


}
