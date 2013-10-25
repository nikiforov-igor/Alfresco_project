package ru.it.lecm.documents.beans;

import java.io.Serializable;
import java.util.List;

/**
 * User: AIvkin
 * Date: 25.10.13
 * Time: 9:47
 */
public interface TableTotalRowCalculator {
	public Serializable calculate(List<Serializable> data);
}
