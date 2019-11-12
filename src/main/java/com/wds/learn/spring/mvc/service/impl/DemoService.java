package com.wds.learn.spring.mvc.service.impl;


import com.wds.learn.spring.annotation.Service;
import com.wds.learn.spring.mvc.service.IDemoService;

/**
 * 核心业务逻辑
 */
@Service
public class DemoService implements IDemoService {

	public String get(String name) {
		return "My name is " + name;
	}

}
