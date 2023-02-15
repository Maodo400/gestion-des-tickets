package com.brianaubry.helpdesk.controller;

import com.brianaubry.helpdesk.model.Status;
import com.brianaubry.helpdesk.model.Ticket;
import com.brianaubry.helpdesk.model.ListDepartement;
import com.brianaubry.helpdesk.model.ListPriorite;
import com.brianaubry.helpdesk.model.Role;
import com.brianaubry.helpdesk.model.Stage;
import com.brianaubry.helpdesk.model.User;
import com.brianaubry.helpdesk.repository.StatusRepository;
import com.brianaubry.helpdesk.repository.TicketRepository;
import com.brianaubry.helpdesk.repository.UserRepository;
import com.brianaubry.helpdesk.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@Controller
@RequestMapping("/ticket")
public class TicketController {
	
	Role r = new Role();

	@Autowired
	UserService userService;

	@Autowired
	TicketRepository ticketRepository;
	
	@Autowired
	UserRepository userRepository;

	@Autowired
	StatusRepository statusRepository;

	// this allows the "logged in user" to have information populated into the
	// methods, i.e. id, etc. I'm not entirely
	// sure it's necessary, but I haven't researched how to change it or where to
	// move it so it isn't reused so often.
	@ModelAttribute("loggedInUser")
	public User populateUserDetails(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User loggedInUser = userService.findUserByEmail(auth.getName());
		model.addAttribute("isUser", userService.isUser(loggedInUser));
		model.addAttribute("isAdmin", userService.isAdmin(loggedInUser));
		return loggedInUser;
	}

	// Opens the main ticket page, showing a list of all tickets and tickets
	// assigned
	@RequestMapping(value = "/")
	public String ticketIndex(Model model, @ModelAttribute User loggedInUser) {
		System.out.println(populateUserDetails(model));
		populateUserDetails(model);
		List<Ticket> openTickets = ticketRepository.findAll();
		model.addAttribute("openTickets", openTickets);

		return "ticket/index";
	}

	@RequestMapping(value = "all")
	public String ticketListAll(Model model, @ModelAttribute User loggedInUser) {
		List<Ticket> openTickets = ticketRepository.findAll();
		model.addAttribute("openTickets", openTickets);
		return "ticket/all-tickets";
	}

	@GetMapping(value = "open")
	public String ticketListOpen(Model model, @ModelAttribute User loggedInUser) {
		List<Ticket> openTickets = new ArrayList<Ticket>();
		List<Ticket> openTicket = ticketRepository.findAll();
		for (Ticket ticket : openTicket) {
			if (ticket.getStage() != Stage.CLOTURE)
				openTickets.add(ticket);
		}
		model.addAttribute("openTickets", openTickets);
		return "ticket/open";
	}
	
	@GetMapping(value = "close")
	public String ticketListClose(Model model, @ModelAttribute User loggedInUser) {
		List<Ticket> openTickets = new ArrayList<Ticket>();
		List<Ticket> openTicket = ticketRepository.findAll();
		for (Ticket ticket : openTicket) {
			if (ticket.getStage() == Stage.CLOTURE)
				openTickets.add(ticket);
		}
		model.addAttribute("openTickets", openTickets);
		return "ticket/open";
	}

	// shows form to create new tickets
	@GetMapping(value = "new")
	public String createNewTicket(Model model) {

		Ticket newTicket = new Ticket();
		model.addAttribute(newTicket);
		model.addAttribute("priorites", ListPriorite.values());
		model.addAttribute("departements", ListDepartement.values());
		return "ticket/new";
	}

	// sends ticket creation form, and forwards to the newly created page for the
	// individual ticket
	@PostMapping(value = "new")
	public String processTicketCreation(Model model, @Valid Ticket newTicket, Errors errors) {

		if (errors.hasErrors()) {
			return "ticket/new";
		}

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User loggedInUser = userService.findUserByEmail(auth.getName());
		Date currentDate = new Date();
		newTicket.setDateOpened(currentDate);
		newTicket.setCreatedBy(loggedInUser);
		ticketRepository.save(newTicket);

		return "redirect:/ticket/open";
	}

	// handles handles the viewing of an individual ticket
	@RequestMapping(value = "{id}")
	public String ticketIndexSingle(Model model, @PathVariable("id") int id) {
		Ticket activeTicket = ticketRepository.findById(id);
		Status newStatus = new Status();
		// creates a list of updates tied to that ticket, and then sorts them in reverse
		// order,
		// so that the newest "status" is on top
		List<Status> updates = activeTicket.getUpdates();
		Collections.reverse(updates);

		model.addAttribute("user", new User());
		model.addAttribute("ticket", activeTicket);
		model.addAttribute("statuses", activeTicket.getUpdates());
		model.addAttribute("status", newStatus);
		model.addAttribute("stages", Stage.values());
		model.addAttribute("priorites", ListPriorite.values());
		model.addAttribute("departements", ListDepartement.values());
		model.addAttribute("assignes", userRepository.findAll());
		
//		List<User> us = userRepository.findAll();
//		for (User status : us) {
//			System.out.println("user "+status);
//		}
		return "ticket/ticket-detail";
	}

	// processes adding of a new status
	@PostMapping(value = "{id}/add")
	public String addStatusUpdate(Model model, @PathVariable("id") int id, @Valid Status newStatus) {
		Ticket activeTicket = ticketRepository.findById(id);
		System.out.println(activeTicket.toString());
		User activeUser = populateUserDetails(model);
		newStatus.setAuthor(activeUser.getEmail());
		activeTicket.addUpdate(newStatus);
		activeTicket.setLastUpdated(new Date());
		statusRepository.save(newStatus);
		return "redirect:/ticket/" + id;
	}

	// processes the updating of specific information tied to the initial ticket
	// when it was created, i.e. the
	// description or stage
	@PostMapping(value = "{id}/update")
	public String processTicketUpdate(Model model, @PathVariable("id") int id, @Valid Ticket ticket, Errors errors) {

		//User user = userRepository.findById(ticket.getAssignedTo().getId());
		Ticket activeTicket = ticketRepository.findById(id);
		//System.out.println(activeTicket.getAssignedTo().getEmail());
		activeTicket.setStage(ticket.getStage());
		if(ticket.getAssignedTo()!=null)
		activeTicket.setAssignedTo(ticket.getAssignedTo());
		ticketRepository.save(activeTicket);

		return "redirect:/ticket/all";
	}

	// processes ticket closure
	@PostMapping(value = "{id}/close")
	public String processTicketClosure(Model model, @PathVariable("id") int id, @Valid Ticket ticket, Errors errors) {

		Ticket activeTicket = ticketRepository.findById(id);
		activeTicket.setTitle("bon");
		activeTicket.setStage(Stage.CLOTURE);
		activeTicket.setDateClosed(new Date());
		ticketRepository.save(activeTicket);

		return "redirect:/ticket/"+ id;
	}

	// enables the ability to reopen tickets when they have been closed
	@PostMapping(value = "{id}/reopen")
	public String processTicketReopen(Model model, @PathVariable("id") int id, @Valid Ticket ticket, Errors errors) {
		// TODO: test ticket reopening
		Ticket activeTicket = ticketRepository.findById(id);
		activeTicket.setStage(Stage.RECU);
		activeTicket.setDateClosed(null);
		return "redirect:/ticket/"+id;
	}

}
