/**
 * 
 */
package com.idsscheer.webapps.arcm.common.constants.metadata.attribute;

import static com.idsscheer.webapps.arcm.common.constants.metadata.attribute.MetadataConstantsUtil.create;

import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IBooleanAttributeType;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IDateAttributeType;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IDoubleAttributeType;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IEnumAttributeType;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IListAttributeType;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.ILongAttributeType;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.ITextAttributeType;


/**
 * <p>  this interface defines all attribute-types and attribute-names of ISSUE IAppObj implementation.
 *      names of common attributes e.g. OBJ_ID and GUID are defined in {@link IObjectAttributeType} 
 *      additional attribute-names of versionized objects are defined in {@link IVersionAttributeType}
 * </p>
 * <p> This is a customization for <b><code>Banco do Brasil Project</code></b> </p>
 *@author brmob
 *@since v20150302-CUST-IssueMgmt
 *@see IIssueAttributeType
 */
public interface IIssueAttributeTypeBB extends IIssueAttributeType {
	
	final static String STR_ISSUETYPE = "bb_issueType";
	final static String STR_FIRST_PLANNED_ENDDATE = "bb_firstPlannedEndDate";
	final static String STR_ISSUEGRADE = "bb_issueGrade";
	final static String STR_ISSUEORIGN = "bb_issueOrign";
	final static String STR_STANDARD_APPROVERL1_STATUS = "bb_standardApproverL1_status";
	final static String STR_STANDARD_APPROVERL2_STATUS = "bb_standardApproverL2_status";
	final static String STR_MGMT_APPROVERL1_GROUP = "bb_mgmtApproverL1_group";
	final static String STR_MGMT_APPROVERL1_STATUS = "bb_mgmtApproverL1_status";
	final static String STR_MGMT_APPROVERL1_REMARK = "bb_mgmtApproverL1_remark";
	final static String STR_MGMT_APPROVERL1 = "bb_mgmtApproverL1";
	final static String STR_MGMT_APPROVERL2_GROUP = "bb_mgmtApproverL2_group";
	final static String STR_MGMT_APPROVERL2_STATUS = "bb_mgmtApproverL2_status";
	final static String STR_MGMT_APPROVERL2_REMARK = "bb_mgmtApproverL2_remark";
	final static String STR_MGMT_APPROVERL2 = "bb_mgmtApproverL2";
	final static String STR_IMPL_APPROVERL1_GROUP = "bb_implApproverL1_group";
	final static String STR_IMPL_APPROVERL1_STATUS = "bb_implApproverL1_status";
	final static String STR_IMPL_APPROVERL1 = "bb_implApproverL1";
	final static String STR_IMPL_APPROVERL2_GROUP = "bb_implApproverL2_group";
	final static String STR_IMPL_APPROVERL2_STATUS = "bb_implApproverL2_status";
	final static String STR_IMPL_APPROVERL2 = "bb_implApproverL2";
	final static String STR_ISTECHSUPPORTNEEDED = "bb_isTechSupportNeeded";
	final static String STR_TECH_APPROVERL1_GROUP = "bb_techApproverL1_group";
	final static String STR_TECH_APPROVERL1_STATUS = "bb_techApproverL1_status";
	final static String STR_TECH_APPROVERL1 = "bb_techApproverL1";
	final static String STR_TECH_APPROVERL2_GROUP = "bb_techApproverL2_group";
	final static String STR_TECH_APPROVERL2 = "bb_techApproverL2";
	final static String STR_INJUREDISSUE = "bb_injuredIssue";
	final static String STR_EXEC_IMPLAPPRL1_STATUS = "bb_execImplApprL1_status";
	final static String STR_EXEC_IMPLAPPRL2_STATUS = "bb_execImplApprL2_status";
	final static String STR_EXEC_IMPLAPPRL1 = "bb_execImplApprL1";
	final static String STR_EXEC_IMPLAPPRL2 = "bb_execImplApprL2";
	final static String STR_EXEC_MGMTAPPRL1_STATUS = "bb_execMgmtApprL1_status";
	final static String STR_EXEC_MGMTAPPRL1_REMARK = "bb_execMgmtApprL1_remark";
	final static String STR_EXEC_MGMTAPPRL1 = "bb_execMgmtApprL1";
	final static String STR_EXEC_MGMTAPPRL2 = "bb_execMgmtApprL2";
	final static String STR_DEALINGDEADLINE_DATE = "bb_dealingDeadlineDate";
	final static String STR_CREATOR_DATE = "bb_creator_date";
	final static String STR_START_DATE = "bb_start_date";
	final static String STR_PLANNED_DAYS = "bb_execPlannedDays";
	final static String STR_EXEC_IMPLAPPRL1_DATE = "bb_execImplApprL1_date";
	final static String STR_EXEC_IMPLAPPRL2_DATE = "bb_execImplApprL2_date";
	final static String STR_WORKED_DAYS = "bb_execWorkedDays";
	final static String STR_PERCENT_DAYS = "bb_execPercentDays";
	final static String STR_ISOVERDUEEXECUTION = "bb_isOverdueExecution";
	final static String STR_IMPL_APPROVERL2_DATE = "bb_implApproverL2_date";
	final static String STR_IMPL_APPRL2ATB_DATE = "bb_implApprL2ATB_date";
	final static String STR_MGMT_APPROVERL2_DATE = "bb_mgmtApproverL2_date";
	final static String STR_MGMT_APPRL2ATB_DATE = "bb_mgmtApprL2ATB_date";
	final static String STR_MGMT_APPRL2ATB_STATUS = "bb_mgmtApprL2ATB_status";
	final static String STR_MGMT_APPROVERL1_DATE = "bb_mgmtApproverL1_date";
	final static String STR_MGMT_APPRL1ATB_DATE = "bb_mgmtApprL1ATB_date";
	final static String STR_OWNERS_GROUP = "bb_owners_group";
	final static String STR_REVIEWERS_GROUP = "bb_reviewers_group";
	final static String STR_TECH_APPROVERL2_STATUS = "bb_techApproverL2_status";
	final static String STR_IMPL_DOCUMENTS = "bb_implDocuments";
	final static String STR_MGMT_APPR_ORGUNIT = "bb_mgmtApprover_orgunit";
	final static String STR_IMPL_APPR_ORGUNIT = "bb_implApprover_orgunit";
	final static String STR_TECH_APPR_ORGUNIT = "bb_techApprover_orgunit";
	final static String STR_CONCLUSION_DATE = "bb_conclusionDate";
	final static String STR_CERTIFICATION_DATE = "bb_certification_date";
	final static String STR_EXEC_MGMT_APPRL1_DATE = "bb_execMgmtApprL1_date";
	
