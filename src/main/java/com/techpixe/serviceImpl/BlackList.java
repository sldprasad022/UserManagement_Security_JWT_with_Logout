package com.techpixe.serviceImpl;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;

@Service
public class BlackList
{
    private Set<String> blackListTokenSet = new HashSet<>(); // Store blacklisted tokens

    public void blacKListToken(String token) 
    {
        blackListTokenSet.add(token); // Add the token to the blacklist
    }

    public boolean isBlackListed(String token) 
    {
        return blackListTokenSet.contains(token); // Check if a token is blacklisted
    }
}
