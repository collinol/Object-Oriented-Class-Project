package camelinaction;import java.util.*;

abstract class ItemTemplate{

	//Auction House's cut determined by concrete classes. The rest is the same
	public double cut;
	public abstract double getCut();
	public final void setCut(double cut){this.cut = cut;}

	public int type;
	public final int getItemType(){return type;}
    public final String getItemTypeString(int type){
        switch(type){
            case 1: return "electronics";
            case 2: return "books";
            case 3: return "clothes";
            case 4: return "food";
            default: return "Unknown";
        }
    }
	public final void setItemType(int type){this.type = type;}

	private boolean itemHasBeenPaid = false;
	public final void setItemHasBeenPaid(boolean paid){this.itemHasBeenPaid = paid;}
	public final boolean getItemHasBeenPaid(){return this.itemHasBeenPaid;}

	private boolean itemIsExpired = false;
	public final void setItemExpiration(boolean expired){this.itemIsExpired = expired;}
	public final boolean getItemExpiration(){return  this.itemIsExpired;}

	public int id;
	public final void setItemId(int id){this.id = id;}
	public final int getItemId(){return id;}

    public String name;
    public final String getItemName() { return name; }
    public final void setItemName(String name) { this.name = name; }

    public double startPrice;
    public final double getStartPrice() { return startPrice; }
    public final void setStartPrice(double price) { this.startPrice = price; }

    public double currentPrice;
    public final double getCurrentPrice() { return currentPrice; }
    public final void setCurrentPrice(double price) { this.currentPrice = price; }


    public int sellerId;
    public final int getSellerId() { return sellerId; }
    public final void setSellerId(int sellerId) { this.sellerId = sellerId; }

    public int winningBidderId;
    public final int getWinningBidderId() { return winningBidderId; }
    public final void setWinningBidderId(int winningBidderId) { this.winningBidderId = winningBidderId; }

    public Date auctionEndTime;
    public final Date getAuctionEndTime() { return auctionEndTime; }


}