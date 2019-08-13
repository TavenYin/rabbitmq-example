package com.github.taven;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SendController {
    @Autowired
    private SendService sendService;

    @PostMapping("/send")
    public void send() {
        sendService.send();
    }

}
