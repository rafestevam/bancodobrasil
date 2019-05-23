/**
 * 
 */
package com.idsscheer.webapps.arcm.ui.components.issuemanagement.actioncommands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.authentication.context.IUserRelations;
import com.idsscheer.webapps.arcm.bl.authentication.context.ServerContext;
import com.idsscheer.webapps.arcm.bl.common.support.AppObjUtility;
import com.idsscheer.webapps.arcm.bl.common.support.UsergroupUtility;
import com.idsscheer.webapps.arcm.bl.component.common.support.ArcmHelperBB;
import com.idsscheer.webapps.arcm.bl.component.common.support.UsergroupUtilityBB;
import com.idsscheer.webapps.arcm.bl.exception.ObjectRelationException;
import com.idsscheer.webapps.arcm.bl.exception.RightException;
import com.idsscheer.webapps.arcm.bl.framework.workflow.WorkflowUtility;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObjFacade;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IUsergroupAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IEnumAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IListAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IValueAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IAppObjIterator;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IAppObjQuery;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.QueryRestriction;
import com.idsscheer.webapps.arcm.bl.re.impl.RECommons;
import com.idsscheer.webapps.arcm.common.constants.metadata.EnumerationsBB;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IIssueAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IIssueAttributeTypeBB;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IUsergroupAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IUsergroupAttributeTypeBB;
import com.idsscheer.webapps.arcm.common.util.ARCMCollections;
import com.idsscheer.webapps.arcm.common.util.ovid.IOVID;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IAttributeType;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IClientSignAttributeType;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IEnumAttributeType;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IListAttributeType;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IObjectType;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IValueAttributeType;
import com.idsscheer.webapps.arcm.config.metadata.rights.roles.RoleLevel;
import com.idsscheer.webapps.arcm.config.metadata.workflow.IStateMetadata;
import com.idsscheer.webapps.arcm.ui.framework.actioncommands.ActionCommandException;


/**
 * <p> This ActionCommand is executed when events based on the 'cache' key are triggered through the ISSUE form.</p>
 *
 * <p> This is a customization for <b><code>Banco do Brasil Project</code></b> </p>
 *@author brmob
 *@since v20150302-CUST-IssueMgmt
 *@see IssueCacheActionCommand
 */
public class IssueCacheActionCommandBB extends IssueCacheActionCommand {
	protected boolean doClearOptions = false;
	List<IEnumerationItem> currentBbTypeList = null;

	@Override
	protected void assumeData(final String... p_exculdeParameters) {
		super.assumeData(p_exculdeParameters);
		if (doClearOptions) {
			resetAttributes();
		}
		else {
			if (currentBbTypeList != null) {
				final IEnumAttribute bbTypeEnumAttribute = formModel.getAppObj().getAttribute(IIssueAttributeTypeBB.ATTR_ISSUETYPE);
				bbTypeEnumAttribute.setRawValue(currentBbTypeList);
			}	
		}
	}

	@Override
	protected boolean beforeExecute() {
		if (!super.beforeExecute()) {
			return CANCEL_EXECUTION;
		}
		if (isDialogReady("okcancel")) {
			doClearOptions = true;
			environment.getBreadcrumbStack().peek().removeDialog("okcancel");
			return CONTINUE_EXECUTION;
		}
        if (isBreadcrumbRequest() || !formModel.isEditable()) {
			return CONTINUE_EXECUTION;
		}
		if (bbIssueTypeIsChanged()) {
			final IEnumAttribute attribute = formModel.getAppObj().getAttribute(IIssueAttributeTypeBB.ATTR_ISSUETYPE);
			currentBbTypeList = attribute.getRawValue();
			assumeData();
			environment.getDialogManager().createConfirmationDialog(requestContext,"okcancel","warning.issue.values.will.be.dropped.WRG");
			forceRollbackTransaction();
			return CANCEL_EXECUTION;
		}
		return CONTINUE_EXECUTION;
	}
	
	@Override
	protected void execute() {
		super.execute();
		IAppObj issue = formModel.getAppObj();
		
		IStateMetadata stateMetadata = WorkflowUtility.getStateMetadata(issue);
		boolean createdByTestCase = issue.getAttribute(IIssueAttributeTypeBB.ATTR_CREATED_BY_TESTCASE).getPersistentRawValue();
		
		if (issue.getVersionData().isInitialVersion() || (stateMetadata.getStateId().equals("openForCreation") && createdByTestCase)) {
			
			IUserContext userContext = environment.getUserContext();
			setIssueAsSpecificClientObject(issue, userContext);
			
			IAppObj creatorUserGroup = getCreatorUserGroupByUserContext(issue, userContext);
			setCreatorUserGroupByUserContext(issue, creatorUserGroup, getFullGrantUserContext());
		}
		
		List<IEnumerationItem> issueTypeAttr = issue.getAttribute(IIssueAttributeTypeBB.ATTR_ISSUETYPE).getRawValue();
		IEnumerationItem issueType = ARCMCollections.extractSingleEntry(issueTypeAttr, true);
		
		if (issueType.equals(EnumerationsBB.ISSUETYPE.TECH_RECOMMENDATION)) {
			
			executeTechRecomendationRules(issue);
		} else if (issueType.equals(EnumerationsBB.ISSUETYPE.SAD)) {
			
			executeSADRules(issue);
		} else if (issueType.equals(EnumerationsBB.ISSUETYPE.DEALING_DEADLINE)){
			
			executeDealingDeadlineRules(issue);
		} else if (issueType.equals(EnumerationsBB.ISSUETYPE.EXTRAPOLATION_SHEET)) {
			
			executeExtrapolationRules(issue);
		} else if (issueType.equals(EnumerationsBB.ISSUETYPE.ACTION)){
			
			executeActionRules(issue);
		} else if (issueType.equals(EnumerationsBB.ISSUETYPE.FAULTLOG)) {
			
			executeFaltLogRules(issue);
		} else if (issueType.equals(EnumerationsBB.ISSUETYPE.MANIFESTATION)) {
			
			executeManifestationRules(issue);
		} 
	}
	
