package nn.data;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/** Display MNist images and labels. */
public class MNistSetPanel extends JPanel {
    private MNistSet set;
    private int startIndex = 0;
    private transient int imagesPerRow = 0; // how many images currently fit in a row?
    private transient int imagesPerColumn = 0; // how many images currently fit in a column?

    public MNistSetPanel(MNistSet set) {
        this.set = set;
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                System.out.println(e.getKeyCode());
            }

            @Override
            public void keyPressed(KeyEvent e) {
                int start = startIndex;
                switch(e.getKeyCode()) {
                    case KeyEvent.VK_RIGHT: case KeyEvent.VK_KP_RIGHT:
                        ++start; break;
                    case KeyEvent.VK_LEFT: case KeyEvent.VK_KP_LEFT:
                        --start; break;
                    case KeyEvent.VK_DOWN: case KeyEvent.VK_KP_DOWN:
                    case KeyEvent.VK_ENTER:
                        start += imagesPerRow; break;
                    case KeyEvent.VK_UP: case KeyEvent.VK_KP_UP:
                        start -= imagesPerRow; break;
                    case KeyEvent.VK_PAGE_DOWN: case KeyEvent.VK_SPACE:
                        start += getImagesPerPage(); break;
                    case KeyEvent.VK_PAGE_UP: case KeyEvent.VK_BACK_SPACE:
                        start -= getImagesPerPage(); break;
                    case KeyEvent.VK_END:
                        start = set.size(); break; // will be fixed by setStartIndex
                    case KeyEvent.VK_HOME:
                        start = 0; break;
                }
                setStartIndex(start);
            }
        });
    }

    private void setStartIndex(int newIndex) {
        if (newIndex < 0) newIndex = 0;
        if (newIndex > (set.size() - getImagesPerPage()))
            newIndex = set.size() - getImagesPerPage();
        boolean change = (newIndex != startIndex);
        startIndex = newIndex;
        if (change)
            repaint();
    }

    private void checkStartIndex() {
        setStartIndex(startIndex);
    }

    private int getImagesPerPage() { return imagesPerColumn * imagesPerRow; }

    /** Fill the window with images and labels. */
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int y = 0, x = 0, i = startIndex;
        int maxHeight = 0;
        int labelHeight = g2.getFontMetrics().getHeight();
        int rowCount = 0;
        while (y < getHeight()) {
            int lineStart = i;
            while (x < getWidth() && i < set.size()) {
                MNistImage image = set.getImage(i);
                g2.drawImage(image.getImage(), x, y, image.width, image.height, null);
                x += image.width;
                String label = "" + set.getLabel(i);
                g2.drawString(label, x, y + (image.height + labelHeight) / 2);
                maxHeight = Math.max(maxHeight, image.height);
                ++i;
                x += g2.getFontMetrics().stringWidth(label);
            }
            if (y == 0)
                imagesPerRow = i - lineStart;
            x = 0;
            y += maxHeight;
            maxHeight = 0;
            ++rowCount;
        }
        imagesPerColumn = rowCount;
        setToolTipText("Displaying " + (i - startIndex) + " of " + set.size()
                + " MNIST \"" + set.name + "\" images (" + startIndex + " - " + (i - 1) + ").");
        checkStartIndex();
    }
}
