/**
 * 
 */
package com.idsscheer.webapps.arcm.bl.component.surveymanagement.evaluation.surveysummary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.idsscheer.webapps.arcm.bl.authentication.context.ContextFactory;
import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.component.surveymanagement.commands.answer.AnswerSetScoreCommand;
import com.idsscheer.webapps.arcm.bl.component.surveymanagement.util.SurveyManagementUtility;
import com.idsscheer.webapps.arcm.bl.dataaccess.query.IViewQuery;
import com.idsscheer.webapps.arcm.bl.dataaccess.query.QueryFactory;
import com.idsscheer.webapps.arcm.bl.exception.BLInternalException;
import com.idsscheer.webapps.arcm.bl.exception.RightException;
import com.idsscheer.webapps.arcm.bl.framework.tree.ClientTreeLogic;
import com.idsscheer.webapps.arcm.bl.framework.tree.ClientTreeNodeObjectIDFilter;
import com.idsscheer.webapps.arcm.bl.framework.tree.IClientTree;
import com.idsscheer.webapps.arcm.bl.framework.tree.IClientTreeNode;
import com.idsscheer.webapps.arcm.bl.framework.tree.IClientTreeNodeFilter;
import com.idsscheer.webapps.arcm.bl.models.filter.IFilterAttributeSet;
import com.idsscheer.webapps.arcm.bl.models.filter.util.FilterAttributeUtility;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObjFacade;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IViewObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.impl.FacadeFactory;
import com.idsscheer.webapps.arcm.common.constants.metadata.Enumerations;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.util.ovid.IOVID;
import com.idsscheer.webapps.arcm.common.util.ovid.OVIDFactory;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;

/**
 * This class was created to solve a collateral effect due to a specific implementation into the survey management module for BdB Project which impacts
 * the all components being use by the class {@link SurveySummary}.
 * We had to amend this class aiming to keep the standard behavior.
 * 
 * <p> This is a customization for <b><code>Banco do Brasil Project</code></b> </p>
 * @author brmob
 * @since v20150105-CUST-SurveyMgmt
 * @see AnswerSetScoreCommand
 */
public class SurveySummaryBB extends SurveySummary {
	
	private static String VIEW_FILTER_QUEST_ID = "questionnaire_id";
	private static String VIEW_FILTER_TEMPLATE_ID = "questionnaire_temp_id";
	
	private final IUserContext fullReadUserCtx = ContextFactory.getFullReadAccessUserContext(Locale.ENGLISH);
	
	private boolean isFilterApplyed;
	
	private IClientTree clientTree;
	
	private IAppObj surveyElement;

	private IAppObj snapshot;

	private Map<Long, Map<Long, BaseAnswerSummary>> sectionIdToSummyMap;
	
	private Map<Long, IClientTreeNode> nodeMap;
	

	public SurveySummaryBB() {
		super();
	}

	@Override
	public void init(IAppObj surveyElement) {
		super.init(surveyElement);
		this.surveyElement = surveyElement;
		this.isFilterApplyed = false;
	}
	
