package ru.it.lecm.reports.api.model;

import ru.it.lecm.reports.model.impl.NamedValue;

import java.util.Set;

public interface FlagsExtendable {

	Set<NamedValue> flags();
}
