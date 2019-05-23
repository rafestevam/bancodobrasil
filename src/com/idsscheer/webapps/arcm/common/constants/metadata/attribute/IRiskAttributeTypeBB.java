/**
 * 
 */
package com.idsscheer.webapps.arcm.common.constants.metadata.attribute;

import static com.idsscheer.webapps.arcm.common.constants.metadata.attribute.MetadataConstantsUtil.create;

import com.idsscheer.webapps.arcm.bl.component.riskmanagement.commands.job.generator.RiskassessmentGenerateCommand;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IEnumAttributeType;

/**
 * <p>  this interface defines all attribute-types and attribute-names of RISK IAppObj implementation.
 *      names of common attributes e.g. OBJ_ID and GUID are defined in {@link IObjectAttributeType} 
 *      additional attribute-names of versionized objects are defined in {@link IVersionAttributeType}
 * </p>
 * <p> This is a customization for <b><code>Banco do Brasil Project</code></b> </p>
 *
 * @author brmob
 * @since v20150728-UPGRADE-ARCM98
 * @see RiskassessmentGenerateCommand
 */
public interface IRiskAttributeTypeBB extends IRiskAttributeType {
	
	final static String STR_TYPE = "type";
	
	final IEnumAttributeType ATTR_TYPE = create(OBJECT_TYPE, STR_TYPE); 

}
