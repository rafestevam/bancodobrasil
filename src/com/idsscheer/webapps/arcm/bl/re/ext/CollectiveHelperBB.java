package com.idsscheer.webapps.arcm.bl.re.ext;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.framework.workflow.WorkflowUtility;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IDateAttribute;
import com.idsscheer.webapps.arcm.bl.re.impl.REEnvironment;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IIssueAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IIssueAttributeTypeBB;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IUserAttributeType;
import com.idsscheer.webapps.arcm.common.support.DateUtils;
import com.idsscheer.webapps.arcm.common.support.DateUtils.Target;
import com.idsscheer.webapps.arcm.config.Metadata;
import com.idsscheer.webapps.arcm.config.metadata.task.ITaskItem;
import com.idsscheer.webapps.arcm.config.metadata.workflow.IStateMetadata;

public class CollectiveHelperBB {
    private static Log log = LogFactory.getLog(CollectiveHelperBB.class);
    
    @SuppressWarnings("serial")
	static final HashMap<String, String> L1_L2_CONFLICT_PLANNING_MAP = new HashMap<String , String>() {{
		put(IIssueAttributeTypeBB.STR_MGMT_APPROVERL1_GROUP, IIssueAttributeTypeBB.STR_MGMT_APPROVERL2);
		put(IIssueAttributeTypeBB.STR_MGMT_APPROVERL2_GROUP, IIssueAttributeTypeBB.STR_MGMT_APPROVERL1);
		put(IIssueAttributeTypeBB.STR_IMPL_APPROVERL1_GROUP, IIssueAttributeTypeBB.STR_IMPL_APPROVERL2);
		put(IIssueAttributeTypeBB.STR_IMPL_APPROVERL2_GROUP, IIssueAttributeTypeBB.STR_IMPL_APPROVERL1);
		put(IIssueAttributeTypeBB.STR_TECH_APPROVERL1_GROUP, IIssueAttributeTypeBB.STR_TECH_APPROVERL2);
		put(IIssueAttributeTypeBB.STR_TECH_APPROVERL2_GROUP, IIssueAttributeTypeBB.STR_TECH_APPROVERL1);
    }};
    
	@SuppressWarnings("serial")
	static final HashMap<String, String> L1_L2_CONFLICT_EXECUTION_MAP = new HashMap<String , String>() {{
		put(IIssueAttributeTypeBB.STR_MGMT_APPROVERL1_GROUP, IIssueAttributeTypeBB.STR_EXEC_MGMTAPPRL2);
		put(IIssueAttributeTypeBB.STR_MGMT_APPROVERL2_GROUP, IIssueAttributeTypeBB.STR_EXEC_MGMTAPPRL1);
		put(IIssueAttributeTypeBB.STR_IMPL_APPROVERL1_GROUP, IIssueAttributeTypeBB.STR_EXEC_IMPLAPPRL2);
		put(IIssueAttributeTypeBB.STR_IMPL_APPROVERL2_GROUP, IIssueAttributeTypeBB.STR_EXEC_IMPLAPPRL1);
    }};

    public static void setObjectReadOnly() {
        REEnvironment env = REEnvironment.getInstance();
        StateInformation stateInformation = env.getStateInformation();			
        for (String key : env.getStateInformation().getKeys()) {
        	log.debug("Key + " + key);
        	stateInformation.setEditable(key, false);
		}     
    }
    
    public static boolean isServiceUser(){
    	REEnvironment env = REEnvironment.getInstance();
    	IUserContext ctx = env.getUserContext();
    	IAppObj user = ctx.getUser();
    	return user.getAttribute(IUserAttributeType.ATTR_USERID).getPersistentRawValue().equals("arisservice") ? true : false;
    }
    
    public static boolean isUserInApproverGroup() {
    	if (CollectiveHelper.isUserInAttributeGroup(":persistent:", "bb_mgmtApproverL1_group") ||
    			CollectiveHelper.isUserInAttributeGroup(":persistent:", "bb_mgmtApproverL2_group") ||
    			CollectiveHelper.isUserInAttributeGroup(":persistent:", "bb_implApproverL1_group") ||
    			CollectiveHelper.isUserInAttributeGroup(":persistent:", "bb_implApproverL2_group") ||
    			CollectiveHelper.isUserInAttributeGroup(":persistent:", "bb_techApproverL1_group") ||
    			CollectiveHelper.isUserInAttributeGroup(":persistent:", "bb_techApproverL2_group")) {
    		return true;
    	}
    	else {
    		return false;
    	}
    }

    public static boolean isUserInNonApproverGroup() {
		if (CollectiveHelper.isUserInAttributeGroup(":persistent:", "bb_owners_group") ||
    			CollectiveHelper.isUserInAttributeGroup(":persistent:", "bb_reviewers_group") || 
    			CollectiveHelper.isUserInAttrList("owners") ||
    			CollectiveHelper.isUserInAttrList("reviewers")) {
			return true;
    	}
    	else {
    		return false;
    	}
    }
    
