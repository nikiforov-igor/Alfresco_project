package ru.it.lecm.orgstructure.scripts.diagram;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

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


    public String getHtml() {

        String result = "<html>";
        result += "<font size=+1>" + title + "</font>";
        if (boss != null || employees.size() > 0) {
            result += "<hr>";
        }
        if (boss != null) {
            result += "<b>"+ boss +"</b>";
        }

        Iterator<String> it = employees.iterator();
        if (it.hasNext()) {
            result += "<br>";
        }
        while (it.hasNext()) {
            result += it.next();
            if (it.hasNext()) {
                result += "<br>";
            }
        }
        result += "</html>";
        return result;
    }

    public ArrayList<String> getEmployees() {
        return employees;
    }

    public double getWidth() {
        BufferedImage bi = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        Font font = FONT;
        Font fontInc = new Font(font.getFamily(), font.getStyle(), font.getSize() + 6);
        Font fontBoss = new Font(font.getFamily(), Font.BOLD, font.getSize());
        FontMetrics fm = bi.getGraphics().getFontMetrics(fontInc);
        int width = fm.stringWidth(title);

        fm = bi.getGraphics().getFontMetrics(fontBoss);
        if (boss != null && fm.stringWidth(boss) > width) {
            width = fm.stringWidth(boss);
        }

        fm = bi.getGraphics().getFontMetrics(font);
        for (String employee : employees) {
            if (fm.stringWidth(employee) > width) {
                width = fm.stringWidth(employee);
            }
        }

        return width < MAX_WIDTH ? width + 12 : MAX_WIDTH + 12;
    }

    public double getHeight() {
        Font font = FONT;
        Font fontInc = new Font(font.getFamily(), font.getStyle(), font.getSize() + 6);
        Font fontBoss = new Font(font.getFamily(), Font.BOLD, font.getSize());
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
