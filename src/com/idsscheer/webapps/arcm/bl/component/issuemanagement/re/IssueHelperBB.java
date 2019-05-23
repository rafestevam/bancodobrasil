/**
 * 
 */
package com.idsscheer.webapps.arcm.bl.component.issuemanagement.re;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;

import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.common.support.AppObjComparator;
import com.idsscheer.webapps.arcm.bl.common.support.AppObjUtility;
import com.idsscheer.webapps.arcm.bl.component.common.support.ArcmHelperBB;
import com.idsscheer.webapps.arcm.bl.component.common.support.UsergroupUtilityBB;
import com.idsscheer.webapps.arcm.bl.exception.BLInternalException;
import com.idsscheer.webapps.arcm.bl.models.filter.attribute.impl.issuemanagement.IIssueApproverGenericFilterAttributeBB;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IUsergroupAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IBooleanAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IDateAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IEnumAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IListAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.QueryOrder;
import com.idsscheer.webapps.arcm.bl.navigation.stack.IBreadcrumb;
import com.idsscheer.webapps.arcm.bl.navigation.stack.IBreadcrumbStack;
import com.idsscheer.webapps.arcm.bl.navigation.stack.IFormBreadcrumb;
import com.idsscheer.webapps.arcm.bl.re.ext.MessageInformation;
import com.idsscheer.webapps.arcm.bl.re.ext.RuleAppObj;
import com.idsscheer.webapps.arcm.bl.re.ext.StateInformation;
import com.idsscheer.webapps.arcm.bl.re.impl.REEnvironment;
import com.idsscheer.webapps.arcm.common.constants.metadata.Enumerations;
import com.idsscheer.webapps.arcm.common.constants.metadata.EnumerationsBB;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IDocumentAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IHierarchyAttributeTypeBB;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IIssueAttributeTypeBB;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IObjectAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IUsergroupAttributeTypeBB;
import com.idsscheer.webapps.arcm.common.util.ARCMCollections;
import com.idsscheer.webapps.arcm.common.util.ovid.IOVID;
import com.idsscheer.webapps.arcm.config.Metadata;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IListAttributeType;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IObjectType;
import com.idsscheer.webapps.arcm.config.metadata.rights.roles.RoleLevel;
import com.idsscheer.webapps.arcm.config.metadata.workflow.IStateMetadata;
import com.idsscheer.webapps.arcm.ui.framework.common.UIEnvironmentManager;

/**
 * <p>
 * Used by the rule engine in 'issue_dealingdeadline.drl', 'issue_mgmtappl2_dealingdeadline.drl' and 'issue_base.dsl'.
 * </p>
 * 
 * <p> This is a customization for <b><code>Banco do Brasil Project</code></b> </p>
 *@author brmob
 *@since v20150302-CUST-IssueMgmt
 *@see IssueHelper
 */
public class IssueHelperBB extends IssueHelper {
	
	public static void executeDealingDeadlineRules(){
		REEnvironment env = REEnvironment.getInstance();
		RuleAppObj issue = env.getRuleAppObj();
		IUserContext userContext = env.getUserContext();
		
		IListAttribute issueRelateObjectAttr = (IListAttribute) issue.getAttribute(IIssueAttributeTypeBB.LIST_ISSUERELEVANTOBJECTS.getId());
		List<IAppObj> issueRelatedObjects = issueRelateObjectAttr.getElements(getFullReadAccessUserContext());
		
		Boolean hasExecutableAsIRO = Boolean.FALSE;
		
		StateInformation stateInformation = env.getStateInformation(); 
		MessageInformation messageInformation = env.getMessageInformation();
		String issueTypeText = Metadata.getMetadata().getResourceBundle(
				getFullReadAccessUserContext().getLanguage()).getString("enumeration.bb_issueType.dealing_deadline.DBI");
		
		for (IAppObj issueRelatedObject : issueRelatedObjects) {
			IObjectType iroObjectType = issueRelatedObject.getObjectType();
			
			if (iroObjectType.equals(ObjectType.ISSUE)) {
				IEnumerationItem issueType = ARCMCollections.extractSingleEntry(
						issueRelatedObject.getAttribute(IIssueAttributeTypeBB.ATTR_ISSUETYPE).getRawValue(), true);
				if (issueType.equals(EnumerationsBB.ISSUETYPE.TECH_RECOMMENDATION) || issueType.equals(EnumerationsBB.ISSUETYPE.ACTION)) {
					hasExecutableAsIRO = true;
					
					/*
					 * This code reflects the following rule:
					 * It's not possible to create a RP for an Action or a RT once the IRO has another RP still pending to be finished.
					 */
					
					List<IAppObj> iROParents = issueRelatedObject.getParentAppObjs(getFullReadAccessUserContext(), 
							IIssueAttributeTypeBB.LIST_ISSUERELEVANTOBJECTS, QueryOrder.ascending(IIssueAttributeTypeBB.BASE_ATTR_OBJ_ID));
					
					for (IAppObj iROParent : iROParents) {
						IObjectType iROParentObjectType = iROParent.getObjectType();
						if (iROParentObjectType.equals(ObjectType.ISSUE)) {
							IEnumerationItem iroParentIssueType = ARCMCollections.extractSingleEntry(iROParent.getAttribute(IIssueAttributeTypeBB.ATTR_ISSUETYPE).getRawValue(), true);
							
							if (iroParentIssueType.equals(EnumerationsBB.ISSUETYPE.DEALING_DEADLINE)) {
								
								AppObjComparator<IAppObj> comparator = new AppObjComparator<IAppObj>(IObjectAttributeType.BASE_ATTR_OBJ_ID, userContext.getLanguage());
								if (comparator.compare(issue.getAppObj(), iROParent) == 0) {
									continue;
								}
									
								IEnumerationItem iroParentStateTime = ARCMCollections.extractSingleEntry(iROParent.getAttribute(IIssueAttributeTypeBB.ATTR_STATETIME).getRawValue(),true);
								
								String issueRelevantObjectViolatedRule = null;
								
								if (!iroParentStateTime.equals(EnumerationsBB.ISSUESTATETIME_BB.FINISHED)) {
									
									stateInformation.setValid(IIssueAttributeTypeBB.LIST_ISSUERELEVANTOBJECTS.getId(), false);
									
									String iROParentId = String.valueOf(iROParent.getObjectId());
									String iROParentName = iROParent.getAttribute(IIssueAttributeTypeBB.ATTR_NAME).getPersistentRawValue();
									
									issueRelevantObjectViolatedRule = Metadata.getMetadata().getResourceBundle(
											getFullReadAccessUserContext().getLanguage()).getString("error.issuerelevantobject.anothernotfinished.deadling_deadline.ERR", iROParentId, iROParentName);
									
									messageInformation.addErrorMessage(IIssueAttributeTypeBB.LIST_ISSUERELEVANTOBJECTS.getId(),
											"error.issuerelevantobject.has.not.specific.element.ERR", issueTypeText,issueRelevantObjectViolatedRule);
									
									break;
								}
							}
						}
					}
					
					/*
					 * This code is checking if attachments are required based on the IRO's priority attribute:
					 * ISSUEPRIORITY.HIGH or ISSUEPRIORITY.MEDIUM
					 * ISSUEPRIORITY.LOW and (issuePlannedDate (deadline) - IROFirstPlannedEndDate >= 120)
					 */
					stateInformation.setMandatory(IIssueAttributeTypeBB.LIST_DOCUMENTS.getId(), false);
					
					IEnumerationItem priority = ARCMCollections.extractSingleEntry(
							issueRelatedObject.getAttribute(IIssueAttributeTypeBB.ATTR_PRIORITY).getRawValue(), true);
					
					IDateAttribute issuePlannedEndDateAttr = (IDateAttribute) issue.getAttribute(IIssueAttributeTypeBB.ATTR_PLANNEDENDDATE.getId());
					Date issuePlannedEndDate = issuePlannedEndDateAttr.getRawValue();
					
					IDateAttribute iROFirstPlannedEndDateAttr = issueRelatedObject.getAttribute(IIssueAttributeTypeBB.ATTR_FIRST_PLANNED_ENDDATE);
					Date iROFirstPlannedEndDate = iROFirstPlannedEndDateAttr.getRawValue();
					
					IListAttribute issueDocumentsAttr = (IListAttribute) issue.getAttribute(IIssueAttributeTypeBB.LIST_DOCUMENTS.getId());
					List<IAppObj> issueDocuments = issueDocumentsAttr.getElements(getFullReadAccessUserContext());
					
					String issueDocumentsViolatedRule = null;
					if (priority.equals(Enumerations.ISSUEPRIORITY.HIGH) || priority.equals(Enumerations.ISSUEPRIORITY.MEDIUM)) {
						
						stateInformation.setMandatory(IIssueAttributeTypeBB.LIST_DOCUMENTS.getId(), true);
						validateMandatoryDocuments(issue, stateInformation, messageInformation, issueTypeText, IIssueAttributeTypeBB.LIST_DOCUMENTS);
						
						if (issueDocuments.isEmpty()) {
							stateInformation.setValid(IIssueAttributeTypeBB.LIST_DOCUMENTS.getId(), false);
							
							issueDocumentsViolatedRule = Metadata.getMetadata().getResourceBundle(
									getFullReadAccessUserContext().getLanguage()).getString(
											"error.issuerelevantobject.deadling_deadline.priority.ab.ERR");
							
							messageInformation.addErrorMessage(issueDocumentsAttr.getAttributeType().getId(),
									"error.issuerelevantobject.has.not.specific.element.ERR", issueTypeText,issueDocumentsViolatedRule);
						}
						
					} else if (priority.equals(Enumerations.ISSUEPRIORITY.LOW) && issuePlannedEndDate != null) {
						long differenceInDays = calculateDifferenceInDaysBetweenDates(issuePlannedEndDate, iROFirstPlannedEndDate);
						if (differenceInDays > 120) {
							
							stateInformation.setMandatory(IIssueAttributeTypeBB.LIST_DOCUMENTS.getId(), true);
							validateMandatoryDocuments(issue, stateInformation, messageInformation, issueTypeText, IIssueAttributeTypeBB.LIST_DOCUMENTS);
							
							if (issueDocuments.isEmpty()) {
								stateInformation.setValid(IIssueAttributeTypeBB.LIST_DOCUMENTS.getId(), false);
								
								issueDocumentsViolatedRule = Metadata.getMetadata().getResourceBundle(
										getFullReadAccessUserContext().getLanguage()).getString(
												"error.issuerelevantobject.deadling_deadline.priority.c.ERR");
								
								messageInformation.addErrorMessage(issueDocumentsAttr.getAttributeType().getId(),
										"error.issuerelevantobject.has.not.specific.element.ERR", issueTypeText,issueDocumentsViolatedRule);
							}
						} 
					}
					
					/*
					 * This code reflects the following rule:
					 * It's only allowed to create a dealing deadline issue when the IRO's stateTime attribute isn't in Planning or Finished neither.
					 */
					IEnumerationItem iROStateTime = ARCMCollections.extractSingleEntry(issueRelatedObject.getAttribute(IIssueAttributeTypeBB.ATTR_STATETIME).getPersistentRawValue(), false);
					String issueStateTimeViolatedRule = null;
					if (EnumerationsBB.ISSUESTATETIME_BB.PLANNING.equals(iROStateTime)) {
						stateInformation.setValid(IIssueAttributeTypeBB.LIST_ISSUERELEVANTOBJECTS.getId(), false);
						
						issueStateTimeViolatedRule = Metadata.getMetadata().getResourceBundle(
								getFullReadAccessUserContext().getLanguage()).getString(
										"error.issuerelevantobject.deadling_deadline.statetime_planning.ERR");
						
						messageInformation.addErrorMessage(issueRelateObjectAttr.getAttributeType().getId(),
								"error.issuerelevantobject.has.not.specific.element.ERR", issueTypeText,issueStateTimeViolatedRule);
					} else if (EnumerationsBB.ISSUESTATETIME_BB.FINISHED.equals(iROStateTime) || EnumerationsBB.ISSUESTATETIME_BB.INJURED.equals(iROStateTime)) {
						stateInformation.setValid(IIssueAttributeTypeBB.LIST_ISSUERELEVANTOBJECTS.getId(), false);
						
						issueStateTimeViolatedRule = Metadata.getMetadata().getResourceBundle(
								getFullReadAccessUserContext().getLanguage()).getString(
										"error.issuerelevantobject.deadling_deadline.statetime_finished.ERR");
						
						messageInformation.addErrorMessage(issueRelateObjectAttr.getAttributeType().getId(),
								"error.issuerelevantobject.has.not.specific.element.ERR", issueTypeText,issueStateTimeViolatedRule);
					}
					
					/*
					 * This code reflects the following rule:
					 * It's only possible to create dealing deadlines when it has the attribute plannedenddate grater than the IRO's plannedenddate attribute.
					 */
					String issuePlannedEndDateViolatedRule = null;
					
					IDateAttribute iROPlannedEndDateAttr = issueRelatedObject.getAttribute(IIssueAttributeTypeBB.ATTR_PLANNEDENDDATE);
					Date iROPlannedEndDate = iROPlannedEndDateAttr.getRawValue();
					
					if (issuePlannedEndDate!= null && calculateDifferenceInDaysBetweenDates(issuePlannedEndDate, iROPlannedEndDate) == -1) {
						stateInformation.setValid(IIssueAttributeTypeBB.ATTR_PLANNEDENDDATE.getId(), false);
						
						issuePlannedEndDateViolatedRule = Metadata.getMetadata().getResourceBundle(
								getFullReadAccessUserContext().getLanguage()).getString(
										"error.issuerelevantobject.deadling_deadline.plannedenddate.ERR");
						
						
						messageInformation.addErrorMessage(IIssueAttributeTypeBB.ATTR_PLANNEDENDDATE.getId(), 
								"error.issuerelevantobject.has.not.specific.element.ERR", issueTypeText, issuePlannedEndDateViolatedRule);
					}
					
					/*
					 * This code reflects the following rule:
					 * A user is only allowed to create issue 'Dealing deadline' when he is member of the defined IRO's Implementation L1 Group(s) or
					 * if is member of the DICOI area, according to the Issue #123499
					 */
					IAppObj issueCreatorUserGroup = UsergroupUtilityBB.getIssueCreatorUserGroup(userContext);
					if (issueCreatorUserGroup != null) 	{						
						IListAttribute issueCreatorOrganizationalUnitAttr = issueCreatorUserGroup.getAttribute(IUsergroupAttributeTypeBB.LIST_BB_ORGUNIT);
						IAppObj issueCreatorOrganizationalUnit = ARCMCollections.extractSingleEntry(issueCreatorOrganizationalUnitAttr.getElements(getFullReadAccessUserContext()), true);
						
						if (issueCreatorOrganizationalUnit.getAttribute(IHierarchyAttributeTypeBB.ATTR_CODIGO_UOR).getPersistentRawValue().equals("000019955")){
							continue;
						}
					}
					
					IListAttribute iROImplApproverL1GroupAttr = issueRelatedObject.getAttribute(IIssueAttributeTypeBB.LIST_IMPL_APPROVERL1_GROUP);
					
					if (!iROImplApproverL1GroupAttr.isEmpty()) {
						IAppObj iROImplApproverL1Group = ARCMCollections.extractSingleEntry(iROImplApproverL1GroupAttr.getElements(getFullReadAccessUserContext()), true);
						List<String> clientSigns = ARCMCollections.createList(AppObjUtility.getRelatedClientSign(iROImplApproverL1Group));
						List<IAppObj> allUserGroupsofCurrentUser = UsergroupUtilityBB.getUsergroupsOfUser(userContext.getUser().getObjectId(), EnumerationsBB.USERROLE_TYPE_BB.ISSUEIMPLAPPROVER_L1, Enumerations.USERROLE_LEVEL.OBJECT, clientSigns);
						
						boolean creatorBelongsToIROImplApproverL1 = allUserGroupsofCurrentUser.contains(iROImplApproverL1Group);
						
						if (!creatorBelongsToIROImplApproverL1) {
							stateInformation.setValid(IIssueAttributeTypeBB.LIST_ISSUERELEVANTOBJECTS.getId(), false);
							
							String issueImplApproverL1ViolatedRule = Metadata.getMetadata().getResourceBundle(
									getFullReadAccessUserContext().getLanguage()).getString(
											"error.issuerelevantobject.deadling_deadline.impl_approverL1.ERR");
							
							messageInformation.addErrorMessage(issueRelateObjectAttr.getAttributeType().getId(),
									"error.issuerelevantobject.has.not.specific.element.ERR", issueTypeText,issueImplApproverL1ViolatedRule);
						}
					}
					
				}
			}
		}
		
		if (issueRelatedObjects.size() > 0 && !hasExecutableAsIRO) {
			
			if (issueRelateObjectAttr.isValid()) {
				stateInformation.setValid(IIssueAttributeTypeBB.LIST_ISSUERELEVANTOBJECTS.getId(), false);
				
				String iROViolatedRule = Metadata.getMetadata().getResourceBundle(
						getFullReadAccessUserContext().getLanguage()).getString("error.issuerelevantobject.deadling_deadline.rules.ERR");
				
				messageInformation.addErrorMessage(issueRelateObjectAttr.getAttributeType().getId(),
						"error.issuerelevantobject.has.not.specific.element.ERR", issueTypeText,iROViolatedRule);
			}
		}
		
		
		
	}
	
