package com.cdos.corescheduler.eventhandlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.*;

import com.cdos.corescheduler.Scheduler;
import com.cdos.corescheduler.projection.*;
import com.cdos.eventschemas.ConfirmedMeetingEvent;

@Component
public class EventHandler {

    private static final Logger logger = LoggerFactory.getLogger(EventHandler.class);
    private MeetingInviteRepository meetingInviteRepository;
    private UserRepository userRepository;
    private UserMeetingInviteRepository userMeetingInviteRepository;
    private Scheduler scheduler;

    @Autowired
    public EventHandler(MeetingInviteRepository meetingInviteRepository, UserRepository userRepository, UserMeetingInviteRepository userMeetingInviteRepository, Scheduler scheduler) {
        this.meetingInviteRepository = meetingInviteRepository;
        this.userRepository = userRepository;
        this.userMeetingInviteRepository = userMeetingInviteRepository;
        this.scheduler = scheduler;
    }

    @KafkaListener(topics="confirmedmeeting", groupId = "coreschedulerconsumer", containerFactory = "kafkaListenerContainerFactory")
    public void meetingInviteEvent(ConfirmedMeetingEvent confirmedMeetingEvent) {

        logger.info("CoreScheduler ConfirmedMeetingEvent handler processing event: " +
                    "for meeting ID: " + confirmedMeetingEvent.getMeetingId());

        // make event handler idempotent. If meeting already exists, do nothing
        Optional<MeetingInvite> meetingInvite = meetingInviteRepository.findById(confirmedMeetingEvent.getMeetingId());
        if (meetingInvite.isPresent()) {
            return;
        }

        List<String> attendeeIds = new ArrayList<>();
        for (String attendeeId : confirmedMeetingEvent.getAttendeeIds()) {
            Optional<User> user = userRepository.findById(attendeeId);
            if (!user.isPresent()) {
                logger.info("User not found : " + attendeeId);
                continue;
            }

            if (user.get().getCommuteMode().contentEquals("cab")) {
                attendeeIds.add(attendeeId);
                userMeetingInviteRepository.save(new UserMeetingInvite(attendeeId, confirmedMeetingEvent.getMeetingId()));
            }

        }

        if (attendeeIds.isEmpty()) {
            logger.info("Not scheduling meeting: " + confirmedMeetingEvent.getMeetingId() +
                        " since none of the attendees have opted for company cab.");
            return;
        }

        logger.info("Scheduling meeting: " + confirmedMeetingEvent.getMeetingId());

        meetingInviteRepository.save(
            new MeetingInvite(
                confirmedMeetingEvent.getMeetingId(),
                confirmedMeetingEvent.getSubject()));

        scheduler.scheduleMeeting(
            confirmedMeetingEvent.getMeetingId(),
            attendeeIds,
            confirmedMeetingEvent.getDaysPerWeek(),
            confirmedMeetingEvent.getHoursPerDay());
    }
 
}
