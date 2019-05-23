#### Control specific items
[condition][]usergroup "{groupFunction}" has_not level "{groupLevel}" =eval(!ControlHelperBB.hasRoleLevel("{groupLevel}", "{groupFunction}"));
[condition][]usergroup "{groupFunction}" has level "{groupLevel}" =eval(ControlHelperBB.hasRoleLevel("{groupLevel}", "{groupFunction}"));