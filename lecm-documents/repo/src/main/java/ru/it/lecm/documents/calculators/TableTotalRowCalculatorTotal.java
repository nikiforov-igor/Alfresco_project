package ru.it.lecm.documents.calculators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.documents.beans.TableTotalRowCalculator;

import java.io.Serializable;
import java.util.List;

/**
 * User: AIvkin
 * Date: 25.10.13
 * Time: 9:55
 */
public class TableTotalRowCalculatorTotal implements TableTotalRowCalculator {
	private final static Logger logger = LoggerFactory.getLogger(TableTotalRowCalculatorTotal.class);

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
		for (Serializable dataNum: data) {
			if (dataNum instanceof Number) {
				result += ((Number) dataNum).doubleValue();
			} else {
				logger.warn("Not supported value '" + data.get(0) + "' for calculator " + this.getClass());
			}
		}
		return result;
	}
}
