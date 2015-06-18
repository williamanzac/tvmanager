package com.wing.provider.priatebay.proxy;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class ProxyServiceTest {
	private ProxyService cut;

	@Before
	public void setup() {
		cut = new ProxyService();
	}

	@Test
	public void verifyProxyList() throws Exception {
		final List<String> list = cut.listProxies();
		System.out.println(list);
	}
}
