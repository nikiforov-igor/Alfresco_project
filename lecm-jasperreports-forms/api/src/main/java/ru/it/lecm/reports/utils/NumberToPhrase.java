package ru.it.lecm.reports.utils;

/**
 * User: pmelnikov
 * Date: 12.12.14
 * Time: 16:53
 */
public class NumberToPhrase {

    private static final String[] dek1 = {"", " од", " дв", " три", " четыре", " пять", " шесть", " семь", " восемь", " девять", " десять", " одиннадцать", " двенадцать", " тринадцать", " четырнадцать", " пятнадцать", " шестнадцать", " семнадцать", " восемнадцать", " девятнадцать"};
    private static final String[] dek2 = {"", "", " двадцать", " тридцать", " сорок", " пятьдесят", " шестьдесят", " семьдесят", " восемьдесят", " девяносто"};
    private static final String[] dek3 = {"", " сто", " двести", " триста", " четыреста", " пятьсот", " шестьсот", " семьсот", " восемьсот", " девятьсот"};
    private static final String[] th = {"", "", " тысяч", " миллион", " миллиард", " триллион", " квадрилион", " квинтилион"};

    public static String numPhrase(int value, boolean IsMale) {
        if (value == 0) return "Ноль";
        String str = "";
        for (byte thc = 1; value > 0; thc++) {
            int gr = value % 1000;
            value = (value - gr) / 1000;
            if (gr > 0) {
                byte d3 = (byte) ((gr - gr % 100) / 100);
                byte d1 = (byte) (gr % 10);
                byte d2 = (byte) ((gr - d3 * 100 - d1) / 10);
                if (d2 == 1) d1 += (byte) 10;
                boolean isMale = (thc > 2) || ((thc == 1) && IsMale);
                str = dek3[d3] + dek2[d2] + dek1[d1] + EndDek1(d1, isMale) + th[thc] + EndTh(thc, d1) + str;
            }
        }
        str = str.substring(1, 2).toUpperCase() + str.substring(2);
        return str;
    }

    private static String EndTh(byte ThNum, byte Dek) {
        boolean In234 = ((Dek >= 2) && (Dek <= 4));
        boolean More4 = ((Dek > 4) || (Dek == 0));
        if (((ThNum > 2) && In234) || ((ThNum == 2) && (Dek == 1))) return "а";
        else if ((ThNum > 2) && More4) return "ов";
        else if ((ThNum == 2) && In234) return "и";
        else return "";
    }

    private static String EndDek1(byte Dek, boolean IsMale) {
        if ((Dek > 2) || (Dek == 0)) return "";
        else if (Dek == 1) {
            if (IsMale) return "ин";
            else return "на";
        } else {
            if (IsMale) return "а";
            else return "е";
        }
    }
}