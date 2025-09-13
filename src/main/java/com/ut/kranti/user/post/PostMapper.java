package com.ut.kranti.user.post;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

public class PostMapper {

    private static final ModelMapper modelMapper = new ModelMapper();

    // Entity to DTO
    public static PostDto toDto(Post post) {
    	
        PostDto obj= modelMapper.map(post, PostDto.class);
        obj.setUserId(post.getAuthor().getId());
        return obj;
    }

    // DTO to Entity
    public static Post toEntity(PostDto postDto) {
        return modelMapper.map(postDto, Post.class);
    }
    // List<Post> to List<PostDto>
    public static List<PostDto> toDtoList(List<Post> posts) {
        return posts.stream().map(PostMapper::toDto).collect(Collectors.toList());
    }

    // Page<Post> to Page<PostDto>
    public static Page<PostDto> toDtoPage(Page<Post> postPage) {
        List<PostDto> dtos = postPage.stream()
                                     .map(PostMapper::toDto)
                                     .collect(Collectors.toList());
        return new PageImpl<>(dtos, postPage.getPageable(), postPage.getTotalElements());
    }
}
