package ru.it.lecm.notifications.email.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.action.executer.MailActionExecuter;
import org.alfresco.repo.admin.SysAdminParams;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.template.DateCompareMethod;
import org.alfresco.repo.template.HasAspectMethod;
import org.alfresco.repo.template.I18NMessageMethod;
import org.alfresco.repo.template.TemplateNode;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.AuthorityType;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.util.Pair;
import org.alfresco.util.UrlUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.extensions.surf.util.I18NUtil;
import org.springframework.mail.MailPreparationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * Created by dkuchurkin on 07.06.2016.
 */
public class MailActionExecuterWithAttachment extends MailActionExecuter {

    public static final String NAME = "mailWithAttachment";
    public static final String ATTACHMENTS = "attachments";
    private static final String FROM_ADDRESS = "alfresco@alfresco.org";

    private PersonService personService;
    private NodeService nodeService;
    private JavaMailSender mailService;
    private AuthenticationService authService;
    private AuthorityService authorityService;
    private TemplateService templateService;
    private ServiceRegistry serviceRegistry;
    private SysAdminParams sysAdminParams;
    private TemplateImageResolver imageResolver;
    private ContentService contentService;
    private SearchService searchService;
    private NamespaceService namespaceService;

    private static Log logger = LogFactory.getLog(MailActionExecuterWithAttachment.class);
    private boolean validateAddresses = true;
    private String fromDefaultAddress;

    @Override
    public void setPersonService(PersonService personService) {
        super.setPersonService(personService);
        this.personService = personService;
    }

    @Override
    public void setNodeService(NodeService nodeService) {
        super.setNodeService(nodeService);
        this.nodeService = nodeService;
    }

    @Override
    public void setMailService(JavaMailSender mailService) {
        super.setMailService(mailService);
        this.mailService = mailService;
    }

    @Override
    public void setAuthenticationService(AuthenticationService authService) {
        super.setAuthenticationService(authService);
        this.authService = authService;
    }

    @Override
    public void setAuthorityService(AuthorityService authorityService) {
        super.setAuthorityService(authorityService);
        this.authorityService = authorityService;
    }

    @Override
    public void setTemplateService(TemplateService templateService) {
        super.setTemplateService(templateService);
        this.templateService = templateService;
    }

    @Override
    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        super.setServiceRegistry(serviceRegistry);
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public void setSysAdminParams(SysAdminParams sysAdminParams) {
        super.setSysAdminParams(sysAdminParams);
        this.sysAdminParams = sysAdminParams;
    }

    @Override
    public void setImageResolver(TemplateImageResolver imageResolver) {
        super.setImageResolver(imageResolver);
        this.imageResolver = imageResolver;
    }

    public void setContentService(ContentService contentService) {
        this.contentService = contentService;
    }

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

    @Override
    public void setValidateAddresses(boolean validateAddresses) {
        super.setValidateAddresses(validateAddresses);
        this.validateAddresses = validateAddresses;
    }

    @Override
    public void setFromAddress(String fromDefaultAddress) {
        super.setFromAddress(fromDefaultAddress);
        this.fromDefaultAddress = fromDefaultAddress;
    }

