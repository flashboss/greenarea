package it.vige.greenarea.bpm.custom.conf;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ComponentScan(basePackages = { "it.vige.greenarea.bpm.custom.conf" })
@ImportResource({ "classpath:activiti-context.xml" })
public class ApplicationConfiguration {

}
