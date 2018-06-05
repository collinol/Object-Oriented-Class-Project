package camelinaction;

import java.util.*;
import java.util.Date;

class Server
{
    private static Server single_instance = null;
    public double ahPayOut = 0;
    public double clientPayOut = 0;

    public double totalAhPay;
    public double totalClientPay;

    public ArrayList<ItemTemplate> items;

    private Server(){
        items = new ArrayList<>(0);
    }

    public static Server getInstance()
    {

        if (single_instance == null)
            single_instance = new Server();
 
        return single_instance;
    }

    public void addItem(ItemTemplate item){
        items.add(item);
    }

    private String toString(ItemTemplate item){
        return "Item: "+item.getItemName()
        +"\nPrice: "+item.getCurrentPrice()+"\nCategory: "+item.getItemTypeString(item.getItemType()) +"\nAuction Ends:"+item.getAuctionEndTime();
    }

    public ArrayList<ItemTemplate> listItems(){
        Iterator itr = items.iterator();
        ArrayList<ItemTemplate> returnItems = new ArrayList<ItemTemplate>(0);
        System.out.println("All items for sale:");
        if (!itr.hasNext()) {
            System.out.println("There are no items for sale on the Auction House");
        }
        this.clientPayOut = 0;
        this.ahPayOut = 0;
        this.totalClientPay = 0;
        this.totalAhPay = 0;
        while(itr.hasNext()){
            Object element = itr.next();
            if (element instanceof ItemTemplate) {
                Date currentTime = new Date(System.currentTimeMillis());
                if ( (((ItemTemplate)element).getAuctionEndTime()).after(currentTime) ){
                    System.out.println(toString((ItemTemplate)element)+ "\n");
                    returnItems.add((ItemTemplate)element);
                }
                else{

                    System.out.println(toString((ItemTemplate)element)+"\nHAS EXPIRED");
                    returnItems.add((ItemTemplate)element);
                    System.out.println("Sold for: "+((ItemTemplate)element).getCurrentPrice());
                    if (!((ItemTemplate) element).getItemHasBeenPaid()) {

                        PaymentStrategy basePayStrategy = null;
                        if (((ItemTemplate) element).getItemType() == 1) {
                            basePayStrategy = new ElectronicsPayment();
                        } else if (((ItemTemplate) element).getItemType() == 2) {
                            basePayStrategy = new BookPayment();
                        } else if (((ItemTemplate) element).getItemType() == 3) {
                            basePayStrategy = new ClothesPayment();
                        } else if (((ItemTemplate) element).getItemType() == 4) {
                            basePayStrategy = new FoodPayment();
                        }
                        this.clientPayOut += basePayStrategy.payOut((ItemTemplate) element)[0];
                        this.ahPayOut += basePayStrategy.payOut((ItemTemplate) element)[1];
                        ((ItemTemplate) element).setItemHasBeenPaid(true);
                    }
                    else{
                        this.clientPayOut += 0;
                        this.ahPayOut += 0;
                    }

                    this.totalAhPay += ahPayOut;
                    this.totalClientPay += clientPayOut;
                }
            }
            
        }
        return returnItems;
    }

    //Use an iterator to safely remove item from ArrayList of items.






}