import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.zip.*;

public class ZipUnzipApp extends JFrame {
    private JTextField filePathField;
    private JButton browseButton, zipButton, unzipButton;
    private JFileChooser fileChooser;

    public ZipUnzipApp() {
        setTitle("Zip and Unzip Utility");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top Panel
        JPanel topPanel = new JPanel(new BorderLayout());
        filePathField = new JTextField();
        browseButton = new JButton("Browse");
        topPanel.add(filePathField, BorderLayout.CENTER);
        topPanel.add(browseButton, BorderLayout.EAST);

        // Bottom Panel
        JPanel bottomPanel = new JPanel(new FlowLayout());
        zipButton = new JButton("Zip");
        unzipButton = new JButton("Unzip");
        bottomPanel.add(zipButton);
        bottomPanel.add(unzipButton);

        add(topPanel, BorderLayout.NORTH);
        add(bottomPanel, BorderLayout.SOUTH);

        fileChooser = new JFileChooser();

        // Action Listeners
        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    filePathField.setText(selectedFile.getAbsolutePath());
                }
            }
        });

        zipButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String filePath = filePathField.getText();
                if (filePath.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please select a file first.");
                    return;
                }
                try {
                    zipFile(filePath);
                    JOptionPane.showMessageDialog(null, "File successfully zipped!");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Error zipping the file: " + ex.getMessage());
                }
            }
        });

        unzipButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String filePath = filePathField.getText();
                if (filePath.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please select a file first.");
                    return;
                }
                try {
                    unzipFile(filePath);
                    JOptionPane.showMessageDialog(null, "File successfully unzipped!");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Error unzipping the file: " + ex.getMessage());
                }
            }
        });
    }

    private void zipFile(String filePath) throws IOException {
        File fileToZip = new File(filePath);
        if (!fileToZip.exists()) {
            throw new FileNotFoundException("File not found: " + filePath);
        }

        FileOutputStream fos = new FileOutputStream(fileToZip.getName() + ".zip");
        ZipOutputStream zos = new ZipOutputStream(fos);
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
        zos.putNextEntry(zipEntry);

        byte[] buffer = new byte[1024];
        int len;
        while ((len = fis.read(buffer)) > 0) {
            zos.write(buffer, 0, len);
        }

        zos.closeEntry();
        fis.close();
        zos.close();
        fos.close();
    }

    private void unzipFile(String zipFilePath) throws IOException {
        File zipFile = new File(zipFilePath);
        if (!zipFile.exists() || !zipFilePath.endsWith(".zip")) {
            throw new FileNotFoundException("Invalid zip file: " + zipFilePath);
        }

        File destDir = new File(zipFile.getParent());
        ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
        ZipEntry zipEntry = zis.getNextEntry();

        while (zipEntry != null) {
            File newFile = new File(destDir, zipEntry.getName());
            FileOutputStream fos = new FileOutputStream(newFile);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
            fos.close();
            zipEntry = zis.getNextEntry();
        }

        zis.closeEntry();
        zis.close();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ZipUnzipApp app = new ZipUnzipApp();
            app.setVisible(true);
        });
    }
}
