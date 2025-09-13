package com.ut.kranti.event;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/events")
public class EventController {
	   @Autowired
    private EventService eventService;
 
    // Create a new event
    @PostMapping("/create")
    public ResponseEntity<EventDTO> createEvent(@RequestBody EventDTO eventDTO) {
        EventDTO createdEvent = eventService.createEvent(eventDTO);
        return new ResponseEntity<>(createdEvent, HttpStatus.CREATED);
    }

    // Get an event by ID
    @GetMapping("/{id}")
    public ResponseEntity<EventDTO> getEventById(@PathVariable Long id) {
        EventDTO eventDTO = eventService.getEventById(id);
        return new ResponseEntity<>(eventDTO, HttpStatus.OK);
    }
    @GetMapping("/title/{title}")
    public ResponseEntity<List<EventDTO>> getEventByTitle(@PathVariable String title) {
        List<EventDTO> eventDTO = eventService.getEventByTitle(title);
        if (eventDTO != null) {
            return new ResponseEntity<>(eventDTO, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Get all events
    @GetMapping
    public ResponseEntity<List<EventDTO>> getAllEvents() {
        List<EventDTO> events = eventService.getAllEvents();
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<EventDTO>> getEventsNearMe(
        @RequestParam double latitude, 
        @RequestParam double longitude, 
        @RequestParam double radius) {
    	System.out.println("event");
        List<EventDTO> events = eventService.findEventsNearLocation(latitude, longitude, radius);
        System.out.println(events);
        return new ResponseEntity<>(events, HttpStatus.OK);
    }
    // Update an event
    @PutMapping("/{id}")
    public ResponseEntity<EventDTO> updateEvent(@PathVariable Long id, @RequestBody EventDTO updatedEventDTO) {
        EventDTO eventDTO = eventService.updateEvent(id, updatedEventDTO);
        return new ResponseEntity<>(eventDTO, HttpStatus.OK);
    }

    // Delete an event
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Follow an event
    @PostMapping("/{eventId}/follow/{userId}")
    public ResponseEntity<String> followEvent(@PathVariable Long eventId, @PathVariable Long userId) {
        eventService.followEvent(eventId, userId);
        return new ResponseEntity<>("User followed the event successfully", HttpStatus.OK);
    }
    // Unfollow an event
    @PostMapping("/{eventId}/unfollow/{userId}")
    public ResponseEntity<String> unfollowEvent(@PathVariable Long eventId, @PathVariable Long userId) {
        eventService.unfollowEvent(eventId, userId);
        return new ResponseEntity<>("User unfollowed the event successfully.", HttpStatus.OK);
    }

    // Add a post to an event
    @PostMapping("/{eventId}/posts")
    public ResponseEntity<String> addPostToEvent(@PathVariable Long eventId, @PathVariable Long PostId) {
        eventService.addPostToEvent(eventId, PostId);
        return new ResponseEntity<>("Post added to the event successfully.", HttpStatus.CREATED);
    }

/**
    // Get followers of an event
    @GetMapping("/{eventId}/followers")
    public ResponseEntity<Set<Long>> getFollowers(@PathVariable Long eventId) {
        Set<Long> followerIds = eventService.getEventFollowers(eventId);
        return new ResponseEntity<>(followerIds, HttpStatus.OK);
    }
*/
}