	@Override
	protected void initQuestionDetails() {

		IViewQuery query = null;
		try {
			HashMap<String, Object> filterMap = new HashMap<String, Object>(1);
			filterMap.put(VIEW_FILTER_TEMPLATE_ID, Long.valueOf(snapshot.getObjectId()));
			query = createQuery("getQuestionDetailsByTemplate", filterMap);
			Iterator<IViewObj> objIterator = query.getResultIterator();

			while (objIterator.hasNext()) {
				IViewObj viewObj = (IViewObj) objIterator.next();
				Long questionId = (Long) viewObj.getRawValue("question_id");
				Long sectionId = (Long) viewObj.getRawValue("section_id");
				String questionText = (String) viewObj.getRawValue("question_text");
				List<IEnumerationItem> items = (List<IEnumerationItem>) viewObj.getRawValue("questiontype");
				if ((items == null) || (items.isEmpty())) {
					throw new IllegalStateException("no question type for question id '" + questionId + "' found");
				}
				IEnumerationItem questionType = (IEnumerationItem) items.get(0);

				IClientTreeNode sectionNode = getSectionNode(sectionId.longValue());

				BaseAnswerSummary summary;

				if ((questionType.equals(Enumerations.QUESTION_TYPE.MULTIPLE_CHOICE)) || 
						(questionType.equals(Enumerations.QUESTION_TYPE.SINGLE_CHOICE_COMBOBOX)) || 
							(questionType.equals(Enumerations.QUESTION_TYPE.SINGLE_CHOICE_RADIO))) {

					summary = new ChoiceAnswerSummary(getQuestionnaireIDs(), questionId.longValue(), sectionNode, questionType, questionText);
					
				} else {
					
					if ((questionType.equals(Enumerations.QUESTION_TYPE.LONG)) || (questionType.equals(Enumerations.QUESTION_TYPE.DOUBLE))) {
						summary = new NumberAnswerSummary(getQuestionnaireIDs(), questionId.longValue(), sectionNode, questionType, questionText);
					} else {
						
						if (questionType.equals(Enumerations.QUESTION_TYPE.DATE)) {
							summary = new DateAnswerSummary(getQuestionnaireIDs(), questionId.longValue(), sectionNode, questionType, questionText);
						} else {
							
							if (questionType.equals(Enumerations.QUESTION_TYPE.DATERANGE)) {
								summary = new DateRangeAnswerSummary(getQuestionnaireIDs(), questionId.longValue(), sectionNode, questionType, questionText);
							} else {
								
								if (questionType.equals(Enumerations.QUESTION_TYPE.TEXT)) {
									summary = new TextAnswerSummary(getQuestionnaireIDs(), questionId.longValue(), sectionNode, questionType, questionText);
								} else
									throw new IllegalStateException("unsupported question type: "+ questionType);
							}
						}
					}
				}
				
				if (this.sectionIdToSummyMap == null) {
					this.sectionIdToSummyMap = new HashMap<Long, Map<Long, BaseAnswerSummary>>();
				}
				
				Map<Long, BaseAnswerSummary> questionToAnswerMap = this.sectionIdToSummyMap.get(Long.valueOf(getNodeId(sectionNode)));
				
				if (questionToAnswerMap == null) {
					questionToAnswerMap = new HashMap<Long, BaseAnswerSummary>();
					this.sectionIdToSummyMap.put(Long.valueOf(getNodeId(sectionNode)), questionToAnswerMap);
				}
				
				questionToAnswerMap.put(questionId, summary);
			}
		} catch (Exception e) {
			throw new BLInternalException(e, "error.bl.evaluation.init.ZZZ", new String[] { e.getMessage() });
		} finally {
			if (query != null) {
				query.release();
			}
		}

	}
	
	@Override
	protected void updateAnswerSummary(String viewName, boolean isChoise) {
		IViewQuery viewQuery = null;
		try {
			Map<String, Object> filterMap = new HashMap<String, Object>();
			filterMap.put(VIEW_FILTER_QUEST_ID, getQuestionnaireIDs());
			viewQuery = createQuery(viewName, filterMap);
			Iterator<IViewObj> viewObjIterator = viewQuery.getResultIterator();

			while (viewObjIterator.hasNext()) {
				IViewObj viewObj = (IViewObj) viewObjIterator.next();
				long questionId = ((Long) viewObj.getRawValue("question_id")).longValue();
				long sectionId = ((Long) viewObj.getRawValue("section_id")).longValue();

				IClientTreeNode node = getSectionNode(sectionId);
				if (node != null) {
					Map<Long, BaseAnswerSummary> questionToAnswerMap = this.sectionIdToSummyMap.get(Long.valueOf(getNodeId(node)));
					BaseAnswerSummary summary = questionToAnswerMap.get(Long.valueOf(questionId));
					if (!isChoise) {
						summary.addData(viewObj);
					} else if ((summary instanceof ChoiceAnswerSummary)) {
						summary.addData(viewObj);
					}
				}
			}
			
		} catch (Exception e) {
			throw new BLInternalException(e, "error.bl.evaluation.init.ZZZ",
					new String[] { e.getMessage() });
		} finally {
			if (viewQuery != null) {
				viewQuery.release();
			}
		}
	}
	
