package com.pinyougou.cart.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class LoginController {
	
	@RequestMapping("/user")
	public Map<String,String> name() {
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		System.out.println(name);
		Map<String,String> map = new HashMap<String,String>();
		map.put("loginName", name);
		return map;
	}

}
