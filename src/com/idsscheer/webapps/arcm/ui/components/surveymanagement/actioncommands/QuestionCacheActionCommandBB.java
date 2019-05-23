/**
 * 
 */
package com.idsscheer.webapps.arcm.ui.components.surveymanagement.actioncommands;

import com.idsscheer.webapps.arcm.bl.common.support.AppObjUtility;
import com.idsscheer.webapps.arcm.bl.exception.BLException;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IListAttribute;
import com.idsscheer.webapps.arcm.common.constants.metadata.Enumerations;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IQuestionAttributeTypeBB;
import com.idsscheer.webapps.arcm.common.util.ARCMCollections;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;
import com.idsscheer.webapps.arcm.ui.framework.actioncommands.ActionCommandException;

/**
 * @author Administrator
 *
 */
public class QuestionCacheActionCommandBB extends QuestionCacheActionCommand {
	
	
	@Override
	protected void assumeData(String... p_exculdeParameters) {
		super.assumeData(p_exculdeParameters);
		if (this.doClearOptions) {
			IListAttribute reviewerOptionSet = this.formModel.getAppObj().getAttribute(IQuestionAttributeTypeBB.LIST_REVIEWER_OPTIONSET);
			if (!reviewerOptionSet.isEmpty()) {
				try {
					reviewerOptionSet.removeAllElements(getFullGrantUserContext());
				} catch (BLException e) {
					e.printStackTrace();
					throw new ActionCommandException(e);
				} 
			}
		}
	}
	
	@Override
	protected void execute() {
		super.execute();
		IAppObj question = formModel.getAppObj();
		IEnumerationItem type = ARCMCollections.extractSingleEntry(question.getAttribute(IQuestionAttributeTypeBB.ATTR_TYPE).getRawValue(), true);
		if (!type.equals(Enumerations.QUESTION_TYPE.SINGLE_CHOICE_COMBOBOX) &&
				!type.equals(Enumerations.QUESTION_TYPE.SINGLE_CHOICE_RADIO) &&
				!type.equals(Enumerations.QUESTION_TYPE.MULTIPLE_CHOICE)) {
			AppObjUtility.copyAttributeValue(question, question, IQuestionAttributeTypeBB.LIST_REVIEWER_OPTIONSET, IQuestionAttributeTypeBB.LIST_OPTIONSET);
		}
		
	}
}
