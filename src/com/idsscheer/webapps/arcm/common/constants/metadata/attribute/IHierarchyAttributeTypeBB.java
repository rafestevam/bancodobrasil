/**
 * 
 */
package com.idsscheer.webapps.arcm.common.constants.metadata.attribute;

import static com.idsscheer.webapps.arcm.common.constants.metadata.attribute.MetadataConstantsUtil.create;

import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IEnumAttributeType;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IListAttributeType;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IStringAttributeType;

/**
 * <p>  this interface defines all attribute-types and attribute-names of HIERARCHY IAppObj implementation.
 *      names of common attributes e.g. OBJ_ID and GUID are defined in {@link IObjectAttributeType} 
 *      additional attribute-names of versionized objects are defined in {@link IVersionAttributeType}
 * </p>
 *
 * <p> This is a customization for <b><code>Banco do Brasil Project</code></b> </p>
 *@author brmob
 *@since v20150203-CUST-PRDIntegration
 *@see IHierarchyAttributeType
 */
public interface IHierarchyAttributeTypeBB extends IHierarchyAttributeType {
	
	final static String STR_FINACCOUNTASSERTIONS = "bb_finaccountAssertions";
	final static String STR_FINACCOUNT = "bb_financcount";
	final static String STR_DEPTMANAGER = "bb_deptManager";
	final static String STR_PREFIXO = "bb_prefixo";
	final static String STR_EMAIL_UOR = "bb_emailUOR";
	final static String STR_CODIGO_UOR = "bb_codigoUOR";
	final static String STR_NIVEL = "bb_nivel";
	
	final IEnumAttributeType ATTR_FINACCOUNTASSERTIONS = create(OBJECT_TYPE, STR_FINACCOUNTASSERTIONS);
	final IListAttributeType LIST_DEPTMANAGER = create(OBJECT_TYPE, STR_DEPTMANAGER);
	final IStringAttributeType ATTR_PREFIXO = create(OBJECT_TYPE, STR_PREFIXO);
	final IStringAttributeType ATTR_EMAIL_UOR = create(OBJECT_TYPE, STR_EMAIL_UOR);
	final IStringAttributeType ATTR_CODIGO_UOR = create(OBJECT_TYPE, STR_CODIGO_UOR);
	final IStringAttributeType ATTR_NIVEL = create(OBJECT_TYPE, STR_NIVEL);
	
	final IListAttributeType LIST_FINACCOUNT = create(OBJECT_TYPE, STR_FINACCOUNT);
			
}
