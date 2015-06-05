package ru.it.lecm.orgstructure.scripts.diagram;

import com.mxgraph.canvas.mxSvgCanvas;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxCellState;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

/**
 * User: pmelnikov
 * Date: 28.05.13
 * Time: 13:54
 */
public class OrgstructureUnit {

    public static final Font FONT = new Font("Arial", Font.PLAIN, 12);

    private String title = null;
    private String boss = null;
    private ArrayList<String> employees = new ArrayList<String>();

    private static final int MAX_WIDTH = 320;

    public OrgstructureUnit(String title) {
        this.title = title;
    }

    public void setBoss(String boss) {
        this.boss = boss;
    }

    public void addEmployee(String employee) {
        employees.add(employee);
    }


    public void draw(mxCellState state, mxSvgCanvas canvas) {
        mxRectangle bounds = state.getPerimeterBounds();
        Point translate = canvas.getTranslate();
        int x = (int)bounds.getX() + translate.x;
        int y = (int)bounds.getY() + translate.y;
        int w = (int) bounds.getWidth();
        int h = (int) bounds.getHeight();

        Map style = state.getStyle();
        style.put(mxConstants.STYLE_FILLCOLOR, "#FFFFFF");
        style.put(mxConstants.STYLE_STROKECOLOR, "#000000");
        canvas.drawShape(x, y, w, h, style);

        x += 6;
        y += 6;

        Font font = FONT;
        Font fontInc = new Font(FONT.getFamily(), FONT.getStyle(), FONT.getSize() + 6);
        Font fontBoss = new Font(FONT.getFamily(), Font.BOLD, FONT.getSize());

        y += drawText(title, canvas, x, y, fontInc);

        if (boss != null || employees.size() > 0) {
            y += 6;
            List<mxPoint> points = new ArrayList<>();
            points.add(new mxPoint(x, y));
            points.add(new mxPoint(x + MAX_WIDTH, y));
            canvas.drawLine(points, style);
            y += 6;
        }

        if (boss != null) {
            y += drawText(boss, canvas, x, y, fontBoss);
        }
        for (String employee : employees) {
            y += drawText(employee, canvas, x, y, font);
        }
    }

    private int drawText(String word, mxSvgCanvas canvas, int x, int y, Font font) {
        BufferedImage bi = new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB);
        FontMetrics fm = bi.getGraphics().getFontMetrics(font);

        Map<String, Object> style = new HashMap<>();
        style.put(mxConstants.STYLE_FONTFAMILY, font.getFamily());
        style.put(mxConstants.STYLE_FONTSIZE, font.getSize());
        style.put(mxConstants.STYLE_ALIGN, mxConstants.ALIGN_LEFT);
        style.put(mxConstants.STYLE_FONTSTYLE, font.getStyle());
        int height = 0;
        if (fm.stringWidth(word) > MAX_WIDTH) {
            StringTokenizer st = new StringTokenizer(word, " -", true);
            String text = "";
            while (st.hasMoreTokens()) {
                String token = st.nextToken();
                int width = fm.stringWidth(text + token);
                if (width > MAX_WIDTH) {
                    canvas.drawText(text, x, y, MAX_WIDTH, fm.getHeight(), style);
                    height += fm.getHeight();
                    y += fm.getHeight();
                    text = "";
                }
                text += token;
            }
            if (!text.equals("")) {
                canvas.drawText(text, x, y, MAX_WIDTH, fm.getHeight(), style);
                height += fm.getHeight();
            }
        } else {
            canvas.drawText(word, x, y, MAX_WIDTH, fm.getHeight(), style);
            height += fm.getHeight();
        }
        return height;
    }

    public double getWidth() {
        return MAX_WIDTH + 12;
    }

    public double getHeight() {
        Font font = FONT;
        Font fontInc = new Font(FONT.getFamily(), FONT.getStyle(), FONT.getSize() + 6);
        Font fontBoss = new Font(FONT.getFamily(), Font.BOLD, FONT.getSize());
        BufferedImage bi = new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB);

        FontMetrics fm = bi.getGraphics().getFontMetrics(fontInc);
        int result = calculateHeight(title, MAX_WIDTH, fm);

        if (boss != null || employees.size() > 0) {
            fm = bi.getGraphics().getFontMetrics(font);
            result += calculateHeight("a", MAX_WIDTH, fm) / 2;
        }

        if (boss != null) {
            fm = bi.getGraphics().getFontMetrics(fontBoss);
            result += calculateHeight(boss, MAX_WIDTH, fm);
        }

        if (employees.size() > 0) {
            fm = bi.getGraphics().getFontMetrics(font);
            for (String employee : employees) {
                result += calculateHeight(employee, MAX_WIDTH, fm);
            }
        }

        return result + 12;
    }

    private int calculateHeight(String word, int maxWidth, FontMetrics metrics) {
        int lineHeight = metrics.getHeight();
        int result = lineHeight;
        StringTokenizer st = new StringTokenizer(word, " -", true);
        int width = 0;
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            width += metrics.stringWidth(token);
            if (width > maxWidth) {
                result += lineHeight;
                width = metrics.stringWidth(token);
            }
        }

        return result;
    }

}