	/**
	 * This method executes some specific rules when a issue 'Tech recommendation' is being created by issue creators.
	 * @param issue
	 */
	private void executeTechRecomendationRules(IAppObj issue){
		
		/* For the issues classified as action or tech_recommendation there are the following requirements to be meet. */
		IStateMetadata stateMetadata = WorkflowUtility.getStateMetadata(issue);
		
		
		
		if (stateMetadata.getType().equals(IStateMetadata.Type.PREPARED) || stateMetadata.getStateId().equals("openForCreation")) {
			/* 
			 * The attribute bb_firstPlannedEndDate will be changed while the creator hasn't finished 
			 * the issue creation procedure. 
			 */ 
			AppObjUtility.copyAttributeValue(issue, issue, IIssueAttributeType.ATTR_PLANNEDENDDATE, IIssueAttributeTypeBB.ATTR_FIRST_PLANNED_ENDDATE);
			
			/*
			 * The attribute owners will be filled based on the bb_owners_group during the issue creation.
			 */
			IListAttribute ownersGroupAttr = issue.getAttribute(IIssueAttributeTypeBB.LIST_OWNERS_GROUP);
			List<IAppObj> ownersGroupList = ownersGroupAttr.getElements(getFullGrantUserContext());
			IListAttribute ownersAttr = issue.getAttribute(IIssueAttributeTypeBB.LIST_OWNERS);
			
			if (ownersGroupAttr.isDirty()) {
				mapUserGroupMembersToAttribute(ownersGroupList, ownersAttr);
			}
			
			/*
			 * The attribute reviewers will be filled based on the bb_reviewers_group during the issue creation.
			 */
			IListAttribute reviewersGroupAttr = issue.getAttribute(IIssueAttributeTypeBB.LIST_REVIEWERS_GROUP);
			List<IAppObj> reviewersGroupList = reviewersGroupAttr.getElements(getFullGrantUserContext());
			IListAttribute reviewersAttr = issue.getAttribute(IIssueAttributeTypeBB.LIST_REVIEWERS);
			
			if (reviewersGroupAttr.isDirty()) {
				mapUserGroupMembersToAttribute(reviewersGroupList, reviewersAttr);
			}
			
			/*
			 * The attributes bb_mgmtApproverL1 and bb_mgmtApproverL2 will be defined automatically following the same
			 * organizational unit that the issue creator belongs to...
			 * But it'll done only once.
			 */
			Map<IListAttributeType, IEnumerationItem> approversGroup = new HashMap<IListAttributeType, IEnumerationItem>();
			approversGroup.put(IIssueAttributeTypeBB.LIST_MGMT_APPROVERL1_GROUP, EnumerationsBB.USERROLE_TYPE_BB.ISSUEMGMTAPPROVER_L1);
			approversGroup.put(IIssueAttributeTypeBB.LIST_MGMT_APPROVERL2_GROUP, EnumerationsBB.USERROLE_TYPE_BB.ISSUEMGMTAPPROVER_L2);
			
			IAppObj issueCreatorUserGroup = UsergroupUtilityBB.getIssueCreatorUserGroup(getUserContext());
			
			defineApproversByIssueCreator(issue, issueCreatorUserGroup, approversGroup);
			
		}
		
		/* When an owner's classified the current issue as 'not injured' the owner's remark attribute will be reseted */
		Boolean isInjuredIssue = issue.getRawValue(IIssueAttributeTypeBB.ATTR_INJUREDISSUE);
		if (isInjuredIssue != null && !isInjuredIssue) {
			//issue.getAttribute(IIssueAttributeTypeBB.ATTR_OWNER_REMARK).setRawValue(StringUtils.EMPTY);
			
			/* During the execution phase, the attribute reviewer_remark will be copied when the execMgmtApprL1 
			 * has agreed with the reviewer's point of view. Of course, it only happens if the issue was classified as 'not injured'
			 */
			IEnumerationItem execMgmtApprL1Status = ARCMCollections.extractSingleEntry(issue.getAttribute(IIssueAttributeTypeBB.ATTR_EXEC_MGMTAPPRL1_STATUS).getRawValue(), true);
			if (execMgmtApprL1Status.equals(EnumerationsBB.ISSUE_EXECAPPRL1_STATUS.ACCEPTED) || execMgmtApprL1Status.equals(EnumerationsBB.ISSUE_EXECAPPRL1_STATUS.REJECTED)) {
				AppObjUtility.copyAttributeValue(issue, issue, IIssueAttributeTypeBB.ATTR_REVIEWER_REMARK, IIssueAttributeTypeBB.ATTR_EXEC_MGMTAPPRL1_REMARK);
			}
		}
	}
	
