package com.ijh165.postalsystem.frontend;

import com.ijh165.postalsystem.frontend.dialog.DelayCommandDialog;
import com.ijh165.postalsystem.frontend.dialog.PickupCommandDialog;
import com.ijh165.postalsystem.frontend.dialog.BuildCommandDialog;
import com.ijh165.postalsystem.frontend.dialog.LetterCommandDialog;
import com.ijh165.postalsystem.frontend.dialog.ScienceCommandDialog;
import com.ijh165.postalsystem.frontend.dialog.PackageCommandDialog;
import com.ijh165.postalsystem.frontend.dialog.CriminalNameDialog;
import com.ijh165.postalsystem.util.CmdTypeStr;
import javax.swing.JOptionPane;

/**
 *
 * @author IvanJonathan
 */
public class ProcessUserInteraction {    
    //process command interaction, existingCommand should be null when adding new command
    public static String commandInteraction(java.awt.Frame parent, String existingCommand) {
        String returnedString = null;
        String initialSelectionValue = CmdTypeStr.DAY_CMD;
        String[] tokens = (existingCommand != null) ? existingCommand.split("\\s") : null;
        
        //determine option pane initial selection value and if new cmd or not
        if (existingCommand != null) {
            initialSelectionValue = tokens[0];
        }
        
        //let user select items
        String[] options = {CmdTypeStr.DAY_CMD, 
                            CmdTypeStr.GOOD_CMD, 
                            CmdTypeStr.SNEAK_CMD, 
                            CmdTypeStr.INFLATION_CMD, 
                            CmdTypeStr.DEFLATION_CMD,
                            CmdTypeStr.PICKUP_CMD, 
                            CmdTypeStr.LETTER_CMD, 
                            CmdTypeStr.PACKAGE_CMD, 
                            CmdTypeStr.BUILD_CMD, 
                            CmdTypeStr.SCIENCE_CMD, 
                            CmdTypeStr.DELAY_CMD};
        String selectedOption = (String) JOptionPane.showInputDialog(parent,
                                                           "Options:",
                                                           "Select Command Type",
                                                           JOptionPane.DEFAULT_OPTION,
                                                           null,
                                                           options,
                                                           initialSelectionValue);
        
        //if user click cancel
        if(selectedOption==null) {
            return null;
        }
        
        //behave depending on option
        switch(selectedOption) {
            //commands with no fields
            case CmdTypeStr.DAY_CMD:
            case CmdTypeStr.GOOD_CMD:
            case CmdTypeStr.SNEAK_CMD:
            case CmdTypeStr.INFLATION_CMD:
            case CmdTypeStr.DEFLATION_CMD:
                returnedString = selectedOption;
                break;
            //comamnds with fields
            case CmdTypeStr.PICKUP_CMD:
                PickupCommandDialog pickupCommandDialog = new PickupCommandDialog(parent, true);
                if (existingCommand!=null && tokens[0].equals(CmdTypeStr.PICKUP_CMD)) {
                    pickupCommandDialog.setOfficeNameTextField(tokens[1]);
                    pickupCommandDialog.setRecipientTextField(tokens[2]);
                }
                pickupCommandDialog.setVisible(true);
                returnedString = pickupCommandDialog.getCommand();
                break;
            case CmdTypeStr.LETTER_CMD:
                LetterCommandDialog letterCommandDialog = new LetterCommandDialog(parent, true);
                if (existingCommand!=null && tokens[0].equals(CmdTypeStr.LETTER_CMD)) {
                    letterCommandDialog.setInitOfficeTextField(tokens[1]);
                    letterCommandDialog.setRecipientTextField(tokens[2]);
                    letterCommandDialog.setDestOfficeTextField(tokens[3]);
                    letterCommandDialog.setReturnRecipientTextField(tokens[4]);
                }
                letterCommandDialog.setVisible(true);
                returnedString = letterCommandDialog.getCommand();
                break;
            case CmdTypeStr.PACKAGE_CMD:
                PackageCommandDialog packageCommandDialog = new PackageCommandDialog(parent, true);
                if (existingCommand!=null && tokens[0].equals(CmdTypeStr.PACKAGE_CMD)) {
                    packageCommandDialog.setInitOfficeTextField(tokens[1]);
                    packageCommandDialog.setRecipientTextField(tokens[2]);
                    packageCommandDialog.setDestOfficeTextField(tokens[3]);
                    packageCommandDialog.setMoneyTextField(tokens[4]);
                    packageCommandDialog.setLengthTextField(tokens[5]);
                }
                packageCommandDialog.setVisible(true);
                returnedString = packageCommandDialog.getCommand();
                break;
            case CmdTypeStr.BUILD_CMD:
                BuildCommandDialog buildCommandDialog = new BuildCommandDialog(parent, true, true);
                if (existingCommand!=null && tokens[0].equals(CmdTypeStr.BUILD_CMD)) {
                    buildCommandDialog.setOfficeNameTextField(tokens[1]);
                    buildCommandDialog.setTransitTimeTextField(tokens[2]);
                    buildCommandDialog.setRequiredPostageTextField(tokens[3]);
                    buildCommandDialog.setCapacityTextField(tokens[4]);
                    buildCommandDialog.setPersuasionAmountTextField(tokens[5]);
                    buildCommandDialog.setMaxPackageLengthTextField(tokens[6]);
                }
                buildCommandDialog.setVisible(true);
                returnedString = buildCommandDialog.getCommand();
                break;
            case CmdTypeStr.SCIENCE_CMD:
                ScienceCommandDialog scienceCommandDialog = new ScienceCommandDialog(parent, true);
                if (existingCommand!=null && tokens[0].equals(CmdTypeStr.SCIENCE_CMD)) {
                    scienceCommandDialog.setTimeTravelValTextField(tokens[1]);
                }
                scienceCommandDialog.setVisible(true);
                returnedString = scienceCommandDialog.getCommand();
                break;
            case CmdTypeStr.DELAY_CMD:
                DelayCommandDialog delayCommandDialog = new DelayCommandDialog(parent, true);
                if (existingCommand!=null && tokens[0].equals(CmdTypeStr.SCIENCE_CMD)) {
                    delayCommandDialog.setDelayVictimTextField(tokens[1]);
                    delayCommandDialog.setDelayAmountTextField(tokens[2]);
                }
                delayCommandDialog.setVisible(true);
                returnedString = delayCommandDialog.getCommand();
                break;
        }
        
        return returnedString;
    }
    
