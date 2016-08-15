package ru.it.lecm.regnumbers.counter;

import org.alfresco.service.cmr.repository.NodeRef;

import java.util.List;

/**
 * Created by dkuchurkin on 15.08.2016.
 */
public interface CounterFactory {
    Counter getCounter(CounterType type, NodeRef documentRef, String tag);

    void initTaggedCounters(String documentType, List<String> tags);
}
