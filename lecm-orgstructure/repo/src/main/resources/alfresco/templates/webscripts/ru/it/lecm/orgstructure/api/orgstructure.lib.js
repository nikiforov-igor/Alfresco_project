/**
 * Получение руководителя для подразделения
 *
 * @param node объект типа lecm-orgstr:organization-unit
 * @return {ScriptNode} руководитель подразделения или null
 */
function findBoss(node){
    // Получаем список штатных расписаний
    var staffLists = node.getChildAssocsByType("lecm-orgstr:staff-list");
    var bossStaff = null, boss = null;
    for (var index in staffLists){// находим то, которое помечено как руководящая позиция
        if(staffLists[index].properties["lecm-orgstr:staff-list-is-boss"]) {
            bossStaff = staffLists[index];
            break;
        }
    }
    if (bossStaff){
        //вытаскиваем ссылку на сотрудника и непосредственно сотрудника (если ссылка имеется)
        var employeeLink = bossStaff.getChildAssocsByType("lecm-orgstr:employee-link");
        if(employeeLink && employeeLink[0]){
            boss = employeeLink[0].assocs["lecm-orgstr:employee-link-employee-assoc"][0];// сотрудник всегда существует и только один
        }
    }

    if (!boss){ // если не нашли руководителя в текущем подразделении, пробуем найти в вышестоящем
        var parent = node.getParent();
        if(parent.getTypeShort() == "lecm-orgstr:organization-unit"){//пока идем по дереву структуры
            boss = findBoss(parent);
        } else { // дошли до директории Структура, пробуем получить руководителя Организации
            var organization = companyhome.childByNamePath("Организация");
            var  orgBoss = organization.assocs["lecm-orgstr:org-boss-assoc"];
            if (orgBoss && orgBoss[0]){
                boss = orgBoss[0];
            }
        }
    }
    return boss;
}