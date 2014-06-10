package ru.it.lecm.integrotest;

/**
 * Интерфейс для обозначения Actions-классов, которые модифицируют поток выполнения.
 * Например, для завершения этапа на текущем шаге, или для пропуска следующего шага.
 * @author rabdullin
 */
public interface RunModifier {

	/**
	 * @return вернуть последующее действие (для Исполнителя тестов ExecutorBean)
	 */
	TodoStatus getTodoStatus();

	/**
	 * Статусы, которые влияют на выполнение следующих шагов этапа (теста).
	 */
	public enum TodoStatus {
		doNormal, 	// продолжать выполнение
		doBreak,	// завершить выполнение этапа
		doSkipNext	// пропустить следующий шаг
	}
}
