if (typeof LogicECM == 'undefined' || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.Singing = LogicECM.module.Singing || {};

(function () {

    var useNPAPI;

    var Dom = YAHOO.util.Dom,
        Bubbling = YAHOO.Bubbling;
		LogicECM.module.Singing.CadesModule = function LogicECM_module_CadesModule(fieldHtmlId) {
        LogicECM.module.Singing.CadesModule.superclass.constructor.call(this, "LogicECM.module.Singing.CadesModule", fieldHtmlId);
        return this;
    };

    YAHOO.extend(LogicECM.module.Singing.CadesModule, Alfresco.component.Base, {
        options: {
            itemId: null,
            catDoc: null,
            catAttachment: null,
			certificateSelectorId: null
        },

        trs: [],

        panel: null,

        selectedtr: null,

        certificates: null,

        certificateTemplate: null,

        certificatesTable: null,

        cadesOk: null,

        documentNodeRef: null,

		signedhashes: [],

        docs: [],

        onReady: function () {
            this.initPanel();
            this.getHtmlElements();
            var js = [];
            useNPAPI = !!!window.Promise || !!!cadesplugin.CreateObjectAsync ? true : false;
            js.push(useNPAPI ? 'scripts/signed-docflow/Code.js' : 'scripts/signed-docflow/async_code.js');
            this.loadJs(js);
        },

        getHtmlElements: function () {
			this.certificateTemplate = Dom.get(this.id + '-certificate-row-template').innerHTML;
			this.certificatesTable = Dom.get(this.id + '-certificates-table');
		},

        loadJs: function (js) {
            LogicECM.module.Base.Util.loadScripts(js, this.about.bind(this));

        },

		about: function() {
			if (useNPAPI) {
                AboutNPAPI(this);
            } else {
				AboutES6(this);
            }
		},

        aboutCallBack: function (ok) {
            this.cadesOk = ok;
            Bubbling.fire('cadesModuleReady', this);
			if (this.cadesOk)
				this.loadCertificates();
        },

        loadCertificates: function () {
            if (!useNPAPI) {
                GetES6CertsJson(this);
            }
            else {
                window.addEventListener("message", function (event) {
                    if (event.data === "cadesplugin_loaded") {
                        useNPAPI = true;
                    }
                },
                false);
                window.postMessage("cadesplugin_echo_request", "*");
                FillCertList_NPAPIJson(this);
            }
        },

        initPanel: function () {
            this.panel = new Alfresco.util.createYUIPanel(this.id + "-certificate-form", {
				modal: true,
				draggable: true,
				fixedcenter: true,
				visible: false});
			this.panel.showPanel = function () {
				YAHOO.util.Dom.removeClass(this.id, 'hidden');
				this.show();
			};
			this.panel.hidePanel = function () {
				YAHOO.util.Dom.addClass(this.id, 'hidden');
				this.hide();
			};
			this.panel.cancelBtn = Alfresco.util.createYUIButton(this, '-certificate-form-cancel', this.panel.hidePanel.bind(this.panel), {}, Dom.get(this.id + '-certificate-form-cancel'));
        },

        onShowCertificate: function(layer, obj) {
            var thumbprint = obj[1].thumbprint;
            this.certificates.forEach(function(certificate) {
                if (thumbprint && certificate.thumbprint && certificate.thumbprint === thumbprint) {
                    this.fillCertForm(certificate);
                    this.panel.showPanel();
                }
            }, this);
        },

        fillCertForm: function(certificate) {
            Dom.get(this.id + '-certificate-form-head').innerHTML = "Сертификат: " + certificate.shortsubject;
            Dom.get(this.id + '-shortissuer').innerHTML = certificate.shortissuer;
            Dom.get(this.id + '-shortsubject').innerHTML = certificate.shortsubject;
            Dom.get(this.id + '-validFrom').innerHTML = certificate.validFrom;
            Dom.get(this.id + '-validTo').innerHTML = certificate.validTo;
            Dom.get(this.id + '-version').innerHTML = "V" + certificate.version;
            Dom.get(this.id + '-thumbprint').innerHTML = certificate.thumbprint;
            Dom.get(this.id + '-serialNumber').innerHTML = certificate.serialNumber;
            Dom.get(this.id + '-hasPrivateKey').innerHTML = certificate.hasPrivateKey ? "Есть" : "Отсутствует";
            Dom.get(this.id + '-containerName').innerHTML = certificate.containerName;
            Dom.get(this.id + '-providerName').innerHTML = certificate.providerName;
            Dom.get(this.id + '-isValid').innerHTML = certificate.isValid ? "Да" : "Нет";
            Dom.get(this.id + '-issuer').innerHTML = certificate.issuer;
            Dom.get(this.id + '-subject').innerHTML = certificate.subject;
        },

        onSelectCertificate: function(layer, obj) {
            var trid = obj[1].selectedid;
            this.selectedtr = YAHOO.util.Dom.get(trid);
            this.trs.forEach(function(tr) {
                if (tr !== trid) {
                    YAHOO.util.Dom.removeClass(tr, "selected");
                }
            });
            YAHOO.util.Dom.addClass(this.selectedtr, "selected");

            var forms = Alfresco.util.ComponentManager.find({
                name: 'Alfresco.FormUI'
            });

            var signingForms = forms.filter(function (form) {
                var hasBpmComment = form.options.fields && form.options.fields.some(function (field) {
                    return field.id == 'prop_bpm_comment';
                });
                return hasBpmComment ? form : null;
            });

            if (signingForms && signingForms.length) {
                signingForms[0].buttons.submit.set("disabled", false);
            }
        },

        loadCertificatesCallBack: function (result) {
            this.certificates = result;
			var certificatesHTML = '<tbody class="table-body">';

            if (!this.certificates || this.certificates.length === 0) {
                this.certificatesTable.innerHTML = '<tr><td><div id="${fieldHtmlId}-empty-placeholder" class="empty-placeholder">Валидные сертификаты не найдены.</div></td></tr>';
                return;
            }

            this.trs = [];
            YAHOO.Bubbling.subscribe("showCertificate", this.onShowCertificate, this);
            YAHOO.Bubbling.subscribe("selectCertificate", this.onSelectCertificate, this);
            var trind = 0;
			this.certificates.forEach(function(certificate) {
				var id = Alfresco.util.generateDomId();
                var trid = Alfresco.util.generateDomId();
                this.trs.push(trid);
				certificatesHTML += YAHOO.lang.substitute(this.certificateTemplate, YAHOO.lang.merge(YAHOO.lang.merge(certificate, {id:id}), {trid:trid}));
				YAHOO.util.Event.onAvailable(id, function () {
					YAHOO.util.Event.addListener(this, "click", function () {
						YAHOO.Bubbling.fire('showCertificate', {
							thumbprint: certificate.thumbprint
						});
					}, null, this);
				});
                YAHOO.util.Event.onAvailable(trid, function () {
					YAHOO.util.Event.addListener(this, "click", function () {
						YAHOO.Bubbling.fire('selectCertificate', {
							selectedid: trid
						});
					}, null, this);
				});
                trind++;
			}, this);

			this.certificatesTable.innerHTML = certificatesHTML + '</tbody>';
        },

		promises: [],

        getSignature: function (signCallBack) {
            var thumbprint = YAHOO.util.Dom.getAttribute(this.selectedtr, 'cert-id');

            if (!useNPAPI) {
                return SignES6Hashes(thumbprint, this.docs, signCallBack);
            }
            else {
                return SignHashes_NPAPI(thumbprint, this.docs, signCallBack);
            }
        }
    });
})();