	public static void executeFaultLogRules(){
		REEnvironment env = REEnvironment.getInstance();
		RuleAppObj issue = env.getRuleAppObj();
		IUserContext userContext = env.getUserContext();
		
		StateInformation stateInformation = env.getStateInformation(); 
		MessageInformation messageInformation = env.getMessageInformation();
		
		String issueTypeText = Metadata.getMetadata().getResourceBundle(
				getFullReadAccessUserContext().getLanguage()).getString("enumeration.bb_issueType.faultlog.DBI");
		
		validateIssueCreatorByIssueType(issue, stateInformation, messageInformation, issueTypeText, userContext);
			
		/*
		 * For the Fault log is only allowed to create if:
		 * MGMT_APPROVERL1_DEPARTMENTCODE = MGMT_APPROVERL2_DEPARTMENTCODE
		 */
		checkInvalidExecutableMgmtApprovers(issue, stateInformation, messageInformation, issueTypeText);
		
		/*
		 * For the Fault log is only allowed to create if:
		 * MGMT_APPROVERL1_DEPARTMENTCODE != IMPL_APPROVERL1_DEPARTMENTCODE
		 */
		checkInvalidFaultLogApprovers(issue, stateInformation, messageInformation, issueTypeText);
		
		/* BdB has requested to desable it via #132296 
		validateMandatoryDocuments(issue, stateInformation, messageInformation, issueTypeText, IIssueAttributeTypeBB.LIST_DOCUMENTS);*/
	}
	
	public static void executeActionRules(){
		REEnvironment env = REEnvironment.getInstance();
		RuleAppObj issue = env.getRuleAppObj();
		IUserContext userContext = env.getUserContext();
		
		StateInformation stateInformation = env.getStateInformation(); 
		MessageInformation messageInformation = env.getMessageInformation();
		String issueTypeText = Metadata.getMetadata().getResourceBundle(
				getFullReadAccessUserContext().getLanguage()).getString("enumeration.bb_issueType.action.DBI");
		
		if (isInWorkflowState(IStateMetadata.Type.PREPARED.name(), issue) || isInWorkflowState("openForCreation", issue)) {
			
			validateIssueCreatorByIssueType(issue, stateInformation, messageInformation, issueTypeText, userContext);
			
			validateIssueCreatorBelongsToIROIssueOwners(issue, stateInformation, messageInformation, issueTypeText, userContext);
			
			/*
			 * For Actions is only allowed to create if:
			 * MGMT_APPROVERL1_DEPARTMENTCODE = MGMT_APPROVERL2_DEPARTMENTCODE
			 */
			checkInvalidExecutableMgmtApprovers(issue, stateInformation, messageInformation, issueTypeText);
			
			/*
			 * For Actions is only allowed to create if:
			 * IMPL_APPROVERL1_DEPARTMENTCODE = IMPL_APPROVERL2_DEPARTMENTCODE
			 */
			checkIvalidExecutableImplApprovers(issue, stateInformation, messageInformation, issueTypeText);
			
			/*
			 * For Actions is only allowed to create whether:
			 * IMPL_APPROVERL1_DEPARTMENTCODE = OWNERS_DEPARTMENTCODE
			 */
			checkInvalidExecutableOwners(issue, stateInformation, messageInformation, issueTypeText);
			
			/*
			 * For Actions is only allowed to create whether:
			 * MGMT_APPROVERL1_DEPARTMENTCODE = REVIEWERS_DEPARTMENTCODE
			 */
			checkInvalidExecutableReviewers(issue, stateInformation, messageInformation, issueTypeText);
			
			/* BdB has requested to desable it via #132296 
			validateMandatoryDocuments(issue, stateInformation, messageInformation, issueTypeText, IIssueAttributeTypeBB.LIST_DOCUMENTS); */
			
			validateActionIro(issue, stateInformation, messageInformation, issueTypeText);
		} else if (isInWorkflowState("openForCreationActionImplApprL1", issue)) {
			/*
			 * For Actions is only allowed to create if:
			 * TECH_APPROVERL1_DEPARTMENTCODE = TECH_APPROVERL2_DEPARTMENTCODE
			 * IMPL_APPROVERS_DEPARTMENTCODE != TECH_APPROVERS_DEPARTMENTCODE
			 */
			checkInvalidExecutableTechApprovers(issue, stateInformation, messageInformation, issueTypeText);
		}
	}
	
