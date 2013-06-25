package ru.it.lecm.reports.api.model;

import java.util.List;

public interface DataSourceDescriptor extends Mnemonicable, L18able {

	List<ColumnDescriptor> getColumns();

}