	/**
	 * This method executes some specific rules when a issue 'Action' is being created by issue creators.
	 * @param issue
	 */
	private void executeActionRules(IAppObj issue){
		
		/* For the issues classified as action or tech_recommendation there are the following requirements to be meet. */
		IStateMetadata stateMetadata = WorkflowUtility.getStateMetadata(issue);
		
		
		/* The attribute bb_firstPlannedEndDate will be change while the creator hasn't finished 
		 * the issue creation procedure. 
		 */ 
		if (stateMetadata.getType().equals(IStateMetadata.Type.PREPARED) || stateMetadata.getStateId().equals("openForCreation")) {
			AppObjUtility.copyAttributeValue(issue, issue, IIssueAttributeType.ATTR_PLANNEDENDDATE, IIssueAttributeTypeBB.ATTR_FIRST_PLANNED_ENDDATE);

			/*
			 * The attribute reviewers will be filled based on the bb_reviewers_group during the issue creation.
			 */
			IListAttribute reviewersGroupAttr = issue.getAttribute(IIssueAttributeTypeBB.LIST_REVIEWERS_GROUP);
			List<IAppObj> reviewersGroupList = reviewersGroupAttr.getElements(getFullGrantUserContext());
			IListAttribute reviewersAttr = issue.getAttribute(IIssueAttributeTypeBB.LIST_REVIEWERS);
			
			if (reviewersGroupAttr.isDirty()) {
				mapUserGroupMembersToAttribute(reviewersGroupList, reviewersAttr);
			}
			
			/*
			 * The attributes bb_implApproverL1 and bb_implApproverL2 will be defined automatically following the same
			 * organizational unit that the issue creator belongs to...
			 * But it'll done only once.
			 */
			Map<IListAttributeType, IEnumerationItem> approversGroup = new HashMap<IListAttributeType, IEnumerationItem>();
			approversGroup.put(IIssueAttributeTypeBB.LIST_IMPL_APPROVERL1_GROUP, EnumerationsBB.USERROLE_TYPE_BB.ISSUEIMPLAPPROVER_L1);
			approversGroup.put(IIssueAttributeTypeBB.LIST_IMPL_APPROVERL2_GROUP, EnumerationsBB.USERROLE_TYPE_BB.ISSUEIMPLAPPROVER_L2);
			
			IAppObj issueCreatorUserGroup = UsergroupUtilityBB.getIssueCreatorUserGroup(getUserContext());
			
			defineApproversByIssueCreator(issue, issueCreatorUserGroup, approversGroup);
			
			IAppObj techRecommendation = getTechRecommendation(issue);
			if (techRecommendation != null) {
				executeIssueTechRecommendationAttachActionEachTime(issue, techRecommendation);
				
				IListAttribute ownersGroupAttr = issue.getAttribute(IIssueAttributeTypeBB.LIST_OWNERS_GROUP);
				IListAttribute implApproverL1Attr = issue.getAttribute(IIssueAttributeTypeBB.LIST_IMPL_APPROVERL1_GROUP);
				IListAttribute implApproverL2Attr = issue.getAttribute(IIssueAttributeTypeBB.LIST_IMPL_APPROVERL2_GROUP);
				/*
				 * It has to be filled just once. Anyway the user can change those attributes:
				 * IIssueAttributeTypeBB.LIST_IMPL_APPROVERL1_GROUP
				 * IIssueAttributeTypeBB.LIST_IMPL_APPROVERL2_GROUP
				 * IIssueAttributeTypeBB.LIST_OWNERS_GROUP
				 * IIssueAttributeTypeBB.LIST_IMPL_APPR_ORGUNIT
				 */
				if (implApproverL1Attr.isEmpty() || implApproverL2Attr.isEmpty() || ownersGroupAttr.isEmpty()) { 
					executeIssueTechRecommendationAttachActionFirstTime(issue, techRecommendation);
				}
				
				defineTechApproversByImplApprovers(issue, techRecommendation);
			}
			
			/*
			 * The attribute owners will be filled based on the bb_owners_group during the issue creation.
			 */
			IListAttribute ownersGroupAttr = issue.getAttribute(IIssueAttributeTypeBB.LIST_OWNERS_GROUP);
			List<IAppObj> ownersGroupList = ownersGroupAttr.getElements(getFullGrantUserContext());
			IListAttribute ownersAttr = issue.getAttribute(IIssueAttributeTypeBB.LIST_OWNERS);
			
			if (ownersGroupAttr.isDirty()) {
				mapUserGroupMembersToAttribute(ownersGroupList, ownersAttr);
			}
		}
		
		/* When an owner's classified the current issue as 'not injured' the owner's remark attribute will be reseted */
		Boolean isInjuredIssue = issue.getRawValue(IIssueAttributeTypeBB.ATTR_INJUREDISSUE);
		if (isInjuredIssue != null && !isInjuredIssue) {
			//issue.getAttribute(IIssueAttributeTypeBB.ATTR_OWNER_REMARK).setRawValue(StringUtils.EMPTY);
			
			/* During the execution phase, the attribute reviewer_remark will be copied when the execMgmtApprL1 
			 * has agreed with the reviewer's point of view. Of course, it only happens if the issue was classified as 'not injured'
			 */
			IEnumerationItem execMgmtApprL1Status = ARCMCollections.extractSingleEntry(issue.getAttribute(IIssueAttributeTypeBB.ATTR_EXEC_MGMTAPPRL1_STATUS).getRawValue(), true);
			if (execMgmtApprL1Status.equals(EnumerationsBB.ISSUE_EXECAPPRL1_STATUS.ACCEPTED) || execMgmtApprL1Status.equals(EnumerationsBB.ISSUE_EXECAPPRL1_STATUS.REJECTED)) {
				AppObjUtility.copyAttributeValue(issue, issue, IIssueAttributeTypeBB.ATTR_REVIEWER_REMARK, IIssueAttributeTypeBB.ATTR_EXEC_MGMTAPPRL1_REMARK);
			}
		}
	}
	
