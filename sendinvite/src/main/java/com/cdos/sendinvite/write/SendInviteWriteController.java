package com.cdos.sendinvite.write;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

import com.cdos.eventschemas.MeetingInviteEvent;
import com.cdos.sendinvite.MeetingInviteApi;
import com.cdos.sendinvite.projection.MeetingInviteRepository;

@RestController
public class SendInviteWriteController {

    private static final Logger logger = LoggerFactory.getLogger(SendInviteWriteController.class);
	private MeetingInviteRepository meetingInviteRepository;

	@Autowired
	private KafkaTemplate<String, Object> kafkaTemplate;

	@Autowired
	SendInviteWriteController(MeetingInviteRepository meetingInviteRepository) {
	    this.meetingInviteRepository = meetingInviteRepository;
	}

	@PostMapping("/meetingrequests/send")
	public void newInvite(@RequestBody MeetingInviteApi meetingInvite, HttpServletResponse response) {
		
		// Move the ID generation out of the database - to assign an ID when the event is produced, not when the projection is created.
		// Move to IdManager class for synchronization across servers
		Long id = meetingInviteRepository.getMaxId();
		id = (id == null) ? 0 : id + 1;
	
		logger.info("Sending MeetingInviteEvent - Meeting ID: " + id + ", Organizer: " + meetingInvite.getOrganizerId());

		MeetingInviteEvent event = new MeetingInviteEvent(
			"created",
			id,
			meetingInvite.getOrganizerId(),
			meetingInvite.getAttendeeIds(),
			meetingInvite.getSubject(),
			meetingInvite.getDaysPerWeek(),
			meetingInvite.getHoursPerDay());
		
        kafkaTemplate.send("meetingrequest", event);
	}

	// Cancel invitation
	// @DeleteMapping("/meetingrequests/{meetingid})
	
	// Add participant
	// @PutMapping("/meetingrequests/{meetingid}")
	
	// Remove participant
	// //@PutMapping("/meetingrequests/{meetingid}")
}
