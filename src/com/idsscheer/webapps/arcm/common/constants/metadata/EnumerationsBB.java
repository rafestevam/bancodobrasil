/**
 * 
 */
package com.idsscheer.webapps.arcm.common.constants.metadata;

import com.idsscheer.webapps.arcm.common.constants.metadata.Enumerations;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.EnumerationWrapper;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumeration;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.JobEnumerationItem;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.MessageTemplateEnumerationItem;

/**
 * This is an Enumerations class for BdB customized enumerations.
 * <p> This is a customization for <b><code>Banco do Brasil Project</code></b> </p>
 * @since v20150126-CUST-Compliance
 * @author brmob
 * @see Enumerations
 */
public class EnumerationsBB extends Enumerations {

	
	public abstract interface TESTTYPE_BB extends TESTTYPE{
		static final IEnumerationItem BB_ELC = lookupEnumerationItem(ENUM, "bb_elc");
		static final IEnumerationItem BB_CONFORMIDADE = lookupEnumerationItem(ENUM, "bb_conformidade");
		static final IEnumerationItem BB_VALIDACAO = lookupEnumerationItem(ENUM, "bb_validacao");
		static final IEnumerationItem BB_DNE = lookupEnumerationItem(ENUM, "bb_dne");
	}
	
	public abstract interface BB_DEFTYPE {
		static final EnumerationWrapper ENUM = new EnumerationWrapper("bb_defType");
		static final IEnumerationItem DESIGN = lookupEnumerationItem(ENUM, "design");
		static final IEnumerationItem CONTROL = lookupEnumerationItem(ENUM, "control");
		static final IEnumerationItem GAP_CONTROL = lookupEnumerationItem(ENUM, "gap_control");
	}
	
	public abstract interface HIERACHY_TYPE_BB  extends HIERARCHY_TYPE{
		static final IEnumerationItem BB_PRODUCT 		= lookupEnumerationItem(ENUM, "bb_product");
		static final IEnumerationItem BB_BUSINESSLINE 	= lookupEnumerationItem(ENUM, "bb_businessline");
		static final IEnumerationItem BB_ICR 			= lookupEnumerationItem(ENUM, "bb_icr");
		static final IEnumerationItem BB_FATOR_DE_RISCO = lookupEnumerationItem(ENUM, "bb_riskfactor");
	}
	
	public abstract interface BB_TIPO_UNIDADE_ORGANIZACIONAL {
		static final EnumerationWrapper ENUM = new EnumerationWrapper("bb_tipoUnidOrganizacional");
		static final IEnumerationItem ALL = lookupEnumerationItem(ENUM, "all");
		static final IEnumerationItem UOR = lookupEnumerationItem(ENUM, "bb_uor");
		static final IEnumerationItem IOR = lookupEnumerationItem(ENUM, "bb_ior");
	}
	
	public abstract interface ISSUETYPE {
		static final EnumerationWrapper ENUM = new EnumerationWrapper("bb_issueType");
		static final IEnumerationItem ALL = lookupEnumerationItem(ENUM, "all");
		static final IEnumerationItem PLEASE_SELECT	= lookupEnumerationItem(ENUM, "please_select");
		static final IEnumerationItem FAULTLOG = lookupEnumerationItem(ENUM, "faultlog");
		static final IEnumerationItem TECH_RECOMMENDATION = lookupEnumerationItem(ENUM, "tech_recommendation");
		static final IEnumerationItem ACTION = lookupEnumerationItem(ENUM, "action");
		static final IEnumerationItem MANIFESTATION = lookupEnumerationItem(ENUM, "manifestation");
		static final IEnumerationItem DEALING_DEADLINE = lookupEnumerationItem(ENUM, "dealing_deadline");
		static final IEnumerationItem SAD = lookupEnumerationItem(ENUM, "sad");
		static final IEnumerationItem EXTRAPOLATION_SHEET = lookupEnumerationItem(ENUM, "extra_sheet");
		
	}
	
	public abstract interface ISSUEGRADE {
		static final EnumerationWrapper ENUM = new EnumerationWrapper("bb_issueGrade");
		static final IEnumerationItem ONE = lookupEnumerationItem(ENUM, "one");
		static final IEnumerationItem TWO = lookupEnumerationItem(ENUM, "two");
		static final IEnumerationItem THREE = lookupEnumerationItem(ENUM, "three");
		static final IEnumerationItem FOUR = lookupEnumerationItem(ENUM, "four");
		
	}
	
