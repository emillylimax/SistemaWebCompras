package com.avaliacao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class ProjWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjWebApplication.class, args);
    }

}
