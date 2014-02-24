package ru.it.lecm.workflow;

/**
 * тип маршрута
 * @author vmalygin
 */
public enum RouteType {
	/**
	 * индивидуальный маршрут сотрудника
	 */
	EMPLOYEE,

	/**
	 * маршрут для подразделения
	 */
	UNIT;

	public static RouteType get(String type) {
		for (RouteType routeType : RouteType.values()) {
			if (routeType.toString().equalsIgnoreCase(type)) {
				return routeType;
			}
		}
		throw new IllegalArgumentException(String.format("'%s' type is invalid. Appropriate RouteType not found!", type));
	}
}
