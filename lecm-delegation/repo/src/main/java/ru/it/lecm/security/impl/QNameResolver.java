package ru.it.lecm.security.impl;

import java.util.HashSet;
import java.util.Set;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;

import ru.it.lecm.delegation.utils.StrUtils;

/**
 * Контейнер для удобного разименования строк в QNames.
 * Например, для конфигурирования Alfresco-типов с целью получить QNames.
 */
public class QNameResolver {

	/**
	 * Строковые значения для разименований (см resolve) в QNames
	 */
	private Set<String> synonyms = null;

	/**
	 * Разименованые значения из synonyms
	 */
	final private Set<QName> qnames = new HashSet<QName>();

	@Override
	public String toString() {
		// return "QNameResolver [synonyms=" + synonyms + ", qnames=" + qnames + "]";
		final StringBuilder sb = new StringBuilder();

		// source string synonyms ...
		sb.append("\t synonyms list ");
		if (synonyms == null || synonyms.isEmpty())
			sb.append("is empty");
		else
			sb.append( String.format( "has %s items: %s", qnames.size(), StrUtils.getAsString( synonyms, ", ")));
		sb.append("\n");

		// resolved as ...
		sb.append("\t as type links: ");
		if (qnames == null || qnames.isEmpty())
			sb.append("empty");
		else {
			sb.append("\n\t\t");
			sb.append(StrUtils.getAsString( synonyms, "\n\t\t"));
		}
		sb.append("\n");

		return sb.toString();
	}

	/**
	 * @return список стороковых (string) синонимов для qnames-списка
	 */
	public Set<String> getSynonyms() {
		return synonyms;
	}

	/**
	 * Задать список синонимов.
	 * (!) Для формирования QNames-списка требуется явно вызвать метод resolve
	 * @param synonyms
	 */
	public void setSynonyms(Set<String> synonyms) {
		if (this.synonyms == synonyms)
			return;
		this.synonyms = synonyms;
		this.qnames.clear();
	}

	/**
	 * @return список разименованных QNames, которые соот-ют списку synonyms
	 * (!) имеет смысл только после выполнения resolve(), до присвоения нового 
	 * значения для synonyms.
	 */
	public Set<QName> getQnames() {
		return qnames;
	}

	public boolean isEmpty() {
		return (qnames == null) || qnames.isEmpty();
	}

	/**
	 * Построить разименованные this.qnames согласно списку this.synonyms
	 * @param nameSrv служба именований
	 */
	void resolve(NamespacePrefixResolver nameSrv) {
		this.qnames.clear();
		if(this.synonyms == null)
			return;
		for(String qnameString : this.synonyms) {
			final QName qname = QName.resolveToQName(nameSrv, qnameString);
			this.qnames.add(qname);
		}
	}

	/**
	 * Проверить перечислен ли тип указанного узла в списке qnames.
	 * @param ref проверяемый узел
	 * @param serv сервис
	 * @param ifEmptyResult значение, воз-мое при пустом списке типов this.qnames 
	 * @return true, если тип узла перечислен в qnames, 
	 * иначе если список qnames не пустой воз-ся false, если пустой, то значение флага whenEmptyCheckOfTypeResult
	 */
	public boolean chkNodeOrAspectsIsOfType(NodeRef ref, NodeService serv,
			boolean ifEmptyResult) 
	{
		if (isEmpty())
			return ifEmptyResult;

		final QName typeQName = serv.getType(ref);

		// сам тип перечислен ...
		if (qnames.contains(typeQName))
			return true;

		// ищем среди аспектов узла ...
		final Set<QName> aspectQNames = serv.getAspects(ref);
		for(QName abstain : qnames) {
			if(aspectQNames.contains(abstain))
				return true;
		}

		// нет не перечислен и аспекты тоже не перечислены ...
		return false;
	}

}