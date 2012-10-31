/*  (c) 2004 Allen I. Holub. All rights reserved.
 *
 *  This code may be used freely by yourself with the following
 *  restrictions:
 *
 *  o Your splash screen, about box, or equivalent, must include
 *    Allen Holub's name, copyright, and URL. For example:
 *
 *      This program contains Allen Holub's SQL package.<br>
 *      (c) 2005 Allen I. Holub. All Rights Reserved.<br>
 *              http://www.holub.com<br>
 *
 *    If your program does not run interactively, then the foregoing
 *    notice must appear in your documentation.
 *
 *  o You may not redistribute (or mirror) the source code.
 *
 *  o You must report any bugs that you find to me. Use the form at
 *    http://www.holub.com/company/contact.html or send email to
 *    allen@Holub.com.
 *
 *  o The software is supplied <em>as is</em>. Neither Allen Holub nor
 *    Holub Associates are responsible for any bugs (or any problems
 *    caused by bugs, including lost productivity or data)
 *    in any of this code.
 */

package erwins.jsample.database.jdbc;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

/**
 * This program is a toy database-console window that lets you exercise the
 * HolubSQL database. It opens up a databse, then displays two windows, one in
 * which you enter SQL and another that shows the result of the operation. If an
 * exception is encountered, a window showing the stack trace pops up.
 * <p>
 * Bugs: The window does not resize elegantly, so I've disabled resising
 * altogether rather than fix the problem.
 * @include /etc/license.txt
 */

public class Console {
    private static final String driverName = "erwins.database.jdbc.JDBCDriver";
    private Connection connection = null;
    private Statement statement = null;

    private JFrame mainFrame = new JFrame("HolubSQL Console");
    {
        mainFrame.getContentPane().setLayout(new GridBagLayout());
    };

    private JTextArea sqlIn = new JTextArea(5, 60);
    {
        sqlIn.setFont(new Font("Monospaced", Font.PLAIN, 14));
    }

    private JTextArea sqlOut = new JTextArea(20, 60);
    {
        sqlOut.setFont(new Font("Monospaced", Font.PLAIN, 14));
    }