	/**
	 * This method executes some specific rules when a issue 'SAD' is being created by issue creators.
	 * @param issue
	 */
	private void executeSADRules(IAppObj issue){
		
		/* For the issues classified as sad there are the following requirements to be meet. */
		IStateMetadata stateMetadata = WorkflowUtility.getStateMetadata(issue);
		
		
		/* 
		 * The rules will be applied while the creator hasn't finished the issue creation procedure. 
		 */ 
		if (stateMetadata.getType().equals(IStateMetadata.Type.PREPARED) || stateMetadata.getStateId().equals("openForCreation")) {
		
			/* Based on a specific requirement, when a issueType is SAD or DEALING DEADLINE, 
			 * the description's attribute value will be copied to the remediation measure attribute
			 */
			AppObjUtility.copyAttributeValue(issue, issue, IIssueAttributeTypeBB.ATTR_DESCRIPTION, IIssueAttributeTypeBB.ATTR_REMEDIATIONMEASURE);
			
			/*
			 * The attributes bb_mgmtApproverL2 will be defined automatically following the same
			 * organizational unit that the issue creator belongs to...
			 * But it'll done only once.
			 */
			Map<IListAttributeType, IEnumerationItem> approversGroup = new HashMap<IListAttributeType, IEnumerationItem>();
			approversGroup.put(IIssueAttributeTypeBB.LIST_MGMT_APPROVERL2_GROUP, EnumerationsBB.USERROLE_TYPE_BB.ISSUEMGMTAPPROVER_L2);
			
			IAppObj issueCreatorUserGroup = UsergroupUtilityBB.getIssueCreatorUserGroup(getUserContext());
			
			defineApproversByIssueCreator(issue, issueCreatorUserGroup, approversGroup);
			
		}
	}
	
	/**
	 * This method executes some specific rules when a issue 'Extrapolation' is being created by issue creators.
	 * @param issue
	 */
	private void executeExtrapolationRules(IAppObj issue){
		
		/* For the issues classified as extrapolation there are the following requirements to be meet. */
		IStateMetadata stateMetadata = WorkflowUtility.getStateMetadata(issue);
		
		
		/* 
		 * The rules will be applied while the creator hasn't finished the issue creation procedure. 
		 */ 
		if (stateMetadata.getType().equals(IStateMetadata.Type.PREPARED) || stateMetadata.getStateId().equals("openForCreation")) {
		
			/*
			 * The attributes bb_mgmtApproverL1 will be defined automatically following the same
			 * organizational unit that the issue creator belongs to...
			 * But it'll done only once.
			 */
			Map<IListAttributeType, IEnumerationItem> approversGroup = new HashMap<IListAttributeType, IEnumerationItem>();
			approversGroup.put(IIssueAttributeTypeBB.LIST_MGMT_APPROVERL1_GROUP, EnumerationsBB.USERROLE_TYPE_BB.ISSUEMGMTAPPROVER_L1);
			
			IAppObj issueCreatorUserGroup = UsergroupUtilityBB.getIssueCreatorUserGroup(getUserContext());
			
			defineApproversByIssueCreator(issue, issueCreatorUserGroup, approversGroup);
			
		}
		
		
		/*
		 * When a issueType is extrapolation, the members of the implApproverL1 and mgmtApproverL1 groups will be
		 * also the issue's owners and issue's reviewers respectively. 
		 *
		 * 15.08.2016 - This rule is deprecated once there is no execution phase for extrapolation anymore.
		IListAttribute implApproverL1GroupAttr = issue.getAttribute(IIssueAttributeTypeBB.LIST_IMPL_APPROVERL1_GROUP);
		List<IAppObj> implApproverL1GroupList = implApproverL1GroupAttr.getElements(getFullGrantUserContext());
		IListAttribute ownersAttr = issue.getAttribute(IIssueAttributeTypeBB.LIST_OWNERS);
		
		mapUserGroupMembersToAttribute(implApproverL1GroupList, ownersAttr);
		
		IListAttribute mgmtApproverL1GroupAttr = issue.getAttribute(IIssueAttributeTypeBB.LIST_MGMT_APPROVERL1_GROUP);
		List<IAppObj> mgmtApproverL1GroupList = mgmtApproverL1GroupAttr.getElements(getFullGrantUserContext());
		IListAttribute reviewersAttr = issue.getAttribute(IIssueAttributeTypeBB.LIST_REVIEWERS);
		
		mapUserGroupMembersToAttribute(mgmtApproverL1GroupList, reviewersAttr); */
		
	}
	