	public static void executeTechRecommendationRules(){
		REEnvironment env = REEnvironment.getInstance();
		RuleAppObj issue = env.getRuleAppObj();
		IUserContext userContext = env.getUserContext();
		
		StateInformation stateInformation = env.getStateInformation(); 
		MessageInformation messageInformation = env.getMessageInformation();
		String issueTypeText = Metadata.getMetadata().getResourceBundle(
				getFullReadAccessUserContext().getLanguage()).getString("enumeration.bb_issueType.tech_recommendation.DBI");
		
		validateIssueCreatorByIssueType(issue, stateInformation, messageInformation, issueTypeText, userContext);
		
		/*
		 * For Tech recommendations is only allowed to create if:
		 * MGMT_APPROVERL1_DEPARTMENTCODE = MGMT_APPROVERL2_DEPARTMENTCODE
		 */
		checkInvalidExecutableMgmtApprovers(issue, stateInformation, messageInformation, issueTypeText);
		
		/*
		 * For Tech recommendations is only allowed to create if:
		 * IMPL_APPROVERL1_DEPARTMENTCODE = IMPL_APPROVERL2_DEPARTMENTCODE
		 */
		checkIvalidExecutableImplApprovers(issue, stateInformation, messageInformation, issueTypeText);
		
		/*
		 * For Tech recommendations is only allowed to create if:
		 * TECH_APPROVERL1_DEPARTMENTCODE = TECH_APPROVERL2_DEPARTMENTCODE
		 * IMPL_APPROVERS_DEPARTMENTCODE != TECH_APPROVERS_DEPARTMENTCODE
		 */
		checkInvalidExecutableTechApprovers(issue, stateInformation, messageInformation, issueTypeText);
		
		/*
		 * For Tech recommendations is only allowed to create whether:
		 * IMPL_APPROVERL1_DEPARTMENTCODE = OWNERS_DEPARTMENTCODE
		 */
		checkInvalidExecutableOwners(issue, stateInformation, messageInformation, issueTypeText);
		
		/*
		 * For Tech recommendations is only allowed to create whether:
		 * MGMT_APPROVERL1_DEPARTMENTCODE = REVIEWERS_DEPARTMENTCODE
		 */
		checkInvalidExecutableReviewers(issue, stateInformation, messageInformation, issueTypeText);
		
		/* BdB has requested to desable it via #132296 
		validateMandatoryDocuments(issue, stateInformation, messageInformation, issueTypeText, IIssueAttributeTypeBB.LIST_DOCUMENTS); */
	}
	
	public static void executeSADRules(){
		REEnvironment env = REEnvironment.getInstance();
		RuleAppObj issue = env.getRuleAppObj();
		IUserContext userContext = env.getUserContext();
		
		StateInformation stateInformation = env.getStateInformation(); 
		MessageInformation messageInformation = env.getMessageInformation();
		String issueTypeText = Metadata.getMetadata().getResourceBundle(
				getFullReadAccessUserContext().getLanguage()).getString("enumeration.bb_issueType.sad.DBI");
		
		validateIssueCreatorByIssueType(issue, stateInformation, messageInformation, issueTypeText, userContext);
		
		/*
		 * Enhancement #111887 - Explorer/Meus apontamentos gerados - Objetos relevantes para apontamento
		 * 
		 * Nesse Enhancement foi solicitado a retirada dessa validação
		 * 
		 * Boolean hasNotDeficiencyAsIRO = Boolean.FALSE;
		
		IListAttribute issueRelateObjectAttr = (IListAttribute) issue.getAttribute(IIssueAttributeTypeBB.LIST_ISSUERELEVANTOBJECTS.getId());
		List<IAppObj> issueRelatedObjects = issueRelateObjectAttr.getElements(getFullReadAccessUserContext());
		
		for (IAppObj issueRelatedObject : issueRelatedObjects) {
			IObjectType iROObjectType = issueRelatedObject.getObjectType();
			
			if (!iROObjectType.equals(ObjectType.DEFICIENCY)) {
				hasNotDeficiencyAsIRO = Boolean.TRUE;
				break;
			}
		}
		
		if (hasNotDeficiencyAsIRO) {
			stateInformation.setValid(IIssueAttributeTypeBB.LIST_ISSUERELEVANTOBJECTS.getId(), false);
			
			String iROAsDeficiencyViolatedRule = Metadata.getMetadata().getResourceBundle(
					getFullReadAccessUserContext().getLanguage()).getString("error.issuerelevantobject.sad.rules.ERR");
			
			messageInformation.addErrorMessage(issueRelateObjectAttr.getAttributeType().getId(),
					"error.issuerelevantobject.has.not.specific.element.ERR", issueTypeText,iROAsDeficiencyViolatedRule);
		}*/
		
	}
	
	public static void executeManifestationRules(){
		
		REEnvironment env = REEnvironment.getInstance();
		RuleAppObj issue = env.getRuleAppObj();
		
		StateInformation stateInformation = env.getStateInformation(); 
		MessageInformation messageInformation = env.getMessageInformation();
		String issueTypeText = Metadata.getMetadata().getResourceBundle(
				getFullReadAccessUserContext().getLanguage()).getString("enumeration.bb_issueType.manifestation.DBI");
		
		/*
		 * For the Manifestations is only allowed to create if:
		 * MGMT_APPROVERL1_DEPARTMENTCODE != IMPL_APPROVERL1_DEPARTMENTCODE
		 */
		checkInvalidManifestationApprovers(issue, stateInformation, messageInformation, issueTypeText);
		
		/* BdB has requested to desable it via #132296 
		validateMandatoryDocuments(issue, stateInformation, messageInformation, issueTypeText, IIssueAttributeTypeBB.LIST_DOCUMENTS); */
	}
	
	public static void executeExtrapolationRules(){
		REEnvironment env = REEnvironment.getInstance();
		RuleAppObj issue = env.getRuleAppObj();
		IUserContext userContext = env.getUserContext();
		
		StateInformation stateInformation = env.getStateInformation(); 
		MessageInformation messageInformation = env.getMessageInformation();
		String issueTypeText = Metadata.getMetadata().getResourceBundle(
				getFullReadAccessUserContext().getLanguage()).getString("enumeration.bb_issueType.extra_sheet.DBI");
		
		validateIssueCreatorByIssueType(issue, stateInformation, messageInformation, issueTypeText, userContext);
		
		/*
		 * For the Extrapolation sheets is only allowed to create if:
		 * MGMT_APPROVERL1_DEPARTMENTCODE != IMPL_APPROVERL1_DEPARTMENTCODE
		 */
		checkInvalidApprovers(issue, stateInformation, messageInformation, issueTypeText, IIssueAttributeTypeBB.LIST_MGMT_APPROVERL1_GROUP, 
				IIssueAttributeTypeBB.LIST_IMPL_APPROVERL2_GROUP);
		
		/*
		 * For Extrapolation sheet is only allowed to create whether:
		 * IMPL_APPROVERL1_DEPARTMENTCODE = OWNERS_DEPARTMENTCODE
		 *
		 * 15.08.2016 - This rule is deprecated once there is no execution phase for extrapolation anymore.
		checkInvalidExecutableOwners(issue, stateInformation, messageInformation, issueTypeText); */
		
		/*
		 * For Extrapolation sheet is only allowed to create whether:
		 * MGMT_APPROVERL1_DEPARTMENTCODE = REVIEWERS_DEPARTMENTCODE
		 * 15.08.2016 - This rule is deprecated once there is no execution phase for extrapolation anymore.
		checkInvalidExecutableReviewers(issue, stateInformation, messageInformation, issueTypeText); */
		
		/* BdB has requested to desable it via #132296 
		validateMandatoryDocuments(issue, stateInformation, messageInformation, issueTypeText, IIssueAttributeTypeBB.LIST_DOCUMENTS); */
		
	}
	
	private static void checkInvalidApprovers(RuleAppObj issue, StateInformation stateInformation, MessageInformation messageInformation, 
			String issueTypeText, IListAttributeType firstApproverGroupAttrType, IListAttributeType secondApproverGroupAttrType){
		
		IListAttribute firstApproverGroupAttr = (IListAttribute) issue.getAttribute(firstApproverGroupAttrType.getId());
		List<IAppObj> firstApproverGroupList = firstApproverGroupAttr.getElements(getFullReadAccessUserContext());
		
		IListAttribute secondApproverGroupAttr = (IListAttribute) issue.getAttribute(secondApproverGroupAttrType.getId());
		List<IAppObj> secondApproverGroupList = secondApproverGroupAttr.getElements(getFullReadAccessUserContext());
		
		if (!CollectionUtils.isEmpty(firstApproverGroupList) && !CollectionUtils.isEmpty(secondApproverGroupList)) {
			IAppObj firstApproverGroup = ARCMCollections.extractSingleEntry(firstApproverGroupList, true);
			IAppObj secondApproverGroup = ARCMCollections.extractSingleEntry(secondApproverGroupList, true);
			
			
			if (checkSameOrganizationalUnitByUserGroups(firstApproverGroup, secondApproverGroup)) {
				stateInformation.setValid(firstApproverGroupAttrType.getId(), false);
				stateInformation.setValid(secondApproverGroupAttrType.getId(), false);
				
				String firstApprGroupLabel = Metadata.getMetadata().getResourceBundle(
						getFullReadAccessUserContext().getLanguage()).getString(firstApproverGroupAttrType.getPropertyKey());
				
				String secondApprGroupLabel = Metadata.getMetadata().getResourceBundle(
						getFullReadAccessUserContext().getLanguage()).getString(secondApproverGroupAttrType.getPropertyKey());
				
				String issueApproversEqualViolatedRule = Metadata.getMetadata().getResourceBundle(
						getFullReadAccessUserContext().getLanguage()).getString("error.issue.generic.approvers_equal.ERR", 
								firstApprGroupLabel, secondApprGroupLabel);
				
				messageInformation.addErrorMessage(firstApproverGroupAttrType.getId(),
						"error.issuerelevantobject.has.not.specific.element.ERR", issueTypeText,issueApproversEqualViolatedRule);
				messageInformation.addErrorMessage(secondApproverGroupAttrType.getId(),
						"error.issuerelevantobject.has.not.specific.element.ERR", issueTypeText,issueApproversEqualViolatedRule);
			}
		}
	}
	
