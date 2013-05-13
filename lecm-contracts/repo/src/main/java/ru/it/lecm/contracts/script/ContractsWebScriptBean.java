package ru.it.lecm.contracts.script;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.businessjournal.beans.EventCategory;
import ru.it.lecm.contracts.beans.ContractsBeanImpl;
import ru.it.lecm.documents.beans.DocumentMembersService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.regnumbers.template.TemplateParseException;
import ru.it.lecm.regnumbers.template.TemplateRunException;

import java.io.Serializable;
import java.util.*;

/**
 * User: mshafeev
 * Date: 12.03.13
 * Time: 10:31
 */
public class ContractsWebScriptBean extends BaseWebScript {
    private ContractsBeanImpl contractService;
	protected NodeService nodeService;
    private BusinessJournalService businessJournalService;
    private OrgstructureBean orgstructureService;

    public void setOrgstructureService(OrgstructureBean orgstructureService) {
        this.orgstructureService = orgstructureService;
    }

    private static enum WhoseEnum {
        MY,
        DEPARTMENT,
        MEMBER
    }

    public void setContractService(ContractsBeanImpl contractService) {
        this.contractService = contractService;
    }

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

    public void setBusinessJournalService(BusinessJournalService businessJournalService) {
        this.businessJournalService = businessJournalService;
    }


    public ScriptNode getDraftRoot() {
        return new ScriptNode(contractService.getDraftRoot(), serviceRegistry, getScope());
    }

    public String getDraftPath() {
        return contractService.getDraftPath();
    }

    /**
     * Получить количество договоров
     * @param path список путей поиска
     * @param properties список значений для фильтрации
     * @return количество
     */
    public Integer getAmountContracts(Scriptable path, Scriptable properties) {
        return contractService.getContracts(getElements(Context.getCurrentContext().getElements(path)), getElements(Context.getCurrentContext().getElements(properties))).size();
    }

    /**
     * Получить количество участников договорной деятельности
     * @return
     */
    public Integer getAmountMembers() {
        return contractService.getAllMembers().size();
    }

    /**
     * Получить список участников договорной деятельности
     * @param sortColumnName сортируемый атрибут
     * @param sortAscending сортировка
     * @return список участников
     */
    public Scriptable getMembers(String sortColumnName, Boolean sortAscending) {
        return createScriptable(contractService.getAllMembers(sortColumnName, sortAscending));
    }

    private ArrayList<String> getElements(Object[] object){
        ArrayList<String> arrayList = new ArrayList<String>();
        for (Object obj : object) {
            if (obj instanceof NativeJavaObject) {
                NativeJavaObject element = (NativeJavaObject) obj;
                arrayList.add((String) element.unwrap());
            } else if (obj instanceof String){
                arrayList.add(obj.toString());
            }
        }
        return arrayList;
    }

	public String createDocumentOnBasis(String typeNodeRef, String packageNodeRef) {
		if (typeNodeRef != null && packageNodeRef != null) {
			NodeRef typeRef = new NodeRef(typeNodeRef);
			NodeRef packageRef = new NodeRef(packageNodeRef);
			if (nodeService.exists(typeRef) && nodeService.exists(packageRef)) {
				NodeRef documentRef = contractService.getDocumentService().getDocumentFromPackageItems(packageRef);
				if (documentRef != null) {
					return contractService.createDocumentOnBasis(typeRef, documentRef);
				}
			}
		}
        return null;
	}

    /**
     * Обозначение причины удаления документа
     *
     * @param reasonNodeRef - ссылка на запись справочника причин удаления документа
     * @param packageNodeRef - ссылка на пакет документов процесса
     */
	public void appendDeleteReason(String reasonNodeRef, String packageNodeRef) {
		if (reasonNodeRef != null && packageNodeRef != null) {
			NodeRef reasonRef = new NodeRef(reasonNodeRef);
			NodeRef packageRef = new NodeRef(packageNodeRef);
			if (nodeService.exists(reasonRef) && nodeService.exists(packageRef)) {
				NodeRef documentRef = contractService.getDocumentService().getDocumentFromPackageItems(packageRef);
				if (documentRef != null) {
					contractService.appendDeleteReason(reasonRef, documentRef);
				}
			}
		}
	}

    public NodeRef[] getContractsByFilters(String daysCount, String userFilter){
        Date now = new Date();
        Date start = null;

        if (daysCount != null &&  !"".equals(daysCount)) {
            Integer days = Integer.parseInt(daysCount);

            if (days > 0) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(now);
                calendar.add(Calendar.DAY_OF_MONTH, (-1) * (days - 1));
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                start = calendar.getTime();
            }
        }

        List<NodeRef> employees = new ArrayList<NodeRef>();
        List<NodeRef> docs = new ArrayList<NodeRef>();

        if (userFilter != null && !"".equals(userFilter)) {
            NodeRef employee = orgstructureService.getCurrentEmployee();
            if (employee != null) {
                switch(WhoseEnum.valueOf(userFilter.toUpperCase())) {
                    case MY : {
                        employees.add(employee);
                        break;
                    }
                    case DEPARTMENT: {
                            List<NodeRef> departmentEmployees = orgstructureService.getBossSubordinate(employee);
                            employees.addAll(departmentEmployees);
                            //departmentEmployees.add(employee);
                        break;
                    }
                    case MEMBER: {
                        List<AssociationRef> membersUnits =  nodeService.getSourceAssocs(employee, DocumentMembersService.ASSOC_MEMBER_EMPLOYEE);
                        for (AssociationRef membersUnit : membersUnits) {
                            NodeRef memberUnit = membersUnit.getSourceRef();
                            List<AssociationRef> documents = nodeService.getSourceAssocs(memberUnit, DocumentMembersService.ASSOC_DOC_MEMBERS);
                            for (AssociationRef document : documents) {
                                NodeRef doc = document.getSourceRef();
                                if (!contractService.isArchive(doc)){
                                    docs.add(doc);
                                }
                            }
                        }
                        break;
                    }
                    default: {
                        employees.add(orgstructureService.getCurrentEmployee());
                        break;
                    }
                }
            }
        }

