package com.techpixe.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.techpixe.dto.ErrorResponseDto;
import com.techpixe.dto.UserDto;
import com.techpixe.entity.User;
import com.techpixe.service.UserService;

@RestController
@RequestMapping("/api/user")
public class UserController 
{
	@Autowired
	private UserService userService;
	
//	@PostMapping("/save")
//	public ResponseEntity<User> saveUser(@RequestParam String userName,@RequestParam String email,@RequestParam Long mobileNumber,
//										@RequestParam String department,@RequestParam Double salary,@RequestParam String password)
//	{
//		User save = userService.saveUser(userName, email, mobileNumber, department, salary, password);
//		return new ResponseEntity<User>(save,HttpStatus.OK);
//	}
	
	@GetMapping("/fetchByUserId/{userId}")
	public ResponseEntity<User> fetchByUserId(@PathVariable("userId") Long id)
	{
		User fetchById = userService.fetchByUserId(id);
		return new ResponseEntity<User>(fetchById,HttpStatus.OK);
	}
	
	@GetMapping("/fetchAll")
	public ResponseEntity<List<User>> fetchAllUsers()
	{
		List<User> fetchedAllUsers = userService.fetchAllUsers();
		return new ResponseEntity<List<User>>(fetchedAllUsers,HttpStatus.OK);
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
	
	
	
	
	
//	@DeleteMapping("/deleteByUserId/{userId}")
//	public ResponseEntity<Void> deleteById(@PathVariable Long userId)
//	{
//		User userFound = userService.fetchByUserId(userId);
//		if (userFound!=null)
//		{
//			System.err.println("User Deleted");
//			userService.deleteByUserId(userId);
//			return new  ResponseEntity<>(HttpStatus.OK);
//		} 
//		else 
//		{
//			System.err.println("****UserController Delete method if the UserId was not Found****");
//			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//		}
//	}
	
	@PutMapping("/update/{userId}")
	public ResponseEntity<User> updateUserId(@PathVariable Long userId,@RequestParam(required=false) String userName,@RequestParam(required = false) String email,@RequestParam(required=false) Long mobileNumber,@RequestParam(required=false) String department,@RequestParam(required=false) Double salary)
	{
		Optional<User> updatedUser = userService.updateUserById(userId, userName, email, mobileNumber, department, salary);
		return updatedUser.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
		//return updatedUser.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}
	
	private boolean isEmail(String email)
	{
		return email.contains("@");
	}
	
//	@PostMapping("/login")
//	public ResponseEntity<UserDto> login(@RequestParam String email,@RequestParam String password)
//	{
//		UserDto loginUser = userService.login(email, password);
//		return new ResponseEntity<UserDto>(loginUser,HttpStatus.OK);
//	}
//	
//	@PostMapping("/forgotPasswordSendOTP/{email}")
//	public ResponseEntity<?> forgotPasswordSendOTP(@PathVariable String email)
//	{
//		String userProfileDTO = userService.forgotPasswordSendOTP(email);
//		return new ResponseEntity<String>(userProfileDTO,HttpStatus.OK);
//	}
//	
//	@PostMapping("/forgotPassword")
//	public ResponseEntity<?> forgotPassword(@RequestParam String email,@RequestParam String newPassword,@RequestParam String otp)
//	{
//		if (email!=null)
//		{
//			if (isEmail(email))
//			{
//				String userProfileDTO = userService.forgotPassword(email, newPassword, otp);
//				return new ResponseEntity<String>(userProfileDTO,HttpStatus.OK);
//			} 
//			else
//			{
//				ErrorResponseDto errorResponseDto = new ErrorResponseDto();
//				errorResponseDto.setError("Invalid Email Pattern");
//				return ResponseEntity.internalServerError().body(errorResponseDto);
//			}
//		} 
//		else
//		{
//			ErrorResponseDto errorResponseDto = new ErrorResponseDto();
//			errorResponseDto.setError("Email is not Found");
//			return ResponseEntity.internalServerError().body(errorResponseDto);
//		}
//	}
//	
	
	@PostMapping("/changePassword/{userProfileId}")
	public ResponseEntity<String> changePassord(@RequestParam String oldPassword ,@RequestParam String password,@RequestParam String confirmPassword,@PathVariable Long userProfileId)
	{
    	String changedPassword= userService.changePassword(oldPassword,password, confirmPassword, userProfileId);
    	return new ResponseEntity<String>(changedPassword,HttpStatus.OK);
	}
	
}
