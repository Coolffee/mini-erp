package br.com.mini_erp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/teste")
public class testeController {

    @GetMapping
    public String hello() {
        return "Mini ERP funcionando!";
    }
}
