/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package camelinaction;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.Processor;
import org.apache.camel.util.jndi.JndiContext;

import java.util.ArrayList;
import java.util.function.Predicate;

import javax.jms.ConnectionFactory;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.component.jms.JmsComponent;

public class AuctionHouseRoutes {


	
    public static void main(String args[]) throws Exception {
        // create CamelContext
        CamelContext context = new DefaultCamelContext();

        // connect to ActiveMQ JMS broker listening on localhost on port 61616
        ConnectionFactory connectionFactory = 
        	new ActiveMQConnectionFactory("tcp://localhost:61616");
        context.addComponent("jms",
            JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));
        
        // add our route to the CamelContext
        Server server = Server.getInstance();
    	ArrayList<Client> clientList = new ArrayList<>(0);
    	double auctionHouseRevenue =0;

        context.addRoutes(new RouteBuilder() {
        	/*
        	 * EVENT DRIVEN CONSUMER - route setup to continously listen for new incoming files 
        	 * and respond accordingly when a command gets submitted
        	 */
            public void configure() {
                from("file:data/UserInputs?noop=true")
                .process(new Processor() {
	               	 public void process(Exchange exchange) throws Exception {
	               	  String array = exchange.getIn().getBody(String.class);
	               	  String first = array.split(" ")[0];
	               	  String second = array.split(" ")[1];
	               	  String third = null;
	               	  String total = "";
	               	  if(array.split(" ").length>2){
	               		for(String s : array.split(" ")){
	               			  total = total+" "+s;
	               		  }
	               	  }	               		  
	               	  else{
	               		  total = first+" "+second;
	               	  }
	               	  exchange.getIn().setBody(total.toString());
	               	  exchange.toString();
	               	 }
	               	 
              	})
                /*
                 * CONTENT BASED ROUTER
                 */
                .choice()
                .when(body().contains("add"))
                .to("file:data/addRequests")
                .when(body().contains("select"))
                .to("file:data/selectRequests")
                .when(body().contains("browse"))
                .to("file:data/browseRequests")
                .when(body().contains("sell"))
                .to("file:data/sellRequests")
                .when(body().contains("bid "))
                .to("file:data/bidRequests")
                .otherwise()
                .to("file:data/itemsForSale");
               
                
                from("file:data/addRequests?noop=true")
                .process(new Processor() {
	               	 public void process(Exchange exchange) throws Exception {
	               	  String array = exchange.getIn().getBody(String.class);
	               	  
	               	  String executed = "Created New Client: ";
	               	  String clientNameToAdd = array.split(" ")[1];
	               	  String total = executed+clientNameToAdd;
	               	  exchange.getIn().setBody(total.toString());
	               	  clientList.add(new Client(clientList.size()+1,clientNameToAdd));
	               	 }
	               	 
              	})
                .to("jms:Client_Added");
                
                from("file:data/selectRequests?noop=true")
                .process(new Processor() {
	               	 public void process(Exchange exchange) throws Exception {
	               	  String array = exchange.getIn().getBody(String.class);
	               	  String executed = "selected client: ";
	               	  String clientNameToAdd = array.split(" ")[1];
	               	  String total = executed+clientNameToAdd;
	               	  exchange.getIn().setBody(total.toString());
	               	  clientList.add(new Client(clientList.size()+1,clientNameToAdd));
	               	 }
	               	 
              	})
                .to("file:data/selectProcessed");
                
                /*
                 * RECIPIENT LIST - sends only the items for sale, by 
                 * a particular client, to that client.
                 * Prevents other clients from seeing a request that only one person needs
                 *
                 */
                
                from("file:data/browseRequests?noop=true")
                .process(new Processor() {
                	/*
                	 * Implemented SPLITTERS here and in other sections to parse the files
                	 * containing user commands and respond accordingly.
                	 */
	               	 public void process(Exchange exchange) throws Exception {
	               	  String array = exchange.getIn().getBody(String.class);
	               	  String browseSelected = array.split(" ")[1];
	               	  String total = browseSelected+"\n";
	               	  String recipients = "jms:clients";
	               	  if(browseSelected.equals("ah")){
               			recipients += "jms:ah";
               		  }
	               	  else if (browseSelected.charAt(0)=='a'){
	               		recipients += "jms:Client"+browseSelected;
	               	  }
	               	  for (String s : array.split(" ")){
	               		  total = total +" "+s;	               		  
	               	  }
	               	  exchange.getIn().setHeader("recipients", recipients);
	               	  exchange.getIn().setBody(array.toString());
	               	  } 
	               	 
              	})
                .recipientList(header("recipients"))
                .to("jms:AllItems");
                
                
                from("file:data/sellRequests?noop=true")
                .process(new Processor() {
	               	 public void process(Exchange exchange) throws Exception {
	               	  String array = exchange.getIn().getBody(String.class);
	               	  int sellSelected = Integer.parseInt(array.split(" ")[1]);
	               	  exchange.getIn().setBody("Client "+sellSelected+" selling an item");
	               	  
	               	 }
	               	 
              	})
                .to("jms:SellRequests");
                
                
                from("file:data/bidRequests")
                .process(new Processor() {
	               	 public void process(Exchange exchange) throws Exception {
	               	  String array = exchange.getIn().getBody(String.class);
	               	  String complete = "";
	               	  for(String s : array.split(" ")){
	               		  complete = complete + " " +s;
	               	  }
	               	  exchange.getIn().setBody(complete.toString());
	               	 }
             	})
                /*
                 * MESSAGE FILTER - only track user bids on items that were successful
                 * ie) bids on non expired items that were greater than current bid.
                 */
                .filter(body().contains("true"))
               .to("jms:BidRequests");
                
                
                
                
                
                
                try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

            }
        });

        // start the route and let it do its work
        context.start();
        Thread.sleep(200000000);

        // stop the CamelContext
        context.stop();
    }
}
