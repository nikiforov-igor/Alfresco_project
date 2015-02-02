var nodes = search.luceneSearch('(TYPE:"lecm-os:nomenclature-year-section" AND @lecm-os\\:nomenclature-year-section-status:"CLOSED") OR (TYPE:"lecm-os:nomenclature-unit-section" AND @lecm-os\\:nomenclature-unit-section-status:"CLOSED")');
model.result = nodes;