	/**
	 * This method executes some specific rules when a issue 'Dealing deadline' is being created by issue creators.
	 * @param issue
	 */
	private void executeDealingDeadlineRules(IAppObj issue){
		/* Based on a specific requirement, when a issueType is SAD or DEALING DEADLINE, 
		 * the description's attribute value will be copied to the remediation measure attribute
		 */
		AppObjUtility.copyAttributeValue(issue, issue, IIssueAttributeTypeBB.ATTR_DESCRIPTION, IIssueAttributeTypeBB.ATTR_REMEDIATIONMEASURE);
		
		IStateMetadata stateMetadata = WorkflowUtility.getStateMetadata(issue);
				
		if (stateMetadata.getType().equals(IStateMetadata.Type.PREPARED) || stateMetadata.getStateId().equals("openForCreation")) {
			List<IAppObj> issueRelatedObjects = issue.getAttribute(IIssueAttributeTypeBB.LIST_ISSUERELEVANTOBJECTS).getElements(getFullGrantUserContext());
			for (IAppObj issueRelatedObject : issueRelatedObjects) {
				IObjectType iROObjectType = issueRelatedObject.getObjectType();
				if (iROObjectType.equals(ObjectType.ISSUE)) {
					
					IEnumAttribute issueTypeAttr = issueRelatedObject.getAttribute(IIssueAttributeTypeBB.ATTR_ISSUETYPE);
					IEnumerationItem issueType = ARCMCollections.extractSingleEntry(issueTypeAttr.getPersistentRawValue(), true);
					
					if (issueType.equals(EnumerationsBB.ISSUETYPE.ACTION) || issueType.equals(EnumerationsBB.ISSUETYPE.TECH_RECOMMENDATION)) {
						AppObjUtility.copyAttributeValue(issueRelatedObject, issue, IIssueAttributeTypeBB.LIST_IMPL_APPROVERL2_GROUP, IIssueAttributeTypeBB.LIST_IMPL_APPROVERL2_GROUP);
						AppObjUtility.copyAttributeValue(issueRelatedObject, issue, IIssueAttributeTypeBB.LIST_MGMT_APPROVERL1_GROUP, IIssueAttributeTypeBB.LIST_MGMT_APPROVERL1_GROUP);
						AppObjUtility.copyAttributeValue(issueRelatedObject, issue, IIssueAttributeTypeBB.LIST_MGMT_APPROVERL2_GROUP, IIssueAttributeTypeBB.LIST_MGMT_APPROVERL2_GROUP);
						AppObjUtility.copyAttributeValue(issueRelatedObject, issue, IIssueAttributeTypeBB.ATTR_ISTECHSUPPORTNEEDED, IIssueAttributeTypeBB.ATTR_ISTECHSUPPORTNEEDED);
						AppObjUtility.copyAttributeValue(issueRelatedObject, issue, IIssueAttributeTypeBB.LIST_TECH_APPROVERL1_GROUP, IIssueAttributeTypeBB.LIST_TECH_APPROVERL1_GROUP);
						AppObjUtility.copyAttributeValue(issueRelatedObject, issue, IIssueAttributeTypeBB.LIST_MGMT_APPR_ORGUNIT, IIssueAttributeTypeBB.LIST_MGMT_APPR_ORGUNIT);
						AppObjUtility.copyAttributeValue(issueRelatedObject, issue, IIssueAttributeTypeBB.LIST_IMPL_APPR_ORGUNIT, IIssueAttributeTypeBB.LIST_IMPL_APPR_ORGUNIT);
						AppObjUtility.copyAttributeValue(issueRelatedObject, issue, IIssueAttributeTypeBB.LIST_TECH_APPR_ORGUNIT, IIssueAttributeTypeBB.LIST_TECH_APPR_ORGUNIT);
					}
				}
			}
		}
		
	}

	private void executeIssueTechRecommendationAttachActionEachTime(IAppObj issue, IAppObj techRecommendation){
		AppObjUtility.copyAttributeValue(techRecommendation, issue, IIssueAttributeTypeBB.ATTR_PRIORITY, IIssueAttributeTypeBB.ATTR_PRIORITY);
		AppObjUtility.copyAttributeValue(techRecommendation, issue, IIssueAttributeTypeBB.ATTR_ISSUEORIGN, IIssueAttributeTypeBB.ATTR_ISSUEORIGN);
		AppObjUtility.copyAttributeValue(techRecommendation, issue, IIssueAttributeTypeBB.LIST_MGMT_APPROVERL1_GROUP, IIssueAttributeTypeBB.LIST_MGMT_APPROVERL1_GROUP);
		AppObjUtility.copyAttributeValue(techRecommendation, issue, IIssueAttributeTypeBB.LIST_MGMT_APPROVERL2_GROUP, IIssueAttributeTypeBB.LIST_MGMT_APPROVERL2_GROUP);
		AppObjUtility.copyAttributeValue(techRecommendation, issue, IIssueAttributeTypeBB.LIST_REVIEWERS_GROUP, IIssueAttributeTypeBB.LIST_REVIEWERS_GROUP);
		AppObjUtility.copyAttributeValue(techRecommendation, issue, IIssueAttributeTypeBB.LIST_MGMT_APPR_ORGUNIT, IIssueAttributeTypeBB.LIST_MGMT_APPR_ORGUNIT);
	}

	private void executeIssueTechRecommendationAttachActionFirstTime(IAppObj issue, IAppObj techRecommendation){
		AppObjUtility.copyAttributeValue(techRecommendation, issue, IIssueAttributeTypeBB.LIST_IMPL_APPROVERL1_GROUP, IIssueAttributeTypeBB.LIST_IMPL_APPROVERL1_GROUP);
		AppObjUtility.copyAttributeValue(techRecommendation, issue, IIssueAttributeTypeBB.LIST_IMPL_APPROVERL2_GROUP, IIssueAttributeTypeBB.LIST_IMPL_APPROVERL2_GROUP);
		AppObjUtility.copyAttributeValue(techRecommendation, issue, IIssueAttributeTypeBB.LIST_OWNERS_GROUP, IIssueAttributeTypeBB.LIST_OWNERS_GROUP);
		AppObjUtility.copyAttributeValue(techRecommendation, issue, IIssueAttributeTypeBB.LIST_IMPL_APPR_ORGUNIT, IIssueAttributeTypeBB.LIST_IMPL_APPR_ORGUNIT);
	}

