<#import "/org/alfresco/components/form/form.lib.ftl" as formLib />

<#if formUI == "true">
    <@formLib.renderFormsRuntime formId=formId />
</#if>

<@formLib.renderFormContainer formId=formId>
<div id="${formId}-tabs" class="yui-navset">
    <ul class="yui-nav">
        <#list form.structure as item>
            <#if item.kind == "set">
                <li <#if item_index == 0>class="selected"</#if>>
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
                <div><p><@formLib.renderSet set=item /></p></div>
            </#if>
        </#list>
    </div>
</div>
</@>

<script type="text/javascript">//<![CDATA[
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event,
        Selector = YAHOO.util.Selector;

    function init() {
        Event.onContentReady("${formId}-tabs", function() {
            var parent = Dom.get("${formId}-tabs")[0];
            var tabs = new YAHOO.widget.TabView(Dom.getElementsByClassName('yui-navset', 'div', parent)[0]);
            var links = Selector.query('a', Dom.getElementsByClassName('yui-nav', 'ul', parent)[0], false);

            Event.addListener(links, 'click', function () {
                setTimeout(function () {
                    LogicECM.module.Base.Util.setHeight();
                }, 10);
            });
            LogicECM.module.Base.Util.setHeight();
        });
    }

    Event.onDOMReady(init);
//]]></script>

