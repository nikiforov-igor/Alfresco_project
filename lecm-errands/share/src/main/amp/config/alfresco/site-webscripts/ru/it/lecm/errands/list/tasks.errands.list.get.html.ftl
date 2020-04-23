<#assign id = args.htmlid?js_string>
<div class="errands-form" id="${id}_errands">
    <div class="panel-header">
        <div class="panel-title">${msg("label.title")}</div>
    </div>
    <div class="list-container">
        <div id="${id}_myErrandsList-view-mode-button-group" class="errands-button-group yui-navset form-tabs yui-navset-top">
            <ul class="yui-nav">
                <li class="selected" title="${msg("errands.list")}">
                    <a href="#links" id="${id}-view-mode-radiofield-links" tabindex="801">
                        <em>${msg("errands.list")}</em>
                    </a>
                </li>
                <li title="${msg("errands.tree")}">
                    <a href="#tree" id="${id}-view-mode-radiofield-tree" tabindex="901">
                        <em>${msg("errands.tree")}</em>
                    </a>
                </li>
            </ul>
        </div>
        <div id="${id}_myErrandsList-actions-button-group" class="yui-buttongroup errands-button-actions">
    <#if isErrandsStarter && hasStatemachine && isRegistered>
            <div class="errand-add create-errand-action">
               <span id="${id}-errand-add" class="yui-button yui-push-button">
                  <span class="first-child">
                     <button type="button" title="${msg("errandslist.add.errand.tooltip")}">${msg("errandslist.add.errand")}</button>
                  </span>
               </span>
            </div>
    </#if>
            <div class="errands-list-filter">
                <select id="${id}-errands-filter">
                    <option value="all">${msg("errandslist.option.all")}</option>
                    <option selected value="active">${msg("errandslist.option.active")}</option>
                    <option value="complete">${msg("errandslist.option.completed")}</option>
                </select>
            </div>
        </div>
        <div class="errands-container" id="${id}-listContainer">
            <div class="body scrollableList" id="${id}_results">
                <div id="${id}_errandsList"></div>
            </div>
        </div>

        <div class="connections-list" id="${id}-connections-list">
        </div>
    </div>
</div>
<script>
    (function() {
        var Dom = YAHOO.util.Dom;

        function initLoad() {
            LogicECM.module.Base.Util.loadScripts(['scripts/lecm-base/components/lecm-form-tabs.js'], init, ['tabview']);
        }

        function init() {
            new LogicECM.BaseFormTabs("${id}_myErrandsList-view-mode-button-group").setMessages(${messages});

            YAHOO.util.Event.addListener("${id}-view-mode-radiofield-links", "click", viewChanged, this, true);
            YAHOO.util.Event.addListener("${id}-view-mode-radiofield-tree", "click", viewChanged, this, true);

        <#if isErrandsStarter && hasStatemachine && isRegistered>
            Alfresco.util.createYUIButton(this, "${id}-errand-add", errandsComponent.createChildErrand.bind(errandsComponent), {}, Dom.get("${id}-errand-add"));
        </#if>
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

        YAHOO.util.Event.onAvailable("${id}_myErrandsList-view-mode-button-group", initLoad);
    })();
</script>