    public static boolean performedApproverAction() {
		long userId = getUserId();
		if (isApprovedByUser(IIssueAttributeTypeBB.STR_EXEC_MGMTAPPRL1, userId) ||
				isApprovedByUser(IIssueAttributeTypeBB.STR_EXEC_MGMTAPPRL2, userId) ||
				isApprovedByUser(IIssueAttributeTypeBB.STR_EXEC_IMPLAPPRL1, userId) ||
				isApprovedByUser(IIssueAttributeTypeBB.STR_EXEC_IMPLAPPRL2, userId)) {
			return true;
		}
		else {
			return false;
		}
    }

    public static boolean performedApproverActionPlanning() {
		long userId = getUserId();
		if (isApprovedByUser(IIssueAttributeTypeBB.STR_MGMT_APPROVERL1, userId) ||
				isApprovedByUser(IIssueAttributeTypeBB.STR_MGMT_APPROVERL2, userId) ||
				isApprovedByUser(IIssueAttributeTypeBB.STR_IMPL_APPROVERL1, userId) ||
				isApprovedByUser(IIssueAttributeTypeBB.STR_IMPL_APPROVERL2, userId) ||
				isApprovedByUser(IIssueAttributeTypeBB.STR_TECH_APPROVERL1, userId) ||
				isApprovedByUser(IIssueAttributeTypeBB.STR_TECH_APPROVERL2, userId)
				) {
			return true;
		}
		else {
			return false;
		}
    }

    public static boolean performedNonApproverAction() {
		long userId = getUserId();
		if (isApprovedByUser(IIssueAttributeType.STR_OWNER, userId) ||
				isApprovedByUser(IIssueAttributeType.STR_REVIEWER, userId)) {
			return true;
		}
		else {
			return false;
		}
    }
    
    public static boolean objectCanBeModifyWithNonApprover() {
    	REEnvironment env = REEnvironment.getInstance();
        RuleAppObj obj = env.getRuleAppObj();
        IAppObj appObj = obj.getAppObj();
        
        /**
         * This was working until v98.2.0
         *
        List<ITaskitemMetadata> taskItem = WorkflowUtility.getStateMetadata(appObj).getTaskitems();
        for (ITaskitemMetadata taskitemMetadata : taskItem) {
        	String responsibleAttrID = taskitemMetadata.getResponsibleAttrID();
            if (responsibleAttrID != null && responsibleAttrID.length() > 0) {
                if ((responsibleAttrID.equals("owners") && CollectiveHelper.isUserInAttrList("owners")) || 
                		(responsibleAttrID.equals("reviewers") && CollectiveHelper.isUserInAttrList("reviewers")) || 
                		(responsibleAttrID.equals("creator") &&  CollectiveHelper.isObjectOpenedByItsCreator())) {
                	return true;
                }
            }
		} */
        
        IStateMetadata stateMetadata = WorkflowUtility.getStateMetadata(appObj);
        List<ITaskItem> taskItems = Metadata.getMetadata().getTaskItems(appObj.getObjectType().getId());
        for (ITaskItem taskItem : taskItems) {
			List<String> workflowStateIDs = taskItem.getWorkflowStateIDsForOpen();
			if (workflowStateIDs.contains(stateMetadata.getStateId())) {
				String responsibleAttrID = taskItem.getResponsibleUsersListAttributeID() != null ? taskItem.getResponsibleUsersListAttributeID() : taskItem.getResponsibleUsergroupListAttributeID();
				if (responsibleAttrID != null && responsibleAttrID.length() > 0) {
	                if ((responsibleAttrID.equals("owners") && CollectiveHelper.isUserInAttrList("owners")) || 
	                		(responsibleAttrID.equals("reviewers") && CollectiveHelper.isUserInAttrList("reviewers")) || 
	                		(responsibleAttrID.equals("creator") &&  CollectiveHelper.isObjectOpenedByItsCreator())) {
	                	return true;
	                }
	            }
			}
		}
        return false;
    }
    
