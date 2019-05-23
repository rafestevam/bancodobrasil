<!DOCTYPE html>
<%@ page pageEncoding="UTF-8" %>
<%@ page import="com.aris.zkc.AZooKeeperClientFactory,
                 com.aris.zkc.EAppType,
                 com.idsscheer.webapps.arcm.common.constants.metadata.Enumerations"%>
<%@ page import="com.idsscheer.webapps.arcm.common.resources.ResourceBundleFactory"%>
<%@ page import="com.idsscheer.webapps.arcm.common.util.StringUtility"%>
<%@ page import="com.idsscheer.webapps.arcm.config.Metadata"%>
<%@ page import="com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumeration"%>
<%@ page import="com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem" %>
<%@ page import="com.idsscheer.webapps.arcm.config.metadata.enumerations.LanguageEnumerationItem" %>
<%@ page import="com.idsscheer.webapps.arcm.dl.framework.dbms.DBMSLayer" %>
<%@ page import="com.idsscheer.webapps.arcm.dl.framework.dbms.mapping.IMapping" %>
<%@ page import="com.idsscheer.webapps.arcm.ui.web.support.IWebKeys" %>
<%@ page import="com.idsscheer.webapps.arcm.ui.web.support.MessageContainer" %>
<%@ page import="org.apache.struts.Globals" %>
<%@ page import="java.util.Locale" %>
<%@ page import="com.idsscheer.webapps.arcm.dl.framework.dbms.mapping.MappingType" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/aam.tld" prefix="aam" %>
<html:html locale="true">


<%
    String umcContextPath;
    String defaultTenant;
    try {
        umcContextPath = AZooKeeperClientFactory.getInstance().getAliveInstance(EAppType.UMC, null).getContext();
        defaultTenant = Metadata.getMetadata().getTenantId();
    } catch (Exception e) {
        umcContextPath = "umc";
        defaultTenant = "";
    }
%>


<head>
	<meta http-equiv="X-UA-Compatible" content="IE=11; IE=10; IE=9; IE=8; IE=EDGE" />
    <aam:title />
    <aam:styles files="base, styles, dialogs, forms"/>
    <aam:scripts  files="jquery,submit,toolbar" />
    <%@ include file="/showInfo.jsp"%>

    <script type="text/javascript">
        $(window).load(function() {
            doReszie();
        });
        $(window).resize(function() {
            doReszie();
        });
    </script>

    <script type="text/javascript">
        var isUnloading = false;

        function doReszie() {
            $('.arcmLoginDialogBox').css({
                top : ($(window).height() - 100 - $('.arcmLoginDialogBox').outerHeight()) / 2
            });
            $('.arcmDialog').css({
                height : $(window).height() - 110
            });
        }

        function doUnload() {
            isUnloading = true;
        }

        function sendLogin() {
            if(!isUnloading) {
                submit(document.forms[0]);
            }
        }

        function sendMail() {
            var thePopup = window.open(contextPath + 'feedback_mail.jsp','Mail','width=500,height=520,status=NO,location=NO,dependent=YES,scrollbars=NO,resizable=NO');
            thePopup.focus();
        }

        function switchLanguage() {
            document.forms[0].action='switchLanguage.do';
            submit(document.forms[0]);
        }

        function handleOnKeyup(event) {
            if (event && (event.which == 13 || event.keyCode == 13)) {
                window.setTimeout("sendLogin()",100);
            }
        }

        function initFocus() {
            document.getElementById("username").focus();
        }

        function forgetPassword() {
            var language = document.forms[0].elements['<%=IWebKeys.LANGUAGE%>'].value;
            var username = document.forms[0].elements['username'].value;
            relocate('<%=umcContextPath%>/resetPassword?tenant=<%=defaultTenant%>&user='+username+'&locale='+language);
        }

        function changePassword() {
            var language = document.forms[0].elements['<%=IWebKeys.LANGUAGE%>'].value;
            var username = document.forms[0].elements['username'].value;
            relocate('<%=umcContextPath%>/changePassword?tenant=<%=defaultTenant%>&user='+username+'&locale='+language);
        }

        <aam:initToolBar  />
    </script>


