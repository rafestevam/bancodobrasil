#### Deficiency specific items
[condition][]check risk max size=eval( DeficiencyHelperBB.checkRiskMaxSize() )

[consequence][]handle magnitudes and currencies editable "{amountAttr}" "{currencyAttr}" "{naAttr}"=DeficiencyHelperBB.handleMagnitudesAndCurrenciesEditable("{amountAttr}", "{currencyAttr}", "{naAttr}");
[consequence][]decide mandatory property "{amountAttr}" "{currencyAttr}" "{naAttr}" "{commentAttr}"=DeficiencyHelperBB.decideRecommendedProperty("{amountAttr}", "{currencyAttr}", "{naAttr}", "{commentAttr}" );
[consequence][]handle magnitudes and currencies mandatory "{amountAttr}" "{currencyAttr}" "{naAttr}"=DeficiencyHelperBB.handleMagnitudesKnowned("{amountAttr}", "{currencyAttr}", "{naAttr}");
[condition][]is date attribute greater than today "{dateAttr}" =eval(CollectiveHelperBB.isDateAttributeGTToday("{dateAttr}"));
