package ru.it.lecm.documents.scripts;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.dictionary.ConstraintDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.ParameterCheck;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.base.beans.LecmTransactionHelper;
import ru.it.lecm.documents.beans.*;
import ru.it.lecm.documents.constraints.ArmUrlConstraint;

import java.io.Serializable;
import java.util.*;

/**
 * User: PMelnikov
 * Date: 01.07.15
 */
public class DocumentStampWebScriptBean extends BaseWebScript {
    private static final Logger logger = LoggerFactory.getLogger(DocumentStampWebScriptBean.class);

    private DocumentStampService documentStampService;

    public void setDocumentStampService(DocumentStampService documentStampService) {
        this.documentStampService = documentStampService;
    }

    /**
     * Получить параметры штампа для документа
     *
     * @param document документ
     * @param code код штампа
     */
    public HashMap<String, Object> getStamp(ScriptNode document, String code) {
        ParameterCheck.mandatory("document", document);
        ParameterCheck.mandatory("code", code);
        return documentStampService.getStamp(document.getNodeRef(), code);
    }

    /**
     * Печать штампа на документе
     *
     * @param document Документ
     * @param attach Вложение на котором будет печататься штамп
     * @param stamp штамп
     * @param x координата штампа
     * @param y координат штампа
     * @param width ширина страницы
     * @param height высота страницы
     */
    public void drawStamp(ScriptNode document, ScriptNode attach, ScriptNode stamp, int x, int y, int width, int height, int page) {
        documentStampService.drawStamp(document.getNodeRef(), attach.getNodeRef(), stamp.getNodeRef(), x, y, width, height, page);
    }

    public void drawStamp(String jsonObject) throws JSONException {
        JSONObject object = new JSONObject(jsonObject);
        NodeRef document = new NodeRef(object.getString("document"));
        NodeRef attach = new NodeRef(object.getString("attach"));
        NodeRef stamp = new NodeRef(object.getString("stamp"));
        int x = object.getInt("x");
        int y = object.getInt("y");
        int width = object.getInt("width");
        int height = object.getInt("height");
        int page = object.getInt("page");
        documentStampService.drawStamp(document, attach, stamp, x, y, width, height, page);
    }


}