    private JButton submitButton = new JButton("Submit");
    {
        submitButton.setMaximumSize(submitButton.getMinimumSize());
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                processSQL();
            }
        });
    }

    //----------------------------------------------------------------------
    public Console() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            Class.forName(driverName).newInstance();
            openDatabase();

            addToFrame(new JLabel("Type SQL here then click \"Submit.\" " + "Separate statements with semicolons."),
                    GridBagConstraints.NONE, 0.0);
            addToFrame(new JScrollPane(sqlIn), GridBagConstraints.BOTH, 0.0);
            addToFrame(submitButton, GridBagConstraints.NONE, 0.0);
            addToFrame(new JLabel(""), GridBagConstraints.NONE, 0.0);
            addToFrame(new JLabel("Output:"), GridBagConstraints.BOTH, 1.0);
            addToFrame(new JScrollPane(sqlOut), GridBagConstraints.NONE, 0.0);

            mainFrame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    closeDatabase();
                    System.exit(1);
                }
            });
            mainFrame.setResizable(false);
            mainFrame.pack();
            mainFrame.setVisible(true);
        }
        catch (ClassNotFoundException e) {
            displayException("Couldn't find driver: " + driverName, e);
        }
        catch (InstantiationException e) {
            displayException("Couldn't load driver: " + driverName, e);
        }
        catch (IllegalAccessException e) {
            displayException("Couldn't access driver: " + driverName, e);
        }
        catch (UnsupportedLookAndFeelException e) {
            displayException("Couldn't set look and feel: " + driverName, e);
        }
    }

    //----------------------------------------------------------------------
    private static final GridBagConstraints constraint = new GridBagConstraints(0, // int gridx,
            0, // int gridy,
            1, // int gridwidth,
            1, // int gridheight,
            1.0, // double weightx,
            0.0, // double weighty,
            GridBagConstraints.WEST, // int anchor,
            GridBagConstraints.BOTH, // int fill,
            new Insets(0, 0, 0, 0), // Insets insets,
            10, // int ipadx,
            10 // int ipady)
    );

    private void addToFrame(JComponent addThis, int fill, double weighty) {
        ++constraint.gridy;
        constraint.fill = fill;
        constraint.weighty = weighty;
        mainFrame.getContentPane().add(addThis, constraint);
    }

    //----------------------------------------------------------------------
    private void processSQL() {
        String input = sqlIn.getText().replaceAll("\\s+", " ");
        String statements[] = input.split(";");

        String line = "====================================\n";

        sqlIn.setText("");

        for (int i = 0; i < statements.length; ++i) {
            try {
                statements[i] = statements[i].trim();
                if (statements[i].length() == 0) continue;

                if (!(statements[i].startsWith("SELECT") || statements[i].startsWith("select"))) {
                    int status = statement.executeUpdate(statements[i]);
                    sqlOut.setText(sqlOut.getText() + line + "Processed: " + statements[i] + "\nStatus=" + String.valueOf(status) + "\n");
                } else {
                    ResultSet results = statement.executeQuery(statements[i]);
                    sqlOut.setText(sqlOut.getText() + line + "Processed: " + statements[i] + "\nResults:\n" + resultSetasString(results));
                }
            }
            catch (Exception e) {
                String message = "Error while processing statement:\n\t" + statements[i] + "\n" + e.toString();

                sqlOut.setText(sqlOut.getText() + line + message);
                displayException(message, e);
            }
        }
    }

    //----------------------------------------------------------------------
    private void displayException(String message, Exception e) {
        StringWriter trace = new StringWriter();
        e.printStackTrace(new PrintWriter(trace));
        JOptionPane.showMessageDialog(mainFrame, message + "\n\n" + trace.toString(), "Alert", JOptionPane.ERROR_MESSAGE);
    }

    //----------------------------------------------------------------------
    private String resultSetasString(ResultSet results) throws SQLException {
        ResultSetMetaData metadata = results.getMetaData();

        StringBuffer b = new StringBuffer();
        int columns = metadata.getColumnCount();
        for (int i = 1; i <= columns; ++i)
            b.append(formatColumn(metadata.getColumnName(i), 10));
        b.append("\n");

        for (int i = 1; i <= columns; ++i)
            b.append("--------- ");
        b.append("\n");

        while (results.next()) {
            for (int i = 1; i <= columns; ++i)
                b.append(formatColumn(results.getString(metadata.getColumnName(i)), 10));
            b.append("\n");
        }
        return b.toString();
    }

    //----------------------------------------------------------------------
    private String formatColumn(String msg, int width) {
        StringBuffer b = new StringBuffer(msg);
        for (width -= msg.length(); --width >= 0;)
            b.append(" ");
        return b.toString();
    }

    //----------------------------------------------------------------------
    private void openDatabase() {
        String databaseName;
        while (true) {
            databaseName = JOptionPane.showInputDialog("Enter database directory (e.g. c:/tmp/foo)\n" + "Directory must exist.");

            if (databaseName == null) System.exit(1);

            File database = new File(databaseName);
            if (database.exists() && database.isDirectory()) break;
            JOptionPane.showMessageDialog(mainFrame, "Directory " + databaseName + " does not exist.\n"
                    + "Please create it before continuing.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        try {
            connection = DriverManager.getConnection("file:/" + databaseName, "harpo", "swordfish");
            statement = connection.createStatement();
        }
        catch (SQLException e) {
            displayException("Couldn't open database: " + databaseName, e);
        }
    }

    //----------------------------------------------------------------------
    private void closeDatabase() {
        try {
            if (statement != null) statement.close();
            if (connection != null) connection.close();
        }
        catch (Exception e) {
            displayException("Closing connection", e);
        }
    }

    //----------------------------------------------------------------------
    public static void main(String[] args) throws Exception {
        new Console();
    }
}