</head>

<body class="login" onunload="doUnload()" onload="initFocus()">

<div class="arcmloginScreen">

    <div class="arcmLoginHeader">
        <img src="design/default/images/arisRiskAndComplianceManager.png">
    </div>


    <div class="arcmLoginDialog">

        <%
            // error messages
            MessageContainer messageContainer = (MessageContainer) session.getAttribute(IWebKeys.MESSAGE_CONTAINER );
            if(messageContainer == null) {
                messageContainer = (MessageContainer) request.getAttribute(IWebKeys.MESSAGE_CONTAINER );
            }
            if (messageContainer == null) {
                messageContainer = new MessageContainer(request);
            }

            // in case of an Derby database is used -> show warning message
            if ( DBMSLayer.CURRENT_DBMS == MappingType.DERBY) {
                messageContainer.addWarning("warning.version.demo.derby.WRG");
            }
            if (Metadata.getMetadata().isTestSystem() || Metadata.getMetadata().getDatalayerParameterValue("dbms","dbms.autoStartMigration", "false").equalsIgnoreCase("true")) {
                messageContainer.addWarning("warning.test.environment.WRG");
            }
        %>


        <form method="post" action="/arcm/login.do">

            <aam:login titleKey="login.dialog.log.in.DBI" width="400">

                <!-- header and messages-->
                <%
                    if (messageContainer != null && messageContainer.hasMessages())
                    {
                %>
                <div class="arcmLoginFieldRow arcmDialogContentPanel">
                    <table class="message">
                        <tbody>
                        <!-- notification layout - each notification with own row -->
                        <%
                            //message container returns its internal notifications as formatted HTML table row Strings
                            for (String messageString : messageContainer.getMessages()) {
                        %>
                        <%=messageString%>
                        <%
                            }
                        %>
                        </tbody>
                    </table>
                </div>
                <%
                        messageContainer.clearMessages();
                    }
                %>


                <div >
                    <!-- /header and messages-->

                    <!-- language -->
                    <div class="arcmLoginFieldRow">
                        <div class="arcmLoginInputPanel arcmLoginInputLanguagePanel" tabindex="0">

                            <%
                                String language = Locale.ENGLISH.getLanguage();
                                final Object selectedLocale = pageContext.getSession().getAttribute(Globals.LOCALE_KEY);
                                if (selectedLocale instanceof Locale && ResourceBundleFactory.isLocaleAvailable((Locale)selectedLocale)) {
                                    language = ((Locale) selectedLocale).getLanguage();
                                } else {
                                    if (ResourceBundleFactory.isLocaleAvailable( request.getLocale())) {
                                        language = request.getLocale().getLanguage();
                                    }
                                }
                            %>

                            <select class="arcmLoginInput" name="<%=IWebKeys.LANGUAGE%>" onchange="switchLanguage();" >
                                <%
                                    // if called by "arcm/login.jsp?devlocale=true"
                                    //set dev locale selection
                                    String devlocale = request.getParameter("devlocale");
                                    if(devlocale!=null && Boolean.parseBoolean(devlocale)){
                                %>
                                <option value="dev" selected="selected">Dev properties</option>
                                <%}else{%>
                                <% final IEnumeration lang = Enumerations.LANGUAGE.ENUM;
                                    for (final IEnumerationItem item : lang.getItems()) {
                                        if (item.isPleaseSelect() || item.isAll() || !item.isFormRelevant()) {
                                            continue;
                                        }
                                %>
                                <option value="<%= item.getValue() %>" <%= item.getValue().equals(language) ? " selected=\"selected\"" : ""%>><aam:message key="<%= item.getPropertyKey() %>" locale="<%= ((LanguageEnumerationItem)item).getLocale() %>"/></option>
                                <%
                                    }} // for
                                %>
                            </select>
                        </div>
                    </div>
                    <!-- /language -->

                    <!-- username -->
                    <%
                        String username = StringUtility.isEmpty(request.getParameter("username"))
                                ? (StringUtility.isEmpty(request.getAttribute("username")) ? "" : (String)request.getAttribute("username"))
                                : request.getParameter("username");
                    %>
                    <div class="arcmLoginFieldRow">
                        <div class="arcmLoginInputPanel arcmLoginInputUsernamePanel" tabindex="0">
                            <input id="username" name="username" onkeyup="handleOnKeyup(event)"
                                   value="<%= username %>" placeholder="<aam:message key="login.user.DBI" />" maxlength="254"
                                   class="arcmLoginInput" type="text"></div>
                        <div style="display: none;" class="arcmLoginErrorLabel"></div>
                    </div>
                    <!-- /username -->


                    <!-- password -->
                    <div class="arcmLoginFieldRow">
                        <div class="arcmLoginInputPanel arcmLoginInputPasswordPanel" tabindex="1">
                            <input name="password" onkeyup="handleOnKeyup(event)"
                                   placeholder="<aam:message key="login.password.DBI" />" maxlength="254"
                                   class="arcmLoginInput" type="password"></div>
                    </div>
                    <!-- /password -->

                    <!-- login button -->
                    <div class="arcmLoginButtonPanel">
                        <button onclick="javascript:sendLogin()" tabindex="2" class="arcmLoginButton" type="button">
                            <aam:message key="button.login.BTN" />
                        </button>
                    </div>
                    <!-- /login button -->

                    <!-- forgot password -->
                    <div class="arcmLoginForgotPasswordLink">
                    	<a href="javascript:changePassword()" tabindex="3"><aam:message key="button.change.password.BTN" /></a> |
                    	<a href="javascript:forgetPassword()" tabindex="3"><aam:message key="button.forget.password.BTN" /></a> |
        		    	<% if(!language.equals(Enumerations.LANGUAGE.PORTUGUESE.getValue())) { %>
		            	<a href="javascript:sendMail()" tabindex="3"><aam:message key="button.whistleblow.BTN" /></a>
        		    	<% } %>
		            	<% if(language.equals(Enumerations.LANGUAGE.PORTUGUESE.getValue())) { %>
        		    	<a href="bb_help/start/bb_arcm_help.htm" target="_blank" tabindex="3"><aam:message key="button.bb_customized.help.BTN" /></a>
		            	<% } %>
        			</div>
                    <!-- /forgot password -->

                </div>
            </aam:login>
            <%
                String guid = StringUtility.isEmpty(request.getParameter(IWebKeys.GUID))
                        ? (StringUtility.isEmpty(request.getParameter(IWebKeys.GUID)) ? "" : request.getParameter(IWebKeys.GUID))
                        : request.getParameter("guid");

                String ovid = StringUtility.isEmpty(request.getParameter(IWebKeys.OVID))
                        ? (StringUtility.isEmpty(request.getParameter(IWebKeys.OVID)) ? "" : request.getParameter(IWebKeys.OVID))
                        : request.getParameter(IWebKeys.OVID);
            %>
            <input type="hidden" name="<%= IWebKeys.GUID %>" value="<%= guid %>">
            <input type="hidden" name="<%= IWebKeys.OVID %>" value="<%= ovid %>">
        </form>
    </div>


    <div class="arcmLoginFooter">
        <div class="arcmLoginCopyrightLabel"><aam:message key="login.copyright.label.ZZZ" /></div>
        <div class="arcmLoginPoweredByLabel"><img src="design/default/images/img_SAGLogo_Footer.png"></div>
    </div>
</div>




</body>
</html:html>
