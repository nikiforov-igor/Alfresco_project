<#import "/org/alfresco/components/form/form.lib.ftl" as formLib />

<#if formUI == "true">
    <@formLib.renderFormsRuntime formId=formId />
</#if>

<@formLib.renderFormContainer formId=formId>

<div id="view-attributes-panel" class="yui-panel" style="display: none">
    <div id="view-attributes-panel-head" class="hd">Просмотр</div>
    <div id="view-attributes-panel-body" class="bd">
        <div id="view-attributes-panel-content"></div>
        <div class="bdft">
                <span id="view-attributes-panel-cancel" class="yui-button yui-push-button">
                    <span class="first-child">
                        <button type="button" tabindex="0" onclick="_hideLinkAttributes()">Закрыть</button>
                    </span>
                </span>
        </div>
    </div>
</div>

<div id="${formId}-tabs" class="yui-navset yui-navset-top">
<ul class="yui-nav">
    <li class="selected">
        <a href="#tab1">
            <em>Основные сведения</em>
        </a>
    </li>
    <li>
        <a href="#tab2">
            <em>Дополнительные объекты</em>
        </a>
    </li>
</ul>
<div class="yui-content">

<div class="tab-common"><div class="set">
    <div class="form-field">
        <div class="viewmode-field">
        <span class="viewmode-label">Дата:</span>
         <span class="viewmode-value" id="${formId}-date">
             (Нет)
         </span>
    </span></span></div>
    </div><div class="form-field">
    <div class="viewmode-field">
        <span class="viewmode-label">Описание:</span>
        <span class="viewmode-value" id="${formId}-description">(Нет)</span>
    </div>
</div>

<div class="form-field">
    <div class="viewmode-field">
        <span class="viewmode-label">Категория событий:</span>
        <span id="${formId}-category" class="viewmode-value">(Нет)</span>
    </div>
    <input type="hidden" id="view-node-form-htmlid_assoc_lecm-busjournal_bjRecord-evCategory-assoc" name="assoc_lecm-busjournal_bjRecord-evCategory-assoc" value="">
</div>

<div class="form-field">
    <div class="viewmode-field">
        <span class="viewmode-label">Тип объекта:</span>
        <span id="${formId}-type" class="viewmode-value">(Нет)</span>
    </div>
    <input type="hidden" id="view-node-form-htmlid_assoc_lecm-busjournal_bjRecord-objType-assoc" name="assoc_lecm-busjournal_bjRecord-objType-assoc" value="">
</div>


<div class="form-field">
    <div id="view-node-form-htmlid_assoc_lecm-busjournal_bjRecord-initiator-assoc-cntrl" class="viewmode-field">
        <span class="viewmode-label">Инициатор:</span>
        <span id="${formId}-initiator" class="viewmode-value current-values object-finder-items">(Нет)</span>
    </div>
    <div id="view-node-form-htmlid_assoc_lecm-busjournal_bjRecord-initiator-assoc-cntrl-link" class="yui-panel" style="display: none">
        <div id="view-node-form-htmlid_assoc_lecm-busjournal_bjRecord-initiator-assoc-cntrl-link-head" class="hd">Просмотр</div>
        <div id="view-node-form-htmlid_assoc_lecm-busjournal_bjRecord-initiator-assoc-cntrl-link-body" class="bd">
            <div id="view-node-form-htmlid_assoc_lecm-busjournal_bjRecord-initiator-assoc-cntrl-link-content"></div>
            <div class="bdft">
    <span id="view-node-form-htmlid_assoc_lecm-busjournal_bjRecord-initiator-assoc-cntrl-link-cancel" class="yui-button yui-push-button">
    <span class="first-child">
    <button type="button" tabindex="0" onclick="_hideLinkAttributes()">Закрыть</button>
    </span>
    </span>
            </div>
        </div>
    </div>
</div>


