package com.fa.api.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fa.api.model.PostDetailsDTO;
import com.fa.api.model.PostInstructionDTO;
import com.fa.api.model.PostRequestDTO;
import com.fa.api.rest.exception.ObjectNotFoundException;
import com.fa.api.service.PostService;


@RestController
@RequestMapping("/posts")
public class PostRestController {
	
	@Autowired
	private PostService postService;
	
	@GetMapping("/{id}")
	public PostDetailsDTO getPost(@PathVariable Long id) {
		PostDetailsDTO postJson = postService.findById(id);

		if (postJson == null) {
			throw new ObjectNotFoundException("Not found post");
		}

		return postJson;
	}

	
	@GetMapping
	public List<PostDetailsDTO> getPostByArticle(@RequestParam("article-id") Long id) {
		List<PostDetailsDTO> postJsons = postService.findByArticleId(id);

		if (postJsons.isEmpty()) {
			throw new ObjectNotFoundException("Not found posts by article ID: " + id);
		}

		return postJsons;
	}
	
	
	@GetMapping("/details")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public Page<PostDetailsDTO> getOrSearchPostByArticle(
			@RequestParam("article-id") Long id,
			@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "size", defaultValue = "10") int size,
			@RequestParam(required = false) String keyword){
		
		Page<PostDetailsDTO> listPost = 
				postService.findAllPostsIncludeChildsByArticleId(id, page, size, keyword);
		
		if (listPost.isEmpty()) {
			throw new ObjectNotFoundException("Not available post(s) for article id: " + id);
		}
		
		return listPost;
	}
	
	
	@GetMapping("/top")
	public Map<String,List<PostInstructionDTO>> getTopPostOfAll() {
		
		Map<String, List<PostInstructionDTO>> map = 
				postService.findTopPostOfAllChildArticles();
		
		if (map.isEmpty()) {
			throw new ObjectNotFoundException("Not found top posts of all");
		}

		return map;
	}
	
	@GetMapping("/reviews/{id}")
	public List<PostInstructionDTO> getTopPostsForReview(@PathVariable("id") Long articleId) {
		List<PostInstructionDTO> postJsons = 
				postService.findTop3PostsOfEachChildByParentArticleId(articleId);
		
		if (postJsons.isEmpty()) {
			throw new ObjectNotFoundException("Not found posts by article id: " + articleId);
		}
		
		return postJsons;
	}

	
	@GetMapping("/parent")
	public Page<PostDetailsDTO> getPostsByParentArticleId(
			@RequestParam("parent-id") Long articleId,
			@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "size", defaultValue = "10") int size) {		
		
		Page<PostDetailsDTO> postJsons = postService.findByParentArticleId(articleId, page, size);
		
		if (postJsons.isEmpty()) {
			throw new ObjectNotFoundException("Not found posts by parent id: " + articleId);
			
		}
		
		return postJsons;
	}
	
	
	
	
	
	@RequestMapping(method = {RequestMethod.POST, RequestMethod.PUT})
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public PostRequestDTO saveOrUpdatePost(@RequestBody PostRequestDTO postRequest) {
		postService.save(postRequest);
		
		return postRequest;
	}
	
	
	@PostMapping("/thumbnails/upload")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<String> uploadFileImage(@RequestParam("thumbnail") MultipartFile multipartFile) {
		
		String imageUploadedPath = postService.uploadImageFile(multipartFile);
		return ResponseEntity.status(HttpStatus.CREATED).body(imageUploadedPath);

	}
	
	
	@GetMapping("/comments/censor")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public List<PostInstructionDTO> getTopPostsOfRecentComment() {
		List<PostInstructionDTO> postJsons = postService.findTopPostByComment();
		
		if (postJsons.isEmpty()) {
			throw new ObjectNotFoundException("Not found posts by recent comment");
		}
		
		return postJsons;
	}
	

	@DeleteMapping("/{id}")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public void deletePost(@PathVariable(value = "id") Long id) {
		
		postService.delete(id);

	}
	
	
	@GetMapping("/piechart/data")
	public List<PostDetailsDTO> getPost(){
		Date startDate = new Date();
		startDate.setYear(2020);
		List<PostDetailsDTO> listPost = postService.findByDateCreateBetween(new Date(2020, 8, 17), new Date(2022,8,11));
		return listPost;

	}
	
}