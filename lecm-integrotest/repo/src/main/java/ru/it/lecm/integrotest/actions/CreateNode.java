package ru.it.lecm.integrotest.actions;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;

import ru.it.lecm.integrotest.FinderBean;
import ru.it.lecm.integrotest.utils.NodeRefData;
import ru.it.lecm.integrotest.utils.Utils;

/**
		<bean class="ru.it.lecm.integrotest.actions.CreateDoc">
			<property name="docType" value="lecm-orgstr::organization-unit" />
			<property name="attributes">
				<props>
					<prop key="cm:name">DocExample-0543</prop>
					<prop key="lecm-contract:regnum">543</prop>
				</props>
			</property>

			<!-- Пользователь, от имени котрого выполнить создание -->
			<property name="createByUser" value="admin" />

			<!-- куда (внутри resultMap) вынести созданный id документа (для последующего доступа) -->
			<property name="destRefArgName" value="result.createdNodeRef" />
		</bean>
 *
 */
public class CreateNode extends LecmActionBase {

	final public static String DEST_ARGNAME = "result.createdNode";

	private String nodeType, createByUser, destRefArgName = DEST_ARGNAME;

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
	 * @return пользователь, от имени котрого выполнить операция создания
	 */
	public String getCreateByUser() {
		return createByUser;
	}

	/**
	 * @param value пользователь, от имени котрого выполнить операция создания
	 * значение null == от имени системы
	 */
	public void setCreateByUser(String value) {
		this.createByUser = ("NULL".equalsIgnoreCase(value)) ? null : value;
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
				+ "createBy '" + Utils.coalesce( createByUser, "system") + "'"
				+ ", put new nodeRef into {"+ destRefArgName + "}"

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

			final QName assicTypeQName = ContentModel.ASSOC_CONTAINS;
			// final QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, rootName);
			final QName assocQName = QName.createQName (NamespaceService.CONTENT_MODEL_1_0_URI, UUID.randomUUID ().toString ());
			final QName nodeTypeQName = getContext().getFinder().makeQName(this.nodeType);

			// получение ссылки на родительский узел ...
			if (this.parentNodeRef == null && this.findParentRef.hasRefData() ) {
				stage = String.format( "run findParentNodeRef(%s)", this.findParentRef);
				logger.debug( stage);
				this.parentNodeRef = this.findParentRef.findNodeRef( getContext().getFinder());
			} else {
				logger.debug( "parentNodeRef used as "+ this.parentNodeRef);
			}

			// выбор исполняющего сервиса ...
			stage = "getService";
			final boolean enRunAsUser = (this.getCreateByUser() != null);
			final NodeService nodeServ = enRunAsUser 
					? getContext().getPublicServices().getNodeService() // публичная служба
					: getContext().getNodeService(); // обыкновенная системная

			final RunAsWork<NodeRef> runner = new RunAsWork<NodeRef>() {
					@Override
					public NodeRef doWork() throws Exception {
						final ChildAssociationRef newRef = nodeServ.createNode( 
								parentNodeRef, assicTypeQName, assocQName, nodeTypeQName
						);
						final NodeRef created = newRef.getChildRef();
						setNodeArgs( created, nodeServ);
						return created; 
					}
			};

			// Выполнение создания узла-документа ...
			final NodeRef result;
			if (enRunAsUser) {
				// получение Person по сконфигурированному имени ...
				stage = String.format( "search Person for login '%s'", this.getCreateByUser());
				logger.debug(stage);
				final NodeRef person = getContext().getPublicServices().getPersonService().getPerson(this.getCreateByUser());

				// doit...
				stage = String.format( "run as user <%s> (found Person is node %s) ...", this.getCreateByUser(), person);
				logger.debug(stage);
				result = AuthenticationUtil.runAs( runner, person.getId());
			} else { // вполнение от имени "системы"
				stage = "run as <system>";
				logger.debug(stage);

				// doit... // result = runner.doWork();
				result = AuthenticationUtil.runAsSystem( runner);
			}
			logger.info( String.format( "Created document: %s", result));

			if (this.destRefArgName != null) {
				stage = String.format( "Pushing result {%s} into context as {%s}", result, this.getDestRefArgName());
				logger.debug(stage);

				getArgsAssigner().setMacroValue( this.getDestRefArgName(), result);
				stage = String.format( "Result {%s} pushed into context as {%s}", result, this.getDestRefArgName());
				logger.info(stage);
			}

		} catch (Exception ex) {
			final String msg = String.format("Failed run action <%s> at phase <%s>:\n%s", this.getClass().getName(), stage, ex.getMessage());
			logger.error( msg);
			throw new RuntimeException( msg, ex);
		}
	}

	/**
	 * Присвоение значений атрибутов для указанного узла
	 * @param destNode
	 * @param nodeServ служба для работы с узлом, (!) может отличаться от getContext().getNodeService()
	 */
	protected void setNodeArgs(NodeRef destNode, NodeService nodeServ) {
		final Map<QName, Serializable> data = getAttributesAsDataMap();
		if (data != null)
			nodeServ.addProperties(destNode, data);
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
		final FinderBean finder = this.getContext().getFinder();
		for(Map.Entry<Object, Object> e: this.attributes.entrySet()) {
			result.put( finder.makeQName(e.getKey().toString()), (Serializable) e.getValue());
		}
		return result;
	}

}
