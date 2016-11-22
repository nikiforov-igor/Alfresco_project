<#import "/org/alfresco/components/form/form.lib.ftl" as formLib />
<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-errands/errands-create-form.css" />

<#assign formId=args.htmlid?js_string?html + "-form"/>
<#assign formUI><#if args.formUI??>${args.formUI}<#else>true</#if></#assign>
<#assign setCount=0/>

<#if formUI == "true">
    <@formLib.renderFormsRuntime formId=formId />
</#if>

<@formLib.renderFormContainer formId=formId>
    <#list form.structure as item>
        <#if item.kind == "set">
            <#assign setCount=setCount+1/>
            <#if setCount gt 2 && item.id?matches("static[0-9]*") != true>
                <#if item.id == "block3">
                <div class="${formId}-panel-block3 hidden1">
                    <@formLib.renderSet set=item/>
                </div>
                <#else>
                <div class="${formId}-panel-block hidden1">
                    <@formLib.renderSet set=item/>
                </div>
                </#if>
            <#else>
                <@formLib.renderSet set=item/>
            </#if>
        <#else>
            <@formLib.renderField field=form.fields[item.id] />
        </#if>
    </#list>

    <span id="${formId}-expand-panel" class="yui-button yui-push-button">
        <span class="first-child">
            <button type="button">${msg("label.show-details")}</button>
        </span>
    </span>
    <span id="${formId}-save-draft" class="yui-button yui-push-button">
        <span class="first-child">
            <button type="button">${msg("label.save-draft")}</button>
        </span>
    </span>

</@>



<script type="text/javascript">
    (function () {
        var Dom = YAHOO.util.Dom,
                Event = YAHOO.util.Event,
                Selector = YAHOO.util.Selector,
                Bubbling = YAHOO.Bubbling,
                expandButton,
                saveDraftButton,
                periodicallyCheckBox,
                routeButton,
                formButtons,
                hidden;


        function init() {
            var toggleBlock = function (e) {
                var panels = [];
                if (e.target == periodicallyCheckBox) {
                    panels = Dom.getElementsByClassName('${formId}-panel-block3');
                } else {
                    expandButton.parentNode.removeChild(expandButton);
                    panels = Dom.getElementsByClassName('${formId}-panel-block');
                    Dom.removeClass(formButtons,"form-4-buttons");
                }
                var hidden = panels[0].classList.contains("hidden1");
                for (var i = 0; i < panels.length; i++) {
                    if (hidden) {
                        Dom.removeClass(panels[i], 'hidden1');
                    } else {
                        Dom.addClass(panels[i], 'hidden1');
                    }
                }

            };
            var saveDraft = function () {
                hidden.value = "false";
                routeButton = Selector.query(".yui-submit-button", formButtons, true);
                routeButton.click();
            };

            var periodicallyInput = document.getElementsByName('prop_lecm-errands_periodically')[0];
            periodicallyCheckBox = Dom.get(periodicallyInput.id+'-entry');
            if (periodicallyCheckBox) {
                Event.addListener(periodicallyCheckBox, "change", toggleBlock);
            }
            routeButton = Dom.get("${formId}-submit");
            expandButton = Dom.get("${formId}-expand-panel");
            saveDraftButton = Dom.get("${formId}-save-draft");
            Event.addListener(expandButton,"click",toggleBlock);
            Event.addListener(saveDraftButton,"click",saveDraft);
            Dom.insertBefore(saveDraftButton,routeButton);
            Dom.insertBefore(expandButton,saveDraftButton);
            formButtons = Dom.get("${formId}-buttons");
            Dom.addClass(formButtons,"form-4-buttons");

            hidden = Dom.getElementBy(function (el) {
                return el.name == "prop_lecm-errands_is-short";
            }, 'input', "${formId}");

            hidden.value = "true";


            Alfresco.util.Ajax.request({
                url: Alfresco.constants.PROXY_URI + "/lecm/errands/isHideAdditionAttributes",
                successCallback: {
                    fn: function (response) {
                        var oResults = JSON.parse(response.serverResponse.responseText);
                        if (oResults && !oResults.hide) {
                            toggleBlock();
                        }
                    }
                },
                failureMessage: "message.failure"
            });

        }

        Event.onContentReady("${formId}", init);
    })();
</script>