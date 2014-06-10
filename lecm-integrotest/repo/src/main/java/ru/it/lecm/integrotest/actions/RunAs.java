package ru.it.lecm.integrotest.actions;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.service.cmr.repository.NodeRef;

import ru.it.lecm.integrotest.RunAction;
import ru.it.lecm.integrotest.utils.Utils;

/**
 * Для запуска других бинов от имени указанного пользователя.
 * @author rabdullin
 * 
 * Пример выполнения действия Abc от имени БольшогоБрата:
				<bean class="ru.it.lecm.integrotest.actions.RunAs">
					<property name="byUser" value="БольшойБрат" />
					<property name="todo">
						<bean class="ru.it.lecm.integrotest.actions.Abc">
							<property name="field1" value="..." />
							<property name="field2" value="..." />
						</bean>
					</property>
				</bean>
 *
 */
public class RunAs extends LecmActionBase {

	final static public String ASSYSTEM = "SYSTEM";

	private String byUser;
	private RunAction todo;

	@Override
	public String toString() {
		return "run by '"+ Utils.coalesce( getByUser(), "DON'T CHANGE") + "'"
				+ " do "+ ( (getTodo() == null) ? "NULL" : getTodo().getClass().getName() + " " + getTodo().toString() )
				+ "]";
	}

	/**
	 * @return пользователь, от имени котрого выполнить операцию вложенного бина
	 */
	public String getByUser() {
		return byUser;
	}

	/**
	 * @param value пользователь, от имени котрого выполнить операцию вложенного бина
	 * значение null или пусто == НЕ МЕНЯТЬ КОНТЕКС,
	 * для запуска от имени системы значение "system" (регистр не важен).
	 */
	public void setByUser(String value) {
		this.byUser = ( value == null || "NULL".equalsIgnoreCase(value) || value.trim().length() == 0) ? null : value.trim();
	}

	/**
	 * @return Вложенное действие для выполнения
	 */
	public RunAction getTodo() {
		return todo;
	}

	/**
	 * @param value Вложенное действие для выполнения
	 */
	public void setTodo(RunAction value) {
		this.todo = value;
	}

	@Override
	public void run() {

		logger.info( toString() );

		if (getTodo() == null) {
			logger.warn( "No nested action assigned -> nothing to do");
			return;
		}

		// (!) задать контекст выполнения для действия
		getTodo().setContext(this.getContext());

		// исполнитель
		final RunAsWork<NodeRef> runner = new RunAsWork<NodeRef>() {
			@Override
			public NodeRef doWork() throws Exception {
				/* 
				 * с  транзакцией:
				 * 
				final boolean transReadonly = false;
				final boolean transReaquiersNew = true;
				final NodeRef newRef = getContext().getTransactionService().getRetryingTransactionHelper().doInTransaction(
					new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
						@Override
						public NodeRef execute() throws Throwable {
							getNested().run();
							return null;
						}
					}, transReadonly, transReaquiersNew);
				return newRef;
				  */
				getTodo().run();
				return null;
			}
		};

		if (getByUser() == null) { // ничего менять не требуется ...
			getTodo().run();
		} else if (ASSYSTEM.equalsIgnoreCase(getByUser())) { // выполнить от имени системы
			AuthenticationUtil.runAsSystem( runner);
		} else { // выполнить от имени указанного ползователя ...
			// (for safe) получение Person по сконфигурированному имени ...
			final NodeRef person = getContext().getPublicServices().getPersonService().getPerson(this.getByUser());
			logger.debug( String.format( "run as user <%s>: found Person node is %s", this.getByUser(), person));

			// doit...
			AuthenticationUtil.runAs( runner, getByUser()) ; // person.getId());
		}

		logger.info( String.format( "SUCCESS %s", this.toString()));
	}

}
