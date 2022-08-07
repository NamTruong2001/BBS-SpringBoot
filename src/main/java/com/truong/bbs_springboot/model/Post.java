package com.truong.bbs_springboot.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String thumbnail;
    @Column
    private String author;
    @Column
    private String title;
    @Column
    @Lob
    private String content;
    @Column
    private Date createdOn;
    @Column
    private Date updatedOn;
    @ManyToOne
    private User user;

}
