package com.cdos.corescheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import com.cdos.corescheduler.projection.*;

@Component
public class Scheduler {

    private final int MaxSlotsPerDay = 6;
    private final int MaxSlotsPerWeek = 30;
    private final int DaysPerWeek = 5;

    private final HashMap<Integer, DayOfWeek> SlotToDayMap = new HashMap<Integer, DayOfWeek>() {{
        put(0, DayOfWeek.MONDAY);
        put(1, DayOfWeek.TUESDAY);
        put(2, DayOfWeek.WEDNESDAY);
        put(3, DayOfWeek.THURSDAY);
        put(4, DayOfWeek.FRIDAY);
    }};

    // Slot 0 = 9 AM to 10 AM
    // Slot 1 = 10 AM to 11 AM
    // Slot 2 = 11 AM to 12 PM
    // Slot 3 = 1 PM to 2 PM
    // Slot 4 = 2 PM to 3 PM
    // Slot 5 = 3 PM to 4 PM
    private final HashMap<Integer, Integer> SlotToHourMap = new HashMap<Integer, Integer>() {{
        put(0, 9);
        put(1, 10);
        put(2, 11);
        put(3, 13);
        put(4, 14);
        put(5, 15);
    }};

    private UserRepository userRepository;
	private AreaCabRepository areaCabRepository;
    private MeetingScheduleRepository meetingScheduleRepository;
    private static final Logger logger = LoggerFactory.getLogger(Scheduler.class);

    List<String> attendeeIds;
    int daysPerWeek, hoursPerDay;
    HashSet<Integer> freeSlotsInWeek;
    HashMap<Integer, Integer> numFreeSlotsPerDay;
    HashMap<Integer, List<Integer>> freeSlotsPerDay;
    HashMap<String, User> users;

    @Autowired
    public Scheduler(UserRepository userRepository, AreaCabRepository areaCabRepository, MeetingScheduleRepository meetingScheduleRepository) {
        this.userRepository = userRepository;
		this.areaCabRepository = areaCabRepository;
        this.meetingScheduleRepository = meetingScheduleRepository;
    }

    public void scheduleMeeting(long meetingId, List<String> attendeeIds, int daysPerWeek, int hoursPerDay) {
        this.attendeeIds = attendeeIds;
        this.daysPerWeek = daysPerWeek;
        this.hoursPerDay = hoursPerDay;
        freeSlotsInWeek = new HashSet<>();
        numFreeSlotsPerDay = new HashMap<>();
        freeSlotsPerDay = new HashMap<>();
        users = new HashMap<>();
        for (String attendeeId : attendeeIds) {
            users.put(attendeeId, userRepository.findById(attendeeId).get());
        }

        setFreeSlotsInWeek();
        setFreeSlotsPerDay();

        if (!canScheduleMeeting()) {
            logger.info("Cannot schedule meeting: " + meetingId + "as required number of slots not found.");
            return;
        }

        int[] optimalSlots = getOptimalSlots();
        updateUserSlots(optimalSlots);
        updateCabSeats(optimalSlots);
        updateSchedule(meetingId, optimalSlots);

        logger.info("Scheduled Meeting: " + meetingId);
    }

    // Set slots for the week when all attendees are free
    private void setFreeSlotsInWeek() {
        int numFreeAttendeesPerSlot[] = new int[MaxSlotsPerWeek];
        for (String attendeeId : attendeeIds) {
            String userSlots = users.get(attendeeId).getSlots();

            for (int slot = 0; slot < MaxSlotsPerWeek; ++slot) {
                numFreeAttendeesPerSlot[slot] += (userSlots.charAt(slot) == '0') ? 1 : 0;
            }
        }

        for (int slot = 0; slot < MaxSlotsPerWeek; ++slot) {
            if (numFreeAttendeesPerSlot[slot] == attendeeIds.size()) {
                freeSlotsInWeek.add(slot);
            }
        }
    }

    private void setFreeSlotsPerDay() {
        for (int slot : freeSlotsInWeek) {
            numFreeSlotsPerDay.put(slot / MaxSlotsPerDay, 1 + numFreeSlotsPerDay.getOrDefault(slot / MaxSlotsPerDay, 0));

            List<Integer> freeSlotsOfDay = freeSlotsPerDay.getOrDefault(slot / MaxSlotsPerDay, new ArrayList<>());
            freeSlotsOfDay.add(slot);
            freeSlotsPerDay.put(slot / MaxSlotsPerDay, freeSlotsOfDay);
        }
    }

    private boolean canScheduleMeeting() {

        // Number of days that have any number of free slots
        if (numFreeSlotsPerDay.size() < daysPerWeek) {
            return false;
        }

        // Number of days that have number of free slots >= hoursPerDay
        int availableDays = 0;
        for (int slots : numFreeSlotsPerDay.values()) {
            if (slots >= hoursPerDay) {
                ++availableDays;
            }
        }

        return availableDays >= daysPerWeek;
    }

