function main() {
    var unit = search.findNode(args["nodeRef"]);
    var hasNomenclatureCases = "false";
    if (unit) {
        var linkedNomenclatureCasesAssocs = unit.sourceAssocs["lecm-os:nomenclature-case-visibility-unit-assoc"];
        if (linkedNomenclatureCasesAssocs && linkedNomenclatureCasesAssocs.length > 0) {
            hasNomenclatureCases = "true";
        }
    }
    model.hasNomenclatureCases = hasNomenclatureCases;
}

main();