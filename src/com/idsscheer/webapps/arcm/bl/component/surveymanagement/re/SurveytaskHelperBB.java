/**
 * 
 */
package com.idsscheer.webapps.arcm.bl.component.surveymanagement.re;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.aspose.imaging.internal.Exceptions.Exception;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IDateAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IEnumAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IListAttribute;
import com.idsscheer.webapps.arcm.bl.re.ext.MessageInformation;
import com.idsscheer.webapps.arcm.bl.re.ext.RuleAppObj;
import com.idsscheer.webapps.arcm.bl.re.ext.StateInformation;
import com.idsscheer.webapps.arcm.bl.re.impl.REEnvironment;
import com.idsscheer.webapps.arcm.common.constants.metadata.Enumerations;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IOptionAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IOptionsetAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IQuestionAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IQuestionAttributeTypeBB;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IQuestionnaire_templateAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.ISectionAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.ISurveytaskAttributeType;
import com.idsscheer.webapps.arcm.common.support.DateUtils;
import com.idsscheer.webapps.arcm.common.support.DateUtils.Target;
import com.idsscheer.webapps.arcm.common.util.ARCMCollections;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IObjectType;

/**
 * <p>
 * Used by the rule engine in 'surveytask.drl' and 'surveytask.dsl'.
 * </p>
 * 
 * <p> This is a customization for <b><code>Banco do Brasil Project</code></b> </p>
 *@author brmob
 *@since v20151124-MNT-UATEnhancements
 *@see SurveytaskHelper
 */
public class SurveytaskHelperBB extends SurveytaskHelper {
	
	private static final String QUESTIONNAIRE_TEMPLATE_TARGETSCORE_ERROR = "error.surveytask.questionnaire_template.children.target_score.ERR";
	private static final String SECTION_TARGETSCORE_ERROR = "error.surveytask.section.children.target_score.ERR";
	private static final String SECTION_WITHOUT_QUESTION_ERROR = "error.surveytask.section.without.question.ERR";
	
	/**
	 * Method that check the target score values for integrity.<br>
	 * It checks the attribute <b>target score</b> of the selected questionnaire template object.<br>
	 * The <b>sum of target score</b> attributes of sections and/or questions objects must to be the same to the connected questionnaire template object. 
	 */
	public static void checkTargetScore(){
		REEnvironment env = REEnvironment.getInstance();
        RuleAppObj surveyTask = env.getRuleAppObj();
        
        StateInformation stateInformation = env.getStateInformation(); 
		MessageInformation messageInformation = env.getMessageInformation();
        
        IListAttribute questionnaireTemplates = (IListAttribute) 
        		surveyTask.getAttribute(ISurveytaskAttributeType.LIST_QUESTIONNAIRETEMPLATE.getId());
        
        IAppObj questionnaireTemplate = ARCMCollections.extractSingleEntry(
        		questionnaireTemplates.getElements(getFullReadAccessUserContext()), true);
        
        calculateTargetScoreByParent(questionnaireTemplate, stateInformation, messageInformation);
        
	}
	
	/**
	 * This methods calculates recursively the targetScore attribute of all objects connected to a specific parent object.
	 * @param parent
	 * @param stateInformation
	 * @param messageInformation
	 */
	private static void calculateTargetScoreByParent(IAppObj parent, StateInformation stateInformation, MessageInformation messageInformation){
		IObjectType parentObjectType = parent.getObjectType();
		if (hasChildren(parent) && !parentObjectType.equals(ObjectType.QUESTION)) {
			validateChildrenTargetScoreByParent(parent, stateInformation, messageInformation);
			List<IAppObj> children = getChildren(parent);
			for (IAppObj child : children) {
				calculateTargetScoreByParent(child, stateInformation, messageInformation);
			}
		}
	}
	
