var EDSExecutorScript = {
    notificateExecutor: function(){
        var doc = bpm_package.children[0];
        if (doc) {
            var docCompiler = doc.assocs["lecm-document:author-assoc"][0];
            var docExecutorAssoc = doc.assocs["lecm-eds-document:executor-assoc"];
            var docExecutor = null;
            if (docExecutorAssoc && docExecutorAssoc.length) {
                docExecutor = docExecutorAssoc[0];
            }
            if (docExecutor && docCompiler && !docExecutor.nodeRef.equals(docCompiler.nodeRef)) {
                statemachine.grandDynamicRoleForEmployee(doc, docExecutor, "BR_INITIATOR", task);
                notifications.sendNotificationFromCurrentUser({
                    recipients: [docExecutor],
                    templateCode: 'EDS_EXECUTOR_NEW',
                    templateConfig: {
                        mainObject: doc
                    }
                });
            }
        }
    }
};