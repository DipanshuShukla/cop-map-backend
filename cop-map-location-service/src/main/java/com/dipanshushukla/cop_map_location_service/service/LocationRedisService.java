package com.dipanshushukla.cop_map_location_service.service;

import com.dipanshushukla.cop_map_location_service.dto.LocationPingDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class LocationRedisService {

    private final RedisTemplate<String, String> redisTemplate;

    public void updateOfficerLocation(LocationPingDTO ping) {
        String geoKey = "thana:" + ping.getThanaId() + ":locations";
        String timeKey = "officer:" + ping.getBadgeNumber() + ":last_seen";

        // 1. Update Map Coordinates (GEOADD)
        redisTemplate.opsForGeo().add(
                geoKey,
                new Point(ping.getLongitude(), ping.getLatitude()),
                ping.getBadgeNumber());

        // 2. Update the MIA Tracker (Store the timestamp)
        redisTemplate.opsForValue().set(
                timeKey,
                ping.getTimestamp().toString());

        // Clear out old data after 12 hours so the map resets after shifts
        redisTemplate.expire(geoKey, 12, TimeUnit.HOURS);
        redisTemplate.expire(timeKey, 12, TimeUnit.HOURS);
    }
}