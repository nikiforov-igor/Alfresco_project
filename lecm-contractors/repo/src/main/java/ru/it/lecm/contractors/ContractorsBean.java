package ru.it.lecm.contractors;

import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.json.JSONArray;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.contractors.api.Contractors;
import ru.it.lecm.dictionary.beans.DictionaryBean;

import java.io.Serializable;
import java.util.*;

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

            if(Boolean.TRUE.equals(isPrimary)) {
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
            result.put("parentRef", parentContractor.toString());

            String parentContractorName = nodeService.getProperty(parentContractor, Contractors.PROP_CONTRACTOR_SHORTNAME).toString();
            result.put("parentName", parentContractorName);

            String childContractorName = nodeService.getProperty(childContractor, Contractors.PROP_CONTRACTOR_SHORTNAME).toString();
            result.put("childName", childContractorName);
        }

        return result;
    }

    @Override
    public List<NodeRef> getContractorsForRepresentative(NodeRef representative) {
        List<NodeRef> results = new ArrayList<>();
        List<NodeRef> links = findNodesByAssociationRef(representative, ASSOC_LINK_TO_REPRESENTATIVE, TYPE_REPRESENTATIVE_AND_CONTRACTOR, ASSOCIATION_TYPE.SOURCE);
        if (links != null) {
            for (NodeRef link: links) {
                results.add(nodeService.getPrimaryParent(link).getParentRef());
            }
        }
        return results;
    }

    @Override
    public NodeRef getContractor(NodeRef representative) {
        Map<String, String> result = new HashMap<String, String>();
        List<AssociationRef> representativeSourceAssocs = nodeService.getSourceAssocs(representative, Contractors.ASSOC_LINK_TO_REPRESENTATIVE);
        if(representativeSourceAssocs == null || representativeSourceAssocs.isEmpty()) {
            return null;
        }
        AssociationRef sourceAssocRefToLink = representativeSourceAssocs.get(0);
        NodeRef linkRef = sourceAssocRefToLink.getSourceRef();
        NodeRef contractorRef = nodeService.getPrimaryParent(linkRef).getParentRef();
        if(contractorRef != null && TYPE_CONTRACTOR.isMatch(nodeService.getType(contractorRef))) {
            return contractorRef;
        } else {
            return null;
        }
    }

    @Override
    public String formatContractorName(final String originalName) {
        //1. удалить спец символы
        String updatedValue = delNoDigOrLet(originalName);
        //2. привести к единому регистру
        updatedValue = updatedValue.toUpperCase();
        //3. удалить все коды из справочника ОПФ
        NodeRef dicOPF = dictionaryService.getDictionaryByName(OPF_DIC_NAME);
        if (dicOPF != null) {
            updatedValue = delDicValuesFromString(updatedValue, dicOPF);
        }
        return updatedValue.trim();
    }

    @Override
    public List<Object> getRepresentatives(NodeRef targetContractor) { // O(n^3)
        // Получить список всех ассоциаций на ссылку.
        List<AssociationRef> contractorToLinkAssocs = nodeService.getTargetAssocs(targetContractor, QName.createQName("http://www.it.ru/lecm/contractors/model/contractor/1.0", "contractor-to-link-association"));

        // Получить список всех ассоциаций на адресантов.
        List<Object> representativesList = new ArrayList<Object>();

        for (AssociationRef contractorToLinkAssoc : contractorToLinkAssocs) {
            NodeRef linkRef = contractorToLinkAssoc.getTargetRef();
            List<AssociationRef> linkToRepresentativeAssocs =
                    nodeService.getTargetAssocs(linkRef, QName.createQName("http://www.it.ru/lecm/contractors/model/contractor/1.0", "link-to-representative-association"));

            // Получить список всех адресантов через ассоциации.
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
		NodeRef representativeDictionary = dictionaryService.getDictionaryByName("Адресанты");
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

    @Override
    public NodeRef getRepresentativeByEmail(String email) {
        return dictionaryService.getRecordByParamValue("Адресанты", PROP_REPRESENTATIVE_EMAIL, email);
    }

    private String delNoDigOrLet(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (Character.isLetterOrDigit(ch) || Character.isSpaceChar(ch)) {
                sb.append(s.charAt(i));
            }
        }
        return sb.toString().trim();
    }

    private String delDicValuesFromString(String str, NodeRef dictionary) {
        List<NodeRef> children = dictionaryService.getChildren(dictionary);
        for (NodeRef child : children) {
            Map<QName, Serializable> recordProps = nodeService.getProperties(child);
            for (QName dicProp : DIC_REPLACE_PROPERTIES) {
                Object dicPropValue = recordProps.get(dicProp);
                if (dicPropValue != null && dicPropValue.toString().length() > 0) {
                    str = str.replaceAll(dicPropValue.toString(), "");
                }
            }
        }
        return str;
    }
}
