package com.guods.mymq.activemq;

import javax.jms.JMSException;
import javax.jms.Queue;

public class JmsProducer {

	private static ConnectionPool connectionPool = new ConnectionPoolImpl();
	private static JmsAction jmsAction = new JmsAction(connectionPool);
	
	public static void main(String[] args) throws JMSException {
		//创建队列
		Queue queue = jmsAction.createQueue("MyQueue");
		//生产消息
		for (int i = 0; i < 5; i++) {
			String message = "第" + i + "条消息！";
			jmsAction.produceP2pMessage(queue, message);
			System.out.println("生产消息：" + message);
		}
	}
}
