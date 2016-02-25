package nn.test;

import nn.data.MNistReader;
import nn.data.MNistSet;
import nn.data.MNistSetPanel;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

/** Test reading & displaying the MNIST data. */
public class TestReader {
    public static void main(String[] args) throws IOException {
        File here = new File("data/mnist");
//        System.out.println(here.getCanonicalPath());
        MNistSet t10k = MNistReader.readMnistSet(here, "t10k");
        showInFrame(t10k);
        MNistSet train = MNistReader.readMnistSet(here, "train");
        showInFrame(train);
    }

    public static void showInFrame(MNistSet set) throws IOException {
        JFrame frame = new JFrame(set.name);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setContentPane(new MNistSetPanel(set));
        frame.setSize(500, 300);
        frame.setVisible(true);
    }
}
