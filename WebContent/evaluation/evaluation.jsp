<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/aam.tld" prefix="aam" %>
<%@ taglib uri="/WEB-INF/aamgui.tld" prefix="gui" %>
<!DOCTYPE html>
<html>
<head>
<aam:title />
<aam:scripts files="jquery,jquery-ui,jquery.colorpicker,submit,logic,listhelper,evaluationhelper,domLib,domTT,extended-combobox"/>
<aam:styles files="base,styles,topnav,sidenav,jquery,tooltip,evaluation,dialogs,filter,extended-combobox" />
<script>
var exportName = '';
<aam:toolBar  />
aam_enableTabs();	
</script>
</head>
<body>
<gui:control />
</body>
<script type="text/javascript">
updateTarget();
<aam:toolBar type="evaluation" />
</script>
</html>
