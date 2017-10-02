var EDSExecutorScript = {
    notificateExecutor: function(doc) {
        var docAuthor = doc.assocs["lecm-document:author-assoc"][0];
        var docExecutorAssoc = doc.assocs["lecm-eds-document:executor-assoc"];
        var docExecutor = null;
        if (docExecutorAssoc && docExecutorAssoc.length) {
            docExecutor = docExecutorAssoc[0];
        }
        if (docExecutor && docAuthor && !docExecutor.nodeRef.equals(docAuthor.nodeRef)) {
            notifications.sendNotificationFromCurrentUser({
                recipients: [docExecutor],
                templateCode: 'EDS_EXECUTOR_NEW',
                templateConfig: {
                    mainObject: doc
                }
            });
        }
    },
    grandDynamicRoleForExecutor: function(doc) {
        if (doc) {
            var docAuthor = doc.assocs["lecm-document:author-assoc"][0];
            var docExecutorAssoc = doc.assocs["lecm-eds-document:executor-assoc"];
            var docExecutor = null;
            if (docExecutorAssoc && docExecutorAssoc.length) {
                docExecutor = docExecutorAssoc[0];
            }
            /* выдадим права исполнителю если он не автор (составитель) */
            if (docExecutor && docAuthor && !docExecutor.nodeRef.equals(docAuthor.nodeRef)) {
                statemachine.grandDynamicRoleForEmployee(doc, docExecutor, "BR_INITIATOR", task);
            }
        }
    }
};