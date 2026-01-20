//package com.trade.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.trade.extra.ApiResponse;
//import com.trade.extra.FunctionResponse;
//import com.trade.extra.PropmpBody;
//import com.trade.service.ChatbotService;
//import com.trade.service.ChatbotServiceImpl;
//
//@RestController
//@RequestMapping("/chatbot")
//public class ChatBotController {
//	
//	private ChatbotServiceImpl chatbotServiceImpl;
//	private ChatbotService chatbotService;
//	
//	@Autowired
//	public ChatBotController(ChatbotServiceImpl chatbotServiceImpl, ChatbotService chatbotService) {
//		super();
//		this.chatbotServiceImpl = chatbotServiceImpl;
//		this.chatbotService = chatbotService;
//	}
//
//	@PostMapping("/details")
//	public ResponseEntity<ApiResponse> getCoinDetails(@RequestBody PropmpBody prompt){
//	    return ResponseEntity.ok(
//	        chatbotService.getCoinDetails(prompt.getPrompt())
//	    );
//	}
//
//
//	
//	@PostMapping("/simple")
//	public ResponseEntity<String> simpleChatHandler(@RequestBody PropmpBody body){
//	    String response = chatbotService.simpleChat(body.getPrompt());
//	    
//	    return new ResponseEntity<String>(response,HttpStatus.OK);
//	}
//	
//	@PostMapping("/stock")
//	public ResponseEntity<FunctionResponse> stockChatHandler(@RequestBody PropmpBody body){
//	    return ResponseEntity.ok(chatbotServiceImpl.getFunctionResponse(body.getPrompt()));
//	}
//
//	@PostMapping("/ask")
//	public ResponseEntity<ApiResponse> ask(@RequestBody PropmpBody body){
//	    return ResponseEntity.ok(chatbotServiceImpl.smartAsk(body.getPrompt()));
//	}
//
//
//}






package com.trade.controller;



import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.trade.extra.ApiResponse;
import com.trade.service.ChatbotService;

@RestController
@RequestMapping("/chatbot")
public class ChatBotController {

    private ChatbotService chatbotService;
    

    public ChatBotController(ChatbotService chatbotService) {
		super();
		this.chatbotService = chatbotService;
	}


	@PostMapping("/ask")
    public ResponseEntity<ApiResponse> ask(@RequestBody Map<String,String> body){
        String prompt = body.get("prompt");
        return ResponseEntity.ok(chatbotService.ask(prompt));
    }
}
