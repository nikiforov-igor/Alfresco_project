package ru.it.lecm.base;

import org.alfresco.service.cmr.repository.NodeRef;

/**
 * Класс для описания корневой папки, которая создается при бутстрапе сервиса
 * @author VLadimir Malygin
 * @since 04.03.2013 15:01:58
 * @see <p>mailto: <a href="mailto:vmalygin@it.ru">vmalygin@it.ru</a></p>
 */
public final class ServiceFolder {
	private String relativePath;
	private NodeRef folderRef;

	public ServiceFolder () {
	}

	/**
	 *
	 * @param relativePath путь до создаваемой папки относительно корня репозитория
	 * @param folderRef ссылка на существующую папку
	 */
	public ServiceFolder (final String relativePath, final NodeRef folderRef) {
		this.relativePath = relativePath;
		this.folderRef = folderRef;
	}

	public String getRelativePath () {
		return relativePath;
	}

	public void setRelativePath (final String relativePath) {
		this.relativePath = relativePath;
	}

	public NodeRef getFolderRef () {
		return folderRef;
	}

	public void setFolderRef (final NodeRef folderRef) {
		this.folderRef = folderRef;
	}
}