    @Override
    public MimeMessageHelper prepareEmail(final Action ruleAction, final NodeRef actionedUponNodeRef, final Pair<String, Locale> recipient, final Pair<InternetAddress, Locale> sender) {
        // Create the mime mail message.
        // Hack: using an array here to get around the fact that inner classes aren't closures.
        // The MimeMessagePreparator.prepare() signature does not allow us to return a value and yet
        // we can't set a result on a bare, non-final object reference due to Java language restrictions.
        final MimeMessageHelper[] messageRef = new MimeMessageHelper[1];
        MimeMessagePreparator mailPreparer = new MimeMessagePreparator() {
            @SuppressWarnings("unchecked")
            public void prepare(MimeMessage mimeMessage) throws MessagingException {
                if (logger.isDebugEnabled()) {
                    logger.debug(ruleAction.getParameterValues());
                }

                List<String> attachments = (List<String>) ruleAction.getParameterValue(ATTACHMENTS);

                boolean multipart = attachments != null && !attachments.isEmpty();

                messageRef[0] = new MimeMessageHelper(mimeMessage, multipart, "UTF-8");


                String to = (String) ruleAction.getParameterValue(PARAM_TO);
                if (to != null && to.length() != 0) {
                    messageRef[0].setTo(to);
                } else {
                    Serializable authoritiesValue = ruleAction.getParameterValue(PARAM_TO_MANY);
                    List<String> authorities = null;
                    if (authoritiesValue != null) {
                        if (authoritiesValue instanceof String) {
                            authorities = new ArrayList<String>(1);
                            authorities.add((String) authoritiesValue);
                        } else {
                            authorities = (List<String>) authoritiesValue;
                        }
                    }

                    if (authorities != null && authorities.size() != 0) {
                        List<String> recipients = new ArrayList<>(authorities.size());

                        if (logger.isTraceEnabled()) {
                            logger.trace(authorities.size() + " recipient(s) for mail");
                        }

                        for (String authority : authorities) {
                            final AuthorityType authType = AuthorityType.getAuthorityType(authority);

                            if (logger.isTraceEnabled()) {
                                logger.trace(" authority type: " + authType);
                            }

                            if (authType.equals(AuthorityType.USER)) {
                                if (personService.personExists(authority) == true) {
                                    NodeRef person = personService.getPerson(authority);
                                    String address = (String) nodeService.getProperty(person, ContentModel.PROP_EMAIL);
                                    if (address != null && address.length() != 0 && validateAddress(address)) {
                                        if (logger.isTraceEnabled()) {
                                            logger.trace("Recipient (person) exists in Alfresco with known email.");
                                        }
                                        recipients.add(address);
                                    } else {
                                        if (logger.isTraceEnabled()) {
                                            logger.trace("Recipient (person) exists in Alfresco without known email.");
                                        }
                                        // If the username looks like an email address, we'll use that.
                                        if (validateAddress(authority)) {
                                            recipients.add(authority);
                                        }
                                    }
                                } else {
                                    if (logger.isTraceEnabled()) {
                                        logger.trace("Recipient does not exist in Alfresco.");
                                    }
                                    if (validateAddress(authority)) {
                                        recipients.add(authority);
                                    }
                                }
                            } else if (authType.equals(AuthorityType.GROUP) || authType.equals(AuthorityType.EVERYONE)) {
                                if (logger.isTraceEnabled()) {
                                    logger.trace("Recipient is a group...");
                                }
                                // Notify all members of the group
                                Set<String> users;
                                if (authType.equals(AuthorityType.GROUP)) {
                                    users = authorityService.getContainedAuthorities(AuthorityType.USER, authority, false);
                                } else {
                                    users = authorityService.getAllAuthorities(AuthorityType.USER);
                                }

                                for (String userAuth : users) {
                                    if (personService.personExists(userAuth) == true) {
                                        NodeRef person = personService.getPerson(userAuth);
                                        String address = (String) nodeService.getProperty(person, ContentModel.PROP_EMAIL);
                                        if (address != null && address.length() != 0) {
                                            recipients.add(address);
                                            if (logger.isTraceEnabled()) {
                                                logger.trace("   Group member email is known.");
                                            }
                                        } else {
                                            if (logger.isTraceEnabled()) {
                                                logger.trace("   Group member email not known.");
                                            }
                                            if (validateAddress(authority)) {
                                                recipients.add(userAuth);
                                            }
                                        }
                                    } else {
                                        if (logger.isTraceEnabled()) {
                                            logger.trace("   Group member person not found");
                                        }
                                        if (validateAddress(authority)) {
                                            recipients.add(userAuth);
                                        }
                                    }
                                }
                            }
                        }

                        if (logger.isTraceEnabled()) {
                            logger.trace(recipients.size() + " valid recipient(s).");
                        }

                        if (recipients.size() > 0) {
                            messageRef[0].setTo(recipients.toArray(new String[recipients.size()]));
                        } else {
                            // All recipients were invalid
                            throw new MailPreparationException(
                                    "All recipients for the mail action were invalid"
                            );
                        }
                    } else {
                        // No recipients have been specified
                        throw new MailPreparationException(
                                "No recipient has been specified for the mail action"
                        );
                    }
                }

                NodeRef fromPerson = null;

                final String currentUserName = authService.getCurrentUserName();

                final List<String> usersNotToBeUsedInFromField = Arrays.asList(AuthenticationUtil.getAdminUserName(),
                        AuthenticationUtil.getSystemUserName(),
                        AuthenticationUtil.getGuestUserName());
                if (!usersNotToBeUsedInFromField.contains(currentUserName)) {
                    fromPerson = personService.getPerson(currentUserName);
                }

                if (isFromEnabled()) {
                    String from = (String) ruleAction.getParameterValue(PARAM_FROM);
                    if (from != null && from.length() > 0) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("from specified as a parameter, from:" + from);
                        }

                        // Check whether or not to use a personal name for the email (will be RFC 2047 encoded)
                        String fromPersonalName = (String) ruleAction.getParameterValue(PARAM_FROM_PERSONAL_NAME);
                        if (fromPersonalName != null && fromPersonalName.length() > 0) {
                            try {
                                messageRef[0].setFrom(from, fromPersonalName);
                            } catch (UnsupportedEncodingException error) {
                                messageRef[0].setFrom(from);
                            }
                        } else {
                            messageRef[0].setFrom(from);
                        }
                    } else {
                        // set the from address from the current user
                        String fromActualUser = null;
                        if (fromPerson != null) {
                            fromActualUser = (String) nodeService.getProperty(fromPerson, ContentModel.PROP_EMAIL);
                        }

                        if (fromActualUser != null && fromActualUser.length() != 0) {
                            if (logger.isDebugEnabled()) {
                                logger.debug("looked up email address for :" + fromPerson + " email from " + fromActualUser);
                            }
                            messageRef[0].setFrom(fromActualUser);
                        } else {
                            // from system or user does not have email address
                            messageRef[0].setFrom(fromDefaultAddress);
                        }
                    }

                } else {
                    if (logger.isDebugEnabled()) {
                        logger.debug("from not enabled - sending from default address:" + fromDefaultAddress);
                    }
                    // from is not enabled.
                    messageRef[0].setFrom(fromDefaultAddress);
                }

                // set subject line
                messageRef[0].setSubject((String) ruleAction.getParameterValue(PARAM_SUBJECT));


                // See if an email template has been specified
                String text = null;

                // templateRef: either a nodeRef or classpath (see ClasspathRepoTemplateLoader)
                Serializable ref = ruleAction.getParameterValue(PARAM_TEMPLATE);
                String templateRef = (ref instanceof NodeRef ? ((NodeRef) ref).toString() : (String) ref);
                if (templateRef != null) {
                    Map<String, Object> suppliedModel = null;
                    if (ruleAction.getParameterValue(PARAM_TEMPLATE_MODEL) != null) {
                        Object m = ruleAction.getParameterValue(PARAM_TEMPLATE_MODEL);
                        if (m instanceof Map) {
                            suppliedModel = (Map<String, Object>) m;
                        } else {
                            logger.warn("Skipping unsupported email template model parameters of type "
                                    + m.getClass().getName() + " : " + m.toString());
                        }
                    }

                    // build the email template model
                    Map<String, Object> model = createEmailTemplateModel(actionedUponNodeRef, suppliedModel, fromPerson);

                    // Determine the locale to use to send the email.
                    Locale locale = recipient.getSecond();
                    if (locale == null) {
                        locale = (Locale) ruleAction.getParameterValue(PARAM_LOCALE);
                    }
                    if (locale == null) {
                        locale = sender.getSecond();
                    }

                    // set subject line
                    String subject = (String) ruleAction.getParameterValue(PARAM_SUBJECT);
                    Object[] subjectParams = (Object[]) ruleAction.getParameterValue(PARAM_SUBJECT_PARAMS);
                    String localizedSubject = getLocalizedSubject(subject, subjectParams, locale);
                    if (locale == null) {
                        // process the template against the model
                        text = templateService.processTemplate("freemarker", templateRef, model);
                    } else {
                        // process the template against the model
                        text = templateService.processTemplate("freemarker", templateRef, model, locale);
                    }

                    messageRef[0].setTo(recipient.getFirst());
                    messageRef[0].setSubject(localizedSubject);

                }

                // set the text body of the message

                boolean isHTML = false;
                if (text == null) {
                    text = (String) ruleAction.getParameterValue(PARAM_TEXT);
                }

                if (text != null) {
                    if (isHTML(text)) {
                        isHTML = true;
                    }
                } else {
                    text = (String) ruleAction.getParameterValue(PARAM_HTML);
                    if (text != null) {
                        // assume HTML
                        isHTML = true;
                    }
                }

                if (text != null) {
                    messageRef[0].setText(text, isHTML);
                }

                if (multipart) {

                    NodeRef nodeRef = new NodeRef("workspace://SpacesStore/notification-template-images");
                    String path = nodeService.getPath(nodeRef).toPrefixString(namespaceService);

                    for (final String attachment : attachments) {

                        SearchParameters parameters = new SearchParameters();
                        parameters.setLanguage(SearchService.LANGUAGE_FTS_ALFRESCO);
                        parameters.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
                        parameters.setQuery(" +PATH:\"" + path + "//*\" AND @cm\\:name:\"" + attachment + "\"");


                        try {
                            ResultSet resultSet = searchService.query(parameters);
                            if (resultSet != null && resultSet.length() > 0) {
                                NodeRef imageNode = resultSet.getRow(0).getNodeRef();
                                ContentReader reader = contentService.getReader(imageNode, ContentModel.PROP_CONTENT);

                                messageRef[0].addInline(attachment, new ByteArrayResource(IOUtils.toByteArray(reader.getContentInputStream())){
                                    @Override
                                    public String getFilename() {
                                        return attachment;
                                    }
                                });

                            }
                        } catch (Exception e) {
                            logger.error("Error while getting image records", e);
                        }
                    }
                }

            }
        };
        MimeMessage mimeMessage = mailService.createMimeMessage();
        try {
            mailPreparer.prepare(mimeMessage);
        } catch (Exception e) {
            // We're forced to catch java.lang.Exception here. Urgh.
            if (logger.isInfoEnabled()) {
                logger.warn("Unable to prepare mail message. Skipping.", e);
            }
        }

        return messageRef[0];
    }

    private boolean validateAddress(String address) {
        boolean result = false;

        // Validate the email, allowing for local email addresses
        EmailValidator emailValidator = EmailValidator.getInstance(true);
        if (!validateAddresses || emailValidator.isValid(address)) {
            result = true;
        } else {
            logger.error("Failed to send email to '" + address + "' as the address is incorrectly formatted");
        }

        return result;
    }

    private Map<String, Object> createEmailTemplateModel(NodeRef ref, Map<String, Object> suppliedModel, NodeRef fromPerson) {
        Map<String, Object> model = new HashMap<String, Object>(8, 1.0f);

        if (fromPerson != null) {
            model.put("person", new TemplateNode(fromPerson, serviceRegistry, null));
        }

        if (ref != null) {
            model.put("document", new TemplateNode(ref, serviceRegistry, null));
            NodeRef parent = serviceRegistry.getNodeService().getPrimaryParent(ref).getParentRef();
            model.put("space", new TemplateNode(parent, serviceRegistry, null));
        }

        // current date/time is useful to have and isn't supplied by FreeMarker by default
        model.put("date", new Date());

        // add custom method objects
        model.put("hasAspect", new HasAspectMethod());
        model.put("message", new I18NMessageMethod());
        model.put("dateCompare", new DateCompareMethod());

        // add URLs
        model.put("url", new URLHelper(sysAdminParams));
        model.put(TemplateService.KEY_SHARE_URL, UrlUtil.getShareUrl(this.serviceRegistry.getSysAdminParams()));

        if (imageResolver != null) {
            model.put(TemplateService.KEY_IMAGE_RESOLVER, imageResolver);
        }

        // if the caller specified a model, use it without overriding
        if (suppliedModel != null && suppliedModel.size() > 0) {
            for (String key : suppliedModel.keySet()) {
                if (model.containsKey(key)) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Not allowing overwriting of built in model parameter " + key);
                    }
                } else {
                    model.put(key, suppliedModel.get(key));
                }
            }
        }

        // all done
        return model;
    }

    private String getLocalizedSubject(String subject, Object[] params, Locale locale) {
        String localizedSubject = null;
        if (locale == null) {
            localizedSubject = I18NUtil.getMessage(subject, params);
        } else {
            localizedSubject = I18NUtil.getMessage(subject, locale, params);
        }

        if (localizedSubject == null) {
            return subject;
        } else {
            return localizedSubject;
        }

    }

    public void afterPropertiesSet() throws Exception {
        if (fromDefaultAddress == null || fromDefaultAddress.length() == 0) {
            fromDefaultAddress = FROM_ADDRESS;
        }

    }
}