	private void executeFaltLogRules(IAppObj issue){
		/* For the issues classified as faultlog there are the following requirements to be meet. */
		IStateMetadata stateMetadata = WorkflowUtility.getStateMetadata(issue);
		
		
		/* 
		 * The rules will be applied while the creator hasn't finished the issue creation procedure. 
		 */ 
		if (stateMetadata.getType().equals(IStateMetadata.Type.PREPARED) || stateMetadata.getStateId().equals("openForCreation")) {
			
			/*
			 * The attributes bb_mgmtApproverL1 and bb_mgmtApproverL2 will be defined automatically following the same
			 * organizational unit that the issue creator belongs to...
			 * But it'll done only once.
			 */
			Map<IListAttributeType, IEnumerationItem> approversGroup = new HashMap<IListAttributeType, IEnumerationItem>();
			approversGroup.put(IIssueAttributeTypeBB.LIST_MGMT_APPROVERL1_GROUP, EnumerationsBB.USERROLE_TYPE_BB.ISSUEMGMTAPPROVER_L1);
			approversGroup.put(IIssueAttributeTypeBB.LIST_MGMT_APPROVERL2_GROUP, EnumerationsBB.USERROLE_TYPE_BB.ISSUEMGMTAPPROVER_L2);
			
			IAppObj issueCreatorUserGroup = UsergroupUtilityBB.getIssueCreatorUserGroup(getUserContext());
			
			defineApproversByIssueCreator(issue, issueCreatorUserGroup, approversGroup);
			
		}
	}
	
	private void executeManifestationRules(IAppObj issue){
		/* For the issues classified as manifestation there are the following requirements to be meet. */
		IStateMetadata stateMetadata = WorkflowUtility.getStateMetadata(issue);
		
		
		/* 
		 * The rules will be applied while the creator hasn't finished the issue creation procedure. 
		 */ 
		if (stateMetadata.getType().equals(IStateMetadata.Type.PREPARED) || stateMetadata.getStateId().equals("openForCreation")) {
			
			/*
			 * The attributes bb_implApproverL1 will be defined automatically following the same
			 * organizational unit that the issue creator belongs to...
			 * But it'll done only once.
			 */
			Map<IListAttributeType, IEnumerationItem> approversGroup = new HashMap<IListAttributeType, IEnumerationItem>();
			approversGroup.put(IIssueAttributeTypeBB.LIST_IMPL_APPROVERL1_GROUP, EnumerationsBB.USERROLE_TYPE_BB.ISSUEIMPLAPPROVER_L1);
			
			IAppObj issueCreatorUserGroup = UsergroupUtilityBB.getIssueCreatorUserGroup(getUserContext());
			
			defineApproversByIssueCreator(issue, issueCreatorUserGroup, approversGroup);
			
		}
	}
	
	private void defineApproversByIssueCreator(IAppObj issue, IAppObj issueCreatorUserGroup, Map<IListAttributeType, IEnumerationItem> approverGroups){
		
		
		Set<Entry<IListAttributeType, IEnumerationItem>> approverGroupsSet = approverGroups.entrySet();
		
		for (Entry<IListAttributeType, IEnumerationItem> approverGroupEntry : approverGroupsSet) {
			IListAttributeType approverGroupAttrType = approverGroupEntry.getKey();
			IEnumerationItem approverGroupRole = approverGroupEntry.getValue();
			
			IListAttribute approverGroupAttr = issue.getAttribute(approverGroupAttrType);
			
			if (approverGroupAttr.isEmpty()) {
				IAppObjQuery approverGroupQuery = null;
				try{
					
					IAppObjFacade userGroupFacade = environment.getAppObjFacade(ObjectType.USERGROUP);
					
					approverGroupQuery = userGroupFacade.createQuery();
					approverGroupQuery.addRestriction(QueryRestriction.eq(IUsergroupAttributeType.ATTR_ROLE, approverGroupRole));
					
					IAppObjIterator approverGroupIterator = approverGroupQuery.getResultIterator();
					
					for (IAppObj approverGroup : approverGroupIterator) {
						
						if (checkSameOrganizationalUnitByUserGroups(issueCreatorUserGroup, approverGroup)) {
							approverGroupAttr.addFirstElement(approverGroup, getFullGrantUserContext());
							break;
						}
					}
				} catch (RightException e) {
					e.printStackTrace();
					throw new ActionCommandException(e);
				} catch (ObjectRelationException e) {
					e.printStackTrace();
					throw new ActionCommandException(e);
				} finally {
					if (approverGroupQuery != null) {
						approverGroupQuery.release();
					}
					
				}
			}
			
		}
		
	}
	
