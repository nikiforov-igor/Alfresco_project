package ru.it.lecm.im.repo.policy;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies.OnUpdateNodePolicy;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.util.PropertyCheck;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.io.IOException;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Akatamanov
 * Date: 01.02.13
 * Time: 15:08
 * To change this template use File | Settings | File Templates.
 */

/*
    See also http://www.igniterealtime.org/projects/openfire/plugins/userservice/readme.html

    if User is successfully created
        <result>ok</result>

    IllegalArgumentException	one of the parameters passed in to the User Service was bad.
    UserNotFoundException	    No user of the name specified, for a delete or update operation, exists on this server.
    UserAlreadyExistsException	A user with the same name as the user about to be added, already exists.
    RequestNotAuthorised    	The supplied secret does not match the secret specified in the Admin Console or the requester is not a valid IP address.
    UserServiceDisabled	        The User Service is currently set to disabled in the Admin Console.

*/

public class CreateIMUserPolicy implements OnUpdateNodePolicy {

    private final static Logger logger = LoggerFactory.getLogger(CreateIMUserPolicy.class);

    private PolicyComponent policyComponent;    
    private OrgstructureBean orgstructureBean;
    private NodeService nodeService;

    public final void init () {
        PropertyCheck.mandatory(this, "policyComponent", policyComponent);

        policyComponent.bindClassBehaviour (OnUpdateNodePolicy.QNAME, OrgstructureBean.TYPE_EMPLOYEE, new JavaBehaviour(this, "onUpdateNode"));
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

        String url = "http://localhost:9090/plugins/userService/userservice";
        String secret = "qwe123";

        AddOrUpdate(url, secret, "add", name, password, login);
        AddOrUpdate(url, secret, "update", name, password, login);

        boolean active = isActive(nodeRef);

        String action = active? "enable" : "disable";
        logger.trace("User is: " + action);

        DisableOrEnable(url, secret, action, login);

    }

    private Boolean isActive(NodeRef nodeRef) {
        return (Boolean) nodeService.getProperty(nodeRef, BaseBean.IS_ACTIVE);
    }

    private void AddOrUpdate(String url, String secret, String action, String name, String password, String login) {
        logger.trace("trying to create jabber login...");
        String params = String.format("?type=%s&secret=%s&username=%s&password=%s&name=%s", action, secret, login, password, name);

        logger.trace(params);

        HttpClient client = new HttpClient();

        // Create a method instance.
        GetMethod method = new GetMethod(url+params);

        // Provide custom retry handler is necessary
        method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                new DefaultHttpMethodRetryHandler(3, false));

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
    }

    private void DisableOrEnable(String url, String secret, String action, String login) {
        logger.trace("trying to change users state...");

        String params = String.format("?type=%s&secret=%s&username=%s", action, secret, login);

        logger.trace(params);

        HttpClient client = new HttpClient();

        // Create a method instance.
        GetMethod method = new GetMethod(url+params);

        // Provide custom retry handler is necessary
        method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                new DefaultHttpMethodRetryHandler(3, false));

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
    }

    private String getLogin(NodeRef nodeRef) {
        NodeRef personRef = orgstructureBean.getPersonForEmployee(nodeRef);
        Serializable userName = nodeService.getProperty(personRef, ContentModel.PROP_USERNAME);
        return userName.toString();
    }

    private String getPassword(NodeRef nodeRef) {
        return getLogin(nodeRef);
    }

    private String getName(NodeRef nodeRef) {
        NodeRef personRef = orgstructureBean.getPersonForEmployee(nodeRef);
        Serializable firstName = nodeService.getProperty(personRef, ContentModel.PROP_FIRSTNAME);
        Serializable lastName = nodeService.getProperty(personRef, ContentModel.PROP_LASTNAME);
        return firstName.toString() + "%20" + lastName.toString();
    }
    
    private boolean hasPerson (NodeRef nodeRef) {
        return orgstructureBean.getPersonForEmployee(nodeRef) != null;
    }
}
