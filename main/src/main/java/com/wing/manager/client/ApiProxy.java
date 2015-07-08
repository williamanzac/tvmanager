package com.wing.manager.client;

import com.eclipsesource.jaxrs.consumer.ConsumerFactory;

public final class ApiProxy {

	public static <T> T create(Class<T> apiClass, String baseUrl) {
		T consumer = ConsumerFactory.createConsumer(baseUrl, apiClass);
		return consumer;
	}
}
