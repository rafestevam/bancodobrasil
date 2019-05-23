<%@ page pageEncoding="UTF-8" %>
<%@ page import="com.idsscheer.webapps.arcm.config.metadata.rights.roles.IComponentRight"%>
<%@ page import="com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType"%>
<%@ page import="com.idsscheer.webapps.arcm.common.license.LicensedComponent"%>
<%@ page import="com.idsscheer.webapps.arcm.bl.authorization.rights.ComponentRight" %>
<%@ page import="com.idsscheer.webapps.arcm.common.constants.metadata.Enumerations" %>
<%@ page import="com.idsscheer.webapps.arcm.ui.framework.support.IRequestKeys" %>
<%@ page import="com.idsscheer.webapps.arcm.ui.framework.common.UIEnvironmentManager" %>
<%@ page import="com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem" %>
<%@ page import="com.idsscheer.webapps.arcm.config.metadata.rights.roles.ObjectRight" %>
<%@ page import="com.idsscheer.webapps.arcm.bl.offlineprocessing.rights.OfflineProcessingConfigReader" %>
<%@ page import="com.idsscheer.webapps.arcm.config.Metadata" %>
<%@ page import="com.idsscheer.webapps.arcm.common.constants.metadata.EnumerationsBB" %>
<%@ taglib uri="/tags/aam" prefix="aam" %>
<%@ taglib uri="/WEB-INF/aamgui.tld" prefix="gui" %>
<!DOCTYPE html>
<%!
    String href="";
    IComponentRight componentRight;
%>
<html>
<head>
	<meta http-equiv="X-UA-Compatible" content="IE=11; IE=10; IE=9; IE=8; IE=EDGE" />
    <aam:title />
    <aam:styles files="base,styles,administration,dialogs,button" />
    <aam:scripts  files="jquery,logic,submit" />
</head>
<body>
<div id="body" class="ADMIN_PAGE_CONTROL">
<gui:page columns="1">

<gui:column>
<gui:groupbox titleKey="html.administration.group.systemmanagement" isContentAnUnorderedList="true">

    <%  href =  "javascript:aam_relocate('object.do?__objectType=site&amp;__commandId=edit&" + IRequestKeys.WINDOW_ID +"=" + UIEnvironmentManager.get().getId() + "')"; %>
    <gui:field  propertyKey="html.administration.site"
                   descriptionKey="html.administration.site.description"
                   right="<%= ObjectRight.READ %>"
                   objectType="<%= ObjectType.SITE%>"
                   href="<%= href %>"/>

    <gui:list  listID="client"
                  descriptionKey="html.administration.group.client.description"
                  right="<%= ObjectRight.READ %>"
                  minRoleLevel="<%= new IEnumerationItem[] {Enumerations.USERROLE_LEVEL.CROSSCLIENT, Enumerations.USERROLE_LEVEL.CLIENT} %>"/>

    <gui:list  listID="usergroup"
                  descriptionKey="html.administration.group.usergroups.description"
                  right="<%= ObjectRight.READ %>"/>

    <gui:list descriptionKey="html.administration.group.users.description"
                  listID="user"
                  right="<%= ObjectRight.READ %>"/>


    <gui:list
              descriptionKey="html.administration.group.news.description"
                  right="<%= ObjectRight.WRITE %>"
                  listID="all_newsmessages"/>
</gui:groupbox>

