<#assign id = args.htmlid?js_string>
<#assign ownerRef = args.owner?string>
<script type="text/javascript">
    //<![CDATA[
    (function() {
        var Dom = YAHOO.util.Dom,
                Event = YAHOO.util.Event,
                Selector = YAHOO.util.Selector;
        var container;


        function init() {
            Alfresco.util.Ajax.jsonGet({
                url: Alfresco.constants.PROXY_URI + "lecm/orgstructure/api/getEmployeePhoto",
                dataObj: {
                    nodeRef: "${ownerRef}"
                },
                successCallback: {
                    scope: this,
                    fn: function (response) {
                        if (response && response.json) {
                            var imageContainer = YAHOO.util.Dom.get("${id}_owner-foto");
                            var className = "thumbnail-view";
                            if (response.json.nodeRef) {
                                var photoRef = new Alfresco.util.NodeRef(response.json.nodeRef);
                                var imgRef = Alfresco.constants.PROXY_URI + "api/node/" + photoRef.uri + "/content";
                                if (imgRef != "") {
                                    var ref = response.json.nodeRef;
                                    var imageId = ref.slice(ref.lastIndexOf('/') + 1);
                                    imageContainer.innerHTML = '<span class="' + className + '">' + '<a href="' + imgRef + '" target="_blank"><img id="' + imageId + '" src="' + imgRef + '" /></a></span>';
                                } else {
                                    imageContainer.innerHTML = '<span class="' + className + '-text">' + Alfresco.util.message('message.upload.not-loaded') + '</span>';
                                }
                            } else {
                                imageContainer.innerHTML = '<span class="' + className + '-text">' + Alfresco.util.message('message.upload.not-loaded') + '</span>';
                            }
                        }
                    }
                },
                failureCallback: {
                    scope: this,
                    fn: function(){
                        var imageContainer = YAHOO.util.Dom.get("${id}_owner-foto");
                        var className = "thumbnail-view";
                        imageContainer.innerHTML = '<span class="'+ className+'-text">' + Alfresco.util.message('message.upload.not-loaded') + '</span>';
                        Alfresco.util.message('message.failure');
                    }
                }
            });
            Alfresco.util.Ajax.jsonPost({
                url: Alfresco.constants.PROXY_URI + "lecm/substitude/format/node",
                dataObj: {
                    nodeRef: "${ownerRef}",
                    substituteString: "{lecm-orgstr:employee-first-name} {lecm-orgstr:employee-middle-name} {lecm-orgstr:employee-last-name}"
                },
                successCallback: {
                    scope: this,
                    fn: function (response) {
                        if (response && response.json.formatString) {
                            var fio = response.json.formatString;
                            var container = Dom.get("${id}_owner-positions");
                            var p = document.createElement("p");
                            p.classList = "owner-fio";
                            p.innerHTML = fio;
                            container.appendChild(p);
                        }
                    }
                },
                failureMessage: Alfresco.util.message('message.failure')
            });
            Alfresco.util.Ajax.jsonGet({
                url: Alfresco.constants.PROXY_URI + "lecm/orgstructure/api/getEmployeePositions",
                dataObj: {
                    nodeRef: "${ownerRef}"
                },
                successCallback: {
                    scope: this,
                    fn: function (response) {
                        if (response && response.json) {
                            var positions = response.json;
                            positions.forEach(function(position){
                                Alfresco.util.Ajax.jsonPost({
                                    url: Alfresco.constants.PROXY_URI + "lecm/substitude/format/node",
                                    dataObj: {
                                        nodeRef: position.nodeRef,
                                        substituteString: "{../lecm-orgstr:element-full-name}"
                                    },
                                    successCallback: {
                                        scope: this,
                                        fn: function (response) {
                                            if (response && response.json.formatString) {
                                                var department = response.json.formatString;
                                                var container = Dom.get("${id}_owner-positions");
                                                var p = document.createElement("p");
                                                p.classList = "owner-position";
                                                p.innerHTML = "\n" + position.positionName + ",\n" + department;
                                                container.appendChild(p);
                                            }
                                        }
                                    },
                                    failureMessage: Alfresco.util.message('message.failure')
                                });
                            },true);
                        }
                    }
                },
                failureMessage: "${msg("message.setExecutionReport.failure")}"
            });
            Alfresco.util.Ajax.jsonGet({
                url: Alfresco.constants.PROXY_URI + "/lecm/orgstructure/api/getCurrentEmployee",
                successCallback: {
                    scope: this,
                    fn: function (response) {
                        if (response && response.json) {
                            var currentEmployee = response.json;
                            var substituteString = "{lecm-secretary-aspects:secretary-assoc-ref},{..lecm-d8n:delegation-opts-owner-assoc/lecm-d8n:delegation-opts-trustee-assoc(lecm-orgstr:employee-short-name=" + currentEmployee.shortName + ")/lecm-orgstr:employee-short-name},{..lecm-absence:abscent-employee-assoc(lecm-absence:activated=true)/lecm-absence:begin},{..lecm-absence:abscent-employee-assoc(lecm-absence:activated=true)/lecm-absence:end},{..lecm-absence:abscent-employee-assoc(lecm-absence:activated=true)/lecm-absence:unlimited}";
                            Alfresco.util.Ajax.jsonPost({
                                url: Alfresco.constants.PROXY_URI + "lecm/substitude/format/node",
                                dataObj: {
                                    nodeRef: "${ownerRef}",
                                    substituteString: substituteString
                                },
                                successCallback: {
                                    scope: this,
                                    fn: function (response) {
                                        if (response && response.json.formatString) {
                                            var delegations = response.json.formatString.split(",");
                                            var secretary = delegations[0];
                                            var delegation = delegations[1];
                                            var absenceBegin = delegations[2];
                                            var absenceEnd = delegations[3];
                                            var absenceUnlimited = delegations[4] == "true";
                                            var container = Dom.get("${id}_container");

                                            if (secretary && secretary.indexOf(currentEmployee.nodeRef) != -1) {
                                                var p = document.createElement("p");
                                                p.innerHTML = "Секретарь - бессрочный";
                                                container.appendChild(p);
                                            }
                                            if (delegation && absenceBegin) {
                                                var p = document.createElement("p");
                                                p.innerHTML = "Делегат -  c " + absenceBegin.substr(0, absenceBegin.length - 5) + "" + (absenceUnlimited ? "" : " по " + absenceEnd.substr(0, absenceEnd.length - 5));
                                                container.appendChild(p);
                                            }
                                        }
                                    }
                                },
                                failureMessage: Alfresco.util.message('message.failure')
                            });
                        }
                    }
                },
                failureMessage: Alfresco.util.message('message.failure')
            });
        }
        Event.onContentReady("${id}_delegation-content", init, true);
    })();
    //]]>
</script>
<p class="delegation-page-title">
${msg("label.delegation.message.advanced-info")}
</p>
<div id="${id}_delegation-content">
    <div id="main-region" class="yui-g">
        <div id="${id}_owner-content" class="yui-u first delegation-owner-content">
            <div id="${id}_owner-foto" class="owner-foto thumbnail-container">

            </div>
            <div id="${id}_owner-positions" class="owner-employee-positions">

            </div>
        </div>
        <div class="yui-u">
            <div class="delegation-info">
                <div class="dashlet bordered delegation-content">
                    <div class="title dashlet-title">
                        <span>${msg("label.owner-delegation.dashlet.title")}</span>
                    </div>
                    <div class="body dashlet-body" id="${id}_results">
                        <div id="${id}_container"></div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
