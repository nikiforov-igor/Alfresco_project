package ru.it.lecm.delegation;

import org.alfresco.service.cmr.repository.NodeRef;

/**
 * Интерфейс службы: 
 * 1) создание теневых sec-group для Бизнес-ролей, орг-штатки и пр
 * 2) раздача прав на папки для бизнес-ролей
 * 3) добавление-удаление Бизнес-ролей для сотрудников (надстройка над 
 * стандартными службами Альфреско для работы с пользователями)
 *
 * @author rabdullin
 */
public interface ISecurityService {

	public class FolderAccessor {
		public NodeRef folder;
		public String[] permissions;
	}

	public class BusinessRole {
		public String name;
		public String[] users;
	}

	void prepareSecurityGroups(BusinessRole brole);

	void grantFolderAccess(BusinessRole brole, FolderAccessor[] folders);

	void grantUser(String user, BusinessRole brole);
	void revokeUser(String user, BusinessRole brole);
}
