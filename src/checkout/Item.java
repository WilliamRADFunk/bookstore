/*
 * Name: William Funk 0969985
 * Course: CNT 4714 – Spring 2016
 * Assignment title: Program 1 – Event-driven Programming
 * Date: Sunday January 24, 2016
*/
package checkout;

import java.text.NumberFormat;

public class Item
{
    private Book book;
    private int quantity;
    private double percent;
    private String percentStr;
    private double total;
    private String totalStr;

    public Item(Book book, int quantity)
    {
        this.book = book;
        this.quantity = quantity;
        this.percent = calculatePercent();
        this.percentStr = Integer.toString((int)percent);
        this.percent = ((this.percent / 100.0) + 1);
        calculateTotal();
    }
    public Book getBook() {return this.book;}
    public int getQuantity() {return this.quantity;}
    public String getPercentStr() {return this.percentStr;}
    public double getTotal() {return this.total;}
    public String getTotalStr() {return this.totalStr;}
    public String toString()
    {
        return (this.book.getId() + " " + this.book.getTitle() + " $" + this.book.getPrice() +
                " " + this.quantity + " " + this.percentStr + "% " + totalStr);
    }
    private void calculateTotal()
    {
        this.total = ( ((this.quantity * this.book.getPrice()) / this.percent) );
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        this.totalStr = formatter.format(this.total);
    }
    private double calculatePercent()
    {
        switch(this.quantity / 5)
        {
            case 0: return 0;
            case 1: return 10;
            case 2: return 15;
            default: return 20;
        }
    }
}
