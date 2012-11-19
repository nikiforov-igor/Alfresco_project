var organization = companyhome.childByNamePath("Организация");
if (organization == null || organization == "") {
    // Генерация структуры директорий
    //Организация
    organization = companyhome.createNode("Организация", "lecm-orgstr:organization");

    /**
     Структура директорий
     Организация
        ---Структура
        ---Сотрудники
        ---Персональные данные
     */
    var structure = organization.createNode("Структура", "lecm-orgstr:structure");
    var employees = organization.createNode("Сотрудники", "lecm-orgstr:employees");
    var personal_data = organization.createNode("Персональные данные", "lecm-orgstr:personal-data-container");

    // генерация тестовых данных
    // организация
    organization.properties["lecm-orgstr:element-full-name"] = "Логика Бизнеса 2.0";
    organization.properties["lecm-orgstr:element-short-name"] = "ЛБ 2.0";
    organization.properties["lecm-orgstr:org-leg-address"] = "115280, г. Москва, ул. Ленинская Слобода, д.19, стр. 6";
    organization.properties["lecm-orgstr:org-act-address"] = "115280, г. Москва, ул. Ленинская Слобода, д.19, стр. 6";
    organization.properties["lecm-orgstr:org-off-site"] = "http://www.blogic20.ru";
    organization.properties["lecm-orgstr:org-phone"] = "+7 (495) 974-79-79; 974-79-80";
    organization.properties["lecm-orgstr:org-fax"] = "+7 (495) 974-79-90";
    organization.properties["lecm-orgstr:org-email"] = "info@blogic20.ru";
    organization.properties["lecm-orgstr:org-incorporation-form"] = "ООО";
    organization.properties["lecm-orgstr:org-founding-docs"] = "Документ-основание";
    organization.properties["lecm-orgstr:org-ownership-typ"] = "Частная компания";
    organization.save();

    // Сотрудники
    var e1 = employees.createNode("Иванов И", "lecm-orgstr:employee");
    e1.properties["lecm-orgstr:employee-first-name"] = "Иван";
    e1.properties["lecm-orgstr:employee-middle-name"] = "Иванович";
    e1.properties["lecm-orgstr:employee-last-name"] = "Иванов";
    e1.properties["lecm-orgstr:employee-sex"] = "MALE";
    e1.properties["lecm-orgstr:employee-phone"] = "1-11-11";
    e1.properties["lecm-orgstr:employee-number"] = "1";
    e1.properties["lecm-orgstr:employee-is-system-user"] = false;
    e1.properties["lecm-orgstr:employee-fio-g"] = "Иванова";
    e1.properties["lecm-orgstr:employee-fio-d"] = "Иванову";
    e1.save();

    var e2 = employees.createNode("Петров П", "lecm-orgstr:employee");
    e2.properties["lecm-orgstr:employee-first-name"] = "Петр";
    e2.properties["lecm-orgstr:employee-middle-name"] = "Петрович";
    e2.properties["lecm-orgstr:employee-last-name"] = "Петров";
    e2.properties["lecm-orgstr:employee-sex"] = "MALE";
    e2.properties["lecm-orgstr:employee-phone"] = "2-11-11";
    e2.properties["lecm-orgstr:employee-number"] = "2";
    e2.properties["lecm-orgstr:employee-is-system-user"] = false;
    e2.properties["lecm-orgstr:employee-fio-g"] = "Петрова";
    e2.properties["lecm-orgstr:employee-fio-d"] = "Петрову";
    e2.save();

    var e3 = employees.createNode("Сидоров С.С", "lecm-orgstr:employee");
    e3.properties["lecm-orgstr:employee-first-name"] = "Сидор";
    e3.properties["lecm-orgstr:employee-middle-name"] = "Сидорович";
    e3.properties["lecm-orgstr:employee-last-name"] = "Сидоров";
    e3.properties["lecm-orgstr:employee-sex"] = "MALE";
    e3.properties["lecm-orgstr:employee-phone"] = "3-11-11";
    e3.properties["lecm-orgstr:employee-number"] = "3";
    e3.properties["lecm-orgstr:employee-is-system-user"] = false;
    e3.properties["lecm-orgstr:employee-fio-g"] = "Сидорова";
    e3.properties["lecm-orgstr:employee-fio-d"] = "Сидорову";
    e3.save();

    //подразделения
    var direction = structure.createNode("Руководство организации", "lecm-orgstr:organization-unit");
    direction.properties["lecm-orgstr:element-full-name"] = "Руководство организации";
    direction.properties["lecm-orgstr:element-short-name"] = "Руководство";
    direction.properties["lecm-orgstr:unit-code"] = "PO";
    direction.properties["lecm-orgstr:unit-type"] = "SEGREGATED";
    direction.properties["lecm-orgstr:unii-is_exists"] = true;
    direction.save();

    var unit1 = structure.createNode("Главное отделение", "lecm-orgstr:organization-unit");
    unit1.properties["lecm-orgstr:element-full-name"] = "Главное отделение";
    unit1.properties["lecm-orgstr:element-short-name"] = "Отдел 1";
    unit1.properties["lecm-orgstr:unit-code"] = "O1";
    unit1.properties["lecm-orgstr:unit-type"] = "SEGREGATED";
    unit1.properties["lecm-dic:active"] = true;
    unit1.save();

    var unit11 = unit1.createNode("Отдел внедрения", "lecm-orgstr:organization-unit");
    unit11.properties["lecm-orgstr:element-full-name"] = "Отдел внедрения";
    unit11.properties["lecm-orgstr:element-short-name"] = "Внедрение";
    unit11.properties["lecm-orgstr:unit-code"] = "O1.1";
    unit11.properties["lecm-orgstr:unit-type"] = "SEGREGATED";
    unit11.properties["lecm-dic:active"] = true;
    unit11.save();

    var unit111 = unit11.createNode("Обнинский Отдел внедрения", "lecm-orgstr:organization-unit");
    unit111.properties["lecm-orgstr:element-full-name"] = "Обнинский Отдел внедрения";
    unit111.properties["lecm-orgstr:element-short-name"] = "Обнинское отделение";
    unit111.properties["lecm-orgstr:unit-code"] = "O1.1.1";
    unit111.properties["lecm-orgstr:unit-type"] = "SEGREGATED";
    unit111.properties["lecm-dic:active"] = true;
    unit111.save();

    var unit2 = structure.createNode("BPM Отдел", "lecm-orgstr:organization-unit");
    unit2.properties["lecm-orgstr:element-full-name"] = "BPM Отдел";
    unit2.properties["lecm-orgstr:element-short-name"] = "BPM";
    unit2.properties["lecm-orgstr:unit-code"] = "O2";
    unit2.properties["lecm-orgstr:unit-type"] = "SEPARATED";
    unit2.properties["lecm-dic:active"] = true;
    unit2.save();
}

var branch = [];
var nodes;
if (args["nodeRef"] == null || args["nodeRef"] == "") {
    var orgFolderRef = organization.getNodeRef().toString();
    if (args["onlyRoot"] == null || args["onlyRoot"] == "") {
        nodes = orgstructure.getRoots("_ROOT_",orgFolderRef);
    } else {
        nodes = orgstructure.getStructure("organization",orgFolderRef);
    }
} else {
    nodes = orgstructure.getStructure(args["type"], args["nodeRef"]);
}
//process response
var oNodes = eval("(" + nodes + ")");

addItems(branch, oNodes);

model.branch = branch;

function addItems(branch, items) {
    for (var index in items) {
        title = (items[index].title != null ? items[index].title : "");
        type = (items[index].type != null ? items[index].type : "");
        nodeRef = items[index].nodeRef;
        isLeaf = (items[index].isLeaf != null ? items[index].isLeaf : true);
        itemType = (items[index].itemType != null ? items[index].itemType : "");
        pattern = (items[index].namePattern != null ? items[index].namePattern : "");
        branch.push({
            title:title,
            type:type,
            nodeRef:nodeRef,
            isLeaf:"" + isLeaf,
            itemType:itemType,
            pattern:pattern
        });
    }
}
