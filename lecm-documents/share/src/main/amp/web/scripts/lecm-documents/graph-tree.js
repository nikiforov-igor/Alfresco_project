YAHOO.Bubbling.on("graphContainerReady", function() {
	var receivedItems = {};
	var layerbylayer = function abc(foler_url, table_id, pag_id) {
		var myDataSource = new YAHOO.util.XHRDataSource(foler_url);

		myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSON;
		myDataSource.connXhrMode = "queueRequests";
		myDataSource.responseSchema = {
			resultsList: "items"
		};

		var myDataTable = new YAHOO.widget.RowExpansionDataTable(
				table_id,
				[
					{
						label: "",
						formatter: function(el, oRecord, oColumn, oData) {
							var $el = new YAHOO.util.Element(el),
									div = document.createElement("div"),
									nodeRef = oRecord._oData.nodeRef,
									previosDocRef = oRecord._oData.previosDocRef;

							if (!receivedItems[nodeRef]) {
								YAHOO.widget.RowExpansionDataTable.formatRowExpansion(el, oRecord, oColumn, oData);
							}

							if (receivedItems[previosDocRef]) {
//								div.className = "lecm-dt-liner";
								el.parentElement.setAttribute("style", "position: relative;");
								el.parentElement.appendChild(div);
							}
						}
					},
					{
						key: "title",
						label: "",
						resizeable: true,
						sortable: false,
						width: '200px',
						formatter: function(el, oRecord, oColumn, oData) {
							var nodeRef = oRecord._oData.nodeRef;
							var previosDocRef = oRecord._oData.previosDocRef;
							var directionIconSpan = '<span class="connection-direction ' + (oRecord._oData.direction || '') + '"> </span>';
							var docTypeIcon = '<img class="document-type" ' +
                                'src="/share/res/images/lecm-documents/type-icons/' + (oRecord._oData.docType || '').replace(':', '_') + '.png"' +
                                'onerror="this.src = \'/share/res/images/lecm-documents/type-icons/default_document.png\';"> </img>';
							var linkBlock = '<span class="link-span">' +
									(oRecord._oData.connectionType ? '<p class="connectionType">' + oRecord._oData.connectionType + '</p>' : '') +
									'<a target="_blank" href="' + Alfresco.constants.URL_PAGECONTEXT + 'document?nodeRef=' + nodeRef + '" >' + oData + '</a>' +
									(oRecord._oData.status ? '<p class="connectionType">'+ this.msg('msg.status') +': ' + oRecord._oData.status + '</p>' : '') + '</span>';
							var descrBlock = '<div class="item-description">' + directionIconSpan + docTypeIcon + linkBlock + '</div>';
							el.innerHTML = descrBlock || '--[ No description ]--';
							if (previosDocRef) {
								if (!receivedItems[nodeRef])
									receivedItems[nodeRef] = receivedItems[previosDocRef] + 1;
							} else {
								receivedItems[nodeRef] = 1;
							}
						}
					}
				],
				myDataSource,
				{
					/**
					 * The "rowExpansionTemplate" property is passed a string. This is passed
					 * through YAHOO.lang.substitute which can match tokens (represented with brackets),
					 * which contain keys from the DataTables data.
					 **/
					rowExpansionTemplate: function(oData) {
						var nodeRef = oData.data._oData.nodeRef;
						var liner_el = oData.liner_element;
						var t_id = 'tab-' + nodeRef;
						liner_el.innerHTML = '<div class="yui-inner-div" id="' + t_id + '"></div>';
						var p_id = 'pag-' + nodeRef;
						//var pag = Dom.insertAfter('<div id='+p_id+'></div>');
						var f_url = Alfresco.constants.PROXY_URI + "/lecm/document/connections/api/tree-datasource?documentNodeRef=" + nodeRef +
								((oData.data._oData.previosDocRef) ? "&previosDocRef=" + oData.data._oData.previosDocRef : "");
						YAHOO.util.Event.onContentReady(t_id, function() {
							layerbylayer(f_url, t_id, p_id)
						}, true);
					},
					//paginator: new YAHOO.widget.Paginator({
					//	rowsPerPage:5,
					//	containers:pag_id
					//})
				}
		);

		/**
		 *
		 * Subscribe to the "cellClickEvent" which will yui-dt-expandablerow-trigger the expansion
		 * when the user clicks on the yui-dt-expandablerow-trigger column
		 *
		 **/
		myDataTable.subscribe('cellClickEvent', myDataTable.onEventToggleRowExpansion);
		myDataTable.set("MSG_EMPTY", "");
		myDataTable.set("MSG_ERROR", this.msg("msg.forbiden"));

		myDataTable.nativeCollapseRow = myDataTable.collapseRow;
		myDataTable.collapseRow = function(record_id) {
			var row_data = this.getRecord(record_id);
			var nodeRef = row_data._oData.nodeRef;
			var level = receivedItems[nodeRef];
			if (level) {
				var itemRefs = Object.keys(receivedItems);
				var newMap = {};
				for (var i = 0, itemRef, itemLevel; i < itemRefs.length; i++) {
					itemRef = itemRefs[i];
					itemLevel = receivedItems[itemRef];
					if (itemLevel && itemLevel <= level)
						newMap[itemRef] = itemLevel;
				}
				receivedItems = newMap;
			}
			myDataTable.nativeCollapseRow(record_id);
		};
		var thead_el = myDataTable.getTheadEl();

		/*return {
		 oDS: myDataSource,
		 oDT: myDataTable
		 };*/
	};
	var documentRef = YAHOO.util.History.getQueryStringParameter('nodeRef');
	var foler_url = Alfresco.constants.PROXY_URI + "/lecm/document/connections/api/tree-datasource?documentNodeRef=" + documentRef;
	receivedItems[documentRef] = 0;
	layerbylayer(foler_url, "expandable_table", "pagination");
});
