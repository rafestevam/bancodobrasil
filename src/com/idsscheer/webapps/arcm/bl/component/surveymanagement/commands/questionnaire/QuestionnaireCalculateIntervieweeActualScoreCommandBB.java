/**
 * 
 */
package com.idsscheer.webapps.arcm.bl.component.surveymanagement.commands.questionnaire;

import java.util.ArrayList;
import java.util.List;

import com.aspose.imaging.internal.Exceptions.Exception;
import com.idsscheer.webapps.arcm.bl.authentication.context.ContextFactory;
import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.exception.BLException;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandContext;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandResult;
import com.idsscheer.webapps.arcm.bl.framework.command.IChainContext;
import com.idsscheer.webapps.arcm.bl.framework.command.ICommand;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IDoubleAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IListAttribute;
import com.idsscheer.webapps.arcm.common.constants.metadata.Enumerations;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IAnswerAttributeTypeBB;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IQuestionnaireAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IQuestionnaireAttrybuteTypeBB;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IQuestionnairesectionAttributeType;
import com.idsscheer.webapps.arcm.common.util.ARCMCollections;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IObjectType;

/**
 * @author Administrator
 *
 */
public class QuestionnaireCalculateIntervieweeActualScoreCommandBB implements ICommand {

	private IUserContext fullGrantUserContext;
	
	/* (non-Javadoc)
	 * @see com.idsscheer.webapps.arcm.bl.framework.command.ICommand#execute(com.idsscheer.webapps.arcm.bl.framework.command.CommandContext)
	 */
	@Override
	public CommandResult execute(CommandContext cc) throws BLException {
		
		IChainContext chainContext = cc.getChainContext();
		IAppObj questionnaire = chainContext.getTriggeringAppObj();
		fullGrantUserContext = ContextFactory.createFullGrantUserContext(cc.getChainContext().getUserContext());
		
		IListAttribute questionnaireSectionsAttr = questionnaire.getAttribute(IQuestionnaireAttributeType.LIST_QUESTIONNAIRESECTIONS);
		List<IAppObj> questionnaireSections = questionnaireSectionsAttr.getElements(fullGrantUserContext);
		
		calculateQuestionnaireIntervieweeActualScore(questionnaire, questionnaireSections);
		return CommandResult.OK;
	}
	
	
	private void calculateQuestionnaireIntervieweeActualScore(IAppObj questionnaire, List<IAppObj> questionnaireSections){
		IDoubleAttribute intervieweeActualScoreAttr = questionnaire.getAttribute(IQuestionnaireAttrybuteTypeBB.ATTR_INTERVIEWEE_ACTUALSCORE);
		Double intervieweeActualScore = intervieweeActualScoreAttr.getRawValue() == null ? 0D : intervieweeActualScoreAttr.getRawValue();
		
		for (IAppObj questionnaireSection : questionnaireSections) {
			intervieweeActualScore += calculateIntervieweeActualScore(questionnaireSection, intervieweeActualScore);
		}
		
		intervieweeActualScoreAttr.setRawValue(intervieweeActualScore);
	}
	
	private Double calculateIntervieweeActualScore(IAppObj appObj, Double intervieweeActualScore){
		
		IObjectType appObjectType = appObj.getObjectType();
		if (appObjectType.equals(ObjectType.QUESTIONNAIRESECTION)) {
			
			List<IAppObj> children = getChildren(appObj);
			for (IAppObj child : children) {
				
				IObjectType childObjectType = child.getObjectType();
				if (childObjectType.equals(ObjectType.QUESTIONNAIRESECTION)) {
					if (hasChildren(child)) {
						intervieweeActualScore = calculateIntervieweeActualScore(child, intervieweeActualScore);
					}
				} else if (childObjectType.equals(ObjectType.ANSWER)) {
					
					IEnumerationItem answerType = ARCMCollections.extractSingleEntry(child.getAttribute(IAnswerAttributeTypeBB.ATTR_TYPE).getRawValue(), true);
					if (answerType.equals(Enumerations.QUESTION_TYPE.SINGLE_CHOICE_COMBOBOX) ||
							answerType.equals(Enumerations.QUESTION_TYPE.MULTIPLE_CHOICE) ||
							answerType.equals(Enumerations.QUESTION_TYPE.SINGLE_CHOICE_RADIO)) {
						
						Double answerActualScore = child.getAttribute(IAnswerAttributeTypeBB.ATTR_ACTUALSCORE).getRawValue() != null ? 
								child.getAttribute(IAnswerAttributeTypeBB.ATTR_ACTUALSCORE).getRawValue() :
									0D;
						intervieweeActualScore += answerActualScore;
					}
				} else {
					throw new Exception("This object type is not supported: "+ childObjectType.getId());
				}
			}
			
		} else if (appObjectType.equals(ObjectType.ANSWER)) {
			
			IEnumerationItem answerType = ARCMCollections.extractSingleEntry(appObj.getAttribute(IAnswerAttributeTypeBB.ATTR_TYPE).getRawValue(), true);
			if (answerType.equals(Enumerations.QUESTION_TYPE.SINGLE_CHOICE_COMBOBOX) ||
					answerType.equals(Enumerations.QUESTION_TYPE.MULTIPLE_CHOICE) ||
					answerType.equals(Enumerations.QUESTION_TYPE.SINGLE_CHOICE_RADIO)) {
				
				Double answerActualScore = appObj.getAttribute(IAnswerAttributeTypeBB.ATTR_ACTUALSCORE).getRawValue() != null ? 
						appObj.getAttribute(IAnswerAttributeTypeBB.ATTR_ACTUALSCORE).getRawValue() :
							0D;
				intervieweeActualScore += answerActualScore;
				
			}
		} else {
			throw new Exception("This object type is not supported: "+ appObjectType.getId());
		}
		
		return intervieweeActualScore;
		
	}
	
	private boolean hasChildren(IAppObj iAppObj){
		return !getChildren(iAppObj).isEmpty();
	}
	
	private List<IAppObj> getChildren(IAppObj parent){
		
		List<IAppObj> children = new ArrayList<IAppObj>();
		IObjectType parentObjectType = parent.getObjectType();
		
		if (parentObjectType.equals(ObjectType.QUESTIONNAIRESECTION)) {
			List<IAppObj> subSections = parent.getAttribute(IQuestionnairesectionAttributeType.LIST_SUBSECTIONS).getElements(fullGrantUserContext);
			List<IAppObj> answers = parent.getAttribute(IQuestionnairesectionAttributeType.LIST_ANSWERS).getElements(fullGrantUserContext);
			
			ARCMCollections.addAll(children, subSections);
			ARCMCollections.addAll(children, answers);
		}
		
		return children;
	}

}
