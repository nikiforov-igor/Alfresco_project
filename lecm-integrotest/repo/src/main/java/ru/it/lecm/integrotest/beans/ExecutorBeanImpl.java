package ru.it.lecm.integrotest.beans;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.integrotest.ExecutorBean;
import ru.it.lecm.integrotest.FinderBean;
import ru.it.lecm.integrotest.RunAction;
import ru.it.lecm.integrotest.RunContext;
import ru.it.lecm.integrotest.RunModifier;
import ru.it.lecm.integrotest.RunModifier.TodoStatus;
import ru.it.lecm.integrotest.SingleTest;
import ru.it.lecm.integrotest.TestFailException;
import ru.it.lecm.integrotest.utils.DurationLogger;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.orgstructure.beans.OrgstructureSGNotifierBean;
import ru.it.lecm.security.events.IOrgStructureNotifiers;
//import ru.it.lecm.statemachine.StateMachineServiceBean;

/**
 * Класс, для интегрального тестирования.
 * Настраивается скриптовой конфигурацией как обычный бин.
 * Проводимые тесты собираются бинами от интерфейсов IExecAction, IStepAction. 
 *
 * @author rabdullin
 */
public class ExecutorBeanImpl 
		extends BaseBean
		implements ExecutorBean 
{

	final static Logger logger = LoggerFactory.getLogger (ExecutorBeanImpl.class);

	private AuthenticationService authenticationService;
	private AuthorityService authorityService;

	private ServiceRegistry publicServices;
	private PermissionService permissionService;

	private OrgstructureSGNotifierBean orgSGNotifier;
	private IOrgStructureNotifiers sgNotifier;
	private OrgstructureBean orgstructureService;

	private Map<String, Object> configArgs; 

	private List<SingleTest> steps;
	private TestingContextImpl context;
	private FinderBean finder;

//	private StateMachineServiceBean stateMachineService;

	public void init() {
		logger.debug("initializing");
		PropertyCheck.mandatory(this, "authService", this.authenticationService);
		PropertyCheck.mandatory(this, "authorityService", this.authorityService);

		PropertyCheck.mandatory(this, "nodeService", this.nodeService);
		PropertyCheck.mandatory(this, "transactionService", this.transactionService);

		PropertyCheck.mandatory(this, "permissionService", this.permissionService);
		PropertyCheck.mandatory(this, "publicServices", this.publicServices);

		PropertyCheck.mandatory(this, "sgNotifier", this.sgNotifier);
		PropertyCheck.mandatory(this, "orgSGNotifier", this.orgSGNotifier);
		PropertyCheck.mandatory(this, "stateMachineService", this.stateMachineService);

		logger.info("initialized");
	}

	public AuthenticationService getAuthenticationService() {
		return authenticationService;
	}

	public void setAuthenticationService(AuthenticationService authService) {
		this.authenticationService = authService;
	}


	public AuthorityService getAuthorityService() {
		return authorityService;
	}

	public void setAuthorityService(AuthorityService authorityService) {
		this.authorityService = authorityService;
	}

	public ServiceRegistry getPublicServices() {
		return publicServices;
	}

	public void setPublicServices(ServiceRegistry value) {
		this.publicServices = value;
	}

	public PermissionService getPermissionService() {
		return permissionService;
	}

	public void setPermissionService(PermissionService permissionService) {
		this.permissionService = permissionService;
	}

	public IOrgStructureNotifiers getSgNotifier() {
		return sgNotifier;
	}

	public void setSgNotifier(IOrgStructureNotifiers sgNotifier) {
		this.sgNotifier = sgNotifier;
	}

	public OrgstructureSGNotifierBean getOrgSGNotifier() {
		return orgSGNotifier;
	}

	public void setOrgSGNotifier(OrgstructureSGNotifierBean orgSGNotifier) {
		this.orgSGNotifier = orgSGNotifier;
	}

	public OrgstructureBean getOrgstructureService() {
		return orgstructureService;
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	public FinderBean getFinder() {
		return finder;
	}

	public void setFinder(FinderBean finder) {
		this.finder = finder;
	}

//	public StateMachineServiceBean getStateMachineService() {
//		return stateMachineService;
//	}
//
//	public void setStateMachineService(StateMachineServiceBean value) {
//		this.stateMachineService = value;
//	}

	public Map<String, Object> getConfigArgs() {
		if (configArgs == null)
			configArgs = new HashMap<String, Object>();
		return configArgs;
	}

	public void setConfigArgs(Map<String, Object> value) {
		this.configArgs = value;
		getConfigArgs();
	}

	public TestingContextImpl getContext() {
		return context;
	}

	public void setContext(TestingContextImpl context) {
		this.context = context;
	}

	@Override
	public List<SingleTest> getSteps() {
		return this.steps;
	}

	@Override
	public void setSteps( List<SingleTest> list) {
		this.steps = list;
	}


	@Override
	public List<StepResult> runAll() {
		final RunAsWork<List<StepResult>> runner = new RunAsWork<List<StepResult>>() {
			@Override
			public List<StepResult> doWork() throws Exception {
				// для проверки записи начинаем новую пишущую транзакцию ...
				/*
				final boolean transReadonly = false;
				final boolean transReaquiersNew = true; // (!) новая транзакция
				final List<StepResult> result 
					= transactionService.getRetryingTransactionHelper().doInTransaction(
						new RetryingTransactionHelper.RetryingTransactionCallback<List<StepResult>>() {
							@Override
							public List<StepResult> execute() throws Throwable {
								return doRunAll();
							}
						}
						, transReadonly, transReaquiersNew);
				*/
				final List<StepResult> result = doRunAll(); 
				return result;
			}
		};

		return AuthenticationUtil.runAsSystem( runner);
	}

	private List<StepResult> doRunAll() {
		final List<StepResult> result = new ArrayList<ExecutorBean.StepResult>();
		int ok = 0;
		for(int i = 0; i < steps.size(); i++) {
			final StepResult res = runStep(i);
			if (res.getCode() == EResult.OK)
				ok++;
			result.add( res);
		}
		if (logger.isInfoEnabled()) {
			final StringBuilder sb = makeLoggingResult(result);
			sb.append( String.format( "\n* tests SUCCUSS: %d \n* tests FAIL: %d\n* TOTAL %d"
					, ok, steps.size() - ok, steps.size()));
			logger.info( sb.toString());
		}
		return result;
	}

	@Override
	public StepResult runStep(final int i) {
		final RunAsWork<StepResult> runner = new RunAsWork<StepResult>() {
			@Override
			public StepResult doWork() throws Exception {
				// для проверки записи надо будет пишущую транзакцию ...
				final boolean transReadonly = false;
				final boolean transReaquiersNew = true; // шаг = отдельный ТЕСТ, так что начинаем новую транзакцию ...
				final StepResult result 
					= transactionService.getRetryingTransactionHelper().doInTransaction(
						new RetryingTransactionHelper.RetryingTransactionCallback<StepResult>() {
							@Override
							public StepResult execute() throws Throwable {
								final StepResult code = doRunStep(i);
								// if (code != null && code.getCode() == EResult.OK) commit();
								return code;
							}
						}
						, transReadonly, transReaquiersNew);
				return result;
			}
		};

		return AuthenticationUtil.runAsSystem( runner);
	}

	private StepResult doRunStep(int i) {
		final int maxLen = (steps == null) ? 0 : steps.size();
		if (i < 0 || i >= maxLen)
			throw new ArrayIndexOutOfBoundsException(String.format( "Step index out of bounds %d: must be inside [0,%d]", i, maxLen-1));
		final DurationLogger d = new DurationLogger();
		final StepResult result = new StepResult( EResult.OK);
		String stage = String.format( "runing step %s", i);
		int j = 0;
		try {
			final SingleTest step = this.steps.get(i);
			if (step == null || step.getActions() == null)
				return null;

			this.context = new TestingContextImpl(step);

			boolean skipNext = false; // признак пропуска следующего шага-действия ...
			runner: for (RunAction act: step.getActions()) {
				j++;
				stage = String.format( "step %s.%s, action %s", i+1, j, act.getClass());
				if (skipNext) {
					skipNext = false;
					logger.warn( "skipping step due to previous RunModifier: "+ stage);
					continue;
				}
				logger.debug( "enter "+ stage);

				act.setContext(this.getContext());
				try {
					act.run();
					// ловим модификаторы потока действий ...
					if (act instanceof RunModifier) {
						final RunModifier mod = (RunModifier) act;
						final TodoStatus todo = mod.getTodoStatus();
						stage = String.format( "(!) RunModifier %s at step %s.%s, action %s", todo, i+1, j, act.getClass());
						logger.warn(stage);
						switch (todo) {
							case doBreak:
								result.getNestedResults().add( new StepResult( EResult.OK, null, stage));
								break runner;
							case doSkipNext:
								skipNext = true;
								break;
							default:
								logger.warn("RunModifier is dummy");
						}
					}

					result.getNestedResults().add( new StepResult( EResult.OK, null, stage)); 
					result.setInfo( String.format( "affected break at step %s.%s", i+1, j));
				} catch (TestFailException tx){
					if (!tx.isCanContinue())
						// поднимаем исключение если прописан подъём
						throw tx;
					// сохраним как вложенный результат ...
					result.getNestedResults().add( new StepResult( EResult.ERROR, tx, stage));
				}

				logger.info( String.format( "SUCCESSFULL step %s.%s, action %s", i+1, j, act.getClass()) );
			} // runner: for

			result.setCode(EResult.OK);
		} catch (Throwable t) {
			result.setData( EResult.ERROR, t, "problem at phase '"+ stage+ "'");
			logger.error( "problem at phase '"+ stage+ "'", t);
		} finally {
			d.logCtrlDuration(logger, String.format( "done step %s (%s actions): duration {t} msec", i+1, j));
		}

		return result;
	}


	static StringBuilder makeLoggingResult(List<StepResult> list) {
		final StringBuilder result = new StringBuilder("Test results: \n");
		if (list != null) {
			int i = 0;
			for (StepResult s: list) {
				i++;
				result.append( String.format("\t[%d]\t%s \n", i, s.toString()));
			}
		}
		return result;
	}

	public class TestingContextImpl implements RunContext {

		private Map<String, Object> workArgs, results;
		final private SingleTest parentTest;
		final private Map<String, Object> roConfigArgs;

		public TestingContextImpl(SingleTest parent) {
			this.parentTest = parent;
			this.roConfigArgs = Collections.unmodifiableMap( getConfigArgs());
			resetContext();
		}

		@Override
		public NodeService getNodeService() {
			return nodeService;
		}

		@Override
		public TransactionService getTransactionService() {
			return transactionService;
		}

		@Override
		public OrgstructureSGNotifierBean getOrgSGNotifier() {
			return orgSGNotifier;
		}

		@Override
		public AuthenticationService getAuthenticationService() {
			return authenticationService;
		}

		@Override
		public AuthorityService getAuthorityService() {
			return authorityService;
		}

		@Override
		public IOrgStructureNotifiers getSgNotifier() {
			return sgNotifier;
		}

		public OrgstructureBean getOrgstructureService() {
			return orgstructureService;
		}

		@Override
		public Map<String, Object> configArgs() {
			return this.roConfigArgs;
		}

//		@Override
//		public void setWorkArgs(Map<String, Object> args) {
//			configArgs = args;
//		}

		@Override
		public Map<String, Object> workArgs() {
			return workArgs;
		}

		@Override
		public Map<String, Object> results() {
			return results;
		}

		public void resetContext() {
			this.workArgs = new HashMap<String, Object>();
			if (configArgs != null) 
				workArgs.putAll(configArgs);
			results = new HashMap<String, Object>();
		}

		@Override
		public SingleTest getParent() {
			return parentTest;
		}

		@Override
		public FinderBean getFinder() {
			return finder;
		}

		@Override
		public ServiceRegistry getPublicServices() {
			return publicServices;
		}

		@Override
		public PermissionService getPermissionService() {
			return permissionService;
		}

//		@Override
//		public StateMachineServiceBean getStateMachineService() {
//			return stateMachineService;
//		}

		final String MACROS_TAG = "@";
		/* 
		 * свойства на запись для удобной нотации в spring-контексте:
		 * 		<property name="workarg" value="id=@idCreated" />
		 */
		/**
		 * Присвоение рабочего аргумента
		 * @param data строка в виде: название_аргумента=значение
		 *  , если значение начинается с '@', то выполняется получение spring-свойства
		 *  текущего объекта
		 */
		public void setWorkarg(String data) {
			if (data == null) return;
			final String parts[] = data.split("=");
			if (parts.length == 0) return;
			final String key = parts[0];
			String value = (parts.length > 1) ? parts[1] : null;
			Object dest = value;
			if (value != null && value.startsWith(MACROS_TAG)) {
				// выполним подстановку ...
				try {
					dest = org.apache.commons.beanutils.PropertyUtils.getProperty(this, value.substring(MACROS_TAG.length()));
				// } catch (IllegalAccessException e, InvocationTargetException e, NoSuchMethodException e) {
				} catch (Throwable ex) {
					final String info = String.format( "Fail to get property '%s':\n", value);
					// logger.error(info, ex);
					// return; // skip assignment
					throw new RuntimeException( info, ex);
				}
			}
			this.workArgs.put( key, dest);
		}

	}

	// в данном бине не используется каталог в /app:company_home/cm:Business platform/cm:LECM/
	@Override
	public NodeRef getServiceRootFolder() {
		return null;
	}
}
