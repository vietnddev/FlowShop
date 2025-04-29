package com.flowiee.pms.common.base.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

@Controller
public class FaviconController {
    @GetMapping("favicon.ico")
    @ResponseBody
    public ResponseEntity<Resource> getFavicon() throws IOException {
        ClassPathResource faviconFile = new ClassPathResource("static/dist/favicon/favicon.ico");
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("image/x-icon"))
                .body(faviconFile);
    }
}