<gui:groupbox titleKey="html.administration.group.actions" isContentAnUnorderedList="true">
    <%
        final String jobPath = "job.do?__jobName=";
    %>

    <%  href =  "javascript:aam_relocate('" + jobPath + Enumerations.JOBS.GENERATORJOB.getId() + "&" + IRequestKeys.OBJECT_TYPE + "=TESTCASE&" + IRequestKeys.WINDOW_ID +"=" + UIEnvironmentManager.get().getId() + "')";
        componentRight = ComponentRight.getRight("generate.testcase") ; %>
    <gui:field     propertyKey="html.server.jobs.generate.testcases.DBI"
                   href="<%= href %>"
                   hasButton="true"
                   buttonTextKey="html.administration.functions.buttons.generate"
                   componentRight="<%= componentRight %>"
                   licensedComponent="<%= LicensedComponent.TEST_MANAGEMENT %>"/>

    <%  componentRight = ComponentRight.getRight("generate.testcase") ; %>
    <gui:list     propertyKey="html.server.jobs.followup.DBI"
                  listID="followup"
                  hasButton="true"
                  buttonTextKey="html.administration.functions.buttons.generate"
                  componentRight="<%= componentRight %>"
                  licensedComponent="<%= LicensedComponent.TEST_MANAGEMENT %>"/>

    <%  href =  "javascript:aam_relocate('" + jobPath + Enumerations.JOBS.GENERATORJOB.getId() + "&" + IRequestKeys.OBJECT_TYPE + "=SURVEY&" + IRequestKeys.WINDOW_ID +"=" + UIEnvironmentManager.get().getId() + "')";
        componentRight = ComponentRight.getRight("generate.survey") ; %>
    <gui:field     propertyKey="html.server.jobs.generate.surveys.DBI"
                   licensedComponent="<%= LicensedComponent.SURVEY_MANAGEMENT %>"
                   href="<%= href %>"
                   hasButton="true"
                   buttonTextKey="html.administration.functions.buttons.generate"
                   componentRight="<%= componentRight %>"/>

    <%  href =  "javascript:aam_relocate('" + jobPath + Enumerations.JOBS.GENERATORJOB.getId() + "&" + IRequestKeys.OBJECT_TYPE + "=SOPROCESS&" + IRequestKeys.WINDOW_ID +"=" + UIEnvironmentManager.get().getId() + "')";
        componentRight = ComponentRight.getRight("generate.soprocess") ; %>
    <gui:field     propertyKey="html.server.jobs.generate.signoffs.DBI"
                   licensedComponent="<%= LicensedComponent.SIGN_OFF_MANAGEMENT %>"
                   href="<%= href %>"
                   hasButton="true"
                   buttonTextKey="html.administration.functions.buttons.generate"
                   componentRight="<%= componentRight %>" />

    <%  href =  "javascript:aam_relocate('" + jobPath + Enumerations.JOBS.GENERATORJOB.getId() + "&" + IRequestKeys.OBJECT_TYPE + "=RISKASSESSMENT&" + IRequestKeys.WINDOW_ID +"=" + UIEnvironmentManager.get().getId() + "')";
        componentRight = ComponentRight.getRight("generate.riskassessment") ; %>
    <gui:field     propertyKey="html.server.jobs.generate.assessments.DBI"
                   licensedComponent="<%= LicensedComponent.RISK_MANAGEMENT %>"
                   href='<%= href %>'
                   hasButton="true"
                   buttonTextKey="html.administration.functions.buttons.generate"
                   componentRight="<%= componentRight %>" />

    <%  href =  "javascript:aam_relocate('" + jobPath + Enumerations.JOBS.GENERATORJOB.getId() + "&" + IRequestKeys.OBJECT_TYPE + "=AUDIT&" + IRequestKeys.WINDOW_ID +"=" + UIEnvironmentManager.get().getId() + "')";
        componentRight = ComponentRight.getRight("generate.audit") ; %>
    <gui:field     propertyKey="html.server.jobs.generate.audits.DBI"
                   licensedComponent="<%= LicensedComponent.AUDIT_MANAGEMENT %>"
                   href='<%= href %>'
                   hasButton="true"
                   buttonTextKey="html.administration.functions.buttons.generate"
                   componentRight="<%= componentRight %>" />

    <%  href =  "javascript:aam_relocate('" + jobPath + Enumerations.JOBS.GENERATORJOB.getId() + "&" + IRequestKeys.OBJECT_TYPE + "=POLICY&" + IRequestKeys.WINDOW_ID +"=" + UIEnvironmentManager.get().getId() + "')";
        componentRight = ComponentRight.getRight("generate.policy") ; %>
    <gui:field     propertyKey="html.server.jobs.generate.policies.DBI"
                   href="<%= href %>"
                   hasButton="true"
                   buttonTextKey="html.administration.functions.buttons.generate"
                   componentRight="<%= componentRight %>"
                   licensedComponent="<%= LicensedComponent.POLICY_MANAGEMENT %>"
                   hasRole="<%= new IEnumerationItem[] { Enumerations.USERROLE_TYPE.POLICYMANAGER }%>"
            />

    <%  href =  "javascript:aam_relocate('" + jobPath + Enumerations.JOBS.UPDATERJOB.getId() + "&" + IRequestKeys.OBJECT_TYPE + "=POLICY&" + IRequestKeys.WINDOW_ID +"=" + UIEnvironmentManager.get().getId() + "')";
        componentRight = ComponentRight.getRight("update.policy") ; %>
    <gui:field     propertyKey="html.server.jobs.update.policies.DBI"
                   href="<%= href %>"
                   hasButton="true"
                   buttonTextKey="html.administration.functions.buttons.update"
                   componentRight="<%= componentRight %>"
                   licensedComponent="<%= LicensedComponent.POLICY_MANAGEMENT %>"/>

    <%  href =  "javascript:aam_relocate('" + jobPath + Enumerations.JOBS.GENERATORJOB.getId() + "&" + IRequestKeys.OBJECT_TYPE + "=POLICYREVIEW&" + IRequestKeys.WINDOW_ID +"=" + UIEnvironmentManager.get().getId() + "')";
        componentRight = ComponentRight.getRight("generate.policyreview") ; %>
    <gui:field     propertyKey="html.server.jobs.generate.policyreviews.DBI"
                   href="<%= href %>"
                   hasButton="true"
                   buttonTextKey="html.administration.functions.buttons.generate"
                   componentRight="<%= componentRight %>"
                   licensedComponent="<%= LicensedComponent.POLICY_MANAGEMENT %>"
                   hasRole="<%= new IEnumerationItem[] { Enumerations.USERROLE_TYPE.POLICYMANAGER }%>"
            />

    <%  href =  "javascript:aam_relocate('" + jobPath + Enumerations.JOBS.GENERATORJOB.getId() + "&" + IRequestKeys.OBJECT_TYPE + "=CHANGEREVIEW&" + IRequestKeys.WINDOW_ID +"=" + UIEnvironmentManager.get().getId() + "')";
        componentRight = ComponentRight.getRight("generate.changereview") ; %>
    <gui:field     propertyKey="html.server.jobs.generate.changereviews.DBI"
                   href="<%= href %>"
                   hasButton="true"
                   buttonTextKey="html.administration.functions.buttons.generate"
                   componentRight="<%= componentRight %>"
                   hasRole="<%= new IEnumerationItem[] { Enumerations.USERROLE_TYPE.HIERARCHYMANAGER }%>"
                   licensedComponent="<%= LicensedComponent.CHANGE_MANAGEMENT %>"
            />

    <%  href =  "javascript:aam_relocate('" + jobPath + Enumerations.JOBS.GENERATORJOB.getId() + "&" + IRequestKeys.OBJECT_TYPE + "=CONTROLEXECUTION&" + IRequestKeys.WINDOW_ID +"=" + UIEnvironmentManager.get().getId() + "')";
        componentRight = Metadata.getMetadata().getComponentRight("generate.controlexecution") ; %>
    <gui:field     propertyKey="html.server.jobs.generate.controlexecution.DBI"
                   href="<%= href %>"
                   hasButton="true"
                   buttonTextKey="html.administration.functions.buttons.generate"
                   componentRight="<%= componentRight %>"
                   hasRole="<%= new IEnumerationItem[] { Enumerations.USERROLE_TYPE.CONTROLMANAGER }%>"
                   licensedComponent="<%= LicensedComponent.TEST_MANAGEMENT %>"
            />


    <%  href =  "javascript:aam_relocate('" + jobPath + Enumerations.JOBS.MONITORJOB.getId() + "&" + IRequestKeys.OBJECT_TYPE + "=TESTCASE&" + IRequestKeys.WINDOW_ID +"=" + UIEnvironmentManager.get().getId() + "')";
        componentRight = ComponentRight.getRight("check.testcase") ; %>
    <gui:field     propertyKey="html.server.jobs.check.testcases.DBI"
                   href="<%= href %>"
                   hasButton="true"
                   buttonTextKey="html.administration.functions.buttons.check"
                   componentRight="<%= componentRight %>"
                   licensedComponent="<%= LicensedComponent.TEST_MANAGEMENT %>"
                   objectType="<%= ObjectType.TESTCASE%>"
                   right="<%= ObjectRight.WRITE %>"
                   minRoleLevel="<%= new IEnumerationItem[] {Enumerations.USERROLE_LEVEL.CLIENT, Enumerations.USERROLE_LEVEL.CROSSCLIENT} %>"/>

    <%   componentRight = ComponentRight.getRight("check.testcase") ; %>
    <gui:list     propertyKey="html.server.jobs.update.testcases.DBI"
                  listID="updateableTestactions"
                  hasButton="true"
                  buttonTextKey="html.administration.functions.buttons.update"
                  componentRight="<%= componentRight %>"
                  licensedComponent="<%= LicensedComponent.TEST_MANAGEMENT %>"/>

    <%  href =  "javascript:aam_relocate('" + jobPath + Enumerations.JOBS.MONITORJOB.getId() + "&" + IRequestKeys.OBJECT_TYPE + "=SURVEY" + "&" + IRequestKeys.OBJECT_TYPE + "=QUESTIONNAIRE" + "&" + IRequestKeys.WINDOW_ID +"=" + UIEnvironmentManager.get().getId() + "')";
        componentRight = ComponentRight.getRight("check.questionnaire") ; %>
    <gui:field     propertyKey="html.server.jobs.check.surveys.DBI"
                   href="<%= href %>"
                   hasButton="true"
                   buttonTextKey="html.administration.functions.buttons.check"
                   licensedComponent="<%= LicensedComponent.SURVEY_MANAGEMENT %>"
                   componentRight="<%= componentRight %>"
                   objectType="<%= ObjectType.SURVEY%>"
                   right="<%= ObjectRight.WRITE %>"
                   minRoleLevel="<%= new IEnumerationItem[] {Enumerations.USERROLE_LEVEL.CLIENT, Enumerations.USERROLE_LEVEL.CROSSCLIENT} %>"/>

    <%  href =  "javascript:aam_relocate('" + jobPath + Enumerations.JOBS.MONITORJOB.getId() + "&" + IRequestKeys.OBJECT_TYPE + "=SOPROCESS&" + IRequestKeys.WINDOW_ID +"=" + UIEnvironmentManager.get().getId() + "')";
        componentRight = ComponentRight.getRight("check.soprocess") ; %>
    <gui:field     propertyKey="html.server.jobs.check.signoffs.DBI"
                   href="<%= href %>"
                   hasButton="true"
                   buttonTextKey="html.administration.functions.buttons.check"
                   licensedComponent="<%= LicensedComponent.SIGN_OFF_MANAGEMENT %>"
                   componentRight="<%= componentRight %>"
                   objectType="<%= ObjectType.SOPROCESS%>"
                   right="<%= ObjectRight.WRITE %>"
                   minRoleLevel="<%= new IEnumerationItem[] {Enumerations.USERROLE_LEVEL.CLIENT, Enumerations.USERROLE_LEVEL.CROSSCLIENT} %>"/>

    <%  href =  "javascript:aam_relocate('" + jobPath + Enumerations.JOBS.MONITORJOB.getId() + "&" + IRequestKeys.OBJECT_TYPE + "=ISSUE&" + IRequestKeys.WINDOW_ID +"=" + UIEnvironmentManager.get().getId() + "')";
        componentRight = ComponentRight.getRight("check.issue") ; %>
    <gui:field     propertyKey="html.server.jobs.check.issues.DBI"
                   href="<%=href %>"
                   hasButton="true"
                   buttonTextKey="html.administration.functions.buttons.check"
                   licensedComponent="<%= LicensedComponent.ISSUE_MANAGEMENT %>"
                   componentRight="<%= componentRight %>"
                   objectType="<%= ObjectType.ISSUE%>"
                   right="<%= ObjectRight.WRITE %>"
                   minRoleLevel="<%= new IEnumerationItem[] {Enumerations.USERROLE_LEVEL.CLIENT, Enumerations.USERROLE_LEVEL.CROSSCLIENT} %>"/>

    <%  href =  "javascript:aam_relocate('" + jobPath + Enumerations.JOBS.MONITORJOB.getId() + "&" + IRequestKeys.OBJECT_TYPE + "=RISKASSESSMENT&" + IRequestKeys.WINDOW_ID +"=" + UIEnvironmentManager.get().getId() + "')";
        componentRight = ComponentRight.getRight("check.riskassessment") ; %>
    <gui:field     propertyKey="html.server.jobs.check.assessments.DBI"
                   licensedComponent="<%= LicensedComponent.RISK_MANAGEMENT %>"
                   href="<%= href %>"
                   hasButton="true"
                   buttonTextKey="html.administration.functions.buttons.check"
                   componentRight="<%= componentRight %>"
                   objectType="<%= ObjectType.RISKASSESSMENT%>"
                   right="<%= ObjectRight.WRITE %>"
                   minRoleLevel="<%= new IEnumerationItem[] {Enumerations.USERROLE_LEVEL.CLIENT, Enumerations.USERROLE_LEVEL.CROSSCLIENT} %>"/>

    <%  href =  "javascript:aam_relocate('" + jobPath + Enumerations.JOBS.MONITORJOB.getId() + "&" + IRequestKeys.OBJECT_TYPE + "=AUDIT" + "&" + IRequestKeys.OBJECT_TYPE + "=AUDITSTEP" + "&" + IRequestKeys.WINDOW_ID +"=" + UIEnvironmentManager.get().getId() + "')";
        componentRight = ComponentRight.getRight("check.audit") ; %>
    <gui:field     propertyKey="html.server.jobs.check.audits.DBI"
                   href="<%= href %>"
                   hasButton="true"
                   buttonTextKey="html.administration.functions.buttons.check"
                   componentRight="<%= componentRight %>"
                   licensedComponent="<%= LicensedComponent.AUDIT_MANAGEMENT %>"
                   objectType="<%= ObjectType.AUDIT%>"
                   right="<%= ObjectRight.WRITE %>"
                   minRoleLevel="<%= new IEnumerationItem[] {Enumerations.USERROLE_LEVEL.CLIENT, Enumerations.USERROLE_LEVEL.CROSSCLIENT} %>"/>

    <%  href =  "javascript:aam_relocate('" + jobPath + Enumerations.JOBS.MONITORJOB.getId() + "&" + IRequestKeys.OBJECT_TYPE + "=POLICY&" + IRequestKeys.WINDOW_ID +"=" + UIEnvironmentManager.get().getId() + "')";
        componentRight = ComponentRight.getRight("check.policy") ; %>
    <gui:field     propertyKey="html.server.jobs.check.policies.DBI"
                   href="<%= href %>"
                   hasButton="true"
                   buttonTextKey="html.administration.functions.buttons.check"
                   componentRight="<%= componentRight %>"
                   licensedComponent="<%= LicensedComponent.POLICY_MANAGEMENT %>"
                   objectType="<%= ObjectType.POLICY%>"
                   right="<%= ObjectRight.WRITE %>"
                   minRoleLevel="<%= new IEnumerationItem[] {Enumerations.USERROLE_LEVEL.CLIENT, Enumerations.USERROLE_LEVEL.CROSSCLIENT} %>"/>

    <%  href =  "javascript:aam_relocate('" + jobPath + Enumerations.JOBS.MONITORJOB.getId() + "&" + IRequestKeys.OBJECT_TYPE + "=POLICYAPPROVAL&" + IRequestKeys.WINDOW_ID +"=" + UIEnvironmentManager.get().getId() + "')";
        componentRight = ComponentRight.getRight("check.policyapproval") ; %>
    <gui:field     propertyKey="html.server.jobs.check.policyapprovals.DBI"
                   href="<%= href %>"
                   hasButton="true"
                   buttonTextKey="html.administration.functions.buttons.check"
                   componentRight="<%= componentRight %>"
                   licensedComponent="<%= LicensedComponent.POLICY_MANAGEMENT %>"
                   objectType="<%= ObjectType.POLICYAPPROVAL%>"
                   right="<%= ObjectRight.WRITE %>"
                   minRoleLevel="<%= new IEnumerationItem[] {Enumerations.USERROLE_LEVEL.CLIENT, Enumerations.USERROLE_LEVEL.CROSSCLIENT} %>"/>

    <%  href =  "javascript:aam_relocate('" + jobPath + Enumerations.JOBS.MONITORJOB.getId() + "&" + IRequestKeys.OBJECT_TYPE + "=POLICYCONFIRMATION&" + IRequestKeys.WINDOW_ID +"=" + UIEnvironmentManager.get().getId() + "')";
        componentRight = ComponentRight.getRight("check.policyconfirmation") ; %>
    <gui:field     propertyKey="html.server.jobs.check.policyconfirmations.DBI"
                   href="<%= href %>"
                   hasButton="true"
                   buttonTextKey="html.administration.functions.buttons.check"
                   componentRight="<%= componentRight %>"
                   licensedComponent="<%= LicensedComponent.POLICY_MANAGEMENT %>"
                   objectType="<%= ObjectType.POLICYAPPROVAL%>"
                   right="<%= ObjectRight.WRITE %>"
                   minRoleLevel="<%= new IEnumerationItem[] {Enumerations.USERROLE_LEVEL.CLIENT, Enumerations.USERROLE_LEVEL.CROSSCLIENT} %>"/>

    <%  href =  "javascript:aam_relocate('" + jobPath + Enumerations.JOBS.MONITORJOB.getId() + "&" + IRequestKeys.OBJECT_TYPE + "=POLICYREVIEW&" + IRequestKeys.WINDOW_ID +"=" + UIEnvironmentManager.get().getId() + "')";
        componentRight = ComponentRight.getRight("check.policyreview") ; %>
    <gui:field     propertyKey="html.server.jobs.check.policyreviews.DBI"
                   href="<%= href %>"
                   hasButton="true"
                   buttonTextKey="html.administration.functions.buttons.check"
                   componentRight="<%= componentRight %>"
                   licensedComponent="<%= LicensedComponent.POLICY_MANAGEMENT %>"
                   hasRole="<%= new IEnumerationItem[] { Enumerations.USERROLE_TYPE.POLICYMANAGER }%>"
                   objectType="<%= ObjectType.POLICYREVIEW%>"
                   right="<%= ObjectRight.WRITE %>"
                   minRoleLevel="<%= new IEnumerationItem[] {Enumerations.USERROLE_LEVEL.CLIENT} %>"/>

    <%  href =  "javascript:aam_relocate('" + jobPath + Enumerations.JOBS.MONITORJOB.getId() + "&" + IRequestKeys.OBJECT_TYPE + "=CHANGEREVIEW&" + IRequestKeys.WINDOW_ID +"=" + UIEnvironmentManager.get().getId() + "')";
        componentRight = ComponentRight.getRight("check.changereview") ; %>
    <gui:field     propertyKey="html.server.jobs.check.changereviews.DBI"
                   href="<%= href %>"
                   hasButton="true"
                   buttonTextKey="html.administration.functions.buttons.check"
                   componentRight="<%= componentRight %>"
                   hasRole="<%= new IEnumerationItem[] { Enumerations.USERROLE_TYPE.HIERARCHYMANAGER }%>"
                   licensedComponent="<%= LicensedComponent.CHANGE_MANAGEMENT %>"
                   objectType="<%= ObjectType.CHANGEREVIEW%>"
                   right="<%= ObjectRight.WRITE %>"
                   minRoleLevel="<%= new IEnumerationItem[] {Enumerations.USERROLE_LEVEL.CLIENT} %>"/>

    <%  href =  "javascript:aam_relocate('" + jobPath + Enumerations.JOBS.MONITORJOB.getId() + "&" + IRequestKeys.OBJECT_TYPE + "=CONTROLEXECUTION&" + IRequestKeys.WINDOW_ID +"=" + UIEnvironmentManager.get().getId() + "')";
        componentRight = Metadata.getMetadata().getComponentRight("check.controlexecution") ; %>
    <gui:field     propertyKey="html.server.jobs.check.controlexecution.DBI"
                   href="<%= href %>"
                   hasButton="true"
                   buttonTextKey="html.administration.functions.buttons.check"
                   componentRight="<%= componentRight %>"
                   licensedComponent="<%= LicensedComponent.TEST_MANAGEMENT %>"
                   objectType="<%= ObjectType.CONTROLEXECUTION%>"
                   right="<%= ObjectRight.WRITE %>"
                   minRoleLevel="<%= new IEnumerationItem[] {Enumerations.USERROLE_LEVEL.CLIENT, Enumerations.USERROLE_LEVEL.CROSSCLIENT} %>"/>

    <%  href =  "javascript:aam_relocate('" + jobPath + EnumerationsBB.JOBS_BB.CONTROLCHECKERJOB.getId() + "&" + IRequestKeys.WINDOW_ID +"=" + UIEnvironmentManager.get().getId() + "')";
        componentRight = ComponentRight.getRight("view.controlchecker") ; %>
    <gui:field     propertyKey="html.server.jobs.check.controls.DBI"
                   href="<%= href %>"
                   hasButton="true"
                   buttonTextKey="html.administration.functions.buttons.check"
                   componentRight="<%= componentRight %>"/>
   <%
        if (OfflineProcessingConfigReader.hasOfflineProcessingRight(UIEnvironmentManager.get().getUserContext())
                || OfflineProcessingConfigReader.hasOfflineProcessingOperatorRight(UIEnvironmentManager.get().getUserContext())) {
    %>
    <%  href =  "javascript:aam_relocate('checkinupload.do?" + IRequestKeys.WINDOW_ID +"=" + UIEnvironmentManager.get().getId() + "')"; %>
    <gui:field
               propertyKey="html.home.offlineprocessing.upload.DBI"
               buttonTextKey="html.home.offlineprocessing.upload.button.BTN"
               hasButton="true"
               href="<%= href %>"/>
    <%  } %>

    <% componentRight = ComponentRight.getRight("plan.systemjobs") ;
      href =  "javascript:aam_relocate('reloadLinkTypes.do?" + IRequestKeys.WINDOW_ID +"=" + UIEnvironmentManager.get().getId() + "')"; %>
    <gui:field     propertyKey="html.linktype.reload"
                   href="<%= href %>"
                   hasButton="true"
                   buttonTextKey="html.administration.functions.buttons.reload"
                   componentRight="<%= componentRight %>"/>

    <% href = "javascript:aam_relocate('" +jobPath + Enumerations.JOBS.UMCSYNCJOB.getId() + "&" + IRequestKeys.WINDOW_ID +"=" + UIEnvironmentManager.get().getId() + "')";
        componentRight = ComponentRight.getRight("umc.synchronization") ; %>
    <gui:field     propertyKey="html.umc.sync"
                   href="<%= href %>"
                   hasButton="true"
                   buttonTextKey="html.administration.functions.buttons.synchronize"
                   componentRight="<%= componentRight %>"
                   objectType="<%= ObjectType.USER%>"
                   right="<%= ObjectRight.READ %>"
                   minRoleLevel="<%= new IEnumerationItem[] {Enumerations.USERROLE_LEVEL.CROSSCLIENT} %>"/>

