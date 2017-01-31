package ru.it.lecm.reports.bootstrap;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.dictionary.DictionaryException;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.PropertyCheck;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import ru.it.lecm.reports.dao.RepositoryReportContentDAOBean;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.context.ApplicationEvent;
import org.springframework.extensions.surf.util.AbstractLifecycleBean;

/**
 * User: dbashmakov
 * Date: 12.02.2015
 * Time: 16:17
 */
public class ConfigToRepositoryLoader extends AbstractLifecycleBean {
    private final Logger logger = LoggerFactory.getLogger(ConfigToRepositoryLoader.class);

    private boolean loadConfigs = true;
    private boolean overrideExisting = true;

    private List<String> configs = new ArrayList<>();
    private TransactionService transactionService;
    private NodeService nodeService;
    private NamespaceService namespaceService;
    private ContentService contentService;

    private RepositoryReportContentDAOBean reportsService;

    public void setConfigs(List<String> configs) {
        this.configs = configs;
    }

    public void setTransactionService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    public void setLoadConfigs(String loadConfigs) {
        if (StringUtils.isEmpty(loadConfigs) || loadConfigs.startsWith("$")) {
            return;
        }
        this.loadConfigs = Boolean.valueOf(loadConfigs);
    }

    public void setOverrideExisting(String overrideExisting) {
        if (StringUtils.isEmpty(overrideExisting) || overrideExisting.startsWith("$")) {
            return;
        }
        this.overrideExisting = Boolean.valueOf(overrideExisting);
    }

    public void setReportsService(RepositoryReportContentDAOBean reportsService) {
        this.reportsService = reportsService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

    public void setContentService(ContentService contentService) {
        this.contentService = contentService;
    }

    /**
     * Размещение конфигов в репозиторий
     */
    public void init() {
        //loadConfigs();
    }

    private void loadConfigs() {
        if (!loadConfigs) {
            return;
        }
        AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Object>() {
            @Override
            public Object doWork() throws Exception {
                return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Object>() {
                    @Override
                    public Object execute() throws Throwable {
                        // грузим конфиги
                        for (final String configFile : configs) {
                            loadConfigFromLocation(configFile, overrideExisting);
                        }
                        return null;
                    }

                }, false);
            }
        });
    }

    public void loadConfigFromLocation(final String location, boolean updateExisting) {
        final NodeRef configRoot = reportsService.getServiceRootConfigFolder();

        PropertyCheck.mandatory(this, "location", location);

        logger.debug("################################################################################");
        logger.trace("Configs location: '{}'", nodeService.getPath(configRoot).toPrefixString(namespaceService));
        logger.trace("Existing configs will {}!", updateExisting ? "BE UPDATED" : "NOT BE UPDATED");
        logger.debug("Load configs from location '{}'", location);

        final ClassPathResource configResource = new ClassPathResource(location);
        try {
            final String name = location.substring(location.lastIndexOf("/") + 1);
            logger.trace("Find config '{}' in repository", name);
            final NodeRef node = nodeService.getChildByName(configRoot, ContentModel.ASSOC_CONTAINS, name);
            if (node == null || (updateExisting)) {
                logger.trace("Add config '{}' to repository", name);
                InputStream contentInputStream = null;
                try {
                    contentInputStream = configResource.getInputStream();

                    NodeRef newNode;
                    boolean update = false;
                    if (node == null) {
                        logger.trace("Config '{}' WAS NOT FOUND in repository location", name);
                        Map<QName, Serializable> props = new HashMap<>();
                        props.put(ContentModel.PROP_NAME, name);

                        logger.trace("Add config '{}' to repository location", name);
                        newNode = nodeService.createNode(configRoot, ContentModel.ASSOC_CONTAINS, QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, name), ContentModel.TYPE_CONTENT, props).getChildRef();
                        logger.trace("Config '{}' successfully added to repository", name);
                        update = true;
                    } else {
                        logger.trace("Config '{}' WAS FOUND in repository location", name);
                        newNode = node;
                        InputStream oldContentInputStream = null;
                        try {
                            logger.trace("Compare config '{}' content with its repository content", name);
                            ContentReader oldContentReader = contentService.getReader(newNode, ContentModel.PROP_CONTENT);
                            oldContentInputStream = oldContentReader.getContentInputStream();
                            update = !IOUtils.contentEquals(oldContentInputStream, contentInputStream);
                        } catch (Exception e) {
                            String msg = String.format("Failed to load config '%s' content with its repository content", name);
                            logger.error(msg);
                            throw new DictionaryException(msg, e);
                        } finally {
                            IOUtils.closeQuietly(oldContentInputStream);
                            IOUtils.closeQuietly(contentInputStream);
                        }
                    }

                    //не обновляем, если нет изменений
                    if (update) {
                        logger.trace("Configs are DIFFERENT. Config content in repository will BE UPDATED.");
                        contentInputStream = configResource.getInputStream();
                        logger.trace("Update config '{}' content", name);
                        ContentWriter contentWriter = contentService.getWriter(newNode, ContentModel.PROP_CONTENT, true);
                        contentWriter.putContent(contentInputStream);
                        logger.trace("Config '{}' content successfully updated", name);
                    } else {
                        logger.trace("Configs are EQUALS. Config content in repository will NOT BE UPDATED.");
                    }
                } catch (Exception e) {
                    String msg = String.format("Error load config '%s' to repository", name);
                    logger.error(msg);
                } finally {
                    IOUtils.closeQuietly(contentInputStream);
                }
            }
        } catch (Exception e) {
            String msg = String.format("Error bootstrap config '%s'", location);
            logger.error(msg);
        }
        logger.debug("Config from location '{}' successfully loaded", location);
        logger.debug("################################################################################");
    }

	@Override
	protected void onBootstrap(ApplicationEvent ae) {
		loadConfigs();
	}

	@Override
	protected void onShutdown(ApplicationEvent ae) {
	}
}
