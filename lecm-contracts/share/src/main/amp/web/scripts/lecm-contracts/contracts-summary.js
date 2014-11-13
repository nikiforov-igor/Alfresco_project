if (typeof LogicECM == 'undefined' || !LogicECM) {
	LogicECM = {};
}
LogicECM.module = LogicECM.module || {};
LogicECM.module.Contracts = LogicECM.module.Contracts|| {};
LogicECM.module.Contracts.dashlet = LogicECM.module.Contracts.dashlet || {};

(function() {
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
		message: {},

		onReady: function Contracts_onReady() {
			this.container = Dom.get(this.id+'_results');
			this.setMessage();
			this.createDialog();
			this.drawForm();
		},

		setMessage: function Set_Message() {
			this.message = {
				'Все': this.msg('label.info.allContracts'),
				'Проекты': this.msg('label.info.contractsToDevelop'),
				'Подписанные': this.msg('label.info.activeContracts'),
				'Завершенные': this.msg('label.info.inactiveContracts'),
				'Участники': this.msg('label.info.participants')
			};
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
			var template = '{proxyUri}lecm/documents/summary?docType={docType}&archive={archive}&skippedStatuses={skippedStatuses}&considerFilter={considerFilter}';
			var url = YAHOO.lang.substitute(template, {
				proxyUri: Alfresco.constants.PROXY_URI,
				docType: 'lecm-contract:document',
				archive: 'false',
				skippedStatuses: 'Корзина',
				considerFilter: location.hash.replace(/#(\w+)=/, '')
			});
			var successCallback = {
				scope: this,
				fn: function(serverResponse) {
					var index, list, members;
					var template = '<div class="column first {bold}">{message}:</div><div class="column second"><a class="status-button text-cropped" href="{href}">{amount}</a></div>';
					var membersTemplate = '<div class="column first bold">{message}:</div><div class="column second"><a class="status-button text-cropped" onclick="{onclick}">{amount}</a></div>';
					if (this.container) {
						this.container.innerHTML = '';
						if (serverResponse.json) {
							list = serverResponse.json.list;
							members = serverResponse.json.members;
							for (index in list) {
								if (!list[index].skip){
									this.createRow(YAHOO.lang.substitute(template, {
										bold: index === 0 ? 'bold' : '',
										message: this.message[list[index].key],
										href: Alfresco.URL_PAGECONTEXT + 'contracts-list?query=' + list[index].filter,
										amount: list[index].amount
									}));
								}
							}
							this.createRow(YAHOO.lang.substitute(membersTemplate, {
								message: this.message[members.key],
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
						var formEl = Dom.get(this.options.formId+'-content');
						formEl.innerHTML = text;
					}
				},
				failureMessage: 'Данные не загружены',
				execScripts: true
			});
			if (this.viewDialog) {
				Dom.setStyle(this.options.formId, 'display', 'block');
				this.viewDialog.show();
			}
		}
	});
})();
