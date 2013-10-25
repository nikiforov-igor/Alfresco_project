package ru.it.lecm.documents.calculators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.documents.beans.TableTotalRowCalculator;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * User: AIvkin
 * Date: 25.10.13
 * Time: 10:37
 */
public class TableTotalRowCalculatorMin implements TableTotalRowCalculator {
	private final static Logger logger = LoggerFactory.getLogger(TableTotalRowCalculatorTotal.class);

	@Override
	public Serializable calculate(List<Serializable> data) {
		if (data != null && data.size() > 0) {
			if (data.get(0) instanceof Number) {
				return calculateNumber(data);
			} else if (data.get(0) instanceof Date) {
				return calculateDate(data);
			} else {
				logger.warn("Not supported value '" + data.get(0) + "' for calculator " + this.getClass());
			}
		}
		return null;
	}

	public Number calculateNumber(List<Serializable> data) {
		Double result = null;
		for (Serializable obj: data) {
			if (obj instanceof Number) {
				Double objNum = ((Number) obj).doubleValue();
				if (result == null || objNum < result) {
					result = objNum;
				}
			} else {
				logger.warn("Not supported value '" + data.get(0) + "' for calculator " + this.getClass());
			}
		}
		return result;
	}

	public Date calculateDate(List<Serializable> data) {
		Date result = null;
		for (Serializable obj: data) {
			if (obj instanceof Date) {
				Date objDate = (Date) obj;
				if (result == null || objDate.before(result)) {
					result = objDate;
				}
			} else {
				logger.warn("Not supported value '" + data.get(0) + "' for calculator " + this.getClass());
			}
		}
		return result;
	}
}
