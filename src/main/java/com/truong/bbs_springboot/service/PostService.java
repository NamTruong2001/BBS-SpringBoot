package com.truong.bbs_springboot.service;

import com.truong.bbs_springboot.FormatDateHelper;
import com.truong.bbs_springboot.dto.PostDTO;
import com.truong.bbs_springboot.model.Post;
import com.truong.bbs_springboot.model.User;
import com.truong.bbs_springboot.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final FormatDateHelper formatDateHelper;
    private static final String EXPORT_FORMAT = ".csv";
    private static final String[] FILE_HEADER = {"Author name", "Title", "Created At", "Updated At"};
    public static String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/csvFiles";


    @Autowired
    public PostService(PostRepository postRepository, FormatDateHelper formatDateHelper) {
        this.postRepository = postRepository;
        this.formatDateHelper = formatDateHelper;

    }


    public Optional<Post> getPostById(Long id) {
        return postRepository.findById(id);
    }

    public Optional getPostByPostIdAndUser(Long id, User user) {
        return postRepository.getPostByIdAndUser(id, user);
    }

    public Optional<Post> createPost(PostDTO postDTO, User user) throws ParseException {
        Post post = new Post();
        post.setCreatedOn(formatDateHelper.stringToDate(
                formatDateHelper.dateToString(new Date())
        ));
        post.setUpdatedOn(new Date());
        post.setAuthor(postDTO.getAuthor());
        post.setContent(postDTO.getContent());
        post.setTitle(postDTO.getTitle());
        post.setThumbnail(postDTO.getThumbnail());
        post.setUser(user);

        Optional<Post> savedPost = Optional.of(postRepository.save(post));
        return savedPost;
    }

    public Optional<Post> editPost(PostDTO postDTO, Long id, User user) {
        Optional<Post> tempPost = getPostByPostIdAndUser(id, user);
        if (tempPost.isPresent()) {
            Post post = tempPost.get();
            post.setUpdatedOn(
                    formatDateHelper.stringToDate(
                            formatDateHelper.dateToString(new Date())
                    )
            );
            post.setAuthor(postDTO.getAuthor());
            post.setContent(postDTO.getContent());
            post.setTitle(postDTO.getTitle());
            post.setThumbnail(postDTO.getThumbnail());

            postRepository.save(post);
        }
        return tempPost;

    }

    public List<Post> getAllPost() {
        return postRepository.findAll();
    }

    public File exportToCSV(Long postId) throws Exception {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isPresent()) {
            Post foundPost = post.get();
            File file = new File(uploadDir + "/" + foundPost.getId() + EXPORT_FORMAT);
            file.createNewFile();

            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(Arrays.stream(FILE_HEADER).collect(Collectors.joining(",")));

            String[] contents = {foundPost.getAuthor(), foundPost.getTitle(), foundPost.getCreatedOn().toString(), foundPost.getUpdatedOn().toString()};

            for (int i = 0; i < contents.length; i++) {
                if (contents[i].isEmpty()) {
                    contents[i] = "[blank]";
                }
            }
            fileWriter.write(System.getProperty("line.separator"));
            fileWriter.write(Arrays.stream(contents).collect(Collectors.joining(",")));

            fileWriter.close();
            return file;
        } else {
            throw new Exception();
        }
    }


}



