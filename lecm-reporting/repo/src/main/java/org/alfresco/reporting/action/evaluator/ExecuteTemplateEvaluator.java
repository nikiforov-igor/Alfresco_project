package org.alfresco.reporting.action.evaluator;

import javax.faces.context.FacesContext;
import org.alfresco.reporting.ReportingModel;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.web.action.evaluator.BaseActionEvaluator;
import org.alfresco.web.bean.repository.Node;
import org.alfresco.web.bean.repository.Repository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ExecuteTemplateEvaluator extends BaseActionEvaluator {

   private static final long serialVersionUID = -290962952423019612L;
   private static Log logger = LogFactory.getLog(ExecuteTemplateEvaluator.class);


   public boolean evaluate(Node node) {
      boolean found = false;
      ServiceRegistry serviceRegistry = Repository.getServiceRegistry(FacesContext.getCurrentInstance());
      logger.debug("start evaluate");
      if(node.hasAspect(ReportingModel.ASPECT_REPORTING_REPORTABLE) && serviceRegistry.getAuthorityService().hasAdminAuthority()) {
         found = true;
      }

      return found;
   }

}
