package ru.it.lecm.contractors;

import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.contractors.api.Contractors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.JSONArray;
import ru.it.lecm.dictionary.beans.DictionaryBean;

public class ContractorsBean extends BaseBean implements Contractors {

	private DictionaryBean dictionaryService;

	public void setDictionaryService(DictionaryBean dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

    @Override
    public NodeRef getServiceRootFolder() {
        return null;
    }

    @Override
    public void assignAsPrimaryRepresentative(NodeRef representativeToAssignAsPrimary) {

        List<ChildAssociationRef> sourceRefs = nodeService.getParentAssocs(representativeToAssignAsPrimary);
        NodeRef contractor = sourceRefs.get(0).getParentRef(); // FIXME: Может работать неверно, если у Контрагента будет больше 1-ой ChildAssociationRef!

        List<ChildAssociationRef> representativesAssocs = nodeService.getChildAssocs(contractor);

        Boolean isPrimaryChanged  = false;
        Boolean isPrimaryAssigned = false;

        for (ChildAssociationRef representativesAssoc : representativesAssocs) {

            NodeRef representative = representativesAssoc.getChildRef();

            Boolean isPrimary = (Boolean) nodeService.getProperty(representative, QName.createQName("http://www.it.ru/lecm/contractors/model/contractor/1.0", "link-to-representative-association-is-primary"));

            if(isPrimary) {
                nodeService.setProperty(representative, QName.createQName("http://www.it.ru/lecm/contractors/model/contractor/1.0", "link-to-representative-association-is-primary"), false);
                isPrimaryChanged = true;
            }

            if (representativeToAssignAsPrimary.equals(representative)) {
                nodeService.setProperty(representative, QName.createQName("http://www.it.ru/lecm/contractors/model/contractor/1.0", "link-to-representative-association-is-primary"), true);
                isPrimaryAssigned = true;
            }

            if(isPrimaryChanged && isPrimaryAssigned)
                break;
        }
    }

    @Override
    public Map<String, String> getParentContractor(NodeRef childContractor) {

        Map<String, String> result = new HashMap<String, String>();

        NodeRef parentContractor = nodeService.getPrimaryParent(childContractor).getParentRef();

        QName TYPE_CONTRACTOR = QName.createQName("http://www.it.ru/lecm/contractors/model/contractor/1.0", "contractor-type");
        if(parentContractor != null && TYPE_CONTRACTOR.equals(nodeService.getType(parentContractor))) {
            result.put("parentContractor", parentContractor.toString());

            String parentContractorName = nodeService.getProperty(parentContractor, QName.createQName("http://www.it.ru/lecm/contractors/model/contractor/1.0", "shortname")).toString();
            result.put("parentContractorName", parentContractorName);

            String childContractorName = nodeService.getProperty(childContractor, QName.createQName("http://www.it.ru/lecm/contractors/model/contractor/1.0", "shortname")).toString();
            result.put("childContractorName", childContractorName);
        }

        return result;
    }

    @Override
    public List<Object> getRepresentatives(NodeRef targetContractor) { // O(n^3)

        // Получить список всех ассоциаций на ссылку.
        List<AssociationRef> contractorToLinkAssocs = nodeService.getTargetAssocs(targetContractor, QName.createQName("http://www.it.ru/lecm/contractors/model/contractor/1.0", "contractor-to-link-association"));

        // Получить список всех ссылок через ассоциации.
        List<NodeRef> linkRefs = new ArrayList<NodeRef>(contractorToLinkAssocs.size());
        for(AssociationRef contractorToLinkAssoc : contractorToLinkAssocs) {
            linkRefs.add(contractorToLinkAssoc.getTargetRef());
        }

        // Получить список всех ассоциаций на представителей.
        List<Object> representativesList = new ArrayList<Object>();
        List<NodeRef> representativeRefs = new ArrayList<NodeRef>();
        for(NodeRef linkRef : linkRefs) {
            List<AssociationRef> linkToRepresentativeAssocs = nodeService.getTargetAssocs(linkRef, QName.createQName("http://www.it.ru/lecm/contractors/model/contractor/1.0", "link-to-representative-association"));

            // Получить список всех представителей через ассоциации.
            for(AssociationRef linkToRepresentativeAssoc : linkToRepresentativeAssocs) {

                Map<String, Object> representativeMap = new HashMap<String, Object>();
                NodeRef representativeRef = linkToRepresentativeAssoc.getTargetRef();

                representativeMap.put("nodeRef", representativeRef.toString());
                representativeMap.put("linkRef", linkRef.toString());

                String shortName = String.format("%s %s", nodeService.getProperty(representativeRef, QName.createQName("http://www.it.ru/lecm/contractors/model/representative/1.0", "surname")),
                                                          nodeService.getProperty(representativeRef, QName.createQName("http://www.it.ru/lecm/contractors/model/representative/1.0", "firstname")));

                representativeMap.put("shortName", shortName);
                representativeMap.put("isPrimary", nodeService.getProperty(linkRef, QName.createQName("http://www.it.ru/lecm/contractors/model/contractor/1.0", "link-to-representative-association-is-primary")));

                representativesList.add(representativeMap);
            }
        }

        return representativesList;
    }

	@Override
	public JSONArray getBusyRepresentatives() {
		NodeRef representativeDictionary = dictionaryService.getDictionaryByName("Контрагенты Представители");
		List<ChildAssociationRef> dicValues = nodeService.getChildAssocs(representativeDictionary);
		Set<String> representatives = new HashSet<String>();
		for (ChildAssociationRef dicValue : dicValues) {
			NodeRef representative = dicValue.getChildRef();
			NodeRef link = findNodeByAssociationRef(representative, ASSOC_LINK_TO_REPRESENTATIVE, TYPE_REPRESENTATIVE_AND_CONTRACTOR, ASSOCIATION_TYPE.SOURCE);
			if (link != null) {
				representatives.add(representative.toString());
			}
		}
		return new JSONArray(representatives);
	}
}
