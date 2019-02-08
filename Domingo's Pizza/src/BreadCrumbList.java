import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.util.Optional;
import javax.swing.plaf.LayerUI;


public class BreadCrumbList {
    public  ArrayList<AbstractButton> buttons =new ArrayList<>();

    public  Component makeBreadcrumbList(List<String> list) {
        Container p = makeContainer(5 + 1);
        ButtonGroup bg = new ButtonGroup();
        list.forEach(title -> {
            AbstractButton b = makeButton(null, new TreePath(title), new Color(255,216,140));
            p.add(b);
            p.setSize(new Dimension(1500, 50));
            bg.add(b);
            buttons.add(b);
        });
        return p;
    }
    private static int getMaxLength(List<String> list){
        int max=0;
        for(String s: list){
            if(s.length()>max){
                max=s.length();
            }
        }
        return max;
    }
    public static AbstractButton makeButton(JTree tree, TreePath path, Color color) {
        AbstractButton b = new JRadioButton(path.getLastPathComponent().toString()) {
            @Override public boolean contains(int x, int y) {
                Icon i = getIcon();
                //check if it's an arrow button, if it is, return the contains method of the Shape. if not return the super method
                if (i instanceof ArrowToggleButtonBarCellIcon) {
                    Shape s = ((ArrowToggleButtonBarCellIcon) i).getShape();
                    if (Objects.nonNull(s)) {
                        return s.contains(x, y);
                    }
                }
                return super.contains(x, y);
            }
        };
        //if you have a tree object (which I dont in this case)
        if (Objects.nonNull(tree)) {
            b.addActionListener(e -> {
                JRadioButton r = (JRadioButton) e.getSource();
                tree.setSelectionPath(path);
                r.setSelected(true);
            });
        }
        b.setIcon(new ArrowToggleButtonBarCellIcon());
        b.setContentAreaFilled(false);
        b.setFont(new Font("times new roman", Font.BOLD, 22));
        b.setBorder(BorderFactory.createEmptyBorder());
        b.setVerticalAlignment(SwingConstants.CENTER);
        b.setVerticalTextPosition(SwingConstants.CENTER);
        b.setHorizontalAlignment(SwingConstants.CENTER);
        b.setHorizontalTextPosition(SwingConstants.CENTER);
        b.setFocusPainted(false);
        b.setOpaque(false);
        b.setBackground(color);
        return b;
    }
    public static Container makeContainer(int overlap) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEADING, overlap, 0)) {
            @Override public boolean isOptimizedDrawingEnabled() {
                return false;
            }
        };
        p.setBorder(BorderFactory.createEmptyBorder(4, overlap + 4, 4, 4));
        p.setOpaque(false);
        return p;
    }
}
class ArrowToggleButtonBarCellIcon implements Icon {
    public static final int TH = 20; // The height of a triangle
    private static final int HEIGHT = TH * 2 + 1;
    private static final int WIDTH =213;
    private Shape shape;


    public Shape getShape() {
        return shape;
    }

    protected Shape makeShape(Container parent, Component c, int x, int y) {
        int w = c.getWidth() - 1;
        int h = c.getHeight() - 1;
        int h2 = (int) (h * .5 + .5);
        int w2 = TH;
        Path2D p = new Path2D.Double();
        p.moveTo(0, 0);
        p.lineTo(w - w2, 0);
        p.lineTo(w, h2);
        p.lineTo(w - w2, h);
        p.lineTo(0, h);
        if (c != parent.getComponent(0)) {
            p.lineTo(w2, h2);
        }
        p.closePath();
        return AffineTransform.getTranslateInstance(x, y).createTransformedShape(p);
    }

    @Override public void paintIcon(Component c, Graphics g, int x, int y) {
        Container parent = c.getParent();
        if (Objects.isNull(parent)) {
            return;
        }
        shape = makeShape(parent, c, x, y);

        Color bgc = new Color(255,216,140);
        Color borderColor = Color.GRAY.brighter();
        if (c instanceof AbstractButton) {
            ButtonModel m = ((AbstractButton) c).getModel();
            if (m.isSelected() || m.isRollover()) {
                bgc = c.getBackground();
                borderColor = Color.GRAY;
            }
        }
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setPaint(bgc);
        g2.fill(shape);
        g2.setPaint(borderColor);
        g2.draw(shape);
        g2.dispose();
    }

    @Override public int getIconWidth() {
        return WIDTH;
    }

    @Override public int getIconHeight() {
        return HEIGHT;
    }
}

class BreadcrumbLayerUI<V extends Component> extends LayerUI<V> {
    private Shape shape;

    @Override
    public void paint(Graphics g, JComponent c) {
        super.paint(g, c);
        Optional.ofNullable(shape).ifPresent(s -> {
            Graphics2D g2 = (Graphics2D) g.create();
            // g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(Color.GRAY);
            g2.draw(shape);
            g2.dispose();
        });
    }

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        if (c instanceof JLayer) {
            ((JLayer<?>) c).setLayerEventMask(AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
        }
    }

    @Override
    public void uninstallUI(JComponent c) {
        if (c instanceof JLayer) {
            ((JLayer<?>) c).setLayerEventMask(0);
        }
        super.uninstallUI(c);
    }

    private void update(MouseEvent e, JLayer<? extends V> l) {
        Shape s = null;
        switch (e.getID()) {
            case MouseEvent.MOUSE_ENTERED:
            case MouseEvent.MOUSE_MOVED:
                Component c = e.getComponent();
                if (c instanceof AbstractButton) {
                    AbstractButton b = (AbstractButton) c;
                    if (b.getIcon() instanceof ArrowToggleButtonBarCellIcon) {
                        ArrowToggleButtonBarCellIcon icon = (ArrowToggleButtonBarCellIcon) b.getIcon();
                        Rectangle r = c.getBounds();
                        AffineTransform at = AffineTransform.getTranslateInstance(r.x, r.y);
                        s = at.createTransformedShape(icon.getShape());
                    }
                }
                break;
            default:
                break;
        }
        if (!Objects.equals(s, shape)) {
            shape = s;
            l.getView().repaint();
        }
    }
}
