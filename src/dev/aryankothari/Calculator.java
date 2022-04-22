package dev.aryankothari;

import javax.sound.sampled.*;
import javax.swing.JOptionPane;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.concurrent.TimeUnit;

public class Calculator {

    private final int breaker_level;
    private final Robot clicker;
    private final String[] options;

    public Calculator(int breaker_level) throws AWTException {
        this.breaker_level = breaker_level;
        this.clicker = new Robot();
        this.options = new String[]{"Close Popup", "Close Microphone Breaker"};
    }


    // Data line for mic input
    protected TargetDataLine line = null;

    /**
     * VolumeMeter::startListening
     * Open audio channel and start working with stream
     */
    public void startListening() {
        // Open a TargetDataLine for getting mic input level
        AudioFormat format = new AudioFormat(42000.0f, 16, 1, true, true); // Get default line
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        if (!AudioSystem.isLineSupported(info)) { // If no default line
            System.out.println("The TargetDataLine is unavailable");
        }

        // Obtain and open the line.
        try {
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();
        } catch (LineUnavailableException ex) {
            System.out.println("The TargetDataLine is Unavailable.");
        }

        int level = 0; // Hold calculated RMS volume level
        byte tempBuffer[] = new byte[6000]; // Data buffer for raw audio
        try {
            // Continually read in mic data into buffer and calculate RMS
            boolean just_popped = false;
            while (true) {

                TimeUnit.MILLISECONDS.sleep(500);
                // If read in enough, calculate RMS
                if (line.read(tempBuffer, 0, tempBuffer.length) > 0) {
                    level = calculateRMSLevel(tempBuffer);
                    if (level > this.breaker_level) {
                        if (just_popped){
                            just_popped = false;
                            continue;
                        }else{
                            just_popped = true;
                            this.clicker.keyPress(KeyEvent.VK_WINDOWS);
                            this.clicker.keyPress(KeyEvent.VK_F9);
                            this.clicker.keyPress(KeyEvent.VK_F10);
                            int response = JOptionPane.showOptionDialog(
                                    null,
                                    "Microphone Breaker Popped!",
                                    "Microphone Breaker",
                                    JOptionPane.WARNING_MESSAGE,
                                    0,
                                    null,
                                    this.options,
                                    this.options[0]
                            );
                            if (response == 1){
                                this.clicker.keyRelease(KeyEvent.VK_WINDOWS);
                                this.clicker.keyRelease(KeyEvent.VK_F9);
                                this.clicker.keyRelease(KeyEvent.VK_F10);
                                System.exit(0);
                            }

                            this.clicker.keyRelease(KeyEvent.VK_WINDOWS);
                            this.clicker.keyRelease(KeyEvent.VK_F9);
                            this.clicker.keyRelease(KeyEvent.VK_F10);
                        }
                    }
                    System.out.println(level);
                }
            }
        } catch (Exception e) {
            System.err.println(e);
            System.exit(0);
        }
    }

    /**
     * VolumeMeter::calculateRMSLevel
     * Calculate the RMS of the raw audio in buffer
     *
     * @param audioData The buffer containing snippet of raw audio data
     * @return int  The RMS value of the buffer
     */
    protected static int calculateRMSLevel(byte[] audioData) {
        long lSum = 0;
        for (int i = 0; i < audioData.length; i++)
            lSum = lSum + audioData[i];

        double dAvg = lSum / audioData.length;

        double sumMeanSquare = 0d;
        for (int j = 0; j < audioData.length; j++)
            sumMeanSquare = sumMeanSquare + Math.pow(audioData[j] - dAvg, 2d);

        double averageMeanSquare = sumMeanSquare / audioData.length;
        return (int) (Math.pow(averageMeanSquare, 0.5d) + 0.5) - 50;
    }
}