	public abstract interface ISSUE_APPROVERL1_STATUS {
		static final EnumerationWrapper ENUM = new EnumerationWrapper("bb_standardApproverL1_status");
		static final IEnumerationItem ALL          	= lookupEnumerationItem(ENUM, "all");
		static final IEnumerationItem PLEASE_SELECT	= lookupEnumerationItem(ENUM, "please_select");
		static final IEnumerationItem UNSPECIFIED  	= lookupEnumerationItem(ENUM, "unspecified");
		static final IEnumerationItem ACCEPTED     	= lookupEnumerationItem(ENUM, "accepted");
		static final IEnumerationItem REJECTED     	= lookupEnumerationItem(ENUM, "rejected");
		static final IEnumerationItem NOT_ACCEPTED 	= lookupEnumerationItem(ENUM, "not_accepted");
	}
	
	public abstract interface ISSUE_APPROVERL2_STATUS {
		static final EnumerationWrapper ENUM = new EnumerationWrapper("bb_standardApproverL2_status");
		static final IEnumerationItem ALL          	= lookupEnumerationItem(ENUM, "all");
		static final IEnumerationItem PLEASE_SELECT	= lookupEnumerationItem(ENUM, "please_select");
		static final IEnumerationItem UNSPECIFIED  	= lookupEnumerationItem(ENUM, "unspecified");
		static final IEnumerationItem ACCEPTED     	= lookupEnumerationItem(ENUM, "accepted");
		static final IEnumerationItem REJECTED     	= lookupEnumerationItem(ENUM, "rejected");
		static final IEnumerationItem NOT_ACCEPTED 	= lookupEnumerationItem(ENUM, "not_accepted");
	}
	
	public abstract interface INITIATORS_BB extends INITIATORS{
		static final MessageTemplateEnumerationItem QUESTIONNAIRE_GENERATORJOB = lookupEnumerationItem(ENUM, "questionnaire_generatorjob");
		static final MessageTemplateEnumerationItem GENERATORJOB_EMPTY_GROUP = lookupEnumerationItem(ENUM, "generatorjob_empty_group");
		static final MessageTemplateEnumerationItem FOLLOWUPJOB_EMPTY_GROUP = lookupEnumerationItem(ENUM, "followupjob_empty_group");
		static final MessageTemplateEnumerationItem USER_ADDED_EMPTY_GROUP = lookupEnumerationItem(ENUM, "user_added_empty_group");
		static final MessageTemplateEnumerationItem LAST_USER_REMOVED_GROUP = lookupEnumerationItem(ENUM, "last_user_removed_group");
		static final MessageTemplateEnumerationItem TESTCASE_DEFICIENCY_REACTIVATED = lookupEnumerationItem(ENUM, "testcase_deficiency_reactivated");
		static final MessageTemplateEnumerationItem ISSUE_GENERAL = lookupEnumerationItem(ENUM, "issue_general");
		static final MessageTemplateEnumerationItem CONTROL_CHECKER_SUCCESSFUL = lookupEnumerationItem(ENUM, "control_checker_successful");
		static final MessageTemplateEnumerationItem CONTROL_CHECKER_FAILED = lookupEnumerationItem(ENUM, "control_checker_failed");
	}
	
	public static abstract interface JOBS_BB  extends JOBS {
		public static final JobEnumerationItem CONTROLCHECKERJOB = lookupEnumerationItem(ENUM, "controlCheckerJob");
	}

	
	public abstract interface ISSUE_REVIEWER_STATUS_BB extends ISSUE_REVIEWER_STATUS {
		static final IEnumerationItem UNDECIDED = lookupEnumerationItem(ENUM, "undecided");
	}
	
