/*
 * Name: William Funk 0969985
 * Course: CNT 4714 – Spring 2016
 * Assignment title: Program 1 – Event-driven Programming
 * Date: Sunday January 24, 2016
*/
package checkout;

import java.util.ArrayList;
import java.util.List;

public class Order
{
    private List<Item> list = new ArrayList<>();

    public Order() {}
    public void addItem(Item item) {list.add(item);}
    public List<Item> getOrder() {return list;}
}
