package ru.it.lecm.errands.reports;

import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.reports.api.ReportDSContext;
import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.generators.GenericDSProviderBase;
import ru.it.lecm.reports.utils.LuceneSearchWrapper;
import ru.it.lecm.reports.utils.Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: dbashmakov
 * Date: 10.04.2014
 * Time: 9:42
 */
public class ErrandsOutOfTimeProvider extends GenericDSProviderBase {
    private Boolean importantOnly;

    @SuppressWarnings("unused")
    private List<String> execUnits;

    private List<String> executorsRefs = new ArrayList<String>();

    @SuppressWarnings("unused")
    public void setImportantOnly(String value) {
        this.importantOnly = Utils.isStringEmpty(value) ? null : Boolean.parseBoolean(value);
    }

    @SuppressWarnings("unused")
    public void setExecUnits(List<String> unitRefs) {
        if (unitRefs != null && !unitRefs.isEmpty()) {
            Set<NodeRef> allSelectedUnits = new HashSet<>();
            for (String ref : unitRefs) {
                if (!ref.isEmpty()){
                    allSelectedUnits.add(new NodeRef(ref));
                    allSelectedUnits.addAll(getServices().getOrgstructureService().getSubUnits(new NodeRef(ref), true, true));
                }
            }
            Set<NodeRef> employeesSet = new HashSet<>();
            for (NodeRef selectedUnit : allSelectedUnits) {
                employeesSet.addAll(getServices().getOrgstructureService().getUnitEmployees(selectedUnit));
            }

            if (allSelectedUnits.size() > 0) {
                executorsRefs.add("NOT_REF");
            }

            for (NodeRef nodeRef : employeesSet) {
                executorsRefs.add(nodeRef.toString());
            }
        }
    }

    @Override
    protected LuceneSearchWrapper buildQuery(ReportDescriptor descriptor, ReportDSContext parentContext) {
        final LuceneSearchWrapper builder = super.buildQuery(descriptor, parentContext);

        if (Boolean.TRUE.equals(importantOnly)) {
            builder.emmit(!builder.isEmpty() ? " AND " : "").
                    emmit("@lecm\\-errands\\:is\\-important:true");
        }

        boolean hasData = !builder.isEmpty();

        if (!executorsRefs.isEmpty()) {
            builder.emmit(hasData ? " AND (" : "");
            StringBuilder executorsQuery = new StringBuilder();
            for (String executorRef : executorsRefs) {
                executorsQuery.append("@lecm\\-errands\\:executor\\-assoc\\-ref:\"").append(executorRef).append("\" OR ");
            }
            builder.emmit(executorsQuery.substring(0, executorsQuery.length() - 4));
            builder.emmit(hasData ? ")" : "");
        }
        return builder;
    }
}
