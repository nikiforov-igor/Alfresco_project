if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.Delegation = LogicECM.module.Delegation || {};

LogicECM.module.Delegation.DelegationOpts = LogicECM.module.Delegation.DelegationOpts || {};

(function () {
	"use strict";
	LogicECM.module.Delegation.DelegationOpts = function (containerId) {
		return LogicECM.module.Delegation.DelegationOpts.superclass.constructor.call(
			this,
			"LogicECM.module.Delegation.DelegationOpts",
			containerId,
			["button", "container", "connection", "json", "selector"]);
	};

	/**
	 * Extend from Alfresco.component.Base
	 */
	YAHOO.lang.extend (LogicECM.module.Delegation.DelegationOpts, Alfresco.component.Base);

	/**
	 * Augment prototype with main class implementation, ensuring overwrite is enabled
	 */
	YAHOO.lang.augmentObject (LogicECM.module.Delegation.DelegationOpts.prototype, {
		datagridId: "",
        viewDialog: null,

		options: {
			delegator: null, //nodeRef на делегирующее лицо
			isActive: false, //флаг показывающий активно ли делегирование
            bubblingLabel: ""
		},

		onDelegationOptsPart1: function (result) {
            var contentEl = null;
            if (this.options.myProfile) {
                contentEl = YAHOO.util.Dom.get(this.id + "-content-part1");
            } else {
			    contentEl = YAHOO.util.Dom.get(this.id + "-form-content");
            }
			contentEl.innerHTML = '<div id="' + this.datagridId + '">' + result.serverResponse.responseText + "<div>";
			var nodeRef = new Alfresco.util.NodeRef(this.options.delegator);
			var submissionUrl = Alfresco.constants.PROXY_URI_RELATIVE + "lecm/delegation/options/save/" + nodeRef.uri;
			YAHOO.util.Dom.setAttribute ("delegation-opts-part1-form", "action", submissionUrl);
			var formEl = YAHOO.util.Dom.get ("delegation-opts-part1-form");
            var me = this;
			if (formEl) {
				var form = new Alfresco.forms.Form ("delegation-opts-part1-form");
				form.setAJAXSubmit (true, {
					successCallback: {
						fn: function () {
                            if (!me.options.myProfile) {
                                me.viewDialog.hide();
                                me.viewDialog.destroy();
                            }
							Alfresco.util.PopupManager.displayMessage ({
								text: Alfresco.util.message("msg.data_refreshed")
							});
                            if (!me.options.myProfile) {
                                YAHOO.Bubbling.fire("datagridRefresh", {
                                    bubblingLabel: me.options.bubblingLabel
                                });
                            }
						},
						scope: this
					},
					failureCallback: {
						fn: function () {
							Alfresco.util.PopupManager.displayMessage ({
								text: Alfresco.util.message("msg.data_refresh_failed")
							});
						},
						scope: this
					}
				});
				form.setSubmitAsJSON (true);
				form.setShowSubmitStateDynamically (true, false);
				// Initialise the form
				form.init ();
			}
            if (!me.options.myProfile) {
                YAHOO.util.Dom.removeClass(this.id + "-form", "hidden1");
                this.viewDialog.show();
            }
		},

		onReady: function () {
			this.datagridId = this.id + YAHOO.util.Dom.generateId();
            if (!this.viewDialog && !this.options.myProfile) {
                var viewDialogId = this.id + "-form";
                // Если viewDialog уже есть в разметке в body (остался с предыдущего открытия этого раздела)
                // удаляем его
                var viewDialogInBody = YAHOO.util.Selector.query("body > div > #" + viewDialogId, null, true);
                if (viewDialogInBody) {
                    viewDialogInBody.parentNode.removeChild(viewDialogInBody);
                }
                this.viewDialog = Alfresco.util.createYUIPanel(viewDialogId,
                    {
                        width: "50em"
                    });
                this.viewDialog.hide();
            }


			Alfresco.logger.info ("A new LogicECM.module.Delegation.DelegationOpts has been created");

			if (this.options.delegator) {

				var mode = (this.options.isActive) ? "view" : "edit";

				var argsPart1 = {
					htmlid: "delegation-opts-part1",
					datagridId: this.datagridId,
					itemKind: "node",
					itemId: this.options.delegator,
					formId: "delegation-opts-part1",
					mode: mode,
					submitType: "json",
					showCancelButton: false,
					showResetButton: false,
					showSubmitButton: true,
					ignoreNodes: [LogicECM.module.Delegation.Const.employee],
					showCaption: false
				};
				Alfresco.util.Ajax.request ({
					method: "GET",
					url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form",
					dataObj: argsPart1,
					successCallback: {
						fn: this.onDelegationOptsPart1,
						scope: this
					},
					failureMessage: Alfresco.util.message("msg.request_failed"),
					execScripts: true
				});

			} else {
				Alfresco.util.PopupManager.displayPrompt ({
					title: Alfresco.util.message("title.procuracy_opts.failed"),
					text: Alfresco.util.message("txt.procuracy_opts.failed")
				});
			}

			YAHOO.util.Dom.setStyle (this.id + "-body", "visibility", "visible");
		}

	}, true);

})();
