<#assign id = args.htmlid?js_string>
<div class="panel-header">
    <div class="panel-title">${msg("label.title")}</div>
    <div class="lecm-dashlet-actions">
        <a id="${id}-action-collapse" class="collapse" title="${msg("btn.collapse")}"></a>
    </div>
</div>
<div class="list-container">
    <div id="${id}_myErrandsList-view-mode-button-group" class="yui-buttongroup errands-view-mode-button-group">
        <input id="${id}-view-mode-radiofield-links" type="radio" name="view-mode-radiofield" value="${msg("errands.list")}" checked />
        <input id="${id}-view-mode-radiofield-tree" type="radio" name="view-mode-radiofield" value="${msg("errands.tree")}" />
        <span class="errands-list-filter">
		<select id="${id}-errands-filter">
			<option value="all">${msg("errandslist.option.all")}</option>
			<option selected value="active">${msg("errandslist.option.active")}</option>
			<option value="complete">${msg("errandslist.option.completed")}</option>
		</select>
	</span>
<#if isErrandsStarter && hasStatemachine && isRegistered>
    <span class="lecm-dashlet-actions create-errand-action">
            <a id="${id}-action-add" href="#" onclick="errandsComponent.createChildErrand(); return false;" class="add" title="${msg("errandslist.add.errand.tooltip")}">${msg("errandslist.add.errand")}</a>
        </span>
</#if>
    </div>

    <div class="list-container" id="${id}-listContainer">
        <div class="body scrollableList" id="${id}_results">
            <div id="${id}_errandsList"></div>
        </div>
    </div>

    <div class="connections-list" id="${id}-connections-list">

    </div>
</div>
<script>
    (function() {
        var Dom = YAHOO.util.Dom;

        function hideButton() {
            if(!location.hash) {
                YAHOO.util.Dom.setStyle(this, 'display', 'none');
            }
        }
        YAHOO.util.Event.onAvailable("${id}-action-collapse", hideButton);

        function init() {
            var viewButtonGroup = new YAHOO.widget.ButtonGroup("${id}_myErrandsList-view-mode-button-group");
            var buttons = viewButtonGroup.getButtons();
            for (var i = 0; i < buttons.length; i++) {
                buttons[i].addListener("click", viewChanged, this, true);
            }
        }
        function loadConnectionTree() {
            Alfresco.util.loadWebscript({
                url: Alfresco.constants.URL_SERVICECONTEXT + "/lecm/components/document/connections-tree",
                properties: {
                    nodeRef: "${nodeRef}",
                    showConnectionType: false,
                    connectionTypes: "onBasis",
                    linkedOnAssocs: "lecm-errands:additional-document-assoc",
                    onlyDirect: true,
                    linkedDocTypes: "lecm-errands:document",
                    containerId: "${id}",
                    msgEmpty: "${msg("errands.tree.no_errands")}"
                },
                target: "${id}-connections-list"
            });
        }

        function viewChanged(event) {
            var graphTreeContainer = Dom.get("${id}-graph-tree");
            var errandsContainer = Dom.get("${id}-listContainer");
            var errandsFilter = Dom.get("${id}-errands-filter");

            if (event.currentTarget.id === "${id}-view-mode-radiofield-links") {
                graphTreeContainer.style.display = "none";
                errandsContainer.style.display = "block";
                errandsFilter.style.display = "block";
            } else if (event.currentTarget.id === "${id}-view-mode-radiofield-tree") {
                errandsContainer.style.display = "none";
                graphTreeContainer.style.display = "block";
                errandsFilter.style.display = "none";
            }
        }
        loadConnectionTree();
        YAHOO.util.Event.onAvailable("${id}_myErrandsList-view-mode-button-group", init);

    })();
</script>
