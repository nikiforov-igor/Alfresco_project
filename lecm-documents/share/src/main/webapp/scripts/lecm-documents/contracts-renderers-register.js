YAHOO.util.Event.onContentReady("alf-hd", function () {
    var $html = Alfresco.util.encodeHTML,
        $links = Alfresco.util.activateLinks;

    if (Alfresco.DocumentList) {
        YAHOO.Bubbling.fire("registerRenderer",
            {
                propertyName: "list-present",
                renderer: function list_present_string_renderer(record, label) {
                    var jsNode = record.jsNode,
                        properties = jsNode.properties,
                        id = Alfresco.util.generateDomId(),
                        html = '<span id="' + id + '" class="faded">' + label + this.msg("details.description.none") + '</span>';

                    if (properties["lecm-document:list-present-string"] && properties["lecm-document:list-present-string"] !== "") {
                        html = '<span id="' + id + '" class="item">' + properties["lecm-document:list-present-string"] + '</span>';
                    }

                    return html;
                }
            });

        /*YAHOO.Bubbling.fire("registerRenderer",
            {
                propertyName: "lecm-tags",
                renderer: function(record, label)
                {
                    var jsNode = record.jsNode,
                        properties = jsNode.properties,
                        id = Alfresco.util.generateDomId(),
                        html = "";

                    var tags = jsNode.tags, tag;
                    if (jsNode.hasAspect("cm:taggable") && tags.length > 0)
                    {
                        for (var i = 0, j = tags.length; i < j; i++)
                        {
                            tag = $html(tags[i]);
                            html += '<span class="tag"><a href="' + Alfresco.constants.URL_PAGECONTEXT + 'documents-journal#filter=tag|' + encodeURIComponent(tag) + '" class="tag-link">' + tag + '</a></span>';
                        }
                    }
                    else
                    {
                        html += '<span class="faded">' + label + this.msg("details.tags.none") + '</span>';
                    }

                    if (jsNode.hasPermission("Write") && !jsNode.isLocked)
                    {
                        // Add the tags insitu editor
                        this.insituEditors.push(
                            {
                                context: id,
                                params:
                                {
                                    type: "tagEditor",
                                    nodeRef: jsNode.nodeRef.toString(),
                                    name: "prop_cm_taggable",
                                    value: record.node.properties["cm:taggable"],
                                    validations: [
                                        {
                                            type: Alfresco.forms.validation.nodeName,
                                            when: "keyup",
                                            message: this.msg("validation-hint.nodeName")
                                        }
                                    ],
                                    title: this.msg("tip.insitu-tag"),
                                    errorMessage: this.msg("message.insitu-edit.tag.failure")
                                },
                                callback:
                                {
                                    fn: this._insituCallback,
                                    scope: this,
                                    obj: record
                                }
                            });
                    }

                    return '<span id="' + id + '" class="item">' + label + html + '</span>';
                }
            });*/
    }
});
