package ru.it.lecm.arm.beans.filters;

import java.util.List;

/**
 * User: DBashmakov
 */
public interface ArmDocumenstFilter {
    public String getQuery(Object armNode, String params, List<String> args);
}
