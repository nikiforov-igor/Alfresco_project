function forceApproval(document, reason) {
    /* получение nodeRef-ы на текущую итерацию */
    var currentIteration = routesService.getDocumentCurrentIteration(document);

    if (currentIteration) {
        var stages = currentIteration.getChildAssocsByType('lecmWorkflowRoutes:stage');
        for (i = 0, size = stages.length; i < size; ++i) {
            state = '' + stages[i].properties['lecmApproveAspects:approvalState'];
            if ('NEW' == state || 'ACTIVE' == state) {
                var items = stages[i].getChildAssocsByType('lecmWorkflowRoutes:stageItem');
                var hasItems = (items && items.length);
                if (hasItems) { /*"Отменен" - только если айтемы, иначе - "Пропущен"*/
                    stages[i].properties['lecmApproveAspects:approvalComment'] = reason;
                    stages[i].properties['lecmApproveAspects:hasComment'] = (reason !== null && reason.length > 0);
                    stages[i].save();
                }
            }
        }

        statemachine.terminateWorkflowsByDefinition(document, 'lecmApprovementWorkflow', 'forcedDecision', 'APPROVED_FORCED');
    }
}

function rejectApproval(document, reason) {
    /* получение nodeRef-ы на текущую итерацию */
    var currentIteration = routesService.getDocumentCurrentIteration(document);

    if (currentIteration) {
        var stages = currentIteration.getChildAssocsByType('lecmWorkflowRoutes:stage');
        for (i = 0, size = stages.length; i < size; ++i) {
            state = '' + stages[i].properties['lecmApproveAspects:approvalState'];
            if ('NEW' == state || 'ACTIVE' == state) {
                var items = stages[i].getChildAssocsByType('lecmWorkflowRoutes:stageItem');
                var hasItems = (items && items.length);
                if (hasItems) { /*"Отменен" - только если айтемы, иначе - "Пропущен"*/
                    stages[i].properties['lecmApproveAspects:approvalComment'] = reason;
                    stages[i].properties['lecmApproveAspects:hasComment'] = (reason !== null && reason.length > 0);
                    stages[i].save();
                }
            }
        }

        statemachine.terminateWorkflowsByDefinition(document, 'lecmApprovementWorkflow', 'forcedDecision', 'REJECTED_FORCED');
    }
}