	/**
	 * This methods validates recursively the targetScore attribute of all objects connected to a specific parent object.
	 * @param parent
	 * @param stateInformation
	 * @param messageInformation
	 */
	private static void validateChildrenTargetScoreByParent(IAppObj parent, StateInformation stateInformation, MessageInformation messageInformation){
		Double childrenTargetScore = 0D;
		Double parentTargetScore = 0D;
		
		IObjectType parentObjectType = parent.getObjectType();
		
		String parentName = null;
		Long parentID = null;
		
		if (parentObjectType.equals(ObjectType.QUESTIONNAIRE_TEMPLATE)) {
			parentTargetScore = parent.getAttribute(IQuestionnaire_templateAttributeType.ATTR_TARGETSCORE).getRawValue();
			
			List<IAppObj> children = getChildren(parent);
			for (IAppObj child : children) {
				childrenTargetScore += child.getAttribute(ISectionAttributeType.ATTR_TARGETSCORE).getRawValue();
			}
			
			if (Double.compare(childrenTargetScore, parentTargetScore) != 0) {
				stateInformation.setValid(ISurveytaskAttributeType.LIST_QUESTIONNAIRETEMPLATE.getId(), false);
				
				parentID = parent.getObjectId();
				parentName = parent.getAttribute(IQuestionnaire_templateAttributeType.ATTR_NAME).getRawValue();
				
				messageInformation.addErrorMessage(ISurveytaskAttributeType.LIST_QUESTIONNAIRETEMPLATE.getId(), 
						QUESTIONNAIRE_TEMPLATE_TARGETSCORE_ERROR, parentID, parentName, parentTargetScore, childrenTargetScore);
				
			}
		} else if (parentObjectType.equals(ObjectType.SECTION)) {
			parentTargetScore = parent.getAttribute(ISectionAttributeType.ATTR_TARGETSCORE).getRawValue();
			
			List<IAppObj> children = getChildren(parent);
			for (IAppObj child : children) {
				IObjectType childObjectType = child.getObjectType();
				
				if (childObjectType.equals(ObjectType.SECTION)) {
					childrenTargetScore += child.getAttribute(ISectionAttributeType.ATTR_TARGETSCORE).getRawValue();
				} else if (childObjectType.equals(ObjectType.QUESTION)) {
					
					IEnumAttribute questionTypeAttr = child.getAttribute(IQuestionAttributeTypeBB.ATTR_TYPE);
					
					if (!questionTypeAttr.hasItem(Enumerations.QUESTION_TYPE.MULTIPLE_CHOICE)) {
						List<IAppObj> grandChildren = getChildren(child);
						Double biggestChildTargetScore = 0D;
						
						for (IAppObj grandChild : grandChildren) {
							IObjectType grandChildObjectType = grandChild.getObjectType();
							
							if (grandChildObjectType.equals(ObjectType.OPTIONSET)) {
								
								List<IAppObj> greatGrandChildren = getChildren(grandChild);
								for (IAppObj greatGrandChild : greatGrandChildren) {
									Double currentChildTargetScore = greatGrandChild.getAttribute(IOptionAttributeType.ATTR_OPTIONVALUE).getRawValue();
									
									biggestChildTargetScore = Double.compare(currentChildTargetScore, biggestChildTargetScore) > 0 ?
											currentChildTargetScore : biggestChildTargetScore;
								}
							} else if (grandChildObjectType.equals(ObjectType.OPTION)) {
								Double currentChildTargetScore = grandChild.getAttribute(IOptionAttributeType.ATTR_OPTIONVALUE).getRawValue();
								
								biggestChildTargetScore = Double.compare(currentChildTargetScore, biggestChildTargetScore) > 0 ?
										currentChildTargetScore : biggestChildTargetScore;
							}
						}
						childrenTargetScore += biggestChildTargetScore;
						
					} else {
						List<IAppObj> grandChildren = getChildren(child);
						Double sumChildTargetScore = 0D;
						
						for (IAppObj grandChild : grandChildren) {
							IObjectType grandChildObjectType = grandChild.getObjectType();
							
							if (grandChildObjectType.equals(ObjectType.OPTIONSET)) {
								
								List<IAppObj> currentGrandChildren = getChildren(grandChild);
								for (IAppObj greatGrandChild : currentGrandChildren) {
									Double currentChildTargetScore = greatGrandChild.getAttribute(IOptionAttributeType.ATTR_OPTIONVALUE).getRawValue();
									
									sumChildTargetScore += currentChildTargetScore;
								}
							} else if (grandChildObjectType.equals(ObjectType.OPTION)) {
								Double currentChildTargetScore = grandChild.getAttribute(IOptionAttributeType.ATTR_OPTIONVALUE).getRawValue();
								
								sumChildTargetScore += currentChildTargetScore;
							}
						}
						childrenTargetScore += sumChildTargetScore;
					}
					
				}
			}
			
			if (Double.compare(childrenTargetScore, parentTargetScore) != 0) {
				stateInformation.setValid(ISurveytaskAttributeType.LIST_QUESTIONNAIRETEMPLATE.getId(), false);
				
				parentID = parent.getObjectId();
				parentName = parent.getAttribute(IQuestionnaire_templateAttributeType.ATTR_NAME).getRawValue();
				
				messageInformation.addErrorMessage(ISurveytaskAttributeType.LIST_QUESTIONNAIRETEMPLATE.getId(), 
						SECTION_TARGETSCORE_ERROR, parentID, parentName, parentTargetScore, childrenTargetScore);
			}
			
		} 
	}
	/**
	 * This method checks if a object has dependencies of other ones.<br>
	 * For example: it checks if a QUESTIONNAIRE_TEMPLATE has SECTIONS connected with.
	 * @param iAppObj
	 * @return
	 */
	private static boolean hasChildren(IAppObj iAppObj){
		return !getChildren(iAppObj).isEmpty();
	}
	
