package com.ut.kranti.event;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ut.kranti.exception.ResourceNotFoundException;
import com.ut.kranti.user.UserProfile;
import com.ut.kranti.user.UserRepository;
import com.ut.kranti.user.post.Post;
import com.ut.kranti.user.post.PostRepository;
@Service
public class EventService {

	  @Autowired
	    private EventRepository eventRepository;

	    @Autowired
	    private UserRepository userProfileRepository;
	 
	    @Autowired
	    private PostRepository postRepository;
	    public EventDTO createEvent(EventDTO eventDTO) {
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
	    	        location.setSRID(4326);
	    	        event.setLocation(location);

	    	     Event savedEvent = eventRepository.save(event);
	    	     
	    	    
	        return EventMapper.toDto(savedEvent);
	    }

	    public EventDTO getEventById(Long id) {
	        Event event = eventRepository.findById(id)
	                .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + id));
	        return EventMapper.toDto(event);
	    }

	    public List<EventDTO> getAllEvents() {
	        List<Event> events = eventRepository.findAll();
	        return events.stream().map(even->EventMapper.toDto(even)).collect(Collectors.toList());
	    }

	    public EventDTO updateEvent(Long id, EventDTO eventDTO) {
	        Event event = eventRepository.findById(id)
	                .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + id));

	        // Update the event details
	        event.setTitle(eventDTO.getTitle());
	        event.setDescription(eventDTO.getDescription());
	        event.setStartDate(eventDTO.getStartDate());
	        event.setEndDate(eventDTO.getEndDate());
	        Event updatedEvent = eventRepository.save(event);
	        return EventMapper.toDto(updatedEvent);
	    }

	    public void deleteEvent(Long id) {
	        Event event = eventRepository.findById(id)
	                .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + id));
	        eventRepository.delete(event);
	    }

	    public void followEvent(Long eventId, Long userId) {
	        Event event = eventRepository.findById(eventId)
	                .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + eventId));
	        UserProfile user = userProfileRepository.findById(userId)
	                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
	        event.getFollowers().add(user);
	        eventRepository.save(event);
	    }

	    public Set<Long> getEventFollowers(Long eventId) {
	        Event event = eventRepository.findById(eventId)
	                .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + eventId));
	        return event.getFollowers().stream().map(UserProfile::getId).collect(Collectors.toSet());
	    }

	    public void addHostToEvent(Long eventId, Long userId) {
	        Event event = eventRepository.findById(eventId)
	                .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + eventId));
	        UserProfile user = userProfileRepository.findById(userId)
	                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
	        event.getHosts().add(user);
	        eventRepository.save(event);
	    }
	    public Point createPoint(double latitude, double longitude) {
	        // Manually creating a GeometryFactory instance
	        GeometryFactory geometryFactory = new GeometryFactory();
	        Coordinate coordinate = new Coordinate(longitude, latitude);
	        return geometryFactory.createPoint(coordinate);
	    }

		public List<EventDTO> getEventByTitle(String title) {
			// TODO Auto-generated method stub
			return eventRepository.findByTitleContainingIgnoreCase(title).stream().map(even->EventMapper.toDto(even)).collect(Collectors.toList());
			
		}
		 public List<EventDTO> findEventsNearLocation(double latitude, double longitude, double radius) {
			 System.out.println("event2");
		        List<Event> events = eventRepository.findNearbyEvents(latitude, longitude, radius);
		        return events.stream().map(even->EventMapper.toDto(even)).collect(Collectors.toList());}

		public void unfollowEvent(Long eventId, Long userId) {
			// TODO Auto-generated method stub
			  Event event = eventRepository.findById(eventId)
			            .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));
			        UserProfile user = userProfileRepository.findById(userId)
			            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

			        event.getFollowers().remove(user);
			        eventRepository.save(event);
		}

		public void addPostToEvent(Long eventId, Long postId) {
			// TODO Auto-generated method stub
			 // Retrieve the event
		    Event event = eventRepository.findById(eventId)
		        .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));
		    
		    // Retrieve the post
		    Post post = postRepository.findById(postId)
		        .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

		    // Check if the post author is a host of the event
		    Long postAuthorId = post.getAuthor().getId(); // Assuming Post has an `author` field (UserProfile)
		    boolean isAuthorAHost = event.getHosts().stream()
		        .anyMatch(host -> host.getId().equals(postAuthorId));

		    if (!isAuthorAHost) {
		        throw new IllegalArgumentException("Post author is not a host of the event.");
		    }

		    // Associate the post with the event
		    post.setEvent(event);
		    postRepository.save(post);
		}
}
