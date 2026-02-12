package com.vroomer.dashboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import etu.sprint.web.FrontServlet;

@SpringBootApplication
@ComponentScan({
	"com.vroomer.dashboard.controller",
	"com.vroomer.dashboard.service",
	"com.vroomer.dashboard.repository"
})
public class VroomerApplication {

	public static void main(String[] args) {
		SpringApplication.run(VroomerApplication.class, args);
	}

	@Bean
	public ServletRegistrationBean<FrontServlet> frontServletRegistration() {
		String[] mappings = {"/home/*","/reservations/*","/vehicules/*","/carburants/*","/api/*"};
		ServletRegistrationBean<FrontServlet> registration = new ServletRegistrationBean<>(new FrontServlet(), mappings);
		registration.setName("FrontServlet");
		registration.setLoadOnStartup(1);
		registration.addInitParameter("controller-package", "com.vroomer.dashboard.controller.login;com.vroomer.dashboard.controller.reservation;com.vroomer.dashboard.controller.vehicule;com.vroomer.dashboard.controller.carburant");
		return registration;
	}
}
