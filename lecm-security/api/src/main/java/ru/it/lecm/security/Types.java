package ru.it.lecm.security;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.util.Pair;

import ru.it.lecm.security.LecmPermissionService.LecmPermissionGroup;


/**
 * Типы и описания подсистемы безопасности.
 *
 * @author rabdullin
 */
public final class Types {

	/**
	 * Суффиксы для security-групп Альфреско
Сводка использования имён sec-объектов:
	------------------------------------------------------------------------------------------------
	NN	Полное Alfresco-название	Назначение
	------------------------------------------------------------------------------------------------
		GROUP_LECM$CLASS-xxx		(общее понимание) sec-группа для объекта класса Class с id=xxx

	01	GROUP_LECM$ME-nnn			личная sec-группа пользователя с id=nnn ("ME" здесь "дань схемам" и в общем можно убирать)
	 (?)	GROUP_LECM$U-nnn		личная sec-группа пользователя с id=nnn
	02	GROUP_LECM$OU-nnn			sec-группа Орг-Штатного подразделения (OU) с id=nnn
	03	GROUP_LECM$DP-nnn			должностная позиция (DP) с id=nnn

	04	GROUP_LECM_SV$OU-nnn		руководящая позиция Орг-Штатного подразделения (OU) с id=nnn
	05	GROUP_LECM$BR-nnn			sec-группа Бизнес Роли с id=nnn (или названием nnn?)
	06	GROUP_LECM$BRME-nnn-mmm		личная sec-группа пользователя id=mmm для Бизнес Роли nnn
		(?) или GROUP_LECM$BR-nnn$U-mmm

	07	GROUP_LECM$BR-nnn$OU-mmm	sec-группа Бизнес Роли nnn, выданной Подразделению mmm
	08	GROUP_LECM$BR-nnn$DP-mmm	sec-группа Бизнес Роли nnn, выданной на Должностную Позицию mmm
	------------------------------------------------------------------------------------------------
	 */

	final static public String SFX_DELIM = "%"; // разделить внутри суффиксов - должен отличаится от разделителя внутри guid (минуса)
	final static public String PFX_LECM = "_LECM";
	final static public String SFX_OU  = "$OU"+ SFX_DELIM;   // by id
	final static public String SFX_DP  = "$DP"+ SFX_DELIM;   // by id

	final static public String SFX_USR = "$ME"+ SFX_DELIM;   // by id
	final static public String SFX_BR  = "$BR"+ SFX_DELIM;   // by id
	final static public String SFX_SV  = "$OUSV"+ SFX_DELIM; // by id

	final static public String SFX_BRME = "$BRME" + SFX_DELIM;   // by id user & id role
	final static public String SFX_SPEC = "$SPEC" + SFX_DELIM;   // by id user & low-level permission group name

	final static public String SFX_PRIV4USER = SFX_DELIM+ "PRIV4USER"; // окончание для индикации личной security-группы пользователя


	/**
	 * Вернуть TRUE, если authority относится к динамическим бизнес-ролям
	 * @param authority
	 * @return
	 */
	public static boolean isDynamicRole(String authority) {
		return (authority != null) && (authority.contains(PFX_LECM+SFX_BRME));
	}

	/**
	 * Выделить из полного имени sec-группы Альфреско пару отдельных элементов:
	 *    1) id Сорудника
	 *    2) Бизнес роль
	 * Здесь формат имени принимается таким:
	 * 		"GROUP__LECM$BRME%BR_INITIATOR%PRIV4USER%usrID"
	 * example: "GROUP__LECM$BRME-BR_INITIATOR-PRIV4USER-usrId"
	 * @param authority полное имя sec-группы Альфреско
	 * @return объект Pair<userId, broleCode> с полями, полученными из названия authority
	 */
	public static Pair<String, String> splitAuthname2UserRolePair(String authority) {
		// [0] буквы префикса [1] id1(=usedId) [2] id2(=roleCodeId)
		final String[] ids = authority.split(SFX_DELIM);
		assert ids.length >= 4 : String.format( "check validity of authority named '%s' -> must be like 'XXX-roleCode-userId'", authority);
		return new Pair<String, String>(ids[3],  ids[1]);
	}


