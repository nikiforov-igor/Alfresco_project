<!-- Parameters and libs -->
<#assign el=args.htmlid/>
<#if record?? && tags??>
<!-- Markup -->
<div class="widget-bordered-panel">
<div class="document-metadata-header document-components-panel document-tags">
    <h2 id="${el}-heading" class="thin dark">
        ${msg("document.tags")}
    </h2>

    <div class="panel-body">
        <span id="${el}-tageditor" class="item"></span>
    </div>

    <script type="text/javascript">//<![CDATA[
    Alfresco.util.createTwister("${el}-heading", "DocumentTags");

    // Tags editor:
    var tageditor;
    var nodeRef = "${nodeRef?js_string}";
    var record = ${record};
    record.jsNode = new Alfresco.util.Node(record.node);

    //Построение списка тэгов на странице
    var renderTags = function(tagsList) {
        var tagEditor = YAHOO.util.Dom.get("${el}-tageditor");

        tagEditor.innerHTML = '';
        if (tagsList && tagsList.length > 0) {
            for (var i = 0; i < tagsList.length; i++) {
                var tag = tagsList[i].name || tagsList[i];

                tagEditor.innerHTML += '<span class="tag"><a href="' + Alfresco.constants.URL_PAGECONTEXT + 'repository#filter=tag|' + encodeURIComponent(tag) + '&page=1" class="tag-link">' + tag + '</a></span>';
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
    var tagsEditCallback = function(response, record) {
        // Reload the node's metadata
        var jsNode = record.jsNode;
        var nodeRef = jsNode.nodeRef;

        Alfresco.util.Ajax.request({
            url: Alfresco.util.combinePaths(Alfresco.constants.URL_SERVICECONTEXT, "lecm/document/data/node/", nodeRef.uri),
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
                fn: function refreshFailure(response) {},
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
    //]]></script>
</div>
</div>
</#if>