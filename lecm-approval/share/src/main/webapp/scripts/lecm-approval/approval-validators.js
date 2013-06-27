/* JSHint Options */
/* global Alfresco */

if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Approval = LogicECM.module.Approval || {};

LogicECM.module.Approval.workflowFormValidator = function( field, args, event, form, silent, message ) {

    "use strict";

    var assignees,
        assigneesLength,
        assigneesTail,

        currentDateString,
        currentDate,

        nextDateString,
        nextDate,

        i = 0,

        today = new Date(),
        selectedDateString = field.value,
        selectedDate = Alfresco.util.fromISO8601( selectedDateString ), // 1970 for field.value === "", it's OK!

        workflowForm = Alfresco.util.ComponentManager.get( "workflow-form-cntrl" ),

        assigneeData = null,
        assigneeDueDateProperty = "prop_lecm-al_assignees-item-due-date",

        approvalType = YAHOO.util.Dom.get( "prop_lecmApprove_approvalType" ).value;

    try {
        assignees = workflowForm.widgets.assigneesDatagrid.widgets.dataTable.getRecordSet().getRecords();
    } catch ( error ) {
        return false;
    }

    assigneesLength = assignees.length;
    assigneesTail = assigneesLength - 1;

    today.setHours( 0 );
    today.setMinutes( 0 );
    today.setSeconds( 0 );
    today.setMilliseconds( 0 );

    // Если общий срок согласования находится в прошлом
    if( selectedDate - today < 0 ) {
        return false;
    }

    // Если список согласующих пуст
    if( assigneesLength === 0 ) {
        return false;
    }

    if( approvalType === "SEQUENTIAL" ) {
        for( ; i < assigneesLength; i++ ) {
            assigneeData = assignees[ i ].getData( "itemData" );

            if( assigneeData[ assigneeDueDateProperty ] &&
                assigneeData[ assigneeDueDateProperty ].value ) {

                currentDateString = assigneeData[ assigneeDueDateProperty ].value;
                currentDate = Alfresco.util.fromISO8601( currentDateString );

                /* jshint eqnull:true */
                if( currentDateString == null || // Если не задан индивидуальный срок согласования
                    selectedDateString == null || // Или не задан общий срок согласования
                    selectedDate - currentDate < 0 ) { // Или срок согласования согласующего больше общего срока

                    return false;
                }
                /* jshint eqnull:false */
            } else {
                return false;
            }
        }

        for( i = 0; i < assigneesTail; i++ ) {
            assigneeData = assignees[ i ].getData( "itemData" );
            currentDateString = assigneeData[ assigneeDueDateProperty ].value;
            currentDate = Alfresco.util.fromISO8601( currentDateString );

            assigneeData = assignees[ i + 1 ].getData( "itemData" );
            nextDateString = assigneeData[ assigneeDueDateProperty ].value;
            nextDate = Alfresco.util.fromISO8601( nextDateString );

            if( nextDate - currentDate < 0 ) { // Если "предыдущий" больше "следующего"
                return false;
            }
        }
    }

    return true;
};