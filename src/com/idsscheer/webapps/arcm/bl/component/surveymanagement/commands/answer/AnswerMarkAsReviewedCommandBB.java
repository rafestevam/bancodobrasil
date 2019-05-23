package com.idsscheer.webapps.arcm.bl.component.surveymanagement.commands.answer;

import com.idsscheer.webapps.arcm.bl.exception.BLException;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandContext;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandResult;
import com.idsscheer.webapps.arcm.bl.framework.command.ICommand;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IEnumAttribute;
import com.idsscheer.webapps.arcm.common.constants.metadata.Enumerations;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IAnswerAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IAnswerAttributeTypeBB;

/**
 * <p>
 * Sets the attribute IAnswerAttributeType.ATTR_REVIEWED to 0 or 1 if ATTR_REVIEWERRATESANSWER is true and
 * LIST_REVIEWEROPTION or ATTR_REVIEWER_EVALUATION are filled.
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
 * @see AnswerMarkAsReviewedCommand
 */
public class AnswerMarkAsReviewedCommandBB implements ICommand {


	@Override
	public CommandResult execute(CommandContext cc) throws BLException {
		IAppObj answer = cc.getChainContext().getTriggeringAppObj();
        boolean markAsReviewed = false;

        Boolean reviewerRatesAnswer = answer.getAttribute(IAnswerAttributeType.ATTR_REVIEWERRATESANSWER).getRawValue();
        IEnumAttribute answerTypeAttribute = answer.getAttribute(IAnswerAttributeType.ATTR_TYPE);
        
        if (reviewerRatesAnswer) {
        	if (answerTypeAttribute.hasPersistentItem(Enumerations.QUESTION_TYPE.MULTIPLE_CHOICE) ||
    				answerTypeAttribute.hasPersistentItem(Enumerations.QUESTION_TYPE.SINGLE_CHOICE_COMBOBOX) ||
    				answerTypeAttribute.hasPersistentItem(Enumerations.QUESTION_TYPE.SINGLE_CHOICE_RADIO)){
        		if (!answer.getAttribute(IAnswerAttributeTypeBB.ATTR_REVIEWER_EVALUATION).isEmpty()) {
        			markAsReviewed = true;
        		}
        	} else if(!answer.getAttribute(IAnswerAttributeType.LIST_REVIEWEROPTION).isEmpty()) {
                markAsReviewed = true;
            } else {
            	// do nothing!
            }
        }
        long value;
        if(markAsReviewed) {
            value = 1;
		} else {
            value = 0;
        }
        answer.getAttribute(IAnswerAttributeType.ATTR_REVIEWED).setRawValue(value);

        return new CommandResult(CommandResult.STATUS.OK);
	}

}