	@Override
	protected void updateReviewerRatesAnswerSummary() {
		IViewQuery viewQuery = null;
		try {
			Map<String, Object> filterMap = new HashMap<String, Object>();
			filterMap.put(VIEW_FILTER_QUEST_ID, getQuestionnaireIDs());
			viewQuery = createQuery("answerSummaryRevRatesAnswers", filterMap);
			Iterator<IViewObj> viewObjIterator = viewQuery.getResultIterator();

			while (viewObjIterator.hasNext()) {
				IViewObj viewObj = (IViewObj) viewObjIterator.next();
				long questionId = ((Long) viewObj.getRawValue("question_id")).longValue();
				List<IEnumerationItem> items = (List<IEnumerationItem>) viewObj.getRawValue("questiontype");
				
				if ((items == null) || (items.isEmpty())) {
					throw new IllegalStateException("no question type for question id '" + questionId + "' found");
				}
				IEnumerationItem questionType = (IEnumerationItem) items.get(0);
				
				if (!questionType.equals(Enumerations.QUESTION_TYPE.MULTIPLE_CHOICE) &&
						!questionType.equals(Enumerations.QUESTION_TYPE.SINGLE_CHOICE_COMBOBOX) &&
							!questionType.equals(Enumerations.QUESTION_TYPE.SINGLE_CHOICE_RADIO)) {
					
					long sectionId = ((Long) viewObj.getRawValue("section_id")).longValue();

					IClientTreeNode node = getSectionNode(sectionId);
					if (node != null) {
						Map<Long, BaseAnswerSummary> questionToAnswerMap = this.sectionIdToSummyMap.get(Long.valueOf(getNodeId(node)));
						BaseAnswerSummary summary = questionToAnswerMap.get(Long.valueOf(questionId));
						summary.addReviewerData(viewObj);
					}
				}
				
				
			}
			
		} catch (Exception e) {
			throw new BLInternalException(e, "error.bl.evaluation.init.ZZZ",
					new String[] { e.getMessage() });
		} finally {
			if (viewQuery != null) {
				viewQuery.release();
			}
		}
	}
	
	@Override
	public List<IAnswerSummary> getAnswerSummary(ISurveySummarySection section) {
		if (!isFilterApplyed) {
			return Collections.emptyList();
		}
		Map<Long, BaseAnswerSummary> summaryMap = this.sectionIdToSummyMap.get(Long.valueOf(section.getId()));

		List<IAnswerSummary> answerSummaries = new ArrayList<IAnswerSummary>();
		if (summaryMap != null) {
			Collection<BaseAnswerSummary> summaryCollection = summaryMap
					.values();
			answerSummaries.addAll(summaryCollection);
		}
		Collections.sort(answerSummaries, new Comparator<IAnswerSummary>() {

			@Override
			public int compare(IAnswerSummary a1, IAnswerSummary a2) {
				return a1.getQuestionNumber().compareTo(a2.getQuestionNumber());
			}
			
		});
		return answerSummaries;
	}
	
	@Override
	protected void initTemplate(IFilterAttributeSet filterAttributeSet) {
		String workingCopy = FilterAttributeUtility.getStringRawValue(filterAttributeSet, "template_id");
		
		if (workingCopy != null) {
			IAppObjFacade appObjFacade = FacadeFactory.getInstance().getAppObjFacade(this.fullReadUserCtx, ObjectType.QUESTIONNAIRE_TEMPLATE);
		
			try {
				snapshot = appObjFacade.load(OVIDFactory.getOVID(workingCopy), false);
				clientTree = null;
	    	
			} catch (RightException e) {
				throw new BLInternalException(e);
			}
		} else {
			IAppObj template = SurveyManagementUtility.getQuestionnaireTemplate(surveyElement);
			if (SurveyManagementUtility.isSnapshot(template)) {
				snapshot = template;
			} else {
				snapshot = SurveyManagementUtility.getNewestSnapshot(template, surveyElement);
			}
			clientTree = null;
		}
	}
	
	@Override
	protected IClientTreeNode getSectionNode(long sectionId) {
		IOVID sectionOVID = OVIDFactory.getOVID(sectionId);
		if (nodeMap == null) {
			nodeMap = new HashMap<Long, IClientTreeNode>();
		}
		IClientTreeNode node = (IClientTreeNode)nodeMap.get(Long.valueOf(sectionId));
		
		if (node == null) {
			IClientTreeNodeFilter filter = new ClientTreeNodeObjectIDFilter(sectionOVID, ClientTreeNodeObjectIDFilter.Mode.ID_ONLY);
			node = ClientTreeLogic.getInstance().findFirst(getTree().getRoot(), filter);
			nodeMap.put(Long.valueOf(sectionId), node);
		}
		return node;
	}
	
	
	@Override
	protected IClientTree getTree() {
		if (this.clientTree == null) {
			this.clientTree = SurveyManagementUtility.getSectionTree(snapshot);
		}
		return this.clientTree;
	}
	
	private IViewQuery createQuery(String viewName, Map<String, Object> viewFilterMap) {
		  IViewQuery viewQuery = QueryFactory.createQuery(this.fullReadUserCtx, viewName, viewFilterMap, null, false, null);
		  viewQuery.setEnablePaging(false, false);
		  return viewQuery;
	}
}
