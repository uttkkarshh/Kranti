package com.ut.kranti.event;

import java.util.Set;
import java.util.stream.Collectors;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import com.ut.kranti.user.UserProfile;

public class EventMapper {

    // Entity to DTO
    public static EventDTO toDto(Event event) {
        EventDTO dto = new EventDTO();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        dto.setStartDate(event.getStartDate());
        dto.setEndDate(event.getEndDate());
        dto.setHostIds(
            event.getHosts() != null
                ? event.getHosts().stream().map(UserProfile::getId).collect(Collectors.toSet())
                : null
        );
        dto.setFollowerIds(
            event.getFollowers() != null
                ? event.getFollowers().stream().map(UserProfile::getId).collect(Collectors.toSet())
                : null
        );
        Point location =event.getLocation();
        dto.setLongitude(location.getX()); // X corresponds to longitude
        dto.setLatitude(location.getY()); 
        return dto;
    }

    // DTO to Entity
    public static Event toEntity(EventDTO dto, Set<UserProfile> hosts, Set<UserProfile> followers) {
        Event event = new Event();
        event.setId(dto.getId());
        event.setTitle(dto.getTitle());
        event.setDescription(dto.getDescription());
        event.setStartDate(dto.getStartDate());
        event.setEndDate(dto.getEndDate());
        event.setHosts(hosts);
        event.setFollowers(followers);
        return event;
    }
    public static Event convertToEntity(EventDTO eventDTO) {
        Event event = new Event();
        event.setTitle(eventDTO.getTitle());
        event.setDescription(eventDTO.getDescription());
        event.setStartDate(eventDTO.getStartDate());
        event.setEndDate(eventDTO.getEndDate());
     // Create Point from latitude and longitude
        Point location = createPoint(
            eventDTO.getLongitude(),
            eventDTO.getLatitude()
        );
        event.setLocation(location);
        return event;
    }
    public static Point createPoint(double latitude, double longitude) {
        // Manually creating a GeometryFactory instance
        GeometryFactory geometryFactory = new GeometryFactory();
        Coordinate coordinate = new Coordinate(longitude, latitude);
        
        return geometryFactory.createPoint(coordinate);
    }
}