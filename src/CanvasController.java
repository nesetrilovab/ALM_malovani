import java.awt.event.*;
import models.LineStyle;

//ovladač, který zachytává vstupy od uživatele
public class CanvasController {
    private final CanvasPanel panel;

    public CanvasController(CanvasPanel panel) {
        this.panel = panel;
    }

    //obsluha myši
    public MouseAdapter createMouseAdapter() {
        return new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                panel.requestFocusInWindow();
                panel.startShape(e.getX(), e.getY());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (panel.getMode() != CanvasPanel.Mode.POLYGON) {
                    panel.finishShape(e.getX(), e.getY());
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                //preview efekt
                panel.updateShape(e.getX(), e.getY());
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (panel.getMode() == CanvasPanel.Mode.POLYGON && e.getButton() == MouseEvent.BUTTON3) {
                    panel.finalizePolygon();
                }
            }
        };
    }

    //klávesové zkratky (zachované z původní verze)
    public KeyAdapter createKeyAdapter() {
        return new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_SHIFT -> panel.setShiftMode(true);
                    case KeyEvent.VK_CONTROL -> panel.setLineStyle(LineStyle.DOTTED);
                    case KeyEvent.VK_ALT -> panel.setLineStyle(LineStyle.DASHED);
                    case KeyEvent.VK_S -> panel.setMode(CanvasPanel.Mode.SELECT);
                    case KeyEvent.VK_P -> panel.setMode(CanvasPanel.Mode.POLYGON);
                    case KeyEvent.VK_R -> panel.setMode(CanvasPanel.Mode.RECTANGLE);
                    case KeyEvent.VK_L -> panel.setMode(CanvasPanel.Mode.LINE);
                    case KeyEvent.VK_O -> panel.setMode(CanvasPanel.Mode.ELLIPSE);
                    case KeyEvent.VK_C -> panel.clearCanvas();
                    case KeyEvent.VK_E -> panel.setMode(CanvasPanel.Mode.ERASE);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                // Navrácení stavů po puštění klávesy
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_SHIFT -> panel.setShiftMode(false);
                    case KeyEvent.VK_CONTROL, KeyEvent.VK_ALT -> panel.setLineStyle(LineStyle.SOLID);
                }
            }
        };
    }
}