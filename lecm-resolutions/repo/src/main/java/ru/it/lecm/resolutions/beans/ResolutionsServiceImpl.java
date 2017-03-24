package ru.it.lecm.resolutions.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ISO8601DateFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.WriteTransactionNeededException;
import ru.it.lecm.eds.api.EDSDocumentService;
import ru.it.lecm.errands.ErrandsService;
import ru.it.lecm.resolutions.api.ResolutionsService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: AIvkin
 * Date: 18.11.2016
 * Time: 14:41
 */
public class ResolutionsServiceImpl extends BaseBean implements ResolutionsService {
    private final static Logger logger = LoggerFactory.getLogger(ResolutionsServiceImpl.class);

    private NamespaceService namespaceService;
    private EDSDocumentService edsDocumentService;
    private NodeRef dashletSettingsNode;

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

    public void setEdsDocumentService(EDSDocumentService edsDocumentService) {
        this.edsDocumentService = edsDocumentService;
    }

    @Override
    public NodeRef getServiceRootFolder() {
        return getFolder(RESOLUTIONS_ROOT_ID);
    }

    @Override
    public boolean checkResolutionErrandsExecutionDate(NodeRef resolution) {
        if (nodeService.exists(resolution)) {
            String errandsJsonStr = (String) nodeService.getProperty(resolution, PROP_ERRANDS_JSON);
            if (errandsJsonStr != null) {
                try {
                    Date resolutionLimitationDate = edsDocumentService.convertComplexDate(
                            (String) nodeService.getProperty(resolution, PROP_LIMITATION_DATE_RADIO),
                            (Date) nodeService.getProperty(resolution, PROP_LIMITATION_DATE),
                            (String) nodeService.getProperty(resolution, PROP_LIMITATION_DATE_TYPE),
                            (Integer) nodeService.getProperty(resolution, PROP_LIMITATION_DATE_DAYS));

                    if (resolutionLimitationDate != null) {
                        JSONArray errandsJsonArray = new JSONArray(errandsJsonStr);
                        for (int i = 0; i < errandsJsonArray.length(); i++) {
                            JSONObject errandJson = errandsJsonArray.getJSONObject(i);

                            String errandLimitationDateRadio = getPropFromJson(errandJson, ErrandsService.PROP_ERRANDS_LIMITATION_DATE_RADIO);
                            String errandLimitationDateDays = getPropFromJson(errandJson, ErrandsService.PROP_ERRANDS_LIMITATION_DATE_DAYS);
                            String errandLimitationDateType = getPropFromJson(errandJson, ErrandsService.PROP_ERRANDS_LIMITATION_DATE_TYPE);
                            String errandLimitationDateDate = getPropFromJson(errandJson, ErrandsService.PROP_ERRANDS_LIMITATION_DATE);

                            Date errandLimitationDate = edsDocumentService.convertComplexDate(errandLimitationDateRadio,
                                    (errandLimitationDateDate != null && !errandLimitationDateDate.isEmpty()) ? ISO8601DateFormat.parse(errandLimitationDateDate) : null,
                                    errandLimitationDateType,
                                    (errandLimitationDateDays != null && !errandLimitationDateDays.isEmpty()) ? Integer.parseInt(errandLimitationDateDays) : null);

                            if (errandLimitationDate != null && errandLimitationDate.after(resolutionLimitationDate)) {
                                return false;
                            }
                        }
                    }
                } catch (JSONException e) {
                    logger.error("Error parse resolution errands json");
                }
            }
        }

        return true;
    }

    @Override
    public List<NodeRef> getResolutionClosers(NodeRef resolution) {
        List<NodeRef> result = new ArrayList<>();

        String closerType = (String) nodeService.getProperty(resolution, PROP_CLOSERS);

        if (CLOSERS_AUTHOR.equals(closerType) || CLOSERS_AUTHOR_AND_CONTROLLER.equals(closerType)) {
            result.add(findNodeByAssociationRef(resolution, ASSOC_AUTHOR, null, ASSOCIATION_TYPE.TARGET));
        }

        if (CLOSERS_CONTROLLER.equals(closerType) || CLOSERS_AUTHOR_AND_CONTROLLER.equals(closerType)) {
            result.add(findNodeByAssociationRef(resolution, ASSOC_CONTROLLER, null, ASSOCIATION_TYPE.TARGET));
        }

        return result;
    }

    @Override
    public void initServiceImpl() {
        if (null == getDashletSettingsNode()) {
            dashletSettingsNode = createDashletSettingsNode();
        }
    }

    @Override
    public NodeRef getDashletSettingsNode() {
        if (dashletSettingsNode == null) {
                dashletSettingsNode = nodeService.getChildByName(this.getServiceRootFolder(), ContentModel.ASSOC_CONTAINS, RESOLUTION_DASHLET_SETTINGS_NODE_NAME);
        }
        return dashletSettingsNode;
    }

    @Override
    public NodeRef createDashletSettingsNode() {
        try {
            return createNode(this.getServiceRootFolder(), TYPE_RESOLUTION_DASHLET_SETTINGS, RESOLUTION_DASHLET_SETTINGS_NODE_NAME , null);
        } catch (WriteTransactionNeededException e) {
            return null;
        }
    }

    @Override
    public void sendAnnulSignal(NodeRef resolution, String reason) {
        nodeService.setProperty(resolution, ResolutionsService.PROP_ANNUL_SIGNAL, true);
        nodeService.setProperty(resolution, ResolutionsService.PROP_ANNUL_SIGNAL_REASON, reason);
    }

    private String getPropFromJson(JSONObject json, QName propQName) throws JSONException {
        String propName = "prop_" + propQName.toPrefixString(namespaceService).replace(":", "_");
        if (json.has(propName)) {
            return json.getString(propName);
        } else {
            return null;
        }
    }
}
