package ru.it.lecm.delegation.security;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.security.permissions.DynamicAuthority;
import org.alfresco.repo.security.permissions.PermissionReference;
import org.alfresco.repo.security.permissions.impl.ModelDAO;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.extensions.surf.util.AbstractLifecycleBean;

import ru.it.lecm.utils.DurationLogger;
import ru.it.lecm.utils.cache.Cache;
import ru.it.lecm.utils.cache.CacheEx;
import ru.it.lecm.utils.cache.CacheableBase;

/**
 * Динамическая авторизация согласно значениям атрибутов.
 * @author rabdullin
 *
 */
public class DynamicByAttribute
		extends AbstractLifecycleBean 
		implements DynamicAuthority 
{
	private final static Logger logger = LoggerFactory.getLogger(DynamicByAttribute.class);

	static final String NSURI_BLANKS = "http://www.it.ru/lecm/model/blanks/1.0";
	//static final String NAME_TYPE = "blank";

	static final QName QNAME_FLAG = QName.createQName(NSURI_BLANKS, "flag");
	static final QName QNAME_STATUS = QName.createQName(NSURI_BLANKS, "status");

	private ModelDAO modelDAO;
	// private CheckOutCheckInService checkOutCheckInService;
	private NodeService nodeService;

	// ex: "ROLE_OWNER:
	private String authority;

	// отслеживаемые типы nodeRef; если null, тогда любой тип подходит
	private Set<QName> monitoringTypes;

	final private Cache<CachingNodePermission> cache = new CacheEx<CachingNodePermission>(4000);
	private int cacheInterval_ms = 10 * 60 * 1000; // 10 минут - большое время жизни в кеше
	private boolean enableCache = false;

	/** текущий способ проверки */
	private EnumCheckStyle checkStyle = DEFAULT_CHECKSTYLE;

	/**
	 * Ссылка на бин с настройками
	 */
	// private Properties settings;

	/** название параметра для типа провеки */
	// public static String PROPNAME_CHECKSTYLE = "security.checkStyle";

	/**
	 * Режим работы проверки доступа:
	 */
	public enum EnumCheckStyle {
			dynamic( "dyna-check is on")
			, alwaysTrue("dyna-check is off, returns true for any node")
			, alwaysFalse("dyna-check is off, return false for any node");

			final String description;

			private EnumCheckStyle(String description) {
				this.description = description;
			}

			public String getDescription() {
				return description;
			}

	}


	private static int makeId(NodeRef refnode, String username) {
		final int prime = 31;
		int result = 0;
		// result = prime * result + getOuterType().hashCode();
		// result = prime * result + ((hasAccess == null) ? 0 : hasAccess.hashCode());
		result = prime * result + ((refnode == null) ? 0 : refnode.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	/**
	 * Кешируемое значение:
	 *    ключ: NodeRef + username
	 *    значение: Boolean = есть доступ или нет
	 */
	private class CachingNodePermission extends CacheableBase {

		final private NodeRef ref;
		final private String userName;
		Boolean hasAccess;

		public CachingNodePermission(NodeRef ref, String userName,
				Boolean hasAccess) {
			super( makeId( ref, userName), cacheInterval_ms);
			this.ref = ref;
			this.userName = userName;
			this.hasAccess = hasAccess;
		}

		/**
		 * Сравнение только по ref и userName
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!super.equals(obj))
				return false;
			// if (getClass() != obj.getClass()) return false; // уже проверено в super
			final CachingNodePermission other = (CachingNodePermission) obj;
			// if (!getOuterType().equals(other.getOuterType())) return false;
			if (ref == null) {
				if (other.ref != null)
					return false;
			} else if (!ref.equals(other.ref))
				return false;
			if (userName == null) {
				if (other.userName != null)
					return false;
			} else if (!userName.equals(other.userName))
				return false;
			return true;
		}

		@Override
		public Object getValue() throws Exception {
			return hasAccess;
		}

//		private DynamicByAttribute getOuterType() {
//			return DynamicByAttribute.this;
//		}

	}

	public int getCacheSize() {
		return cache.getCapacity();
	}

	public void setCacheSize(int value) {
		cache.setCapacity(value);
	}

	public int getCacheIntervalMsec() {
		return this.cacheInterval_ms;
	}

	public void setCacheIntervalMsec(int value) {
		this.cacheInterval_ms = value;
	}

	public boolean getEnableCache() {
		return this.enableCache;
	}

	public void setEnableCache(boolean flag) {
		this.enableCache = flag;
		logger.info("cache is "+ (this.enableCache ? "Enabled" : "Disabled"));
	}

	/**
	 * whenRequired и requiredFor два разнотипных списка отрабатываемых случаев
	 */
	private List<String> requiredFor; // ex: "{http://www.alfresco.org/model/content/1.0}cmobject.Contributor" или "{http://www.alfresco.org/model/system/1.0}base.ReadProperties"
	private Set<PermissionReference> whenRequired;

	@Override
	protected void onBootstrap(ApplicationEvent event)
	{
		logger.debug("initializing ...");

		final ApplicationContext ctx = super.getApplicationContext(); // org.springframework.context.ApplicationContext;
		// checkOutCheckInService = (CheckOutCheckInService) ctx.getBean("checkOutCheckInService");
		if (modelDAO == null)
			modelDAO = (ModelDAO) ctx.getBean("modelDAO");
		if (nodeService == null)
			nodeService = (NodeService) ctx.getBean("nodeService");

		// PropertyCheck.mandatory(this, "lockService", lockService);
		// PropertyCheck.mandatory(this, "checkOutCheckInService", checkOutCheckInService);
		PropertyCheck.mandatory(this, "modelDAO", modelDAO);
		PropertyCheck.mandatory(this, "nodeService", nodeService);

		// Build the permission set
		if(requiredFor != null)
		{
			whenRequired = new HashSet<PermissionReference>();
			for(String permission : requiredFor)
			{
				final PermissionReference permissionReference = modelDAO.getPermissionReference(null, permission);
				final Set<PermissionReference> grantee= modelDAO.getGranteePermissions(permissionReference);
				final Set<PermissionReference> granting= modelDAO.getGrantingPermissions(permissionReference);
				whenRequired.addAll(grantee);
				whenRequired.addAll(granting);
				if (logger.isDebugEnabled()) {
					logger.debug( String.format( 
							"adding permissions required for <%s> as: \n\t grantee %s\n\t granting %s", permission, grantee, granting));
				}
			}
			logger.info("set requiredFor: "+ whenRequired);
		}

		logger.info("initialized");
	}

//	/**
//	 * @return текущие установки
//	 */
//	public Properties getSettings() {
//		return this.settings;
//	}
//
//	/**
//	 * Задать бин с установками. Подразумевается присвоение непосредственно в config-xml.  
//	 * @param settings
//	 */
//	public void setSettings(Properties settings) {
//		this.settings = settings;
//	}


	/** по-умолчанию проверка отключена и положительна */
	final static EnumCheckStyle DEFAULT_CHECKSTYLE = EnumCheckStyle.alwaysTrue;

	public void setCheckStyle(String value) {
		this.checkStyle = (value != null && value.length() > 0) ? EnumCheckStyle.valueOf(value) : DEFAULT_CHECKSTYLE;
		logger.info( String.format( "current SecurityStyle is <%s>", this.checkStyle));
	}

	/**
	 * Вернуть текущий способ работы проверки: работает, всегда плоожительна, всегда отрицательна.
	 * @return способ указанный в settings[PROPNAME_CHECKSTYLE], по-умолчанию см. DEFAULT_CHECKSTYLE
	 */
	public EnumCheckStyle getCheckStyleEnum() {
		return this.checkStyle;
		/*
		EnumCheckStyle result = DEFAULT_CHECKSTYLE;
		try {
			// достаём глобальный флаг стиля проверки: всегда true/false или "честная" ...
			if (settings != null && settings.containsKey(PROPNAME_CHECKSTYLE)) {
				final String s = (String) settings.get(PROPNAME_CHECKSTYLE);
				result = (s != null && s.length() > 0) ? EnumCheckStyle.valueOf(s) : result;
			}
		} catch (Throwable ex) {
			logger.error( String.format( "Fail to get CheckStyle, default value <%s> is used", DEFAULT_CHECKSTYLE), ex);
		}
		// logger.trace( String.format( "SecurityStyle is <%s>", result));
		return result;
		 */
	}


	/**
	 * No-op
	 */
	@Override
	protected void onShutdown(ApplicationEvent event) {
		logger.info("shutdown performed");
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setModelDAO(ModelDAO modelDAO)
	{
		this.modelDAO = modelDAO;
	}


	/**
	 * If this authority is granted this method provides the string
	 * representation of the granted authority.
	 * 
	 * @return the authority that may be assigned
	 */
	@Override
	public String getAuthority() {
		// return PermissionService.OWNER_AUTHORITY;
		return this.authority;
	}

	public void setAuthority(String authority) {
		this.authority = authority;
		logger.info("set authority: "+ this.authority);
	}


	public void setMonitoringTypes(List<String> types) {
		if (types == null)
			this.monitoringTypes = null;
		else {
			this.monitoringTypes = new HashSet<QName>();
			for(String t: types)
				this.monitoringTypes.add( QName.createQName(t));
		}
		logger.info("set monitoring types: "+ this.monitoringTypes);
	}


	/**
	 * Set the permissions for which this dynamic authority is required
	 */
	public void setRequiredFor(List<String> requiredFor)
	{
		this.requiredFor = requiredFor;
	}

	/**
	 * For what permission checks is this dynamic authority required?
	 * If null, it is required for all checks. 
	 * example:
		&lt;property name="requiredFor">
			&lt;list>
				&lt;value>Unlock&lt;/value>
				&lt;value>CheckIn&lt;/value>
				&lt;value>CancelCheckOut&lt;/value>
			&lt;/list>
		&lt;/property>
	 * @return the set of permissions for which this dynamic authority should be evaluated
	 */
	@Override
	public Set<PermissionReference> requiredFor() {
		return whenRequired;
	}

	public long getCacheInfoIntervalMsec() {
		return this.infoInterval_ms;
	}

	public void setCacheInfoIntervalMsec(long value) {
		if (value <= 0)
			value = DEFAULT_INTERVAL_MS;
		this.infoInterval_ms = value;

		// выполнить немедленное дампирование и потом ждать ...
		this.infoTime =  System.currentTimeMillis() - 2; // "прошлое", чтобы гарантировать дамп
		cacheInfo();
	}

	static final long DEFAULT_INTERVAL_MS = 20 * 1000; // 20 сек
	private long infoTime = 0, infoInterval_ms = DEFAULT_INTERVAL_MS;

	private void cacheInfo() {
		if (logger.isInfoEnabled()) {
			final long curTime = System.currentTimeMillis();
			if (curTime > infoTime) {
				infoTime = curTime + infoInterval_ms;
				logger.info(String.format( "authority cache info: " +
						"\n\t size/capacity: %s/%s, fillRate %s" + 
						"\n\t hit/miss: %s/%s, hitRate %s" +
						"\n\t push/expired: %s/%s"
						, this.cache.getSize()
						, this.cache.getCapacity()
						, getPercents(this.cache.getSize(), this.cache.getCapacity())

						, this.cache.getHitCount()
						, this.cache.getMissCount()
						, getPercents(this.cache.getHitCount(), this.cache.getMissCount() + this.cache.getHitCount())

						, this.cache.getPushCount()
						, (this.cache instanceof CacheEx) ? ((CacheEx<?>) this.cache).getExpiredCounter() : "<unused>"
				));
			}
		}
	}

	static String getPercents(double size, double capacity) {
		return (capacity == 0) ? "<?>%" 
				: MessageFormat.format( "{0,number,percent}", size/capacity);
	}

	/**
	 * Is this authority granted to the given user for this node ref?
	 * 
	 * @param nodeRef
	 * @param userName
	 * @return true if the current user has the authority
	 */
	@Override
	public boolean hasAuthority(final NodeRef nodeRef, final String userName) {

		// logger.debug( String.format( "hasAuthority performing: user '%s', nodeRef %s ", userName, nodeRef));

		// net.sf.acegisecurity.vote.RoleVoter
		
		final DurationLogger d = new DurationLogger();
		try {
			// достаём глобальный флаг стиля проверки: всегда true/false или "честная" ...
			switch(getCheckStyleEnum()) {
				case alwaysFalse: return false;
				case alwaysTrue: return true;
				default: // надо выполнить детальную проверку ...
			}

			// проверка наличия объекта в кеше ...
			if (this.enableCache) {
				final CachingNodePermission item = new CachingNodePermission(nodeRef, userName, null);
				Boolean found = (Boolean) this.cache.get(item);
				if (found != null) { // (!) уже есть в кеше - повезло
					cacheInfo();
					return found;
				}
			}

			final Boolean result = AuthenticationUtil.runAs(new RunAsWork<Boolean>(){

				public Boolean doWork() throws Exception
				{
					// if (nodeRef == null) return true;

					if (monitoringTypes != null) {
						final QName type = nodeService.getType(nodeRef);
						if (!monitoringTypes.contains(type)) { // skip unsupported type as "don't include dyna role" ...
							return false;
						}
					}
					final Object flag = nodeService.getProperty( nodeRef, QNAME_FLAG);
					final Object status = nodeService.getProperty( nodeRef, QNAME_STATUS);

					final Boolean templateFlag;
					final String templateStatus;
					if ("blank1".equals(userName) || "admin".equals(userName)) {
						templateFlag = false;
						templateStatus = "New";
					} else if ("blank2".equals(userName)) {
						templateFlag = true;
						templateStatus = "New";
					} else if ("blank3".equals(userName)) {
						templateFlag = false;
						templateStatus = "Active";
					} else if ("blank4".equals(userName)) {
						templateFlag = true;
						templateStatus = "Active";
					} else {
						return false; // "don't include dyna role" ...
					}
					final boolean granted = templateFlag.equals(flag) 
							&& (status != null && templateStatus.equalsIgnoreCase(status.toString()) );
					return  granted;
				}}, AuthenticationUtil.getSystemUserName());

				if (this.enableCache) {
					final CachingNodePermission item = new CachingNodePermission(nodeRef, userName, result);
					this.cache.put(item);
					cacheInfo();
				}

				return result;

		} catch(Exception ex) {
			logger.error( "Fail to find authority for node "+ nodeRef, ex);
			return false;
		} finally {
			d.logCtrlDuration(logger,  "call time is {t} msec");
		}
	}
/*
	public boolean hasAuthority1(final NodeRef nodeRef, final String userName)
	{
		return AuthenticationUtil.runAs(new RunAsWork<Boolean>(){

			public Boolean doWork() throws Exception
			{
				if (lockService.getLockStatus(nodeRef, userName) == LockStatus.LOCK_OWNER)
					return true;

				final NodeRef original = checkOutCheckInService.getCheckedOut(nodeRef);
				if (original != null)
					return (lockService.getLockStatus(original, userName) == LockStatus.LOCK_OWNER);
				return false;
			}}, AuthenticationUtil.getSystemUserName());

	}
*/
}