	/**
	 * This method checks whether the user groups are connected to the same organizational unit or not
	 * @param firstUserGroup
	 * @param secondUserGroup
	 * @return
	 */
	private boolean checkSameOrganizationalUnitByUserGroups(IAppObj firstUserGroup, IAppObj secondUserGroup){
		boolean hasSameOrganizationalUnit = Boolean.FALSE;
		
		if (firstUserGroup != null && secondUserGroup != null) {
			List<IAppObj> firstOrganizationalUnitList = firstUserGroup.getAttribute(IUsergroupAttributeTypeBB.LIST_BB_ORGUNIT).getElements(getFullGrantUserContext());
			IAppObj firstOrganizationalUnit = ARCMCollections.extractSingleEntry(firstOrganizationalUnitList, false);
			
			List<IAppObj> secondOrganizationalUnitList = secondUserGroup.getAttribute(IUsergroupAttributeTypeBB.LIST_BB_ORGUNIT).getElements(getFullGrantUserContext());
			IAppObj secondOrganizationalUnit = ARCMCollections.extractSingleEntry(secondOrganizationalUnitList, false);
			
			if (firstOrganizationalUnit == null || secondOrganizationalUnit == null) {
				return false;
			}
			hasSameOrganizationalUnit = firstOrganizationalUnit.getGuid().equals(secondOrganizationalUnit.getGuid()) ? true : false;
		}
		 
		return hasSameOrganizationalUnit;
	}
	
	private void mapUserGroupMembersToAttribute(List<IAppObj> userGroupList, IListAttribute attribute){
		if (!CollectionUtils.isEmpty(userGroupList)) {
			try {
				attribute.removeAllElements(getFullGrantUserContext());
				attribute.addElements(UsergroupUtility.getGroupMembers(userGroupList), getFullGrantUserContext());
			} catch (RightException e) {
				throw new ActionCommandException(e);
			} catch (ObjectRelationException e) {
				throw new ActionCommandException(e);
			}
			
		}
	}
	
	private void setIssueAsSpecificClientObject(IAppObj issue, IUserContext userContext){
		IAppObj creatorUserGroup = getCreatorUserGroupByUserContext(issue, userContext);
		if (creatorUserGroup != null) {
			String clientSign = AppObjUtility.getRelatedClientSign(creatorUserGroup);
			IClientSignAttributeType type = (IClientSignAttributeType) issue.getAttributeType("client_sign");
			issue.getAttribute(type).setRawValue(ServerContext.getInstance().getClient(clientSign));
		}
	}
	
	private IAppObj getCreatorUserGroupByUserContext(IAppObj issue, IUserContext userContext){
		List<IEnumerationItem> issueTypeAttr = issue.getAttribute(IIssueAttributeTypeBB.ATTR_ISSUETYPE).getRawValue();
		IEnumerationItem issueType = ARCMCollections.extractSingleEntry(issueTypeAttr, true);
		
		
		if (!issueType.equals(EnumerationsBB.ISSUETYPE.PLEASE_SELECT)) {
			IUserRelations userRelations = userContext.getUserRelations();
			List<IUsergroupAppObj> userGroups = userRelations.getGroups(EnumerationsBB.USERROLE_TYPE_BB.ISSUECREATOR, RoleLevel.O);
			
			if (issueType.equals(EnumerationsBB.ISSUETYPE.SAD)) {
				return containsCreatorUserGroup(userGroups, "bpacasad");
			} else if (issueType.equals(EnumerationsBB.ISSUETYPE.TECH_RECOMMENDATION)) {
				return containsCreatorUserGroup(userGroups, "bpacart0");
			} else if (issueType.equals(EnumerationsBB.ISSUETYPE.EXTRAPOLATION_SHEET)) {
				return containsCreatorUserGroup(userGroups, "bpacafex");
			} else if (issueType.equals(EnumerationsBB.ISSUETYPE.FAULTLOG)) {
				return containsCreatorUserGroup(userGroups, "bpacarf0");
			} else {
				return containsCreatorUserGroup(userGroups, "bpaca000");
			}
		}
		
		return null;
	}
	
	private void setCreatorUserGroupByUserContext(IAppObj issue, IAppObj creatorUserGroup, IUserContext userContext){
		if (creatorUserGroup != null) {
			IListAttribute creatorUserGroupAttr = issue.getAttribute(IIssueAttributeTypeBB.LIST_CREATORS_GROUP);
			try {
				creatorUserGroupAttr.addFirstElement(creatorUserGroup, userContext);
			} catch (RightException | ObjectRelationException e) {
				throw new ActionCommandException(e);
			}
		}
		
	}
	
	private IAppObj containsCreatorUserGroup(List<IUsergroupAppObj> userGroups, String prefix){
		for (IUsergroupAppObj userGroup : userGroups) {
			String userGroupDescription = userGroup.getAttribute(IUsergroupAttributeTypeBB.ATTR_DESCRIPTION).getPersistentRawValue();
			if (userGroupDescription.startsWith(prefix)) {
				return userGroup;
			}
		}
		return null;
	}
	
	protected boolean bbIssueTypeIsChanged() {
		final IEnumAttribute bbTypeEnumAttribute = formModel.getAppObj().getAttribute(IIssueAttributeTypeBB.ATTR_ISSUETYPE);

		final String newType = requestContext.getParameter(IIssueAttributeTypeBB.STR_ISSUETYPE);
		if (0 == newType.length()) {
			return false;
		}	
		IEnumerationItem oldTypeEnumItem = ARCMCollections.extractSingleEntry(bbTypeEnumAttribute.getRawValue(), false);
		if (oldTypeEnumItem != null ) {
			String oldType = oldTypeEnumItem.getValue();
			if (!oldType.equals("0")) {
				if (!oldType.equals(newType)) {
					return true;
				}
			}	
		}
		return false;
	}
	
