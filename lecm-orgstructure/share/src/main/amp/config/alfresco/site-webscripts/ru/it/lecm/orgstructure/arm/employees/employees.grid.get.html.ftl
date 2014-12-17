<#assign id = args.htmlid>

<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>
<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<script type="text/javascript">//<![CDATA[
(function () {
    function createDatagrid() {
        var datagrid = new LogicECM.module.Base.DataGrid('${id}').setOptions(
                {
                    usePagination: false,
                    showExtendSearchBlock: false,
                    showActionColumn: false,
                    maxResults: 100,
                    loopSize: 50,
                    unlimited: true,
                    actions: [],
                    bubblingLabel: "employees",
                    showCheckboxColumn: false,
                    attributeForShow: "lecm-orgstr:employee-last-name",
                    excludeColumns: ["lecm-orgstr:employee-person-login"]
                }).setMessages(${messages});

        YAHOO.util.Event.onContentReady('${id}', function () {
            YAHOO.Bubbling.fire("activeGridChanged", {
                datagridMeta: {
                    itemType: LogicECM.module.OrgStructure.EMPLOYEES_SETTINGS.itemType,
                    nodeRef: LogicECM.module.OrgStructure.EMPLOYEES_SETTINGS.nodeRef,
                    datagridFormId: "arm-datagrid",
                    useOnlyInSameOrg: true,
                    useFilterByOrg: true
                },
                bubblingLabel: "employees"
            });
        });
    }

    function createToolbar() {
        LogicECM.module.OrgStructure.DictionaryToolbar = function (containerId) {
            LogicECM.module.OrgStructure.DictionaryToolbar.superclass.constructor.call(this, containerId);
            return this;
        };

        YAHOO.lang.extend(LogicECM.module.OrgStructure.DictionaryToolbar, LogicECM.module.OrgStructure.Toolbar);

        YAHOO.lang.augmentObject(LogicECM.module.OrgStructure.DictionaryToolbar.prototype, {
            onSearchClick: function (e, obj) {
                debugger;
                var searchTerm = Dom.get(this.id + "-full-text-search").value;

                var maySearch = this.options.minSTermLength == null || this.options.minSTermLength <= 0 || searchTerm.length == 0;
                if (!maySearch) {// проверяем длину терма
                    maySearch = (searchTerm.length >= this.options.minSTermLength);
                }
                if (maySearch) {
                    var dataGrid = this.modules.dataGrid;
                    var datagridMeta = dataGrid.datagridMeta;

                    datagridMeta.useOnlyInSameOrg = false;
                    datagridMeta.useFilterByOrg = false;

                    if (searchTerm.length > 0) {
                        var fields = dataGrid.getTextFields();
                        var fullTextSearch = {
                            parentNodeRef: datagridMeta.nodeRef,
                            fields: fields,
                            searchTerm: searchTerm
                        };
                        if (!datagridMeta.searchConfig) {
                            datagridMeta.searchConfig = {};
                        }
                        datagridMeta.searchConfig.fullTextSearch = fullTextSearch;
                        datagridMeta.sort = datagridMeta.sort ? datagridMeta.sort : "cm:modified|false";
                        if (datagridMeta.searchConfig.formData) {
                            if (typeof datagridMeta.searchConfig.formData == "string") {
                                datagridMeta.searchConfig.formData = YAHOO.lang.JSON.parse(datagridMeta.searchConfig.formData);
                            }
                            datagridMeta.searchConfig.formData.datatype = datagridMeta.itemType;
                        } else {
                            datagridMeta.searchConfig.formData = {
                                datatype: datagridMeta.itemType
                            };
                        }
                        this.modules.dataGrid.search.performSearch({
                            searchConfig: datagridMeta.searchConfig,
                            searchNodes: datagridMeta.searchNodes,
                            searchShowInactive: dataGrid.options.searchShowInactive,
                            useFilterByOrg: false,
                            useOnlyInSameOrg: false,
                            sort: datagridMeta.sort
                        });
                    } else {
                        this.onClearSearch();
                    }

                    if (obj && obj[1]) {
                        obj[1].preventDefault();
                    }
                } else {
                    Alfresco.util.PopupManager.displayMessage(
                            {
                                displayTime: 3,
                                text: this.msg("label.need_more_symbols_for_search")
                            });
                }
            },
            onClearSearch: function Toolbar_onSearch() {
                Dom.get(this.id + "-full-text-search").value = "";
                if (this.modules.dataGrid) {
                    var dataGrid = this.modules.dataGrid;
                    var datagridMeta = dataGrid.datagridMeta;

                    datagridMeta.useOnlyInSameOrg = true;
                    datagridMeta.useFilterByOrg = true;

                    //сбрасываем на значение по умолчанию
                    if (datagridMeta.searchConfig) {
                        // сбрасываем терм поиска
                        if (datagridMeta.searchConfig.fullTextSearch) {
                            if (typeof datagridMeta.searchConfig.fullTextSearch == "string") {
                                datagridMeta.searchConfig.fullTextSearch = YAHOO.lang.JSON.parse(datagridMeta.searchConfig.fullTextSearch);
                            }
                            datagridMeta.searchConfig.fullTextSearch.searchTerm = null;

                            //проверяем, заполнена ли форма (атрибутивный поиск)
                            if (datagridMeta.searchConfig.formData) {
                                if (typeof datagridMeta.searchConfig.formData == "string") {
                                    datagridMeta.searchConfig.formData = YAHOO.lang.JSON.parse(datagridMeta.searchConfig.formData);
                                }
                                var nProps = 0;
                                for (var key in datagridMeta.searchConfig.formData) {
                                    nProps++;
                                }
                                if (nProps <= 1) {
                                    datagridMeta.searchConfig.fullTextSearch = null;
                                }
                            } else {
                                datagridMeta.searchConfig.fullTextSearch = null;
                            }
                        }
                    }
                    this.modules.dataGrid.search.performSearch({
                        parent: datagridMeta.nodeRef,
                        itemType: datagridMeta.itemType,
                        searchConfig: datagridMeta.searchConfig,
                        searchShowInactive: dataGrid.options.searchShowInactive,
                        useFilterByOrg: true,
                        useOnlyInSameOrg: true,
                        sort: datagridMeta.sort
                    });
                    this.checkShowClearSearch();
                }
            }
        }, true);

        new LogicECM.module.OrgStructure.DictionaryToolbar("${id}-toolbar").setMessages(${messages}).setOptions({
            minSTermLength: 3,
            bubblingLabel: "${bubblingLabel!'employees'}"
        });
    }

    function initComponents() {
        createToolbar();
        createDatagrid();
    }

    function init() {
        LogicECM.module.Base.Util.loadResources([
            'modules/simple-dialog.js',
            'scripts/lecm-base/components/advsearch.js',
            'scripts/lecm-base/components/lecm-datagrid.js',
            'scripts/lecm-base/components/lecm-toolbar.js',
            'scripts/lecm-orgstructure/orgstructure-toolbar.js'
        ], [
            'components/search/search.css',
            'modules/document-details/historic-properties-viewer.css',
            'yui/treeview/assets/skins/sam/treeview.css',
            'components/data-lists/toolbar.css',
            'css/lecm-orgstructure/orgstructure-tree.css'
        ], initComponents);
    }

    YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>

<div id="${id}-toolbar"></div>
<@comp.baseToolbar "${id}-toolbar" false true false/>

<div class="yui-t1" id="orgstructure-employees-grid">
    <div id="yui-main-2">
        <div class="yui-b body scrollableList datagrid-content" id="alf-content">
            <!-- include base datagrid markup-->
        <@grid.datagrid id=id showViewForm=false showArchiveCheckBox=false/>
        </div>
    </div>
</div>
