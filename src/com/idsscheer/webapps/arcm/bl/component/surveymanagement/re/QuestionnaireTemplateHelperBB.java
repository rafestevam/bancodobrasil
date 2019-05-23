/**
 * 
 */
package com.idsscheer.webapps.arcm.bl.component.surveymanagement.re;

import java.util.ArrayList;
import java.util.List;

import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IListAttribute;
import com.idsscheer.webapps.arcm.bl.re.ext.MessageInformation;
import com.idsscheer.webapps.arcm.bl.re.ext.RuleAppObj;
import com.idsscheer.webapps.arcm.bl.re.ext.StateInformation;
import com.idsscheer.webapps.arcm.bl.re.impl.REEnvironment;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IQuestionnaire_templateAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.ISectionAttributeType;
import com.idsscheer.webapps.arcm.common.util.ARCMCollections;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IObjectType;

public class QuestionnaireTemplateHelperBB extends QuestionnaireTemplateHelper {
	private static final String SECTION_WITHOUT_QUESTION_ERROR = "error.surveytask.section.without.question.ERR";
	
	public static void checkSectionsCurrentTemplate() {
		REEnvironment env = REEnvironment.getInstance();
        RuleAppObj ruleAppObj = env.getRuleAppObj();
        IAppObj appObj = ruleAppObj.getAppObj();
        
        StateInformation stateInformation = env.getStateInformation(); 
		MessageInformation messageInformation = env.getMessageInformation();
        
		boolean isOK = true;
        List<IAppObj> children = getChildren(appObj);
		for (IAppObj child : children) {
			if (countSections(child, stateInformation, messageInformation) == 0) {
				isOK = false;
				break;
			}
		}		
		stateInformation.setValid(IQuestionnaire_templateAttributeType.LIST_SECTIONS.getId(), isOK);
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
			stateInformation.setValid(IQuestionnaire_templateAttributeType.LIST_SECTIONS.getId(), false);
			long id = parentSection.getObjectId();
			String parentName = parentSection.getAttribute(IQuestionnaire_templateAttributeType.ATTR_NAME).getRawValue();
			messageInformation.addErrorMessage(IQuestionnaire_templateAttributeType.LIST_SECTIONS.getId(), SECTION_WITHOUT_QUESTION_ERROR, id, parentName);
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
	private static List<IAppObj> getChildren(IAppObj iAppObj){
		IObjectType appObjectType = iAppObj.getObjectType();
		List<IAppObj> children = new ArrayList<IAppObj>();
		
		if (appObjectType.equals(ObjectType.QUESTIONNAIRE_TEMPLATE)) {
			IListAttribute sections = iAppObj.getAttribute(IQuestionnaire_templateAttributeType.LIST_SECTIONS);
			
			ARCMCollections.addAll(children, sections.getElements(getFullReadAccessUserContext()));
			
		} 
		return children;
	}

}
