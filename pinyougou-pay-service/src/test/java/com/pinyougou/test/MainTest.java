package com.pinyougou.test;

import java.util.Map;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.pinyougou.pay.service.WeixinPayService;

public class MainTest {
	@Test
	public void main() {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
		WeixinPayService weixinPayService = applicationContext.getBean(WeixinPayService.class);
		Map<String, String> map = weixinPayService.createNative("123123124212312", "1");
		System.out.println(map);
	}
}