    //process office interaction, existingOffice should be null when adding new office
    public static String officeInteraction(java.awt.Frame parent, String existingOffice) {
        String[] tokens = (existingOffice != null) ? existingOffice.split("\\s") : null;
        BuildCommandDialog officeInfoDialog = new BuildCommandDialog(parent, true, false);
        if (existingOffice != null) {
            officeInfoDialog.setOfficeNameTextField(tokens[0]);
            officeInfoDialog.setTransitTimeTextField(tokens[1]);
            officeInfoDialog.setRequiredPostageTextField(tokens[2]);
            officeInfoDialog.setCapacityTextField(tokens[3]);
            officeInfoDialog.setPersuasionAmountTextField(tokens[4]);
            officeInfoDialog.setMaxPackageLengthTextField(tokens[5]);
        }
        officeInfoDialog.setVisible(true);
        return officeInfoDialog.getCommand();
    }
    
    //process wanted interaction, existingCriminal should be null when adding new criminal
    public static String wantedInteraction(java.awt.Frame parent, String existingCriminal) {
        CriminalNameDialog criminalNameDialog = new CriminalNameDialog(parent, true);
        if (existingCriminal!=null) {
            criminalNameDialog.setCriminalNameTextField(existingCriminal);
        }
        criminalNameDialog.setVisible(true);
        return criminalNameDialog.getCriminalName();
    }
}
