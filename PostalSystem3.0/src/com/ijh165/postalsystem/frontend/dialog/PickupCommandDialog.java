package com.ijh165.postalsystem.frontend.dialog;

import java.util.regex.Pattern;
import javax.swing.JOptionPane;

/**
 *
 * @author IvanJonathan
 */
public class PickupCommandDialog extends javax.swing.JDialog {

    /**
     * Creates new form PickupCommandDialog
     */
    public PickupCommandDialog(java.awt.Frame parent, boolean modal) {
        //basic initialization
        super(parent, modal);
        initComponents();
        //center depending on parent
        super.setLocationRelativeTo(parent);
        //initialize completeCommand
        command = null;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        officeNameLabel = new javax.swing.JLabel();
        recipientLabel = new javax.swing.JLabel();
        officeNameTextField = new javax.swing.JTextField();
        recipientTextField = new javax.swing.JTextField();
        confirmButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("PICKUP Command");

        officeNameLabel.setText("Post Office Name:");

        recipientLabel.setText("Person Picking Up:");

        confirmButton.setText("Confirm");
        confirmButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                confirmButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(recipientLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(officeNameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(officeNameTextField)
                    .addComponent(recipientTextField)))
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 489, Short.MAX_VALUE)
                .addComponent(confirmButton, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(officeNameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(officeNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(recipientLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(recipientTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(confirmButton, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void confirmButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_confirmButtonActionPerformed
        //fetch input
        String officeName = officeNameTextField.getText();
        String recipient = recipientTextField.getText();
        //check for empty input
        if (officeName.length()==0 || recipient.length() == 0) {
            JOptionPane.showMessageDialog(this, "All fields cannot be empty!", 
                                          "Required information missing!", JOptionPane.ERROR_MESSAGE);
            return;
        }
        //validate input
        final Pattern nonAlphabeticPattern = Pattern.compile("[^a-zA-Z]");
        if (nonAlphabeticPattern.matcher(officeName).find() || nonAlphabeticPattern.matcher(recipient).find()) {
            JOptionPane.showMessageDialog(this, "All fields must be alphabetic and must not contain spaces!",
                                          "Invalid input!", JOptionPane.ERROR_MESSAGE);
            return;
        }
        //concatenate into command
        command = "PICKUP " + officeName + " " + recipient;
        //close dialog
        dispose();
    }//GEN-LAST:event_confirmButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed
    
    public void setOfficeNameTextField(String text) {
        officeNameTextField.setText(text);
    }
    
    public void setRecipientTextField(String text) {
        recipientTextField.setText(text);
    }
    
    public String getCommand() {
        return command;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton confirmButton;
    private javax.swing.JLabel officeNameLabel;
    private javax.swing.JTextField officeNameTextField;
    private javax.swing.JLabel recipientLabel;
    private javax.swing.JTextField recipientTextField;
    // End of variables declaration//GEN-END:variables

    private String command;
}
