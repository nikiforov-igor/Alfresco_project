package ru.it.lecm.integrotest.actions;

import ru.it.lecm.integrotest.RunModifier;

/**
 * Действие для управления последовательным выполнением шагов тестирования:
 *    - для завершения теста (этапа тестирования) (удобно при отладке теста), 
 *    - пропуска следующего шага-действия.
 * 
 * Предполагается использование там, где надо досрочно завершить тест с 
 * "положительным прохожденнием" - например, для быстрого комментирования бинов
 * в service-context.xml - вставляя такой класс мы отсекаем выполнение всех 
 * нижележащих действий.
 * 
 * + develop: возможность добавлять проверки custom-условий завершения.
 * 
 * @author rabdullin
 */
public class RunModifierImpl
		extends LecmActionBase
		implements RunModifier
{
	/**
	 * Действие по-умолчанию для управления потоком выполнения шагов теста.
	 */
	public static final TodoStatus DEFAULT_TODO = TodoStatus.doBreak;

	private TodoStatus todoStatus = DEFAULT_TODO;


	@Override
	public TodoStatus getTodoStatus() {
		return (this.todoStatus != null) ? this.todoStatus : DEFAULT_TODO;
	}

	/**
	 * Задать поведение исполнителя тество (ExecutorBean) после выполнения данного действия (this)  
	 * @param value значение статуса, если null - то будет использоваться DEFAULT_TODO. 
	 */
	public void setTodoStatus(TodoStatus value) {
		this.todoStatus = (value == null) ? DEFAULT_TODO : value;
		logger.info("todoStatus set to "+ value);
	}

	@Override
	public void run() {
		// тут можно выполнять проверку доп условий, проверить контекст и поднять нужное исключение ...
		// пока пусто, т.к. вся работа выполняется методом getTodoStatus
		logger.info("todoStatus is "+ this.getTodoStatus() );
	}

}
