package ru.it.lecm.documents.scripts;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.repo.jscript.ValueConverter;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.util.ParameterCheck;
import org.json.JSONException;
import org.json.JSONObject;
import org.mozilla.javascript.Scriptable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.documents.beans.DocumentStampService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
        drawStamp(jsonObject, null);
    }

    public void drawStamp(String jsonObject, Scriptable additionalString) throws JSONException {
        JSONObject object = new JSONObject(jsonObject);
        final NodeRef document = new NodeRef(object.getString("document"));
        final NodeRef attach = new NodeRef(object.getString("attach"));
        final NodeRef stamp = new NodeRef(object.getString("stamp"));
        final int x = object.getInt("x");
        final int y = object.getInt("y");
        final int width = object.getInt("width");
        final int height = object.getInt("height");
        final int page = object.getInt("page");

        final List<String> additionalStringList = new ArrayList<>();
        if (additionalString != null) {
            ValueConverter converter = new ValueConverter();
            additionalStringList.addAll((List<String>) converter.convertValueForJava(additionalString));

        }
        final AuthenticationUtil.RunAsWork<ScriptNode> runAsWork = new AuthenticationUtil.RunAsWork<ScriptNode>() {
            @Override
            public ScriptNode doWork() throws Exception {
                documentStampService.drawStamp(document, attach, stamp, x, y, width, height, page, additionalStringList);
                return null;
            }
        };

        AuthenticationUtil.runAsSystem(runAsWork);
    }
    public void clearPreviousStampInfo(ScriptNode document) {
        documentStampService.clearPreviousStampInfo(document.getNodeRef());
    }
}
