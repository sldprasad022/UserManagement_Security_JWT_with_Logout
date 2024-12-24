package com.techpixe.serviceImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.techpixe.dto.UserDto;
import com.techpixe.entity.User;
import com.techpixe.repository.UserRepository;
import com.techpixe.service.UserService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class UserServiceImpl implements UserService
{
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	 @Autowired
	 private JwtUtils jwtUtils;

	
	@Autowired
	private JavaMailSender javaMailSender;
	
	@Value("$(spring.mail.username)")
	private String fromMail;

	@Override
	public User saveUser(String userName, 
						 String email, 
						 Long mobileNumber, 
						 String department, 
						 Double salary,
						 String password) 
	{
		
		
		
		
		User user = new User();
		user.setUserName(userName);
		user.setEmail(email);
		user.setMobileNumber(mobileNumber);
		user.setDepartment(department);
		user.setSalary(salary);
		user.setPassword(passwordEncoder.encode(password));
		user.setRole("ROLE_USER");
		
		return userRepository.save(user);
	}

	@Override
	public User fetchByUserId(Long userId) 
	{
		System.err.println("User FetchByUserId method for deleting for checking wheather the userId exists or not");
		return userRepository.findById(userId).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"User with this "+userId+" not Found"));
	}

	@Override
	public List<User> fetchAllUsers()
	{
		List<User> fetchAll = userRepository.findAll();
		if (fetchAll.isEmpty())
		{
			throw new ResponseStatusException(HttpStatus.OK,"No Records Found");
		}
		return fetchAll;
	}
	
	
	
	//Fetch All With Sorting based on field
	@Override
	public List<User> fetchAllUserswithSorting(String field)
	{
		List<User> fetchAll = userRepository.findAll(Sort.by(Sort.Direction.ASC,field));
		if (fetchAll.isEmpty())
		{
			throw new ResponseStatusException(HttpStatus.OK,"No Records Found");
		}
		return fetchAll;
	}
	
	//Pagination
	@Override
	public Page<User> fetchAllUsersWithPagination(int offset,int pageSize)
	{
		Page<User> fetchAll = userRepository.findAll(PageRequest.of(offset, pageSize));
		if (fetchAll.isEmpty())
		{
			throw new ResponseStatusException(HttpStatus.OK,"No Records Found");
		}
		return fetchAll;
	}
	
	
	//Pagination with Sorting 
	@Override
	public Page<User> fetchAllUsersWithPaginationWithSorting(int offset,int pageSize,String field)
	{
		Page<User> fetchAll = userRepository.findAll(PageRequest.of(offset, pageSize).withSort(Sort.by(field)));
		if (fetchAll.isEmpty())
		{
			throw new ResponseStatusException(HttpStatus.OK,"No Records Found");
		}
		return fetchAll;
	}


	@Override
	public void deleteByUserId(Long userId) 
	{
		userRepository.deleteById(userId);
	}
	
	@Override
	public Optional<User> updateUserById(Long userId, String userName, 
										 String email, Long mobileNumber,
										 String department, Double salary) {
		return userRepository.findById(userId).map(existingUser->{
			existingUser.setUserName(userName!=null ? userName: existingUser.getUserName());
			existingUser.setEmail(email!=null ? email: existingUser.getEmail());
			existingUser.setMobileNumber(mobileNumber !=null ? mobileNumber : existingUser.getMobileNumber());
			existingUser.setDepartment(department!=null ? department : existingUser.getDepartment());
			existingUser.setSalary(salary!=null ? salary : existingUser.getSalary());
			
			return userRepository.save(existingUser);
		});
	}

