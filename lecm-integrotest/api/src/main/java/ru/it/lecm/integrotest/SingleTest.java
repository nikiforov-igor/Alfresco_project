package ru.it.lecm.integrotest;

import java.util.ArrayList;
import java.util.List;

public class SingleTest {

	private List<RunAction> actions;

	/**
	 * Вернуть последовательность действий, составляющих один полный тест.
	 * @return
	 */
	public List<RunAction> getActions() {
		return this.actions;
	}

	/**
	 * Задать последовательность действий, составляющих один полный тест.
	 * @param list
	 */
	public void setActions(List<RunAction> list) {
		this.actions = list;
	}

	public void setAddAction(RunAction item) {
		if (actions == null)
			actions = new ArrayList<RunAction>();
		actions.add(item);
	}

//	void loadConfig(Reader config);
}
