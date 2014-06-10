YAHOO.util.Event.onDOMReady((function() {
    YAHOO.Bubbling.fire("registerAction",
    {
        actionName: "onUnlockAction",
        fn: function onUnlock(file) {

            var actionScope = this;

            function handleNo() {
                this.hide();
            };
            
            Alfresco.util.PopupManager.displayPrompt(
                { 
                    title: "Подтверждение разблокировки", // the title of the dialog, default is null 
                    text: "В случае создания новых версий документа, загруженные Электронные подписи будут более недоступны. Вы уверены, что хотите разблокировать функции создания версий?", // the text to display for the user, mandatory 
                    modal: true, // if a grey transparent overlay should be displayed in the background 
                    buttons: [
                        { 
                            text: "Да", 
                            handler: handleYes
                        },
                        {
                            text: "Нет",
                            handler: handleNo
                        }
                    ]
                }
                );

            function handleYes(scope){
                actionScope.modules.actions.genericAction(
                {
                    failure:
                    {
                        message: actionScope.msg("Произошла ошибка при разблокировании документа {0}", file.displayName, Alfresco.constants.USERNAME)
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
                    		scope: actionScope
                    	}
                    },
                    config:
                    {
    					
                    }

                });

                this.hide();
            }
        }
    });
}));