	private static void checkInvalidManifestationApprovers(RuleAppObj issue, StateInformation stateInformation, MessageInformation messageInformation,
			String issueTypeText){
		
		IListAttribute mgmtApproverL1GroupAttr = (IListAttribute) issue.getAttribute(IIssueAttributeTypeBB.LIST_MGMT_APPROVERL1_GROUP.getId());
		List<IAppObj> mgmtApproverL1GroupList = mgmtApproverL1GroupAttr.getElements(getFullReadAccessUserContext());
		
		IListAttribute implApproverL1GroupAttr = (IListAttribute) issue.getAttribute(IIssueAttributeTypeBB.LIST_IMPL_APPROVERL1_GROUP.getId());
		List<IAppObj> implApproverL1GroupList = implApproverL1GroupAttr.getElements(getFullReadAccessUserContext());
		
		if (!CollectionUtils.isEmpty(mgmtApproverL1GroupList) && !CollectionUtils.isEmpty(implApproverL1GroupList)) {
			IAppObj mgmtApproverL1Group = ARCMCollections.extractSingleEntry(mgmtApproverL1GroupList, true);
			IAppObj implApproverL1Group = ARCMCollections.extractSingleEntry(implApproverL1GroupList, true);
			
			if (checkSameOrganizationalUnitByUserGroups(mgmtApproverL1Group, implApproverL1Group)) {
				stateInformation.setValid(IIssueAttributeTypeBB.LIST_MGMT_APPROVERL1_GROUP.getId(), false);
				stateInformation.setValid(IIssueAttributeTypeBB.LIST_IMPL_APPROVERL1_GROUP.getId(), false);
				
				String issueMgmtAndTechApproversViolatedRule = Metadata.getMetadata().getResourceBundle(
						getFullReadAccessUserContext().getLanguage()).getString("error.issue.mgmtimpl_equal.ERR");
				
				messageInformation.addErrorMessage(IIssueAttributeTypeBB.LIST_MGMT_APPROVERL1_GROUP.getId(),
						"error.issuerelevantobject.has.not.specific.element.ERR", issueTypeText,issueMgmtAndTechApproversViolatedRule);
				messageInformation.addErrorMessage(IIssueAttributeTypeBB.LIST_IMPL_APPROVERL1_GROUP.getId(),
						"error.issuerelevantobject.has.not.specific.element.ERR", issueTypeText,issueMgmtAndTechApproversViolatedRule);
			}
		}
	}
	
	private static void checkInvalidFaultLogApprovers(RuleAppObj issue, StateInformation stateInformation, MessageInformation messageInformation,
			String issueTypeText){
		
		IListAttribute mgmtApproverL1GroupAttr = (IListAttribute) issue.getAttribute(IIssueAttributeTypeBB.LIST_MGMT_APPROVERL1_GROUP.getId());
		List<IAppObj> mgmtApproverL1GroupList = mgmtApproverL1GroupAttr.getElements(getFullReadAccessUserContext());
		
		IListAttribute implApproverL1GroupAttr = (IListAttribute) issue.getAttribute(IIssueAttributeTypeBB.LIST_IMPL_APPROVERL1_GROUP.getId());
		List<IAppObj> implApproverL1GroupList = implApproverL1GroupAttr.getElements(getFullReadAccessUserContext());
		
		if (!CollectionUtils.isEmpty(mgmtApproverL1GroupList) && !CollectionUtils.isEmpty(implApproverL1GroupList)) {
			IAppObj mgmtApproverL1Group = ARCMCollections.extractSingleEntry(mgmtApproverL1GroupList, true);
			IAppObj implApproverL1Group = ARCMCollections.extractSingleEntry(implApproverL1GroupList, true);
			
			if (checkSameOrganizationalUnitByUserGroups(mgmtApproverL1Group, implApproverL1Group)) {
				stateInformation.setValid(IIssueAttributeTypeBB.LIST_MGMT_APPROVERL1_GROUP.getId(), false);
				stateInformation.setValid(IIssueAttributeTypeBB.LIST_MGMT_APPROVERL2_GROUP.getId(), false);
				stateInformation.setValid(IIssueAttributeTypeBB.LIST_IMPL_APPROVERL1_GROUP.getId(), false);
				
				String issueMgmtAndTechApproversViolatedRule = Metadata.getMetadata().getResourceBundle(
						getFullReadAccessUserContext().getLanguage()).getString("error.issue.mgmtimpl_equal.ERR");
				
				messageInformation.addErrorMessage(IIssueAttributeTypeBB.LIST_MGMT_APPROVERL1_GROUP.getId(),
						"error.issuerelevantobject.has.not.specific.element.ERR", issueTypeText,issueMgmtAndTechApproversViolatedRule);
				messageInformation.addErrorMessage(IIssueAttributeTypeBB.LIST_MGMT_APPROVERL2_GROUP.getId(),
						"error.issuerelevantobject.has.not.specific.element.ERR", issueTypeText,issueMgmtAndTechApproversViolatedRule);
				messageInformation.addErrorMessage(IIssueAttributeTypeBB.LIST_IMPL_APPROVERL1_GROUP.getId(),
						"error.issuerelevantobject.has.not.specific.element.ERR", issueTypeText,issueMgmtAndTechApproversViolatedRule);
			}
		}
		
	}
	
	/**
	 * This method checks if for a specific issue type, the following rule is valid:
	 * mgmt_approverl1_group is equal to mgmt_approverl1_group
	 * 
	 * @param issue
	 * @param stateInformation
	 * @param messageInformation
	 * @param issueTypeText
	 */
	private static void checkInvalidExecutableMgmtApprovers(RuleAppObj issue, StateInformation stateInformation, MessageInformation messageInformation, 
			String issueTypeText){
		
		IListAttribute mgmtApproverL1GroupAttr = (IListAttribute) issue.getAttribute(IIssueAttributeTypeBB.LIST_MGMT_APPROVERL1_GROUP.getId());
		List<IAppObj> mgmtApproverL1GroupList = mgmtApproverL1GroupAttr.getElements(getFullReadAccessUserContext());
		
		IListAttribute mgmtApproverL2GroupAttr = (IListAttribute) issue.getAttribute(IIssueAttributeTypeBB.LIST_MGMT_APPROVERL2_GROUP.getId());
		List<IAppObj> mgmtApproverL2GroupList = mgmtApproverL2GroupAttr.getElements(getFullReadAccessUserContext());
		
		if (!CollectionUtils.isEmpty(mgmtApproverL1GroupList) && !CollectionUtils.isEmpty(mgmtApproverL2GroupList)) {
			IAppObj mgmtApproverL1Group = ARCMCollections.extractSingleEntry(mgmtApproverL1GroupList, true);
			IAppObj mgmtApproverL2Group = ARCMCollections.extractSingleEntry(mgmtApproverL2GroupList, true);
			
			
			if (!checkSameOrganizationalUnitByUserGroups(mgmtApproverL1Group, mgmtApproverL2Group)) {
				stateInformation.setValid(IIssueAttributeTypeBB.LIST_MGMT_APPROVERL1_GROUP.getId(), false);
				stateInformation.setValid(IIssueAttributeTypeBB.LIST_MGMT_APPROVERL2_GROUP.getId(), false);
				
				String issueMgmtApproverNotEqualViolatedRule = Metadata.getMetadata().getResourceBundle(
						getFullReadAccessUserContext().getLanguage()).getString("error.issue.executable.mgmt_approver_notequal.ERR");
				
				messageInformation.addErrorMessage(IIssueAttributeTypeBB.LIST_MGMT_APPROVERL1_GROUP.getId(),
						"error.issuerelevantobject.has.not.specific.element.ERR", issueTypeText,issueMgmtApproverNotEqualViolatedRule);
				messageInformation.addErrorMessage(IIssueAttributeTypeBB.LIST_MGMT_APPROVERL2_GROUP.getId(),
						"error.issuerelevantobject.has.not.specific.element.ERR", issueTypeText,issueMgmtApproverNotEqualViolatedRule);
			}
		} 
	}
	
	/**
	 * This method checks if for a specific issue type, the following rule is valid:<br>
	 * <b>impl_approverl1_group</b> is equal to <b>impl_approverl1_group</b>
	 * 
	 * @param issue
	 * @param stateInformation
	 * @param messageInformation
	 * @param issueTypeText
	 */
	private static void checkIvalidExecutableImplApprovers(RuleAppObj issue, StateInformation stateInformation, MessageInformation messageInformation, 
			String issueTypeText){
		
		IListAttribute implApproverL1GroupAttr = (IListAttribute) issue.getAttribute(IIssueAttributeTypeBB.LIST_IMPL_APPROVERL1_GROUP.getId());
		List<IAppObj> implApproverL1GroupList = implApproverL1GroupAttr.getElements(getFullReadAccessUserContext());
		
		IListAttribute implApproverL2GroupAttr = (IListAttribute) issue.getAttribute(IIssueAttributeTypeBB.LIST_IMPL_APPROVERL2_GROUP.getId()); 
		List<IAppObj> implApproverL2GroupList = implApproverL2GroupAttr.getElements(getFullReadAccessUserContext());
		
		if (!CollectionUtils.isEmpty(implApproverL1GroupList) && !CollectionUtils.isEmpty(implApproverL2GroupList)) {
			IAppObj implApproverL1Group = ARCMCollections.extractSingleEntry(implApproverL1GroupList, true);
			IAppObj implApproverL2Group = ARCMCollections.extractSingleEntry(implApproverL2GroupList, true);
			
			if (!checkSameOrganizationalUnitByUserGroups(implApproverL1Group, implApproverL2Group)) {
				stateInformation.setValid(IIssueAttributeTypeBB.LIST_IMPL_APPROVERL1_GROUP.getId(), false);
				stateInformation.setValid(IIssueAttributeTypeBB.LIST_IMPL_APPROVERL2_GROUP.getId(), false);
				
				String issueImplApproverNotEqualViolatedRule = Metadata.getMetadata().getResourceBundle(
						getFullReadAccessUserContext().getLanguage()).getString("error.issue.executable.impl_approver_notequal.ERR");
				
				messageInformation.addErrorMessage(IIssueAttributeTypeBB.LIST_IMPL_APPROVERL1_GROUP.getId(),
						"error.issuerelevantobject.has.not.specific.element.ERR", issueTypeText,issueImplApproverNotEqualViolatedRule);
				messageInformation.addErrorMessage(IIssueAttributeTypeBB.LIST_IMPL_APPROVERL2_GROUP.getId(),
						"error.issuerelevantobject.has.not.specific.element.ERR", issueTypeText,issueImplApproverNotEqualViolatedRule);
			}
		}
	}
	