	final static String STR_DEFICIENCYGRAVITY_QST01 = "bb_deficiencyGravityQst01";
	final static String STR_DEFICIENCYGRAVITY_QST02 = "bb_deficiencyGravityQst02";
	final static String STR_DEFICIENCYGRAVITY_QST03 = "bb_deficiencyGravityQst03";
	final static String STR_DEFICIENCYGRAVITY_QST04 = "bb_deficiencyGravityQst04";
	final static String STR_DEFICIENCYGRAVITY_REMARK_QST04 = "bb_defGravityRmrk04";
	final static String STR_DEFICIENCYGRAVITY_QST05 = "bb_deficiencyGravityQst05";
	final static String STR_DEFICIENCYGRAVITY_REMARK_QST05 = "bb_defGravityRmrk05";
	final static String STR_DEFICIENCYGRAVITY_QST06 = "bb_deficiencyGravityQst06";
	final static String STR_DEFICIENCYGRAVITY_SUGGRESULT = "bb_defGravitySuggResult";
	final static String STR_DEFICIENCYGRAVITY_RESULT = "bb_defGravityResult";
	
	final static String STR_TASKITEM_STARTDATE = "bb_taskItemStartDate";
	final static String STR_TASKTEIM_ENDDATE = "bb_taskItemEndDate";
	
	final static String STR_CREATORS_GROUP = "bb_creators_group";
	