</gui:groupbox>

<gui:groupbox titleKey="html.administration.group.monitoring"
              helpId="ARCM_ADMINISTRATION_MONITORING.HLP"  isContentAnUnorderedList="true">

    <%  href =  "javascript:aam_relocate('navigation.do?" + IRequestKeys.TARGET + "=jobviewer&" + IRequestKeys.TITLE_KEY + "=html.administration.monitoring.running&" + IRequestKeys.WINDOW_ID +"=" + UIEnvironmentManager.get().getId() + "')";
        componentRight = ComponentRight.getRight("view.monitoring") ; %>
    <gui:field propertyKey="html.administration.monitoring.running"
               href="<%= href %>"
               componentRight="<%= componentRight %>"/>

    <%  href =  "javascript:aam_relocate('navigation.do?" + IRequestKeys.TARGET + "=buddylist&" + IRequestKeys.TITLE_KEY + "=html.server.buddylist&" + IRequestKeys.WINDOW_ID +"=" + UIEnvironmentManager.get().getId() + "')";
        componentRight = ComponentRight.getRight("view.buddylist") ; %>
    <gui:field  propertyKey="html.server.buddylist"
               href="<%= href %>"
               componentRight="<%= componentRight %>"/>


    <%  href =  "javascript:aam_relocate('navigation.do?" + IRequestKeys.TARGET + "=lockservice&" + IRequestKeys.TITLE_KEY + "=html.administration.objectsinwork&" + IRequestKeys.WINDOW_ID +"=" + UIEnvironmentManager.get().getId() + "')";
        componentRight = ComponentRight.getRight("view.object.in.process") ; %>
    <gui:field propertyKey="html.administration.objectsinwork"
               href="<%= href %>"
               componentRight="<%= componentRight %>"/>

    <%  href =  "javascript:aam_relocate('navigation.do?"+ IRequestKeys.TARGET + "=serverstate&" + IRequestKeys.TITLE_KEY + "=html.administration.serverstate.DBI&" + IRequestKeys.WINDOW_ID +"=" + UIEnvironmentManager.get().getId() + "')"; %>
    <gui:field propertyKey="html.administration.serverstate.DBI"
               hasRole="<%= new IEnumerationItem[] { Enumerations.USERROLE_TYPE.SYSADMIN }%>"
               href="<%= href %>"/>

