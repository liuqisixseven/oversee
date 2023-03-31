package com.chd.modules.message.handle.impl;

import lombok.extern.slf4j.Slf4j;
import com.chd.modules.message.handle.ISendMsgHandle;

/**
 * @Description: 发微信消息模板
 * 
 */
@Slf4j
public class WxSendMsgHandle implements ISendMsgHandle {

	@Override
	public void sendMsg(String esReceiver, String esTitle, String esContent) {
		// TODO Auto-generated method stub
		log.info("发微信消息模板");
	}

}
