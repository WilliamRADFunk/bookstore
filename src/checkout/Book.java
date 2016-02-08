/*
 * Name: William Funk 0969985
 * Course: CNT 4714 – Spring 2016
 * Assignment title: Program 1 – Event-driven Programming
 * Date: Sunday January 24, 2016
*/
package checkout;

import java.text.NumberFormat;

public class Book
{
    private String id;
    private String title;
    private double price;

    public Book(String id, String title, double price)
    {
        this.id = id;
        this.title = title;
        this.price = price;
    }
    public String getId() {return this.id;}
    public String getTitle() {return this.title;}
    public double getPrice() {return this.price;}
    public String getPriceStr()
    {
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        return (formatter.format(this.price));
    }
}
