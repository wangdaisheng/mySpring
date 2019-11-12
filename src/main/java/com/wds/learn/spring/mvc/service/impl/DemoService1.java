package com.wds.learn.spring.mvc.service.impl;


import com.wds.learn.spring.annotation.Service;
import com.wds.learn.spring.mvc.service.IDemoService;
import com.wds.learn.spring.mvc.service.IDemoService1;

/**
 * 核心业务逻辑
 */
@Service
public class DemoService1 implements IDemoService1 {

	public String get(String name) {
		return "My name is " + name;
	}

}