<div class="form-field">
    <div id="view-node-form-htmlid_assoc_lecm-busjournal_bjRecord-mainObject-assoc-cntrl" class="viewmode-field">
        <span class="viewmode-label">Основной объект:</span>
        <span id="${formId}-mainObject" class="viewmode-value current-values object-finder-items">(Нет)</span>
    </div>
    <div id="view-node-form-htmlid_assoc_lecm-busjournal_bjRecord-mainObject-assoc-cntrl-link" class="yui-panel" style="display: none">
        <div id="view-node-form-htmlid_assoc_lecm-busjournal_bjRecord-mainObject-assoc-cntrl-link-head" class="hd">Просмотр</div>
        <div id="view-node-form-htmlid_assoc_lecm-busjournal_bjRecord-mainObject-assoc-cntrl-link-body" class="bd">
            <div id="view-node-form-htmlid_assoc_lecm-busjournal_bjRecord-mainObject-assoc-cntrl-link-content"></div>
            <div class="bdft">
    <span id="view-node-form-htmlid_assoc_lecm-busjournal_bjRecord-mainObject-assoc-cntrl-link-cancel" class="yui-button yui-push-button">
    <span class="first-child">
    <button type="button" tabindex="0" onclick="_hideLinkAttributes()">Закрыть</button>
    </span>
    </span>
    </div>