	/**
	 * This method returns a list of the connected objects.<br>
	 * For example: it checks if a QUESTIONNAIRE_TEMPLATE has SECTIONS connected with.
	 * @param iAppObj
	 * @return
	 */
	private static List<IAppObj> getChildren(IAppObj iAppObj){
		IObjectType appObjectType = iAppObj.getObjectType();
		List<IAppObj> children = new ArrayList<IAppObj>();
		
		if (appObjectType.equals(ObjectType.QUESTIONNAIRE_TEMPLATE)) {
			IListAttribute sections = iAppObj.getAttribute(IQuestionnaire_templateAttributeType.LIST_SECTIONS);
			
			ARCMCollections.addAll(children, sections.getElements(getFullReadAccessUserContext()));
			
		} else if (appObjectType.equals(ObjectType.SECTION)) {
			IListAttribute subSections = iAppObj.getAttribute(ISectionAttributeType.LIST_SUBSECTIONS);
			IListAttribute questions = iAppObj.getAttribute(ISectionAttributeType.LIST_QUESTIONS);
			
			ARCMCollections.addAll(children, subSections.getElements(getFullReadAccessUserContext()));
			ARCMCollections.addAll(children, questions.getElements(getFullReadAccessUserContext()));
			
		} else if (appObjectType.equals(ObjectType.QUESTION)) {
			IListAttribute optionSets = iAppObj.getAttribute(IQuestionAttributeType.LIST_OPTIONSET);
			IListAttribute options = iAppObj.getAttribute(IQuestionAttributeType.LIST_OPTIONS);
			
			ARCMCollections.addAll(children, optionSets.getElements(getFullReadAccessUserContext()));
			ARCMCollections.addAll(children, options.getElements(getFullReadAccessUserContext()));
			
		} else if (appObjectType.equals(ObjectType.OPTIONSET)) {
			IListAttribute options = iAppObj.getAttribute(IOptionsetAttributeType.LIST_OPTIONS);
			
			ARCMCollections.addAll(children, options.getElements(getFullReadAccessUserContext()));
			
		} else {
			throw new Exception("This object type is not supported: "+ appObjectType.getId());
		}
		
		return children;
	}
	
