<div class="form-field">
<#escape x as x?js_string>
    <div class="viewmode-field">
        <span class="viewmode-label">${field.label?html}:</span>
    </div>
    <div id="experts" class="yui-skin-sam"></div>
    <#if form.mode != "view">
        <br/>
        <div id="buttons" class="yui-skin-sam"></div>
    <#else>
    </#if>
</#escape>
</div>
<script type="text/javascript" src="${url.context}/res/ru/it/lecm/experts/experts.js"></script>
<script type="text/javascript">//<![CDATA[
(function(){
    function init() {
        var expertsTable = new LogicECM.module.Experts("experts");
        expertsTable.init("${form.arguments.itemId}");
    }

    YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>


