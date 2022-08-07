package com.truong.bbs_springboot.validator;


import com.truong.bbs_springboot.dto.RegisterDTO;
import com.truong.bbs_springboot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class UserValidator implements Validator {

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return false;
    }

    @Override
    public void validate(Object target, Errors errors) {
        RegisterDTO registerDTO = (RegisterDTO) target;

        if (userRepository.existsByEmail(registerDTO.getEmail())) {
            errors.rejectValue("email", "error.email", "Email has already exist");
        }

    }
}