	/**
	 * This method checks if for a specific issue type, the following rule is valid:<br>
	 * <b>tech_approverl1_group</b> is equal to <b>tech_approverl1_group</b><br>
	 * <b>impl_approvers</b> and <b>tech_approvers</b> are different
	 * 
	 * @param issue
	 * @param stateInformation
	 * @param messageInformation
	 * @param issueTypeText
	 */
	private static void checkInvalidExecutableTechApprovers(RuleAppObj issue, StateInformation stateInformation, MessageInformation messageInformation, 
			String issueTypeText){
		
		IBooleanAttribute isTechSupportNeededAttr = (IBooleanAttribute) issue.getAttribute(IIssueAttributeTypeBB.ATTR_ISTECHSUPPORTNEEDED.getId());
		Boolean isTechSupportNeeded = isTechSupportNeededAttr.getRawValue();
		
		if (isTechSupportNeeded) {
			IListAttribute techApproverL1GroupAttr = (IListAttribute) issue.getAttribute(IIssueAttributeTypeBB.LIST_TECH_APPROVERL1_GROUP.getId());
			List<IAppObj> techApproverL1GroupList = techApproverL1GroupAttr.getElements(getFullReadAccessUserContext());
			
			IListAttribute techApproverL2GroupAttr = (IListAttribute) issue.getAttribute(IIssueAttributeTypeBB.LIST_TECH_APPROVERL2_GROUP.getId()); 
			List<IAppObj> techApproverL2GroupList = techApproverL2GroupAttr.getElements(getFullReadAccessUserContext());
			
			
			if (!CollectionUtils.isEmpty(techApproverL1GroupList) && !CollectionUtils.isEmpty(techApproverL2GroupList)) {
				IAppObj techApproverL1Group = ARCMCollections.extractSingleEntry(techApproverL1GroupList, true);
				IAppObj techApproverL2Group = ARCMCollections.extractSingleEntry(techApproverL2GroupList, true);
				
				
				if (!checkSameOrganizationalUnitByUserGroups(techApproverL1Group, techApproverL2Group)) {
					stateInformation.setValid(IIssueAttributeTypeBB.LIST_TECH_APPROVERL1_GROUP.getId(), false);
					stateInformation.setValid(IIssueAttributeTypeBB.LIST_TECH_APPROVERL2_GROUP.getId(), false);
					
					String issueTechApproverNotEqualViolatedRule = Metadata.getMetadata().getResourceBundle(
							getFullReadAccessUserContext().getLanguage()).getString("error.issue.executable.tech_approver_notequal.ERR");
					
					messageInformation.addErrorMessage(IIssueAttributeTypeBB.LIST_TECH_APPROVERL1_GROUP.getId(),
							"error.issuerelevantobject.has.not.specific.element.ERR", issueTypeText,issueTechApproverNotEqualViolatedRule);
					messageInformation.addErrorMessage(IIssueAttributeTypeBB.LIST_TECH_APPROVERL2_GROUP.getId(),
							"error.issuerelevantobject.has.not.specific.element.ERR", issueTypeText,issueTechApproverNotEqualViolatedRule);
				} else {
					IListAttribute implApproverL1GroupAttr = (IListAttribute) issue.getAttribute(IIssueAttributeTypeBB.LIST_IMPL_APPROVERL1_GROUP.getId());
					List<IAppObj> implApproverL1GroupList = implApproverL1GroupAttr.getElements(getFullReadAccessUserContext());
					
					IListAttribute implApproverL2GroupAttr = (IListAttribute) issue.getAttribute(IIssueAttributeTypeBB.LIST_IMPL_APPROVERL2_GROUP.getId()); 
					List<IAppObj> implApproverL2GroupList = implApproverL2GroupAttr.getElements(getFullReadAccessUserContext());
					
					if (!CollectionUtils.isEmpty(implApproverL1GroupList) && !CollectionUtils.isEmpty(implApproverL2GroupList)) {
						IAppObj implApproverL1Group = ARCMCollections.extractSingleEntry(implApproverL1GroupList, true);
						IAppObj implApproverL2Group = ARCMCollections.extractSingleEntry(implApproverL2GroupList, true);
						
						if (checkSameOrganizationalUnitByUserGroups(implApproverL1Group, implApproverL2Group) && checkSameOrganizationalUnitByUserGroups(techApproverL1Group, implApproverL1Group)) {
							stateInformation.setValid(IIssueAttributeTypeBB.LIST_IMPL_APPROVERL1_GROUP.getId(), false);
							stateInformation.setValid(IIssueAttributeTypeBB.LIST_IMPL_APPROVERL2_GROUP.getId(), false);
							stateInformation.setValid(IIssueAttributeTypeBB.LIST_TECH_APPROVERL1_GROUP.getId(), false);
							stateInformation.setValid(IIssueAttributeTypeBB.LIST_TECH_APPROVERL2_GROUP.getId(), false);
							
							String issueImplAndTechApproverEqualViolatedRule = Metadata.getMetadata().getResourceBundle(
									getFullReadAccessUserContext().getLanguage()).getString(
											"error.issue.executable.impltech_approver_equal.ERR");
							
							messageInformation.addErrorMessage(IIssueAttributeTypeBB.LIST_IMPL_APPROVERL1_GROUP.getId(),
									"error.issuerelevantobject.has.not.specific.element.ERR", issueTypeText,issueImplAndTechApproverEqualViolatedRule);
							messageInformation.addErrorMessage(IIssueAttributeTypeBB.LIST_IMPL_APPROVERL2_GROUP.getId(),
									"error.issuerelevantobject.has.not.specific.element.ERR", issueTypeText,issueImplAndTechApproverEqualViolatedRule);
							messageInformation.addErrorMessage(IIssueAttributeTypeBB.LIST_TECH_APPROVERL1_GROUP.getId(),
									"error.issuerelevantobject.has.not.specific.element.ERR", issueTypeText,issueImplAndTechApproverEqualViolatedRule);
							messageInformation.addErrorMessage(IIssueAttributeTypeBB.LIST_TECH_APPROVERL2_GROUP.getId(),
									"error.issuerelevantobject.has.not.specific.element.ERR", issueTypeText,issueImplAndTechApproverEqualViolatedRule);
						}
					}
				}
			}
		}
		
	}
	
	
	/**
	 * This method checks if for a specific issue type, the following rule is valid:<br>
	 * <b>impl_approverl1_group</b> is equal to <b>owners_group</b>
	 * 
	 * @param issue
	 * @param stateInformation
	 * @param messageInformation
	 * @param issueTypeText
	 */
	private static void checkInvalidExecutableOwners(RuleAppObj issue, StateInformation stateInformation, MessageInformation messageInformation, 
			String issueTypeText){
		
		IListAttribute implApproverL1GroupAttr = (IListAttribute) issue.getAttribute(IIssueAttributeTypeBB.LIST_IMPL_APPROVERL1_GROUP.getId());
		List<IAppObj> implApproverL1GroupList = implApproverL1GroupAttr.getElements(getFullReadAccessUserContext());
		
		IListAttribute executableOwnerGroupAttr = (IListAttribute) issue.getAttribute(IIssueAttributeTypeBB.LIST_OWNERS_GROUP.getId()); 
		List<IAppObj> executableOwnerGroupList = executableOwnerGroupAttr.getElements(getFullReadAccessUserContext());
		
		if (!CollectionUtils.isEmpty(implApproverL1GroupList) && !CollectionUtils.isEmpty(executableOwnerGroupList)) {
			IAppObj implApproverL1Group = ARCMCollections.extractSingleEntry(implApproverL1GroupList, true);
			IAppObj executableOwnerGroup = ARCMCollections.extractSingleEntry(executableOwnerGroupList, true);
			
			if (!checkSameOrganizationalUnitByUserGroups(implApproverL1Group, executableOwnerGroup)) {
				stateInformation.setValid(IIssueAttributeTypeBB.LIST_OWNERS_GROUP.getId(), false);
				
				String executableOwnerNotEqualViolatedRule = Metadata.getMetadata().getResourceBundle(
						getFullReadAccessUserContext().getLanguage()).getString("error.issue.executable.owner_notequal.ERR");
				
				messageInformation.addErrorMessage(IIssueAttributeTypeBB.LIST_OWNERS_GROUP.getId(),
						"error.issuerelevantobject.has.not.specific.element.ERR", issueTypeText,executableOwnerNotEqualViolatedRule);
			}
		}
		
	}
	
	/**
	 * This method checks if for a specific issue type, the following rule is valid:<br>
	 * <b>mgmt_approverl1_group</b> is equal to <b>reviewers_group</b>
	 * 
	 * @param issue
	 * @param stateInformation
	 * @param messageInformation
	 * @param issueTypeText
	 */
	private static void checkInvalidExecutableReviewers(RuleAppObj issue, StateInformation stateInformation, MessageInformation messageInformation, 
			String issueTypeText){
		
		IListAttribute mgmtApproverL1GroupAttr = (IListAttribute) issue.getAttribute(IIssueAttributeTypeBB.LIST_MGMT_APPROVERL1_GROUP.getId());
		List<IAppObj> mgmtApproverL1GroupList = mgmtApproverL1GroupAttr.getElements(getFullReadAccessUserContext());
		
		IListAttribute executableReviewerGroupAttr = (IListAttribute) issue.getAttribute(IIssueAttributeTypeBB.LIST_REVIEWERS_GROUP.getId()); 
		List<IAppObj> executableReviewerGroupList = executableReviewerGroupAttr.getElements(getFullReadAccessUserContext());
		
		if (!CollectionUtils.isEmpty(mgmtApproverL1GroupList) && !CollectionUtils.isEmpty(executableReviewerGroupList)) {
			IAppObj mgmtApproverL1Group = ARCMCollections.extractSingleEntry(mgmtApproverL1GroupList, true);
			IAppObj executableReviewerGroup = ARCMCollections.extractSingleEntry(executableReviewerGroupList, true);
			
			if (!checkSameOrganizationalUnitByUserGroups(mgmtApproverL1Group, executableReviewerGroup)) {
				stateInformation.setValid(IIssueAttributeTypeBB.LIST_REVIEWERS_GROUP.getId(), false);
				
				String executableOwnerNotEqualViolatedRule = Metadata.getMetadata().getResourceBundle(
						getFullReadAccessUserContext().getLanguage()).getString("error.issue.executable.reviewer_notequal.ERR");
				
				messageInformation.addErrorMessage(IIssueAttributeTypeBB.LIST_REVIEWERS_GROUP.getId(),
						"error.issuerelevantobject.has.not.specific.element.ERR", issueTypeText,executableOwnerNotEqualViolatedRule);
			}
		}
		
	}
	
