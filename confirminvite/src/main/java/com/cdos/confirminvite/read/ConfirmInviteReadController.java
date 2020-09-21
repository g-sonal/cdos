package com.cdos.confirminvite.read;

import com.cdos.confirminvite.MeetingInviteSummary;
import com.cdos.confirminvite.projection.UserMeetingInviteRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class ConfirmInviteReadController {

	private UserMeetingInviteRepository userMeetingInviteRepository;

	@Autowired
	public ConfirmInviteReadController(UserMeetingInviteRepository userMeetingInviteRepository) {
		this.userMeetingInviteRepository = userMeetingInviteRepository;
	}

	@GetMapping("/meetingrequests/{userId}/received")
	public Iterable<MeetingInviteSummary> getByUserId(@PathVariable(value = "userId") String userId) {
		return userMeetingInviteRepository.findForUserId(userId);
	}

}
