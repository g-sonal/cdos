package com.cdos.confirminvite.eventhandlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

import com.cdos.confirminvite.projection.*;
import com.cdos.eventschemas.MeetingInviteEvent;

@Component
public class EventHandler {

    private static final Logger logger = LoggerFactory.getLogger(EventHandler.class);
    private MeetingInviteRepository meetingInviteRepository;
    private UserMeetingInviteRepository userMeetingInviteRepository;

    @Autowired
    public EventHandler(MeetingInviteRepository meetingInviteRepository, UserMeetingInviteRepository userMeetingInviteRepository) {
        this.meetingInviteRepository = meetingInviteRepository;
        this.userMeetingInviteRepository = userMeetingInviteRepository;
    }

    @KafkaListener(topics="meetingrequest", groupId = "confirminviteconsumer", containerFactory = "kafkaListenerContainerFactory")
    public void meetingInviteEvent(MeetingInviteEvent meetingInviteEvent) {

        logger.info("ConfirmInvite MeetingInviteEvent handler processing - event: " + meetingInviteEvent.getEventType() + 
                    " for meeting ID: " + meetingInviteEvent.getId());

        if (meetingInviteEvent.getEventType().equals("created")) {

            // make event handler idempotent. If meeting already exists, do nothing
            Optional<MeetingInvite> meetingInvite = meetingInviteRepository.findById(meetingInviteEvent.getId());

            if (meetingInvite.isPresent()) {
                return;
            }

            logger.info("Saving MeetingInviteEvent for ConfirmInvite svc.");
            
            meetingInviteRepository.save(
                new MeetingInvite(
                    meetingInviteEvent.getId(),
                    meetingInviteEvent.getOrganizerId(),
                    meetingInviteEvent.getAttendeeIds().size(),
                    meetingInviteEvent.getSubject(),
                    meetingInviteEvent.getDaysPerWeek(),
                    meetingInviteEvent.getHoursPerDay()));

            for (String attendeeId : meetingInviteEvent.getAttendeeIds()) {
                userMeetingInviteRepository.save(new UserMeetingInvite(attendeeId, meetingInviteEvent.getId()));
            }
        }
    }
 
}
