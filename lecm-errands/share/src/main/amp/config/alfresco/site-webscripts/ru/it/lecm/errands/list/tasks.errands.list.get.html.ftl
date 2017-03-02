<#assign id = args.htmlid?js_string>
<div class="panel-header">
    <div class="panel-title">${msg("label.title")}</div>
    <div class="lecm-dashlet-actions">
        <a id="${id}-action-collapse" class="collapse" title="${msg("btn.collapse")}"></a>
    </div>
</div>

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
            <a id="${id}-action-add" href="javascript:void(0);" onclick="errandsComponent.createChildErrand()" class="add" title="${msg("errandslist.add.errand.tooltip")}">${msg("errandslist.add.errand")}</a>
        </span>
    </#if>
</div>

<div class="list-container" id="${id}-listContainer">
    <div class="body scrollableList" id="${id}_results">
        <div id="${id}_errandsList"></div>
    </div>
</div>

<div class="connections-list">
        <div id="${id}-errands-graph-tree" class="graph-tree hidden1">
            <div class ="yui-skin-sam">
                <div id="expandable_table"> </div>
                <div id="pagination"></div>
            </div>
        </div>
</div>

<script>
(function() {

    var Dom = YAHOO.util.Dom;

    function hideButton() {
        if(location.hash != "#expanded") {
            YAHOO.util.Dom.setStyle(this, 'display', 'none');
        }
    }
    YAHOO.util.Event.onAvailable("${id}-action-collapse", hideButton);

    function init() {
        var viewButtonGroup = new YAHOO.widget.ButtonGroup("${id}_myErrandsList-view-mode-button-group");
        var buttons = viewButtonGroup.getButtons()
        for (var i = 0; i < buttons.length; i++) {
            buttons[i].addListener("click", viewChanged, this, true);
        }
    }

    function viewChanged(event) {
        var graphTreeContainer = Dom.get("${id}-errands-graph-tree");
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

    YAHOO.util.Event.onAvailable("${id}_myErrandsList-view-mode-button-group", init);

    YAHOO.util.Event.onContentReady("${id}-errands-graph-tree", function() {
        YAHOO.Bubbling.fire("graphContainerReady", {
            isErrandCard: true,
            onlyDirect: true,
            onlySystem: true,
            connectionTypes: "onBasis"
        });
    });
})();
</script>
