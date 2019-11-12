package com.wds.learn.spring.mvc.action;

import com.wds.learn.spring.annotation.Autowired;
import com.wds.learn.spring.annotation.Controller;
import com.wds.learn.spring.annotation.RequestMapping;
import com.wds.learn.spring.annotation.RequestParam;
import com.wds.learn.spring.mvc.service.IDemoService;
import com.wds.learn.spring.mvc.service.IDemoService1;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Controller
@RequestMapping("/demo")
public class DemoAction {

  @Autowired
  private IDemoService demoService;

  @Autowired
  private IDemoService1 demoService1;


  @RequestMapping("/query")
  public void query(HttpServletRequest req, HttpServletResponse resp,
                    @RequestParam("name") String name) {
    String result = demoService.get(name);
    System.out.println(result);
//		String result = "My name is " + name;

//    try {
//      resp.getWriter().write(result);
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
  }

  @RequestMapping("/add")
  public void add(HttpServletRequest req, HttpServletResponse resp,
                  @RequestParam("a") Integer a, @RequestParam("b") Integer b) {
    try {
      resp.getWriter().write(a + "+" + b + "=" + (a + b));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @RequestMapping("/remove")
  public void remove(HttpServletRequest req, HttpServletResponse resp,
                     @RequestParam("id") Integer id) {
  }

}