//	@Override
//	public Map<String, Object> login(String email, String password) 
//	{
//		User user1 = userRepository.findByEmail(email);
//		if (user1!=null&& passwordEncoder.matches(password,user1.getPassword()))
//		{
//			
//			UserDto userDto = new UserDto();
//			userDto.setUserName(user1.getUserName());
//			System.err.println(user1.getUserName());
//			userDto.setDepartment(user1.getDepartment());
//			System.err.println(user1.getDepartment());
//			
//			
//			// Generate JWT token
//		    String token = jwtUtils.generateToken(user1.getEmail(),user1.getRole());
//			
//			Map<String, Object> response = new HashMap<>();
//		    response.put("token", token);
//		    response.put("userDetails",userDto);
//			
//			return response;
//		} 
//		else 
//		{
//			throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Email "+email+" is not Found");
//		}
//	}
	
	
	@Override
	public Map<String, Object> login(String email, String password) 
	{
		User user1 = userRepository.findByEmail(email);
		if (user1!=null)
		{
			if (passwordEncoder.matches(password,user1.getPassword())) 
			{
				UserDto userDto = new UserDto();
				userDto.setUserName(user1.getUserName());
				System.err.println(user1.getUserName());
				userDto.setDepartment(user1.getDepartment());
				System.err.println(user1.getDepartment());
				
				
				// Generate JWT token
			    String token = jwtUtils.generateToken(user1.getEmail(),user1.getRole());
				
				Map<String, Object> response = new HashMap<>();
			    response.put("token", token);
			    response.put("userDetails",userDto);
				
				return response;
			} 
			else 
			{
				throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Password does not match");
			}
		} 
		else 
		{
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Email "+email+" is not Found");
		}
	}
	
	
	
	public static String generateOTP() {
		Random random = new Random();
		int otp = 100000 + random.nextInt(900000); // Generates a random number between 100000 and 999999
		return String.valueOf(otp);
	}
	
	@Override
	public String forgotPasswordSendOTP(String email) {
	    User userProfile1 = userRepository.findByEmail(email);
	    if (userProfile1 != null) {

	        // Generate OTP and save it to the user profile
	        String generateOTP = generateOTP();
	        userProfile1.setOtp(generateOTP);
	        userRepository.save(userProfile1);

	        try {
	            // Create MIME message for HTML content
	            MimeMessage message = javaMailSender.createMimeMessage();
	            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
	            
	            helper.setFrom(fromMail);
	            helper.setTo(email);
	            helper.setSubject("OTP for Password Reset - Astrology Application");

	            // Compose the colorful HTML email body
	            String emailBody = String.format(
	                "<!DOCTYPE html>" +
	                "<html lang=\"en\">" +
	                "<head>" +
	                "   <meta charset=\"UTF-8\">" +
	                "   <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
	                "   <style>" +
	                "       body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }" +
	                "       .container { padding: 20px; background-color: #ffffff; max-width: 600px; margin: auto; border: 1px solid #e0e0e0; border-radius: 8px; }" +
	                "       .header { background-color: #4CAF50; padding: 20px; border-top-left-radius: 8px; border-top-right-radius: 8px; color: #ffffff; text-align: center; }" +
	                "       .header h1 { margin: 0; font-size: 24px; }" +
	                "       .content { padding: 20px; color: #333333; }" +
	                "       .content p { line-height: 1.6; font-size: 16px; }" +
	                "       .otp { font-size: 24px; font-weight: bold; color: #ff5722; background-color: #fff3e0; padding: 10px; text-align: center; border-radius: 5px; margin: 15px 0; }" +
	                "       .footer { background-color: #4CAF50; padding: 15px; text-align: center; color: #ffffff; border-bottom-left-radius: 8px; border-bottom-right-radius: 8px; font-size: 14px; }" +
	                "       .support { background-color: #e0f7fa; padding: 10px; margin: 20px 0; border-radius: 5px; text-align: center; }" +
	                "       a { color: #00796b; text-decoration: none; }" +
	                "   </style>" +
	                "</head>" +
	                "<body>" +
	                "   <div class=\"container\">" +
	                "       <div class=\"header\">" +
	                "           <h1>Astrology App - Password Reset</h1>" +
	                "       </div>" +
	                "       <div class=\"content\">" +
	                "           <p>Dear %s,</p>" + 
	                "           <p>We received a request to reset the password for your account associated with this email address (<strong>%s</strong>).</p>" +
	                "           <p>To proceed with the password reset, please use the following One-Time Password (OTP):</p>" +
	                "           <div class=\"otp\">%s</div>" +
	                "           <p>Enter this code to reset your password. Once verified, you’ll be prompted to create a new password.</p>" +
	                "           <p>If you did not request this, please ignore this email or contact our support team.</p>" +
	                "           <div class=\"support\">" +
	                "               <p>For assistance, contact us at <a href=\"mailto:support@techpixe.com\">info@techpixe.com</a>.</p>" +
	                "           </div>" +
	                "       </div>" +
	                "       <div class=\"footer\">" +
	                "           <p>Thank you, <br>The TechPixe Team</p>" +
	                "           <p>&copy; 2024 TechPixe. All rights reserved.</p>" +
	                "       </div>" +
	                "   </div>" +
	                "</body>" +
	                "</html>", userProfile1.getUserName(), email, generateOTP);

	            // Set the HTML content in the email
	            helper.setText(emailBody, true);

	            // Send the email
	            javaMailSender.send(message);
	        } catch (MessagingException e) {
	            throw new RuntimeException("Failed to send email", e);
	        }

	        return "OTP Sent Successfully to your Email";
	    } else {
	        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Email not found");
	    }
	}

	
	



	@Override
	public String forgotPassword(String email, String newPassword, String otp) {
		User user1 = userRepository.findByEmail(email);
		if (user1 != null) {
			if (user1.getOtp().equals(otp)) {

				user1.setPassword(passwordEncoder.encode(newPassword));
				userRepository.save(user1);

				return "Password Changed Successfully";
			} else {
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "OTP does not Macth");
			}

		} else {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Email with this user is not Found");
		}
	}

	
	@Override
	public String changePassword(String oldPassword,String password, String confirmPassword, Long userProfileId)
	{
		User user1 = userRepository.findById(userProfileId).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"UserProfile Id was not not found"));
		if (passwordEncoder.matches(oldPassword, user1.getPassword())) 
		{
			if (user1!=null && password.equals(confirmPassword))
			{
				user1.setPassword(passwordEncoder.encode(password));
				userRepository.save(user1);
				
				return "Password Changed Successfully";
			} 
			else 
			{
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Password and Confirm Password Does not match");
			}
		} 
		else 
		{
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Old Password Does not match");
		}
		
	}
	
	
	

}
