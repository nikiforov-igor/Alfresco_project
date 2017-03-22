<#import "/org/alfresco/components/form/form.lib.ftl" as formLib />

<#if formUI == "true">
    <@formLib.renderFormsRuntime formId=formId />
</#if>

<#assign aDateTime = .now>
<#assign panelId = "view-attributes-panel-" + aDateTime?iso_utc>

<@formLib.renderFormContainer formId=formId>

<div id="${panelId}" class="yui-panel hidden1">
    <div id="${panelId}-head" class="hd">Просмотр</div>
    <div id="${panelId}-body" class="bd">
        <div id="${panelId}-content"></div>
        <div class="bdft">
                <span id="${panelId}-cancel" class="yui-button yui-push-button">
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

    <div class="control textfield viewmode">
        <div class="label-div">
            <label>Дата:</label>
        </div>
        <div class="container">
            <div class="value-div">
                <span id="${formId}-date">(Нет)</span>
            </div>
        </div>
    </div>

    <div class="control textfield viewmode">
        <div class="label-div">
            <label>Описание:</label>
        </div>
        <div class="container">
            <div class="value-div">
                <span id="${formId}-description">(Нет)</span>
            </div>
        </div>
    </div>

    <div class="control textfield viewmode">
        <div class="label-div">
            <label>Категория событий:</label>
        </div>
        <div class="container">
            <div class="value-div">
                <span id="${formId}-category">(Нет)</span>
            </div>
        </div>
    </div>

    <div class="control textfield viewmode">
        <div class="label-div">
            <label>Тип объекта:</label>
        </div>
        <div class="container">
            <div class="value-div">
                <span id="${formId}-type">(Нет)</span>
            </div>
        </div>
    </div>

    <div class="control textfield viewmode">
        <div class="label-div">
            <label>Инициатор:</label>
        </div>
        <div class="container">
            <div class="value-div">
                <span id="${formId}-initiator">(Нет)</span>
            </div>
        </div>
    </div>

    <div class="control textfield viewmode">
        <div class="label-div">
            <label>Основной объект:</label>
        </div>
        <div class="container">
            <div class="value-div">
                <span id="${formId}-mainObject">(Нет)</span>
            </div>
        </div>
    </div>

</div>
</div><div class="tab-secondary yui-hidden">
<div class="set">

    <div class="control textfield viewmode">
        <div class="label-div">
            <label>Объект 1:</label>
        </div>
        <div class="container">
            <div class="value-div">
                <span id="${formId}-object1">(Нет)</span>
            </div>
        </div>
    </div>

    <div class="control textfield viewmode">
        <div class="label-div">
            <label>Объект 2:</label>
        </div>
        <div class="container">
            <div class="value-div">
                <span id="${formId}-object2">(Нет)</span>
            </div>
        </div>
    </div>

    <div class="control textfield viewmode">
        <div class="label-div">
            <label>Объект 3:</label>
        </div>
        <div class="container">
            <div class="value-div">
                <span id="${formId}-object3">(Нет)</span>
            </div>
        </div>
    </div>

    <div class="control textfield viewmode">
        <div class="label-div">
            <label>Объект 4:</label>
        </div>
        <div class="container">
            <div class="value-div">
                <span id="${formId}-object4">(Нет)</span>
            </div>
        </div>
    </div>

    <div class="control textfield viewmode">
        <div class="label-div">
            <label>Объект 5:</label>
        </div>
        <div class="container">
            <div class="value-div">
                <span id="${formId}-object5">(Нет)</span>
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

    function loadDeps() {
        LogicECM.module.Base.Util.loadScripts([
            'scripts/lecm-base/components/object-finder/lecm-object-finder.js'
        ],init,["tabview"]);
    }

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

                    function addOnClickListener(el,ref){
                        var reqObj = {
                            formId: '${panelId}',
                            itemId: ref,
                            htmlId: 'LinkMetadata-' + Alfresco.util.generateDomId(),
                            setId: 'common',
                            failureMessage: 'message.object-not-found'
                        };
                        YAHOO.util.Event.addListener(el, "click",LogicECM.module.Base.Util.viewAttributes.bind(LogicECM.module.Base.Util,reqObj));
                    }

                    var response = eval("(" + oResponse.responseText + ")");
                    if (response.date) {
                        document.getElementById("${formId}-date").innerHTML =  Alfresco.util.formatDate(Alfresco.util.fromISO8601(response.date), "dd mmm yyyy HH:MM:ss");
                    }
                    if (response.description) {
                        document.getElementById("${formId}-description").innerHTML = response.description;
                    }
                    if (response.category) {
                        document.getElementById("${formId}-category").innerHTML = response.category;
                    }
                    if (response.type) {
                        if (response.typeRef) {
                            var el = document.getElementById("${formId}-type");
                            el.innerHTML = "<a href='javascript:void(0);'>" + response.type + "</a>";
                            addOnClickListener(el,response.typeRef);
                        } else {
                            document.getElementById("${formId}-type").innerHTML = response.type;
                        }
                    }
                    if (response.initiator) {
                        if (response.initiatorRef) {
                            var el = document.getElementById("${formId}-initiator");
                            el.innerHTML = "<a href='javascript:void(0);'>" + response.initiator + "</a>";
                            addOnClickListener(el,response.initiatorRef);
                        } else {
                            document.getElementById("${formId}-initiator").innerHTML = response.initiator;
                        }
                    }
                    if (response.mainObject) {
                        if (response.mainObjectRef) {
                            var el = document.getElementById("${formId}-mainObject");
                            el.innerHTML = "<a href='javascript:void(0);'>" + response.mainObject + "</a>";
                            addOnClickListener(el,response.mainObjectRef);
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

    Event.onDOMReady(loadDeps);
})();
//]]></script>

