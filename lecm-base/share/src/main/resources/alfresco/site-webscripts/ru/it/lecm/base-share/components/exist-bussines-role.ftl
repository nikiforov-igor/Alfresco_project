<#import "/org/alfresco/components/form/form.lib.ftl" as formLib />

<#if item??>
    <#assign thisSet = item />
<#else>
    <#assign thisSet = set />
</#if >

<#assign id=args.htmlid/>

<div id="${id}-${thisSet.id}-exist-panel" class="exist-panel">
    <#list thisSet.children as unit>
        <@formLib.renderField field=form.fields[unit.id] />
    </#list>
</div>

<script type="text/javascript">//<![CDATA[
(function () {
    var Dom = YAHOO.util.Dom,
            Event = YAHOO.util.Event;
    var idPanel = "${id}-${thisSet.id}-exist-panel";

    function init() {
        Alfresco.util.Ajax.request(
                {
                    url: Alfresco.constants.PROXY_URI + "lecm/orgstructure/isCurrentEmployeeHasBusinessRole?roleId="+"${thisSet.appearance}",
                    successCallback: {
                        fn: function (response) {
                            if (response.json == false) {
                                Dom.setStyle(idPanel,"visibility","hidden");
                                Dom.setStyle(idPanel,"position","absolute");

                                var inputs = Dom.getElementsBy(function() {return true;},"input", Dom.get(idPanel));
                                if (inputs) {
                                    for (var index = 0; index < inputs.length; ++index) {
	                                    if (inputs[index].type == "text" || inputs[index].type == "button") {
		                                    inputs[index].setAttribute("disabled","disabled");
	                                    }
                                    }
                                }

	                            var buttons = Dom.getElementsBy(function() {return true;},"button", Dom.get(idPanel));
	                            if (buttons) {
		                            for (var index = 0; index < buttons.length; ++index) {
			                            buttons[index].setAttribute("disabled","disabled");
		                            }
	                            }
                            }
                        }
                    },
                    failureMessage: {
                        fn: function () {
                            alert("failure.message");
                        }
                    }
                });
    }

    Event.onContentReady(idPanel, init);
})();
//]]></script>