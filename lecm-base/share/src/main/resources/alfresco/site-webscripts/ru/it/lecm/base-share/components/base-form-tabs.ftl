<#import "/org/alfresco/components/form/form.lib.ftl" as formLib />

<#if formUI == "true">
    <@formLib.renderFormsRuntime formId=formId />
</#if>

<@formLib.renderFormContainer formId=formId>
<div id="${formId}-tabs" class="yui-navset form-tabs">
    <ul class="yui-nav">
        <#list form.structure as item>
            <#if item.kind == "set">
                <li <#if (item_index == 0 && !args.setId??) || (args.setId?? && item.id == args.setId)>class="selected"</#if>>
                    <a href="#tab${item_index + 1}">
                        <em>${item.label}</em>
                    </a>
                </li>
            </#if>
        </#list>
    </ul>
    <div class="yui-content">
        <#list form.structure as item>
            <#if item.kind == "set">
                <div class="tab-${item.id!""}"><@formLib.renderSet set=item /></div>
            </#if>
        </#list>
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

                YAHOO.Bubbling.fire("activeTabChange", e);
            }

            tabs.addListener('beforeActiveTabChange', onBeforeActive);
            tabs.addListener('activeTabChange', onActive);
            LogicECM.module.Base.Util.setHeight();
        });
    }

    Event.onDOMReady(init);
})();
//]]></script>

