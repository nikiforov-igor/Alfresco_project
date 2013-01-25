package ru.it.lecm.notifications.dashlet.scripts;

import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import ru.it.lecm.notifications.dashlet.beans.NotificationsDashletChannel;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * User: ORakovskaya
 * Date: 23.01.13
 */
public class NotificationDashletWebScriptBean extends BaseScopableProcessorExtension {

    private NotificationsDashletChannel service;

    public void setService(NotificationsDashletChannel service) {
        this.service = service;
    }

    public Scriptable getRecordsByParams(String daysCount) {
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
        List<NodeRef> refs = service.getRecordsByInterval(start, now);
        return createScriptable(refs);
    }

    /**
     * Возвращает массив, пригодный для использования в веб-скриптах
     *
     * @return Scriptable
     */
    private Scriptable createScriptable(List<NodeRef> refs) {
        Object[] results = new Object[refs.size()];
        for (int i = 0; i < results.length; i++) {
            results[i] = new ScriptNode(refs.get(i), service.getServiceRegistry(), getScope());
        }
        return Context.getCurrentContext().newArray(getScope(), results);
    }
}
