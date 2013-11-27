package ru.it.lecm.reports.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MultiplySortObject implements Comparable {
    private List<Object> columnValues = new ArrayList<Object>(5);
    private List<Boolean> sortDirs = new ArrayList<Boolean>(5);

    public void addSort(Object value, boolean dir) {
        columnValues.add(value);
        sortDirs.add(dir);
    }

    @Override
    public int compareTo(Object o) {
        if (o == null || !(o instanceof MultiplySortObject)) {
            return -1;
        }

        MultiplySortObject o2 = (MultiplySortObject) o;

        for (int i = 0; i < this.columnValues.size(); i++) {
            Object value1 = this.columnValues.get(i);
            boolean sortDir1 = this.sortDirs.get(i);
            Object value2 = null;
            try {
                value2 = o2.columnValues.get(i);
            } catch (IndexOutOfBoundsException ignored) {
            }
            if (value1 == null && value2 == null) {
                return 0;
            } else if (value1 == null) {
                return -1;
            } else if (value2 == null) {
                return 1;
            }

            int comp = 0;

            if (value1 instanceof String) {
                comp = value1.toString().compareTo(value2.toString());
            } else if (value1 instanceof Date) {
                comp = ((Date) value1).compareTo((Date) value2);
            } else if (value1 instanceof Integer) {
                comp = ((Integer) value1).compareTo((Integer) value2);
            } else if (value1 instanceof Float) {
                comp = ((Float) value1).compareTo((Float) value2);
            } else if (value1 instanceof Boolean) {
                comp = ((Boolean) value1).compareTo((Boolean) value2);
            }

            if (comp == 0) { // первые значения равны - переходим к следующим
                continue;
            }

            if (sortDir1) {
                return comp;
            } else {
                return -comp;
            }
        }
        return 0;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        for (Object columnValue : columnValues) {
            result = prime * result + ((columnValue == null) ? 0 : columnValue.hashCode());
        }
        return result;
    }

    @Override
    public boolean equals(Object aThat) {
        if (this == aThat) return true;
        if (!(aThat instanceof MultiplySortObject)) return false;

        MultiplySortObject that = (MultiplySortObject) aThat;

        for (int i = 0; i < this.columnValues.size(); i++) {
            Object valueThis = this.columnValues.get(i);
            //boolean sortDirThis = this.sortDirs.get(i);

            Object valueThat = null;
            //boolean sortDirThat = false;
            try {
                valueThat = that.columnValues.get(i);
                //sortDirThat = that.sortDirs.get(i);
            } catch (IndexOutOfBoundsException ignored) {
            }
            if (!valueThis.equals(valueThat)) {
                return false;
            }
                /*if (!(sortDirThis == sortDirThat)){
                    return false;
                }*/
        }
        return true;
    }
}