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

import java.util.ArrayList;

import javax.jms.ConnectionFactory;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.component.jms.JmsComponent;

public class RouteReader {

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

        context.addRoutes(new RouteBuilder() {
            public void configure() {
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
                .to("file:data/addProcessed");
                
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
                
                try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

                //from("file:data/outbox2").to("jms:MPCS51050_config_test");
            }
        });

        // start the route and let it do its work
        context.start();
        Thread.sleep(2000000);

        // stop the CamelContext
        context.stop();
    }
}
