package com.trade.message.controller;

import com.trade.message.dto.SecurityId;
import com.trade.message.dto.TradeMessage;
import com.trade.message.service.KafkaMessagePublisher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/producer-app")
public class MessageController {

    @Autowired
    private KafkaMessagePublisher publisher;
    
    @Autowired
	private RestTemplate restTemplate;
    
    private static final String Key = "trade-message";

    @PostMapping("/publish")
    public ResponseEntity<TradeMessage> sendMessages(@RequestBody TradeMessage tradeMessage) {
    	
    	HttpEntity<String> requestEntity =creatSecurityConfig();
        // get all Security related attributes via find call		
        ResponseEntity<SecurityId> responseEntity = restTemplate.exchange("http://localhost:9191/producer-app/find",HttpMethod.POST,
    			requestEntity,SecurityId.class);
    	updateTradeMessageWithSecuirityInfo(tradeMessage, responseEntity);
      //  publisher.sendMessageToTopic(tradeMessage);
    	publisher.sendMessageDefault(Key,tradeMessage);
        //return "message published !";
    	TradeMessage updatedTradeVal = tradeMessage;
    	return new ResponseEntity<TradeMessage>(updatedTradeVal, HttpStatus.OK);
    }

    @PostMapping(path ="/find")
	public ResponseEntity<SecurityId> findSecurity(@RequestBody SecurityId newSecurityId) {
    	SecurityId securityId = newSecurityId;
		return new ResponseEntity<>(securityId, HttpStatus.OK);
	}
    
    
    public HttpEntity<String> creatSecurityConfig(){    	
    	 // Create the request body
        String jsonRequestBody = "{ \"ric\": \"rictest\", \"isin\": \"testisin\", \"cusip\": \"testcusip\",\"sedol\": \"testsedol\" ,\"ticker\": \"testticker\",\"name\": \"testname\"}";
        // Set up the headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // Wrap the request body and headers in HttpEntity
        HttpEntity<String> requestEntity = new HttpEntity<>(jsonRequestBody, headers);    	
        return requestEntity;
    }
    
	private void updateTradeMessageWithSecuirityInfo(TradeMessage tradeMessage,
			ResponseEntity<SecurityId> responseEntity) {
		// Update TradeMessage with additional Security details information via find call
    	// Update the request body elements before sending to Kafka
          if (tradeMessage.getRic() != null) {
        	  tradeMessage.setRic(responseEntity.getBody().getRic());
          }
          if (tradeMessage.getIsin() != null) {
        	  tradeMessage.setIsin(responseEntity.getBody().getIsin());
          }
          if (tradeMessage.getCusip() != null) {
        	  tradeMessage.setCusip(responseEntity.getBody().getCusip());
          }
          if (tradeMessage.getSedol() != null) {
        	  tradeMessage.setSedol(responseEntity.getBody().getSedol());
          }
          if (tradeMessage.getTicker() != null) {
        	  tradeMessage.setTicker(responseEntity.getBody().getTicker());
          }
	}

}
