package com.techpixe.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.techpixe.entity.User;
import com.techpixe.service.UserService;

@RestController
@RequestMapping("/api/admin")
public class AdminController 
{
	@Autowired
	private UserService userService;
	
	@GetMapping("/fetchAll")
	public ResponseEntity<List<User>> fetchAllUsers()
	{
		List<User> fetchedAllUsers = userService.fetchAllUsers();
		return new ResponseEntity<List<User>>(fetchedAllUsers,HttpStatus.OK);
	}
	
	@DeleteMapping("/deleteByUserId/{userId}")
	public ResponseEntity<Void> deleteById(@PathVariable Long userId)
	{
		User userFound = userService.fetchByUserId(userId);
		if (userFound!=null)
		{
			System.err.println("User Deleted");
			userService.deleteByUserId(userId);
			return new  ResponseEntity<>(HttpStatus.OK);
		} 
		else 
		{
			System.err.println("****UserController Delete method if the UserId was not Found****");
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	@GetMapping("/fetchAllUsersWithSorting/{field}")
	public ResponseEntity<List<User>> fetchAllUserswithSorting(@PathVariable String field)
	{
		List<User> fetchedAllUsers = userService.fetchAllUserswithSorting(field);
		return new ResponseEntity<List<User>>(fetchedAllUsers,HttpStatus.OK);
	}
	
	
	@GetMapping("/fetchAllUsersWithPagination/{offset}/{pageSize}")
	public ResponseEntity<Page<User>> fetchAllUsersWithPagination(@PathVariable int offset, int pageSize)
	{
		Page<User> fetchedAllUsers = userService.fetchAllUsersWithPagination(offset,pageSize);
		return new ResponseEntity<Page<User>>(fetchedAllUsers,HttpStatus.OK);
	}
	
	@GetMapping("/fetchAllUsersWithPaginationAndSorting/{offset}/{pageSize}/{field}")
	public ResponseEntity<Page<User>> fetchAllUsersWithPagination(@PathVariable int offset, int pageSize,String field)
	{
		Page<User> fetchedAllUsers = userService.fetchAllUsersWithPaginationWithSorting(offset,pageSize,field);
		return new ResponseEntity<Page<User>>(fetchedAllUsers,HttpStatus.OK);
	}
}