	/**
	 * Стандартные security-группы (SG) Альфреско для модельных случаев:
	 *    01	GROUP_LECM$ME-nnn			личная sec-группа пользователя с id=nnn ("ME" здесь "дань схемам" и в общем можно убирать)
	 *    		(?)	GROUP_LECM$U-nnn		личная sec-группа пользователя с id=nnn
	 *    02	GROUP_LECM$OU-nnn			sec-группа Орг-Штатного подразделения (OU) с id=nnn
	 *    03	GROUP_LECM$DP-nnn			должностная позиция (DP) с id=nnn
	 *    04	GROUP_LECM_SV$OU-nnn		руководящая позиция Орг-Штатного подразделения (OU) с id=nnn
	 *    05	GROUP_LECM$BR-nnn			sec-группа Бизнес Роли с id=nnn (или названием nnn?)
	 *    06	GROUP_LECM$BRME-nnn-mmm		личная sec-группа пользователя id=mmm для Бизнес Роли nnn
	 *    		(?) или GROUP_LECM$BR-nnn$U-mmm
	 * Дополнительно можно иметь:
	 *    	07	GROUP_LECM$BR-nnn$OU-mmm		sec-группа Бизнес Роли nnn, выданной Подразделению mmm
	 *    	08	GROUP_LECM$BR-nnn$DP-mmm		sec-группа Бизнес Роли nnn, выданной на Должностную Позицию mmm
	 *
	 * @author rabdullin
	 */
	public enum SGKind {
		  SG_ME(SFX_USR, "Private User Point")		// личная группа Сотрудника-пользователя
		, SG_DP(SFX_DP, "Deputy Position")			// группа Должностной позиции
		, SG_OU(SFX_OU, "Org Unit")					// группа Подразделения

		, SG_SV(SFX_SV, "Boss Position")			// группа Руководящая (связана с Подразделением и Должностью)
		, SG_BR(SFX_BR, "Business Role Point")		// группа бизнес-роли
		, SG_BRME(SFX_BRME, "Private User Business Role Point")	// личная группа Сотрудника-пользователя для конкретной бизнес-роли

		, SG_SPEC(SFX_SPEC, "Individual user access for node") // индивидуальнаый доступ Сотрудника на конкретный узел
		;

		final private String suffix;
		final private String humanTag;

		private SGKind(String suffix, String humanTag) {
			this.suffix = suffix;
			this.humanTag = humanTag;
		}

		public String getSuffix() {
			return suffix;
		}

		public String getHumanInfo() {
			return this.humanTag;
		}

		@Override
		public String toString() {
			return String.format( "%s-%s(%s, %s)", this.getClass().getSimpleName(), this.name(), this.suffix, this.humanTag);
		}

		/**
		 * Получить объект security-позиции, соот-щий this.
		 * (!) Для получения объекта личной бизнес-роли надо использовать
		 * getSGBusinessRolePos, для Должностной Позиции getSGDeputyPosition.
		 * @param objId
		 * @return
		 */
		public SGPosition getSGPos(String objId) {
			return getSGPos(objId, null);
		}

		/**
		 * Получить объект security-позиции, соот-щий this.
		 * (!) Для получения объекта личной бизнес-роли надо использовать
		 * getSGBusinessRolePos, для Должностной Позиции getSGDeputyPosition.
		 * @param objId Id объекта
		 * @param displayName название объекта для удобного чтения
		 * @return
		 */
		public SGPosition getSGPos(String objId, String displayName) {
			if (this == SG_ME)
				return new SGPrivateMeOfUser(objId, displayName);
			if (this == SG_OU)
				return new SGOrgUnit(objId, displayName);
			if (this == SG_SV)
				return new SGSuperVisor(objId, displayName);
			if (this == SG_BR)
				return new SGBusinessRole(objId, displayName);
			// if (this == SG_BRME) return new SGPrivateBusinessRole(objId, moreId);
			throw new RuntimeException( String.format("Cannot create simple locate descriptor for sg-enum %s", this));
		}

		public String getAlfrescoSuffix(String objId) {
			return getSGPos(objId).getAlfrescoSuffix();
		}

		public static SGPrivateBusinessRole getSGMyRolePos(String employeerId, String userLogin, String broleCode) {
			return new SGPrivateBusinessRole(employeerId, userLogin, broleCode);
		}

		public static SGPrivateBusinessRole getSGMyRolePos(String employeerId, String broleCode) {
			return new SGPrivateBusinessRole(employeerId, broleCode);
		}

		public static SGDeputyPosition getSGDeputyPosition(String dpId, String dpName, String userLogin, String employeerId) {
			return new SGDeputyPosition(dpId, dpName, employeerId, userLogin);
		}

		public static SGDeputyPosition getSGDeputyPosition(String dpId, String employeerId, String userLogin) {
			return getSGDeputyPosition(dpId, null, userLogin, employeerId);
		}

		public static SGDeputyPosition getSGDeputyPosition(String dpId, String employeerId) {
			return getSGDeputyPosition(dpId, employeerId, null);
		}

		public static SGSpecialUserRole getSGSpecialUserRole(String employeeId,
				LecmPermissionGroup permissionGroup, NodeRef nodeRef, String userLogin) {
			return new SGSpecialUserRole(employeeId, nodeRef, permissionGroup, userLogin);
		}

