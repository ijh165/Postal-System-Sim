package com.ijh165.postalsystem;

import com.ijh165.postalsystem.frontend.PostalSystemFrontend;

/**
 *
 * @author IvanJonathan
 */
public class PostalSystem {
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new PostalSystemFrontend().setVisible(true);
            }
        });
    }
}
