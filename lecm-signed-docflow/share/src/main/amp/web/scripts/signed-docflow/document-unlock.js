YAHOO.util.Event.onDOMReady(function() {
	YAHOO.Bubbling.fire("registerAction", {
		actionName: "onUnlockAction",
		fn: function(file) {

			var actionScope = this;

			Alfresco.util.PopupManager.displayPrompt({
				title: Alfresco.util.message('lecm.signdoc.msg.unblock.confirm'),
				text: Alfresco.util.message('lecm.signdoc.msg.unbloc.version.fetures'), // the text to display for the user, mandatory
				modal: true,
				buttons: [
					{
						text: Alfresco.util.message('lecm.signdoc.yes'),
						handler: handleYes
					}, {
						text: Alfresco.util.message('lecm.signdoc.no'),
						handler: function() {
							this.hide();
						}
					}
				]
			});

			function handleYes() {
				actionScope.modules.actions.genericAction({
					failure: {
						message: actionScope.msg(Alfresco.util.message('lecm.signdoc.msg.unlock.doc.error') + " {0}", file.displayName, Alfresco.constants.USERNAME)
					},
					webscript: {
						name: "lecm/signed-docflow/unlock?nodeRef={nodeRef}",
						stem: Alfresco.constants.PROXY_URI,
						method: Alfresco.util.Ajax.GET,
						params: {
							nodeRef: file.nodeRef
						}
					},
					success: {
						callback: {
							fn: function() {
								window.location.reload();
							},
							scope: actionScope
						}
					},
					config: {}

				});

				this.hide();
			}
		}
	});
});