		public static SGSpecialUserRole getSGSpecialUserRole(String employeeId,
				LecmPermissionGroup permissionGroup, NodeRef nodeRef) {
			return getSGSpecialUserRole(employeeId, permissionGroup, nodeRef, null);
		}

	}


	/**
	 * Непосредственная SG-группа конкретного объекта.
	 * "Фабрика" для создания подобных объектво - см. {@link SGKind.getSGXXX}
	 */
	public abstract static class SGPosition {
		final private SGKind sgKind;
		private String id; // в зависимости от sgKind это Id Должности или Подразделения, login Сотрудника.
		private String displayInfo;   // название объекта (мнемоническое или просто "ориентированное на чтение"

		private SGPosition(SGKind sgKind, String id) {
			super();
			this.sgKind = sgKind;
			this.id = id;
		}

		private SGPosition(SGKind sgKind, String id, String displayInfo) {
			super();
			this.sgKind = sgKind;
			this.id = id;
			this.displayInfo = displayInfo;
		}

		public SGKind getSgKind() {
			return sgKind;
		}

		public String getId() {
			return id;
		}

		public void setId(String anId) {
			this.id = anId;
		}

		public String getDisplayInfo() {
			return displayInfo;
		}

		public void setDisplayInfo(String displayInfo) {
			this.displayInfo = displayInfo;
		}

		@Override
		public String toString() {
			return String.format( "SGPOS(%s '%s', %s)", sgKind, id, (displayInfo == null ? "" : displayInfo));
		}

		/**
		 * Получить часть имени реального security-объекта, который в Альфреско
		 * соответствует группе, описываемой this
		 * @return
		 */
		public String getAlfrescoSuffix() {
			return String.format( "%s%s%s", PFX_LECM, this.sgKind.getSuffix(), this.id);
		}
	}

	/**
	 * Индикатор личной группы безопасности для Пользователя-Сотрудника
	 * [1] Сейчас в качестве super.id используется employee.id, что не явлеятся "human-oriented",
	 * но зато позволяет легко привязывать (или изменять) пользователя Альфреско
	 * к Сотрудникам на любом этапе.
	 * [2] В принципе можно использовать login пользователя в качестве super.id. Но
	 * тогда при привзяке user к employee, надо будет фактически заново выполнять
	 * связывания security-group по полной программе т.е. и для Сотрудника в орг-штатке,
	 * и для всех доступных Сотруднику бизнес-ролей (выданных на Сотрудника, должность,
	 * и все подразделения до верхнего уровня).
	 * В перспективе стоит подумать на этим в сторону более читабельного второго варианта,
	 * но при условии упрощения доп действий.
	 */
	public static class SGPrivateMeOfUser extends SGPosition {

		private SGPrivateMeOfUser(String userId) {
			super( SGKind.SG_ME, userId);
		}

		private SGPrivateMeOfUser(String userId, String userDisplayOrLogin) {
			super( SGKind.SG_ME, userId, userDisplayOrLogin);
		}

//		public String getUserLogin() {
//			return super.getId();
//		}
		public String getEmployeeId() {
			return super.getId();
		}
	}

	/**
	 * Индикатор позиции, связанной с пользователем (например, DP или BR_ME).
	 */
	private abstract static class SGPositionWithUser extends SGPosition {

		private String userId;
		private String userLogin;

		private SGPositionWithUser(SGKind sgKind, String id, String displayInfo
				, String userId, String userLogin)
		{
			super( sgKind, id, displayInfo);
			this.userId = userId;
			this.userLogin = userLogin;
		}

		private SGPositionWithUser(SGKind sgKind, String id, String displayInfo
				, String userId) {
			this(sgKind, id, displayInfo, userId, null);
		}

		private SGPositionWithUser(SGKind sgKind, String id, String displayInfo) {
			this(sgKind, id, displayInfo, null, null);
		}

		private SGPositionWithUser(SGKind sgKind, String id) {
			this(sgKind, id, null, null, null);
		}

		public String getUserId() {
			return this.userId;
		}

		public void setUserId(String value) {
			this.userId = value;
		}

		public String getUserLogin() {
			return this.userLogin;
		}

		public void setUserLogin(String value) {
			this.userLogin = value;
		}

		@Override
		public String toString() {
			return super.toString() + String.format( "  user '%s', userId %s", (userLogin == null ? "" : userLogin), userId);
		}

