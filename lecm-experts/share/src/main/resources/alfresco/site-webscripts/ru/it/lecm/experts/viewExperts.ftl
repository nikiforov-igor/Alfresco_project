<#assign controlId = fieldHtmlId + "-cntrl">

<script type="text/javascript">//<![CDATA[
(function () {
    function init() {
        var expertsTable = new LogicECM.module.Experts("experts");
        expertsTable.init("${form.arguments.itemId}");
    }

    //YAHOO.util.Event.onDOMReady(init);
    //YAHOO.util.Event.addListener("experts", "load", init);
    function ElementAvaiable(id) {
        YAHOO.util.Event.onContentReady(id, this.handleOnAvailable, this);
    }

    ElementAvaiable.prototype.handleOnAvailable = function (me) {
        init();
    }

    var obj = new ElementAvaiable("${controlId}");

})();
//]]></script>

<div class="form-field">
<#escape x as x?js_string>
    <#if form.mode == "view">
        <div id="${controlId}" class="viewmode-field">
            <#if (field.endpointMandatory!false || field.mandatory!false) && field.value == "">
            <span class="incomplete-warning">
                <img src="${url.context}/res/components/form/images/warning-16.png" title="${msg("form.field.incomplete")}"/>
            <span>
            </#if>
            <span class="viewmode-label">${field.label?html}:</span>
            <div id="experts" class="yui-skin-sam"></div>
        </div>
    <#else>
        <div id="${controlId}">
            <label for="${controlId}">${field.label?html}:<#if field.endpointMandatory!false || field.mandatory!false>
                <span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
            <div id="experts" class="yui-skin-sam"></div>
            <br/>
            <div id="buttons" class="yui-skin-sam"></div>
        </div>
    </#if>
</#escape>
</div>



