package ru.it.lecm.security;

/**
 * Обработчик событий, затрагивающих подсистему безопасности.
 */
public interface ISecurityNotifier {

	void attachEmpoyee2User(String idEmployee, String login);

}
