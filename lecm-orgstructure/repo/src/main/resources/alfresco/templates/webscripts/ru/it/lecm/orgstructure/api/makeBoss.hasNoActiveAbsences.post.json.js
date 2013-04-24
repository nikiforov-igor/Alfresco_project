/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

logger.log("makeBoss.hasNoActiveAbsences!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

model.hasNoActiveAbsences = true;
model.reason = '';

var inputNodeRef = json.get("nodeRef");
logger.log("makeBoss.hasNoActiveAbsences!! Given Staff: "+ inputNodeRef);

var checkStuff = function( nr, targetStaff ){
    logger.log("CheckingStuff:" +nr );
    if (nr){
        var employee = orgstructure.getEmployeeByPosition( nr);
        logger.log(jsonUtils.toJSONString(employee));
        if (employee){
            logger.log("makeBoss.hasNoActiveAbsences!! Checking absence for: "+ employee.nodeRef);
            if (absence.isEmployeeAbsentToday(employee.nodeRef + "")){
                    logger.log("makeBoss.hasNoActiveAbsences! Employee CName: " + employee.properties["cm:name"] + " target: " + targetStaff);
                    model.reason  = model.reason + employee.properties["cm:name"] + '\n';
                    model.hasNoActiveAbsences = false;
            }
        }
    }

};




if (inputNodeRef != null) {
    logger.log("makeBoss.hasNoActiveAbsences!! NodeRef is not null! Ok!");
    var node = search.findNode(inputNodeRef);
    var allStaffs = orgstructure.getAllStaffLists(node.parent.nodeRef);
    logger.log(jsonUtils.toJSONString(allStaffs));
    for each(var staff in allStaffs) {
        if (staff.nodeRef == ("" + inputNodeRef)) { // назначаемая ветка
            logger.log("makeBoss.hasNoActiveAbsences!! TargetStaff: "+ staff.nodeRef);
            checkStuff(staff.nodeRef, true);
        } else if (staff.properties["lecm-orgstr:staff-list-is-boss"] == true) {
            logger.log("makeBoss.hasNoActiveAbsences!! SourceStaff: " + staff.nodeRef);
            checkStuff(staff.nodeRef, false);
        }
    }
}