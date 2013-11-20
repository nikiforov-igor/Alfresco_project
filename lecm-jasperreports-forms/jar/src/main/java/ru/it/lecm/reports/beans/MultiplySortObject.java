package ru.it.lecm.reports.beans;

import java.util.ArrayList;
import java.util.List;

public class MultiplySortObject implements Comparable {
    private List<String> columnValues = new ArrayList<String>(5);
    private List<Boolean> sortDirs = new ArrayList<Boolean>(5);

    public void addSort(String value, boolean dir) {
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
            String value1 = this.columnValues.get(i);
            boolean sortDir1 = this.sortDirs.get(i);
            String value2 = null;
            try {
                value2 = o2.columnValues.get(i);
            } catch (IndexOutOfBoundsException ignored) {
            }
            if (value2 == null) {
                return 1;
            }
            int comp = value1.compareTo(value2);
            if (comp == 0) {
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
        for (String columnValue : columnValues) {
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
            String valueThis = this.columnValues.get(i);
            //boolean sortDirThis = this.sortDirs.get(i);

            String valueThat = null;
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