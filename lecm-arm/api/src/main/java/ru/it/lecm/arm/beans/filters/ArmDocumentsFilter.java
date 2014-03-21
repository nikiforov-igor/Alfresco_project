package ru.it.lecm.arm.beans.filters;

import java.util.List;

/**
 * User: DBashmakov
 */
public interface ArmDocumentsFilter {
    public String getQuery(String params, List<String> args);
}
