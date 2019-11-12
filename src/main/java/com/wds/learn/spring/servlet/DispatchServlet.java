/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2019，所有权利保留。
 * 项目名：  mySpring
 * FileName: DispatchServlet
 * Author:   wangdaisheng
 * Date:     2019/11/8 13:57
 */
package com.wds.learn.spring.servlet;

import com.wds.learn.spring.annotation.Autowired;
import com.wds.learn.spring.annotation.Controller;
import com.wds.learn.spring.annotation.Service;
import com.wds.learn.spring.mvc.action.DemoAction;
import javafx.beans.binding.ObjectExpression;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wangdaisheng
 */
public class DispatchServlet extends HttpServlet {
  private Properties contextConfig = new Properties();

  private Map<String, Object> beanMap = new ConcurrentHashMap<>();

  private List<String> classNames = new ArrayList<String>();

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    this.doPost(req, resp);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    System.out.println("----------- 调用doPost -------------");
  }

  @Override
  public void init(ServletConfig config) throws ServletException {
    //开始初始化过程

    //定位
    doLoadConfig(config.getInitParameter("contextConfigLocation"));
    //加载
    doScanner(contextConfig.getProperty("scanPackage"));
    //注册
    doRegistry();
    //自动依赖注入
    doAutowired();

    DemoAction deemoAction = (DemoAction) beanMap.get("demoAction");
    deemoAction.query(null,null,"wds");

    //将@RequestMapping中配置的url和方法关联
    initHandlerMapper();
  }

  private void initHandlerMapper() {
  }

  private void  doAutowired() {

    if (beanMap.isEmpty()) {
      return;
    }
    for (Map.Entry<String, Object> entry : beanMap.entrySet()) {

      Field[] fields = entry.getValue().getClass().getDeclaredFields();
      for (Field field : fields) {
        if (!field.isAnnotationPresent(Autowired.class)) {
          continue;
        }
        Autowired autowired = field.getAnnotation(Autowired.class);
        String beanName = autowired.value().trim();
        if ("".equals(beanName)) {
          beanName = field.getType().getName();
        }
        field.setAccessible(true);
        try {
          field.set(entry.getValue(), beanMap.get(beanName));
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        }
      }
    }


  }

  private void doRegistry() {
    if (classNames.isEmpty()) {
      return;
    }
    for (String className : classNames) {
      try {
        Class<?> clazz = Class.forName(className);
        //在spring用得是多个子方法处理的
        if (clazz.isAnnotationPresent(Controller.class)) {
          String beanName = lowerFirstCase(clazz.getSimpleName());
          beanMap.put(beanName, clazz.newInstance());

        } else if (clazz.isAnnotationPresent(Service.class)) {
          Service service = clazz.getAnnotation(Service.class);
          String beanName = service.value();
          if ("".equals(beanName.trim())) {
            beanName = lowerFirstCase(clazz.getSimpleName());
          }
          Object instance = clazz.newInstance();
          beanMap.put(beanName, instance);

          Class<?>[] interfaces = clazz.getInterfaces();
          for (Class<?> i : interfaces) {
            beanMap.put(i.getName(), instance);
          }

        } else {
          continue;
        }

      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      } catch (InstantiationException e) {
        e.printStackTrace();
      }
    }


  }

  private void doScanner(String packageName) {
    URL url = this.getClass().getClassLoader().getResource("/" + packageName.replaceAll("\\.", "/"));
    File classDir = new File(url.getFile());
    for (File file : classDir.listFiles()) {
      if (file.isDirectory()) {
        doScanner(packageName + "." + file.getName());
      } else {
        classNames.add(packageName + "." + file.getName().replace(".class", ""));
      }
    }

  }

  private void doLoadConfig(String location) {
    //spring中通过reader拿到
    InputStream is = this.getClass().getClassLoader().getResourceAsStream(location.replace("classpath:", ""));
    try {
      contextConfig.load(is);
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (null != is) {
        try {
          is.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  private String lowerFirstCase(String str) {
    char[] chars = str.toCharArray();
    chars[0] += 32;

    return String.valueOf(chars);


  }
}