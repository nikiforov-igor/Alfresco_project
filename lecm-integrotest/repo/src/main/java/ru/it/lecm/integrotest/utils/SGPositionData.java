package ru.it.lecm.integrotest.utils;

import java.util.HashMap;
import java.util.Map;


import ru.it.lecm.integrotest.FinderBean;
import ru.it.lecm.security.Types.SGPosition;

/**
 * Связанная пара: позиция и её строковое представление
 */
public class SGPositionData { 

	private String sgKind;
	private String id, userId, roleCode;

	final private NodeRefData idRef = new NodeRefData();
	final private NodeRefData userIdRef = new NodeRefData();

	private SGPosition pos;

	@Override
	public String toString() {
		return "SGPositionData {"
				+ "pos=" + pos
				+ "by ref {"
					+ "sgKind=" + sgKind

					+ ifNotNull( ", id=", id)
					+ ifNotNull( ", idRef=" , idRef, idRef.hasRefData())

					+ ifNotNull( ", userId=", userId)
					+ ifNotNull( ", userIdRef=" , userIdRef, userIdRef.hasRefData())

					+ ifNotNull( ", roleCode=", roleCode)
					+ "}"
				+ "}";
	}

	/**
	 * Вернуть (tag + val), если условие выполняется и значение не null,
	 * иначе (если значение val==null или условие false) - пустую строку. 
	 * @param tag
	 * @param val
	 * @param cond
	 * @return 
	 */
	static String ifNotNull(String tag, Object val, boolean cond) {
		return (cond && val != null) ? String.format("%s%s", tag, val.toString()) : "";
	}

	/**
	 * Вернуть tag + val, если значение (val != null) и пустую строку иначе.
	 * @param tag
	 * @param val
	 * @return
	 */
	static String ifNotNull(String tag, Object val) {
		return ifNotNull( tag, val, true);
	}

	public String getSgKind() {
		return sgKind;
	}

	public void setSgKind(String sgKind) {
		this.sgKind = sgKind;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public NodeRefData getIdRef() {
		return idRef;
	}

	/**
	 * Присвоить одной строкой сразу три параметра idRef:
	 *    nodeType, propName, value
	 * (значения разделяются запятой или точкой с запятой) 
	 */
	public void setIdRefStr(String value) {
		idRef.setRefStr(value);
		this.id = null; // очистка
	}

	public NodeRefData getUserIdRef() {
		return userIdRef;
	}

	/**
	 * Присвоить одной строкой сразу три параметра idRef:
	 *    nodeType, propName, value
	 * (значения разделяются запятой или точкой с запятой) 
	 */
	public void setUserIdRefStr(String value) {
		userIdRef.setRefStr(value);
		this.userId = null; // очистка
	}

	public Map<String, String> makeArgsMap() {
		final Map<String, String> result = new HashMap<String, String>(); // Utils.makeArgsMap(strpos);
		result.put( "sgKind", sgKind);
		result.put( "roleCode", roleCode);
		result.put( "id", id);
		result.put( "userId", id);
		return result;
	}

	/**
	 * Если (pos != null) воз-ся pos, 
	 * иначе (при pos == null) - по необходимости выполняется догрузка id и userId слогласно соот-щим refData.
	 * @return
	 */
	public SGPosition getPos(FinderBean service) {
		if (pos == null && hasRefData()) {
			// прогружаем ссылки
			if (this.id == null) { // прогружаем id ...
				this.id = idRef.findNodeId(service); 
			}
			if (this.userId == null) { // прогружаем id ...
				this.userId = userIdRef.findNodeId(service); 
			}

			// формируем sg-position ...
			pos = Utils.makeSGPosition( makeArgsMap());
		}
		return pos;
	}

	public void setPos(SGPosition value) {
		this.pos = value;
		if (value != null) { // присвоение непустого значения вызовет очистку ссылок ...
			this.idRef.clear();
			this.userIdRef.clear();
		}
	}

	public boolean hasRefData() {
		return idRef.hasRefData() || userIdRef.hasRefData();
	}
}