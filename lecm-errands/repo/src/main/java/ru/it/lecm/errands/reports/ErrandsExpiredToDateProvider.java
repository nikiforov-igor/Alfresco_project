package ru.it.lecm.errands.reports;

import ru.it.lecm.reports.api.ReportDSContext;
import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.utils.ArgsHelper;
import ru.it.lecm.utils.LuceneSearchWrapper;

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
    protected LuceneSearchWrapper buildQuery(ReportDescriptor descriptor, ReportDSContext parentContext) {
        final LuceneSearchWrapper builder = super.buildQuery(descriptor, parentContext);

        boolean hasData = !builder.isEmpty();

        if (expiredDate != null) {
            Date expired = ArgsHelper.tryMakeDate(expiredDate, null);
            String query = getQueryHelper().emmitDateIntervalCheck("lecm-errands:limitation-date", null, expired);
            builder.emmit(hasData ? " AND " : "").emmit(query);
        }
        return builder;
    }

}
