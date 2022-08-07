package com.truong.bbs_springboot.controller;

import com.truong.bbs_springboot.dto.PostDTO;
import com.truong.bbs_springboot.dto.ResponseBody;
import com.truong.bbs_springboot.model.Post;
import com.truong.bbs_springboot.model.User;
import com.truong.bbs_springboot.service.PostService;
import com.truong.bbs_springboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/post")
public class PostController {
    @Autowired
    private PostService postService;
    @Autowired
    private UserService userService;

    private User getLoggedInUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getLoggedInUser(auth);

        return user;
    }

    @GetMapping("/get-post/{id}")
    public ResponseEntity<?> getPost(@PathVariable("id") Long id) {
        Optional<Post> post = postService.getPostById(id);
        if (post.isPresent()) {
            return new ResponseEntity<>(post.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ResponseBody(
                HttpStatus.BAD_REQUEST,
                "Post with id = " + id + " cant be found",
                null
        ), HttpStatus.BAD_REQUEST);

    }

    @PostMapping("/create-post")
    public ResponseEntity<?> createPost(@RequestBody @Valid PostDTO postDTO, BindingResult bindingResult) throws ParseException {
        if (bindingResult.hasErrors()) {
           /* List<String> errors = new ArrayList<>();
            for (ObjectError err : bindingResult.getAllErrors()) {
                errors.add(err.getDefaultMessage());
            }*/
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

        Optional<Post> post = postService.createPost(postDTO, getLoggedInUser());
        if (post.isPresent()) {
            return new ResponseEntity<>(new ResponseBody(
                    HttpStatus.OK,
                    "Create post success with post id = " + post.get().getId(),
                    null), HttpStatus.OK
            );
        } else {
            return new ResponseEntity<>(new ResponseBody(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Can't create post",
                    null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/edit-post/{id}")
    public ResponseEntity<?> editPost(@RequestBody @Valid PostDTO postDTO, BindingResult bindingResult,
                                      @PathVariable("id") Long id) {
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
        Optional<Post> post = postService.editPost(postDTO, id, getLoggedInUser());
        if (post.isPresent()) {
            return new ResponseEntity<>(new ResponseBody(
                    HttpStatus.OK,
                    "edit post with id = " + post.get().getId() + "success",
                    null
            ), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ResponseBody(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Can't create post",
                    null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get-all-post")
    public ResponseEntity<?> getAllPost() {
        return new ResponseEntity<>(postService.getAllPost(), HttpStatus.OK);
    }

    @GetMapping("/convert-to-csv/{id}")
    public ResponseEntity<?> convertToCSV(@PathVariable("id") Long id) {
        File file;
        try {
            file = postService.exportToCSV(id);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ResponseBody(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Can't export file",
                    null), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getPath());
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");

        ByteArrayResource resource;
        try {
            resource = new ByteArrayResource(Files.readAllBytes(
                    Paths.get(file.getAbsolutePath())
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ResponseBody(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Can't export file",
                    null), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity.ok()
                .headers(header)
                .contentLength(file.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }


}
