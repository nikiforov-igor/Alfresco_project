package ru.it.lecm.documents;

import ru.it.lecm.documents.beans.DocumentTableService;

import java.util.Map;

/**
 * User: AIvkin
 * Date: 25.10.13
 * Time: 11:26
 */
public class TableTotalRowCalculatorInitializer {
	protected DocumentTableService documentTableService;
	private Map<String, TableTotalRowCalculator> calculators;

	public void setDocumentTableService(DocumentTableService documentTableService) {
		this.documentTableService = documentTableService;
	}

	protected TableTotalRowCalculatorInitializer(Map<String, TableTotalRowCalculator> calculators) {
		this.calculators = calculators;
	}

	public void init() {
		if (calculators != null) {
			for (Map.Entry<String, TableTotalRowCalculator> entry : calculators.entrySet()) {
				documentTableService.addCalculator(entry.getKey(), entry.getValue());
			}
		}
	}
}
