package ru.it.lecm.documents.beans;

import ru.it.lecm.documents.TableTotalRowCalculator;
import ru.it.lecm.documents.TableTotalRowCalculatorInitializer;

import java.util.Map;

/**
 * User: AIvkin
 * Date: 25.10.13
 * Time: 11:32
 */
public class TableTotalRowCalculatorInitializerBean extends TableTotalRowCalculatorInitializer {
	public TableTotalRowCalculatorInitializerBean(Map<String, TableTotalRowCalculator> calculators) {
		super(calculators);
	}
}
