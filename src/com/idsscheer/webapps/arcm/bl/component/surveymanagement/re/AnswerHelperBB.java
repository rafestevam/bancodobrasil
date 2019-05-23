/**
 * 
 */
package com.idsscheer.webapps.arcm.bl.component.surveymanagement.re;

import java.util.List;
import java.util.Set;

import com.idsscheer.webapps.arcm.bl.models.objectmodel.IQuestionAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IListAttribute;
import com.idsscheer.webapps.arcm.bl.re.ext.RuleAppObj;
import com.idsscheer.webapps.arcm.bl.re.ext.StateInformation;
import com.idsscheer.webapps.arcm.bl.re.impl.REEnvironment;
import com.idsscheer.webapps.arcm.common.constants.metadata.Enumerations;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IAnswerAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IAnswerAttributeTypeBB;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IQuestionAttributeTypeBB;
import com.idsscheer.webapps.arcm.common.util.ARCMCollections;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;

/**
 * <p>
 * Used by the rule engine in 'answer.drl' and 'answer.dsl'.
 * </p>
 * <p> This is a customization for <b><code>Banco do Brasil Project</code></b> </p>
 * @author brmob
 * @since v20150105-CUST-SurveyMgmt
 * @see AnswerHelper
 */
public class AnswerHelperBB extends AnswerHelper {
	
