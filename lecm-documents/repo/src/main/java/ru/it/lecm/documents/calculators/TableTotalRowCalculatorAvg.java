package ru.it.lecm.documents.calculators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.documents.beans.TableTotalRowCalculator;

import java.io.Serializable;
import java.util.List;

/**
 * User: AIvkin
 * Date: 25.10.13
 * Time: 11:05
 */
public class TableTotalRowCalculatorAvg implements TableTotalRowCalculator {
	private final static Logger logger = LoggerFactory.getLogger(TableTotalRowCalculatorAvg.class);

	@Override
	public Serializable calculate(List<Serializable> data) {
		if (data != null && data.size() > 0) {
			if (data.get(0) instanceof Number) {
				return calculateNumber(data);
			} else {
				logger.warn("Not supported value '" + data.get(0) + "' for calculator " + this.getClass());
			}
		}
		return null;
	}

	public Number calculateNumber(List<Serializable> data) {
		double result = 0;
		int k = 0;
		for (Serializable value: data) {
			if (value instanceof Number) {
				result += ((Number) value).doubleValue();
				k++;
			} else {
				logger.warn("Not supported value '" + data.get(0) + "' for calculator " + this.getClass());
			}
		}
		if (k > 0) {
			return result / k;
		} else {
			return 0;
		}
	}
}
