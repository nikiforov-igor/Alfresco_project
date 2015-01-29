<#macro viewForm formId="view-node-form" useDefaultForms=false>
<script type="text/javascript">//<![CDATA[

    if (typeof LogicECM == "undefined" || !LogicECM) {
        LogicECM = {};
    }

    LogicECM.CurrentModules = LogicECM.CurrentModules || {};
    LogicECM.CurrentModules.ViewFormModule = LogicECM.CurrentModules.ViewFormModule || {};

    LogicECM.CurrentModules.ViewFormModule[ "${formId}" ] = LogicECM.CurrentModules.ViewFormModule[ "${formId}" ] || (function() {

        var viewDialog = null;

        function _createFormMarkup() {
            var formContainer = document.createElement( "div" ),
                formContainerHead = document.createElement( "div" ),
                formContainerHeadText = document.createTextNode( "${msg("logicecm.view")}" ),
                formContainerBody = document.createElement( "div" ),
                formContainerBodyContent = document.createElement( "div" ),
                formContainerBodyBdft = document.createElement( "div" ),
                formContainerBodyBdftCancel = document.createElement( "span" ),
                formContainerBodyBdftCancelFirst = document.createElement( "span" ),
                formContainerBodyBdftCancelFirstButton = document.createElement( "button" ),
                formContainerBodyBdftCancelFirstButtonText = document.createTextNode( "${msg("button.close")}" );

            formContainer.id = "${formId}";
            formContainer.className = "yui-panel";
            formContainer.style.display = "none";

            // <div id="${formId}-head" class="hd">
            formContainerHead.id = "${formId}-head";
            formContainerHead.className = "hd";

            // <div id="${formId}-body" class="bd">
            formContainerBody.id = "${formId}-body";
            formContainerBody.className = "bd";

            // <div id="${formId}-content">
            formContainerBodyContent.id = "${formId}-content";

            // <div class="bdft">
            formContainerBodyBdft.className = "bdft";

            // <span id="${formId}-cancel" class="yui-button yui-push-button">
            formContainerBodyBdftCancel.id = "${formId}-cancel";
            formContainerBodyBdftCancel.className = "yui-button yui-push-button";

            // <span class="first-child">
            formContainerBodyBdftCancelFirst.className = "first-child";

            // <button id="${formId}-cancel-button" type="button" tabindex="0">
            formContainerBodyBdftCancelFirstButton.id = "${formId}-cancel-button";
            formContainerBodyBdftCancelFirstButton.type = "button";
            formContainerBodyBdftCancelFirstButton.tabIndex = "0";

            // Формируем основную структуру.
            formContainer.appendChild( formContainerHead );
            formContainer.appendChild( formContainerBody );
            formContainerBody.appendChild( formContainerBodyContent );
            formContainerBody.appendChild( formContainerBodyBdft );
            formContainerBodyBdft.appendChild( formContainerBodyBdftCancel );
            formContainerBodyBdftCancel.appendChild( formContainerBodyBdftCancelFirst );
            formContainerBodyBdftCancelFirst.appendChild( formContainerBodyBdftCancelFirstButton );

            // Добавляем текст.
            formContainerHead.appendChild( formContainerHeadText );
            formContainerBodyBdftCancelFirstButton.appendChild( formContainerBodyBdftCancelFirstButtonText );

            document.body.appendChild( formContainer );
        }

        function addNewDialog() {

            _createFormMarkup();

            viewDialog = Alfresco.util.createYUIPanel( "${formId}", { width: "50em" } );

            YAHOO.util.Event.addListener( "${formId}-cancel-button", "click", function() {
                viewDialog.hide();

                // charnog
                // Uncaught TypeError: Cannot call method 'fire' of null происходит из-за того, что viewDialog.destroy(),
                // который ниже, выполняется "чуть-чуть" быстрее, чем надо. Другие "удобные" события не нашёл. На
                // функционал это не влияет, но исправить всё же стоит. U R Welcome!

                // container-debug.js --- setProperty:265
            });

            viewDialog.subscribe( "hide", function( event, args ) { // destroyOnHide: true
                viewDialog.destroy();
            });
        }

        function viewAttributes( nodeRef, setId, title ) {

            function onSuccessAjaxRequest( response ) {

                var titleElem = YAHOO.util.Dom.get( "${formId}-head" );

                YAHOO.util.Dom.get( "${formId}-content" ).innerHTML = response.serverResponse.responseText;
                YAHOO.util.Dom.setStyle( "${formId}", "display", "block" );

                if( title ) { // Если что-то пришло в title...
                    if( typeof Alfresco.messages.global[ title ] === "undefined" ) { // И оказалось, что пришла простая строка...
                        titleElem.innerHTML = title;
                    } else { // И оказалось, что пришёл ID сообщения...
                        titleElem.innerHTML = Alfresco.messages.global[ title ];
                    }
                } else { // Если же ничего не пришло...
                    titleElem.innerHTML = "${msg( "logicecm.view" )}";
                }

                viewDialog.show();
            }

            LogicECM.CurrentModules.ViewFormModule[ "${formId}" ].add();
            Alfresco.util.Ajax.request({
                url: Alfresco.constants.URL_SERVICECONTEXT + "components/form",

                dataObj: {
                    htmlid: nodeRef.replace("workspace://SpacesStore/","").replace("-",""),
                    itemKind: "node",
                    itemId: nodeRef,
                <#if !useDefaultForms>
                    formId: "${formId}",
                </#if>
                    mode: "view",
                    setId: setId || "common"
                },

                successCallback: {
                    fn: onSuccessAjaxRequest
                },

                failureMessage: "${msg("message.load-parent-form.failure")}",

                execScripts: true
            });

            return false;
        }

        return {
            add: addNewDialog,
            view: viewAttributes,
            dialog: function() { return viewDialog; }
        }

    })();
//]]>
</script>
</#macro>