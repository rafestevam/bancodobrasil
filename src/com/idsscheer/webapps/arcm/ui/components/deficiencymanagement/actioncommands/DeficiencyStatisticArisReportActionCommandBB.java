/**
 * 
 */
package com.idsscheer.webapps.arcm.ui.components.deficiencymanagement.actioncommands;

import java.util.List;

import com.idsscheer.webapps.arcm.bl.framework.evaluation.statistic.IStatisticModel;
import com.idsscheer.webapps.arcm.bl.models.dialog.IBLDialogModel;
import com.idsscheer.webapps.arcm.common.constants.ReportType;
import com.idsscheer.webapps.arcm.config.Metadata;
import com.idsscheer.webapps.arcm.config.metadata.dialog.IDialog;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;
import com.idsscheer.webapps.arcm.ui.framework.actioncommands.evaluation.BaseStatisticArisReportActionCommand;

/**
 * <p>
 * Before executing the PDF or XLS aris report this action shows another dialog where the user has to decide:
 * <ul>
 * <li>which datarows shall be displayed (all rows or only visible rows)</li>
 * </ul>
 * </p>
 * <p> This is a customization for <b><code>Banco do Brasil Project</code></b> </p>
 * @author brmob
 * @since v20160314-MNT-UATEnhancements
 * @see BaseStatisticArisReportActionCommand
 */
public class DeficiencyStatisticArisReportActionCommandBB<M extends IStatisticModel> extends BaseStatisticArisReportActionCommand<M> {
	
	/**
     * Creates and fills the dialog "statistic_report" (if there are no charts inside the statistic) or the
     * "statistic_report_chart" (in case of charts).
     * If this dialog already exists on the breadcrumb then its values are read instead.
     *
     * @return true if the dialog creation / reading was successful
     */
	@Override
	protected boolean getReportOptionDialog() {
		/*
		 * BdB wants dialogs even when it's a XLS format
		 *
		 *	if (!reportType.equals(ReportType.PDF)) {
		 *		return true;
		 *	}
		 */

        //get dialog ID by convention
        String dialogID = getDialogID();
        IDialog dialogDefinition = Metadata.getMetadata().getDialog(dialogID);

        //if there is a dialog specified then display it
        if (dialogDefinition != null) {
            if (!isDialogReady(dialogID)) {
                createReportOptionDialog(dialogID);
                redirectToSelectionJSP = true;
            } else {
                IBLDialogModel dialog = environment.getBreadcrumbStack().peek().getDialog(getDialogID());
                readReportOptionDialog(dialog);
            }
        }

        return true;
	}
	
	@Override
	protected String getDialogID() {
		if (chartColumnMap.isEmpty() || reportType.equals(ReportType.EXCEL)) {
            return "statistic_report";
        } else {
            return "statistic_report_chart";
        }
	}
	
	@Override
	protected void readReportOptionDialog(IBLDialogModel dialog) {
		hierachyElementsOption = ((List<IEnumerationItem>)dialog.getDialog().getAttribute("hierarchy").getValue()).get(0);
        if (!chartColumnMap.isEmpty() && !reportType.equals(ReportType.EXCEL)) {
            selectedChartColumnIDs = (List<String>)dialog.getDialog().getAttribute("chart").getValue();
        }
	}

}
