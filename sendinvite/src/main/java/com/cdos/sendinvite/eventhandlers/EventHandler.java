package com.cdos.sendinvite.eventhandlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

import com.cdos.sendinvite.projection.MeetingInvite;
import com.cdos.sendinvite.projection.MeetingInviteRepository;
import com.cdos.eventschemas.MeetingInviteEvent;

@Component
public class EventHandler {

    private static final Logger logger = LoggerFactory.getLogger(EventHandler.class);
    private MeetingInviteRepository meetingInviteRepository;

    @Autowired
    public EventHandler(MeetingInviteRepository meetingInviteRepository) {
        this.meetingInviteRepository = meetingInviteRepository;
    }

    @KafkaListener(topics="meetingrequest", groupId = "sendinviteconsumer", containerFactory = "kafkaListenerContainerFactory")
    public void meetingInviteEvent(MeetingInviteEvent meetingInviteEvent) {

        logger.info("SendInvite MeetingInviteEvent handler processing - event: " + meetingInviteEvent.getEventType() + 
                    "for meeting ID: " + meetingInviteEvent.getId());

        if (meetingInviteEvent.getEventType().equals("created")) {

            // make event handler idempotent. If meeting already exists, do nothing
            Optional<MeetingInvite> meetingInvite = meetingInviteRepository.findById(meetingInviteEvent.getId());
            if (!meetingInvite.isPresent()) {

                logger.info("Saving MeetingInviteEvent for SendInvite svc.");

                meetingInviteRepository.save(
                    new MeetingInvite(
                        meetingInviteEvent.getId(),
                        meetingInviteEvent.getOrganizerId(),
                        meetingInviteEvent.getSubject()));
            }
        }
    }
 
 }
