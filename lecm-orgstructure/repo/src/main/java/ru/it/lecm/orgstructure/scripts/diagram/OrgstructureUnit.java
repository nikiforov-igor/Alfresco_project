package ru.it.lecm.orgstructure.scripts.diagram;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;

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
        Font fontInc = new Font(font.getFamily(), font.getStyle(), font.getSize() + 7);
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

        return width + 12;
    }

    public double getHeight() {
        Font font = FONT;
        Font fontInc = new Font(font.getFamily(), font.getStyle(), font.getSize() + 7);
        BufferedImage bi = new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB);

        FontMetrics fm = bi.getGraphics().getFontMetrics(fontInc);
        int heightInc = fm.getHeight();

        fm = bi.getGraphics().getFontMetrics(font);
        int height = fm.getHeight();

        int result = heightInc;

        if (boss != null) {
            result += height;
        }

        result += employees.size() * height;

        return result + 12;
    }
}
