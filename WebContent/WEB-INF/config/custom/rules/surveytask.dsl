#### Surveytask specific items
[consequence][]check questionnaire template scores=SurveytaskHelperBB.checkTargetScore();
[consequence][]check questionnaire template sections=SurveytaskHelperBB.checkSections();
[condition][]is startdate less than today=eval(SurveytaskHelperBB.isStartDateLTToday());