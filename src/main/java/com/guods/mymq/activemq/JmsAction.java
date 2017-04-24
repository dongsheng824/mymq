package com.guods.mymq.activemq;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

/**
 * 执行activemq的各种业务操作
 * @author guods
 *
 */
public class JmsAction {

	private ConnectionPool connectionPool;
	
	public JmsAction(ConnectionPool connectionPool) {
		super();
		this.connectionPool = connectionPool;
	}

	/**
	 * 创建消息队列
	 * @param queueName
	 * @return
	 * @throws JMSException
	 */
	public Queue createQueue(final String queueName) throws JMSException {
		
		return (Queue) new JmsTemplate(connectionPool) {
			
			@Override
			public Object action(Session session) {
				try {
					return session.createQueue(queueName);
				} catch (JMSException e) {
					return null;
				}
			}
		}.execute();
	};

	/**
	 * 发送p2p消息
	 * @param queue
	 * @param message
	 * @throws JMSException
	 */
	public void produceP2pMessage(final Queue queue, final String message) throws JMSException{
		new JmsTemplate(connectionPool) {
			
			@Override
			public Object action(Session session) throws JMSException {
				MessageProducer messageProducer = session.createProducer(queue);
				TextMessage textMessage = session.createTextMessage(message);
				messageProducer.send(queue, textMessage);
				return null;
			}
		}.execute();
	}
	
	/**
	 * 消费一条p2p消息
	 * @param queue
	 * @return
	 * @throws JMSException
	 */
	public String consumP2pMessage(final Queue queue) throws JMSException{
		return (String) new JmsTemplate(connectionPool) {
			
			@Override
			public Object action(Session session) throws JMSException {
				MessageConsumer createConsumer = session.createConsumer(queue);
				TextMessage textMessage = (TextMessage) createConsumer.receive(10000);
				if (textMessage == null) {
					return null;
				}
				return textMessage.getText();
			}
		}.execute();
	}
	
	
}
