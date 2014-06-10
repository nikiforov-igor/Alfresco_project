package ru.it.lecm.base.scripts;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.tenant.TenantService;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.transaction.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.config.ConfigException;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * User: AIvkin Date: 14.11.13 Time: 15:31
 */
public class RepoShareConfigGet extends AbstractWebScript {

	private static final transient Logger log = LoggerFactory.getLogger(RepoShareConfigGet.class);

	private static final String CONFIG_OPEN_TAG = "<alfresco-config>";
	private static final String CONFIG_CLOSE_TAG = "</alfresco-config>";

	private NodeService nodeService;
	private TenantService tenantService;
	private SearchService searchService;
	private NamespaceService namespaceService;
	private ContentService contentService;
	protected TransactionService transactionService;
	private List<String> repoFoldersUrls;

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setTenantService(TenantService tenantService) {
		this.tenantService = tenantService;
	}

	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}

	public void setNamespaceService(NamespaceService namespaceService) {
		this.namespaceService = namespaceService;
	}

	public void setContentService(ContentService contentService) {
		this.contentService = contentService;
	}

	public void setRepoFoldersUrls(List<String> repoFoldersUrls) {
		this.repoFoldersUrls = repoFoldersUrls;
	}

	public void setTransactionService(TransactionService transactionService) {
		this.transactionService = transactionService;
	}

	public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
		OutputStream resOutputStream = null;
		try {
			res.setContentEncoding("UTF-8");
			res.setContentType("text/xml");

			resOutputStream = res.getOutputStream();
			resOutputStream.write(getConfigFrom(this.repoFoldersUrls).getBytes());
			resOutputStream.flush();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		} finally {
			if (resOutputStream != null) {
				resOutputStream.close();
			}
		}
	}

	public String getConfigFrom(final List<String> repoFoldersUrls) {
        //TODO: DONE Вызывается из вебскрипта с транзакцией read, дополнительная не нужна
		return AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<String>() {

			@Override
			public String doWork() throws Exception {
				StringBuilder result = new StringBuilder(CONFIG_OPEN_TAG);

				List<NodeRef> nodes = new ArrayList<NodeRef>();
				if (repoFoldersUrls != null) {
					for (String folderUrl : repoFoldersUrls) {
						int idx = folderUrl.indexOf(StoreRef.URI_FILLER);
						if (idx != -1) {
							// assume this is a repository location
							int idx2 = folderUrl.indexOf("/", idx + 3);

							String store = folderUrl.substring(0, idx2);
							String path = folderUrl.substring(idx2);

							StoreRef storeRef = tenantService.getName(new StoreRef(store));
							NodeRef rootNode;

							try {
								rootNode = nodeService.getRootNode(storeRef);
							} catch (InvalidStoreRefException e) {
								throw new ConfigException("Wrong configuration", e);
							}

							List<NodeRef> nodeRefs = searchService.selectNodes(rootNode, path, null, namespaceService, false);

							if (nodeRefs != null) {
								for (NodeRef nodeRef : nodeRefs) {
									if (nodeService.getType(nodeRef).equals(ContentModel.TYPE_CONTENT)) {
										nodes.add(nodeRef);
									}
								}
							}
						}
					}
				}

				for (NodeRef node : nodes) {
					ContentReader configReader = contentService.getReader(node, ContentModel.PROP_CONTENT);
					String content = configReader.getContentString();
					content = content.replace(CONFIG_OPEN_TAG, "");
					content = content.replace(CONFIG_CLOSE_TAG, "");
					result.append(content);
				}

				result.append(CONFIG_CLOSE_TAG);
				return result.toString();
			}
		});
	}
}
