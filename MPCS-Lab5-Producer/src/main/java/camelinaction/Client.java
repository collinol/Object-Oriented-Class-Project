package camelinaction;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;
class Client{
	private int clientId;
	private String clientName;
    private double clientWallet = 0;

	private ArrayList<ItemTemplate> itemsForSale = new ArrayList<ItemTemplate>(0);
	private int userCommandId = 1;
	private Server server = Server.getInstance();

	public Client(int id,String name){
		this.clientId = id;
		this.clientName = name;
	}

	public int getId(){
		return this.clientId;
	}

	public String getName(){
		return this.clientName;
	}

	public double getClientWallet(){
        this.clientWallet += server.totalClientPay;
        return  clientWallet;
    }

	public void sellItem() throws FileNotFoundException{
        
		File file = new File("data/UserInputs","ClientSell#"+userCommandId+".txt");
        PrintWriter output = new PrintWriter(file) ;
		ItemTemplate item;
        String s = "";
        Scanner scanner = new Scanner(System.in);

        System.out.print("Select item this type\n1 Electronic\n2 Book\n3 Clothes\n4 Food\n: ");
        int t;
        t = Integer.parseInt(scanner.nextLine());
        switch(t){
            case 1:
                System.out.println("new Electronic item up for bid");
                item = new Electronic();
                item.setItemType(t);
                break;
            case 2:
                System.out.println("new Book item up for bid");
                item = new Book();
                item.setItemType(t);
                break;
            case 3:
                System.out.println("new Clothes item up for bid");
                item = new Clothes();
                item.setItemType(t);
                break;
            case 4:
                System.out.println("new Food item up for bid");
                item = new Food();
                item.setItemType(t);
            default:
                System.out.println("invalid selection");
                item = new Food(); // this is just here to get rid of the
                //uninstantiated error when assigning a name
                break;
        }


        System.out.print("Item name: ");

        s = scanner.nextLine();
        item.setItemName(s);
        
        //ensure valid startPrice
        while(true)
        {
            System.out.print("Starting startPrice: ");

            s = scanner.nextLine();
            
            try
            {
                item.setStartPrice(Double.parseDouble(s));
                item.setCurrentPrice(Double.parseDouble(s));
                break;
            }
            catch (NumberFormatException ignored)
            {
            }
        }
        
        
        do {
            System.out.print("Auction duration in seconds (default 10 min): ");
            s = scanner.nextLine();
            if (s.equals(""))
            {
                long millisecondsPerMinute = 60 * 1000;
                Date tenMinutesLater = new Date(System.currentTimeMillis()
                        + 10 * millisecondsPerMinute);
                item.auctionEndTime = tenMinutesLater;
            }
            else
            {

                Integer auctionDuration = Integer.parseInt(s);

                long millisecondsPerSecond = 1000;
                item.auctionEndTime = new Date(System.currentTimeMillis()
                        + auctionDuration * millisecondsPerSecond);
           
                
                
            }
        } while (false);
        item.setSellerId(clientId);
        System.out.println("item id initiated as "+server.items.size()+1);
        for (String line : toString(item).split("\n")){
        	System.out.println("writing: "+line);
        	output.write(line+"\n");
        }

        output.close();
        userCommandId += 1;
        itemsForSale.add(item);
        server.addItem(item);
	}

    private String toString(ItemTemplate item){
        String itemString = "Id "+item.getItemId()+"\nItem: "+item.getItemName()
        +"\nOriginal Price: "+item.getStartPrice()+"\nCategory: "+item.getItemTypeString(item.getItemType())
                +"\nCurrent bid: "+item.getCurrentPrice()+"\nAuction Ends:"+item.getAuctionEndTime();
        return itemString;
    }

