import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import models.*;

public class App {

    private final JFrame frame;           // Hlavní okno aplikace
    private final CanvasPanel canvas;     // kreslicí plocha
    private final CanvasController controller; // Ovladač

    public App(int width, int height) {
        frame = new JFrame("Paint App");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        canvas = new CanvasPanel(width, height);
        controller = new CanvasController(canvas);

        // posluchače pro ovládání kreslení a manipulaci s tvary
        canvas.addMouseListener(controller.createMouseAdapter());
        canvas.addMouseMotionListener(controller.createMouseAdapter());
        canvas.addKeyListener(controller.createKeyAdapter());

        frame.setLayout(new BorderLayout());
        frame.add(createToolbar(), BorderLayout.NORTH); // Lišta nahoru
        frame.add(canvas, BorderLayout.CENTER);         // Plátno doprostřed

        frame.pack();
        frame.setVisible(true);
    }

    //Toolbar
    private JToolBar createToolbar() {
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);

        //Výběr tvarů
        String[] shapes = {"Line", "Polygon", "Rectangle", "Ellipse"};
        JComboBox<String> shapeBox = new JComboBox<>(shapes);
        shapeBox.addActionListener(e -> {
            String sel = (String) shapeBox.getSelectedItem();
            if (sel == null) return;
            switch (sel) {
                case "Line" -> canvas.setMode(CanvasPanel.Mode.LINE);
                case "Polygon" -> canvas.setMode(CanvasPanel.Mode.POLYGON);
                case "Rectangle" -> canvas.setMode(CanvasPanel.Mode.RECTANGLE);
                case "Ellipse" -> canvas.setMode(CanvasPanel.Mode.ELLIPSE);
            }
            canvas.requestFocusInWindow(); // Vrátí focus plátnu pro klávesové zkratky
        });
        toolbar.add(new JLabel(" Shape: "));
        toolbar.add(shapeBox);

        toolbar.addSeparator();

        //Výběr stylu čáry
        String[] styles = {"Solid", "Dotted", "Dashed"};
        JComboBox<String> styleBox = new JComboBox<>(styles);
        styleBox.addActionListener(e -> {
            String sel = (String) styleBox.getSelectedItem();
            if (sel == null) return;
            switch (sel) {
                case "Solid" -> canvas.setLineStyle(LineStyle.SOLID);
                case "Dotted" -> canvas.setLineStyle(LineStyle.DOTTED);
                case "Dashed" -> canvas.setLineStyle(LineStyle.DASHED);
            }
            canvas.requestFocusInWindow();
        });
        toolbar.add(new JLabel(" Style: "));
        toolbar.add(styleBox);

        toolbar.addSeparator();

        //Nastavení tloušťky čáry
        JSlider thicknessSlider = new JSlider(JSlider.HORIZONTAL, 1, 4, 1);
        thicknessSlider.setMajorTickSpacing(1);
        thicknessSlider.setPaintTicks(true);
        thicknessSlider.setPaintLabels(true); // Zobrazí čísla 1, 2, 3, 4
        thicknessSlider.setPreferredSize(new Dimension(100, 45));

        thicknessSlider.addChangeListener(e -> {
            if (!thicknessSlider.getValueIsAdjusting()) {
                canvas.setCurrentThickness(thicknessSlider.getValue());
                canvas.requestFocusInWindow();
            }
        });
        toolbar.add(new JLabel(" Thickness: "));
        toolbar.add(thicknessSlider);

        toolbar.addSeparator();

        //Tlačítko pro výběr barvy
        JButton colorBtn = new JButton("Color");
        colorBtn.addActionListener(e -> {
            Color selected = JColorChooser.showDialog(frame, "Select Color", canvas.getCurrentColor());
            if (selected != null) {
                canvas.setCurrentColor(selected);
            }
            canvas.requestFocusInWindow();
        });
        toolbar.add(colorBtn);

        toolbar.addSeparator();

        //Speciální módy
        JButton selectBtn = new JButton("Select");
        selectBtn.addActionListener(e -> { canvas.setMode(CanvasPanel.Mode.SELECT); canvas.requestFocusInWindow(); });
        toolbar.add(selectBtn);

        JButton eraseBtn = new JButton("Erase");
        eraseBtn.addActionListener(e -> { canvas.setMode(CanvasPanel.Mode.ERASE); canvas.requestFocusInWindow(); });
        toolbar.add(eraseBtn);

        JButton fillBtn = new JButton("Fill");
        fillBtn.addActionListener(e -> { canvas.setMode(CanvasPanel.Mode.FILL); canvas.requestFocusInWindow(); });
        toolbar.add(fillBtn);
        JButton undoBtn = new JButton("Undo");
        undoBtn.addActionListener(e -> {
            canvas.undo();
            canvas.requestFocusInWindow();
        });
        toolbar.add(undoBtn);
        toolbar.add(Box.createHorizontalGlue()); // Zarovná Clear tlačítko vpravo

        //Vymazání celého plátna
        JButton clearBtn = new JButton("Clear Canvas");
        clearBtn.setBackground(new Color(255, 200, 200));
        clearBtn.addActionListener(e -> {
            canvas.clearCanvas();
            canvas.requestFocusInWindow();
        });
        toolbar.add(clearBtn);

        return toolbar;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new App(800, 600));
    }
}