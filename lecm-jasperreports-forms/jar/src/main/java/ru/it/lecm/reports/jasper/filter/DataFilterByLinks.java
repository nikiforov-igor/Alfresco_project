package ru.it.lecm.reports.jasper.filter;

import org.alfresco.service.cmr.repository.NodeRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.SubstitudeBean;
import ru.it.lecm.reports.api.DataFilter;
import ru.it.lecm.reports.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * User: dbashmakov
 * Date: 23.01.14
 * Time: 9:47
 */
public class DataFilterByLinks implements DataFilter {
    private static final Logger logger = LoggerFactory.getLogger(DataFilterByLinks.class);

    private List<DataFilterDesc> dataLists = new ArrayList<DataFilterDesc>();
    private SubstitudeBean substitudeBean;

    public DataFilterByLinks(SubstitudeBean substitudeBean) {
        this.substitudeBean = substitudeBean;
    }

    public void addFilter(DataFilterDesc desc) {
        dataLists.add(desc);
    }

    @Override
    public boolean isOk(NodeRef id) {
        if (this.dataLists.isEmpty()) {
            // в фильтре ничего не задачно -> любые данные подойдут
            return true;
        }

        for (DataFilterDesc desc : this.dataLists) {
            if (desc.formatString == null) {
                continue;
            }

            Object nodeValue = substitudeBean.getNodeFieldByFormat(id, desc.formatString);
            boolean result = false;
            try {
                if (logger.isDebugEnabled()){
                    logger.debug("Filter by Link: Need values - " + desc.values + ", current Value - " + nodeValue);
                }
                result = desc.filter.isOk(nodeValue, desc.values);
            } catch (ClassCastException ignored) {
            }
            if (!result) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "DataFilterByLinks [ values count: " + dataLists.size() + "\n\t " + Utils.getAsString(dataLists, "\n\t") + "\n]";
    }
}
