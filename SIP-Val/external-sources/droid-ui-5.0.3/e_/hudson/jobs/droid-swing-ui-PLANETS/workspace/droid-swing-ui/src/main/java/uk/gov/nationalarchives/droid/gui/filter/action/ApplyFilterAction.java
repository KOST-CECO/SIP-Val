/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.gui.filter.action;

import java.awt.Component;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import uk.gov.nationalarchives.droid.core.interfaces.filter.CriterionFieldEnum;
import uk.gov.nationalarchives.droid.gui.action.ApplyFilterToTreeTableAction;
import uk.gov.nationalarchives.droid.gui.filter.DatePicker;
import uk.gov.nationalarchives.droid.gui.filter.FilterDialog;
import uk.gov.nationalarchives.droid.gui.filter.TextBoxAndButton;
import uk.gov.nationalarchives.droid.gui.filter.domain.FilterValidationException;
import uk.gov.nationalarchives.droid.gui.filter.domain.GenericMetadata;
import uk.gov.nationalarchives.droid.profile.FilterCriterionImpl;

/**
 * @author Alok Kumar Dash
 *
 */
public class ApplyFilterAction {

    
    /**
     * Applies filter after Apply filter button is pressed.
     * @param filterDialog FIlterDialog.
     */
    public void applyFilter(FilterDialog filterDialog) {
        
        
        
        
        DefaultTableModel tableModel = (DefaultTableModel) filterDialog.getFilterTable().getModel();
        List<String> errorStrings = new ArrayList<String>();
        // Iterate through the table and set all the free text.
        // do not anything for the last row.
        for (int i = 0; i < tableModel.getRowCount() - 1; i++) {
            // get the first combobox selected item.
            final JComboBox comboBox1 = (JComboBox) tableModel.getValueAt(i, 0);
            CriterionFieldEnum firstComboBoxSelectedItem = (CriterionFieldEnum) comboBox1.getSelectedItem();
            // get the Component .
            Component componentAtThirdRow = (Component) tableModel.getValueAt(
                    i, 2);

            String freeTextAtRow = null;
            try {
                if (componentAtThirdRow instanceof TextBoxAndButton) {
                    freeTextAtRow = ((TextBoxAndButton) componentAtThirdRow)
                            .getTextField().getText();
                } else if (componentAtThirdRow instanceof DatePicker) {
                    freeTextAtRow = ((DatePicker) componentAtThirdRow)
                            .getDateString();
                } else {
                    throw new RuntimeException("Fatal error");
                }
    
                // get meta data object from the selected string.
                GenericMetadata metadata = filterDialog.getFilterDomain()
                        .getMetaDataFromFieldType(firstComboBoxSelectedItem);

                // Validate the values.
                metadata.validate(freeTextAtRow);
            } catch (FilterValidationException filterValidationException) {
                errorStrings.add(filterValidationException.getMessage());
            }
            FilterCriterionImpl filterCriteria = filterDialog.getFilterContext().getFilterCriteria(i);
            filterCriteria.setValueFreeText(freeTextAtRow);
        }

        
        if (errorStrings.size() > 0) {
            JOptionPane.showMessageDialog(filterDialog, errorStrings.toArray());
        } else {
            filterDialog.getDroidContext().getSelectedProfile().getProfile().setDirty(true);
//            filterDialog.getDroidContext().getSelectedProfile().onResourceChanged();
            ApplyFilterToTreeTableAction applyFilterToTreeAction = 
                new ApplyFilterToTreeTableAction(filterDialog.getDroidContext(), filterDialog.getProfileManager());
            applyFilterToTreeAction.applyFilter();
            filterDialog.dispatchEvent(new WindowEvent(filterDialog,  WindowEvent.WINDOW_CLOSING));
        }
    }

    
}
