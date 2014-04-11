package ru.it.lecm.errands.reports;

import ru.it.lecm.reports.generators.GenericDSProviderBase;
import ru.it.lecm.reports.generators.LucenePreparedQuery;
import ru.it.lecm.utils.LuceneSearchBuilder;

import java.util.Date;

/**
 * User: dbashmakov
 * Date: 10.04.2014
 * Time: 9:42
 */
public class ErrandsExpiredToDateProvider extends ErrandsOutOfTimeProvider {

    private Date expiredDate;

    @SuppressWarnings("unused")
    public void setExpiredDate(Date expiredDate) {
        this.expiredDate = expiredDate;
    }

    @Override
    protected LucenePreparedQuery buildQuery() {
        final LucenePreparedQuery result = super.buildQuery();

        final LuceneSearchBuilder builder = new LuceneSearchBuilder(getServices().getServiceRegistry().getNamespaceService());
        builder.emmit(result.luceneQueryText());

        boolean hasData = !builder.isEmpty();

        if (expiredDate != null) {
            builder.emmit(hasData ? " AND " : "").
                    emmit("@lecm\\-errands\\:limitation\\-date:[MIN TO " + GenericDSProviderBase.DateFormatISO8601.format(expiredDate) + "]");
        }
        result.setLuceneQueryText(builder.toString());
        return result;
    }

}
