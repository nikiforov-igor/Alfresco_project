package ru.it.lecm.base.processors;

import org.alfresco.service.cmr.repository.NodeRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.RepositoryStructureHelper;
import ru.it.lecm.base.beans.SearchQueryProcessor;
import ru.it.lecm.base.beans.WriteTransactionNeededException;

import java.util.Map;

/**
 * User: dbashmakov
 * Date: 26.12.2014
 * Time: 13:58
 */
public class UserHomeProcessor extends SearchQueryProcessor {
    private final static Logger logger = LoggerFactory.getLogger(UserHomeProcessor.class);

    private RepositoryStructureHelper repositoryStructureHelper;

    public void setRepositoryStructureHelper(RepositoryStructureHelper repositoryStructureHelper) {
        this.repositoryStructureHelper = repositoryStructureHelper;
    }

    @Override
    public String getQuery(Map<String, Object> params) {
        NodeRef homeRef = null;
        try {
            homeRef = repositoryStructureHelper.getDraftsRef(authService.getCurrentUserName());
        } catch (WriteTransactionNeededException e) {
            logger.error(e.getMessage(), e);
        }
        if (homeRef != null) {
            String pathStr = nodeService.getPath(homeRef).toPrefixString(namespaceService);
            return "PATH:\"" + pathStr + "//*\"";
        } else {
            return "";
        }
    }
}
