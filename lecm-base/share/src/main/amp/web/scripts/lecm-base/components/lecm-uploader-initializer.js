if (typeof LogicECM == "undefined" || !LogicECM) {
    var LogicECM = {};
}
(function () {
	LogicECM.LecmUploaderInitializer = {
		initLecmUploaders: function() {
			var uploadersContainer = Dom.get("lecm-controls-file-uploaders");
			if (uploadersContainer == null) {
                uploadersContainer = document.createElement("div");
                uploadersContainer.id = "lecm-controls-file-uploaders";
                document.body.appendChild(uploadersContainer);

                this._initLecmUploader("components/upload/html-upload", "lecm-controls-html-uploader", uploadersContainer, "html");
                this._initLecmUploader("components/upload/flash-upload", "lecm-controls-flash-uploader", uploadersContainer, "flash");
                this._initLecmUploader("components/upload/file-upload", "lecm-controls-file-uploader", uploadersContainer, "file");
                this._initLecmUploader("components/upload/dnd-upload", "lecm-controls-dnd-uploader", uploadersContainer, "dnd");
            }
		},
	
		_initLecmUploader: function(url, containerId, uploadersContainer, type) {
			if (Dom.get(containerId) == null) {
				var container = document.createElement("div");
				container.id = containerId;
				document.body.appendChild(container);
			}
			Alfresco.util.Ajax.request(
				{
					url: Alfresco.constants.URL_SERVICECONTEXT + url,
					dataObj: {
						htmlid: containerId
					},
					successCallback: {
						fn:function(response){
							YAHOO.util.Dom.get(containerId).innerHTML = response.serverResponse.responseText;
                            switch (type) {
                                case "html" :
                                    if (Alfresco.HtmlUpload) {
                                        window.htmlUpload = window.htmlUpload || new Alfresco.HtmlUpload('lecm-controls-html-uploader').setMessages({"header.singleUpload": "Загрузить вложение"});
                                    } else {
                                        LogicECM.module.Base.Util.loadScripts([
                                            'components/upload/html-upload.js'],
                                            function() {
                                                window.htmlUpload = window.htmlUpload || new Alfresco.HtmlUpload('lecm-controls-html-uploader').setMessages({"header.singleUpload": "Загрузить вложение"});
                                            });
                                    }
                                    break;
                                case "flash" :
                                    if (Alfresco.FlashUpload) {
                                        window.flashUpload = window.flashUpload || new Alfresco.FlashUpload('lecm-controls-flash-uploader').setMessages({"header.singleUpload": "Загрузить вложение"});
                                    } else {
                                        LogicECM.module.Base.Util.loadScripts([
                                            'components/upload/flash-upload.js'],
                                            function() {
                                                window.flashUpload = window.flashUpload || new Alfresco.FlashUpload('lecm-controls-flash-uploader').setMessages({"header.singleUpload": "Загрузить вложение"});
                                            });
                                    }
                                    break;
                                case "file" :
                                    if (Alfresco.FileUpload) {
                                        window.fileUpload = window.fileUpload || new Alfresco.FileUpload('lecm-controls-file-uploader').setMessages({"header.singleUpload": "Загрузить вложение"});
                                    } else {
                                        LogicECM.module.Base.Util.loadScripts([
                                            'components/upload/file-upload.js'],
                                            function() {
                                                window.fileUpload = window.fileUpload || new Alfresco.FileUpload('lecm-controls-file-uploader').setMessages({"header.singleUpload": "Загрузить вложение"});
                                            });
                                    }
                                    break;
                                case "dnd" :
                                    if (Alfresco.DNDUpload) {
                                        window.dndUpload = window.dndUpload || new Alfresco.DNDUpload('lecm-controls-dnd-uploader').setMessages({"header.singleUpload": "Загрузить вложение"});
                                    } else {
                                        LogicECM.module.Base.Util.loadScripts([
                                            'components/upload/dnd-upload.js'],
                                            function() {
                                                window.dndUpload = window.dndUpload || new Alfresco.DNDUpload('lecm-controls-dnd-uploader').setMessages({"header.singleUpload": "Загрузить вложение"});
                                            });
                                    }
                                    break;
                            }
						},
						scope: this
					},
					failureMessage: "Load uploader failure",
					scope: this,
					execScripts: true
				});
		},
	
		initLecmDndUploader: function() {
			var uploaderContainerId = "lecm-controls-dnd-uploader";
			var dndUploaderContainer = YAHOO.util.Dom.get(uploaderContainerId);
			if (dndUploaderContainer == null) {
				dndUploaderContainer = document.createElement("div");
				dndUploaderContainer.id = uploaderContainerId;
				document.body.appendChild(dndUploaderContainer);
	
				Alfresco.util.Ajax.request(
					{
						url: Alfresco.constants.URL_SERVICECONTEXT + "components/upload/dnd-upload",
						dataObj: {
							htmlid: uploaderContainerId
						},
						successCallback: {
							fn:function(response){
								dndUploaderContainer.innerHTML = response.serverResponse.responseText;
							},
							scope: this
						},
						failureMessage: "Load uploader failure",
						scope: this,
						execScripts: true
					});
			}
		}
	};
})();