	final static IEnumAttributeType ATTR_ISSUETYPE = create(OBJECT_TYPE, STR_ISSUETYPE);
	final static IDateAttributeType ATTR_FIRST_PLANNED_ENDDATE = create(OBJECT_TYPE, STR_FIRST_PLANNED_ENDDATE);
	final static IEnumAttributeType ATTR_ISSUEGRADE = create(OBJECT_TYPE, STR_ISSUEGRADE);
	final static IEnumAttributeType ATTR_ISSUEORIGN = create(OBJECT_TYPE, STR_ISSUEORIGN);
	final static IBooleanAttributeType ATTR_ISTECHSUPPORTNEEDED = create(OBJECT_TYPE, STR_ISTECHSUPPORTNEEDED);
	final static IEnumAttributeType ATTR_MGMT_APPROVERL1_STATUS = create(OBJECT_TYPE, STR_MGMT_APPROVERL1_STATUS);
	final static ITextAttributeType ATTR_MGMT_APPROVERL1_REMARK = create(OBJECT_TYPE, STR_MGMT_APPROVERL1_REMARK);
	final static IEnumAttributeType ATTR_MGMT_APPROVERL2_STATUS = create(OBJECT_TYPE, STR_MGMT_APPROVERL2_STATUS);
	final static ITextAttributeType ATTR_MGMT_APPROVERL2_REMARK = create(OBJECT_TYPE, STR_MGMT_APPROVERL2_REMARK);
	final static IEnumAttributeType ATTR_IMPL_APPROVERL1_STATUS = create(OBJECT_TYPE, STR_IMPL_APPROVERL1_STATUS);	
	final static IListAttributeType LIST_IMPL_APPROVERL1_GROUP = create(OBJECT_TYPE, STR_IMPL_APPROVERL1_GROUP);
	final static IListAttributeType LIST_MGMT_APPROVERL1_GROUP = create(OBJECT_TYPE, STR_MGMT_APPROVERL1_GROUP);
	final static IListAttributeType LIST_MGMT_APPROVERL2_GROUP = create(OBJECT_TYPE, STR_MGMT_APPROVERL2_GROUP);
	final static IEnumAttributeType ATTR_IMPL_APPROVERL2_STATUS = create(OBJECT_TYPE, STR_IMPL_APPROVERL2_STATUS);
	final static IListAttributeType LIST_IMPL_APPROVERL2_GROUP = create(OBJECT_TYPE, STR_IMPL_APPROVERL2_GROUP);
	final static IListAttributeType LIST_TECH_APPROVERL1_GROUP = create(OBJECT_TYPE, STR_TECH_APPROVERL1_GROUP);
	final static IEnumAttributeType ATTR_TECH_APPROVERL1_STATUS = create(OBJECT_TYPE, STR_TECH_APPROVERL1_STATUS);
	final static IListAttributeType LIST_TECH_APPROVERL2_GROUP = create(OBJECT_TYPE, STR_TECH_APPROVERL2_GROUP);
	final static IBooleanAttributeType ATTR_INJUREDISSUE = create(OBJECT_TYPE, STR_INJUREDISSUE);
	final static IEnumAttributeType ATTR_EXEC_IMPLAPPRL1_STATUS = create(OBJECT_TYPE, STR_EXEC_IMPLAPPRL1_STATUS);
	final static IEnumAttributeType ATTR_EXEC_IMPLAPPRL2_STATUS = create(OBJECT_TYPE, STR_EXEC_IMPLAPPRL2_STATUS);
	final static IEnumAttributeType ATTR_EXEC_MGMTAPPRL1_STATUS = create(OBJECT_TYPE, STR_EXEC_MGMTAPPRL1_STATUS);
	final static ITextAttributeType ATTR_EXEC_MGMTAPPRL1_REMARK = create(OBJECT_TYPE, STR_EXEC_MGMTAPPRL1_REMARK);
	final static IDateAttributeType ATTR_DEALINGDEADLINE_DATE = create(OBJECT_TYPE, STR_DEALINGDEADLINE_DATE);
	final static IDateAttributeType ATTR_CREATOR_DATE = create(OBJECT_TYPE, STR_CREATOR_DATE);
	final static IDateAttributeType ATTR_START_DATE = create(OBJECT_TYPE, STR_START_DATE);
	final static ILongAttributeType ATTR_PLANNED_DAYS = create(OBJECT_TYPE, STR_PLANNED_DAYS);
	final static IDateAttributeType ATTR_EXEC_IMPLAPPRL1_DATE = create(OBJECT_TYPE, STR_EXEC_IMPLAPPRL1_DATE);
	final static IDateAttributeType ATTR_EXEC_IMPLAPPRL2_DATE = create(OBJECT_TYPE, STR_EXEC_IMPLAPPRL2_DATE);
	final static ILongAttributeType ATTR_WORKED_DAYS = create(OBJECT_TYPE, STR_WORKED_DAYS);
	final static IDoubleAttributeType ATTR_PERCENT_DAYS = create(OBJECT_TYPE, STR_PERCENT_DAYS);
	final static IBooleanAttributeType ATTR_ISOVERDUEEXECUTION = create(OBJECT_TYPE, STR_ISOVERDUEEXECUTION);
	final static IDateAttributeType ATTR_IMPL_APPROVERL2_DATE = create(OBJECT_TYPE, STR_IMPL_APPROVERL2_DATE);
	final static IDateAttributeType ATTR_IMPL_APPRL2ATB_DATE = create(OBJECT_TYPE, STR_IMPL_APPRL2ATB_DATE);
	final static IDateAttributeType ATTR_MGMT_APPROVERL2_DATE = create(OBJECT_TYPE, STR_MGMT_APPROVERL2_DATE);
	final static IDateAttributeType ATTR_MGMT_APPRL2ATB_DATE = create(OBJECT_TYPE, STR_MGMT_APPRL2ATB_DATE);
	final static IEnumAttributeType ATTR_MGMT_APPRL2ATB_STATUS = create(OBJECT_TYPE, STR_MGMT_APPRL2ATB_STATUS);
	final static IDateAttributeType ATTR_MGMT_APPROVERL1_DATE = create(OBJECT_TYPE, STR_MGMT_APPROVERL1_DATE);
	final static IDateAttributeType ATTR_MGMT_APPRL1ATB_DATE = create(OBJECT_TYPE, STR_MGMT_APPRL1ATB_DATE);
	final static IListAttributeType LIST_OWNERS_GROUP = create(OBJECT_TYPE, STR_OWNERS_GROUP);
	final static IListAttributeType LIST_REVIEWERS_GROUP = create(OBJECT_TYPE, STR_REVIEWERS_GROUP);
	final static IEnumAttributeType ATTR_TECH_APPROVERL2_STATUS = create(OBJECT_TYPE, STR_TECH_APPROVERL2_STATUS);
	final static IListAttributeType LIST_IMPL_DOCUMENTS = create(OBJECT_TYPE, STR_IMPL_DOCUMENTS);
	final static IListAttributeType LIST_MGMT_APPR_ORGUNIT = create(OBJECT_TYPE, STR_MGMT_APPR_ORGUNIT);
	final static IListAttributeType LIST_IMPL_APPR_ORGUNIT = create(OBJECT_TYPE, STR_IMPL_APPR_ORGUNIT);
	final static IListAttributeType LIST_TECH_APPR_ORGUNIT = create(OBJECT_TYPE, STR_TECH_APPR_ORGUNIT);
	final static IDateAttributeType ATTR_CONCLUSION_DATE = create(OBJECT_TYPE, STR_CONCLUSION_DATE);
	final static IDateAttributeType ATTR_CERTIFICATION_DATE = create(OBJECT_TYPE, STR_CERTIFICATION_DATE);
	final static IDateAttributeType ATTR_EXEC_MGMT_APPRL1_DATE = create(OBJECT_TYPE, STR_EXEC_MGMT_APPRL1_DATE);
	
