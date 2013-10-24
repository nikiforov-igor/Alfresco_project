package ru.it.lecm.im.repo.policy;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.util.PropertyCheck;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.io.IOException;
import java.io.Serializable;
import java.net.URLEncoder;
import java.util.Properties;
import org.apache.commons.httpclient.methods.RequestEntity;
import ru.it.lecm.im.PropertiesBean;

public class CreateProsodyUserPolicy implements NodeServicePolicies.OnUpdateNodePolicy {

    private final static Logger logger = LoggerFactory.getLogger(CreateIMUserPolicy.class);

    private PolicyComponent policyComponent;
    private OrgstructureBean orgstructureBean;
    private NodeService nodeService;
    private PropertiesBean properties;

    public void setProperties(PropertiesBean properties) {
        this.properties = properties;
    }

    public final void init () {
        if(properties.isActive()){
            try
            {
                PropertyCheck.mandatory(this, "policyComponent", policyComponent);
                policyComponent.bindClassBehaviour (NodeServicePolicies.OnUpdateNodePolicy.QNAME, OrgstructureBean.TYPE_EMPLOYEE, new JavaBehaviour(this, "onUpdateNode"));
            }
            catch (Exception e)
            {
                logger.error("CreateProsodyUserPolicy.init error!", e);
            }
        } else {
            logger.info("Messenger disabled");
        }
    }

    public void setPolicyComponent (PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    public void setOrgstructureBean(OrgstructureBean orgstructureBean) {
        this.orgstructureBean = orgstructureBean;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }


    @Override
    public void onUpdateNode(NodeRef nodeRef) {
        try
        {
            if (!hasPerson(nodeRef)) {
                logger.warn ("there is no any person associated with " + nodeService.getProperty(nodeRef, ContentModel.PROP_NAME));
                return;
            }

            String name = this.getName(nodeRef);
            if (name == null || name.length() == 0)
            {
                logger.debug("Name is null or empty");
                return;
            }

            String password = this.getPassword(nodeRef);
            if (password == null || password.length() == 0)
            {
                logger.debug("password is null or empty");
                return;
            }

            String login = this.getLogin(nodeRef);
            if (login == null || login.length() == 0)
            {
                logger.debug("login is null or empty");
                return;
            }

            Add(name, password, login);

        }
        catch (Exception e)
        {
            logger.error("CreateProsodyUserPolicy error!", e);
        }

    }


    private void Add(String name, String password, String login) {
        try
        {
            logger.trace("trying to create jabber login...");

            HttpClient client = new HttpClient();
            client.getParams().setAuthenticationPreemptive(true);
            Credentials defaultcreds = new UsernamePasswordCredentials("admin@localhost", "admin");
            client.getState().setCredentials(new AuthScope("localhost", 5280, AuthScope.ANY_REALM), defaultcreds);

            String url = String.format("http://localhost:5280/data/localhost/%s/accounts", login);

            // Create a method instance.
            PutMethod method = new PutMethod(url);
            method.setRequestHeader("Content-Type", "application/json");

            // Provide custom retry handler is necessary
            final DefaultHttpMethodRetryHandler retryHandler = new DefaultHttpMethodRetryHandler(3, false);
            method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, retryHandler);

            JSONObject json = new JSONObject();
            json.put("password", password);
            json.put("FN", name);
            String requestBody = String.format(json.toString());
//            String requestBody = String.format("{\n" +
//                    "  \"password\": \"%s\",\n" +
//                    "  \"FN\": \"%s\"\n" +
//                    "}",
//                    password,
//                    name
//                    );
            final StringRequestEntity requestEntity = new StringRequestEntity(requestBody, "application/json", "UTF-8" );
            method.setRequestEntity(requestEntity);

            try {
                // Execute the method.
                int statusCode = client.executeMethod(method);

                if (statusCode != HttpStatus.SC_OK) {
                    logger.trace("Method failed: " + method.getStatusLine());
                }

                // Read the response body.
                byte[] responseBody = method.getResponseBody();

                // Deal with the response.
                // Use caution: ensure correct character encoding and is not binary data
                logger.trace(new String(responseBody));

            } catch (HttpException e) {
                logger.error("Fatal protocol violation: ", e);
            } catch (IOException e) {
                logger.error("Fatal transport error: ", e);

            } finally {
                // Release the connection.
                method.releaseConnection();
            }

        }catch (Exception e)
        {
            logger.error("AddOrUpdate error!!!", e);
        }
    }

    private String getLogin(NodeRef nodeRef) {
        try
        {
            NodeRef personRef = orgstructureBean.getPersonForEmployee(nodeRef);
            Serializable userName = nodeService.getProperty(personRef, ContentModel.PROP_USERNAME);
            return URLEncoder.encode(userName.toString(), "UTF-8");
        }
        catch (Exception e)
        {
            logger.error("getLogin error!", e);
            return null;
        }
    }

    private String getPassword(NodeRef nodeRef) {
        return getLogin(nodeRef);
    }

    private String getName(NodeRef nodeRef) {
        try
        {
            NodeRef personRef = orgstructureBean.getPersonForEmployee(nodeRef);
            Serializable firstName = nodeService.getProperty(personRef, ContentModel.PROP_FIRSTNAME);
            Serializable lastName = nodeService.getProperty(personRef, ContentModel.PROP_LASTNAME);

            String result = firstName + " " + lastName;
            logger.trace("Name : "+result);
            return result;
        }
        catch (Exception e)
        {
            logger.error("getName error!", e);
            return null;
        }
    }

    private boolean hasPerson (NodeRef nodeRef) {
        try
        {
            return orgstructureBean.getPersonForEmployee(nodeRef) != null;
        }
        catch (Exception e)
        {
            logger.error("hasPerson error!!!", e);
            return false;
        }
    }


}
