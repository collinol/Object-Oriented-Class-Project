package camelinaction;

import javax.swing.filechooser.FileSystemView;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import camelinaction.Client.returnBidPair;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.Buffer;
import java.nio.file.Files;
import java.util.*;
import java.util.Date;

import static java.nio.file.StandardOpenOption.*;

class AuctionHouse{

	private Integer currentClient = -1;
	private ArrayList<Client> clientList = new ArrayList<>(0);
	private double auctionHouseRevenue = 0;
    public Server server;

	public AuctionHouse(){
	    server = Server.getInstance();
	}

	public void start() throws IOException {

		System.out.println("Auction House is open");
        System.out.println("Select Option (--help for available options):");
		Scanner in = new Scanner(System.in);
		String line;
        int userCommandId = 1;
		while (in.hasNextLine()){
            Date currentTime = new Date(System.currentTimeMillis());


			line = in.nextLine();
            Scanner lineScanner = new Scanner(line.trim());

            if (lineScanner.hasNext())
            {
                //File file = new File("~/OOArch/mpcs51050-work/lab5/workspace/MPCS-Lab5-Producer/data/inbox2/Command.txt");

                File file = new File("data/UserInputs","Command#"+userCommandId+".txt");
                PrintWriter output = new PrintWriter(file) ;


                String command = lineScanner.nextLine();
                command = command.toLowerCase();
                String [] args = command.split("\\s+");

                if (command.contains("--help")) {
                    System.out.println("Available Options");
                    System.out.println("add <name>: creates a new client with <name>");
                    System.out.println("select: client id to view client's current wallet");
                    System.out.println("browse: browse either all items on auction, or items for sale by client");
                    System.out.println("sell: sell item -- you will be given the option to select client to sell from if not already selected");
                    System.out.println("bid: bid on item -- you will be given the option to select client to sell from if not already selected ");

                } else if (command.contains("add") && args.length > 1) {

                    output.write(command);
                    output.close();
                    String clientNameToAdd = command.split(" ")[1];
                    clientList.add(new Client(clientList.size()+1,clientNameToAdd));



                } 
                else if (command.contains("add") && args.length <= 1){
                    System.out.println("Please specify the name of client you wish to add");
                    Scanner scanName = new Scanner(System.in);
                    String clientName = scanName.nextLine();
                    clientList.add(new Client(clientList.size()+1,clientName));
                    output.write("add "+clientName);
                    output.close();



                }

                else if (command.contains("select") && args.length > 1) {
                    Integer clientSelect = Integer.parseInt(command.split(" ")[1]);
                    output.write(command);
                    output.close();
                    System.out.println(clientList.get(clientSelect - 1).getName() + " has earned " +
                            clientList.get(clientSelect - 1).getClientWallet() + " from sold items");

                } 
                else if(command.contains("select") && args.length <= 1){
                	System.out.println("Please specify the id of client you wish to select");
                    Scanner scanId = new Scanner(System.in);
                    Integer clientSelect = Integer.parseInt(scanId.next());
                    output.write("select "+clientSelect);
                    output.close();
                    System.out.println(clientList.get(clientSelect - 1).getName() + " has earned " +
                            clientList.get(clientSelect - 1).getClientWallet() + " from sold items");

                }
                
                else if (command.contains("browse") && args.length <= 1) {
                    System.out.println("type 'ah' to view items on Auction House " +
                            "or enter an existing client id to browse");
                    Scanner unselected = new Scanner(System.in);
                    String option3 = unselected.nextLine();
                    if (option3.equals("ah")) {
                    	output.write("browse " + option3);
                        server.listItems();
                        for (ItemTemplate item : server.listItems()){
                        	output.write("\nId "+item.getItemId()+"\nSellerId: "+item.getSellerId()
                        	+"\nItem: "+item.getItemName()
                            +"\nOriginal Price: "+item.getCurrentPrice()+"\nCategory: "
                        			+item.getItemTypeString(item.getItemType())+"\nCurrent bid: "+item.getCurrentPrice()
                        			+"\nAuction Ends:"+item.getAuctionEndTime()+"\n|");
                        }
                        auctionHouseRevenue += server.totalAhPay;
                    } else {
                        Integer clientSelect3 = null;
                        try {
                            clientSelect3 = Integer.parseInt(String.valueOf(option3));
                        } catch (NumberFormatException | NullPointerException e) {
                            System.out.println("invalid option");
                            ;
                        }
                        if (clientSelect3 <= clientList.size()) {
                        	output.write("browse "+clientSelect3 );
                        	for (ItemTemplate item : clientList.get(clientSelect3 - 1).listItemsForSale()){
                            	output.write("\nId "+item.getItemId()+"\nSellerId: "+item.getSellerId()
                            	+"\nItem: "+item.getItemName()
                                +"\nOriginal Price: "+item.getCurrentPrice()+"\nCategory: "
                            			+item.getItemTypeString(item.getItemType())+"\nCurrent bid: "+item.getCurrentPrice()
                            			+"\nAuction Ends:"+item.getAuctionEndTime()+"\n|");
                            }
                            
                        } else {
                            System.out.println("Client id " + clientSelect3 + " doesn't exist");
                        }


                    }
                    
                    output.close();
                    
                } 
                else if (command.contains("browse")&& args.length > 1){
                	String option3 = command.split(" ")[1];
                	output.write(command);
                	output.close();
                	if (option3.equals("ah")) {
                    	output.write("browse " + option3);
                        server.listItems();
                        for (ItemTemplate item : server.listItems()){
                        	output.write("\nId "+item.getItemId()+"\nSellerId: "+item.getSellerId()
                        	+"\nItem: "+item.getItemName()
                            +"\nOriginal Price: "+item.getCurrentPrice()+"\nCategory: "
                        			+item.getItemTypeString(item.getItemType())+"\nCurrent bid: "+item.getCurrentPrice()
                        			+"\nAuction Ends:"+item.getAuctionEndTime()+"\n|");
                        }
                        auctionHouseRevenue += server.totalAhPay;
                    } else {
                        Integer clientSelect3 = null;
                        try {
                            clientSelect3 = Integer.parseInt(String.valueOf(option3));
                        } catch (NumberFormatException | NullPointerException e) {
                            System.out.println("invalid option");
                            ;
                        }
                        if (clientSelect3 <= clientList.size()) {
                        	output.write("browse "+clientSelect3 );
                        	for (ItemTemplate item : clientList.get(clientSelect3 - 1).listItemsForSale()){
                        		output.write("\nId "+item.getItemId()+"\nSellerId: "+item.getSellerId()
                            	+"\nItem: "+item.getItemName()
                                +"\nOriginal Price: "+item.getCurrentPrice()+"\nCategory: "
                            			+item.getItemTypeString(item.getItemType())+"\nCurrent bid: "+item.getCurrentPrice()
                            			+"\nAuction Ends:"+item.getAuctionEndTime()+"\n|");
                            }
                            
                        }else {
                            System.out.println("Client id " + clientSelect3 + " doesn't exist");
                        }
                    }
                }
                
                else if (command.contains("sell") && args.length > 1) {   
                		currentClient = Integer.parseInt(command.split(" ")[1]);
                		clientList.get(currentClient-1).sellItem();
                        output.write(command);
                        output.close();
                } 
                else if (command.contains("sell") && args.length <= 1){
                	System.out.println("Please select client id to sell an item");
                	Scanner sellId = new Scanner(System.in);
                	currentClient = Integer.parseInt(sellId.nextLine());
                	output.write("sell "+currentClient);
                	output.close();
                	clientList.get(currentClient - 1).sellItem();
                    System.out.println("item listed on Auction House");
                }
                else if (command.contains("bid")&& args.length > 1) {
                    currentClient = Integer.parseInt(command.split(" ")[1]);
                    returnBidPair result = clientList.get(currentClient -1).bid();
                    output.write("bid "+ currentClient +" "+ result.getItem()+" "+result.getSucces());
                    
                    output.close();
                    

                } 
                else if(command.contains("bid") && args.length <= 1){
                	Scanner unselected;
                    System.out.println("select client id to  place bid");
                    unselected = new Scanner(System.in);
                    currentClient = Integer.parseInt(unselected.nextLine());
                    returnBidPair result = clientList.get(currentClient - 1).bid();
                    output.write("bid "+ currentClient +" "+ result.getItem()+" "+result.getSucces());
                    
                }
                else if ("AH".equals(command)) {
                    System.out.println("Auction House has revenue of : " + auctionHouseRevenue);
                }

            }
            System.out.println("\nSelect a new option (--help for available options):");
            userCommandId += 1;
		}

	}


}

