YAHOO.util.Event.onDOMReady((function() {
    YAHOO.Bubbling.fire("registerAction",
    {
        actionName: "onUnlockAction",
        fn: function onUnlock(file) {
            this.modules.actions.genericAction(
            {
                failure:
                {
                    message: this.msg("Произошла ошибка при разблокировании документа {0}", file.displayName, Alfresco.constants.USERNAME)
                },
                webscript:
                {
                    name: "lecm/signed-docflow/unlock?nodeRef={nodeRef}",
                    stem: Alfresco.constants.PROXY_URI,
                    method: Alfresco.util.Ajax.GET,
                    params:
                    {
                        nodeRef: file.nodeRef,
                    }
                },
                success:{
                	callback:{
                		fn: function (){
                			window.location.reload();                			
                		},
                		scope: this
                	}
                },
                config:
                {
                }

            });
        }
    });
}));