        List<NodeRef> refs = contractService.getContractsByFilter(DocumentService.PROP_STATUS_CHANGED_DATE, start, now,
                Arrays.asList(contractService.getDraftPath(), contractService.getDocumentsFolderPath()), null, employees, docs, true);
        return refs.toArray(new NodeRef[refs.size()]);
    }

    /**
     * Изменение срока договора
     * @param document - основной документ
     * @param fromDate - дата начала действия договора
     * @param toDate - дата завершения действия договора
     * @param reasonDocumentRef - стороковая ссылка на документ основание
     */
    public void setContractTime(ScriptNode document, Date fromDate, Date toDate, boolean unlimited, String reasonDocumentRef) {
        nodeService.setProperty(document.getNodeRef(), ContractsBeanImpl.PROP_START_DATE, fromDate);
        nodeService.setProperty(document.getNodeRef(), ContractsBeanImpl.PROP_END_DATE, toDate);
        nodeService.setProperty(document.getNodeRef(), ContractsBeanImpl.PROP_UNLIMITED, unlimited);

        List<String> objects = new ArrayList<String>();
        objects.add(reasonDocumentRef);
        businessJournalService.log(document.getNodeRef(), EventCategory.EXEC_ACTION, "#initiator изменил(а) срок действия договора #mainobject. Основанием изменения является данный документ #object1.", objects);
    }

	/**
	 * Расторжение договора
	 * @param document - основной документ
	 * @param reasonDocumentRef - стороковая ссылка на документ основание
	 */
	public void terminateContract(ScriptNode document, String reasonDocumentRef) {
        List<NodeRef> additionalDocuments = this.contractService.getAllContractDocuments(document.getNodeRef());
        for (NodeRef additionalDocument : additionalDocuments) {
            HashMap<QName, Serializable> aspectProps = new HashMap<QName, Serializable>();
            nodeService.addAspect(additionalDocument, ContractsBeanImpl.ASPECT_PRIMARY_DOCUMENT_DELETE, aspectProps);
            nodeService.setProperty(additionalDocument, ContractsBeanImpl.PROP_PRIMARY_DOCUMENT_DELETE, true);
        }

        List<String> objects = new ArrayList<String>();
		objects.add(reasonDocumentRef);
		businessJournalService.log(document.getNodeRef(), EventCategory.EXEC_ACTION, "#initiator зафиксировал(а) факт расторжения договора #mainobject. Основанием изменения является #object1.", objects);
	}

	public Scriptable getAllContractDocuments(ScriptNode document) {
		List<NodeRef> additionalDocuments = this.contractService.getAllContractDocuments(document.getNodeRef());
		return createScriptable(additionalDocuments);
	}

	/**
	 * Регистрация проекта договора
	 * @param contract договор
	 * @throws TemplateParseException
	 * @throws TemplateRunException
	 */
	public void registrationContractProject(ScriptNode contract) throws TemplateParseException, TemplateRunException {
	 	this.contractService.registrationContractProject(contract.getNodeRef());
	}

	/**
	 * Регистрация договора
	 * @param contract договор
	 * @throws TemplateParseException
	 * @throws TemplateRunException
	 */
	public void registrationContract(ScriptNode contract) throws TemplateParseException, TemplateRunException {
	 	this.contractService.registrationContract(contract.getNodeRef());
	}

	/**
	 * Регистрация проекта документа к договору
	 * @param document документ к договору
	 * @throws TemplateParseException
	 * @throws TemplateRunException
	 */
	public void registrationContractDocumentProject(ScriptNode document) throws TemplateParseException, TemplateRunException {
	 	this.contractService.registrationContractDocumentProject(document.getNodeRef());
	}

	/**
	 * Передача договора на подписание
	 * @param contract договор
	 */
	public void sendingToSign(ScriptNode contract) {
		this.contractService.sendingToSign(contract.getNodeRef());
	}

	/**
	 * Передача договора на подписание контрагентом
	 * @param contract договор
	 */
	public void sendingToContragentSign(ScriptNode contract) {
		this.contractService.sendingToContragentSign(contract.getNodeRef());
	}

	/**
	 * Подписание договора
	 * @param contract договор
	 */
	public void signing(ScriptNode contract) {
		this.contractService.signing(contract.getNodeRef());
	}

	/**
	 * Передача документа к договору на подписание
	 * @param document документ к договору
	 */
	public void additionalDocumentSendingToSign(ScriptNode document) {
		this.contractService.additionalDocumentSendingToSign(document.getNodeRef());
	}

	/**
	 * Подписание документа к договору
	 * @param document документ к договору
	 */
	public void additionalDocumentSigning(ScriptNode document) {
		this.contractService.additionalDocumentSigning(document.getNodeRef());
	}

    public Scriptable getAdditionalDocsByType(String typeFilter){
        String[] types = typeFilter != null && typeFilter.length() > 0 ? typeFilter.split("\\s*,\\s"): new String[0];
        String filter = "";
        for (String type : types) {
            if (filter.length() > 0) {
                filter += " OR ";
            }
            filter += "@lecm\\-additional\\-document\\:additionalDocumentType-text-content:\"" + type + "\"";
        }
        List<NodeRef> additionalDocuments = this.contractService.getAdditionalDocs(filter.length() > 0 ? filter : null);
        return createScriptable(additionalDocuments);
    }
}
