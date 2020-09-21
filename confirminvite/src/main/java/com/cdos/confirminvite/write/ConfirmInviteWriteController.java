package com.cdos.confirminvite.write;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import com.cdos.confirminvite.projection.*;
import com.cdos.eventschemas.ConfirmedMeetingEvent;

@RestController
public class ConfirmInviteWriteController {

    private static final Logger logger = LoggerFactory.getLogger(ConfirmInviteWriteController.class);
	private MeetingInviteRepository meetingInviteRepository;
	private UserMeetingInviteRepository userMeetingInviteRepository;

	@Autowired
	private KafkaTemplate<String, Object> kafkaTemplate;

	@Autowired
	ConfirmInviteWriteController(MeetingInviteRepository meetingInviteRepository, UserMeetingInviteRepository userMeetingInviteRepository) {
		this.meetingInviteRepository = meetingInviteRepository;
		this.userMeetingInviteRepository = userMeetingInviteRepository;
	}
 
	@PostMapping("/meetingrequests/{userId}/received/{meetingId}/accept")
	public void acceptInvite(@PathVariable(value = "userId") String userId, @PathVariable(value = "meetingId") long meetingId, HttpServletResponse response) {
		handleMeetingInviteResponse(userId, meetingId, "accepted");
	}
 
	/*@PostMapping("/meetingrequests/{userId}/received/{meetingId}/accept")
	public String acceptInvite(@PathVariable(value = "userId") String userId, @PathVariable(value = "meetingId") long meetingId, HttpServletResponse response) {
		meetingInviteRepository.save(new MeetingInvite(meetingId, "organizerId", 1, "subject", 2, 1));
		userMeetingInviteRepository.save(new UserMeetingInvite(userId, meetingId));
		handleMeetingInviteResponse(userId, meetingId, "accepted");
		return String.valueOf(userMeetingInviteRepository.findAttendees(meetingId, "accepted").size());
	}*/

	// Decline invitation
	@PostMapping("/meetingrequests/{userId}/received/{meetingId}/decline")
	public void declineInvite(@PathVariable(value = "userId") String userId, @PathVariable(value = "meetingId") long meetingId, HttpServletResponse response) {
		handleMeetingInviteResponse(userId, meetingId, "declined");
	}

	private void handleMeetingInviteResponse(String userId, long meetingId, String response) {
		Optional<UserMeetingInvite> userMeetingInvite = userMeetingInviteRepository.findByUserIdAndMeetingId(userId, meetingId);
		if (!userMeetingInvite.isPresent()) {
			logger.info("Can not process meeting ID: " + meetingId + " for User ID: " + userId + " : No such record found.");
			return;
		}

		Optional<MeetingInvite> meetingInvite = meetingInviteRepository.findById(meetingId);
		if (!meetingInvite.isPresent()) {
			logger.info("Can not process meeting ID " + meetingId + " : No such meeting found.");
			return;
		}

		logger.info("Updating response of user: " + userId + "for meeting: " + meetingId + " as " + response);
		userMeetingInviteRepository.updateResponse(userId, meetingId, response);
		meetingInviteRepository.incrementResponseCount(meetingId);

		if (!meetingInviteRepository.haveAllInviteesResponded(meetingId)) {
			return;
		}
		
		List<String> attendees = userMeetingInviteRepository.findAttendees(meetingId, response);
		if (attendees.isEmpty()) {
			logger.info("Meeting: " + meetingId + " not accepted by any invitee.");
			return;
		}
		attendees.add(meetingInvite.get().getOrganizerId());

		logger.info("Sending ConfirmedMeetingEvent - Meeting ID: " + meetingId);

		ConfirmedMeetingEvent event = new ConfirmedMeetingEvent(
			meetingId,
			meetingInvite.get().getSubject(),
			attendees,
			meetingInvite.get().getDaysPerWeek(),
			meetingInvite.get().getHoursPerDay());
		
        kafkaTemplate.send("confirmedmeeting", event);
	}
	
}