	/**
     * Defines the visibility flag value for most of answer attributes depending on any state of the answer
     * @see AnswerHelper#defineQuestionTypeDependentVisibility()
     */
    public static void defineQuestionTypeDependentVisibility() {
        REEnvironment env = REEnvironment.getInstance();
        RuleAppObj answer = env.getRuleAppObj();

        String actualScore = answer.createAtomicKey(IAnswerAttributeType.STR_ACTUALSCORE).getString();
        String annotationAllowed = answer.createAtomicKey(IAnswerAttributeType.STR_ANNOTATIONALLOWED).getString();
        String answered = answer.createAtomicKey(IAnswerAttributeType.STR_ANSWERED).getString();
        String answerIndex = answer.createAtomicKey(IAnswerAttributeType.STR_ANSWERINDEX).getString();
        String dateValue = answer.createAtomicKey(IAnswerAttributeType.STR_DATEVALUE).getString();
        String dateValueFrom = answer.createAtomicKey(IAnswerAttributeType.STR_DATEVALUEFROM).getString();
        String dateValueTo = answer.createAtomicKey(IAnswerAttributeType.STR_DATEVALUETO).getString();
        String doubleValue = answer.createAtomicKey(IAnswerAttributeType.STR_DOUBLEVALUE).getString();
        String longValue = answer.createAtomicKey(IAnswerAttributeType.STR_LONGVALUE).getString();
        String objID = answer.createAtomicKey(IAnswerAttributeType.STR_OBJ_ID).getString();
        String optionalAnswer = answer.createAtomicKey(IAnswerAttributeType.STR_OPTIONALANSWER).getString();
        String performedDate = answer.createAtomicKey(IAnswerAttributeType.STR_EXECUTION_DATE).getString();
        String personInCharge = answer.createAtomicKey(IAnswerAttributeType.STR_OWNER).getString();
        String possibleOptions = answer.createAtomicKey(IAnswerAttributeType.STR_POSSIBLEOPTIONS).getString();
        String question = answer.createAtomicKey(IAnswerAttributeType.STR_QUESTION).getString();
        String questionText = answer.createAtomicKey(IAnswerAttributeType.STR_QUESTION_TEXT).getString();
        String questionValue = answer.createAtomicKey(IAnswerAttributeType.STR_QUESTIONVALUE).getString();
        String relatedClient = answer.createAtomicKey(IAnswerAttributeType.STR_RELATED_CLIENT).getString();
        String remark = answer.createAtomicKey(IAnswerAttributeType.STR_REMARK).getString();
        String reviewedDate = answer.createAtomicKey(IAnswerAttributeType.STR_REVIEW_DATE).getString();
        String reviewed = answer.createAtomicKey(IAnswerAttributeType.STR_REVIEWED).getString();
        String reviewedBy = answer.createAtomicKey(IAnswerAttributeType.STR_REVIEWER).getString();
        String reviewerRatesAnswer = answer.createAtomicKey(IAnswerAttributeType.STR_REVIEWERRATESANSWER).getString();
        String selectedOptions = answer.createAtomicKey(IAnswerAttributeType.STR_SELECTEDOPTIONS).getString();
        String reviewerOption = answer.createAtomicKey(IAnswerAttributeType.STR_REVIEWEROPTION).getString();
        String substitute = answer.createAtomicKey(IAnswerAttributeType.STR_OWNER_SUBSTITUTE).getString();
        String substituteUserID = answer.createAtomicKey(IAnswerAttributeType.STR_SUBSTITUTE_USER_ID).getString();
        String substituteReviewer = answer.createAtomicKey(IAnswerAttributeType.STR_REVIEWER_SUBSTITUTE).getString();
        String textValue = answer.createAtomicKey(IAnswerAttributeType.STR_TEXTVALUE).getString();
        String type = answer.createAtomicKey(IAnswerAttributeType.STR_TYPE).getString();
        String questionRemark = answer.createAtomicKey(IAnswerAttributeType.STR_QUESTION_REMARK).getString();
        String questionDocuments = answer.createAtomicKey(IAnswerAttributeType.STR_QUESTION_DOCUMENTS).getString();
        String relatedQuestionnaire = answer.createAtomicKey(IAnswerAttributeType.STR_RELATEDQUESTIONNAIRE).getString();
        /** BdB Customization */
        String reviewerEvaluation = answer.createAtomicKey(IAnswerAttributeTypeBB.STR_REVIEWER_EVALUATION).getString();
        String answerDocuments = answer.createAtomicKey(IAnswerAttributeTypeBB.STR_ANSWER_DOCUMENTS).getString();

        Set<String> allKeys = ARCMCollections.createSet();
        allKeys.add(actualScore);
        allKeys.add(annotationAllowed);
        allKeys.add(answered);
        allKeys.add(answerIndex);
        allKeys.add(dateValue);
        allKeys.add(dateValueFrom);
        allKeys.add(dateValueTo);
        allKeys.add(doubleValue);
        allKeys.add(longValue);
        allKeys.add(objID);
        allKeys.add(optionalAnswer);
        allKeys.add(performedDate);
        allKeys.add(personInCharge);
        allKeys.add(possibleOptions);
        allKeys.add(question);
        allKeys.add(questionText);
        allKeys.add(questionValue);
        allKeys.add(relatedClient);
        allKeys.add(remark);
        allKeys.add(reviewedDate);
        allKeys.add(reviewed);
        allKeys.add(reviewedBy);
        allKeys.add(reviewerRatesAnswer);
        allKeys.add(selectedOptions);
        allKeys.add(reviewerOption);
        allKeys.add(substitute);
        allKeys.add(substituteUserID);
        allKeys.add(substituteReviewer);
        allKeys.add(textValue);
        allKeys.add(type);
        allKeys.add(questionRemark);
        allKeys.add(questionDocuments);
        allKeys.add(relatedQuestionnaire);
        /** BdB Customization */
        allKeys.add(reviewerEvaluation);
        allKeys.add(answerDocuments);

        boolean isOwner = userHasRoleDependingOnAppObj(answer, Enumerations.USERROLE_TYPE.QUESTIONNAIREOWNER.getId());
        boolean isReviewer = userHasRoleDependingOnAppObj(answer, Enumerations.USERROLE_TYPE.SURVEYREVIEWER.getId());
        boolean isManager = userHasRoleDependingOnAppObj(answer, Enumerations.USERROLE_TYPE.SURVEYMANAGER.getId());
        boolean isAuditor = userHasRoleDependingOnAppObj(answer, Enumerations.USERROLE_TYPE.SURVEYAUDITOR.getId());

        Set<String> vKeys = ARCMCollections.createSet();
        List<IEnumerationItem> typeEnumList =
            answer.getRawValue(answer.createAtomicKey(IAnswerAttributeType.STR_TYPE).getString());
        IEnumerationItem e = ARCMCollections.extractSingleEntry(typeEnumList, false);

        /** BdB Customization */
        IListAttribute relatedQuestionAttribute = (IListAttribute) answer.getAttribute(IAnswerAttributeType.STR_QUESTION);
        IQuestionAppObj relatedQuestion = (IQuestionAppObj) relatedQuestionAttribute.getElements(getFullReadAccessUserContext()).iterator().next();
        Boolean questionEvidenceAllowed = relatedQuestion.getAttribute(IQuestionAttributeTypeBB.ATTR_EVIDENCESALLOWED).getPersistentRawValue();
        
        if (isOwner) {
            if (Enumerations.QUESTION_TYPE.TEXT.equals(e)) {
                vKeys.add(textValue);
            } else if (Enumerations.QUESTION_TYPE.SINGLE_CHOICE_RADIO.equals(e)) {
                vKeys.add(selectedOptions);
            } else if (Enumerations.QUESTION_TYPE.SINGLE_CHOICE_COMBOBOX.equals(e)) {
                vKeys.add(selectedOptions);
            } else if (Enumerations.QUESTION_TYPE.MULTIPLE_CHOICE.equals(e)) {
                vKeys.add(selectedOptions);
            } else if (Enumerations.QUESTION_TYPE.DATE.equals(e)) {
                vKeys.add(dateValue);
            } else if (Enumerations.QUESTION_TYPE.LONG.equals(e)) {
                vKeys.add(longValue);
            } else if (Enumerations.QUESTION_TYPE.DOUBLE.equals(e)) {
                vKeys.add(doubleValue);
            } else if (Enumerations.QUESTION_TYPE.DATERANGE.equals(e)) {
                vKeys.add(dateValueFrom);
                vKeys.add(dateValueTo);
            }
            if ((Boolean)answer.getPersistentRawValue(annotationAllowed)) {
                vKeys.add(remark);
            }
            if (!answer.isEmpty(questionRemark)) {
                vKeys.add(questionRemark);
            }
            if (!answer.isEmpty(questionDocuments)) {
                vKeys.add(questionDocuments);
            }
            if (questionEvidenceAllowed) {
				vKeys.add(answerDocuments);
			}
            vKeys.add(questionText);
            vKeys.add(type);
            vKeys.add(personInCharge);
            vKeys.add(substitute);
            vKeys.add(performedDate);
	        vKeys.add(relatedQuestionnaire);
        }
        if (isReviewer) {
            if (Enumerations.QUESTION_TYPE.TEXT.equals(e)) {
                vKeys.add(textValue);
            } else if (Enumerations.QUESTION_TYPE.SINGLE_CHOICE_RADIO.equals(e)) {
                vKeys.add(selectedOptions);
            } else if (Enumerations.QUESTION_TYPE.SINGLE_CHOICE_COMBOBOX.equals(e)) {
                vKeys.add(selectedOptions);
            } else if (Enumerations.QUESTION_TYPE.MULTIPLE_CHOICE.equals(e)) {
                vKeys.add(selectedOptions);
            } else if (Enumerations.QUESTION_TYPE.DATE.equals(e)) {
                vKeys.add(dateValue);
            } else if (Enumerations.QUESTION_TYPE.LONG.equals(e)) {
                vKeys.add(longValue);
            } else if (Enumerations.QUESTION_TYPE.DOUBLE.equals(e)) {
                vKeys.add(doubleValue);
            } else if (Enumerations.QUESTION_TYPE.DATERANGE.equals(e)) {
                vKeys.add(dateValueFrom);
                vKeys.add(dateValueTo);
            }
            if ((Boolean)answer.getPersistentRawValue(reviewerRatesAnswer)) {
                if ((Long)answer.getPersistentRawValue(answered)==1) {
                    vKeys.add(reviewedBy);
                    vKeys.add(substituteReviewer);
                    vKeys.add(reviewedDate);
                    /** BdB Customization */
                    if (Enumerations.QUESTION_TYPE.SINGLE_CHOICE_COMBOBOX.equals(e) || 
                    		Enumerations.QUESTION_TYPE.SINGLE_CHOICE_RADIO.equals(e) ||
                    		Enumerations.QUESTION_TYPE.MULTIPLE_CHOICE.equals(e)){
                    	vKeys.add(reviewerEvaluation);
                    } else {
                    	vKeys.add(reviewerOption);
                    }
                }
            }
            if ((Boolean)answer.getPersistentRawValue(annotationAllowed)) {
                vKeys.add(remark);
            }
            if (!answer.isEmpty(questionRemark)) {
                vKeys.add(questionRemark);
            }
            if (!answer.isEmpty(questionDocuments)) {
                vKeys.add(questionDocuments);
            }
            if (questionEvidenceAllowed) {
				vKeys.add(answerDocuments);
			}
            vKeys.add(questionText);
            vKeys.add(type);
            vKeys.add(actualScore);
            vKeys.add(personInCharge);
            vKeys.add(substitute);
            vKeys.add(performedDate);
	        vKeys.add(relatedQuestionnaire);
        }
        if (isManager) {
            if (Enumerations.QUESTION_TYPE.TEXT.equals(e)) {
                vKeys.add(textValue);
            } else if (Enumerations.QUESTION_TYPE.SINGLE_CHOICE_RADIO.equals(e)) {
                vKeys.add(selectedOptions);
            } else if (Enumerations.QUESTION_TYPE.SINGLE_CHOICE_COMBOBOX.equals(e)) {
                vKeys.add(selectedOptions);
            } else if (Enumerations.QUESTION_TYPE.MULTIPLE_CHOICE.equals(e)) {
                vKeys.add(selectedOptions);
            } else if (Enumerations.QUESTION_TYPE.DATE.equals(e)) {
                vKeys.add(dateValue);
            } else if (Enumerations.QUESTION_TYPE.LONG.equals(e)) {
                vKeys.add(longValue);
            } else if (Enumerations.QUESTION_TYPE.DOUBLE.equals(e)) {
                vKeys.add(doubleValue);
            } else if (Enumerations.QUESTION_TYPE.DATERANGE.equals(e)) {
                vKeys.add(dateValueFrom);
                vKeys.add(dateValueTo);
            }
            if ((Boolean)answer.getPersistentRawValue(reviewerRatesAnswer)) {
                if ((Long)answer.getPersistentRawValue(answered)==1) {
                    vKeys.add(reviewedBy);
                    vKeys.add(substituteReviewer);
                    vKeys.add(reviewedDate);
                    /** BdB Customization */
                    if (Enumerations.QUESTION_TYPE.SINGLE_CHOICE_COMBOBOX.equals(e) || 
                    		Enumerations.QUESTION_TYPE.SINGLE_CHOICE_RADIO.equals(e) ||
                    		Enumerations.QUESTION_TYPE.MULTIPLE_CHOICE.equals(e)){
                    	vKeys.add(reviewerEvaluation);
                    } else {
                    	vKeys.add(reviewerOption);
                    }
                }
            }
            if ((Boolean)answer.getPersistentRawValue(annotationAllowed)) {
                vKeys.add(remark);
            }
            if (!answer.isEmpty(questionRemark)) {
                vKeys.add(questionRemark);
            }
            if (!answer.isEmpty(questionDocuments)) {
                vKeys.add(questionDocuments);
            }
            if (questionEvidenceAllowed) {
				vKeys.add(answerDocuments);
			}
            vKeys.add(questionText);
            vKeys.add(type);
            vKeys.add(actualScore);
            vKeys.add(personInCharge);
            vKeys.add(substitute);
            vKeys.add(performedDate);
	        vKeys.add(relatedQuestionnaire);
        }
        if (isAuditor) {
            if (Enumerations.QUESTION_TYPE.TEXT.equals(e)) {
                vKeys.add(textValue);
            } else if (Enumerations.QUESTION_TYPE.SINGLE_CHOICE_RADIO.equals(e)) {
                vKeys.add(selectedOptions);
            } else if (Enumerations.QUESTION_TYPE.SINGLE_CHOICE_COMBOBOX.equals(e)) {
                vKeys.add(selectedOptions);
            } else if (Enumerations.QUESTION_TYPE.MULTIPLE_CHOICE.equals(e)) {
                vKeys.add(selectedOptions);
            } else if (Enumerations.QUESTION_TYPE.DATE.equals(e)) {
                vKeys.add(dateValue);
            } else if (Enumerations.QUESTION_TYPE.LONG.equals(e)) {
                vKeys.add(longValue);
            } else if (Enumerations.QUESTION_TYPE.DOUBLE.equals(e)) {
                vKeys.add(doubleValue);
            } else if (Enumerations.QUESTION_TYPE.DATERANGE.equals(e)) {
                vKeys.add(dateValueFrom);
                vKeys.add(dateValueTo);
            }
            if ((Boolean)answer.getPersistentRawValue(reviewerRatesAnswer)) {
                if ((Long)answer.getPersistentRawValue(answered)==1) {
                    vKeys.add(reviewedBy);
                    vKeys.add(substituteReviewer);
                    vKeys.add(reviewedDate);
                    /** BdB Customization */
                    if (Enumerations.QUESTION_TYPE.SINGLE_CHOICE_COMBOBOX.equals(e) || 
                    		Enumerations.QUESTION_TYPE.SINGLE_CHOICE_RADIO.equals(e) ||
                    		Enumerations.QUESTION_TYPE.MULTIPLE_CHOICE.equals(e)){
                    	vKeys.add(reviewerEvaluation);
                    } else {
                    	vKeys.add(reviewerOption);
                    }
                }
            }
            if ((Boolean)answer.getPersistentRawValue(annotationAllowed)) {
                vKeys.add(remark);
            }
            if (!answer.isEmpty(questionRemark)) {
                vKeys.add(questionRemark);
            }
            if (!answer.isEmpty(questionDocuments)) {
                vKeys.add(questionDocuments);
            }
            if (questionEvidenceAllowed) {
				vKeys.add(answerDocuments);
			}
            vKeys.add(questionText);
            vKeys.add(type);
            vKeys.add(actualScore);
            vKeys.add(personInCharge);
            vKeys.add(substitute);
            vKeys.add(performedDate);
	        vKeys.add(relatedQuestionnaire);
        }
        StateInformation s = env.getStateInformation();
        for (String key : allKeys) {
            s.setVisible(key, vKeys.contains(key));
        }
    }
    
