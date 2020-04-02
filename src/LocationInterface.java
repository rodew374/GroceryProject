import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.sql.*;

public class LocationInterface {
    private JPanel spacerLeft;
    private JPanel spacerRight;
    private JPanel spacerBottom;
    private JPanel paneLocation;
    private JTextField inputLoc;
    private JPanel panePriority;
    private JTextField inputPrior;
    private JPanel paneInsert;
    private JButton insertButton;
    private JPanel spacerTableUpper;
    private JScrollPane paneData;
    private JTable table;
    private JPanel paneEdit;
    private JButton editButton;
    private JPanel paneDelete;
    private JButton deleteButton;
    private JPanel spacerTableLower;
    private JPanel paneLocationEdit;
    private JTextField editLoc;
    private JPanel panePriorityEdit;
    private JTextField editPrior;
    private JPanel paneUpdate;
    private JButton updateButton;
    private JPanel locationWindow;
    private JPanel spacerTop;
    private Connection con;

    /**
     * Constructor. Creates a connection to the DB.
     * Initializes the ActionEvents for the buttons.
     */
    public LocationInterface() {
        createConnection();

        deleteButton.addActionListener(this::delete);
        editButton.addActionListener(this::edit);
        insertButton.addActionListener(this::insert);
        updateButton.addActionListener(this::update);

        refresh();
    }

    /**
     * Creates the JFrame window.
     * @param args
     */
    public static void main(String[] args) {
        JFrame frame;

        frame = new JFrame("LocationInterface");
        frame.setContentPane(new LocationInterface().locationWindow);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * Creates the initial connection to the the database.
     */
    private void createConnection() {

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/project_grocer", "root", "7@AyCXG3");

            System.out.println("Database Connection Success\n");

        } catch (SQLException| ClassNotFoundException e) {
            e.printStackTrace();

        }

    }

    /**
     * ActionEvent for the deleteButton.
     * Deletes the selected row of data from
     * the database table.
     * @param e
     */
    private void delete(ActionEvent e) {
        int row;
        PreparedStatement stmt;
        String loc;

        row = table.getSelectedRow();
        loc = table.getValueAt(row, 0).toString();

        try {
            stmt = con.prepareStatement("Delete FROM MAP WHERE LOCATION=?");
            stmt.setString(1, loc);

            stmt.executeUpdate();
            refresh();
            System.out.println("Deletion Completed");

            stmt.close();

        } catch (SQLException ex) {
            ex.printStackTrace();

        }
    }

    /**
     * ActionEvent for the editButton.
     * Copies the selected row of the table into
     * the update text boxes.
     * @param r
     */
    private void edit(ActionEvent r) {
        DefaultTableModel model;
        int row;
        String loc, prior;

        model = (DefaultTableModel) table.getModel();
        row = table.getSelectedRow();
        loc = (String) model.getValueAt(row, 0);
        prior = model.getValueAt(row, 1).toString();

        editLoc.setText(loc);
        editPrior.setText(prior);

    }

    /**
     * Action event for the insertButton.
     * Inserts the data entered into the
     * input text fields into the DB.
     * @param e
     */
    private void insert(ActionEvent e) {
        int prior;
        PreparedStatement stmt;
        String loc;

        loc = inputLoc.getText();
        prior = Integer.parseInt(inputPrior.getText());

        try {
            stmt = con.prepareStatement("INSERT INTO MAP VALUES(?,?)");
            stmt.setString(1, loc);
            stmt.setInt(2, prior);

            stmt.execute();
            refresh();
            System.out.println("Insertion Completed\n");

            stmt.close();

        } catch (SQLException ex) {
            ex.printStackTrace();

        }
    }

    /**
     * Refreshes the JFrame table to reflect any updates.
     */
    private void refresh() {
        DefaultTableModel tableModel;
        Statement stmt;
        ResultSet rs;

        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new Object[]{"Location", "Priority"});

        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT * FROM MAP");

            while (rs.next()) {
                int prior;
                String loc;

                loc = rs.getString("location");
                prior = rs.getInt("priority");

                tableModel.addRow(new Object[]{loc, prior});

            }

            table.setModel(tableModel);

            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();

        }
    }

    /**
     * ActionEvent for the updateButton.
     * Updates the selected location with a
     * new priority in the database.
     * @param e
     */
    private void update(ActionEvent e) {
        PreparedStatement stmt;

        try {
            stmt = con.prepareStatement("UPDATE MAP SET PRIORITY = ? WHERE LOCATION = ?");
            stmt.setInt(1, Integer.parseInt(editPrior.getText()));
            stmt.setString(2, editLoc.getText());

            stmt.executeUpdate();
            refresh();
            System.out.println("Update Completed\n");

            stmt.close();

        } catch (SQLException ex) {
            ex.printStackTrace();

        }
    }
}