</div>
</div>
</div>
</div>
</div><div class="tab-secondary yui-hidden">
<div class="set">
    <div class="form-field">
        <div id="view-node-form-htmlid_assoc_lecm-busjournal_bjRecord-secondaryObj1-assoc-cntrl" class="viewmode-field">
            <span class="viewmode-label">Объект 1:</span>
            <span id="${formId}-object1" class="viewmode-value current-values object-finder-items">(Нет)</span>
        </div>
        <div id="view-node-form-htmlid_assoc_lecm-busjournal_bjRecord-secondaryObj1-assoc-cntrl-link" class="yui-panel" style="display: none">
            <div id="view-node-form-htmlid_assoc_lecm-busjournal_bjRecord-secondaryObj1-assoc-cntrl-link-head" class="hd">Просмотр</div>
            <div id="view-node-form-htmlid_assoc_lecm-busjournal_bjRecord-secondaryObj1-assoc-cntrl-link-body" class="bd">
                <div id="view-node-form-htmlid_assoc_lecm-busjournal_bjRecord-secondaryObj1-assoc-cntrl-link-content"></div>
                <div class="bdft">
        <span id="view-node-form-htmlid_assoc_lecm-busjournal_bjRecord-secondaryObj1-assoc-cntrl-link-cancel" class="yui-button yui-push-button">
        <span class="first-child">
        <button type="button" tabindex="0" onclick="_hideLinkAttributes()">Закрыть</button>
        </span>
        </span>
                </div>
            </div>
        </div>
    </div>

    <div class="form-field">
        <div id="view-node-form-htmlid_assoc_lecm-busjournal_bjRecord-secondaryObj2-assoc-cntrl" class="viewmode-field">
            <span class="viewmode-label">Объект 2:</span>
            <span id="${formId}-object1" class="viewmode-value current-values object-finder-items">(Нет)</span>
        </div>
        <div id="view-node-form-htmlid_assoc_lecm-busjournal_bjRecord-secondaryObj2-assoc-cntrl-link" class="yui-panel" style="display: none">
            <div id="view-node-form-htmlid_assoc_lecm-busjournal_bjRecord-secondaryObj2-assoc-cntrl-link-head" class="hd">Просмотр</div>
            <div id="view-node-form-htmlid_assoc_lecm-busjournal_bjRecord-secondaryObj2-assoc-cntrl-link-body" class="bd">
                <div id="view-node-form-htmlid_assoc_lecm-busjournal_bjRecord-secondaryObj2-assoc-cntrl-link-content"></div>
                <div class="bdft">
        <span id="view-node-form-htmlid_assoc_lecm-busjournal_bjRecord-secondaryObj2-assoc-cntrl-link-cancel" class="yui-button yui-push-button">
        <span class="first-child">
        <button type="button" tabindex="0" onclick="_hideLinkAttributes()">Закрыть</button>
        </span>
        </span>
                </div>
            </div>
        </div>
    </div>

    <div class="form-field">
        <div id="view-node-form-htmlid_assoc_lecm-busjournal_bjRecord-secondaryObj3-assoc-cntrl" class="viewmode-field">
            <span class="viewmode-label">Объект 3:</span>
            <span id="${formId}-object1" class="viewmode-value current-values object-finder-items">(Нет)</span>
        </div>
        <div id="view-node-form-htmlid_assoc_lecm-busjournal_bjRecord-secondaryObj3-assoc-cntrl-link" class="yui-panel" style="display: none">
            <div id="view-node-form-htmlid_assoc_lecm-busjournal_bjRecord-secondaryObj3-assoc-cntrl-link-head" class="hd">Просмотр</div>
            <div id="view-node-form-htmlid_assoc_lecm-busjournal_bjRecord-secondaryObj3-assoc-cntrl-link-body" class="bd">
                <div id="view-node-form-htmlid_assoc_lecm-busjournal_bjRecord-secondaryObj3-assoc-cntrl-link-content"></div>
                <div class="bdft">
        <span id="view-node-form-htmlid_assoc_lecm-busjournal_bjRecord-secondaryObj3-assoc-cntrl-link-cancel" class="yui-button yui-push-button">
        <span class="first-child">
        <button type="button" tabindex="0" onclick="_hideLinkAttributes()">Закрыть</button>
        </span>
        </span>
                </div>
            </div>
        </div>
    </div>

    <div class="form-field">
        <div id="view-node-form-htmlid_assoc_lecm-busjournal_bjRecord-secondaryObj4-assoc-cntrl" class="viewmode-field">
            <span class="viewmode-label">Объект 4:</span>
            <span id="${formId}-object1" class="viewmode-value current-values object-finder-items">(Нет)</span>
        </div>
        <div id="view-node-form-htmlid_assoc_lecm-busjournal_bjRecord-secondaryObj4-assoc-cntrl-link" class="yui-panel" style="display: none">
            <div id="view-node-form-htmlid_assoc_lecm-busjournal_bjRecord-secondaryObj4-assoc-cntrl-link-head" class="hd">Просмотр</div>
            <div id="view-node-form-htmlid_assoc_lecm-busjournal_bjRecord-secondaryObj4-assoc-cntrl-link-body" class="bd">
                <div id="view-node-form-htmlid_assoc_lecm-busjournal_bjRecord-secondaryObj4-assoc-cntrl-link-content"></div>
                <div class="bdft">
        <span id="view-node-form-htmlid_assoc_lecm-busjournal_bjRecord-secondaryObj4-assoc-cntrl-link-cancel" class="yui-button yui-push-button">
        <span class="first-child">
        <button type="button" tabindex="0" onclick="_hideLinkAttributes()">Закрыть</button>
        </span>
        </span>
                </div>
            </div>
        </div>
    </div>

    <div class="form-field">
        <div id="view-node-form-htmlid_assoc_lecm-busjournal_bjRecord-secondaryObj5-assoc-cntrl" class="viewmode-field">
            <span class="viewmode-label">Объект 5:</span>
            <span id="${formId}-object1" class="viewmode-value current-values object-finder-items">(Нет)</span>
        </div>
        <div id="view-node-form-htmlid_assoc_lecm-busjournal_bjRecord-secondaryObj5-assoc-cntrl-link" class="yui-panel" style="display: none">
            <div id="view-node-form-htmlid_assoc_lecm-busjournal_bjRecord-secondaryObj5-assoc-cntrl-link-head" class="hd">Просмотр</div>
            <div id="view-node-form-htmlid_assoc_lecm-busjournal_bjRecord-secondaryObj5-assoc-cntrl-link-body" class="bd">
                <div id="view-node-form-htmlid_assoc_lecm-busjournal_bjRecord-secondaryObj5-assoc-cntrl-link-content"></div>
                <div class="bdft">
        <span id="view-node-form-htmlid_assoc_lecm-busjournal_bjRecord-secondaryObj5-assoc-cntrl-link-cancel" class="yui-button yui-push-button">
        <span class="first-child">
        <button type="button" tabindex="0" onclick="_hideLinkAttributes()">Закрыть</button>
        </span>
        </span>
            </div>
        </div>
    </div>
