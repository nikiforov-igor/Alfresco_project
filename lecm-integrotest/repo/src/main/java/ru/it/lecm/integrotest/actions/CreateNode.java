package ru.it.lecm.integrotest.actions;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;

import ru.it.lecm.integrotest.FinderBean;
import ru.it.lecm.integrotest.utils.NodeRefData;
import ru.it.lecm.integrotest.utils.Utils;

/**
 * Действие создания узлов заданных тиов.
 * Пример конфигурации бина:
		<bean class="ru.it.lecm.integrotest.actions.CreateNode">
			<property name="docType" value="lecm-orgstr::organization-unit" />
			<property name="attributes">
				<props>
					<prop key="cm:name">DocExample-0543</prop>
					<prop key="lecm-document:regnum">543</prop>
				</props>
			</property>

			<!-- Пользователь, от имени которого выполнить создание - см action RunAs -->

			<!-- куда именно (внутри resultMap) сохранить созданный id документа (для последующего доступа) -->
			<property name="destRefArgName" value="result.createdNodeRef" />
		</bean>
 *
 */
public class CreateNode extends LecmActionBase {

	final public static String DEST_ARGNAME = "result.createdNode";

	private String nodeType, destRefArgName = DEST_ARGNAME;

	private Properties attributes;

	private NodeRef parentNodeRef;
	private final NodeRefData findParentRef = new NodeRefData();

	/**
	 * @return тип создаваемого узла
	 */
	public String getNodeType() {
		return nodeType;
	}

	/**
	 * @param docType тип создаваемого узла
	 */
	public void setNodeType(String docType) {
		this.nodeType = docType;
	}

	/**
	 * @return название параметра для сохранения NodeRef созданного узла
	 * по-умолчанию "result.createdNodeRef"
	 * значение null = не выполнять сохранение
	 */
	public String getDestRefArgName() {
		return destRefArgName;
	}

	/**
	 * @param destRefArgName название параметра для сохранения NodeRef созданного узла
	 * значение null или "null" = не выполнять сохранение
	 */
	public void setDestRefArgName(String destRefArgName) {
		this.destRefArgName = ("NULL".equalsIgnoreCase(destRefArgName)) ? null : destRefArgName;
	}

	/**
	 * @return список атрибутов и значений для создаваемого узла
	 */
	public Properties getAttributes() {
		return attributes;
	}

	/**
	 * Добавить атриюут в начальные значения с гарантией инициализации списка хранимых атрибутов.
	 * @param attrName
	 * @param value
	 * @return not-null список атрибутов, содержаизим как минимум attrName=value.
	 */
	public Properties addProp(String attrName, Object value) {
		Properties props = getAttributes();
		if (props == null)
			props = new Properties();
		props.put( attrName, value);
		setAttributes(props);
		return getAttributes();
	}

	/**
	 * Задать значения свойств для создаваемого узла.
	 * @param attributes
	 */
	public void setAttributes(Properties attributes) {
		this.attributes = attributes;
	}

	public NodeRef getParentNodeRef() {
		return parentNodeRef;
	}

	public void setParentNodeRef(NodeRef value) {
		this.parentNodeRef = value;
		this.findParentRef.clear();// drop
	}

	public void setParentNodeRefStr(String value) {
		this.setParentNodeRef( new NodeRef(value));
	}

	public NodeRefData getFindParentRef() {
		return findParentRef;
	}


	/**
	 * "дамп" свойств
	 * @param srcprops свойства для дампа
	 * @param propsTag название списка свойств для заголовка
	 * @return построчный список вида:
	 * 		[nn] название значение
	 */
	public static String makeDumpAttributes(final Properties srcprops)
	{
		final StringBuilder sb = new StringBuilder();
		if (srcprops == null || srcprops.isEmpty())
			sb.append("\t empty");
		else {
			sb.append(String.format( "counter %d\n", sb.length()));
			int i = 0;
			for (Map.Entry<Object, Object> entry: srcprops.entrySet()) {
				i++;
				sb.append( String.format( "\t[%s]\t%s \t '%s'\n", i, entry.getKey(), entry.getValue()));
			}
		}
		return sb.toString();
	}


	@Override
	public String toString() {
		return "CreateNode [" 
				+ "put new nodeRef into {"+ destRefArgName + "}"

				+ ", nodeType '" + nodeType+ "'"
				+ ", parentNodeRef=" + parentNodeRef
				+ ", findParentRef=" + findParentRef

				+ ", attributes: " + 
					(	(attributes == null || attributes.isEmpty())
						? "empty"
						: makeDumpAttributes(attributes)
					)
				+ "]";
	}

	@Override
	public void run() {
		String stage = "prepare";
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("run() with args: "+ this.toString() );
			}

			// предварительная очистка результатов
			if (this.destRefArgName != null) {
				stage = String.format( "Clear result {%s} inside context as NULL", this.getDestRefArgName());
				logger.debug(stage);
				getArgsAssigner().setMacroValue( this.getDestRefArgName(), null);
			}

