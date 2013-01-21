package ru.it.lecm.security;

import org.alfresco.util.Pair;


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

	final static public String PFX_LECM = "_LECM";
	final static public String SFX_OU  = "$OU-";   // by id
	final static public String SFX_DP  = "$DP-";   // by id

	final static public String SFX_USR = "$ME-";   // by id
	final static public String SFX_BR  = "$BR-";   // by id
	final static public String SFX_SV  = "SV$OU-"; // by id

	final static public String SFX_BRME = "$BRME-";   // by id user & id role


	/**
	 * Вернуть TRUE, если authority относится к динамическим бизнес-ролям
	 * @param authority
	 * @return
	 */
	public static boolean isDynamicRole(String authority) {
		return (authority != null) && (authority.contains(PFX_LECM+SFX_BRME));
	}

	/**
	 * Получить Pair<userId, broleCode> из названия Бизнес Роли вида:
	 * "xxx_LECM_$BRME-USERID-ROLEID"
	 * @param authority
	 * @return
	 */
	public static Pair<String, String> getUserRolePair(String authority) {
		// [0] буквы префикса [1] id1(=usedId) [2] id2(=roleCodeId)
		final String[] ids = authority.split("-");
		assert ids.length == 3 : String.format( "check validity of authority named '%s' -> must be like 'XXX-userId-roleCode'", authority);
		return new Pair<String, String>(ids[1],  ids[2]);
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
		  SG_ME(SFX_USR)		// личная группа Сотрудника-пользователя
		, SG_DP(SFX_DP)			// группа Должностной позиции
		, SG_OU(SFX_OU)			// группа Подразделения

		, SG_SV(SFX_SV)			// группа Руководящая (связана с Подразделением и Должностью)
		, SG_BR(SFX_BR)			// группа бизнес-роли
		, SG_BRME(SFX_BRME)		// личная группа Сотрудника-пользователя для конкретной бизнес-роли
		;

		final private String suffix;

		private SGKind(String suffix) {
			this.suffix = suffix;
		}

		public String getSuffix() {
			return suffix;
		}

		/**
		 * Получить объект security-позиции, соот-щий this.
		 * (!) Для получения объекта личной бизнес-роли надо использовать getSGBusinessRolePos, 
		 * для Должностной Позиции getSGDeputyPosition.
		 * @param objId
		 * @return
		 */
		public SGPosition getSGPos(String objId) {
			if (this == SG_ME)
				return new SGPrivateMeOfUser(objId);
			if (this == SG_OU)
				return new SGOrgUnit(objId);
			if (this == SG_SV)
				return new SGSuperVisor(objId);
			if (this == SG_BR)
				return new SGBusinessRole(objId);
			// if (this == SG_BRME) return new SGPrivateBusinessRole(objId, moreId);
			throw new RuntimeException( String.format("Cannot create simple locate descriptor for sg-enum %s", this));
		}

		public String getAlfrescoSuffix(String objId) {
			return getSGPos(objId).getAlfrescoSuffix();
		}

		public static SGPrivateBusinessRole getSGBusinessRolePos(String userId, String broleCode) {
			return new SGPrivateBusinessRole(userId, broleCode);
		}

		public static SGDeputyPosition getSGDeputyPosition(String dpId, String userId) {
			return new SGDeputyPosition(dpId, userId);
		}
	}


	/**
	 * Непосредственная SG-группа конкретного объекта.
	 * "Фабрика" для создания подобных объектво - см. {@link SGKind.getSGPos/getSGPos2}
	 */
	public abstract static class SGPosition {
		final private SGKind sgKind;
		final private String id; // в зависимости от sgKind это Id Сотрудника, Должности или Подразделения.

		private SGPosition(SGKind sgKind, String id) {
			super();
			this.sgKind = sgKind;
			this.id = id;
		}

		public SGKind getSgKind() {
			return sgKind;
		}

		public String getId() {
			return id;
		}

		@Override
		public String toString() {
			return sgKind + "(" + id + ")";
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

	public static class SGPrivateMeOfUser extends SGPosition {
		private SGPrivateMeOfUser(String userId) {
			super( SGKind.SG_ME, userId);
		}

		public String getUserId() {
			return super.getId();
		}
	}

	public static class SGDeputyPosition extends SGPosition {
		final String userId; 
		/**
		 * 
		 * @param dpId id Должностной Позиции
		 * @param userId id Пользователя, который назначен на DP. Здесь можно передавать
		 * либо id-employee, либо Login пользователя, который соот-ет employee.
		 */
		private SGDeputyPosition(String dpId, String userId) {
			super( SGKind.SG_DP, dpId);
			this.userId = userId;
		}

		public String getDPId() {
			return super.getId();
		}

		public String getUserId() {
			return this.userId;
		}
	}

	public static class SGOrgUnit extends SGPosition {
		private SGOrgUnit(String orgUnitId) {
			super( SGKind.SG_OU, orgUnitId);
		}
		public String getOUId() {
			return super.getId();
		}
	}

	public static class SGSuperVisor extends SGPosition {
		private SGSuperVisor(String orgUnitId) {
			super( SGKind.SG_SV, orgUnitId);
		}
		public String getOUId() {
			return super.getId();
		}
	}

	public static class SGBusinessRole extends SGPosition {
		private SGBusinessRole(String businessRoleId) {
			super( SGKind.SG_BR, businessRoleId);
		}

		public String getBusinessRoleId() {
			return super.getId();
		}
	}

	/**
	 * Личная группа Сотрудника для некоторой Бизнес Роли
	 * @author rabdullin
	 *
	 */
	public static class SGPrivateBusinessRole extends SGPosition {
		final private String businessRoleId;

		private SGPrivateBusinessRole(String userId, String businessRoleId) {
			super(SGKind.SG_BRME, userId);
			this.businessRoleId = businessRoleId;
		}

		public String getUserId() {
			return super.getId();
		}

		public String getBusinessRoleId() {
			return this.businessRoleId;
		}

		/**
		 * Получить часть имени реального security-объекта, который в Альфреско
		 * соответствует группе, описываемой this
		 * @return
		 */
		@Override
		public String getAlfrescoSuffix() {
			return super.getAlfrescoSuffix() + "-" + this.businessRoleId;
		}
		@Override

		public String toString() {
			return getSgKind() + "(" + getUserId() + ", role=" + getBusinessRoleId()+ ")";
		}

	}

}
