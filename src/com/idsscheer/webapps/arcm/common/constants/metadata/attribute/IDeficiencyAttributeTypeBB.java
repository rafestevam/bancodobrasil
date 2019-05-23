/**
 * 
 */
package com.idsscheer.webapps.arcm.common.constants.metadata.attribute;

import static com.idsscheer.webapps.arcm.common.constants.metadata.attribute.MetadataConstantsUtil.create;

import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IBooleanAttributeType;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IDateAttributeType;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IEnumAttributeType;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IListAttributeType;

/**
 * <p>  this interface defines all attribute-types and attribute-names of DEFICIENCY IAppObj implementation.
 *      names of common attributes e.g. OBJ_ID and GUID are defined in {@link IObjectAttributeType} 
 *      additional attribute-names of versionized objects are defined in {@link IVersionAttributeType}
 * </p>
 *
 * <p> This is a customization for <b><code>Banco do Brasil Project</code></b> </p>
 *@author brmob
 *@since v20150209-CUST-DeficiencyMgmt
 *@see IDeficiencyAttributeType
 */
public interface IDeficiencyAttributeTypeBB extends IDeficiencyAttributeType {
	
	public static final String STR_CONTROL_ASSERTIONS = "bb_controlAssertions";
	public static final String STR_FINACCOUNT_ASSERTIONS = "bb_finAccountAssertions";
	public static final String STR_BB_TESTCASE = "bb_testcase";
	public static final String STR_INICIO_CICLO_AVALIATORIO = "bb_inicioClicloAvaliatorio";
	public static final String STR_FIM_CICLO_AVALIATORIO = "bb_fimClicloAvaliatorio";
	public static final String STR_SCOPE = "bb_scope";
	public static final String STR_APPSYSTEMS = "bb_appSystems";
	public static final String STR_PLAN_DEPARTAMENTAL = "bb_plan_departamental";
	
	
	public static final IEnumAttributeType ATTR_CONTROL_ASSERTIONS = create(OBJECT_TYPE, STR_CONTROL_ASSERTIONS);
	public static final IEnumAttributeType ATTR_FINACCOUNT_ASSERTIONS = create(OBJECT_TYPE, STR_FINACCOUNT_ASSERTIONS);
	public static final IListAttributeType LIST_BB_TESTCASE = create(OBJECT_TYPE, STR_BB_TESTCASE);
	public static final IDateAttributeType ATTR_INICIO_CICLO_AVALIATORIO = create(OBJECT_TYPE, STR_INICIO_CICLO_AVALIATORIO);
	public static final IDateAttributeType ATTR_FIM_CICLO_AVALIATORIO = create(OBJECT_TYPE, STR_FIM_CICLO_AVALIATORIO);
	public static final IEnumAttributeType ATTR_SCOPE = create(OBJECT_TYPE, STR_SCOPE);
	public static final IListAttributeType LIST_APPSYSTEMS = create(OBJECT_TYPE, STR_APPSYSTEMS);
	public static final IBooleanAttributeType ATTR_PLAN_DEPARTAMENTAL = create(OBJECT_TYPE, STR_PLAN_DEPARTAMENTAL);
			
}
