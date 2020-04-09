package org.sso.server.controller;

import org.springframework.web.bind.annotation.GetMapping;

@org.springframework.stereotype.Controller
public class Controller {
    
	@GetMapping("/zidingyi")
	public String loginPage(){
        return "login";
	}
}
