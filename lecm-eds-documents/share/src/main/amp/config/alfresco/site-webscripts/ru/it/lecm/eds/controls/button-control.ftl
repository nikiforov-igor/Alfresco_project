<#assign formId=args.htmlid?js_string?html/>

<#assign insertType = "prepend"/>
<#if field.control.params.insertType??>
    <#assign insertType = field.control.params.insertType/>
</#if>
<#assign clickFireAction = "insertedButtonClick"/>
<#if field.control.params.clickFireAction??>
    <#assign clickFireAction = field.control.params.clickFireAction/>
</#if>
<#assign insertToDiv = "form-buttons"/>
<#if field.control.params.insertToDiv??>
    <#assign insertToDiv = field.control.params.insertToDiv/>
</#if>
<span id="${fieldHtmlId}" class="yui-button yui-push-button">
    <span class="first-child">
        <button type="button">${msg("${field.label?html}")}</button>
    </span>
</span>

<script type="text/javascript">//<![CDATA[
(function() {

    function init(){

        var formButtons = YAHOO.util.Dom.get("${formId}-${insertToDiv}");
        var button = YAHOO.util.Dom.get("${fieldHtmlId}");
        if(formButtons && button) {
            switch ("${insertType}") {
                case 'append':
                    formButtons.appendChild(button);
                    break;
                case 'prepend':
                    formButtons.insertBefore(button,formButtons.firstElementChild);
                    break;
                default:
                    formButtons.insertBefore(button,formButtons.firstElementChild);
                    break;
            }
            YAHOO.util.Event.addListener(button,'click',function(){
                YAHOO.Bubbling.fire("${clickFireAction}", {
                    formId: "${formId}",
                    fieldHtmlId: "${fieldHtmlId}"
                });});

        }
    }

    YAHOO.util.Event.onAvailable("${fieldHtmlId}",init);

})();
//]]></script>