	public ArrayList<ItemTemplate> listItemsForSale(){
		ArrayList<ItemTemplate> returnItems = new ArrayList<ItemTemplate>(0);
		Iterator itr = itemsForSale.iterator();
		if (itemsForSale.size() == 0) {
        	System.out.println("Client "+getName()+" has no items for sale");
        }
        while(itr.hasNext()){
            Object element = itr.next();
            if (element instanceof ItemTemplate) {
                Date currentTime = new Date(System.currentTimeMillis());
                if ( (((ItemTemplate)element).getAuctionEndTime()).after(currentTime) ){
                    System.out.println(toString((ItemTemplate)element)+ "\n");
                    returnItems.add((ItemTemplate)element);
                }
                else{
                	returnItems.add((ItemTemplate)element);
                    System.out.println(toString((ItemTemplate)element)+"\nHAS EXPIRED");
                    System.out.println("Sold for: "+((ItemTemplate)element).getCurrentPrice());


                }
                
            }
            
        }

        return returnItems;
	}

	public class returnBidPair{
		private boolean success;
		private int itemBidId;
		public returnBidPair(boolean success,int itemBidId){
			this.success = success;
			this.itemBidId = itemBidId;
		}
		public boolean getSucces(){return this.success;}
		public int getItem(){return this.itemBidId;}
	}

	public returnBidPair bid() {
		
        returnBidPair rbp;
        System.out.println("select item id to bid on, or type 'browse' to view items, or enter 'q' to exit bid option");
        label:
        while (true) {
            Scanner bidOption = new Scanner(System.in);
            String choice = bidOption.nextLine();
            switch (choice) {
                case "browse":
                    server.listItems();
                    break;
                case "q":
                    break label;
                default:
                    Integer itemIdSelect = null;
                    try {
                        itemIdSelect = Integer.parseInt(String.valueOf(choice));
                    } catch (NumberFormatException | NullPointerException e) {
                        System.out.println("invalid option");
                    }
                    if (server.items.get(itemIdSelect - 1).getSellerId() == this.clientId){
                        System.out.println("You cannot bid on your own item");
                    }
                    else if (itemIdSelect <= server.items.size()) {
                        ItemTemplate itemSelected = server.items.get(itemIdSelect - 1);
                        System.out.println("Enter your bid");
                        Scanner bid = new Scanner(System.in);
                        double bidPrice = Double.parseDouble(bid.nextLine());
                        double currentPrice = itemSelected.getCurrentPrice();
                        if (bidPrice > currentPrice && itemSelected.getSellerId() != this.clientId && !itemSelected.getItemExpiration()) {
                            itemSelected.setWinningBidderId(clientId);
                            System.out.println("Client " + clientId + " currently has the highest bid on item " + itemIdSelect);
                            itemSelected.setWinningBidderId(clientId);
                            itemSelected.setCurrentPrice(bidPrice);
                            rbp = new returnBidPair(true,itemSelected.getItemId());
                            return rbp;
                        }
                        else if (bidPrice <= currentPrice) {
                            while (true){
                                System.out.println("Your bid is too low." +
                                        "The current bid is $"+itemSelected.getCurrentPrice() +"\nPlease enter a new bid, or type 'q' to exit");
                                Scanner reBid = new Scanner(System.in);
                                String reBidPriceString = reBid.nextLine();
                                double reBidPrice = 0;
                                try {
                                    reBidPrice = Double.parseDouble(reBidPriceString);
                                } catch (NumberFormatException | NullPointerException e) {
                                    if (reBidPriceString == "q"){
                                        break;
                                    }
                                    else
                                        System.out.println("invalid option");
                                }

                                if (reBidPrice > currentPrice && itemSelected.getSellerId() != this.clientId && !itemSelected.getItemExpiration()) {
                                    itemSelected.setWinningBidderId(clientId);
                                    System.out.println("Client " + clientId + " currently has the highest bid on item " + itemIdSelect);
                                    itemSelected.setWinningBidderId(clientId);
                                    itemSelected.setCurrentPrice(bidPrice);
                                    rbp = new returnBidPair(true,itemSelected.getItemId());
                                    return rbp;
                                }

                                rbp = new returnBidPair(false,itemSelected.getItemId());
                                return rbp;
                            }
                            break;
                        }
                        else if (!itemSelected.getItemExpiration()) {
                            System.out.println("That Item has expired");
                        }



                    } else {
                        System.out.println("Sorry, I had trouble processing your item selection. " +
                                "Please make sure you enter the id number of an existing item. " +
                                "You can type 'browse' to view all items");
                    }
                    break;
            }
            break;
        }
        rbp = new returnBidPair(false,-1);
        return rbp;
    }


}