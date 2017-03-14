<!-- Parameters and libs -->
<#assign el=args.htmlid/>
<#if record?? && tags??>
<!-- Markup -->
<div class="widget-bordered-panel tags-panel">
    <div id="${el}-wide-view" class="document-metadata-header document-components-panel document-tags">
        <h2 id="${el}-heading" class="dark">
        ${msg("document.tags")}
            <span class="alfresco-twister-actions">
			<a id="${el}-action-cloud" href="javascript:void(0);" class="expand"
               title="${msg("label.expand")}">&nbsp</a>
        </span>
        </h2>

        <div class="panel-body">
            <span id="${el}-tageditor" class="item"></span>
        </div>
    </div>
    <div id="${el}-short-view" class="document-components-panel document-tags short-view hidden">
        <div id="${el}-formContainer" class="right-block-content">
        <span class="yui-button yui-push-button">
           <span class="first-child">
              <button type="button" title="${msg('document.tags')}"></button>
           </span>
        </span>
        </div>
    </div>
</div>
<script type="text/javascript">//<![CDATA[
(function () {
    function init() {
        LogicECM.module.Base.Util.loadResources([
            'components/document-details/document-metadata.js'
        ], [
            'css/components/document-tags.css',
            'components/document-details/document-metadata.css'
        ], create);
    }

    function create() {
        Alfresco.util.createTwister("${el}-heading", "DocumentTags");

// Tags editor:
        var tageditor;
        var nodeRef = "${nodeRef?js_string}";
        var record = ${record};
        record.jsNode = new Alfresco.util.Node(record.node);

//Построение списка тэгов на странице
        var renderTags = function (tagsList) {
            var tagEditor = YAHOO.util.Dom.get("${el}-tageditor");

            tagEditor.innerHTML = '';
            if (tagsList && tagsList.length > 0) {
                for (var i = 0; i < tagsList.length; i++) {
                    var tag = tagsList[i].name || tagsList[i];

                    tagEditor.innerHTML += '<span class="tag"><a href="' + Alfresco.constants.URL_PAGECONTEXT + 'documents-journal#filter=tag|' + encodeURIComponent(tag) + '" class="tag-link">' + tag + '</a></span>';
                }
            } else {
                tagEditor.innerHTML = '<span class="faded">${msg("label.none")}</span>';
            }
        };
        /**
         * Tags Editor callback function
         *
         * @method tagsEditCallback
         * @param response {object} AJAX response
         * @param record {YAHOO.widget.Record} Record for the item being edited
         */
        var tagsEditCallback = function (response, record) {
            // Reload the node's metadata
            var jsNode = record.jsNode;
            var nodeRef = jsNode.nodeRef;

            Alfresco.util.Ajax.request({
                url: Alfresco.util.combinePaths(Alfresco.constants.URL_SERVICECONTEXT, "components/documentlibrary/data/node/", nodeRef.uri),
                successCallback: {
                    fn: function refreshSuccess(response) {
                        var record = response.json.item;
                        var taggable = record.node.properties["cm:taggable"];

                        record.jsNode = new Alfresco.util.Node(record.node);
                        //Заново собрать список тэгов для показа на странице (обновить)
                        renderTags(taggable || []);
                        //Скрыть форму редактирования тэгов, показать просто список
                        tageditor.doHide(true);
                        //Обновить список тэгов в форме редактирования
                        tageditor.params.value = taggable;
                        tageditor._generateCurrentTagMarkup();

                        // Fire "renamed" event
                        YAHOO.Bubbling.fire(record.node.isContainer ? "folderRenamed" : "fileRenamed",
                                {
                                    file: record
                                });

                        // Fire "tagRefresh" event
                        YAHOO.Bubbling.fire("tagRefresh");

                        // Prevent hide call which briefly shows stale data
                        return false;
                    },
                    scope: this
                },
                failureCallback: {
                    fn: function refreshFailure(response) {
                    },
                    scope: this
                }
            });
        };

        renderTags(${tags});
        if (${mayEdit}) {
            tageditor = Alfresco.util.createInsituEditor("${el}-tageditor",
                    {
                        type: "tagEditor",
                        nodeRef: nodeRef,
                        name: "prop_cm_taggable",
                        value: record.node.properties["cm:taggable"], // here go the tags of the current node
                        validations: [{
                            type: Alfresco.forms.validation.nodeName,
                            when: "keyup",
                            message: '${msg("validation-hint.tagName")}'
                        }],
                        title: '${msg("document.tip.insitu-tag")}',
                        errorMessage: '${msg("document.insitu-edit.tag.failure")}'
                    },
                    {
                        fn: tagsEditCallback,
                        scope: this,
                        obj: record
                    });
        }

        var semanticEl = YAHOO.util.Dom.get("semantic-mudule-active-htmlid");
        if (!semanticEl) {
            var tagAction = YAHOO.util.Dom.get("${el}-action-cloud");
            if (tagAction) {
                YAHOO.util.Dom.addClass(tagAction, 'hidden');
            }

        }
        else {
            documentTagsComponent = new LogicECM.DocumentTags("${el}").setOptions(
                    {
                        nodeRef: "${nodeRef}",
                        title: "${msg('label.terms.document.cloud')}",
                        showAfterReady: ${(view?? && view == "tags")?string}
                    }).setMessages(${messages});
        }
    }

    YAHOO.util.Event.onDOMReady(init);

    LogicECM.services = LogicECM.services || {};
    if (LogicECM.services.documentViewPreferences) {
        var shortView = LogicECM.services.documentViewPreferences.getShowRightPartShort();
        if (shortView) {
            Dom.addClass("${el}-wide-view", "hidden");
            Dom.removeClass("${el}-short-view", "hidden");
        }
        YAHOO.Bubbling.on("showRightPartShortChanged", function () {
            var rightPartWide = Dom.get("${el}-wide-view");
            var rightPartShort = Dom.get("${el}-short-view");
            var shortView = LogicECM.services.documentViewPreferences.getShowRightPartShort();
            if (shortView) {
                Dom.addClass(rightPartWide, "hidden");
                Dom.removeClass(rightPartShort, "hidden");
            } else {
                Dom.addClass(rightPartShort, "hidden");
                Dom.removeClass(rightPartWide, "hidden");
            }
        });
    }
})();
//]]></script>
</#if>