	protected void resetAttributes() {
		IAppObj issueAppObj = formModel.getAppObj();
		List<IAttribute> editableAttributes = issueAppObj.getEditableAttributes(getUserContext());
		//begin special case
		IAttribute attr = issueAppObj.getAttribute(IIssueAttributeTypeBB.ATTR_ISTECHSUPPORTNEEDED);
		editableAttributes.add(attr);
		
		IAttribute issueOrignAttr = null;
		IEnumAttribute issueTypeAttr = issueAppObj.getAttribute(IIssueAttributeTypeBB.ATTR_ISSUETYPE);
		if (issueTypeAttr != null) {
			IEnumerationItem issueType = ARCMCollections.extractSingleEntry(issueTypeAttr.getRawValue(), true);
			if (issueType.equals(EnumerationsBB.ISSUETYPE.ACTION)) {
				issueOrignAttr = issueAppObj.getAttribute(IIssueAttributeTypeBB.ATTR_ISSUEORIGN);
				editableAttributes.add(issueOrignAttr);
			}
		}
		
		IAttribute creatorsGroupAttr = issueAppObj.getAttribute(IIssueAttributeTypeBB.LIST_CREATORS_GROUP);
		editableAttributes.add(creatorsGroupAttr);
		
		//end special case
		for (IAttribute attribute : editableAttributes) {
			if (!attribute.getAttributeType().equals(IIssueAttributeTypeBB.ATTR_ISSUETYPE)) {
				resetToDefaultValue(attribute);
			}	
		}	
	}
	
	@SuppressWarnings("rawtypes")
	public void resetToDefaultValue(IAttribute attribute) {
        IAttributeType attributeType = attribute.getAttributeType();
        if (attributeType instanceof IValueAttributeType) {
            IValueAttributeType type = (IValueAttributeType) attributeType;
            ((IValueAttribute) attribute).setRawValue(type.getDefaultValue());
        } else if (attributeType instanceof IEnumAttributeType) {
            IEnumAttributeType type = (IEnumAttributeType) attributeType;
            ((IEnumAttribute) attribute).setRawValue(type.getDefaultValue());
        } else if (attributeType instanceof IListAttributeType) {
            try {
                ((IListAttribute) attribute).removeAllElements(RECommons.getFullGrantContext());
            } catch (RightException e) {
                throw new RuntimeException(e);
            } catch (ObjectRelationException e) {
                throw new RuntimeException(e);
            }
        }
    }

	private IAppObj getTechRecommendation(IAppObj issue){
		IAppObj result = null;
		
		List<IAppObj> issueRelatedObjects = issue.getAttribute(IIssueAttributeTypeBB.LIST_ISSUERELEVANTOBJECTS).getElements(getFullGrantUserContext());
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
	
	private void defineTechApproversByImplApprovers(IAppObj issue, IAppObj techRecommendation){
		IOVID implApproverL1OVID = ArcmHelperBB.getListAttributeOvid(issue, IIssueAttributeTypeBB.LIST_IMPL_APPROVERL1_GROUP);
		IOVID implApproverL2OVID = ArcmHelperBB.getListAttributeOvid(issue, IIssueAttributeTypeBB.LIST_IMPL_APPROVERL2_GROUP);
		IOVID implApproverL1techRecommendationOVID = ArcmHelperBB.getListAttributeOvid(techRecommendation, IIssueAttributeTypeBB.LIST_IMPL_APPROVERL1_GROUP);
		IOVID implApproverL2techRecommendationOVID = ArcmHelperBB.getListAttributeOvid(techRecommendation, IIssueAttributeTypeBB.LIST_IMPL_APPROVERL2_GROUP);
		if (!(implApproverL1OVID.equals(implApproverL1techRecommendationOVID) && (implApproverL2OVID.equals(implApproverL2techRecommendationOVID)))) {
			issue.getAttribute(IIssueAttributeTypeBB.ATTR_ISTECHSUPPORTNEEDED).setRawValue(true);

			Map<IListAttributeType, IEnumerationItem> techApproverGroups = new HashMap<IListAttributeType, IEnumerationItem>();
			techApproverGroups.put(IIssueAttributeTypeBB.LIST_TECH_APPROVERL1_GROUP, EnumerationsBB.USERROLE_TYPE_BB.ISSUETECHAPPROVER_L1);
			techApproverGroups.put(IIssueAttributeTypeBB.LIST_TECH_APPROVERL2_GROUP, EnumerationsBB.USERROLE_TYPE_BB.ISSUETECHAPPROVER_L2);
			IAppObj implApproverL1techRecommendationGroup = ArcmHelperBB.getListAttributeAppObj(techRecommendation, IIssueAttributeTypeBB.LIST_IMPL_APPROVERL1_GROUP, getUserContext());
			if (implApproverL1techRecommendationGroup != null) {
				defineApproversByIssueCreator(issue, implApproverL1techRecommendationGroup, techApproverGroups);
			}	
		}
		else {
			try {
				issue.getAttribute(IIssueAttributeTypeBB.ATTR_ISTECHSUPPORTNEEDED).setRawValue(false);
				issue.getAttribute(IIssueAttributeTypeBB.LIST_TECH_APPROVERL1_GROUP).removeAllElements(getFullGrantUserContext());
				issue.getAttribute(IIssueAttributeTypeBB.LIST_TECH_APPROVERL2_GROUP).removeAllElements(getFullGrantUserContext());
			} catch (RightException | ObjectRelationException e) {
				throw new RuntimeException(e);			
			}
		}
	}
}