	/**
	 * This method checks whether the user groups are connected to the same organizational unit or not
	 * @param firstUserGroup
	 * @param secondUserGroup
	 * @return
	 */
	private static boolean checkSameOrganizationalUnitByUserGroups(IAppObj firstUserGroup, IAppObj secondUserGroup){
		
		boolean hasSameOrganizationalUnit = Boolean.FALSE;
		
		List<IAppObj> firstOrganizationalUnitList = firstUserGroup.getAttribute(IUsergroupAttributeTypeBB.LIST_BB_ORGUNIT).getElements(getFullReadAccessUserContext());
		IAppObj firstOrganizationalUnit = ARCMCollections.extractSingleEntry(firstOrganizationalUnitList, true);
		
		List<IAppObj> secondOrganizationalUnitList = secondUserGroup.getAttribute(IUsergroupAttributeTypeBB.LIST_BB_ORGUNIT).getElements(getFullReadAccessUserContext());
		IAppObj secondOrganizationalUnit = ARCMCollections.extractSingleEntry(secondOrganizationalUnitList, true);
		
		hasSameOrganizationalUnit = firstOrganizationalUnit.getGuid().equals(secondOrganizationalUnit.getGuid()) ? true : false;
		 
		return hasSameOrganizationalUnit;
	}
	
	/**
	 * This method defines that only BdB's Internal Control department <b>will be able
	 * to create issues on behalf of others departments.<b>
	 * @return
	 */
	public static boolean canCreatorGroupDelegateIssues(){
		
		final String dicoiUOR = "000019955";
		boolean canCreatorDelegateIssues = false;
		
		REEnvironment env = REEnvironment.getInstance();
		IUserContext userContext = env.getUserContext();
		
		List<IUsergroupAppObj> issueCreatorUserGroup = userContext.getUserRelations().getGroups(EnumerationsBB.USERROLE_TYPE_BB.ISSUECREATOR, RoleLevel.O );
		if (issueCreatorUserGroup.size() > 0) {
			for (IUsergroupAppObj userGroup : issueCreatorUserGroup) {
				List<IAppObj> userGroupOrgUnitList = userGroup.getAttribute(IUsergroupAttributeTypeBB.LIST_BB_ORGUNIT).getElements(getFullReadAccessUserContext());
				IAppObj userGroupOrgUnit = ARCMCollections.extractSingleEntry(userGroupOrgUnitList, true);
			
				String userGroupOrgUnitCodUOR = userGroupOrgUnit.getAttribute(IHierarchyAttributeTypeBB.ATTR_CODIGO_UOR).getPersistentRawValue();
				if (userGroupOrgUnitCodUOR.equals(dicoiUOR)) {
					canCreatorDelegateIssues = true;
					break;
				} 
			}
		}
		
		return canCreatorDelegateIssues;
	}
	
	private static void validateIssueCreatorBelongsToIROIssueOwners(RuleAppObj issue, StateInformation stateInformation, MessageInformation messageInformation, String issueTypeText, IUserContext userContext){
		IListAttribute issueRelevantObjectAttr = (IListAttribute) issue.getAttribute(IIssueAttributeTypeBB.LIST_ISSUERELEVANTOBJECTS.getId());
		List<IAppObj> issueRelevantObjects = issueRelevantObjectAttr.getElements(getFullReadAccessUserContext());
		
		for (IAppObj issueRelevantObject : issueRelevantObjects) {
			IObjectType iroObjectType = issueRelevantObject.getObjectType();
			
			if (iroObjectType.equals(ObjectType.ISSUE)) {
				IEnumAttribute iroTypeAttr = issueRelevantObject.getAttribute(IIssueAttributeTypeBB.ATTR_ISSUETYPE);
				IEnumerationItem iroType = ARCMCollections.extractSingleEntry(iroTypeAttr.getRawValue(), true);
				
				if (iroType.equals(EnumerationsBB.ISSUETYPE.TECH_RECOMMENDATION)) {
					
					IAppObj issueCreatorUserGroup = UsergroupUtilityBB.getIssueCreatorUserGroup(userContext);
					if (issueCreatorUserGroup != null) {
						
						IListAttribute issueCreatorOrganizationalUnitAttr = issueCreatorUserGroup.getAttribute(IUsergroupAttributeTypeBB.LIST_BB_ORGUNIT);
						IAppObj issueCreatorOrganizationalUnit = ARCMCollections.extractSingleEntry(issueCreatorOrganizationalUnitAttr.getElements(getFullReadAccessUserContext()), true);
						
						if (issueCreatorOrganizationalUnit.getAttribute(IHierarchyAttributeTypeBB.ATTR_CODIGO_UOR).getPersistentRawValue().equals("000019955")) {
							continue;
						}
					}
					
					RuleAppObj iRORuleObject = new RuleAppObj(issueRelevantObject, userContext.getLanguage());
					IListAttribute iROOwnersGroupAttr = (IListAttribute) iRORuleObject.getAttribute(IIssueAttributeTypeBB.LIST_OWNERS_GROUP.getId());
					
					if (!iROOwnersGroupAttr.isEmpty()) {
						IAppObj iROOwnersGroup = ARCMCollections.extractSingleEntry(iROOwnersGroupAttr.getElements(getFullReadAccessUserContext()), true);
						List<String> clientSigns = ARCMCollections.createList(AppObjUtility.getRelatedClientSign(issueCreatorUserGroup));
						List<IAppObj> allUserGroupsOfCurrentUser = UsergroupUtilityBB.getUsergroupsOfUser(userContext.getUser().getObjectId(), EnumerationsBB.USERROLE_TYPE_BB.ISSUEOWNER, Enumerations.USERROLE_LEVEL.OBJECT, clientSigns);
						
						boolean creatorBelongsToIROIssueOwners = allUserGroupsOfCurrentUser.contains(iROOwnersGroup);
						
						if (!creatorBelongsToIROIssueOwners) {
							stateInformation.setValid(IIssueAttributeTypeBB.LIST_ISSUERELEVANTOBJECTS.getId(), false);
							
							String creatorBelongsToIROIssueOwnersViolatedRule = Metadata.getMetadata().getResourceBundle(
									getFullReadAccessUserContext().getLanguage()).getString("error.issuerelevantobject.action.creator_issueowers.ERR");
							
							messageInformation.addErrorMessage(IIssueAttributeTypeBB.LIST_ISSUERELEVANTOBJECTS.getId(),
									"error.issuerelevantobject.has.not.specific.element.ERR", issueTypeText,creatorBelongsToIROIssueOwnersViolatedRule);
							break;
						}	
					}
					
					
				}
			}
			
		}
	}
	
	/**
	 * This method checks whether a issue creator is able to create a specific issue based on the following variables:
	 * <ul>
	 * <li> Organizational unit</li>
	 * <li> Issue type </li>
	 * </ul>
	 * @param issue
	 * @param userContext
	 * @return
	 */
	private static void validateIssueCreatorByIssueType(RuleAppObj issue, StateInformation stateInformation, MessageInformation messageInformation, String issueTypeText, IUserContext userContext){
		IAppObj issueCreatorUserGroup = UsergroupUtilityBB.getIssueCreatorUserGroup(userContext);
		boolean userHasRoleIssueManager = userHasRoleIndependentFromAppObj(Enumerations.USERROLE_TYPE.ISSUEMANAGER.getId());
		
		boolean issueCreatorByIssueType = Boolean.TRUE;
		
		if (issueCreatorUserGroup == null && !userHasRoleIssueManager) {
			issueCreatorByIssueType = false;
		} else {
			IEnumAttribute issueTypeAttr = (IEnumAttribute) issue.getAttribute(IIssueAttributeTypeBB.ATTR_ISSUETYPE.getId());
			IEnumerationItem issueType = ARCMCollections.extractSingleEntry(issueTypeAttr.getRawValue(), true);
			
			if (!issueType.equals(EnumerationsBB.ISSUETYPE.DEALING_DEADLINE) && !issueType.equals(EnumerationsBB.ISSUETYPE.MANIFESTATION) && !issueType.equals(EnumerationsBB.ISSUETYPE.ACTION)) {
				
				if (!userHasRoleIssueManager) {
					List<IAppObj> userGroupOrgUnitList = issueCreatorUserGroup.getAttribute(IUsergroupAttributeTypeBB.LIST_BB_ORGUNIT).getElements(getFullReadAccessUserContext());
					if (userGroupOrgUnitList.size() > 0) {
						IAppObj userGroupOrgUnit = ARCMCollections.extractSingleEntry(userGroupOrgUnitList, true);
						String codUOR = userGroupOrgUnit.getAttribute(IHierarchyAttributeTypeBB.ATTR_CODIGO_UOR).getPersistentRawValue();
						issueCreatorByIssueType = getIssueCreatorByIssueType(issueType).contains(codUOR);
					} else {
						issueCreatorByIssueType = false;
					}
				}
				
			}
		}
		
		if (!issueCreatorByIssueType) {
			stateInformation.setValid(IIssueAttributeTypeBB.ATTR_ISSUETYPE.getId(), false);
			
			messageInformation.addErrorMessage(IIssueAttributeTypeBB.ATTR_ISSUETYPE.getId(),
					"error.issue.type.issuecreator.not_allowed.ERR", issueTypeText);
		}
		
		
	}
	