		/**
		 * Получить часть имени реального security-объекта, который в Альфреско
		 * соответствует группе, описываемой this
		 * @return
		 */
		@Override
		public String getAlfrescoSuffix() {
			// не подвязываемся на userId, т.к. оно может быть NULL, в то же время 
			// в качестве Id для this будет уникальное значение и его будет
			// вполне достаточно для уникальности security-группы
			return super.getAlfrescoSuffix() + SFX_PRIV4USER; // "-" + this.getUserId();
		}

	}

	/**
	 * Индикатор Должностной Позиции.
	 * название ДП можно использовать как super.id, но с ограничениями аналогичны как и для id в п 2.
	 */
	public static class SGDeputyPosition extends SGPositionWithUser {

		/**
		 * @param dpId id Должностной Позиции
		 * @param dpName название Должностной Позиции
		 * @param userId id Пользователя, который назначен на DP. Здесь можно передавать
		 * либо id-employee, либо id пользователя, который соот-ет employee.
		 * @param userLogin имя входа Пользователя для Сотрудника
		 */
		private SGDeputyPosition(String id, String dpName, String userId, String userLogin)
		{
			super( SGKind.SG_DP, id, dpName, userId, userLogin);
		}

		private SGDeputyPosition(String id, String dpName, String userId) {
			this(id, dpName, userId, null);
		}

		private SGDeputyPosition(String id, String dpName) {
			this(id, dpName, null, null);
		}

		public String getDPId() {
			return super.getId();
		}

		public String getDPName() {
			return super.getDisplayInfo();
		}

		public void setDPName(String value) {
			super.setDisplayInfo(value);
		}

	}

	/**
	 * Индикатор индивидуальной роли Сотрудника в документе.
	 */
	public static class SGSpecialUserRole extends SGPositionWithUser 
	{
		public SGSpecialUserRole(String employeeId, NodeRef nodeRef,
				LecmPermissionGroup permissionGroup, String userLogin) {
			super( SGKind.SG_SPEC, permissionGroup.getName(), /*displayInfo*/nodeRef.getId(), employeeId, userLogin);
		}

		public SGSpecialUserRole(String employeeId, NodeRef nodeRef, LecmPermissionGroup permissionGroup) {
			this(employeeId, nodeRef, permissionGroup, null);
		}

		public String getLecmPermGroupName() {
			return super.getId();
		}

		public String getDocId() {
			return super.getDisplayInfo();
		}
	}

	/**
	 * Индикатор орг-штатной единицы.
	 * название или Id объекта Альфреско можно использовать как super.id
	 */
	public static class SGOrgUnit extends SGPosition {
		private SGOrgUnit(String orgUnitId) {
			super( SGKind.SG_OU, orgUnitId);
		}

		private SGOrgUnit(String orgUnitId, String displayName) {
			super( SGKind.SG_OU, orgUnitId, displayName);
		}

		public String getOUId() {
			return super.getId();
		}

		public void setOUId(String value) {
			super.setId(value);
		}

	}

	/**
	 * Индикатор Руководящей Позиции
	 * название или Id соот-щей орг-штатной единицы можно использовать как id
	 */
	public static class SGSuperVisor extends SGPosition {

		private SGSuperVisor(String orgUnitId) {
			super( SGKind.SG_SV, orgUnitId);
		}

		private SGSuperVisor(String orgUnitId, String displayName) {
			super( SGKind.SG_SV, orgUnitId, displayName);
		}

		public String getOUId() {
			return super.getId();
		}
	}

	/**
	 * Индикатор бизнес-роли (Статической или Динамической)
	 */
	public static class SGBusinessRole extends SGPosition {
		private SGBusinessRole(String businessRoleId) {
			super( SGKind.SG_BR, businessRoleId);
		}

		private SGBusinessRole(String businessRoleId, String displayName) {
			super( SGKind.SG_BR, businessRoleId, displayName);
		}

		public String getBusinessRoleId() {
			return super.getId();
		}

		public void setBusinessRoleId(String roleID) {
			super.setId( roleID);
		}
	}

	/**
	 * Личная группа Сотрудника для некоторой Бизнес Роли
	 */
	public static class SGPrivateBusinessRole extends SGPositionWithUser {

		// super хранит данные по сотруднику
		private SGPrivateBusinessRole(String userId, String userLogin, String businessRole) {
			super(SGKind.SG_BRME, businessRole, "role", userId, userLogin);
		}

		private SGPrivateBusinessRole(String userId, String businessRoleId) {
			this(userId, null, businessRoleId);
		}

		public String getBusinessRole() {
			return super.getId();
		}

		public void setBusinessRole(String value) {
			super.setId(value);
		}

		@Override
		public String getAlfrescoSuffix() {
			// для личной бизнес-роли надо ввести id Сотрудника ...
			return super.getAlfrescoSuffix() + SFX_DELIM + this.getUserId();
		}
	}

}
