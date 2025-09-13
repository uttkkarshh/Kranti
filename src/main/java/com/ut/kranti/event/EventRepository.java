package com.ut.kranti.event;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ut.kranti.comments.CommentDto;
import com.ut.kranti.user.UserProfile;
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
	List<Event> findByTitleContainingIgnoreCase(String name);
	@Query(value = """
		    SELECT * FROM event e
		    WHERE ST_Distance_Sphere(
		            e.location,
		            ST_GeomFromText(CONCAT('POINT(', :longitude, ' ', :latitude, ')'), 4326)
		          ) <= :radius
		    """, nativeQuery = true)
		List<Event> findNearbyEvents(@Param("latitude") double latitude, 
		                             @Param("longitude") double longitude, 
		                             @Param("radius") double radius);

	   
}