	private static List<String> getIssueCreatorByIssueType(IEnumerationItem issueType){
		
		List<String> issueCreatorByIssueType = null;
		
		if (issueType.equals(EnumerationsBB.ISSUETYPE.FAULTLOG) || issueType.equals(EnumerationsBB.ISSUETYPE.SAD)) {
			issueCreatorByIssueType = Arrays.asList("000019955");
		} else if (issueType.equals(EnumerationsBB.ISSUETYPE.EXTRAPOLATION_SHEET)) {
			issueCreatorByIssueType = Arrays.asList("000024738");
		} else if (issueType.equals(EnumerationsBB.ISSUETYPE.TECH_RECOMMENDATION)) {
			issueCreatorByIssueType = Arrays.asList("000019955", "000024738");
		}
		
		return issueCreatorByIssueType;
		
	}
	

	
	/**
     * Method extended by BdB project
     * This method will be only executed when an issue is in the implementation phase.
     * Otherwise, nothing happens.
     * @see IssueHelper#recalculateTimeDependentState()
     */
    public static void recalculateTimeDependentState() {
        REEnvironment env = REEnvironment.getInstance();
        RuleAppObj issue = env.getRuleAppObj();
        final IEnumAttribute stateTimeAttr = (IEnumAttribute) issue.getAttribute(IIssueAttributeTypeBB.ATTR_STATETIME.getId());
        final IEnumerationItem stateTime = ARCMCollections.extractSingleEntry(stateTimeAttr.getRawValue(), true);
        
        if (stateTime.equals(EnumerationsBB.ISSUESTATETIME_BB.ON_TIME) || stateTime.equals(EnumerationsBB.ISSUESTATETIME_BB.OVERDUE)) {
			IssueHelper.recalculateTimeDependentState();
		}
    }
    
	public static long calculateDifferenceInDaysBetweenDates(Date secondDate, Date firstDate){

		long duration = secondDate.getTime() - firstDate.getTime();
		long differenceInDays = TimeUnit.DAYS.convert(duration, TimeUnit.MILLISECONDS);
		
		
		return differenceInDays >= 0 ? differenceInDays : -1;
	}
	
	/**
	 * This function set  the visibility of SAD issue's gravity questions
	 */
	public static void checkSADDeficiencyGravityQuestions()
	{
		REEnvironment env = REEnvironment.getInstance(); 
		RuleAppObj issue = env.getRuleAppObj();	
		
		try
		{

			IBooleanAttribute firstQstAttr = (IBooleanAttribute) issue.getAttribute(IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_QST01.getId());
			boolean firstQst = firstQstAttr.getRawValue();
			
			//Question´s values
			IBooleanAttribute secondtQstAttr = (IBooleanAttribute) issue.getAttribute(IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_QST02.getId());
			IBooleanAttribute thirdQstAttr = (IBooleanAttribute) issue.getAttribute(IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_QST03.getId());
			IBooleanAttribute fourthQstAttr = (IBooleanAttribute) issue.getAttribute(IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_QST04.getId());
			IBooleanAttribute fifthQstAttr = (IBooleanAttribute) issue.getAttribute(IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_QST05.getId());
			IBooleanAttribute sixthQstAttr = (IBooleanAttribute) issue.getAttribute(IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_QST06.getId());	
			
			//If question 1 is 'NO'
			if(!firstQst){
				if (isDirty(IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_QST01.getId())) {
					
					reset(IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_QST04.getId(), 
							 IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_REMARK_QST04.getId(),
							 IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_QST03.getId(),
							 IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_QST02.getId());
					
					disable(IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_QST04.getId(), 
							 IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_REMARK_QST04.getId(),
							 IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_QST03.getId(),
							 IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_QST02.getId());
				}
				
				enable(IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_QST05.getId(),
						IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_REMARK_QST05.getId());
			} else if (firstQst){
				enable(IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_QST02.getId());

				//Analyze question 2
				if(!secondtQstAttr.isEmpty()){						
					Boolean secondtQst = secondtQstAttr.getRawValue();
					
					if(!secondtQst){
						if (isDirty(IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_QST02.getId())) {
							
							reset(IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_QST04.getId(), 
									 IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_REMARK_QST04.getId(),
									 IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_QST03.getId());
							
							disable(IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_QST04.getId(), 
									 IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_REMARK_QST04.getId(),
									 IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_QST03.getId());
						}
						
						enable(IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_QST05.getId(),
								IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_REMARK_QST05.getId());
					} else if(secondtQst){
						enable(IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_QST03.getId());	
						
						//Analyze question 3
						if(!thirdQstAttr.isEmpty()){
							boolean thirdQst = thirdQstAttr.getRawValue();
							
							if(!thirdQst){
								if (isDirty(IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_QST03.getId())) {
									
									reset(IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_QST04.getId(), 
											 IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_REMARK_QST04.getId());
									
									disable(IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_QST04.getId(), 
											 IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_REMARK_QST04.getId());
								}
								
								enable(IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_QST05.getId(),
										IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_REMARK_QST05.getId());

							} else if(thirdQst) {
								enable(IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_QST04.getId(),
										IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_REMARK_QST04.getId());
								
								//Analyze question 4
								if(!fourthQstAttr.isEmpty()){
									boolean fourthQst = fourthQstAttr.getRawValue();
									
									if(!fourthQst){
										if (isDirty(IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_QST04.getId())) {
											
											reset(IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_QST05.getId(),
													 IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_REMARK_QST05.getId(),
													 IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_QST06.getId());
											
											disable(IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_QST05.getId(),
													 IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_REMARK_QST05.getId(),
													 IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_QST06.getId());
										}
										
										//Show Result of question 4
										calculateSAGGravityResultsByQuestion(issue, fourthQstAttr);

									} else if(fourthQst){
										
										enable(IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_QST05.getId(),
												IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_REMARK_QST05.getId());
									}
								} else {
									reset(IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_QST05.getId(),
										   IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_REMARK_QST05.getId(),
										   IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_QST06.getId(),
										   IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_SUGGRESULT.getId(),
										   IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_RESULT.getId());
									
									disable(IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_QST05.getId(),
											 IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_REMARK_QST05.getId(),
											 IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_QST06.getId(),
											 IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_SUGGRESULT.getId(),
											 IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_RESULT.getId());
								}
							}
						} else {
							reset(IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_QST05.getId(),
								   IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_REMARK_QST05.getId(),
								   IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_QST06.getId(),
								   IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_SUGGRESULT.getId(),
								   IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_RESULT.getId());
								
							disable(IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_QST05.getId(),
									 IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_REMARK_QST05.getId(),
									 IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_QST06.getId(),
									 IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_SUGGRESULT.getId(),
									 IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_RESULT.getId());
						}
					}
				} else {
					reset(IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_QST05.getId(),
						   IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_REMARK_QST05.getId(),
						   IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_QST06.getId(),
						   IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_SUGGRESULT.getId(),
						   IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_RESULT.getId());
							
					disable(IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_QST05.getId(),
							 IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_REMARK_QST05.getId(),
							 IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_QST06.getId(),
							 IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_SUGGRESULT.getId(),
							 IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_RESULT.getId());
				}
			} else {
				reset(IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_QST05.getId(),
					   IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_REMARK_QST05.getId(),
					   IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_QST06.getId(),
					   IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_SUGGRESULT.getId(),
					   IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_RESULT.getId());
							
				disable(IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_QST05.getId(),
						 IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_REMARK_QST05.getId(),
						 IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_QST06.getId(),
						 IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_SUGGRESULT.getId(),
						 IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_RESULT.getId());
			}
			
			//Analyze question 5
			if(!fifthQstAttr.isEmpty()){
				boolean fifthQst = fifthQstAttr.getRawValue();
				if(!fifthQst){
					if (isDirty(IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_QST05.getId())) {
						
						reset(IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_QST06.getId());
						disable(IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_QST06.getId());
					}
					
					//Show Result of question 5		
					calculateSAGGravityResultsByQuestion(issue, fifthQstAttr);
				} else if(fifthQst) {
					enable(IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_QST06.getId());
				} 
			} 
			
			//Analyze question 6
			if(!sixthQstAttr.isEmpty()){
				
				//Show Result of question 6		
				calculateSAGGravityResultsByQuestion(issue, sixthQstAttr);
				
			}

		} catch (Exception e) {
			System.out.println("error.checkQuestions: " + e.getMessage());
			throw new BLInternalException(e, "error.checkQuestions", new String[0]);
		}
	}

	
	private static void calculateSAGGravityResultsByQuestion(RuleAppObj issue, IBooleanAttribute question){
		String questionAttrID = question.getAttributeType().getId();
		IEnumAttribute deficiencyGravityResultAttr = (IEnumAttribute) issue.getAttribute(IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_RESULT.getId());
		
		switch (questionAttrID) {
			case IIssueAttributeTypeBB.STR_DEFICIENCYGRAVITY_QST04:
				
				setVisible(IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_SUGGRESULT.getId(), true);
				
				if (deficiencyGravityResultAttr.isEmpty()) {
					reset(IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_RESULT.getId());
				}
				
				enable(IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_RESULT.getId());
				setMandatory(IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_RESULT.getId(), true);
				setValue(IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_SUGGRESULT.getId(), EnumerationsBB.DEFICIENCYGRAVITY_RESULT.MATERIAL_WEAKNESS.getId());
				break;
			
			case IIssueAttributeTypeBB.STR_DEFICIENCYGRAVITY_QST05:
				
				setVisible(IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_SUGGRESULT.getId(), true);
				
				if (deficiencyGravityResultAttr.isEmpty()) {
					reset(IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_RESULT.getId());
				}
				
				enable(IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_RESULT.getId());
				setMandatory(IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_RESULT.getId(), true);
				setValue(IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_SUGGRESULT.getId(), EnumerationsBB.DEFICIENCYGRAVITY_RESULT.CONTROL_DEFICIENCY.getId());
				break;
				
			case IIssueAttributeTypeBB.STR_DEFICIENCYGRAVITY_QST06:
				
				Boolean sixthQst = question.getRawValue();
				
				if (!sixthQst) {
					
					setVisible(IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_SUGGRESULT.getId(), true);
					
					if (deficiencyGravityResultAttr.isEmpty()) {
						reset(IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_RESULT.getId());
					}
					
					enable(IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_RESULT.getId());
					setMandatory(IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_RESULT.getId(), true);
					setValue(IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_SUGGRESULT.getId(), EnumerationsBB.DEFICIENCYGRAVITY_RESULT.SIGNIFICANT_DEFICIENCY.getId());
					
				} else {
					
					setVisible(IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_SUGGRESULT.getId(), true);
					
					if (deficiencyGravityResultAttr.isEmpty()) {
						reset(IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_RESULT.getId());
					}
					
					enable(IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_RESULT.getId());
					setMandatory(IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_RESULT.getId(), true);
					setValue(IIssueAttributeTypeBB.ATTR_DEFICIENCYGRAVITY_SUGGRESULT.getId(), EnumerationsBB.DEFICIENCYGRAVITY_RESULT.MATERIAL_WEAKNESS.getId());
					
				}
				break;
		}
	}
	
