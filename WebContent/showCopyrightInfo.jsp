<%@ page pageEncoding="UTF-8" %>
<%@ page import="com.idsscheer.webapps.arcm.common.util.IVersionInfo"%>
<%@ page import="com.idsscheer.webapps.arcm.ui.web.support.IWebKeys"%>
<%@ page import="com.idsscheer.webapps.arcm.ui.web.support.MessageContainer"%>
<%@ page import="com.idsscheer.webapps.arcm.config.Metadata"%>
<%
    String approach = Metadata.getMetadata().getSchemaInformation().getApproach();
    String versionInfo =
            "ARIS Risk & Compliance Manager<br>\n"
                    + "Version: " + IVersionInfo.VERSION_TEXT + " ("+approach+")<br>\n"
        			+ "BB Build: <b>@UNDER_CONSTRUCTION@</b>"; 

    MessageContainer messageContainer = (MessageContainer)session.getAttribute( IWebKeys.MESSAGE_CONTAINER );
    if( messageContainer == null ) {
        messageContainer = new MessageContainer( request );
        session.setAttribute( IWebKeys.MESSAGE_CONTAINER, messageContainer );
    }

    messageContainer.setHeaderTitleKey("html.copyright");


    messageContainer.addInfoLocalized( versionInfo );
    messageContainer.addInfo("legal.copyright.DBI");
    messageContainer.addInfo("legal.trademark.patent.DBI");
    messageContainer.addInfo("legal.third.party.DBI");

    messageContainer.setWidthPx(800); //broader message div because of lengthy text

%>
<jsp:include page="message.jsp" />