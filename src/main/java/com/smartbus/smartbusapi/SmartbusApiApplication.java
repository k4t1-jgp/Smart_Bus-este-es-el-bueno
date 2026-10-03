package com.smartbus.smartbusapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal: aquí arranca todo el proyecto.
 *
 * @SpringBootApplication le dice a Spring que busque automáticamente controladores, servicios,
 * repositorios y entidades dentro de este paquete (com.smartbus.smartbusapi) y sus subpaquetes.
 */
@SpringBootApplication
public class SmartbusApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartbusApiApplication.class, args);
    }
}
