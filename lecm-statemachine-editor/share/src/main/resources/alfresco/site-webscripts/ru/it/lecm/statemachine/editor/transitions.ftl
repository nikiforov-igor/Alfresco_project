<#assign controlId = fieldHtmlId + "-cntrl">

<script type="text/javascript">//<![CDATA[
(function () {
    function init() {
        var transitions = new LogicECM.module.Transitions("transitions");
        transitions.init("${form.arguments.itemId}");
    }

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
    <div id="${controlId}">
        <label for="${controlId}">${field.label?html}:<#if field.endpointMandatory!false || field.mandatory!false>
            <span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
        <div id="transitions" class="yui-skin-sam"></div>
        <br/>
        <div id="buttons" class="yui-skin-sam"></div>
    </div>
</#escape>
</div>