	public abstract interface ISSUE_EXECAPPRL1_STATUS {
		static final EnumerationWrapper ENUM = new EnumerationWrapper("bb_executionApproverL1_status");
		static final IEnumerationItem ALL          	= lookupEnumerationItem(ENUM, "all");
		static final IEnumerationItem PLEASE_SELECT	= lookupEnumerationItem(ENUM, "please_select");
		static final IEnumerationItem UNSPECIFIED  	= lookupEnumerationItem(ENUM, "unspecified");
		static final IEnumerationItem ACCEPTED     	= lookupEnumerationItem(ENUM, "accepted");
		static final IEnumerationItem REJECTED     	= lookupEnumerationItem(ENUM, "rejected");
		static final IEnumerationItem NOT_ACCEPTED 	= lookupEnumerationItem(ENUM, "not_accepted");
	}
	
	public abstract interface ISSUE_EXECAPPRL2_STATUS {
		static final EnumerationWrapper ENUM = new EnumerationWrapper("bb_executionApproverL2_status");
		static final IEnumerationItem ALL          	= lookupEnumerationItem(ENUM, "all");
		static final IEnumerationItem PLEASE_SELECT	= lookupEnumerationItem(ENUM, "please_select");
		static final IEnumerationItem UNSPECIFIED  	= lookupEnumerationItem(ENUM, "unspecified");
		static final IEnumerationItem ACCEPTED     	= lookupEnumerationItem(ENUM, "accepted");
		static final IEnumerationItem REJECTED     	= lookupEnumerationItem(ENUM, "rejected");
		static final IEnumerationItem NOT_ACCEPTED 	= lookupEnumerationItem(ENUM, "not_accepted");
	}
	
	public abstract interface ISSUESTATETIME_BB extends ISSUESTATETIME{
		static final IEnumerationItem PLANNING	= lookupEnumerationItem(ENUM, "planning");
		static final IEnumerationItem INJURED	= lookupEnumerationItem(ENUM, "injured");
		static final IEnumerationItem FINISHED	= lookupEnumerationItem(ENUM, "finished");
	}
	
	public interface DEFICIENCYGRAVITY_RESULT {
		static final EnumerationWrapper ENUM = new EnumerationWrapper("bb_deficiencyGravityResult");
		static final IEnumerationItem CONTROL_DEFICIENCY	= lookupEnumerationItem(ENUM, "control_def");
		static final IEnumerationItem SIGNIFICANT_DEFICIENCY	= lookupEnumerationItem(ENUM, "significant_def");
		static final IEnumerationItem MATERIAL_WEAKNESS	= lookupEnumerationItem(ENUM, "material_weakness");
	}
	
	public abstract interface USERROLE_TYPE_BB extends USERROLE_TYPE{
		public static final IEnumerationItem ISSUECREATOR = lookupEnumerationItem(ENUM, "issuecreator");
		public static final IEnumerationItem ISSUEMGMTAPPROVER_L1 = lookupEnumerationItem(ENUM, "issuemgmtapproverl1");
		public static final IEnumerationItem ISSUEMGMTAPPROVER_L2 = lookupEnumerationItem(ENUM, "issuemgmtapproverl2");
		public static final IEnumerationItem ISSUEIMPLAPPROVER_L1 = lookupEnumerationItem(ENUM, "issueimplapproverl1");
		public static final IEnumerationItem ISSUEIMPLAPPROVER_L2 = lookupEnumerationItem(ENUM, "issueimplapproverl2");
		public static final IEnumerationItem ISSUETECHAPPROVER_L1 = lookupEnumerationItem(ENUM, "issuetechapproverl1");
		public static final IEnumerationItem ISSUETECHAPPROVER_L2 = lookupEnumerationItem(ENUM, "issuetechapproverl2");
		public static final IEnumerationItem ISSUEOWNER = lookupEnumerationItem(ENUM, "issueowner");
		public static final IEnumerationItem ISSUEREVIEWER = lookupEnumerationItem(ENUM, "issuereviewer");
	}
	
	public abstract interface LOSSDB_LOSS_TYPE_BB extends LOSSDB_LOSS_TYPE{
		public static final IEnumerationItem LOST_INCOME = lookupEnumerationItem(ENUM, "bb_receitasPerdidas");
	}
	
    @SuppressWarnings("unchecked")
	private static <T extends IEnumerationItem> T lookupEnumerationItem(final IEnumeration enumeration, final String itemId) {
        return (T)enumeration.getItemById(itemId);
    }
	

}
