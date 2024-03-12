package fr.pajonti.concasse.helper.technical;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class JFileChooserHelper {
    private JFileChooserHelper(){

    }

    public static File getChooseDirectoryDialog(){
        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.setPreferredSize(new Dimension(1000,1000));

            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                return fileChooser.getSelectedFile();
            }
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return null;
    }
}
