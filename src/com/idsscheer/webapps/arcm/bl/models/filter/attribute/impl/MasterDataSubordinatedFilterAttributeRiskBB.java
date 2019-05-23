package com.idsscheer.webapps.arcm.bl.models.filter.attribute.impl;

import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.common.constants.metadata.Enumerations;
import com.idsscheer.webapps.arcm.common.constants.metadata.EnumerationsBB;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;
import com.idsscheer.webapps.arcm.config.metadata.filter.IFilterAttributeType;

public class MasterDataSubordinatedFilterAttributeRiskBB extends MasterDataSubordinatedFilterAttributeRisk {

	public MasterDataSubordinatedFilterAttributeRiskBB(
			IUserContext userContext, IFilterAttributeType filterAttributeConfig) {
		super(userContext, filterAttributeConfig);
	}
	@Override
	protected String[] getIDListStrings(IEnumerationItem hierarchyType)
	{
		if (Enumerations.HIERARCHY_TYPE.ORGUNIT.equals(hierarchyType))
			return new String[] { "idlist_orgH" };
		if (Enumerations.HIERARCHY_TYPE.TESTER.equals(hierarchyType))
			return new String[] { "idlist_testerH" };
		if (Enumerations.HIERARCHY_TYPE.PROCESS.equals(hierarchyType))
			return new String[] { "idlist_controlHPr" };
		if(EnumerationsBB.HIERACHY_TYPE_BB.BB_FATOR_DE_RISCO.equals(hierarchyType))
			return new String[] { "idlist_riskHFR" };
		if (Enumerations.HIERARCHY_TYPE.FINANCIALACCOUNT.equals(hierarchyType)) {
			return new String[] { "idlist_riskHFA" };
		}if (EnumerationsBB.HIERACHY_TYPE_BB.BB_PRODUCT.equals(hierarchyType)) {
			return new String[] { "idlist_processHpro" };
		}
		return new String[] { "idlist_riskHOR" };
	}


	@Override
	protected String[] getIDStrings(IEnumerationItem hierarchyType)
	{
		if (Enumerations.HIERARCHY_TYPE.ORGUNIT.equals(hierarchyType))
			return new String[] { "id_orgH" };
		if (Enumerations.HIERARCHY_TYPE.TESTER.equals(hierarchyType))
			return new String[] { "id_testerH" };
		if (Enumerations.HIERARCHY_TYPE.PROCESS.equals(hierarchyType))
			return new String[] { "id_controlHPr" };
		if(EnumerationsBB.HIERACHY_TYPE_BB.BB_FATOR_DE_RISCO.equals(hierarchyType))
			return new String[] { "id_riskHFR" };
		if (Enumerations.HIERARCHY_TYPE.FINANCIALACCOUNT.equals(hierarchyType)) {
			return new String[] { "id_riskHFA" };
		}if (EnumerationsBB.HIERACHY_TYPE_BB.BB_PRODUCT.equals(hierarchyType)) {
			return new String[] { "id_processHpro" };
		}
		return new String[] { "id_riskHOR" };
	}


	@Override
	protected String[] getExcludingFilterIDStrings(IEnumerationItem hierarchyType)
	{
		if (Enumerations.HIERARCHY_TYPE.ORGUNIT.equals(hierarchyType))
			return new String[] { "idforexcludefilter_orgH" };
		if (Enumerations.HIERARCHY_TYPE.TESTER.equals(hierarchyType))
			return new String[] { "idforexcludefilter_testerH" };
		if (Enumerations.HIERARCHY_TYPE.PROCESS.equals(hierarchyType))
			return new String[] { "idforexcludefilter_controlHPr" };
		if(EnumerationsBB.HIERACHY_TYPE_BB.BB_FATOR_DE_RISCO.equals(hierarchyType))
			return new String[] { "idforexcludefilter_riskHFR" };
		if (Enumerations.HIERARCHY_TYPE.FINANCIALACCOUNT.equals(hierarchyType)) {
			return new String[] { "idforexcludefilter_riskHFA" };
		}if (EnumerationsBB.HIERACHY_TYPE_BB.BB_PRODUCT.equals(hierarchyType)) {
			return new String[] { "idforexcludefilter_processHpro" };
		}
		return new String[] { "idforexcludefilter_riskHOR" };
	}
	
}