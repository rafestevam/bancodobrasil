/**
 * 
 */
package com.idsscheer.webapps.arcm.common.constants.metadata.attribute;

import static com.idsscheer.webapps.arcm.common.constants.metadata.attribute.MetadataConstantsUtil.create;

import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IDoubleAttributeType;

/**
 * <p>  this interface defines all attribute-types and attribute-names of QUESTIONNAIRE IAppObj implementation.
 *      names of common attributes e.g. OBJ_ID and GUID are defined in {@link IObjectAttributeType} 
 *      additional attribute-names of versionized objects are defined in {@link IVersionAttributeType}
 * </p>
 * <p> This is a customization for <b><code>Banco do Brasil Project</code></b> </p>
 *@author brmob
 *@since v20160119-MNT-BRMOB.UATEnhancements
 *@see IQuestionnaireAttributeType
 */
public interface IQuestionnaireAttrybuteTypeBB extends IQuestionnaireAttributeType {
	
	final static String STR_INTERVIEWEE_ACTUALSCORE = "bb_intervieweeActualScore";
	
	final IDoubleAttributeType ATTR_INTERVIEWEE_ACTUALSCORE = create(OBJECT_TYPE, STR_INTERVIEWEE_ACTUALSCORE);

}