    /**
     * Defines the editable flag value for most of answer attributes depending on any state of the answer
     * @see AnswerHelper#defineQuestionTypeDependentEditable()
     */
    public static void defineQuestionTypeDependentEditable() {
        REEnvironment env = REEnvironment.getInstance();
        RuleAppObj answer = env.getRuleAppObj();

        String actualScore = answer.createAtomicKey(IAnswerAttributeType.STR_ACTUALSCORE).getString();
        String annotationAllowed = answer.createAtomicKey(IAnswerAttributeType.STR_ANNOTATIONALLOWED).getString();
        String answered = answer.createAtomicKey(IAnswerAttributeType.STR_ANSWERED).getString();
        String answerIndex = answer.createAtomicKey(IAnswerAttributeType.STR_ANSWERINDEX).getString();
        String dateValue = answer.createAtomicKey(IAnswerAttributeType.STR_DATEVALUE).getString();
        String dateValueFrom = answer.createAtomicKey(IAnswerAttributeType.STR_DATEVALUEFROM).getString();
        String dateValueTo = answer.createAtomicKey(IAnswerAttributeType.STR_DATEVALUETO).getString();
        String doubleValue = answer.createAtomicKey(IAnswerAttributeType.STR_DOUBLEVALUE).getString();
        String longValue = answer.createAtomicKey(IAnswerAttributeType.STR_LONGVALUE).getString();
        String objID = answer.createAtomicKey(IAnswerAttributeType.STR_OBJ_ID).getString();
        String optionalAnswer = answer.createAtomicKey(IAnswerAttributeType.STR_OPTIONALANSWER).getString();
        String performedDate = answer.createAtomicKey(IAnswerAttributeType.STR_EXECUTION_DATE).getString();
        String personInCharge = answer.createAtomicKey(IAnswerAttributeType.STR_OWNER).getString();
        String possibleOptions = answer.createAtomicKey(IAnswerAttributeType.STR_POSSIBLEOPTIONS).getString();
        String question = answer.createAtomicKey(IAnswerAttributeType.STR_QUESTION).getString();
        String questionText = answer.createAtomicKey(IAnswerAttributeType.STR_QUESTION_TEXT).getString();
        String questionValue = answer.createAtomicKey(IAnswerAttributeType.STR_QUESTIONVALUE).getString();
        String relatedClient = answer.createAtomicKey(IAnswerAttributeType.STR_RELATED_CLIENT).getString();
        String remark = answer.createAtomicKey(IAnswerAttributeType.STR_REMARK).getString();
        String reviewedDate = answer.createAtomicKey(IAnswerAttributeType.STR_REVIEW_DATE).getString();
        String reviewed = answer.createAtomicKey(IAnswerAttributeType.STR_REVIEWED).getString();
        String reviewedBy = answer.createAtomicKey(IAnswerAttributeType.STR_REVIEWER).getString();
        String reviewerRatesAnswer = answer.createAtomicKey(IAnswerAttributeType.STR_REVIEWERRATESANSWER).getString();
        String selectedOptions = answer.createAtomicKey(IAnswerAttributeType.STR_SELECTEDOPTIONS).getString();
        String reviewerOption = answer.createAtomicKey(IAnswerAttributeType.STR_REVIEWEROPTION).getString();
        String substitute = answer.createAtomicKey(IAnswerAttributeType.STR_OWNER_SUBSTITUTE).getString();
        String substituteUserID = answer.createAtomicKey(IAnswerAttributeType.STR_SUBSTITUTE_USER_ID).getString();
        String substituteReviewer = answer.createAtomicKey(IAnswerAttributeType.STR_REVIEWER_SUBSTITUTE).getString();
        String textValue = answer.createAtomicKey(IAnswerAttributeType.STR_TEXTVALUE).getString();
        String type = answer.createAtomicKey(IAnswerAttributeType.STR_TYPE).getString();
        String questionRemark = answer.createAtomicKey(IAnswerAttributeType.STR_QUESTION_REMARK).getString();
        String questionDocuments = answer.createAtomicKey(IAnswerAttributeType.STR_QUESTION_DOCUMENTS).getString();
        /** BdB Customization */
        String reviewerEvaluation = answer.createAtomicKey(IAnswerAttributeTypeBB.STR_REVIEWER_EVALUATION).getString();
        String answerDocuments = answer.createAtomicKey(IAnswerAttributeTypeBB.STR_ANSWER_DOCUMENTS).getString();

        Set<String> allKeys = ARCMCollections.createSet();
        allKeys.add(actualScore);
        allKeys.add(annotationAllowed);
        allKeys.add(answered);
        allKeys.add(answerIndex);
        allKeys.add(dateValue);
        allKeys.add(dateValueFrom);
        allKeys.add(dateValueTo);
        allKeys.add(doubleValue);
        allKeys.add(longValue);
        allKeys.add(objID);
        allKeys.add(optionalAnswer);
        allKeys.add(performedDate);
        allKeys.add(personInCharge);
        allKeys.add(possibleOptions);
        allKeys.add(question);
        allKeys.add(questionText);
        allKeys.add(questionValue);
        allKeys.add(relatedClient);
        allKeys.add(remark);
        allKeys.add(reviewedDate);
        allKeys.add(reviewed);
        allKeys.add(reviewedBy);
        allKeys.add(reviewerRatesAnswer);
        allKeys.add(selectedOptions);
        allKeys.add(reviewerOption);
        allKeys.add(substitute);
        allKeys.add(substituteUserID);
        allKeys.add(substituteReviewer);
        allKeys.add(textValue);
        allKeys.add(type);
        allKeys.add(questionRemark);
        allKeys.add(questionDocuments);
        /** BdB Customization */
        allKeys.add(reviewerEvaluation);
        allKeys.add(answerDocuments);

        boolean isOwner = userHasRoleDependingOnAppObj(answer, Enumerations.USERROLE_TYPE.QUESTIONNAIREOWNER.getId());
        boolean isReviewer = userHasRoleDependingOnAppObj(answer, Enumerations.USERROLE_TYPE.SURVEYREVIEWER.getId());
        boolean isManager = userHasRoleDependingOnAppObj(answer, Enumerations.USERROLE_TYPE.SURVEYMANAGER.getId());
        boolean isAuditor = userHasRoleDependingOnAppObj(answer, Enumerations.USERROLE_TYPE.SURVEYAUDITOR.getId());

        Set<String> eKeys = ARCMCollections.createSet();
        List<IEnumerationItem> typeEnumList =
            answer.getRawValue(answer.createAtomicKey(IAnswerAttributeType.STR_TYPE).getString());
        IEnumerationItem e = ARCMCollections.extractSingleEntry(typeEnumList, false);

        /** BdB Customization */
        IListAttribute relatedQuestionAttribute = (IListAttribute) answer.getAttribute(IAnswerAttributeType.STR_QUESTION);
        IQuestionAppObj relatedQuestion = (IQuestionAppObj) relatedQuestionAttribute.getElements(getFullReadAccessUserContext()).iterator().next();
        Boolean questionEvidenceAllowed = relatedQuestion.getAttribute(IQuestionAttributeTypeBB.ATTR_EVIDENCESALLOWED).getPersistentRawValue();
        
        if (!isQuestionnaireExpired()) {
            if (isOwner) {
                if (isQuestionnaireInWorkflowState("openForExecution")) {
                    if (Enumerations.QUESTION_TYPE.TEXT.equals(e)) {
                        eKeys.add(textValue);
                    } else if (Enumerations.QUESTION_TYPE.SINGLE_CHOICE_RADIO.equals(e)) {
                        eKeys.add(selectedOptions);
                    } else if (Enumerations.QUESTION_TYPE.SINGLE_CHOICE_COMBOBOX.equals(e)) {
                        eKeys.add(selectedOptions);
                    } else if (Enumerations.QUESTION_TYPE.MULTIPLE_CHOICE.equals(e)) {
                        eKeys.add(selectedOptions);
                    } else if (Enumerations.QUESTION_TYPE.DATE.equals(e)) {
                        eKeys.add(dateValue);
                    } else if (Enumerations.QUESTION_TYPE.LONG.equals(e)) {
                        eKeys.add(longValue);
                    } else if (Enumerations.QUESTION_TYPE.DOUBLE.equals(e)) {
                        eKeys.add(doubleValue);
                    } else if (Enumerations.QUESTION_TYPE.DATERANGE.equals(e)) {
                        eKeys.add(dateValueFrom);
                        eKeys.add(dateValueTo);
                    }
                    if ((Boolean)answer.getPersistentRawValue(annotationAllowed)) {
                        eKeys.add(remark);
                    }
                    if (questionEvidenceAllowed) {
						eKeys.add(answerDocuments);
					}
                }
            }
        } // else: questionnaire already expired: in this case _only_the_owner_ is not allowed to edit anything
        if (isReviewer) {
            if ( isQuestionnaireInWorkflowState("openForReview") ) {
                if ((Boolean)answer.getPersistentRawValue(reviewerRatesAnswer)) {
                    if ((Long)answer.getPersistentRawValue(answered)==1) {
                    	/** BdB Customization */
                        if (Enumerations.QUESTION_TYPE.SINGLE_CHOICE_COMBOBOX.equals(e) || 
                        		Enumerations.QUESTION_TYPE.SINGLE_CHOICE_RADIO.equals(e) ||
                        		Enumerations.QUESTION_TYPE.MULTIPLE_CHOICE.equals(e)){
                        	eKeys.add(reviewerEvaluation);
                        } else {
                        	eKeys.add(reviewerOption);
                        }
                    }
                }
            }
        }
        if (isManager) {
            // The survey manager is not allowed to edit any answer attributes though.
        }
        if (isAuditor) {
            // The auditor is _never_ allowed to edit any answer attributes though.
        }
        StateInformation s = env.getStateInformation();
        for (String key : allKeys) {
            s.setEditable(key, eKeys.contains(key));
        }
    }
    
