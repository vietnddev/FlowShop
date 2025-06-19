package com.flowiee.pms.common.base.controller;

import com.flowiee.pms.common.utils.CoreUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

@Controller
public class MediaController {
    @GetMapping("favicon.ico")
    @ResponseBody
    public ResponseEntity<Resource> getFavicon() throws IOException {
        ClassPathResource faviconFile = new ClassPathResource("static/dist/favicon/favicon.ico");
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("image/x-icon"))
                .body(faviconFile);
    }

    @GetMapping("/media/default/{type}")
    @ResponseBody
    public ResponseEntity<Resource> getDefaultMedia(@PathVariable("type") String pType) throws IOException {
        String lvType = CoreUtils.trim(pType);
        String lvResourcePath = switch(lvType) {
            case "product" -> "static/dist/img/product-default-image.png";
            case "user" -> "static/dist/img/user-default-avatar.png";
            default -> throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Media type not found");
        };

        Resource lvResource = new ClassPathResource(lvResourcePath);
        if (!lvResource.exists()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Default image not found");
        }

        String lvMimeType = Files.probeContentType(Paths.get(lvResource.getURI()));

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(lvMimeType))
                .cacheControl(CacheControl.maxAge(7, TimeUnit.DAYS))
                .body(lvResource);
    }
}