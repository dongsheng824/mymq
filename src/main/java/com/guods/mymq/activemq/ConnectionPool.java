package com.guods.mymq.activemq;

import javax.jms.Connection;
import javax.jms.JMSException;

/**
 * jms连接池接口
 * @author guods
 *
 */
public interface ConnectionPool {

	Connection getConnection() throws JMSException;
	void releaseConnection(Connection connection);
}