    private int[] getOptimalSlots() {
        int[] optimalDays = getOptimalDays();

        logger.info("Optimal Slots:");

        int optimalSlots[] = new int[daysPerWeek * hoursPerDay];
        for (int day = 0; day < daysPerWeek; ++day) {

            List<Integer> availableSlots = freeSlotsPerDay.get(optimalDays[day]);
            for (int slot = 0; slot < hoursPerDay; ++slot) {
                optimalSlots[day * hoursPerDay + slot] = availableSlots.get(slot);
                    
                logger.info(String.valueOf(optimalSlots[day * hoursPerDay + slot]));
            }
        }

        return optimalSlots;
    }

    private int[] getOptimalDays() {
        HashMap<Integer, Integer> cabScores = getCabScores();

        PriorityQueue<Map.Entry<Integer, Integer>> pq = new PriorityQueue<>((a,b) -> {
            if (b.getKey() == a.getKey()) {
                return cabScores.get(b.getValue()) - cabScores.get(a.getValue());
            }

            return b.getKey() - a.getKey();
        });

        for (Map.Entry<Integer, Integer> numFreeSlotsPerDayEntry : numFreeSlotsPerDay.entrySet()) {
            pq.offer(new AbstractMap.SimpleEntry<>(numFreeSlotsPerDayEntry.getValue(), numFreeSlotsPerDayEntry.getKey()));
        }

        logger.info("Optimal Days:");

        int[] optimalDays = new int[daysPerWeek];
        for (int dayIndex = 0; dayIndex < daysPerWeek; ++dayIndex) {
            optimalDays[dayIndex] = pq.poll().getValue();
            logger.info(String.valueOf(optimalDays[dayIndex]));
        }

        return optimalDays;
    }

    private HashMap<Integer, Integer> getCabScores() {
        
        HashMap<Integer, Integer> areaAttendees = new HashMap<>();
        for (String attendee : attendeeIds) {
            int areaCode = users.get(attendee).getAreaCode();
            areaAttendees.put(areaCode, 1 + areaAttendees.getOrDefault(areaCode, 0));
        }

        HashMap<Integer, String> freeCabSeats = new HashMap<>();
        for (int areaCode : areaAttendees.keySet()) {
            freeCabSeats.put(areaCode, areaCabRepository.findById(areaCode).get().getFreeCabSeats());
        }

        HashMap<Integer, Integer> cabScores = new HashMap<>();

        for (int day = 0; day < DaysPerWeek; ++day) {
            int cabScore = 0;

            for (int areaCode : areaAttendees.keySet()) {
                int freeCabSeatsForDay = freeCabSeats.get(areaCode).charAt(day) - '0';
                cabScore += Math.min(areaAttendees.get(areaCode), freeCabSeatsForDay);
            
                cabScores.put(day, cabScore);
            }
        }

        return cabScores;
    }

    private void updateUserSlots(int[] optimalSlots) {
        for (String attendeeId : attendeeIds) {

            StringBuilder slots = new StringBuilder(users.get(attendeeId).getSlots());
            for (int slot : optimalSlots) {
                slots.setCharAt(slot, '1');
            }

            userRepository.updateSlot(attendeeId, slots.toString());
        }
    }

    private void updateCabSeats(int[] optimalSlots) {
        HashMap<Integer, Integer> areaAttendees = new HashMap<>();
        for (String attendee : attendeeIds) {

            int areaCode = users.get(attendee).getAreaCode();
            areaAttendees.put(areaCode, 1 + areaAttendees.getOrDefault(areaCode, 0));
        }

        for (int areaCode : areaAttendees.keySet()) {
            AreaCab areaCab = areaCabRepository.findById(areaCode).get();

            StringBuilder freeCabSeats = new StringBuilder(areaCab.getFreeCabSeats());
            for (int slot : optimalSlots) {
                int slotDay = slot / MaxSlotsPerDay;
                
                int freeCabSeatsForDay =  freeCabSeats.charAt(slotDay) - '0';
                freeCabSeatsForDay -= areaAttendees.get(areaCode);
                
                if (freeCabSeatsForDay == 0) {
                    freeCabSeatsForDay = areaCab.getCabCapacity();
                }
                
                freeCabSeats.setCharAt(slotDay, (char)(freeCabSeatsForDay + '0'));
            }

            areaCabRepository.updateCabSeats(areaCode, freeCabSeats.toString());
        }
    }

    private void updateSchedule(long meetingId, int[] optimalSlots) {
        for (int slot : optimalSlots) {
            LocalDateTime scheduledDateTime = LocalDate.now()
                                                        .with(TemporalAdjusters.next(SlotToDayMap.get(slot / MaxSlotsPerDay)))
                                                        .atTime(SlotToHourMap.get(slot % MaxSlotsPerDay), 0);

            meetingScheduleRepository.save(new MeetingSchedule(meetingId, scheduledDateTime));
        }
    }
}