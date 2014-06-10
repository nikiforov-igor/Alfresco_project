package ru.it.lecm.reports.api;

import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.reports.api.model.ParameterType;

import java.util.Arrays;
import java.util.List;

public interface DataFilter {

    public enum FilterType {
        EQUAL {
            @Override
            public boolean isOk(Object testValue, List<Object> values) {
                if (values == null || values.isEmpty()) {
                    // не с чем сравнивать - значит подходит
                    return true;
                }
                if (testValue != null) {
                    for (Object value : values) {
                        if (testValue.equals(value)){ // равен хотя бы одному из списка
                            return true;
                        }
                    }
                    return false;
                } else {
                    return values.get(0) == null;
                }
            }
        },
        NOT_EQUAL {
            @Override
            public boolean isOk(Object testValue, List<Object> values) {
                return !FilterType.EQUAL.isOk(testValue, values);
            }
        },
        IN_RANGE {
            @Override
            public boolean isOk(Object testValue, List<Object> values) {
                if (values == null || values.isEmpty()) {
                    // не с чем сравнивать - значит подходит
                    return true;
                }
                if (testValue != null) {
                    Object range1 = values.get(0);
                    Object range2 = values.size() > 1 ? values.get(1) : null;

                    return FilterType.GREATER_OR_EQUAL.isOk(testValue, Arrays.asList(range1))
                            && FilterType.LESSER_OR_EQUAL.isOk(testValue, Arrays.asList(range2));
                } else {
                    return false; // пустые значения - не попадают в любой диапозон
                }
            }
        },
        GREATER {
            @Override
            public boolean isOk(Object testValue, List<Object> values) {
                if (values == null || values.isEmpty()) {
                    return true;
                }
                Object rangeValue = values.get(0);
                if (rangeValue == null) {
                    return true;
                }
                if (testValue != null &&
                        testValue instanceof Comparable
                        && rangeValue instanceof Comparable) {
                    int compare = ((Comparable) testValue).compareTo(rangeValue);
                    return compare > 0;
                } else {
                    return false; // пустые значения - не попадают в любой диапозон
                }
            }
        },
        GREATER_OR_EQUAL {
            @Override
            public boolean isOk(Object testValue, List<Object> values) {
                if (values == null || values.isEmpty()) {
                    return true;
                }
                Object rangeValue = values.get(0);
                if (rangeValue == null) {
                    return true;
                }
                if (testValue != null &&
                        testValue instanceof Comparable
                        && rangeValue instanceof Comparable) {
                    int compare = ((Comparable) testValue).compareTo(rangeValue);
                    return compare >= 0;
                } else {
                    return false; // пустые значения - не попадают в любой диапозон
                }
            }
        },
        LESSER {
            @Override
            public boolean isOk(Object testValue, List<Object> values) {
                if (values == null || values.isEmpty()) {
                    return true;
                }
                Object rangeValue = values.get(0);
                if (rangeValue == null) {
                    return true;
                }
                if (testValue != null &&
                        testValue instanceof Comparable
                        && rangeValue instanceof Comparable) {
                    int compare = ((Comparable) testValue).compareTo(rangeValue);
                    return compare < 0;
                } else {
                    return false; // пустые значения - не попадают в любой диапозон
                }
            }
        },
        LESSER_OR_EQUAL {
            @Override
            public boolean isOk(Object testValue, List<Object> values) {
                if (values == null || values.isEmpty()) {
                    return true;
                }
                Object rangeValue = values.get(0);
                if (rangeValue == null) {
                    return true;
                }
                if (testValue != null &&
                        testValue instanceof Comparable
                        && rangeValue instanceof Comparable) {
                    int compare = ((Comparable) testValue).compareTo(rangeValue);
                    return compare <= 0;
                } else {
                    return false; // пустые значения - не попадают в любой диапозон
                }
            }
        };

        public abstract boolean isOk(Object testValue, List<Object> values);

        public static FilterType findFilter(String filterName) {
            if (filterName != null) {
                // поиск точного соот-вия
                for (FilterType v : values()) {
                    if (filterName.equalsIgnoreCase(v.name())) {
                        return v;
                    }
                }
            }
            return null;
        }

        public static FilterType getFilterByParamType(ParameterType.Type paramType) {
            switch (paramType) {
                case VALUE:{
                }
                case LIST:{
                    return FilterType.EQUAL;
                }
                case RANGE:{
                    return FilterType.IN_RANGE;
                }
            }
            return null;
        }
    }

	/**
	 * Проверить, выполняются ли для указанного узла условия фильтра по ассоциациям
	 */
	boolean isOk(NodeRef id); 

    public class DataFilterDesc {
        public final String formatString;
        public final FilterType filter;
        public final List<Object> values;

        public DataFilterDesc(FilterType filter, String formatString, List<Object> values) {
            super();
            this.filter = filter;
            this.formatString = formatString;
            this.values = values;
        }

        @Override
        public String toString() {
            return "DataFilterDesc[ " + ", filter=" + filter + ", formatString=" + formatString + " ]";
        }
    }
}
