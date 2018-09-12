package com.pinyougou.page.service.impl;

import java.util.Arrays;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pinyougou.page.service.ItemPageService;

@Component
public class PageListener implements MessageListener {

	@Autowired
	private ItemPageService itemPageService;
	
	@Override
	public void onMessage(Message message) {
		ObjectMessage objectMessage = (ObjectMessage)message;
		try {
			Long[] ids = (Long[]) objectMessage.getObject();
			System.out.println(Arrays.toString(ids));
			for (Long goodsId : ids) {
				itemPageService.genItemHtml(goodsId);
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

}
