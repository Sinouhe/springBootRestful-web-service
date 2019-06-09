package com.in28minutes.rest.webservices.restfulwebservice.user;

import java.net.URI;
import java.util.List;
import java.util.Locale;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
public class UserRessource {

	@Autowired
	private UserDAOService service;
	
	@Autowired
	private MessageSource messageSource;
	
	@GetMapping("/users")
	public List<User> retrieveAllUsers() {
		return service.findAll();
	}
	
	@GetMapping("/user/{id}")
	public Resource<User> retrieveUSer(@PathVariable int id) {
		User user = service.findOne(id);
		if(user == null) {
			throw new UserNotFoundException("id : " + id);
		}
		Resource<User> resource = new Resource<User>(user);
		ControllerLinkBuilder linkTo = 
				ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(this.getClass()).retrieveAllUsers());
		
		resource.add(linkTo.withRel("all-users"));
		
		return resource;
	}
	
	@PostMapping("/user")
	public ResponseEntity<Object> creatUser(@Valid @RequestBody User user) {
		User savedUser = service.save(user);
		//created user to resend with id
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
									.path("/{id}")
									.buildAndExpand(savedUser.getId())
									.toUri();
		return ResponseEntity.created(location).build();
	}
	
	@DeleteMapping("/user/{id}")
	public User deleteUser(@PathVariable int id) {
		User user = service.deleteByID(id);
		if(user == null)
			throw new UserNotFoundException("id :" + id );
		
		return user;
	}
	
	//for the message 
	@GetMapping(path="/hello-world-worldWide")
	public String helloWorldWorldWide(@RequestHeader(name="Accept-Language", required=false) Locale locale) {
		return messageSource.getMessage("good.mornig.message", null, locale);
	}
	
}
