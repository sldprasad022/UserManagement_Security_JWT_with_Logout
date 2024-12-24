package com.techpixe.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.techpixe.entity.User;
import com.techpixe.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService 
{

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException 
    {
        //User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    	
    	User user = userRepository.findByEmail(email);
    	if (user==null)
    	{
			throw new UsernameNotFoundException("User not found");
		}
                

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), // Use email
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(user.getRole()))
        );
    }
    
}
