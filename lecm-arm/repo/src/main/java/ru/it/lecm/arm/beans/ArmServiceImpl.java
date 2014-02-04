package ru.it.lecm.arm.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.base.beans.BaseBean;

import java.util.*;

/**
 * User: AIvkin
 * Date: 04.02.14
 * Time: 10:10
 */
public class ArmServiceImpl extends BaseBean implements ArmService {

	@Override
	public NodeRef getServiceRootFolder() {
		return getFolder(ARM_ROOT_ID);
	}

	@Override
	public boolean isArmAccordion(NodeRef ref) {
		Set<QName> types = new HashSet<QName>();
		types.add(TYPE_ARM_ACCORDION);
		return isProperType(ref, types);
	}

	@Override
	public boolean isArmNode(NodeRef ref) {
		Set<QName> types = new HashSet<QName>();
		types.add(TYPE_ARM_NODE);
		return isProperType(ref, types);
	}

	public NodeRef getDictionaryArmSettings() {
		return nodeService.getChildByName(getServiceRootFolder(), ContentModel.ASSOC_CONTAINS, ARM_SETTINGS_DICTIONARY_NAME);
	}

	@Override
	public NodeRef getArmByCode(String code) {
		Set<QName> armTypeSet = new HashSet<QName>(1);
		armTypeSet.add(TYPE_ARM);

		List<ChildAssociationRef> arms = nodeService.getChildAssocs(getDictionaryArmSettings(), armTypeSet);
		if (arms != null) {
			for (ChildAssociationRef armAssoc: arms) {
				if (nodeService.getProperty(armAssoc.getChildRef(), PROP_ARM_CODE).equals(code)) {
					return armAssoc.getChildRef();
				}
			}
		}
		return null;
	}

	@Override
	public List<NodeRef> getArmAccordions(NodeRef arm) {
		List<NodeRef> result = new ArrayList<NodeRef>();

		Set<QName> typeSet = new HashSet<QName>(1);
		typeSet.add(TYPE_ARM_ACCORDION);
		List<ChildAssociationRef> accordionsAssocs = nodeService.getChildAssocs(arm, typeSet);
		if (accordionsAssocs != null) {
			for (ChildAssociationRef accordionAssoc: accordionsAssocs) {
				result.add(accordionAssoc.getChildRef());
			}
		}
		return result;
	}

	@Override
	public List<NodeRef> getChildNodes(NodeRef node) {
		List<NodeRef> result = new ArrayList<NodeRef>();

		Set<QName> typeSet = new HashSet<QName>(1);
		typeSet.add(TYPE_ARM_ACCORDION);
		typeSet.add(TYPE_ARM_NODE);
		List<ChildAssociationRef> childAssocs = nodeService.getChildAssocs(node, typeSet);
		if (childAssocs != null) {
			for (ChildAssociationRef child: childAssocs) {
				result.add(child.getChildRef());
			}
		}
		return result;
	}

	@Override
	public List<String> getNodeTypes(NodeRef node) {
		List<String> result = new ArrayList<String>();
		String types = (String) nodeService.getProperty(node, PROP_NODE_TYPES);
		if (types != null) {
			result.addAll(Arrays.asList(types.split(",")));
		}

		return result;
	}

	@Override
	public List<String> getNodeFilters(NodeRef node) {
		List<String> result = new ArrayList<String>();
		String types = (String) nodeService.getProperty(node, PROP_NODE_FILTERS);
		if (types != null) {
			result.addAll(Arrays.asList(types.split(",")));
		}

		return result;
	}

	@Override
	public ArmCounter getNodeCounter(NodeRef node) {
		boolean counterEnable = (Boolean) nodeService.getProperty(node, PROP_COUNTER_ENABLE);
		if (counterEnable) {
			ArmCounter result = new ArmCounter();
			result.setQuery((String) nodeService.getProperty(node, PROP_COUNTER_QUERY));
			result.setDescription((String) nodeService.getProperty(node, PROP_COUNTER_DESCRIPTION));

			return result;
		}

		return null;
	}

	@Override
	public List<ArmColumn> getNodeColumns(NodeRef node) {
		List<ArmColumn> result = new ArrayList<ArmColumn>();
		List<NodeRef> columns = findNodesByAssociationRef(node, ASSOC_NODE_COLUMNS, TYPE_ARM_COLUMN, ASSOCIATION_TYPE.TARGET);
		if (columns != null) {
			for (NodeRef ref: columns) {
				ArmColumn column = new ArmColumn();
				column.setTitle((String) nodeService.getProperty(ref, PROP_COLUMN_TITLE));
				column.setField((String) nodeService.getProperty(ref, PROP_COLUMN_FIELD_NAME));
				column.setFormatString((String) nodeService.getProperty(ref, PROP_COLUMN_FORMAT_STRING));
				column.setSortable((Boolean) nodeService.getProperty(ref, PROP_COLUMN_SORTABLE));
				result.add(column);
			}
		}

		return result;
	}
}
