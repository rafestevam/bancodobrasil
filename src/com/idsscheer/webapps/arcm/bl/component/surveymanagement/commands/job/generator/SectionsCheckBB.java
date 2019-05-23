package com.idsscheer.webapps.arcm.bl.component.surveymanagement.commands.job.generator;

import java.util.ArrayList;
import java.util.List;

import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.component.common.command.job.GeneratorConditionCheckCommand;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandContext;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandException;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IListAttribute;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IQuestionnaire_templateAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.ISectionAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.ISurveytaskAttributeType;
import com.idsscheer.webapps.arcm.common.notification.INotification;
import com.idsscheer.webapps.arcm.common.notification.NotificationTypeEnum;
import com.idsscheer.webapps.arcm.common.notification.ResourceBundleNotification;
import com.idsscheer.webapps.arcm.common.util.ARCMCollections;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IObjectType;


public class SectionsCheckBB extends GeneratorConditionCheckCommand {
	private static final String JOB_GENERATOR_JOB_SURVEY_SECTION_WITHOUT_QUESTION_ERR = "job.generator.job.survey.section.without.question.ERR";

	@Override
	protected CheckResult checkGeneratorCondition(IAppObj appObj, CommandContext cc) throws CommandException {
		IUserContext userContext = cc.getChainContext().getUserContext();
        
        IListAttribute questionnaireTemplates = (IListAttribute) appObj.getAttribute(ISurveytaskAttributeType.LIST_QUESTIONNAIRETEMPLATE);
        IAppObj questionnaireTemplate = ARCMCollections.extractSingleEntry(questionnaireTemplates.getElements(userContext), true);
        
		boolean isOK = true;
        List<IAppObj> children = getChildren(questionnaireTemplate, userContext);
		for (IAppObj child : children) {
			if (countSections(child, userContext) == 0) {
				isOK = false;
				break;
			}
		}
		if (!isOK) {
			Long surveyId = appObj.getObjectId();
			Long templateId = questionnaireTemplate.getObjectId();
			final INotification notification =  new ResourceBundleNotification( NotificationTypeEnum.ERROR,
					JOB_GENERATOR_JOB_SURVEY_SECTION_WITHOUT_QUESTION_ERR, surveyId.toString(), templateId.toString());
			return new CheckResult(notification, true);
		}

		return null;
	}

	protected static int countSections(IAppObj parentSection, IUserContext userContext) {
		List<IAppObj> questions = new ArrayList<IAppObj>();
		List<IAppObj> subSections = new ArrayList<IAppObj>();

		IListAttribute questionListAttr = parentSection.getAttribute(ISectionAttributeType.LIST_QUESTIONS);
		ARCMCollections.addAll(questions, questionListAttr.getElements(userContext));		
		IListAttribute subSectionListAttr = parentSection.getAttribute(ISectionAttributeType.LIST_SUBSECTIONS);
		ARCMCollections.addAll(subSections, subSectionListAttr.getElements(userContext));

		int result = questions.size();
		if (result == 0 && subSections.size() == 0) {
			return result;	
		}
		
		for (IAppObj subSection : subSections) {
			int count = countSections(subSection, userContext);
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

	private static List<IAppObj> getChildren(IAppObj iAppObj, IUserContext userContext){
		IObjectType appObjectType = iAppObj.getObjectType();
		List<IAppObj> children = new ArrayList<IAppObj>();
		
		if (appObjectType.equals(ObjectType.QUESTIONNAIRE_TEMPLATE)) {
			IListAttribute sections = iAppObj.getAttribute(IQuestionnaire_templateAttributeType.LIST_SECTIONS);
			ARCMCollections.addAll(children, sections.getElements(userContext));
		}
		return children;
	}	
}