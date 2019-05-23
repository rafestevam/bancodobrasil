/**
 * 
 */
package com.idsscheer.webapps.arcm.common.constants.metadata.attribute;

import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IBooleanAttributeType;

/**
 * @author Administrator
 *
 */
public interface ITestdefinitionAttributeTypeBB extends ITestdefinitionAttributeType {
	
	public static final String STR_TESTEVALIDACAO = "bb_testeValidacao";
	
	public static final IBooleanAttributeType ATTR_TESTEVALIDACAO = (IBooleanAttributeType)MetadataConstantsUtil.create(OBJECT_TYPE, STR_TESTEVALIDACAO);

}
