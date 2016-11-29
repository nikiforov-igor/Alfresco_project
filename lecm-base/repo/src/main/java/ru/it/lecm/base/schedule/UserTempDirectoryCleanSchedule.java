package ru.it.lecm.base.schedule;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.RegexQNamePattern;
import ru.it.lecm.base.beans.RepositoryStructureHelper;
import ru.it.lecm.base.beans.WriteTransactionNeededException;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.util.*;
import ru.it.lecm.base.beans.BaseTransactionalSchedule;

/**
 * User: AIvkin
 * Date: 19.09.13
 * Time: 11:59
 */
public class UserTempDirectoryCleanSchedule extends BaseTransactionalSchedule {
	private OrgstructureBean orgstructureService;
	private RepositoryStructureHelper repositoryStructureHelper;

	public void setRepositoryStructureHelper(RepositoryStructureHelper repositoryStructureHelper) {
		this.repositoryStructureHelper = repositoryStructureHelper;
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	@Override
	public List<NodeRef> getNodesInTx() {
		List<NodeRef> collectedNodes = new ArrayList<>();

	    Calendar cal = Calendar.getInstance();
	    cal.add(Calendar.DAY_OF_YEAR, -1);
	    Date beforeFireTime = cal.getTime();

		List<NodeRef> allEmployees = this.orgstructureService.getAllEmployees();
		if (allEmployees != null) {
			Set<NodeRef> allPersons = new HashSet<>(allEmployees.size());

			for (NodeRef employee: allEmployees) {
				allPersons.add(this.orgstructureService.getPersonForEmployee(employee));
			}
			for (NodeRef person: allPersons) {
                if (person != null) {
					try {
						NodeRef tempDirectory = repositoryStructureHelper.getUserTemp(person, false);
                        collectNodes(collectedNodes, tempDirectory, beforeFireTime);
                    } catch (WriteTransactionNeededException ex) {
						throw new RuntimeException(ex);
					}
                }
            }
		}
		return collectedNodes;
	}

    private boolean collectNodes(final List<NodeRef> collectedNodes, final NodeRef parentDirectory, final Date beforeFireTime) {
        boolean result = false;
        if (parentDirectory != null) {
            List<ChildAssociationRef> childs = nodeService.getChildAssocs(parentDirectory);
            if (childs != null && !childs.isEmpty()) {
                result = true;
                for (ChildAssociationRef child : childs) {
                    NodeRef nodeRef = child.getChildRef();

                    boolean hasChilds = false;
                    if (nodeService.getType(nodeRef).equals(ContentModel.TYPE_FOLDER)) {
                        hasChilds = collectNodes(collectedNodes, nodeRef, beforeFireTime);
                    }
                    if (!hasChilds) {  //директории - только пустые, остальные - всегда
                        Date createDate = (Date) nodeService.getProperty(nodeRef, ContentModel.PROP_CREATED);
                        if (createDate != null && createDate.before(beforeFireTime)) {
                            List<AssociationRef> source = nodeService.getSourceAssocs(nodeRef, RegexQNamePattern.MATCH_ALL);
                            if (source == null || source.size() == 0) {
                                collectedNodes.add(nodeRef);
                            }
                        }
                    }
                }
            }
        }
        return result;
    }
}
