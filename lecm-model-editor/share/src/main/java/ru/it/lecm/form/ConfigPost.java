package ru.it.lecm.form;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.config.ConfigService;
import org.springframework.extensions.surf.util.Content;
import org.springframework.extensions.webscripts.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConfigPost extends DeclarativeWebScript {
	
	private static Log logger = LogFactory.getLog(ConfigPost.class);
	
	protected ConfigService configService;
	
	public void setConfigService(ConfigService configService)
    {
        this.configService = configService;
    }
	
	@Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
		Map<String, Object> model = new HashMap<String, Object>();
		Content c = req.getContent();
        if (c == null)
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Missing POST body.");
        }
        
        JSONObject json;
        try
        {
            json = new JSONObject(c.getContent());
            String name = json.getJSONObject("model").getJSONObject("types").getJSONObject("type").getString("_name");
            JSONArray prop = json.getJSONObject("model").getJSONObject("types").getJSONObject("type").getJSONObject("properties").getJSONArray("property");
            JSONArray assoc = json.getJSONObject("model").getJSONObject("types").getJSONObject("type").getJSONObject("associations").getJSONArray("association");
            StringBuilder s = new StringBuilder("" +
    				"<alfresco-config>" +
    				"	<config evaluator=\"node-type\" condition=\""+name+"\" replace=\"true\">" +
    				"		<forms>" +
    				"			<form>" +
    				"				<field-visibility>");
            	for(int i=0 ; i<prop.length(); i++){
    				s.append("					<show id=\"").append(prop.getJSONObject(i).getString("_name")).append("\"/>");
            	}
            	for(int i=0 ; i<assoc.length(); i++){
    				s.append("					<show id=\"").append(assoc.getJSONObject(i).getString("_name")).append("\"/>");
            	}
    			s.append("				</field-visibility>" + "				<appearance>" + "					<set id=\"contract-info\" appearance=\"\"/>");
                for(int i=0 ; i<prop.length(); i++){
    				s.append("					<field id=\"").append(prop.getJSONObject(i).getString("_name")).append("\" set=\"contract-info\"/>");
                }
                for(int i=0 ; i<assoc.length(); i++){
    				s.append("					<field id=\"").append(assoc.getJSONObject(i).getString("_name")).append("\" set=\"contract-info\"/>");
                }
    			s.append("				</appearance>" + "			</form>" + "		</forms>" + "	</config>" + "	<config evaluator=\"model-type\" condition=\"").append(name).append("\" replace=\"true\">").append("		<forms>").append("			<form>").append("				<field-visibility>");
            	for(int i=0 ; i<prop.length(); i++){
    				s.append("					<show id=\"").append(prop.getJSONObject(i).getString("_name")).append("\"/>");
            	}
            	for(int i=0 ; i<assoc.length(); i++){
    				s.append("					<show id=\"").append(assoc.getJSONObject(i).getString("_name")).append("\"/>");
            	}
    			s.append("				</field-visibility>" + "				<appearance>" + "					<set id=\"contract-info\" appearance=\"\"/>");
                for(int i=0 ; i<prop.length(); i++){
                    	s.append("					<field id=\"").append(prop.getJSONObject(i).getString("_name")).append("\" set=\"contract-info\"/>");
                }
                for(int i=0 ; i<assoc.length(); i++){
    				s.append("					<field id=\"").append(assoc.getJSONObject(i).getString("_name")).append("\" set=\"contract-info\"/>");
                }
    			s.append("				</appearance>" + "			</form>" + "			<form id=\"datagrid\">" + "				<field-visibility>");
            	for(int i=0 ; i<prop.length(); i++){
    				s.append("					<show id=\"").append(prop.getJSONObject(i).getString("_name")).append("\"/>");
            	}
            	for(int i=0 ; i<assoc.length(); i++){
    				s.append("					<show id=\"").append(assoc.getJSONObject(i).getString("_name")).append("\"/>");
            	}
    			s.append("				</field-visibility>" + "				<appearance>");
                for(int i=0 ; i<prop.length(); i++){
                    	s.append("					<field id=\"").append(prop.getJSONObject(i).getString("_name")).append("\" set=\"contract-info\"/>");
                }
                for(int i=0 ; i<assoc.length(); i++){
    				s.append("					<field id=\"").append(assoc.getJSONObject(i).getString("_name")).append("\" set=\"contract-info\"/>");
                }
    			s.append("				</appearance>" + "			</form>" + "		</forms>" + "	</config>" + "</alfresco-config>");
    		
    		
    		
    		StringConfigSource cs = new StringConfigSource(s.toString());
    		
    		this.configService.appendConfig(cs);
    		
//    		JSONArray ja = json.optJSONObject("model").optJSONObject("types").optJSONObject("type").optJSONObject("properties").optJSONArray("property");
//    		for(int i ; i<ja.length(); i++){
//    			ja.getString("_name");
//    		}
    		
    		logger.info(json);
    		
    		model.put("configResult","Ok");
    		model.put("jsonBody",json);
        } 
        catch (JSONException jErr)
        {
        	model.put("configResult","Error");
            throw new WebScriptException(Status.STATUS_BAD_REQUEST,
                    "Unable to parse JSON POST body: " + jErr.getMessage());
        }
        catch (IOException ioErr)
        {
        	model.put("configResult","Error");
            throw new WebScriptException(Status.STATUS_INTERNAL_SERVER_ERROR,
                    "Unable to retrieve POST body: " + ioErr.getMessage());
        }
		
		
		return model;
    }
}