	public static void checkSections() {
		REEnvironment env = REEnvironment.getInstance();
        RuleAppObj surveyTask = env.getRuleAppObj();
        
        StateInformation stateInformation = env.getStateInformation(); 
		MessageInformation messageInformation = env.getMessageInformation();
        
        IListAttribute questionnaireTemplates = (IListAttribute) surveyTask.getAttribute(ISurveytaskAttributeType.LIST_QUESTIONNAIRETEMPLATE.getId());
        IAppObj questionnaireTemplate = ARCMCollections.extractSingleEntry(questionnaireTemplates.getElements(getFullReadAccessUserContext()), true);
        
		boolean isOK = true;
        List<IAppObj> children = getChildren(questionnaireTemplate);
		for (IAppObj child : children) {
			if (countSections(child, stateInformation, messageInformation) == 0) {
				isOK = false;
				break;
			}
		}
		stateInformation.setValid(ISurveytaskAttributeType.LIST_QUESTIONNAIRETEMPLATE.getId(), isOK);
	}

	protected static int countSections(IAppObj parentSection, StateInformation stateInformation, MessageInformation messageInformation) {
		List<IAppObj> questions = new ArrayList<IAppObj>();
		List<IAppObj> subSections = new ArrayList<IAppObj>();

		IListAttribute questionListAttr = parentSection.getAttribute(ISectionAttributeType.LIST_QUESTIONS);
		ARCMCollections.addAll(questions, questionListAttr.getElements(getFullReadAccessUserContext()));		
		IListAttribute subSectionListAttr = parentSection.getAttribute(ISectionAttributeType.LIST_SUBSECTIONS);
		ARCMCollections.addAll(subSections, subSectionListAttr.getElements(getFullReadAccessUserContext()));

		int result = questions.size();
		if (result == 0 && subSections.size() == 0) {
			stateInformation.setValid(ISurveytaskAttributeType.LIST_QUESTIONNAIRETEMPLATE.getId(), false);
			long id = parentSection.getObjectId();
			String parentName = parentSection.getAttribute(IQuestionnaire_templateAttributeType.ATTR_NAME).getRawValue();
			messageInformation.addErrorMessage(ISurveytaskAttributeType.LIST_QUESTIONNAIRETEMPLATE.getId(), SECTION_WITHOUT_QUESTION_ERROR, id, parentName);
			return result;	
		}
		
		for (IAppObj subSection : subSections) {
			int count = countSections(subSection, stateInformation, messageInformation);
			if (count == 0) {
				result = 0;
				break;
			}
			else {
				result = result + count;
			}
		}
		
		return result;
	}
	
	/**
	 * This method is used to check if start date of survey task is older than the current date
	 * 
	 *  @return true | false
	 * */
	public static boolean isStartDateLTToday(){
		REEnvironment env = REEnvironment.getInstance();
        RuleAppObj surveyTask = env.getRuleAppObj();
        IDateAttribute startdateAttr = (IDateAttribute)surveyTask.getAttribute(ISurveytaskAttributeType.ATTR_STARTDATE.getId());
        Date startdate = startdateAttr.getRawValue();
        Date today = new Date();
        startdate = DateUtils.normalizeLocalDate(startdate, Target.BEGIN_OF_DAY);
        today = DateUtils.normalizeLocalDate(today, Target.BEGIN_OF_DAY);
        
        long diffTimeMiliseconds = 0;
        
        if(startdate != null){
        	diffTimeMiliseconds = (long)(startdate.getTime() - today.getTime());
            if (diffTimeMiliseconds < 0) {
            	return true;
            }
        }else{
        	return false;
        }
    	return false;
    }
}
