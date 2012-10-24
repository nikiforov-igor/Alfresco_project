var organization = companyhome.childByNamePath("Организация");
if (organization == null || organization == "") {
    // Генерация структуры директорий
    //Организация
    organization = companyhome.createNode("Организация", "lecm-orgstr:organization");

    //структура директорий
    var structure = organization.createNode("Структура", "lecm-orgstr:organization-structure");
    var offices = organization.createNode("Офисы", "lecm-orgstr:office-container");
    var emploeers = organization.createNode("Сотрудники", "lecm-orgstr:employee-container");
    var personal_data = organization.createNode("Персональные данные", "lecm-orgstr:personal-data-container");
    var officials = organization.createNode("Должностные лица", "lecm-orgstr:official-container");
    var workforces = organization.createNode("Трудовые ресурсы", "lecm-orgstr:workforce-container");
    var composition = organization.createNode("Составы подразделений", "lecm-orgstr:unit-composition-container");
    var pr_register = organization.createNode("Реестр проектов", "lecm-orgstr:project-register");
    var staff_list = organization.createNode("Штатное расписание", "lecm-orgstr:staff-list");

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
    organization.createAssociation(structure, "lecm-orgstr:org-structure-assoc");
    organization.createAssociation(pr_register, "lecm-orgstr:org-projects-assoc");
    organization.createAssociation(staff_list, "lecm-orgstr:org-staff-assoc");

    // офисы
    var of1 = offices.createNode("Офис 1", "lecm-orgstr:office");
    of1.properties["lecm-orgstr:office-full-name"] = "Офис в Москве";
    of1.properties["lecm-orgstr:office-short-name"] = "Офис-М";
    of1.properties["lecm-orgstr:office-phone"] = "1-11-11";
    of1.properties["lecm-orgstr:office-fax"] = "111-11-11";
    of1.properties["lecm-orgstr:office-address"] = "115280, г. Москва, ул. Ленинская Слобода, д.19, стр. 6";
    of1.save();
    var of2 = offices.createNode("Офис 2", "lecm-orgstr:office");
    of2.properties["lecm-orgstr:office-full-name"] = "Офис в Обнинске";
    of2.properties["lecm-orgstr:office-short-name"] = "Офис-О";
    of2.properties["lecm-orgstr:office-phone"] = "2-22-22";
    of2.properties["lecm-orgstr:office-fax"] = "222-22-22";
    of2.properties["lecm-orgstr:office-address"] = "249038, г. Обнинск, ул. Гурьянова, д.21";
    of2.save();
    var of3 = offices.createNode("Офис 3", "lecm-orgstr:office");
    of3.properties["lecm-orgstr:office-full-name"] = "Центральный офис";
    of3.properties["lecm-orgstr:office-short-name"] = "ЦО";
    of3.properties["lecm-orgstr:office-phone"] = "3-33-33";
    of3.properties["lecm-orgstr:office-fax"] = "333-33-33";
    of3.properties["lecm-orgstr:office-address"] = "115280, г. Москва, ул. Ленинская Слобода, д.19, стр. 6";
    of3.save();

    //должности
    var d1 = staff_list.createNode("Директор", "lecm-orgstr:position");
    d1.properties["lecm-orgstr:position-description"] = "Генеральный директор";
    d1.properties["lecm-orgstr:position-name-g"] = "Директора";
    d1.properties["lecm-orgstr:position-name-d"] = "Директору";
    d1.save();
    var d2 = staff_list.createNode("Тестировщик", "lecm-orgstr:position");
    d2.properties["lecm-orgstr:position-description"] = "Тестировщик системы";
    d2.properties["lecm-orgstr:position-name-g"] = "Тестировщика";
    d2.properties["lecm-orgstr:position-name-d"] = "Тестирвщику";
    d2.save();
    var d3 = staff_list.createNode("Аналитик", "lecm-orgstr:position");
    d3.properties["lecm-orgstr:position-description"] = "Аналитик";
    d3.properties["lecm-orgstr:position-name-g"] = "Аналитика";
    d3.properties["lecm-orgstr:position-name-d"] = "Аналитику";
    d3.save();
    var d4 = staff_list.createNode("Программист", "lecm-orgstr:position");
    d4.properties["lecm-orgstr:position-description"] = "Инженер-программист";
    d4.properties["lecm-orgstr:position-name-g"] = "Программиста";
    d4.properties["lecm-orgstr:position-name-d"] = "программисту";
    d4.save();

    // персональные данные
    var pd1 = personal_data.createNode("ПД-Иванов", "lecm-orgstr:personal-data");
    pd1.properties["lecm-orgstr:person-data-address"] = "адрес 1";
    pd1.properties["lecm-orgstr:person-data-email"] = "ivanov@it.ru";
    pd1.properties["lecm-orgstr:person-data-date"] = new Date("1987", "02", "25");
    pd1.properties["lecm-orgstr:person-data-service-id"] = "11111111";
    pd1.save();
    var pd2 = personal_data.createNode("ПД-Петров", "lecm-orgstr:personal-data");
    pd2.properties["lecm-orgstr:person-data-address"] = "адрес 2";
    pd2.properties["lecm-orgstr:person-data-email"] = "ivanov@it.ru";
    pd2.properties["lecm-orgstr:person-data-date"] = new Date("1986", "02", "25");
    pd2.properties["lecm-orgstr:person-data-service-id"] = "22222222";
    pd2.save();
    var pd3 = personal_data.createNode("ПД-Сидоров", "lecm-orgstr:personal-data");
    pd3.properties["lecm-orgstr:person-data-address"] = "адрес 3";
    pd3.properties["lecm-orgstr:person-data-email"] = "ivanov@it.ru";
    pd3.properties["lecm-orgstr:person-data-date"] = new Date("1989", "02", "25");
    pd3.properties["lecm-orgstr:person-data-service-id"] = "33333333";
    pd3.save();
    var pd4 = personal_data.createNode("ПД-Башмаков", "lecm-orgstr:personal-data");
    pd4.properties["lecm-orgstr:person-data-address"] = "адрес 4";
    pd4.properties["lecm-orgstr:person-data-email"] = "ivanov@it.ru";
    pd4.properties["lecm-orgstr:person-data-date"] = new Date("1977", "02", "25");
    pd4.properties["lecm-orgstr:person-data-service-id"] = "44444444";
    pd4.save();
    var pd5 = personal_data.createNode("ПД-Никитина", "lecm-orgstr:personal-data");
    pd5.properties["lecm-orgstr:person-data-address"] = "адрес 5";
    pd5.properties["lecm-orgstr:person-data-email"] = "ivanov@it.ru";
    pd5.properties["lecm-orgstr:person-data-date"] = new Date("1987", "07", "25");
    pd5.properties["lecm-orgstr:person-data-service-id"] = "55555555";
    pd5.save();
    var pd6 = personal_data.createNode("ПД-Петрушевская", "lecm-orgstr:personal-data");
    pd6.properties["lecm-orgstr:person-data-address"] = "адрес 6";
    pd6.properties["lecm-orgstr:person-data-email"] = "ivanov@it.ru";
    pd6.properties["lecm-orgstr:person-data-date"] = new Date("1987", "07", "26");
    pd6.properties["lecm-orgstr:person-data-service-id"] = "66666666";
    pd6.save();

    // Сотрудник и персональные данные
    var e1 = emploeers.createNode("Иванов И", "lecm-orgstr:employee");
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
    e1.createAssociation(pd1, "lecm-orgstr:employee-person-data-assoc");

    var e2 = emploeers.createNode("Петров П", "lecm-orgstr:employee");
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
    e2.createAssociation(pd2, "lecm-orgstr:employee-person-data-assoc");

    var e3 = emploeers.createNode("Сидоров С.С", "lecm-orgstr:employee");
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
    e3.createAssociation(pd3, "lecm-orgstr:employee-person-data-assoc");

    var e4 = emploeers.createNode("Башмаков Д.И", "lecm-orgstr:employee");
    e4.properties["lecm-orgstr:employee-first-name"] = "Денис";
    e4.properties["lecm-orgstr:employee-middle-name"] = "Игоревич";
    e4.properties["lecm-orgstr:employee-last-name"] = "Башмаков";
    e4.properties["lecm-orgstr:employee-sex"] = "MALE";
    e4.properties["lecm-orgstr:employee-phone"] = "4-11-11";
    e4.properties["lecm-orgstr:employee-number"] = "4";
    e4.properties["lecm-orgstr:employee-is-system-user"] = true;
    e4.properties["lecm-orgstr:employee-fio-g"] = "Башмакова";
    e4.properties["lecm-orgstr:employee-fio-d"] = "Башмакову";
    var ppl = people.createPerson("dbashmakov", "Denis", "Bashmakov", "dbashmakov@it.ru", "123456", true);
    if (ppl == null) {
        ppl = people.getPerson("dbashmakov");
    }
    e4.save();
    e4.createAssociation(pd4, "lecm-orgstr:employee-person-data-assoc");
    e4.createAssociation(ppl, "lecm-orgstr:employee-person-assoc");

    var e5 = emploeers.createNode("Никитина Л.А", "lecm-orgstr:employee");
    e5.properties["lecm-orgstr:employee-first-name"] = "Людмила";
    e5.properties["lecm-orgstr:employee-middle-name"] = "Александровна";
    e5.properties["lecm-orgstr:employee-last-name"] = "Никитина";
    e5.properties["lecm-orgstr:employee-sex"] = "FEMALE";
    e5.properties["lecm-orgstr:employee-phone"] = "5-11-11";
    e5.properties["lecm-orgstr:employee-number"] = "5";
    e5.properties["lecm-orgstr:employee-is-system-user"] = true;
    e5.properties["lecm-orgstr:employee-fio-g"] = "Никитиной";
    e5.properties["lecm-orgstr:employee-fio-d"] = "Никитиной";
    ppl = people.createPerson("lnikitina", "Luda", "Nikitina", "lnikitina@it.ru", "123456", true);
    if (ppl == null) {
        ppl = people.getPerson("lnikitina");
    }
    e5.save();
    e5.createAssociation(pd5, "lecm-orgstr:employee-person-data-assoc");
    e5.createAssociation(ppl, "lecm-orgstr:employee-person-assoc");

    var e6 = emploeers.createNode("Петрушевская Е.А", "lecm-orgstr:employee");
    e6.properties["lecm-orgstr:employee-first-name"] = "Екатерина";
    e6.properties["lecm-orgstr:employee-middle-name"] = "Алексеевна";
    e6.properties["lecm-orgstr:employee-last-name"] = "Петрушевская";
    e6.properties["lecm-orgstr:employee-sex"] = "FEMALE";
    e6.properties["lecm-orgstr:employee-phone"] = "6-11-11";
    e6.properties["lecm-orgstr:employee-number"] = "6";
    e6.properties["lecm-orgstr:employee-is-system-user"] = false;
    e6.properties["lecm-orgstr:employee-fio-g"] = "Петрушевской";
    e6.properties["lecm-orgstr:employee-fio-d"] = "Петрушевской";
    e6.save();
    e6.createAssociation(pd6, "lecm-orgstr:employee-person-data-assoc");

    //подразделения
    var direction = structure.createNode("Руководство организации", "lecm-orgstr:organization-unit", "lecm-orgstr:org-unit-assoc");
    direction.properties["lecm-orgstr:element-full-name"] = "Руководство организации";
    direction.properties["lecm-orgstr:element-short-name"] = "Руководство";
    direction.properties["lecm-orgstr:unit-code"] = "PO";
    direction.properties["lecm-orgstr:unit-type"] = "SEGREGATED";
    direction.properties["lecm-orgstr:unii-is_exists"] = true;
    direction.save();

    var unit1 = structure.createNode("Главное отделение", "lecm-orgstr:organization-unit", "lecm-orgstr:org-unit-assoc");
    unit1.properties["lecm-orgstr:element-full-name"] = "Главное отделение";
    unit1.properties["lecm-orgstr:element-short-name"] = "Отдел 1";
    unit1.properties["lecm-orgstr:unit-code"] = "O1";
    unit1.properties["lecm-orgstr:unit-type"] = "SEGREGATED";
    unit1.properties["lecm-orgstr:unit-is-exists"] = true;
    unit1.save();
    unit1.createAssociation(of1, "lecm-orgstr:unit-offices-assoc");
    unit1.createAssociation(of2, "lecm-orgstr:unit-offices-assoc");

    var unit11 = unit1.createNode("Отдел внедрения", "lecm-orgstr:organization-unit", "lecm-orgstr:unit-inner-assoc");
    unit11.properties["lecm-orgstr:element-full-name"] = "Отдел внедрения";
    unit11.properties["lecm-orgstr:element-short-name"] = "Внедрение";
    unit11.properties["lecm-orgstr:unit-code"] = "O1.1";
    unit11.properties["lecm-orgstr:unit-type"] = "SEGREGATED";
    unit11.properties["lecm-orgstr:unit-is-exists"] = true;
    unit11.save();
    unit11.createAssociation(of3, "lecm-orgstr:unit-offices-assoc");

    var unit111 = unit11.createNode("Обнинский Отдел внедрения", "lecm-orgstr:organization-unit", "lecm-orgstr:unit-inner-assoc");
    unit111.properties["lecm-orgstr:element-full-name"] = "Обнинский Отдел внедрения";
    unit111.properties["lecm-orgstr:element-short-name"] = "Обнинское отделение";
    unit111.properties["lecm-orgstr:unit-code"] = "O1.1.1";
    unit111.properties["lecm-orgstr:unit-type"] = "SEGREGATED";
    unit111.properties["lecm-orgstr:unit-is-exists"] = true;
    unit111.save();

    var unit2 = structure.createNode("BPM Отдел", "lecm-orgstr:organization-unit", "lecm-orgstr:org-unit-assoc");
    unit2.properties["lecm-orgstr:element-full-name"] = "BPM Отдел";
    unit2.properties["lecm-orgstr:element-short-name"] = "BPM";
    unit2.properties["lecm-orgstr:unit-code"] = "O2";
    unit2.properties["lecm-orgstr:unit-type"] = "SEPARATED";
    unit2.properties["lecm-orgstr:unit-is-exists"] = true;
    unit2.save();
    unit2.createAssociation(of3, "lecm-orgstr:unit-offices-assoc");
    unit2.createAssociation(of2, "lecm-orgstr:unit-offices-assoc");

    var o1 = officials.createNode("ДЛ-Башмаков", "lecm-orgstr:official");
    o1.properties["lecm-orgstr:official-can-facsimile"] = true;
    o1.save();
    o1.createAssociation(e4, "lecm-orgstr:official-employee-assoc");

    var o2 = officials.createNode("ДЛ-Никитина", "lecm-orgstr:official");
    o2.properties["lecm-orgstr:official-can-facsimile"] = true;
    o2.save();
    o2.createAssociation(e5, "lecm-orgstr:official-employee-assoc");

    var o3 = officials.createNode("ДЛ-Петров", "lecm-orgstr:official");
    o3.properties["lecm-orgstr:official-can-facsimile"] = false;
    o3.save();
    o3.createAssociation(e2, "lecm-orgstr:official-employee-assoc");

    unit1.createAssociation(o2, "lecm-orgstr:element-official-assoc");
    organization.createAssociation(o3, "lecm-orgstr:element-official-assoc");
}

var branch = [];
var nodes;
if (args["nodeRef"] == null || args["nodeRef"] == "") {
    if (args["onlyStructure"] == null || args["onlyStructure"] == "") {
        nodes = orgstructure.getRoots("_ROOT_", organization.getNodeRef().toString());
    } else {
        var structure = organization.childByNamePath("Структура");
        nodes = orgstructure.getStructure("organization", organization.getNodeRef().toString());
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
        title = items[index].title;
        type = items[index].type;
        nodeRef = items[index].nodeRef;
        isLeaf = items[index].isLeaf;
        dsUri = (items[index].dsUri != null ? items[index].dsUri : null);
        childType = (items[index].childType != null ? items[index].childType : null);
        childAssoc = (items[index].childAssoc != null ? items[index].childAssoc : null);
        pattern = (items[index].namePattern != null ? items[index].namePattern : null);
        branch.push({
            title:title,
            type:type,
            nodeRef:nodeRef,
            isLeaf:"" + isLeaf,
            dsUri:dsUri,
            childType:childType,
            childAssoc:childAssoc,
            pattern:pattern
        });
    }
}
