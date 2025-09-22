package io.romeosarkar10x.learn.springusersrestsinpeace;

import io.romeosarkar10x.learn.springusersrestsinpeace.Config.AppConfig;

import jakarta.servlet.ServletRegistration;
import org.springframework.web.servlet.DispatcherServlet;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

public class Main implements org.springframework.web.WebApplicationInitializer {
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException{
        AnnotationConfigWebApplicationContext annotationConfigWebApplicationContext = new AnnotationConfigWebApplicationContext();
        annotationConfigWebApplicationContext.register(AppConfig.class);

        DispatcherServlet dispatcherServlet = new DispatcherServlet(annotationConfigWebApplicationContext);
        ServletRegistration.Dynamic servletRegistration = servletContext.addServlet("dispatcher",  dispatcherServlet);
        servletRegistration.setLoadOnStartup(1);
        servletRegistration.addMapping("/");

    }
    /*
    public static void main(String[] args) {
        System.out.println("Hello world!");
    }

     */
}