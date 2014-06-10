package ru.it.lecm.errands.reports;

import ru.it.lecm.reports.generators.GenericDSProviderBase;
import ru.it.lecm.reports.generators.LucenePreparedQuery;
import ru.it.lecm.reports.utils.ArgsHelper;
import ru.it.lecm.utils.LuceneSearchBuilder;

import java.util.Date;

/**
 * User: dbashmakov
 * Date: 10.04.2014
 * Time: 9:42
 */
public class ErrandsExpiredToDateProvider extends ErrandsOutOfTimeProvider {

    //Хак, чтобы избежать ошибки при конвертировании даты. Нам приходит дата в строковом формате
    private String expiredDate;

    @SuppressWarnings("unused")
    public void setExpiredDate(String expiredDate) {
        this.expiredDate = expiredDate;
    }

    @Override
    protected LucenePreparedQuery buildQuery() {
        final LucenePreparedQuery result = super.buildQuery();

        final LuceneSearchBuilder builder = new LuceneSearchBuilder(getServices().getServiceRegistry().getNamespaceService());
        builder.emmit(result.luceneQueryText());

        boolean hasData = !builder.isEmpty();

        if (expiredDate != null) {
            Date expired = ArgsHelper.tryMakeDate(expiredDate, null);
            builder.emmit(hasData ? " AND " : "").
                    emmit("@lecm\\-errands\\:limitation\\-date:[MIN TO " + GenericDSProviderBase.DateFormatISO8601.format(expired) + "]");
        }
        result.setLuceneQueryText(builder.toString());
        return result;
    }

}
