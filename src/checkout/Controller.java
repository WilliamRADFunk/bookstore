/*
 * Name: William Funk 0969985
 * Course: CNT 4714 – Spring 2016
 * Assignment title: Program 1 – Event-driven Programming
 * Date: Sunday January 24, 2016
*/
package checkout;

import java.io.*;
import java.net.URL;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class Controller implements Initializable
{
    private static final String newLine = System.getProperty("line.separator");
    private int itemCount = 1;
    private int totalItems = 0;
    private int totalQuantity = 0;
    private double subtotal = 0.0;
    private double grandTotal = 0.0;
    private boolean totalItemsLocked = false;
    private Order order = new Order();
    private Writer writer = null;
    private Item item = null;
    private Date transDate = null;
    private java.util.List<Book> inventory = new ArrayList<>();
    private File output = new File("transactions.txt");
    private NumberFormat formatter = NumberFormat.getCurrencyInstance();

    @FXML
    private Button btn_process;
    @FXML
    private Button btn_confirm;
    @FXML
    private Button btn_view;
    @FXML
    private Button btn_finish;
    @FXML
    private Button btn_new;
    @FXML
    private Button btn_exit;
    @FXML
    private TextField text_totalItems;
    @FXML
    private TextField text_bookID;
    @FXML
    private TextField text_quantity;
    @FXML
    private TextField text_info;
    @FXML
    private TextField text_subtotal;
    @FXML
    private Label label_totalItems;
    @FXML
    private Label label_bookID;
    @FXML
    private Label label_quantity;
    @FXML
    private Label label_info;
    @FXML
    private Label label_subtotal;

    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources)
    {
        readInventoryFromFile();

        assert btn_process != null : "fx:id=\"btn_process\" was not injected: check your FXML file 'checkout.fxml'.";
        assert btn_confirm != null : "fx:id=\"btn_confirm\" was not injected: check your FXML file 'checkout.fxml'.";
        btn_confirm.setDisable(true);
        assert btn_view != null : "fx:id=\"btn_view\" was not injected: check your FXML file 'checkout.fxml'.";
        btn_view.setDisable(true);
        assert btn_finish != null : "fx:id=\"btn_finish\" was not injected: check your FXML file 'checkout.fxml'.";
        btn_finish.setDisable(true);
        assert btn_new != null : "fx:id=\"btn_new\" was not injected: check your FXML file 'checkout.fxml'.";
        assert btn_exit != null : "fx:id=\"btn_exit\" was not injected: check your FXML file 'checkout.fxml'.";

        assert text_totalItems != null : "fx:id=\"text_totalItems\" was not injected: check your FXML file 'checkout.fxml'.";
        assert text_bookID != null : "fx:id=\"text_bookID\" was not injected: check your FXML file 'checkout.fxml'.";
        assert text_quantity != null : "fx:id=\"text_quantity\" was not injected: check your FXML file 'checkout.fxml'.";
        assert text_info != null : "fx:id=\"text_info\" was not injected: check your FXML file 'checkout.fxml'.";
        text_info.setDisable(true);
        assert text_subtotal != null : "fx:id=\"text_subtotal\" was not injected: check your FXML file 'checkout.fxml'.";
        text_subtotal.setDisable(true);

        assert label_totalItems != null : "fx:id=\"label_totalItems\" was not injected: check your FXML file 'checkout.fxml'.";
        assert label_bookID != null : "fx:id=\"label_bookID\" was not injected: check your FXML file 'checkout.fxml'.";
        assert label_quantity != null : "fx:id=\"label_quantity\" was not injected: check your FXML file 'checkout.fxml'.";
        assert label_info != null : "fx:id=\"label_info\" was not injected: check your FXML file 'checkout.fxml'.";
        assert label_subtotal != null : "fx:id=\"label_subtotal\" was not injected: check your FXML file 'checkout.fxml'.";
    }

    public void processItem()
    {
        createItem();
        if(item != null)
        {
            text_info.setText(item.toString());
            btn_confirm.setDisable(false);
        }
    }
    public void confirmItem()
    {
        this.subtotal += item.getTotal();
        String subtotalStr = formatter.format(this.subtotal);
        text_bookID.setText("");
        text_quantity.setText("");
        text_subtotal.setText(subtotalStr);

        confirmAlert();

        order.addItem(item);
        this.totalQuantity += item.getQuantity();
        changeItemNumber();
        btn_confirm.setDisable(true);
        btn_view.setDisable(false);
        text_totalItems.setDisable(true);
        item = null;
    }
    public void viewOrder()
    {
        String currentOrder = "";
        int counter = 0;
        for(Item i : order.getOrder())
        {
            counter++;
            currentOrder += counter + ". " + i.toString() + "\n";
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Current Order");
        alert.setHeaderText("Your current order is as follows:");
        alert.setContentText(currentOrder);
        alert.getDialogPane().setStyle(" -fx-max-width:500px; -fx-pref-width: 500px;");

        alert.showAndWait();
    }
    public void finishOrder()
    {
        String message, transaction;
        message = buildInvoice();
        transaction = buildTransaction();

        invoiceAlert(message);
        writeTransactionsToFile(transaction);

        newOrder();
    }
    public void newOrder()
    {
        text_totalItems.setDisable(false);
        text_totalItems.setText("");
        text_bookID.setText("");
        text_quantity.setText("");
        text_info.setText("");
        text_subtotal.setText("");

        btn_process.setDisable(false);
        btn_confirm.setDisable(true);
        btn_view.setDisable(true);
        btn_finish.setDisable(true);

        this.totalItemsLocked = false;
        this.itemCount = 0;
        this.subtotal = 0.0;
        this.grandTotal = 0.0;
        this.totalQuantity = 0;
        this.order = new Order();
        this.item = null;
        this.transDate = null;

        changeItemNumber();
    }
    public void exit()
    {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Quit?");
        alert.setHeaderText("You've selected to quit this program.");
        alert.setContentText("Are you sure you want to quit?");

        Optional<ButtonType> result = alert.showAndWait();
        if(result.get() == ButtonType.OK) System.exit(0);
    }

    private void readInventoryFromFile()
    {
        try
        {
            Scanner scan = new Scanner(new File("inventory.txt"));
            while(scan.hasNext())
            {
                scan.useDelimiter(", ");
                String id = scan.next().trim();
                String title = scan.next().trim();
                scan.useDelimiter("\n");
                double price = Double.parseDouble(scan.next().replace(",", "").trim());
                Book book = new Book(id, title, price);
                inventory.add(book);
            }
        }
        catch(FileNotFoundException e) {System.out.println("DEBUG: -----Start-----\n" + e + "DEBUG: ------End------\n");}
    }
    private void writeTransactionsToFile(String transaction)
    {
        try
        {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output, true), "utf-8"));
            writer.append(transaction);
            writer.append(newLine);
        }
        catch (IOException e) {System.out.println("DEBUG: -----Start-----\n" + e + "DEBUG: ------End------\n");}
        try {if(writer != null) writer.close();}
        catch (IOException e) {System.out.println("DEBUG: -----Start-----\n" + e + "DEBUG: ------End------\n");}
    }
    private void createItem()
    {
        try
        {
            if(!totalItemsLocked)
            {
                totalItems = Integer.parseInt(text_totalItems.getText());
                totalItemsLocked = true;
            }
            String bookQuery = text_bookID.getText();
            boolean found = false;
            for(Book book : inventory)
                if(book.getId().equals(bookQuery))
                {
                    this.item = new Item(book, Integer.parseInt(text_quantity.getText()));
                    found = true;
                }
            if(!found) bookNotFoundAlert(bookQuery);
        }
        catch(Exception e) {System.out.println("DEBUG: -----Start-----\n" + e + "DEBUG: ------End------\n");}
    }
    private String buildInvoice()
    {
        double taxRate = 6;
        String message;

        this.grandTotal = ( (taxRate / 100) * this.subtotal) + this.subtotal;

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/YY HH:mm:ss z");
        this.transDate = new Date();

        message = "Date: " + dateFormat.format(this.transDate) + "\n\n";
        message += "Number of line items: " + this.totalItems + "\n\n";
        message += "Item# / ID / Title / Price / Qty / Disc % / Subtotal:\n\n";
        int counter = 0;
        for(Item i : order.getOrder())
        {
            counter++;
            message += counter + ". " + i.toString() + "\n";
        }
        message += "\n";
        message += "Order subtotal: " + formatter.format(this.subtotal) + "\n\n";
        message += "Tax rate: " + taxRate + "%\n\n";
        message += "Tax amount: " + formatter.format(((taxRate / 100) * this.subtotal)) + "\n\n";
        message += "Order total: " + formatter.format(this.grandTotal) + "\n\n";
        message += "Thanks for shopping at Funky Town Books\n\n";

        return message;
    }
    private String buildTransaction()
    {
        String transaction = "";
        DateFormat dateFormat;

        for(Item i : order.getOrder())
        {
            dateFormat = new SimpleDateFormat("YYMMddHHmmss");
            transaction += dateFormat.format(this.transDate) + ", ";
            transaction += i.getBook().getId() + ", " + i.getBook().getTitle() + ", " + i.getBook().getPriceStr();
            transaction += ", " + i.getQuantity() + ", " + i.getPercentStr() + ", " + i.getTotalStr() + ", ";
            dateFormat = new SimpleDateFormat("dd/MM/YY HH:mm:ss z");
            transaction += dateFormat.format(this.transDate) + newLine;
        }
        return transaction;
    }
    private void changeItemNumber()
    {
        if(totalItems > itemCount)
        {
            itemCount++;
            label_bookID.setText("Enter Book ID for Item #" + itemCount + ": ");
            label_quantity.setText("Enter quantity for Item #" + itemCount + ": ");
            label_info.setText("Info for Item #" + itemCount + ": ");
            label_subtotal.setText("Order subtotal for " + this.totalQuantity + " Item(s): ");
            btn_process.setText("Process Item #" + itemCount);
            btn_confirm.setText("Confirm Item #" + itemCount);
        }
        else
        {
            label_subtotal.setText("Order subtotal for " + this.totalQuantity + " Item(s): ");
            btn_process.setDisable(true);
            btn_confirm.setDisable(true);
            btn_finish.setDisable(false);
        }
    }
    private void bookNotFoundAlert(String id)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Book Not on File!");
        alert.setHeaderText(null);
        alert.setContentText("Book ID: " + id + " is not in our inventory.");

        alert.show();
    }
    private void confirmAlert()
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Item Accepted");
        alert.setHeaderText(null);
        alert.setContentText("Item #" + itemCount + " accepted.");

        alert.show();
    }
    private void invoiceAlert(String message)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Invoice");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().setStyle(" -fx-max-width:500px; -fx-pref-width: 500px;");

        alert.show();
    }
}
