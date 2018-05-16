if (typeof LogicECM == 'undefined' || !LogicECM) {
	LogicECM = {};
}
LogicECM.module = LogicECM.module || {};
LogicECM.module.Contracts = LogicECM.module.Contracts || {};
LogicECM.module.Contracts.dashlet = LogicECM.module.Contracts.dashlet || {};

(function () {
	var Dom = YAHOO.util.Dom;

	LogicECM.module.Contracts.dashlet.Summary = function Contracts_constructor(htmlId) {
		LogicECM.module.Contracts.dashlet.Summary.superclass.constructor.call(this, 'LogicECM.module.Contracts.dashlet.Summary', htmlId, ['button', 'container']);

		return this;
	};

	YAHOO.extend(LogicECM.module.Contracts.dashlet.Summary, Alfresco.component.Base, {
		options: {
			formId: null,
			nodeRef: null
		},
		container: null,
		viewDialog: null,
		settings: {},

		onReady: function Contracts_onReady() {
			this.container = Dom.get(this.id + '_results');
			this.setSettings();
			this.createDialog();
			this.drawForm();
		},

		setSettings: function Set_Message() {
            var successCallback = {
                scope: this,
                fn: function (serverResponse) {
                    this.settings = {
                    	'armCode': serverResponse.json.armCode,
                        'Участники': this.msg('label.info.participants'),
                        'Все действующие': {'message': this.msg('label.info.allContracts'), 'path' : serverResponse.json.currentContractsPath},
                        'Проекты': {'message': this.msg('label.info.contractsToDevelop'), 'path' : serverResponse.json.projectContractsPath},
                        'На исполнении': {'message': this.msg('label.info.activeContracts'), 'path': serverResponse.json.executionContractsPath}
                    };
                }
            };
            Alfresco.util.Ajax.jsonGet({
                url: Alfresco.constants.PROXY_URI + "lecm/contracts/dashlet/settings/url",
                successCallback: successCallback,
                failureMessage: this.msg('message.failure')
            });
		},

		createRow: function Create_row(innerHtml) {
			var div = document.createElement('div');
			div.setAttribute('class', 'row summary');
			if (innerHtml) {
				div.innerHTML = innerHtml;
			}
			if (this.container) {
				this.container.appendChild(div);
			}
		},

		drawForm: function Draw_form() {
			var template = '{proxyUri}lecm/documents/summary?docType={docType}&archive={archive}&skippedStatuses={skippedStatuses}';
			var url = YAHOO.lang.substitute(template, {
				proxyUri: Alfresco.constants.PROXY_URI,
				docType: 'lecm-contract:document',
				archive: 'false',
				skippedStatuses: encodeURIComponent('Корзина')
			});
			var successCallback = {
				scope: this,
				fn: function (serverResponse) {
					var index, list, members, listItem;
					var template = '<div class="column first {bold}">{message}:</div><div class="column second"><a class="status-button text-cropped" href="{href}">{amount}</a></div>';
					var membersTemplate = '<div class="column first bold">{message}:</div><div class="column second"><a class="status-button text-cropped" onclick="{onclick}">{amount}</a></div>';
					if (this.container) {
						this.container.innerHTML = '';
						if (serverResponse.json) {
							list = serverResponse.json.list;
							members = serverResponse.json.members;
							for (index in list) {
								listItem = list[index];
								if (!listItem.skip) {
									this.createRow(YAHOO.lang.substitute(template, {
										bold: index === 0 ? 'bold' : '',
										message: this.settings[listItem.key] && this.settings[listItem.key].message ? this.settings[listItem.key].message : listItem.key,
										href: Alfresco.constants.URL_PAGECONTEXT + 'arm?code=' + this.settings.armCode + '&path=' + encodeURIComponent(this.settings[listItem.key].path),
										amount: listItem.amount
									}));
								}
							}
							this.createRow(YAHOO.lang.substitute(membersTemplate, {
								message: this.settings[members.key]?this.settings[members.key]:members.key,
								onclick: 'LogicECM.module.Contracts.dashlet.Summary.instance.showDialog();',
								amount: members.amountMembers
							}));
						}
					}
				}
			};

			Alfresco.util.Ajax.jsonGet({
				url: url,
				successCallback: successCallback,
				failureMessage: this.msg('message.failure')
			});
		},

		createDialog: function createDialog() {
			this.viewDialog = Alfresco.util.createYUIPanel(this.options.formId, {
				width: '50em',
				close: false
			});
			this.hideViewDialog();
		},

		hideViewDialog: function hideViewDialog() {
			if (this.viewDialog) {
				this.viewDialog.hide();
				Dom.setStyle(this.options.formId, 'display', 'none');
			}
		},

		showDialog: function showViewDialog(){
			Alfresco.util.Ajax.request({
				url: Alfresco.constants.URL_SERVICECONTEXT + 'lecm/dashlet/summary/members',
				dataObj: {
					nodeRef: this.options.nodeRef,
					htmlid: this.id
				},
				successCallback: {
					scope: this,
					fn: function (response) {
						var text = response.serverResponse.responseText;
						var formEl = Dom.get(this.options.formId + '-content');
						formEl.innerHTML = text;
					}
				},
				failureMessage: this.msg('message.data.not.loaded'),
				execScripts: true
			});
			if (this.viewDialog) {
				Dom.setStyle(this.options.formId, 'display', 'block');
				this.viewDialog.show();
			}
		}
	});
})();
