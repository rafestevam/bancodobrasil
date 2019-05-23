#### Issue specific items
[consequence][]custom recalculate time dependent state=IssueHelperBB.recalculateTimeDependentState();
[consequence][]execute dealing deadline rules=IssueHelperBB.executeDealingDeadlineRules();
[consequence][]execute tech_recommendation rules=IssueHelperBB.executeTechRecommendationRules();
[consequence][]execute fault log rules=IssueHelperBB.executeFaultLogRules();
[consequence][]execute action rules=IssueHelperBB.executeActionRules();
[consequence][]execute manifestation rules=IssueHelperBB.executeManifestationRules();
[consequence][]execute sad rules=IssueHelperBB.executeSADRules();
[consequence][]execute extrapolation rules=IssueHelperBB.executeExtrapolationRules();
[condition][]can creator group delegate issues=eval( IssueHelperBB.canCreatorGroupDelegateIssues() )
[consequence][]validate mandatory documents "{issueTypePropertyKey}"=IssueHelperBB.validateMandatoryDocuments("{issueTypePropertyKey}");

#### BB items
[condition][]is_not in workflow state "{stateId}"=eval( !CollectiveHelper.isInWorkflowState("{stateId}") )
[condition][]user is "{which}" non restricted member of group in list "{attributeID}"=eval( CollectiveHelperBB.isUserInAttributeGroupNonRestricted("{which}", "{attributeID}") )
[condition][]user is in approver group=eval( CollectiveHelperBB.isUserInApproverGroup() );
[condition][]user is in non approver group=eval( CollectiveHelperBB.isUserInNonApproverGroup() );
[condition][]user performed approver action=eval( CollectiveHelperBB.performedApproverAction() );
[condition][]user not performed approver action=eval( !CollectiveHelperBB.performedApproverAction() );
[condition][]user performed non approver action=eval( CollectiveHelperBB.performedNonApproverAction() );
[condition][]user performed planning approver action=eval( CollectiveHelperBB.performedApproverActionPlanning() );
[condition][]object can be modify with non approver=eval( CollectiveHelperBB.objectCanBeModifyWithNonApprover() );
[condition][]object cannot be modify with non approver=eval( !CollectiveHelperBB.objectCanBeModifyWithNonApprover() );
[condition][]object can be modify with approver=eval( CollectiveHelperBB.objectCanBeModifyWithApprover() );
[condition][]is planning L1L2 conflict =eval( CollectiveHelperBB.conflictL1L2Planning() );
[condition][]is execution L1L2 conflict =eval( CollectiveHelperBB.conflictL1L2Execution() );
[consequence][] set object readonly=CollectiveHelperBB.setObjectReadOnly();
[consequence][] check sad deficiency_gravity questions=IssueHelperBB.checkSADDeficiencyGravityQuestions();
[condition][]is IRO=eval( IssueHelperBB.isIro() );
[condition][]are implementation approvers the same as in RT=eval( IssueHelperBB.areImplementationApproversTheSameAsInRT() );
#[condition][]are_not implementation approvers the same as in RT=eval( !IssueHelperBB.areImplementationApproversTheSameAsInRT() );