			final QName assocTypeQName = ContentModel.ASSOC_CONTAINS;
			// final QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, rootName);
			final QName assocQName = QName.createQName (NamespaceService.CONTENT_MODEL_1_0_URI, UUID.randomUUID ().toString ());
			final QName nodeTypeQName = getContext().getFinder().makeQName(this.nodeType);

			// получение ссылки на родительский узел ...
			if (this.parentNodeRef == null && this.findParentRef.hasRefData() ) {
				stage = String.format( "run findParentNodeRef(%s)", this.findParentRef);
				logger.debug( stage);
				this.parentNodeRef = this.findParentRef.findNodeRef( getContext().getFinder());
				logger.debug("found parent: "+ this.parentNodeRef);
			} else {
				logger.debug( "parentNodeRef used as "+ this.parentNodeRef);
			}

			// выбор исполняющего сервиса ...
			stage = "getService";
//			final boolean enRunAsUser = (this.getCreateByUser() != null);
//			final NodeService nodeServ = enRunAsUser 
//					? getContext().getPublicServices().getNodeService() // публичная служба
//					: getContext().getNodeService(); // обыкновенная системная
			final NodeService nodeServ = getContext().getNodeService(); // обыкновенная системная

			if (parentNodeRef != null && logger.isDebugEnabled()) {
				logger.debug( Utils.makeAttrDump(parentNodeRef, nodeServ, String.format("\nAttributes of parent node {%s}:\n", parentNodeRef)).toString());
			}

			// (!) Выполнение создания узла-документа ...
			final Map<QName, Serializable> data = getAttributesAsDataMap(); // получение сконфигурированных атрибутов

			stage = "checkCreateArgs";
			checkArgs( parentNodeRef, assocTypeQName, assocQName, nodeTypeQName, data);

			stage = "createNode";
			final ChildAssociationRef newRef = nodeServ.createNode( 
						parentNodeRef
						, assocTypeQName, assocQName
						, nodeTypeQName, data);
			final NodeRef result = newRef.getChildRef();
			// setNodeArgs( result, nodeServ);
			logger.info( String.format( "Created document: %s", result));

			// журналирование свойств созданного документа ...
			if (result != null && logger.isDebugEnabled()) {
				logger.debug( Utils.makeAttrDump(result, nodeServ).toString());
			}

			// сохранение id результата ...
			if (this.destRefArgName != null) {
				stage = String.format( "Pushing result {%s} into context as {%s}", result, this.getDestRefArgName());
				logger.debug(stage);

				getArgsAssigner().setMacroValue( this.getDestRefArgName(), result);

				stage = String.format( "Result {%s} pushed into context as {%s}", result, this.getDestRefArgName());
				logger.info(stage);
			}

		} catch (Exception ex) {
			final String msg = String.format("Failed CreateNode %s at phase %s:\n%s", this.getClass().getName(), stage, ex.getMessage());
			logger.error( msg, ex);
			throw new RuntimeException( msg, ex);
		}
	}

	/**
	 * Проверить допустить указанным параметров для создания узла
	 * @param parentRef 
	 * @param assocTypeQName
	 * @param assocQName
	 * @param nodeTypeQName
	 * @param data 
	 */
	protected void checkArgs(NodeRef parentRef, QName assocTypeQName, QName assocQName,
			QName nodeTypeQName, Map<QName, Serializable> data) {
		// DO NOTHING = enable any arguments
	}

	/**
	 * Присвоение значений атрибутов для указанного узла
	 * @param destNode
	 * @param nodeServ служба для работы с узлом, (!) может отличаться от getContext().getNodeService()
	 */
	protected void setNodeArgs( final NodeRef destNode, final NodeService nodeServ) {
		try {
			final Map<QName, Serializable> data = getAttributesAsDataMap();
			if (data != null) {
				// nodeServ.addProperties(destNode, data);
				final Map<QName, Serializable> curNodeData = nodeServ.getProperties(destNode);
				curNodeData.putAll( data);
				nodeServ.setProperties(destNode, curNodeData);
			}
		} catch(Throwable tx) {
			final String msg = String.format( "Exception setting configured attributes to node {%s}", destNode);
			logger.error( msg, tx);
			throw new RuntimeException(msg, tx);
		}
	}

	/**
	 * Получить значения в виде совместимом с Альфреско 
	 * @return null, если данных нет или непустые данные
	 */
	private Map<QName, Serializable> getAttributesAsDataMap() {
		if (this.attributes == null || this.attributes.isEmpty())
			return null;
		final Map<QName, Serializable> result = new HashMap<QName, Serializable>();

		// Выполняем ~ result.putAll( this.attributes); ...
		if (this.attributes != null) {
			final FinderBean finder = this.getContext().getFinder();
			for(Map.Entry<Object, Object> e: this.attributes.entrySet()) {
				result.put( finder.makeQName(e.getKey().toString()), (Serializable) e.getValue());
			}
		}
		return result;
	}

}
