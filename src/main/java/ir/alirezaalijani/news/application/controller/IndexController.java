package ir.alirezaalijani.news.application.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping
public class IndexController {

    @Value("${spring.application.name}")
    private String applicationName;

    @GetMapping
    public String index() {
        return "index";
    }

    @ResponseBody
    @GetMapping(value = "index/health",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> indexHealth(){
        System.out.println(applicationName);
        return ResponseEntity.ok("{\"status\":\"UP\"}");
    }

}
