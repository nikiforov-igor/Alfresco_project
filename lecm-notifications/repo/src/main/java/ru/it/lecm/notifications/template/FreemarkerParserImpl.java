package ru.it.lecm.notifications.template;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.TemplateService;
import org.springframework.context.ApplicationContext;
import ru.it.lecm.notifications.beans.TemplateParseException;
import ru.it.lecm.notifications.beans.TemplateRunException;

/**
 *
 * @author vmalygin
 */
public class FreemarkerParserImpl extends AbstractParserImpl {

	private final Map<String, Object> utilsModel = new HashMap<>();
	private final TemplateService templateService;


	public FreemarkerParserImpl(ApplicationContext applicationContext) {
		super(applicationContext);
		utilsModel.put("formatLink", new FormatLinkFunc());
		templateService = applicationContext.getBean("templateService", TemplateService.class);
	}


	@Override
	public String runTemplate(String templateStr, Map<String, NodeRef> objectsMap) throws TemplateParseException, TemplateRunException {
		setObjects(objectsMap);
		Map<String, Object> templateModel = new HashMap<>();
		templateModel.putAll(utilsModel);
		templateModel.putAll(getObjects().getFullMap());
		return templateService.processTemplate(templateStr, templateModel);
	}

	@Override
	public void parseTemplate(String templateStr) throws TemplateParseException {
		//NOP
	}

	private static class FormatLinkFunc implements TemplateMethodModelEx {

		@Override
		public Object exec(List args) throws TemplateModelException {
			if (args == null || args.size() < 2) {
				throw new TemplateModelException("function formatLink requires 2 args: String url, String description");
			}
			return Utils.formatLink((String)args.get(0), (String)args.get(1));
		}
	}
}
