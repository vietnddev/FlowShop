package com.flowiee.pms.modules.system.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flowiee.pms.modules.system.dto.GeoLocationResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class GeoLocationService {
    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${IPWho.apiKey}")
    private String IPWhoApiKey;

    //@Cacheable(value = "geo-ip", key = "#ip")
    public GeoLocationResult lookup(String ip) {
        // 1️⃣ Try ipwho (primary)
        GeoLocationResult result = callIpWho(ip);
        if (result != null && result.getCountry() != null) return result;

        // 2️⃣ Fallback sang ipapi
        result = callIpApi(ip);
        if (result != null && result.getCountry() != null) return result;

        return new GeoLocationResult();
    }

    private GeoLocationResult callIpWho(String ip) {
        try {
            // 1. Gọi API trả về String
            String url = String.format("https://api.ipwho.org/ip/?ip=%s&apiKey=%s", ip, IPWhoApiKey);
            String json = restTemplate.getForObject(url, String.class);

            System.out.println("RAW RESPONSE = " + json); // debug

            // 2. Parse JSON sang Map
            ObjectMapper mapper = new ObjectMapper();
            Map<?, ?> res = mapper.readValue(json, Map.class);

            // 3. Kiểm tra success
            if (res == null || Boolean.FALSE.equals(res.get("success"))) {
                System.out.println("IPWho returned: " + res);
                return null;
            }

            // 4. Map sang DTO
            GeoLocationResult r = new GeoLocationResult();
            r.setIp(ip);
            r.setCountry((String) res.get("country"));
            r.setCity((String) res.get("city"));
            r.setSource("ipwho");
            return r;

        } catch (Exception e) {
            e.printStackTrace(); // log để debug
            return null;
        }
    }


    private GeoLocationResult callIpApi(String ip) {
        try {
            String url = "https://ipapi.co/{ip}/json/";
            Map<?, ?> res = restTemplate.getForObject(url, Map.class, ip);

            if (res == null || res.get("error") != null) {
                return null;
            }

            GeoLocationResult r = new GeoLocationResult();
            r.setIp(ip);
            r.setCountry((String) res.get("country_name"));
            r.setCity((String) res.get("city"));
            r.setSource("ipapi");
            return r;

        } catch (Exception e) {
            return null;
        }
    }
}