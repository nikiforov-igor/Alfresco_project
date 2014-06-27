package ru.it.lecm.contracts.reports;

import ru.it.lecm.reports.generators.GenericDSProviderBase;
import ru.it.lecm.reports.utils.Utils;
import ru.it.lecm.utils.LuceneSearchWrapper;

/**
 * Отчёт по реестру договоров
 * Параметры отчёта:
 * "contractActualOnly" - только актуальные
 * @author rabdullin
 */
public class DSProviderReestrDogovorov extends GenericDSProviderBase {

    private Boolean contractActualOnly;

    @SuppressWarnings("unused")
    public void setContractActualOnly(String value) {
        contractActualOnly = Utils.isStringEmpty(value) ? null : Boolean.parseBoolean(value);
    }

    @Override
    protected LuceneSearchWrapper buildQuery() {
        final LuceneSearchWrapper builder = super.buildQuery();

        boolean hasData = !builder.isEmpty();

        // Контракт актуален: если ещё не истёк срок
        if (Boolean.TRUE.equals(contractActualOnly)) {
            builder.emmit(hasData ? " AND " : "").
                    emmit("(@lecm\\-contract\\:unlimited:true OR @lecm\\-contract\\:endDate:[NOW TO MAX])"); // неограниченый или "истекает позже чем сейчас"
        }

        return builder;
    }
}
