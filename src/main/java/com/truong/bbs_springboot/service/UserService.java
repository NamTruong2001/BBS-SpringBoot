package com.truong.bbs_springboot.service;

import com.truong.bbs_springboot.dto.LoginDTO;
import com.truong.bbs_springboot.dto.RegisterDTO;
import com.truong.bbs_springboot.model.User;
import com.truong.bbs_springboot.repository.UserRepository;
import com.truong.bbs_springboot.utility.jwtconfig.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    public UserService(UserRepository userRepository,
                       AuthenticationManager authenticationManager,
                       UserDetailsService userDetailsService,
                       JwtTokenUtil jwtTokenUtil) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    public void register(RegisterDTO registerDTO) {
        User user = new User();
        user.setEmail(registerDTO.getEmail());
        user.setUsername(registerDTO.getUsername());
        user.setPassword(registerDTO.getPassword());
    }

    public User findUserById(Long id) {
        return userRepository.findById(id).get();
    }

    public String login(LoginDTO clientLoginRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                clientLoginRequest.getEmail(), clientLoginRequest.getPassword()
        ));
        UserDetails userDetails = userDetailsService
                .loadUserByUsername(clientLoginRequest.getEmail());

        String token = jwtTokenUtil.generateToken(userDetails);

        return token;
    }

    public User getLoggedInUser(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findUserByEmail(email);
        return user;
    }


}