</div>
</div>
</div>
</div>
</@>

<script type="text/javascript">//<![CDATA[
(function() {
    var Dom = YAHOO.util.Dom,
            Event = YAHOO.util.Event,
            Selector = YAHOO.util.Selector;

    YAHOO.widget.Tab.prototype.ACTIVE_TITLE = '';

    function init() {
        Event.onContentReady("${formId}-tabs", function() {
            var parent = Dom.get("${formId}-fields");
            var tabs = new YAHOO.widget.TabView(Dom.getElementsByClassName('yui-navset', 'div', parent)[0]);
            var prevTabHeight;
            function onBeforeActive(e) {
                var prev = e.prevValue.get("contentEl");

                prevTabHeight = parseFloat(Dom.getStyle(prev, 'height'));
            }
            function onActive(e) {
                var current = e.newValue.get("contentEl");
                var currentHeight = parseFloat(Dom.getStyle(current, 'height'));

                if ((prevTabHeight > 0) && (currentHeight < prevTabHeight)) {
                    Dom.setStyle(current, 'height', prevTabHeight + 'px');
                }
                setTimeout(function () {
                    LogicECM.module.Base.Util.setHeight();
                }, 10);
            }

            tabs.addListener('beforeActiveTabChange', onBeforeActive);
            tabs.addListener('activeTabChange', onActive);
            LogicECM.module.Base.Util.setHeight();

            //load data
            var sUrl = Alfresco.constants.PROXY_URI + "/lecm/business-journal/component/record?recordId=${args.nodeId}";
            var callback = {
                success: function (oResponse) {
                    var response = eval("(" + oResponse.responseText + ")");
                    if (response.date) {
                        document.getElementById("${formId}-date").innerHTML = response.date;
                    }
                    if (response.description) {
                        document.getElementById("${formId}-description").innerHTML = response.description;
                    }
                    if (response.category) {
                        document.getElementById("${formId}-category").innerHTML = response.category;
                    }
                    if (response.type) {
                        if (response.typeRef) {
                            document.getElementById("${formId}-type").innerHTML = "<a href='javascript:void(0);' onclick=\"_viewLinkAttributes('view-attributes-panel','" + response.typeRef + "')\">" + response.type + "</a>";
                        } else {
                            document.getElementById("${formId}-type").innerHTML = response.type;
                        }
                    }
                    if (response.initiator) {
                        if (response.initiatorRef) {
                            document.getElementById("${formId}-initiator").innerHTML = "<a href='javascript:void(0);' onclick=\"_viewLinkAttributes('view-attributes-panel','" + response.initiatorRef + "')\">" + response.initiator + "</a>";
                        } else {
                            document.getElementById("${formId}-initiator").innerHTML = response.initiator;
                        }
                    }
                    if (response.mainObject) {
                        if (response.mainObjectRef) {
                            document.getElementById("${formId}-mainObject").innerHTML = "<a href='javascript:void(0);' onclick=\"_viewLinkAttributes('view-attributes-panel','" + response.mainObjectRef + "')\">" + response.mainObject + "</a>";
                        } else {
                            document.getElementById("${formId}-mainObject").innerHTML = response.mainObject;
                        }
                    }
                    if (response.object1) {
                        document.getElementById("${formId}-object1").innerHTML = response.object1;
                    }
                    if (response.object2) {
                        document.getElementById("${formId}-object2").innerHTML = response.object2;
                    }
                    if (response.object3) {
                        document.getElementById("${formId}-object3").innerHTML = response.object3;
                    }
                    if (response.object4) {
                        document.getElementById("${formId}-object4").innerHTML = response.object4;
                    }
                    if (response.object5) {
                        document.getElementById("${formId}-object5").innerHTML = response.object5;
                    }

                },
                argument:{
                    parent: this
                },
                timeout: 60000
            };
            YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
        });
    }

    Event.onDOMReady(init);
})();
//]]></script>

