/**
 * 
 */
package com.idsscheer.webapps.arcm.common.constants.metadata.attribute;

import static com.idsscheer.webapps.arcm.common.constants.metadata.attribute.MetadataConstantsUtil.*;

import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IEnumAttributeType;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IListAttributeType;
/**
 * <p>  this interface defines all attribute-types and attribute-names of ANSWER IAppObj implementation.
 *      names of common attributes e.g. OBJ_ID and GUID are defined in {@link IObjectAttributeType} 
 *      additional attribute-names of versionized objects are defined in {@link IVersionAttributeType}
 * </p>
 *
 * <p> This is a customization for <b><code>Banco do Brasil Project</code></b> </p>
 *@author brmob
 *@since v20150105-CUST-SurveyMgmt
 *@see IAnswerAttributeType
 */
public interface IAnswerAttributeTypeBB extends IAnswerAttributeType {
	
	final static String STR_REVIEWER_EVALUATION = "bb_reviewerEvaluation";
	
	final static String STR_ANSWER_DOCUMENTS = "bb_answerDocuments";
	
	final IEnumAttributeType ATTR_REVIEWER_EVALUATION = create(OBJECT_TYPE, STR_REVIEWER_EVALUATION);
	
	final IListAttributeType ATTR_ANSWER_DOCUMENTS = create(OBJECT_TYPE, STR_ANSWER_DOCUMENTS);
	


}
