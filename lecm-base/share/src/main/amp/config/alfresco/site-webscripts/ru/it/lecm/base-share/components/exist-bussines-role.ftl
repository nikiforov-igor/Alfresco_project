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

    function hidePanel() {
        Dom.setStyle(idPanel, "display", "none");
        Dom.setStyle(idPanel, "position", "absolute");

        var inputs = Dom.getElementsBy(function () {
            return true;
        }, "input", Dom.get(idPanel));
        if (inputs) {
            for (var index = 0; index < inputs.length; ++index) {
                if (inputs[index].type == "text" || inputs[index].type == "button") {
                    inputs[index].setAttribute("disabled", "disabled");
                }
            }
        }

        var buttons = Dom.getElementsBy(function () {
            return true;
        }, "button", Dom.get(idPanel));
        if (buttons) {
            for (var index = 0; index < buttons.length; ++index) {
                buttons[index].setAttribute("disabled", "disabled");
            }
        }
    }

    function init() {
        var brPermission = "${thisSet.appearance}";
        var notCase = (brPermission.indexOf("!") == 0);
        var isBRole = (brPermission.indexOf("~") < 0);
        brPermission = brPermission.replace("!", "").replace("~", "");

        Alfresco.util.Ajax.jsonGet({
			url: Alfresco.constants.PROXY_URI + (isBRole ? "lecm/orgstructure/isCurrentEmployeeHasBusinessRole" : "lecm/security/api/getPermission"),
			dataObj: {
				nodeRef: "${form.arguments.itemId}",
				roleId: brPermission,
				permission: brPermission
			},
			successCallback: {
				fn: function (response) {
					if ((notCase && response.json === true) || (!notCase && response.json === false)) {
						// НЕ УДАЛАЯТЬ!
						// Для карточки сотрудника:
						// показывать содержимое, если сотрудник просматривает свою карточку, иначе - скрыть
						Alfresco.util.Ajax.jsonGet({
							url: Alfresco.constants.PROXY_URI + "lecm/orgstructure/api/getCurrentEmployee",
							successCallback: {
								fn: function (response) {
									var emplNodeRef = response.json.nodeRef;
									var formNodeRef = "${form.arguments.itemId}";

									if (emplNodeRef != formNodeRef) {
										  hidePanel();
									}
								}
							},
							failureMessage: {
								fn: function () {
									console.log("Failed to load current Employee.");
								}
							}
						});
					}
				}
			},
			failureCallback: {
				fn: function () {
					console.log("Failed to load current Employee.");
				}
			}
		});
    }

    Event.onContentReady(idPanel, init);
})();
//]]></script>
