/**
 * 
 */
package com.idsscheer.webapps.arcm.common.constants.metadata.attribute;

import static com.idsscheer.webapps.arcm.common.constants.metadata.attribute.MetadataConstantsUtil.create;

import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IDoubleAttributeType;

/**
 * <p>  this interface defines all attribute-types and attribute-names of LOSS IAppObj implementation.
 *      names of common attributes e.g. OBJ_ID and GUID are defined in {@link IObjectAttributeType} 
 *      additional attribute-names of versionized objects are defined in {@link IVersionAttributeType}
 * </p>
 * <p> This is a customization for <b><code>Banco do Brasil Project</code></b> </p>
 *@author brmob
 *@since v20160711-UPGD-Upgrade982To985
 *@see IRa_impacttypeAttributeType
 */
public interface IRa_impacttypeAttributeTypeBB extends IRa_impacttypeAttributeType {
	
	public static final String STR_SUM_LOSS = "bb_sum_loss";
	public static final String STR_RED_SUM_LOSS = "bb_red_sum_loss";
	public static final String STR_FREQU_SUM_LOSS = "bb_frequ_sum_loss";
	public static final String STR_RED_FREQU_SUM_LOSS = "bb_red_frequ_sum_loss";
	public static final String STR_SUM_RELEVANT_EVENTS = "bb_frequ_relevant_events";
	public static final String STR_NEARLOSS_SUM = "bb_near_loss";
	public static final String STR_LOSTINCOME_SUM = "bb_lost_income";
	public static final String STR_INDIRECTLOSS_SUM = "bb_oportunity_cost";
	public static final String STR_GAIN_SUM = "bb_operational_gain";
	
	public static final IDoubleAttributeType ATTR_SUM_LOSS = create(OBJECT_TYPE, STR_SUM_LOSS);
	public static final IDoubleAttributeType ATTR_RED_SUM_LOSS = create(OBJECT_TYPE, STR_RED_SUM_LOSS);
	public static final IDoubleAttributeType ATTR_FREQU_SUM_LOSS = create(OBJECT_TYPE, STR_FREQU_SUM_LOSS);
	public static final IDoubleAttributeType ATTR_RED_FREQU_SUM_LOSS = create(OBJECT_TYPE, STR_RED_FREQU_SUM_LOSS);
	public static final IDoubleAttributeType ATTR_SUM_RELEVANT_EVENTS = create(OBJECT_TYPE, STR_SUM_RELEVANT_EVENTS);
	public static final IDoubleAttributeType ATTR_NEARLOSS_SUM = create(OBJECT_TYPE, STR_NEARLOSS_SUM);
	public static final IDoubleAttributeType ATTR_LOSTINCOME_SUM = create(OBJECT_TYPE, STR_LOSTINCOME_SUM);
	public static final IDoubleAttributeType ATTR_INDIRECTLOSS_SUM = create(OBJECT_TYPE, STR_INDIRECTLOSS_SUM);
	public static final IDoubleAttributeType ATTR_GAIN_SUM = create(OBJECT_TYPE, STR_GAIN_SUM);

}
