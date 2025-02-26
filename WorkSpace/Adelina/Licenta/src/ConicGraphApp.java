package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class ConicGraphApp extends JFrame {
    private JTextField equationInput;
    private GraphPanel graphPanel;
    private JLabel equationTypeLabel;

    public ConicGraphApp() {
        setTitle("Graficul conicelor");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        equationInput = new JTextField(20);
        JButton plotButton = new JButton("Desenează");
        equationTypeLabel = new JLabel("Tipul ecuației: ");

        inputPanel.add(new JLabel("f(x) = "));
        inputPanel.add(equationInput);
        inputPanel.add(plotButton);
        inputPanel.add(equationTypeLabel);

        graphPanel = new GraphPanel();

        plotButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String equation = equationInput.getText();
                if (!equation.isEmpty()) {
                    String type = determineConicType(equation);
                    equationTypeLabel.setText("Tipul ecuației: " + type);
                    graphPanel.setEquation(equation);
                    graphPanel.repaint();
                } else {
                    JOptionPane.showMessageDialog(ConicGraphApp.this, "Introduceți o ecuație validă!", "Eroare", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        add(inputPanel, BorderLayout.NORTH);
        add(graphPanel, BorderLayout.CENTER);
    }

    private String determineConicType(String equation) {
        // Îmbunătățit pentru a detecta cercurile (x^2 + y^2 = r^2)
        if (equation.matches("x\\^2\\s*\\+\\s*y\\^2\\s*=\\s*\\d+")) {
            return "Cerc";
        } else if (equation.contains("x^2") && equation.contains("y^2")) {
            return "Elipsă sau hiperbolă";
        } else if (equation.contains("x^2")) {
            return "Parabolă";
        } else if (equation.contains("y^2")) {
            return "Parabolă orizontală";
        } else {
            return "Dreaptă";
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ConicGraphApp app = new ConicGraphApp();
            app.setVisible(true);
        });
    }
}

class GraphPanel extends JPanel {
    private String equation;

    public void setEquation(String equation) {
        this.equation = equation;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        int width = getWidth();
        int height = getHeight();
        int originX = width / 2;
        int originY = height / 2;

        g2.setColor(Color.LIGHT_GRAY);
        g2.drawLine(0, originY, width, originY);
        g2.drawLine(originX, 0, originX, height);

        if (equation != null && !equation.isEmpty()) {
            g2.setColor(Color.RED);
            try {
                // Verificăm dacă ecuația este un cerc (x^2 + y^2 = r^2)
                if (equation.matches("x\\^2\\s*\\+\\s*y\\^2\\s*=\\s*\\d+")) {
                    // Extragem raza cercului
                    String radiusStr = equation.replaceAll("x\\^2\\s*\\+\\s*y\\^2\\s*=\\s*", "");
                    double radius = Math.sqrt(Double.parseDouble(radiusStr));

                    // Desenăm cercul
                    Ellipse2D.Double circle = new Ellipse2D.Double(originX - radius * 40, originY - radius * 40, radius * 80, radius * 80);
                    g2.draw(circle);
                } else {
                    // Calcul grafic pentru alte ecuații
                    double scale = 40;
                    Expression expression = new ExpressionBuilder(equation).variable("x").build();
                    for (double x = -width / 2.0 / scale; x < width / 2.0 / scale; x += 0.01) {
                        double y = expression.setVariable("x", x).evaluate();
                        double nextY = expression.setVariable("x", x + 0.01).evaluate();

                        if (Double.isNaN(y) || Double.isNaN(nextY)) continue;

                        int pixelX1 = (int) (originX + x * scale);
                        int pixelY1 = (int) (originY - y * scale);
                        int pixelX2 = (int) (originX + (x + 0.01) * scale);
                        int pixelY2 = (int) (originY - nextY * scale);

                        g2.draw(new Line2D.Double(pixelX1, pixelY1, pixelX2, pixelY2));
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Eroare la interpretarea ecuației: " + e.getMessage(), "Eroare", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}