	public static void validateMandatoryDocuments(String issueTypePropertyKey){
		
		REEnvironment env = REEnvironment.getInstance();
		RuleAppObj issue = env.getRuleAppObj();
		
		StateInformation stateInformation = env.getStateInformation(); 
		MessageInformation messageInformation = env.getMessageInformation();
		
		String issueTypeText = Metadata.getMetadata().getResourceBundle(getFullReadAccessUserContext().getLanguage()).getString("enumeration.bb_issueType."+issueTypePropertyKey+".DBI");
		
		validateMandatoryDocuments(issue, stateInformation, messageInformation, issueTypeText, IIssueAttributeTypeBB.LIST_IMPL_DOCUMENTS);
		
	}
	
	private static void validateMandatoryDocuments(RuleAppObj issue, StateInformation stateInformation, MessageInformation messageInformation, String issueTypeText, IListAttributeType docAttrType){
		IListAttribute issueAttachmentAttr = (IListAttribute) issue.getAttribute(docAttrType.getId());
		if (stateInformation.isMandatory(issueAttachmentAttr.getAttributeType().getId()) || !issueAttachmentAttr.isEmpty()) {
			final String mandatoryAttachmentType = "ADS_ARCM";
			boolean mandatoryAttachmentAvailable = false;
			List<IAppObj> issueAttachments = issueAttachmentAttr.getElements(getFullReadAccessUserContext());
			
			for (IAppObj issueAttachment : issueAttachments) {
				String attachmentType = issueAttachment.getAttribute(IDocumentAttributeType.ATTR_TYPE).getRawValue();
				if (attachmentType.equals(mandatoryAttachmentType)) {
					mandatoryAttachmentAvailable = true;
					break;
				}
			}
			
			if (!mandatoryAttachmentAvailable) {
				stateInformation.setValid(issueAttachmentAttr.getAttributeType().getId(), false);
				
				messageInformation.addErrorMessage(docAttrType.getId(), "error.issue.documents.linktype.ads_arcm.ERR");
			}
		}
		
		
		
	}

	private static void enable(String... keys) {
		for (String key : keys) {
			setVisible(key, true);
			setEditable(key, true);
			setMandatory(key, true);
		}
	}

	private static void disable(String... keys) {
		for (String key : keys) {
			setVisible(key, false);
			setEditable(key, false);
			setMandatory(key, false);
		}
	}
	
	private static void reset(String... attributes){
		for (String attribute : attributes) {
			resetToDefaultValue(attribute);
		}
	}
	
	private static void validateActionIro(RuleAppObj issue, StateInformation stateInformation, MessageInformation messageInformation, String issueTypeText) {
		IListAttribute issueRelateObjectAttr = (IListAttribute) issue.getAttribute(IIssueAttributeTypeBB.LIST_ISSUERELEVANTOBJECTS.getId());
		List<IAppObj> issueRelatedObjects = issueRelateObjectAttr.getElements(getFullReadAccessUserContext());

		stateInformation.setMandatory(IIssueAttributeTypeBB.LIST_ISSUERELEVANTOBJECTS.getId(), true);

		int count = 0;
		for (IAppObj issueRelatedObject : issueRelatedObjects) {
			IObjectType iroObjectType = issueRelatedObject.getObjectType();
			if (iroObjectType.equals(ObjectType.ISSUE)) {
				IEnumerationItem issueType = ARCMCollections.extractSingleEntry(
						issueRelatedObject.getAttribute(IIssueAttributeTypeBB.ATTR_ISSUETYPE).getRawValue(), true);
				if (issueType.equals(EnumerationsBB.ISSUETYPE.TECH_RECOMMENDATION)) {
					count ++;
					if (count > 1) {
						break;
					}
				}
			}	
		}
		if (count != 1) {
			stateInformation.setValid(IIssueAttributeTypeBB.LIST_ISSUERELEVANTOBJECTS.getId(), false);
			messageInformation.addErrorMessage(IIssueAttributeTypeBB.LIST_ISSUERELEVANTOBJECTS.getId(), "error.action.iro.tech.recommendation.count.ERR");
		}
	}
	
	public static boolean isIro() {
		IBreadcrumbStack breadcrumbStack = UIEnvironmentManager.get().getBreadcrumbStack();
		if (breadcrumbStack.getSize() > 0) {
			List<IBreadcrumb> breadcrumbs = breadcrumbStack.getBreadcrumbs();
			IBreadcrumb breadcrumb = breadcrumbs.get(breadcrumbStack.getSize()- 2);
			if (breadcrumb instanceof IFormBreadcrumb) {
	            IFormBreadcrumb formBreadcrumb = (IFormBreadcrumb) breadcrumb;
	            if (formBreadcrumb.getModel().getAppObj().getObjectType().equals(ObjectType.ISSUE)) {
	            	return true;
	            }
			}
		}
		return false;
	}
	
	public static boolean areImplementationApproversTheSameAsInRT() {
		REEnvironment env = REEnvironment.getInstance(); 
		RuleAppObj ruleAppObj = env.getRuleAppObj();
		IUserContext userContext = env.getUserContext();		

		IAppObj issue = ruleAppObj.getAppObj();
		IAppObj techRecommendation = getTechRecommendation(issue, userContext); 
		IOVID implApproverL1OVID = ArcmHelperBB.getListAttributeOvid(issue, IIssueAttributeTypeBB.LIST_IMPL_APPROVERL1_GROUP);
		IOVID implApproverL2OVID = ArcmHelperBB.getListAttributeOvid(issue, IIssueAttributeTypeBB.LIST_IMPL_APPROVERL2_GROUP);
		IOVID implApproverL1techRecommendationOVID = ArcmHelperBB.getListAttributeOvid(techRecommendation, IIssueAttributeTypeBB.LIST_IMPL_APPROVERL1_GROUP);
		IOVID implApproverL2techRecommendationOVID = ArcmHelperBB.getListAttributeOvid(techRecommendation, IIssueAttributeTypeBB.LIST_IMPL_APPROVERL2_GROUP);
		return implApproverL1OVID.equals(implApproverL1techRecommendationOVID) && (implApproverL2OVID.equals(implApproverL2techRecommendationOVID));
	}

	private static IAppObj getTechRecommendation(IAppObj issue, IUserContext userContext){
		IAppObj result = null;
		List<IAppObj> issueRelatedObjects = issue.getAttribute(IIssueAttributeTypeBB.LIST_ISSUERELEVANTOBJECTS).getElements(userContext);
		for (IAppObj issueRelatedObject : issueRelatedObjects) {
			IObjectType iROObjectType = issueRelatedObject.getObjectType();
			if (iROObjectType.equals(ObjectType.ISSUE)) {
				List<IEnumerationItem> issueRelatedObjectTypeAttr = issueRelatedObject.getAttribute(IIssueAttributeTypeBB.ATTR_ISSUETYPE).getRawValue();
				IEnumerationItem issueRelatedType = ARCMCollections.extractSingleEntry(issueRelatedObjectTypeAttr, true);
				if (issueRelatedType.equals(EnumerationsBB.ISSUETYPE.TECH_RECOMMENDATION)) {
					result = issueRelatedObject;
				}
			}
		}
		return result;
	}
	
	public static List<IEnumerationItem> getSupportedIssueTypesByApproverInfo(String apprInfo){
		List<IEnumerationItem> supportedIssueTypes = new ArrayList<IEnumerationItem>();
		
		switch (apprInfo) {
		case IIssueApproverGenericFilterAttributeBB.MGMT_APPRL1:
			supportedIssueTypes = Arrays.asList(
					EnumerationsBB.ISSUETYPE.FAULTLOG,
					EnumerationsBB.ISSUETYPE.MANIFESTATION,
					EnumerationsBB.ISSUETYPE.TECH_RECOMMENDATION,
					EnumerationsBB.ISSUETYPE.ACTION,
					EnumerationsBB.ISSUETYPE.DEALING_DEADLINE,
					EnumerationsBB.ISSUETYPE.EXTRAPOLATION_SHEET);
			break;

		case IIssueApproverGenericFilterAttributeBB.MGMT_APPRL2:
			supportedIssueTypes = Arrays.asList(
					EnumerationsBB.ISSUETYPE.SAD,
					EnumerationsBB.ISSUETYPE.FAULTLOG,
					EnumerationsBB.ISSUETYPE.TECH_RECOMMENDATION,
					EnumerationsBB.ISSUETYPE.ACTION,
					EnumerationsBB.ISSUETYPE.DEALING_DEADLINE);
			break;
			
		case IIssueApproverGenericFilterAttributeBB.IMPL_APPRL1:
			supportedIssueTypes = Arrays.asList(
					EnumerationsBB.ISSUETYPE.FAULTLOG,
					EnumerationsBB.ISSUETYPE.MANIFESTATION,
					EnumerationsBB.ISSUETYPE.TECH_RECOMMENDATION,
					EnumerationsBB.ISSUETYPE.ACTION);
			break;
			
		case IIssueApproverGenericFilterAttributeBB.IMPL_APPRL2:
			supportedIssueTypes = Arrays.asList(
					EnumerationsBB.ISSUETYPE.TECH_RECOMMENDATION,
					EnumerationsBB.ISSUETYPE.ACTION,
					EnumerationsBB.ISSUETYPE.DEALING_DEADLINE,
					EnumerationsBB.ISSUETYPE.EXTRAPOLATION_SHEET);
			break;
			
		case IIssueApproverGenericFilterAttributeBB.TECH_APPRL1:
			supportedIssueTypes = Arrays.asList(
					EnumerationsBB.ISSUETYPE.TECH_RECOMMENDATION,
					EnumerationsBB.ISSUETYPE.ACTION,
					EnumerationsBB.ISSUETYPE.DEALING_DEADLINE);
			break;
			
		case IIssueApproverGenericFilterAttributeBB.TECH_APPRL2:
			supportedIssueTypes = Arrays.asList(
					EnumerationsBB.ISSUETYPE.TECH_RECOMMENDATION,
					EnumerationsBB.ISSUETYPE.ACTION);
			
		}
		return supportedIssueTypes;
	}

}