/**
 * 
 */
package com.idsscheer.webapps.arcm.common.constants.metadata.attribute;

import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IListAttribute;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IBooleanAttributeType;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IListAttributeType;

import static com.idsscheer.webapps.arcm.common.constants.metadata.attribute.MetadataConstantsUtil.*;

/**
 * <p>  this interface defines all attribute-types and attribute-names of QUESTION IAppObj implementation.
 *      names of common attributes e.g. OBJ_ID and GUID are defined in {@link IObjectAttributeType} 
 *      additional attribute-names of versionized objects are defined in {@link IVersionAttributeType}
 * </p>
 * <p> This is a customization for <b><code>Banco do Brasil Project</code></b> </p>
 *@author brmob
 *@since v20150105-CUST-SurveyMgmt
 *@see IQuestionAttributeType
 */
@SuppressWarnings("unused")
public interface IQuestionAttributeTypeBB extends IQuestionAttributeType {
	final static String STR_EVIDENCES_ALLOWED = "bb_evidencesAllowed";
	final static String STR_REVIEWER_OPTIONSET = "bb_reviewerOptionSet";
	
	final IBooleanAttributeType ATTR_EVIDENCESALLOWED = create(OBJECT_TYPE, STR_EVIDENCES_ALLOWED);
	
	final IListAttributeType LIST_REVIEWER_OPTIONSET = create(OBJECT_TYPE, STR_REVIEWER_OPTIONSET);

}
