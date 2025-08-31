import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class task5 extends JFrame {

    private JTextField urlField;
    private JTable table;
    private DefaultTableModel tableModel;

    public task5() {
        setTitle("Simple Web Scraper (No Jsoup)");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Input Panel
        JPanel inputPanel = new JPanel(new BorderLayout());
        JLabel urlLabel = new JLabel("Enter URL: ");
        urlField = new JTextField("https://books.toscrape.com/catalogue/category/books_1/index.html");
        JButton scrapeButton = new JButton("Scrape");

        inputPanel.add(urlLabel, BorderLayout.WEST);
        inputPanel.add(urlField, BorderLayout.CENTER);
        inputPanel.add(scrapeButton, BorderLayout.EAST);

        // Table
        String[] columnNames = {"Product Name", "Price"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        JButton saveButton = new JButton("Save to CSV");

        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(saveButton, BorderLayout.SOUTH);

        scrapeButton.addActionListener((ActionEvent e) -> scrapeData());
        saveButton.addActionListener((ActionEvent e) -> saveToCSV());
    }

    private void scrapeData() {
        String url = urlField.getText().trim();
        tableModel.setRowCount(0);

        try {
            String html = fetchHTML(url);

            // Extract product names
            Pattern namePattern = Pattern.compile("<h3>.*?<a[^>]*title=\"(.*?)\"");
            Matcher nameMatcher = namePattern.matcher(html);

            // Extract prices
            Pattern pricePattern = Pattern.compile("<p class=\"price_color\">(.*?)</p>");
            Matcher priceMatcher = pricePattern.matcher(html);

            while (nameMatcher.find() && priceMatcher.find()) {
                String name = nameMatcher.group(1);
                String price = priceMatcher.group(1);
                tableModel.addRow(new Object[]{name, price});
            }

            JOptionPane.showMessageDialog(this, "Scraping Completed!");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private String fetchHTML(String urlString) throws IOException {
        StringBuilder result = new StringBuilder();
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
        }
        return result.toString();
    }

    private void saveToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save CSV File");
        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            try (FileWriter csvWriter = new FileWriter(fileChooser.getSelectedFile() + ".csv")) {
                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    csvWriter.append(tableModel.getColumnName(i)).append(",");
                }
                csvWriter.append("\n");

                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    for (int j = 0; j < tableModel.getColumnCount(); j++) {
                        csvWriter.append(tableModel.getValueAt(i, j).toString()).append(",");
                    }
                    csvWriter.append("\n");
                }

                JOptionPane.showMessageDialog(this, "CSV File Saved Successfully!");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error Saving File: " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new task5().setVisible(true));
    }
}