</gui:groupbox>

<gui:groupbox titleKey="html.administration.group.importexport"
              helpId="ARCM_ADMINISTRATION_IMPORTEXPORT.HLP"  isContentAnUnorderedList="true">

    <%  href =  "javascript:aam_relocate('forward.do?"+ IRequestKeys.TARGET +"=/aamml/importfile_upload_dialog.jsp&"+ IRequestKeys.TITLE_KEY+"=html.server.import.uploaddialog.header&" + IRequestKeys.WINDOW_ID +"=" + UIEnvironmentManager.get().getId() + "')";
        componentRight = ComponentRight.getRight("view.importexport") ; %>
    <gui:field propertyKey="html.server.import.data" addSeparator="true"
               hasButton="true"
               buttonTextKey="html.server.import.data.BTN"
               href="<%= href %>" componentRight="<%= componentRight %>"/>

    <%  href =  "javascript:aam_relocate('forward.do?"+ IRequestKeys.TARGET +"=/aamml/export_dialog.jsp&"+ IRequestKeys.TITLE_KEY+"=html.server.export.data&" + IRequestKeys.WINDOW_ID +"=" + UIEnvironmentManager.get().getId() + "')";
        componentRight = ComponentRight.getRight("view.importexport") ; %>
    <gui:field propertyKey="html.server.export.data"
               hasButton="true"
               buttonTextKey="html.server.export.data.BTN"
               href="<%= href %>" componentRight="<%= componentRight %>"/>

    <%  href = "javascript:aam_relocate('forward.do?"+ IRequestKeys.TARGET +"=/aamml/snapshot_db_dialog.jsp&snapshot=true&"+ IRequestKeys.TITLE_KEY+"=html.server.backup.database&" + IRequestKeys.WINDOW_ID +"=" + UIEnvironmentManager.get().getId() + "')";
        componentRight = ComponentRight.getRight("view.importexport"); %>
    <gui:field propertyKey="html.server.backup.database"
               hasButton="true"
               buttonTextKey="html.server.backup.database.BTN"
               href="<%= href %>" addSeparator="true" componentRight="<%= componentRight %>"/>

    <%  componentRight = ComponentRight.getRight("view.mashzone.url.builder") ;
     href =  "javascript:aam_relocate('mashzoneintegration.do?" + IRequestKeys.WINDOW_ID +"=" + UIEnvironmentManager.get().getId() + "')"; %>
    <gui:field propertyKey="html.mashzone.integration.DBI"
               componentRight="<%= componentRight %>"
               href="<%= href %>" />

</gui:groupbox>
</gui:column>

</gui:page>
</div>
<jsp:include page="/include/wait.jsp" />
<form action="administration.jsp" method="post" style="display:none;"></form>
</body>
<script type="text/javascript">
    setModule('administration');
    <aam:toolBar  />
</script>
</html>
