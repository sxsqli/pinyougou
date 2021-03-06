package com.pinyougou.cart.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbPayLog;

import entity.Result;

@RestController
@RequestMapping("/pay")
public class PayController {
	@Reference
	private  WeixinPayService weixinPayService;
	
	@Reference
	private OrderService orderService;

	/**
	 * 生成二维码
	 * @return
	 */
	@RequestMapping("/createNative")
	public Map<String, String> createNative(){
		//获取当前用户		
		String userId=SecurityContextHolder.getContext().getAuthentication().getName();
		//到redis查询支付日志
		TbPayLog payLog = orderService.searchPayLogFromRedis(userId);

		//判断支付日志存在
		if(payLog!=null){
			return weixinPayService.createNative(payLog.getOutTradeNo(),payLog.getTotalFee()+"");
		}else{
			return new HashMap<String,String>();
		}		
	}
	
	/**
	 * 查询支付状态
	 * @param out_trade_no
	 * @return
	 */
	@RequestMapping("/queryPayStatus")
	public Result queryPayStatus(String out_trade_no){
		Result result=new  Result(false, "二维码超时");	
		for(int i = 0;i<100;i++){
			//调用查询接口
			Map<String,String> map = weixinPayService.queryPayStatus(out_trade_no);
			if(map==null){//出错			
				result=new  Result(false, "支付出错");
				break;
			}			
			if(map.get("trade_state").equals("SUCCESS")){//如果成功				
				result=new  Result(true, "支付成功");
				//修改订单状态
				orderService.updateOrderStatus(out_trade_no, map.get("transaction_id"));
				break;
			}			
			try {
				Thread.sleep(3000);//间隔三秒
			} catch (InterruptedException e) {
				e.printStackTrace();
			}							
		}
		return result;
	}
}