    public static boolean objectCanBeModifyWithApprover() {
    	REEnvironment env = REEnvironment.getInstance();
        RuleAppObj obj = env.getRuleAppObj();
        IAppObj appObj = obj.getAppObj();
        IStateMetadata stateMetadata = WorkflowUtility.getStateMetadata(appObj);
        List<ITaskItem> taskItems = Metadata.getMetadata().getTaskItems(appObj.getObjectType().getId());
        for (ITaskItem taskItem : taskItems) {
			List<String> workflowStateIDs = taskItem.getWorkflowStateIDsForOpen();
			if (workflowStateIDs.contains(stateMetadata.getStateId())) {
				String responsibleAttrID = taskItem.getResponsibleUsersListAttributeID() != null ? taskItem.getResponsibleUsersListAttributeID() : taskItem.getResponsibleUsergroupListAttributeID();
				if (responsibleAttrID != null && responsibleAttrID.length() > 0) {
	                if ((responsibleAttrID.equals(IIssueAttributeTypeBB.STR_MGMT_APPROVERL1_GROUP) && CollectiveHelper.isUserInAttributeGroup(":persistent:", IIssueAttributeTypeBB.STR_MGMT_APPROVERL1_GROUP)) ||
	                		(responsibleAttrID.equals(IIssueAttributeTypeBB.STR_MGMT_APPROVERL2_GROUP) && CollectiveHelper.isUserInAttributeGroup(":persistent:", IIssueAttributeTypeBB.STR_MGMT_APPROVERL2_GROUP)) ||
	                		(responsibleAttrID.equals(IIssueAttributeTypeBB.STR_IMPL_APPROVERL1_GROUP) && CollectiveHelper.isUserInAttributeGroup(":persistent:", IIssueAttributeTypeBB.STR_IMPL_APPROVERL1_GROUP)) ||
	                		(responsibleAttrID.equals(IIssueAttributeTypeBB.STR_IMPL_APPROVERL2_GROUP) && CollectiveHelper.isUserInAttributeGroup(":persistent:", IIssueAttributeTypeBB.STR_IMPL_APPROVERL2_GROUP)) ||
	                		(responsibleAttrID.equals(IIssueAttributeTypeBB.STR_TECH_APPROVERL1_GROUP) && CollectiveHelper.isUserInAttributeGroup(":persistent:", IIssueAttributeTypeBB.STR_TECH_APPROVERL1_GROUP)) ||
	                		(responsibleAttrID.equals(IIssueAttributeTypeBB.STR_TECH_APPROVERL2_GROUP) && CollectiveHelper.isUserInAttributeGroup(":persistent:", IIssueAttributeTypeBB.STR_TECH_APPROVERL2_GROUP))
	                		) {
	                	return true;
	                }
	            }
			}
		}
        return false;
    }

    public static boolean conflictL1L2Planning() {
    	return conflictL1L2(L1_L2_CONFLICT_PLANNING_MAP);
    }
    
    public static boolean conflictL1L2Execution() {
    	return conflictL1L2(L1_L2_CONFLICT_EXECUTION_MAP);
    }

    protected static boolean conflictL1L2(HashMap<String, String> map) {
		long userId = getUserId();
    	REEnvironment env = REEnvironment.getInstance();
        RuleAppObj obj = env.getRuleAppObj();
        IAppObj appObj = obj.getAppObj();
        IStateMetadata stateMetadata = WorkflowUtility.getStateMetadata(appObj);
        List<ITaskItem> taskItems = Metadata.getMetadata().getTaskItems(appObj.getObjectType().getId());
        for (ITaskItem taskItem : taskItems) {
			List<String> workflowStateIDs = taskItem.getWorkflowStateIDsForOpen();
			if (workflowStateIDs.contains(stateMetadata.getStateId())) {
				String responsibleAttrID = taskItem.getResponsibleUsersListAttributeID() != null ? taskItem.getResponsibleUsersListAttributeID() : taskItem.getResponsibleUsergroupListAttributeID();
				if (responsibleAttrID != null && responsibleAttrID.length() > 0) {
					if (map.containsKey(responsibleAttrID)) {
						String approver = map.get(responsibleAttrID);
						if (isApprovedByUser(approver, userId)) {
							return true;
						}
					}
	            }
			}
		}
        return false;
    }
    
    protected static boolean isApprovedByUser(String attr, long userId) {
    	REEnvironment env = REEnvironment.getInstance();
        RuleAppObj obj = env.getRuleAppObj();
		List<RuleAppObj> userList = obj.getElements(attr, false, false, null);
		if (!userList.isEmpty()) {
			RuleAppObj owner = userList.get(0);
			if (owner.getObjectId() == userId) {
				return true;
			}
		}
		return false;
    }
    
    protected static long getUserId() {
    	REEnvironment env = REEnvironment.getInstance();
        IUserContext ctx = env.getUserContext();
        return ctx.getUser().getObjectId();
    }
    
    public static boolean isDateAttributeGTToday(String dateAttribute){
    	REEnvironment env = REEnvironment.getInstance();
        RuleAppObj ruleAppObj = env.getRuleAppObj();
        IDateAttribute dateAttr = (IDateAttribute) ruleAppObj.getAttribute(dateAttribute);
        
        boolean isGT = false;
        
        Date date = dateAttr.getRawValue();
        Date today = new Date();
        
        date = DateUtils.normalizeLocalDate(date, Target.END_OF_DAY);
        today = DateUtils.normalizeLocalDate(today, Target.END_OF_DAY);
        
        long diffTimeMiliseconds = 0;
        
        if(date != null){
        	diffTimeMiliseconds = (long)(date.getTime() - today.getTime());
        	isGT = diffTimeMiliseconds > 0 ? true : false;
        }
        
        return isGT;
        
	}
}