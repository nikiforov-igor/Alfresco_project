package ru.it.lecm.reports.beans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class MultiplySortObject implements Comparable {
    private static final Logger logger = LoggerFactory.getLogger(MultiplySortObject.class);

    private List<Comparable> columnValues = new ArrayList<Comparable>(5);
    private List<Boolean> sortDirs = new ArrayList<Boolean>(5);

    public void addSort(Comparable value, boolean dir) {
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
            Comparable value1 = this.columnValues.get(i);
            boolean sortDir1 = this.sortDirs.get(i);
            Comparable value2 = null;
            try {
                value2 = o2.columnValues.get(i);
            } catch (IndexOutOfBoundsException ignored) {
            }
            if (value1 == null && value2 == null) {
                return 0;
            } else if (value1 == null) {
                return (sortDir1 ? -1 : 1);
            } else if (value2 == null) {
                return (sortDir1 ? 1 : -1);
            }

            int comp;
            try {
                comp = value1.compareTo(value2);
            } catch (ClassCastException cEx) {
                logger.error(cEx.getMessage(), cEx);
                comp = -1; //TODO
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