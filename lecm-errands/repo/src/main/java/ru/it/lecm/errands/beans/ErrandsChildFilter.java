package ru.it.lecm.errands.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.documents.beans.DocumentFilter;

/**
 * User: AIvkin
 * Date: 08.08.13
 * Time: 16:43
 */
public class ErrandsChildFilter extends DocumentFilter {
	final private static Logger logger = LoggerFactory.getLogger(ErrandsChildFilter.class);

	final public static String ID = "errandsChildFilter";

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public String getQuery(Object[] args) {
		String query = "";
		try {
			NodeRef nodeRef = new NodeRef(args[0].toString(), args[1].toString(), args[2].toString());

			if (nodeService.exists(nodeRef)) {
				final String PROP_ADDITIONAL_DOCUMENT =
						ErrandsServiceImpl.PROP_ERRANDS_ADDITIONAL_DOCUMENT_REF.toPrefixString(namespaceService).replaceAll(":", "\\\\:").replaceAll("-", "\\\\-");

				query += "(@" + PROP_ADDITIONAL_DOCUMENT + ":\"" + nodeRef.toString().replace(":", "\\:") + "\")";
			}
		} catch (Exception ignored) {
			logger.warn("Incorrect filter! Filter args:" + args);
		}
		return query;
	}
}
