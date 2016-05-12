/* global YAHOO, Alfresco */

if (typeof LogicECM == 'undefined' || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Review = LogicECM.module.Review || {};
LogicECM.module.Review.ReviewList = LogicECM.module.Review.ReviewList || {};

(function () {
	var Bubbling = YAHOO.Bubbling,
		Dom = YAHOO.util.Dom;

	LogicECM.module.Review.ReviewList.SaveControl = function (containerId, options, messages) {
		LogicECM.module.Review.ReviewList.SaveControl.superclass.constructor.call(this, 'LogicECM.module.Review.ReviewList.SaveControl', containerId);
		this.saveDialogOpening = false;
		this.deferredInit = new Alfresco.util.Deferred(['onReady', 'onReviewListDictionary'], {
			scope: this,
			fn: this.onDeferredInit
		});
		this.setOptions(options);
		this.setMessages(messages);
		this.getReviewListDictionary();

		Bubbling.on('employeesSelected', this.onSelectedItems, this);
		Bubbling.on('reviewListsSelected', this.onSelectedItems, this);

		return this;
	};

	YAHOO.lang.extend(LogicECM.module.Review.ReviewList.SaveControl, Alfresco.component.Base, {

		deferredInit: null,

		reviewListDictionary: null,

		saveDialogOpening: null,

		options: {
			reviewListDictionary: null,
			buttonSaveLabelId: null
		},

		onSelectedItems: function (layer, args) {
			var obj = args[1],
				nodeRef,
				disabled = true;
			if (obj.selectedItems && Object.keys(obj.selectedItems).length) {
				disabled = false;
				for (nodeRef in obj.selectedItems) {
					if ('lecm-review-list:review-list-item' === obj.selectedItems[nodeRef].type) {
						disabled = true;
						break;
					}
				}
			}
			this.widgets.buttonSave.set('disabled', disabled);
		},

		onButtonSaveClick: function (evt, target) {

			function saveDialogDoBeforeDialogShow(p_form, p_dialog) {
				this.saveDialogOpening = false;
				Dom.addClass(p_dialog.id + '-form-container', 'metadata-form-edit');
				p_dialog.dialog.setHeader('Новый список');
				p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, { moduleId: p_dialog.id }, this);
			}

			function saveDialogOnSuccess(successResponse) {
				Alfresco.util.PopupManager.displayMessage({
					text: this.msg('message.save.success')
				});
			}

			if (this.saveDialogOpening) {
				return;
			}
			this.saveDialogOpening = true;
			var saveDialog = new Alfresco.module.SimpleDialog(this.id + '-saveReviewList');
			saveDialog.setOptions({
				width: '50em',
				templateUrl: Alfresco.constants.URL_SERVICECONTEXT + 'components/form',
				templateRequestParams: {
					itemKind: 'type',
					itemId: 'lecm-review-list:review-list-item',
					destination: this.reviewListDictionary,
					mode: 'create',
//					formId: null,
					submitType: 'json',
					showCancelButton: true
				},
				destroyOnHide: true,
				doBeforeDialogShow: {
					scope: this,
					fn: saveDialogDoBeforeDialogShow
				},
				onSuccess: {
					scope: this,
					fn: saveDialogOnSuccess
				},
				onFailureMessage: this.msg('message.failure')
			});
			saveDialog.show();
		},

		getReviewListDictionary: function() {

			function onSuccess(successResponse) {
				this.reviewListDictionary = successResponse.json.nodeRef;
				this.deferredInit.fulfil('onReviewListDictionary');
			}

			Alfresco.util.Ajax.jsonGet({
				url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/dictionary/api/getDictionary',
				dataObj: {
					dicName: this.options.reviewListDictionary
				},
				successCallback: {
					scope: this,
					fn: onSuccess
				},
				failureMessage: this.msg('message.failure')
			});
		},

		onDeferredInit: function(obj) {
			this.widgets.buttonSave = Alfresco.util.createYUIButton(this, 'btn-save', this.onButtonSaveClick, {
				disabled: true,
				label: this.msg(this.options.buttonSaveLabelId),
				type: 'push'
			});
		},

		onReady: function () {
			console.log(this.name + '[' + this.id + '] is ready');
			this.deferredInit.fulfil('onReady');
		}
	}, true);
})();
