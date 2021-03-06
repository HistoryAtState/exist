/*
 *  eXist Open Source Native XML Database
 *  Copyright (C) 2001-2015 The eXist Project
 *  http://exist-db.org
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.exist.backup;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;


public class RestoreDialog extends JDialog {
    private static final long serialVersionUID = 3773486348231766907L;
    private static final DecimalFormat PERCENTAGE_FORMAT = new DecimalFormat("###.##%");

    JTextField currentCollection;
    JTextField currentBackup;
    JTextField resource;
    JTextArea messages;
    JProgressBar progress;
    JButton dismissButton;

    private long totalRestoreUncompressedSize = 0;
    private long zippedUncompressedSize = 0;

    private long totalTransferSize = 0;
    private long transferredSize = 0;

    private long totalNumberOfFiles = 0;
    private long fileCounter = 0;


    /**
     * Creates a new RestoreDialog object.
     *
     * @param owner Parent window.
     * @param title Window title.
     * @param modal Flag to have modal window.
     * @throws HeadlessException Environment  does not support a keyboard, display, or mouse.
     */
    public RestoreDialog(final Frame owner, final String title, final boolean modal) throws HeadlessException {
        super(owner, title, modal);
        setupComponents();
        setSize(new Dimension(350, 200));
        pack();
    }

    private void setupComponents() {
        final GridBagLayout grid = new GridBagLayout();
        getContentPane().setLayout(grid);
        final GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);

        JLabel label = new JLabel("Backup:");
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.EAST;
        c.fill = GridBagConstraints.NONE;
        grid.setConstraints(label, c);
        getContentPane().add(label);

        currentBackup = new JTextField(50);
        currentBackup.setEditable(false);
        c.gridx = 1;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        grid.setConstraints(currentBackup, c);
        getContentPane().add(currentBackup);

        label = new JLabel("Collection:");
        c.gridx = 0;
        c.gridy = 1;
        c.anchor = GridBagConstraints.EAST;
        c.fill = GridBagConstraints.NONE;
        grid.setConstraints(label, c);
        getContentPane().add(label);

        currentCollection = new JTextField(50);
        currentCollection.setEditable(false);
        c.gridx = 1;
        c.gridy = 1;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        grid.setConstraints(currentCollection, c);
        getContentPane().add(currentCollection);

        label = new JLabel("Restoring:");
        c.gridx = 0;
        c.gridy = 2;
        c.anchor = GridBagConstraints.EAST;
        c.fill = GridBagConstraints.NONE;
        grid.setConstraints(label, c);
        getContentPane().add(label);

        resource = new JTextField(40);
        resource.setEditable(false);
        c.gridx = 1;
        c.gridy = 2;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        grid.setConstraints(resource, c);
        getContentPane().add(resource);

        label = new JLabel("Progress:");
        c.gridx = 0;
        c.gridy = 3;
        c.anchor = GridBagConstraints.EAST;
        c.fill = GridBagConstraints.NONE;
        grid.setConstraints(label, c);
        getContentPane().add(label);

        progress = new JProgressBar();
        progress.setStringPainted(true);
        progress.setString("");
        progress.setIndeterminate(false);
        c.gridx = 1;
        c.gridy = 3;
        c.anchor = GridBagConstraints.EAST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        grid.setConstraints(progress, c);
        getContentPane().add(progress);

        messages = new JTextArea(5, 50);
        messages.setEditable(false);
        final JScrollPane scroll = new JScrollPane(messages, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(BorderFactory.createTitledBorder("Messages"));
        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1.0;
        grid.setConstraints(scroll, c);
        getContentPane().add(scroll);

        dismissButton = new JButton("Dismiss");
        dismissButton.setEnabled(false);
        dismissButton.addActionListener(e -> setVisible(false));
        c.gridx = 0;
        c.gridy = 5;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.NONE;
        grid.setConstraints(dismissButton, c);
        getContentPane().add(dismissButton);
    }

    public void setBackup(final String backup) {
        currentBackup.setText(backup);
    }

    public void setCollection(final String collection) {
        currentCollection.setText(collection);
    }

    public void setResource(final String current) {
        resource.setText(current);
    }

    public void displayMessage(final String message) {
        messages.append(message + '\n');
        messages.setCaretPosition(messages.getDocument().getLength());
    }

    public void setTotalRestoreUncompressedSize(final long totalRestoreUncompressedSize) {
        this.totalRestoreUncompressedSize = totalRestoreUncompressedSize;
    }

    public void setTotalTransferSize(final long totalTransferSize) {
        this.totalTransferSize = totalTransferSize;
    }

    /**
     *  Set the total number of files in the backup.
     *
     * @param totalNumberOfFiles Number of files.
     */
    public void setTotalNumberOfFiles(final long totalNumberOfFiles) {
        this.totalNumberOfFiles = totalNumberOfFiles;
    }

    public void addedFileToZip(final long uncompressedSize) {
        zippedUncompressedSize += uncompressedSize;

        if (totalRestoreUncompressedSize < 0L) {
            progress.setString("N/A");
        } else {
            final double percentage = zippedUncompressedSize / (double)totalRestoreUncompressedSize;
            progress.setString(PERCENTAGE_FORMAT.format(percentage));
            progress.setValue((int)(percentage * 100));
        }
    }

    public void transferred(final long chunkSize) {
        transferredSize += chunkSize;

        if (totalTransferSize < 0L) {
            progress.setString("N/A");
        } else {
            final double percentage = transferredSize / (double)totalTransferSize;
            progress.setString(PERCENTAGE_FORMAT.format(percentage));
            progress.setValue((int)(percentage * 100));
        }
    }

    /**
     * Increment the number of files that are restored and display the value.
     */
    public void incrementFileCounter() {
        incrementFileCounter(1);
    }

    /**
     * Increment the number of files that are restored and display the value.
     *
     * @param step the amount to increment by
     */
    public void incrementFileCounter(final long step) {
        fileCounter += step;

        if (totalNumberOfFiles == 0L) {
            progress.setString("N/A");
        } else {
            final double percentage = fileCounter / (double)totalNumberOfFiles;
            progress.setString(PERCENTAGE_FORMAT.format(percentage));
            progress.setValue((int)(percentage * 100));
        }
    }

}
