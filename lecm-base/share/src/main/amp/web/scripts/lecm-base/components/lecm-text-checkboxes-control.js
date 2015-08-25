if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.ARM = LogicECM.module.ARM || {};

(function () {
	var Dom = YAHOO.util.Dom;

	LogicECM.module.TextCheckboxesControl = function (htmlId) {
		LogicECM.module.TextCheckboxesControl.superclass.constructor.call(this, "LogicECM.module.TextCheckboxesControl", htmlId);
		this.selectedItems = [];
		return this;
	};

	YAHOO.extend(LogicECM.module.TextCheckboxesControl, Alfresco.component.Base,
		{
			controlId: null,

			checkboxesContainerId: null,

			selectedItems: null,

			options: {
				disabled: false,

				dataSource: null,

				currentValues: null,

				itemId: null,

				destination: null,

                                singleValue: false,

				mandatory:false
			},

			onReady: function () {
				this.controlId = this.id + '-cntrl';
				this.checkboxesContainerId = this.controlId + "-checkboxes";

				this.loadValues();
 			},

			loadValues: function() {
				var me = this;
				var url = Alfresco.constants.PROXY_URI + this.options.dataSource + (this.options.dataSource.indexOf("?") != -1 ? "&" : "?") + "itemId=" + encodeURIComponent(this.options.itemId);
				if (this.options.destination != null) {
					url += "&destination=" + encodeURIComponent(this.options.destination);
				}
				Alfresco.util.Ajax.request(
					{
						url: url,
						successCallback: {
							fn: function (response) {
								if (response.json != null) {
									var content = "";
									for (var i = 0; i < response.json.length; i++) {
										var data = response.json[i];
										var id = "text-chbx-" + data.value;
										data.inputId = "text-chbx-" + data.value;

										content += '<li><input id="' + id + '" type="checkbox" value="'
											+ data.value + '"';

										if (me.options.currentValues != null) {
											for (var j = 0; j < me.options.currentValues.length; j++) {
												if (me.options.currentValues[j] == data.value) {
													me.selectedItems[data.value] = data;
													content += 'checked="checked"';
													break;
												}
											}
										}

										if (me.options.disabled) {
											content += 'disabled="disabled"';
										}
										content += '>';
										content += '<label class="checkbox" for="' + id + '">' + data.name + '</label></li>';
										YAHOO.util.Event.onAvailable(id, me.attachCheckboxClickListener, data, me);
									}
									Dom.get(me.checkboxesContainerId).innerHTML = content;
									me.updateFormFields();
								}
							}
						},
						failureMessage: "message.failure"
					});
			},

			attachCheckboxClickListener: function (node) {
				YAHOO.util.Event.on(node.inputId, 'click', this.checkboxOnClick, node, this);
			},

			checkboxOnClick: function (event, node) {
				var checkbox = Dom.get(node.inputId);
				if (checkbox.checked) {
                    if (this.options.singleValue) {
                        var container = document.getElementById(this.checkboxesContainerId);
                        var checkboxes = container.getElementsByTagName("input");
                        for (var key in checkboxes) {
                            if (checkboxes[key].id != node.inputId) {
                                checkboxes[key].checked = false;
                            }
                        }
                        this.selectedItems = [];
                    }
					this.selectedItems[node.value] = node;
				} else {
					delete this.selectedItems[node.value];
				}
				this.updateFormFields();
			},

			updateFormFields: function() {
				Dom.get(this.id).value = this.getSelectedItems().toString();

				if (this.options.mandatory) {
					YAHOO.Bubbling.fire("mandatoryControlValueUpdated", this);
				}
			},

			getSelectedItems:function AssociationTreeViewer_getSelectedItems() {
				var selectedItems = [];

				for (var item in this.selectedItems) {
					if (this.selectedItems.hasOwnProperty(item)) {
						selectedItems.push(item);
					}
				}
				return selectedItems;
			}
		});
})();
