/**
 * 
 */
package com.idsscheer.webapps.arcm.common.constants.metadata.attribute;

import static com.idsscheer.webapps.arcm.common.constants.metadata.attribute.MetadataConstantsUtil.create;

import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IListAttributeType;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IStringAttributeType;

/**
 * <p>  this interface defines all attribute-types and attribute-names of USERGROUP IAppObj implementation.
 *      names of common attributes e.g. OBJ_ID and GUID are defined in {@link IObjectAttributeType} 
 *      additional attribute-names of versionized objects are defined in {@link IVersionAttributeType}
 * </p>
 *
 * <p> This is a customization for <b><code>Banco do Brasil Project</code></b> </p>
 *@author brmob
 *@since v20160119-MNT-BRMOB.UATEnhancements
 *@see IUsergroupAttributeType
 */
public interface IUsergroupAttributeTypeBB extends IUsergroupAttributeType {
	
	final static String STR_BB_ORGUNIT = "bb_orgunit";
	final static String STR_BB_EMAIL = "bb_email";
	
	final IListAttributeType LIST_BB_ORGUNIT = create(OBJECT_TYPE, STR_BB_ORGUNIT);
	final IStringAttributeType ATTR_BB_EMAIL = create(OBJECT_TYPE, STR_BB_EMAIL);
}
