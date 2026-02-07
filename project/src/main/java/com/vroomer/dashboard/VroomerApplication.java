package com.vroomer.dashboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import etu.sprint.web.FrontServlet;

@SpringBootApplication
public class VroomerApplication {

	public static void main(String[] args) {
		SpringApplication.run(VroomerApplication.class, args);
	}

	@Bean
	public ServletRegistrationBean<FrontServlet> frontServletRegistration() {
		String[] mappings = {"/home/*", "/home", "/home/", "/reservations/*", "/reservations", "/reservations/"};
		ServletRegistrationBean<FrontServlet> registration = new ServletRegistrationBean<>(new FrontServlet(), mappings);
		registration.setName("FrontServlet");
		registration.setLoadOnStartup(1);
		registration.addInitParameter("controller-package", "com.vroomer.dashboard.controller.login;com.vroomer.dashboard.controller.reservation");
		return registration;
	}
}
