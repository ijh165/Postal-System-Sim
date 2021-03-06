package com.ijh165.postalsystem.frontend.dialog;

import java.util.regex.Pattern;
import javax.swing.JOptionPane;

/**
 *
 * @author IvanJonathan
 */
public class ScienceCommandDialog extends javax.swing.JDialog {

    /**
     * Creates new form ScienceCommandDialog
     */
    public ScienceCommandDialog(java.awt.Frame parent, boolean modal) {
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

        timeTravelValLabel = new javax.swing.JLabel();
        timeTravelValTextField = new javax.swing.JTextField();
        confirmButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("SCIENCE Command");

        timeTravelValLabel.setText("Time Travel Value:");

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
                .addComponent(timeTravelValLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(timeTravelValTextField))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(317, Short.MAX_VALUE)
                .addComponent(confirmButton, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(timeTravelValLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(timeTravelValTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(confirmButton, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void confirmButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_confirmButtonActionPerformed
        //fetch input
        String timeTravelVal = timeTravelValTextField.getText();
        //check for empty input
        if (timeTravelVal.length()==0)
        {
            String timeTravelValString = timeTravelValLabel.getText().substring(0, timeTravelValLabel.getText().length()-1);
            JOptionPane.showMessageDialog(this, "\"" + timeTravelValString + "\" cannot be empty!",
                                          "Required information missing!", JOptionPane.ERROR_MESSAGE);
            return;
        }
        //validate input
        Pattern p = Pattern.compile("[^0-9]");
        if (p.matcher(timeTravelVal).find() && timeTravelVal.charAt(0)!='-') {
            String timeTravelValString = timeTravelValLabel.getText().substring(0, timeTravelValLabel.getText().length()-1);
            JOptionPane.showMessageDialog(this, "\"" + timeTravelValString + "\" must be integer!",
                                          "Invalid input!", JOptionPane.ERROR_MESSAGE);
            return;
        }
        //concatenate into command
        command = "SCIENCE " + timeTravelVal;
        //close dialog
        dispose();
    }//GEN-LAST:event_confirmButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed
    
    public void setTimeTravelValTextField(String text) {
        timeTravelValTextField.setText(text);
    }
    
    public String getCommand() {
        return command;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton confirmButton;
    private javax.swing.JLabel timeTravelValLabel;
    private javax.swing.JTextField timeTravelValTextField;
    // End of variables declaration//GEN-END:variables

    private String command;
}
