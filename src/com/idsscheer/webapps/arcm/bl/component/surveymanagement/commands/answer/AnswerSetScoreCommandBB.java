/**
 * 
 */
package com.idsscheer.webapps.arcm.bl.component.surveymanagement.commands.answer;

import com.idsscheer.webapps.arcm.bl.exception.BLException;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandContext;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandResult;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IDoubleAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IEnumAttribute;
import com.idsscheer.webapps.arcm.common.constants.metadata.Enumerations;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IAnswerAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IAnswerAttributeTypeBB;
import com.idsscheer.webapps.arcm.common.util.ARCMCollections;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;

/**
 * <p>
 * Set score = optionValue * weight * questionValue.<br>
 * This is summed up for all selected options.
 * </p>
 * <p> This is a customization for <b><code>Banco do Brasil Project</code></b> </p>
 * <br>
 * <p>
 * command XML parameters (<b>bold</b>=mandatory): none
 * </p>
 * <p>
 * input work objects (<b>bold</b>=mandatory): none
 * </p>
 * <p>
 * output work objects (<b>bold</b>=mandatory): none
 * </p>
 * @author brmob
 * @since v20150105-CUST-SurveyMgmt
 * @see AnswerSetScoreCommand
 */
public class AnswerSetScoreCommandBB extends AnswerSetScoreCommand {
	
	private static final String REVIEWER_EVALUATION_PLEASE_SELECT = "-2";

	@Override
	public CommandResult execute(CommandContext cc) throws BLException {
		super.execute(cc);
		
		IAppObj answer = cc.getChainContext().getTriggeringAppObj();
		IEnumAttribute answerTypeAttribute = answer.getAttribute(IAnswerAttributeType.ATTR_TYPE);
		
		if (answerTypeAttribute.hasPersistentItem(Enumerations.QUESTION_TYPE.MULTIPLE_CHOICE) ||
				answerTypeAttribute.hasPersistentItem(Enumerations.QUESTION_TYPE.SINGLE_CHOICE_COMBOBOX) ||
				answerTypeAttribute.hasPersistentItem(Enumerations.QUESTION_TYPE.SINGLE_CHOICE_RADIO)){
			
			IEnumAttribute reviewerEvaluationEnum = answer.getAttribute(IAnswerAttributeTypeBB.ATTR_REVIEWER_EVALUATION);
			IEnumerationItem reviewerEvaluationEnumItem = ARCMCollections.extractSingleEntry(reviewerEvaluationEnum.getRawValue(),true);
			
			if (!reviewerEvaluationEnumItem.getValue().equals(REVIEWER_EVALUATION_PLEASE_SELECT)) {
				Double reviewerEvaluation = Double.valueOf(reviewerEvaluationEnumItem.getValue());
				
				IDoubleAttribute actualScoreAttr = answer.getAttribute(IAnswerAttributeType.ATTR_ACTUALSCORE);
				Double actualScore = actualScoreAttr.getRawValue();
				
				actualScoreAttr.setRawValue(actualScore * reviewerEvaluation);
			}
		}
		
		
		return new CommandResult(CommandResult.STATUS.OK);
	}
	
	


}
