package com.cdos.corescheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

import com.cdos.corescheduler.projection.*;

@RestController
public class CoreSchedulerController {

    private static final Logger logger = LoggerFactory.getLogger(CoreSchedulerController.class);
	private UserRepository userRepository;
	private AreaCabRepository areaCabRepository;
	private MeetingScheduleRepository meetingScheduleRepository;

	@Autowired
	CoreSchedulerController(UserRepository userRepository, AreaCabRepository areaCabRepository, MeetingScheduleRepository meetingScheduleRepository) {
		this.userRepository = userRepository;
		this.areaCabRepository = areaCabRepository;
		this.meetingScheduleRepository = meetingScheduleRepository;
	}

	@PostMapping("/users")
	public void newUser(@RequestBody UserApi user, HttpServletResponse httpServletResponse) {
		logger.info("New user added: " + user.getUserId());
		userRepository.save(new User(user.getUserId(), user.getAreaCode(), user.getCommuteMode()));
	}

	@PostMapping("/cabs")
	public void newCab(@RequestBody AreaCabApi areaCab, HttpServletResponse httpServletResponse) {
		areaCabRepository.save(new AreaCab(areaCab.getAreaCode(), areaCab.getCabCapacity()));
	}
 
	/*@GetMapping("/schedule/{meetingId}")
	public Iterable<UserSchedule> getScheduleByMeetingId(@PathVariable(value = "meetingId") String meetingId) {
		return meetingScheduleRepository.getScheduleForMeetingId(meetingId);
	}*/
 
	@GetMapping("/schedule/{userId}")
	public Iterable<UserSchedule> getScheduleByUserId(@PathVariable(value = "userId") String userId) {
		return meetingScheduleRepository.getScheduleForUserId(userId);
	}

	/*
	@GetMapping("/schedule/all")
	public Iterable<MeetingSchedule> getSchedule() {
	}
	*/

}
