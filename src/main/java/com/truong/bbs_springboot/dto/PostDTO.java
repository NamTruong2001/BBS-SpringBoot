package com.truong.bbs_springboot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDTO {

    @NotEmpty(message = "Thumbnail is required")
    private String thumbnail;

    @NotBlank(message = "Author name is required")
    private String author;

    @Length(max = 150, message = "max character is 150")
    @NotBlank(message = "Title is required")
    private String title;

    @Lob
    @NotBlank(message = "Content is required")
    private String content;
}