	final static IBooleanAttributeType ATTR_DEFICIENCYGRAVITY_QST01 = create(OBJECT_TYPE, STR_DEFICIENCYGRAVITY_QST01);
	final static IBooleanAttributeType ATTR_DEFICIENCYGRAVITY_QST02 = create(OBJECT_TYPE, STR_DEFICIENCYGRAVITY_QST02);
	final static IBooleanAttributeType ATTR_DEFICIENCYGRAVITY_QST03 = create(OBJECT_TYPE, STR_DEFICIENCYGRAVITY_QST03);
	final static IBooleanAttributeType ATTR_DEFICIENCYGRAVITY_QST04 = create(OBJECT_TYPE, STR_DEFICIENCYGRAVITY_QST04);
	final static ITextAttributeType ATTR_DEFICIENCYGRAVITY_REMARK_QST04 = create(OBJECT_TYPE, STR_DEFICIENCYGRAVITY_REMARK_QST04);
	final static IBooleanAttributeType ATTR_DEFICIENCYGRAVITY_QST05 = create(OBJECT_TYPE, STR_DEFICIENCYGRAVITY_QST05);
	final static ITextAttributeType ATTR_DEFICIENCYGRAVITY_REMARK_QST05 = create(OBJECT_TYPE, STR_DEFICIENCYGRAVITY_REMARK_QST05);
	final static IBooleanAttributeType ATTR_DEFICIENCYGRAVITY_QST06 = create(OBJECT_TYPE, STR_DEFICIENCYGRAVITY_QST06);
	final static IEnumAttributeType ATTR_DEFICIENCYGRAVITY_SUGGRESULT = create(OBJECT_TYPE, STR_DEFICIENCYGRAVITY_SUGGRESULT);
	final static IEnumAttributeType ATTR_DEFICIENCYGRAVITY_RESULT = create(OBJECT_TYPE, STR_DEFICIENCYGRAVITY_RESULT);
	
	final static IDateAttributeType ATTR_TASKITEM_STARTDATE = create(OBJECT_TYPE, STR_TASKITEM_STARTDATE);
	final static IDateAttributeType ATTR_TASKITEM_ENDDATE = create(OBJECT_TYPE, STR_TASKTEIM_ENDDATE);
	
	final static IListAttributeType LIST_CREATORS_GROUP = create(OBJECT_TYPE, STR_CREATORS_GROUP);
}
