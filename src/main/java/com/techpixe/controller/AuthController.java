package com.techpixe.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.techpixe.dto.ErrorResponseDto;
import com.techpixe.entity.User;
import com.techpixe.service.UserService;
import com.techpixe.serviceImpl.BlackList;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/auth")
public class AuthController 
{
	@Autowired
	private UserService userService;
	
	@Autowired
	private BlackList blackList;
	
	@PostMapping("/save")
	public ResponseEntity<User> saveUser(@RequestParam String userName,@RequestParam String email,@RequestParam Long mobileNumber,
										@RequestParam String department,@RequestParam Double salary,@RequestParam String password)
	{
		User save = userService.saveUser(userName, email, mobileNumber, department, salary, password);
		return new ResponseEntity<User>(save,HttpStatus.OK);
	}
	
	private boolean isEmail(String email)
	{
		return email.contains("@");
	}
	
	
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestParam String email,@RequestParam String password)
	{
		if (email!=null && isEmail(email)) 
		{
			Map<String, Object> loginResponse = userService.login(email, password);
			return ResponseEntity.ok(loginResponse);
		} 
		else 
		{
			ErrorResponseDto errorResponseDto = new ErrorResponseDto();
			errorResponseDto.setError("Invalid Email Pattern");
			return ResponseEntity.internalServerError().body(errorResponseDto);
		}
	}
	
	
//	@PostMapping("/login")
//	public ResponseEntity<?> login(@RequestParam String email,@RequestParam String password)
//	{
//		try 
//		{
//			if (email!=null && isEmail(email)) 
//			{
//				Map<String, Object> loginResponse = userService.login(email, password);
//				return ResponseEntity.ok(loginResponse);
//			} 
//		} 
//		catch (Exception e) 
//		{
//			else 
//			{
//				ErrorResponseDto errorResponseDto = new ErrorResponseDto();
//				errorResponseDto.setError("Invalid Email Pattern");
//				return ResponseEntity.internalServerError().body(errorResponseDto);
//			}
//		}
//		
//	}
	
	
//	private boolean isEmail(String email)
//	{
//		return email.contains("@");
//	}
//	
//	@PostMapping("/login")
//	public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password)
//	{
//	    if (email == null || !isEmail(email)) 
//	    {
//	        ErrorResponseDto errorResponseDto = new ErrorResponseDto();
//	        errorResponseDto.setError("Invalid Email Pattern");
//	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseDto);
//	    }
//
//	    try 
//	    {
//	        // Call the login service method
//	        Map<String, Object> loginResponse = userProfileService.login(email, password);
//	        return ResponseEntity.ok(loginResponse);
//	    } 
//	    catch (ResponseStatusException ex)
//	    {
//	        ErrorResponseDto errorResponseDto = new ErrorResponseDto();
//	        errorResponseDto.setError(ex.getReason());
//	        return ResponseEntity.status(ex.getStatusCode()).body(errorResponseDto);
//	    }
//	}
	
	
	
	@PostMapping("/logout")
	//@PreAuthorize("hasAuthority('USER_ROLES') or hasAuthority('ADMIN_ROLES')")
	public String logoutUser(HttpServletRequest request) 
	{
	    String authHeader = request.getHeader("Authorization"); // Extract Authorization header
	    String token = null;

	    if (authHeader != null && authHeader.startsWith("Bearer"))// Check if header contains a token 
	    { 
	        token = authHeader.substring(7); // Extract token by removing "Bearer " prefix
	    }

	    blackList.blacKListToken(token); // Add the token to the blacklist
	    return "You have successfully logged out !!"; // Response message
	}

	
	
	
	@PostMapping("/forgotPasswordSendOTP/{email}")
	public ResponseEntity<?> forgotPasswordSendOTP(@PathVariable String email)
	{
		String userProfileDTO = userService.forgotPasswordSendOTP(email);
		return new ResponseEntity<String>(userProfileDTO,HttpStatus.OK);
	}
	
	@PostMapping("/forgotPassword")
	public ResponseEntity<?> forgotPassword(@RequestParam String email,@RequestParam String newPassword,@RequestParam String otp)
	{
		if (email!=null)
		{
			if (isEmail(email))
			{
				String userProfileDTO = userService.forgotPassword(email, newPassword, otp);
				return new ResponseEntity<String>(userProfileDTO,HttpStatus.OK);
			} 
			else
			{
//				ErrorResponseDto errorResponseDto = new ErrorResponseDto();
//				errorResponseDto.setError("Invalid Email Pattern");
//				return ResponseEntity.internalServerError().body(errorResponseDto);
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Invalid Email Pattern");
			}
		} 
		else
		{
			ErrorResponseDto errorResponseDto = new ErrorResponseDto();
			errorResponseDto.setError("Email is not Found");
			return ResponseEntity.internalServerError().body(errorResponseDto);
		}
	}
	
}
