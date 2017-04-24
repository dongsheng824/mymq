package com.guods.mymq.activemq;

import javax.jms.JMSException;
import javax.jms.Queue;

public class JmsConsumer {

	private static ConnectionPool connectionPool = new ConnectionPoolImpl();
	private static JmsAction jmsAction = new JmsAction(connectionPool);
	
	public static void main(String[] args) throws JMSException {
		//创建队列
		Queue queue = jmsAction.createQueue("MyQueue");
		//消费消息
		while (true) {
			String message = jmsAction.consumP2pMessage(queue);
			if (message != null) {
				System.out.println("消费消息：" + message);
			}
		}
	}
}