    /**
     * Defines the mandatory flag value for most of answer attributes depending on any state of the answer
     * @see AnswerHelper#defineQuestionTypeDependentMandatory()
     */
    public static void defineQuestionTypeDependentMandatory() {
        REEnvironment env = REEnvironment.getInstance();
        RuleAppObj answer = env.getRuleAppObj();

        String actualScore = answer.createAtomicKey(IAnswerAttributeType.STR_ACTUALSCORE).getString();
        String annotationAllowed = answer.createAtomicKey(IAnswerAttributeType.STR_ANNOTATIONALLOWED).getString();
        String answered = answer.createAtomicKey(IAnswerAttributeType.STR_ANSWERED).getString();
        String answerIndex = answer.createAtomicKey(IAnswerAttributeType.STR_ANSWERINDEX).getString();
        String dateValue = answer.createAtomicKey(IAnswerAttributeType.STR_DATEVALUE).getString();
        String dateValueFrom = answer.createAtomicKey(IAnswerAttributeType.STR_DATEVALUEFROM).getString();
        String dateValueTo = answer.createAtomicKey(IAnswerAttributeType.STR_DATEVALUETO).getString();
        String doubleValue = answer.createAtomicKey(IAnswerAttributeType.STR_DOUBLEVALUE).getString();
        String longValue = answer.createAtomicKey(IAnswerAttributeType.STR_LONGVALUE).getString();
        String objID = answer.createAtomicKey(IAnswerAttributeType.STR_OBJ_ID).getString();
        String optionalAnswer = answer.createAtomicKey(IAnswerAttributeType.STR_OPTIONALANSWER).getString();
        String performedDate = answer.createAtomicKey(IAnswerAttributeType.STR_EXECUTION_DATE).getString();
        String personInCharge = answer.createAtomicKey(IAnswerAttributeType.STR_OWNER).getString();
        String possibleOptions = answer.createAtomicKey(IAnswerAttributeType.STR_POSSIBLEOPTIONS).getString();
        String question = answer.createAtomicKey(IAnswerAttributeType.STR_QUESTION).getString();
        String questionText = answer.createAtomicKey(IAnswerAttributeType.STR_QUESTION_TEXT).getString();
        String questionValue = answer.createAtomicKey(IAnswerAttributeType.STR_QUESTIONVALUE).getString();
        String relatedClient = answer.createAtomicKey(IAnswerAttributeType.STR_RELATED_CLIENT).getString();
        String remark = answer.createAtomicKey(IAnswerAttributeType.STR_REMARK).getString();
        String reviewedDate = answer.createAtomicKey(IAnswerAttributeType.STR_REVIEW_DATE).getString();
        String reviewed = answer.createAtomicKey(IAnswerAttributeType.STR_REVIEWED).getString();
        String reviewedBy = answer.createAtomicKey(IAnswerAttributeType.STR_REVIEWER).getString();
        String reviewerRatesAnswer = answer.createAtomicKey(IAnswerAttributeType.STR_REVIEWERRATESANSWER).getString();
        String selectedOptions = answer.createAtomicKey(IAnswerAttributeType.STR_SELECTEDOPTIONS).getString();
        String reviewerOption = answer.createAtomicKey(IAnswerAttributeType.STR_REVIEWEROPTION).getString();
        String substitute = answer.createAtomicKey(IAnswerAttributeType.STR_OWNER_SUBSTITUTE).getString();
        String substituteUserID = answer.createAtomicKey(IAnswerAttributeType.STR_SUBSTITUTE_USER_ID).getString();
        String substituteReviewer = answer.createAtomicKey(IAnswerAttributeType.STR_REVIEWER_SUBSTITUTE).getString();
        String textValue = answer.createAtomicKey(IAnswerAttributeType.STR_TEXTVALUE).getString();
        String type = answer.createAtomicKey(IAnswerAttributeType.STR_TYPE).getString();
        String questionRemark = answer.createAtomicKey(IAnswerAttributeType.STR_QUESTION_REMARK).getString();
        String questionDocuments = answer.createAtomicKey(IAnswerAttributeType.STR_QUESTION_DOCUMENTS).getString();
        /** BdB Customization */
        String reviewerEvaluation = answer.createAtomicKey(IAnswerAttributeTypeBB.STR_REVIEWER_EVALUATION).getString();
        
        Set<String> allKeys = ARCMCollections.createSet();
        allKeys.add(actualScore);
        allKeys.add(annotationAllowed);
        allKeys.add(answered);
        allKeys.add(answerIndex);
        allKeys.add(dateValue);
        allKeys.add(dateValueFrom);
        allKeys.add(dateValueTo);
        allKeys.add(doubleValue);
        allKeys.add(longValue);
        allKeys.add(objID);
        allKeys.add(optionalAnswer);
        allKeys.add(performedDate);
        allKeys.add(personInCharge);
        allKeys.add(possibleOptions);
        allKeys.add(question);
        allKeys.add(questionText);
        allKeys.add(questionValue);
        allKeys.add(relatedClient);
        allKeys.add(remark);
        allKeys.add(reviewedDate);
        allKeys.add(reviewed);
        allKeys.add(reviewedBy);
        allKeys.add(reviewerRatesAnswer);
        allKeys.add(selectedOptions);
        allKeys.add(reviewerOption);
        allKeys.add(substitute);
        allKeys.add(substituteUserID);
        allKeys.add(substituteReviewer);
        allKeys.add(textValue);
        allKeys.add(type);
        allKeys.add(questionRemark);
        allKeys.add(questionDocuments);
        /** BdB Customization */
        allKeys.add(reviewerEvaluation);

        boolean isOwner = userHasRoleDependingOnAppObj(answer, Enumerations.USERROLE_TYPE.QUESTIONNAIREOWNER.getId());
        boolean isReviewer = userHasRoleDependingOnAppObj(answer, Enumerations.USERROLE_TYPE.SURVEYREVIEWER.getId());
        boolean isManager = userHasRoleDependingOnAppObj(answer, Enumerations.USERROLE_TYPE.SURVEYMANAGER.getId());
        boolean isAuditor = userHasRoleDependingOnAppObj(answer, Enumerations.USERROLE_TYPE.SURVEYAUDITOR.getId());

        Set<String> mKeys = ARCMCollections.createSet();
        List<IEnumerationItem> typeEnumList =
            answer.getRawValue(answer.createAtomicKey(IAnswerAttributeType.STR_TYPE).getString());
        IEnumerationItem e = ARCMCollections.extractSingleEntry(typeEnumList, false);

        if (!isQuestionnaireExpired()) {
            if (isOwner) {
                if (!(Boolean)answer.getPersistentRawValue(optionalAnswer)) {
                    if (Enumerations.QUESTION_TYPE.TEXT.equals(e)) {
                        mKeys.add(textValue);
                    } else if (Enumerations.QUESTION_TYPE.SINGLE_CHOICE_RADIO.equals(e)) {
                        mKeys.add(selectedOptions);
                    } else if (Enumerations.QUESTION_TYPE.SINGLE_CHOICE_COMBOBOX.equals(e)) {
                        mKeys.add(selectedOptions);
                    } else if (Enumerations.QUESTION_TYPE.MULTIPLE_CHOICE.equals(e)) {
                        mKeys.add(selectedOptions);
                    } else if (Enumerations.QUESTION_TYPE.DATE.equals(e)) {
                        mKeys.add(dateValue);
                    } else if (Enumerations.QUESTION_TYPE.LONG.equals(e)) {
                        mKeys.add(longValue);
                    } else if (Enumerations.QUESTION_TYPE.DOUBLE.equals(e)) {
                        mKeys.add(doubleValue);
                    } else if (Enumerations.QUESTION_TYPE.DATERANGE.equals(e)) {
                        mKeys.add(dateValueFrom);
                        mKeys.add(dateValueTo);
                    }
                }
            }
        } // else: questionnaire already expired: in this case _only_the_owner_ is not allowed to edit anything and therefore no attributes are mandatory.
        if (isReviewer) {
            if ( isQuestionnaireInWorkflowState("openForReview") ) {
                if (!(Boolean)answer.getPersistentRawValue(optionalAnswer)) {
                    if ((Boolean)answer.getPersistentRawValue(reviewerRatesAnswer)) {
                        if ((Long)answer.getPersistentRawValue(answered)==1) {
                        	/** BdB Customization */
                            if (Enumerations.QUESTION_TYPE.SINGLE_CHOICE_COMBOBOX.equals(e) || 
                            		Enumerations.QUESTION_TYPE.SINGLE_CHOICE_RADIO.equals(e) ||
                            		Enumerations.QUESTION_TYPE.MULTIPLE_CHOICE.equals(e)){
                            	mKeys.add(reviewerEvaluation);
                            } else {
                            	mKeys.add(reviewerOption);
                            }
                        }
                    }
                }
            }
        }
        if (isManager) {
            // The survey manager is not allowed to edit any answer attributes though.
        }
        if (isAuditor) {
            // The auditor is _never_ allowed to edit any answer attributes though.
        }
        StateInformation s = env.getStateInformation();
        for (String key : allKeys) {
            s.setMandatory(key, mKeys.contains(key));
        }
    }

}
