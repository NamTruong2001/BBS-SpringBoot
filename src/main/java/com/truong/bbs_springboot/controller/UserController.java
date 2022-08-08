package com.truong.bbs_springboot.controller;

import com.truong.bbs_springboot.dto.JwtResponse;
import com.truong.bbs_springboot.dto.LoginDTO;
import com.truong.bbs_springboot.dto.RegisterDTO;
import com.truong.bbs_springboot.dto.ResponseBody;
import com.truong.bbs_springboot.service.UserService;
import com.truong.bbs_springboot.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserValidator userValidator;
    @Autowired
    @Qualifier("userService")
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterDTO registerDTO, BindingResult bindingResult) {

        userValidator.validate(registerDTO, bindingResult);

        if (bindingResult.hasErrors()) {
            Map<String, String> errors =
                    bindingResult.getAllErrors()
                            .stream().collect(Collectors.toMap(
                                    field -> ((FieldError) field).getField(),
                                    message -> message.getDefaultMessage()
                            ));
            return new ResponseEntity<>(new ResponseBody(
                    HttpStatus.BAD_REQUEST,
                    "Validation failed",
                    errors
            ), HttpStatus.BAD_REQUEST);
        }
        userService.register(registerDTO);
        return new ResponseEntity<>(new ResponseBody(
                HttpStatus.OK,
                "New user has been registered",
                null), HttpStatus.OK);

    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
       JwtResponse response = userService.login(loginDTO);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

