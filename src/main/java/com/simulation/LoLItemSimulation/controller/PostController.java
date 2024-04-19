package com.simulation.LoLItemSimulation.controller;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.simulation.LoLItemSimulation.config.PhotoUtil;
import com.simulation.LoLItemSimulation.domain.*;
import com.simulation.LoLItemSimulation.dto.PostDto;
import com.simulation.LoLItemSimulation.dto.SimulationDTO;
import com.simulation.LoLItemSimulation.repository.*;
import com.simulation.LoLItemSimulation.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
// PostController.java
@Controller
@RequestMapping("/post")
@Log4j2
public class PostController {
  @Autowired
  private PostService postService;

  @Autowired
  private HttpServletRequest request;
  @Autowired
  private PostRepository postRepository;

  @Qualifier("postLikeServiceImpl")
  @Autowired
  private PostLikeService postLikeService;

  @Qualifier("postHateServiceImpl")
  @Autowired
  private PostHateService postHateService;

  @Autowired
  private PostLikeRepository postLikeRepository;

  @Autowired
  private PostHateRepository postHateRepository;

  @Autowired
  private CommentRepository commentRepository;

  @Autowired
  private CommentService commentService;

  @Autowired
  private CommentLikeService commentLikeService;

  @Autowired
  private SimulationService simulationService;

  @Autowired
  private SimulationRepository simulationRepository;


  @Autowired
  private PhotoUtil photoUtil;


    //    @PostMapping("/create")
  //    public ResponseEntity<String> createPost(@RequestBody PostDto postDto) {
  //        // DTO를 엔터티로 변환 후 저장 로직
  //        Post post = convertDtoToEntity(postDto);
  //
  //        // 비밀번호, 닉네임, IP 주소 설정
  //        post.setPassword(postDto.getPassword());
  //        post.setNickname(postDto.getNickname());
  //        post.setIpAddress(request.getRemoteAddr());
  //
  //        postService.savePost(post);
  //        return new ResponseEntity<>("게시글이 생성되었습니다.", HttpStatus.CREATED);
  //    }


  // 파일 업로드
//  @PostMapping("/upload")
//  public ModelAndView upload(MultipartHttpServletRequest request) {
//    ModelAndView mav = new ModelAndView("jsonView");
//
//    String uploadPath = photoUtil.ckUpload(request);
//
//    mav.addObject("uploaded", true);
//    mav.addObject("url", uploadPath);
//    return mav;
//  }
  @PostMapping("/upload")
  public ModelAndView upload(MultipartHttpServletRequest request) {
      ModelAndView mav = new ModelAndView("jsonView");

      try {

        MultipartFile file = request.getFile("upload");
        if (file != null) {
          String uploadPath = photoUtil.uploadToFirebase(file);
          mav.addObject("uploaded", true);
          mav.addObject("url", uploadPath);
        } else {
          mav.addObject("uploaded", false);
          mav.addObject("error", "업로드할 파일을 찾을 수 없습니다.");
        }
      } catch (Exception e) {
        mav.addObject("uploaded", false);
        mav.addObject("error", "Failed to upload image.");
        e.printStackTrace();
        mav.addObject("error", "파일 업로드에 실패했습니다: " + e.getMessage());
      }

      return mav;
  }


  @PostMapping("/uploadMovie")
  public ModelAndView uploadMovie(MultipartHttpServletRequest request) {
    ModelAndView mav = new ModelAndView("jsonView");


      MultipartFile file = request.getFile("upload");
      if (file != null) {
        String uploadPath = photoUtil.uploadToFirebase(file); // uploadToFirebase 메서드를 uploadVideoToFirebase로 수정
        mav.addObject("uploaded", true);
        mav.addObject("url", uploadPath);
      }

    return mav;
  }

  //  @PostMapping("/uploadMovie")
  //  public ModelAndView uploadMovie(MultipartHttpServletRequest request) {
  //    ModelAndView mav = new ModelAndView("jsonView");
  //
  //    try {
  //
  //      MultipartFile file = request.getFile("upload");
  //      if (file != null) {
  //        String uploadPath = photoUtil.uploadVideoToFirebase(file); // uploadToFirebase 메서드를 uploadVideoToFirebase로 수정
  //        mav.addObject("uploaded", true);
  //        mav.addObject("url", uploadPath);
  //      } else {
  //        mav.addObject("uploaded", false);
  //        mav.addObject("error", "업로드할 파일을 찾을 수 없습니다.");
  //      }
  //    } catch (Exception e) {
  //      mav.addObject("uploaded", false);
  //      mav.addObject("error", "Failed to upload video.");
  //      e.printStackTrace();
  //      mav.addObject("error", "파일 업로드에 실패했습니다: " + e.getMessage());
  //    }
  //
  //    return mav;
  //  }


  /* ---------------------------------- submit 영역 시작 ------------------------------------*/

  @PostMapping("/submitForm/{type}")
  public String submitForm(@ModelAttribute("post") Post post, HttpServletRequest request, @PathVariable String type,
                           @RequestParam(value = "nextATagClass1", required = false) String nextATagClass1,
                           @RequestParam(value = "nextATagClass2", required = false) String nextATagClass2,
                           @RequestParam(value = "nextATagClass3", required = false) String nextATagClass3,
                           @RequestParam(value = "nextATagClass4", required = false) String nextATagClass4,
                           @RequestParam(value = "nextATagClass5", required = false) String nextATagClass5,
                           @RequestParam(value = "nextATagClass6", required = false) String nextATagClass6,
                           @RequestParam(value = "nextATagClass7", required = false) String nextATagClass7) {
    // 닉네임, 비밀번호 설정
    post.setNickname(post.getNickname());
    post.setPassword(post.getPassword());

    // IP 주소 설정
    String ipAddress = getClientIP(request);
    post.setIpAddress(ipAddress);

    // 한국 시간대로 설정
    ZoneId koreaZone = ZoneId.of("Asia/Seoul");
    LocalDateTime createtime = LocalDateTime.now(koreaZone);


    // 글 작성일
    post.setCreatetime(createtime);

    post.setType(type);
    // title과 content는 HTML 폼에서의 매핑을 기다립니다.

    // 저장
    postRepository.save(post);

    // 다른 로직 또는 리다이렉션 등...
    // 글 작성이 성공하면 readPost 페이지로 리다이렉션
    if(type.equals("movie")){
      return "redirect:/post/read/movie/" + post.getId();
    } else if (type.equals("poll")) {
      return "redirect:/post/read/poll/" + post.getId();
    } else if (type.equals("simulation")) {
      System.out.println("클래스 값 1: " + nextATagClass1);
      System.out.println("클래스 값 2: " + nextATagClass2);
      System.out.println("클래스 값 3: " + nextATagClass3);
      System.out.println("클래스 값 4: " + nextATagClass4);
      System.out.println("클래스 값 5: " + nextATagClass5);
      System.out.println("클래스 값 6: " + nextATagClass6);
      System.out.println("클래스 값 7: " + nextATagClass7);
      System.out.println("postid:" +  post.getId());
      simulationService.submitSimulation(post.getId(), nextATagClass1, nextATagClass2, nextATagClass3,
              nextATagClass4, nextATagClass5, nextATagClass6,
              nextATagClass7);

      return "redirect:/post/read/simulation/" + post.getId();

    } else if (type.equals("roulette")) {
      return "redirect:/post/read/roulette/" + post.getId();
    } else{
      return "redirect:/post/read/default/" + post.getId();
    }
  }


//  @PostMapping("/simulSubmit")
//  public String simulSubmit(@ModelAttribute("post") Post post, HttpServletRequest request) {
//    // 닉네임, 비밀번호 설정
//    post.setNickname(post.getNickname());
//    post.setPassword(post.getPassword());
//
//    // IP 주소 설정
//    String ipAddress = getClientIP(request);
//    post.setIpAddress(ipAddress);
//
//    // 글 작성일
//    LocalDateTime createtime = LocalDateTime.now();
//    post.setCreatetime(createtime);
//
//    // title과 content는 HTML 폼에서의 매핑을 기다립니다.
//
//    // 저장
//    postRepository.save(post);
//
//    // 다른 로직 또는 리다이렉션 등...
//    // 글 작성이 성공하면 readPost 페이지로 리다이렉션
//    return "redirect:/post/simulRead/" + post.getId();
//  }

//  @PostMapping("/rouletteSubmit")
//  public String rouletteSubmit(@ModelAttribute("post") Post post, HttpServletRequest request) {
//    // 닉네임, 비밀번호 설정
//    post.setNickname(post.getNickname());
//    post.setPassword(post.getPassword());
//
//    // IP 주소 설정
//    String ipAddress = getClientIP(request);
//    post.setIpAddress(ipAddress);
//
//    // 글 작성일
//    LocalDateTime createtime = LocalDateTime.now();
//    post.setCreatetime(createtime);
//
//    // title과 content는 HTML 폼에서의 매핑을 기다립니다.
//
//    // 저장
//    postRepository.save(post);
//
//    // 다른 로직 또는 리다이렉션 등...
//    // 글 작성이 성공하면 readPost 페이지로 리다이렉션
//    return "redirect:/post/rouletteRead/" + post.getId();
//  }


//  @PostMapping("/pollSubmit")
//  public String pollSubmit(@ModelAttribute("post") Post post, HttpServletRequest request) {
//    // 닉네임, 비밀번호 설정
//    post.setNickname(post.getNickname());
//    post.setPassword(post.getPassword());
//
//    // IP 주소 설정
//    String ipAddress = getClientIP(request);
//    post.setIpAddress(ipAddress);
//
//    // 글 작성일
//    LocalDateTime createtime = LocalDateTime.now();
//    post.setCreatetime(createtime);
//
//    // title과 content는 HTML 폼에서의 매핑을 기다립니다.
//
//    // 저장
//    postRepository.save(post);
//
//    // 다른 로직 또는 리다이렉션 등...
//    // 글 작성이 성공하면 readPost 페이지로 리다이렉션
//    return "redirect:/post/pollRead/" + post.getId();
//  }




  /* ---------------------------------- submit 영역 종료 ------------------------------------*/


/* ---------------------------------- createPost 영역 시작 ------------------------------------*/

  @GetMapping("/create")
  public String showForm(Model model) {
    model.addAttribute("post", new Post());
    return "postForm";
  }
//  @GetMapping("/simulCreate")
//  public String simulCreate(Model model) {
//    model.addAttribute("post", new Post());
//    return "simulCreate";
//  }
@GetMapping("/simulCreate")
public String simulCreate(Model model,
                          @RequestParam("championName") String championName,
                          @RequestParam("item1") String item1,
                          @RequestParam("item2") String item2,
                          @RequestParam("item3") String item3,
                          @RequestParam("item4") String item4,
                          @RequestParam("item5") String item5,
                          @RequestParam("item6") String item6) {

  model.addAttribute("post", new Post());
  model.addAttribute("championName", championName);
  model.addAttribute("item1", item1);
  model.addAttribute("item2", item2);
  model.addAttribute("item3", item3);
  model.addAttribute("item4", item4);
  model.addAttribute("item5", item5);
  model.addAttribute("item6", item6);
  String championImageUrl = simulationService.generateChampionImageUrl(championName);
//  System.out.println(championImageUrl);
  model.addAttribute("championImageUrl", championImageUrl);

  String itme1ImageUrl = simulationService.generateItemImageUrl(item1);
  model.addAttribute("itme1ImageUrl", itme1ImageUrl);
  String itme2ImageUrl = simulationService.generateItemImageUrl(item2);
  model.addAttribute("itme2ImageUrl", itme2ImageUrl);
  String itme3ImageUrl = simulationService.generateItemImageUrl(item3);
  model.addAttribute("itme3ImageUrl", itme3ImageUrl);
  String itme4ImageUrl = simulationService.generateItemImageUrl(item4);
  model.addAttribute("itme4ImageUrl", itme4ImageUrl);
  String itme5ImageUrl = simulationService.generateItemImageUrl(item5);
  model.addAttribute("itme5ImageUrl", itme5ImageUrl);
  String itme6ImageUrl = simulationService.generateItemImageUrl(item6);
  model.addAttribute("itme6ImageUrl", itme6ImageUrl);

  return "simulCreate"; // 예시로 성공 응답 반환
}
  @GetMapping("/simulRead")
  public String simulRead(Model model) {
    model.addAttribute("post", new Post());
    return "simulRead";
  }

  @GetMapping("/rouletteCreate")
  public String rouletteCreate(Model model) {
    model.addAttribute("post", new Post());
    return "rouletteCreate";
  }

  @GetMapping("/movieCreate")
  public String movieCreate(Model model) {
    model.addAttribute("post", new Post());
    return "movieCreate";
  }

  @GetMapping("/pollCreate")
  public String pollCreate(Model model) {
    model.addAttribute("post", new Post());
    return "pollCreate";
  }


/* ---------------------------------- createPost 영역 끝 ------------------------------------*/





/* ---------------------------------- list 영역 시작 ------------------------------------*/
  @GetMapping("/list/{type}")
  public String getPostList(@PathVariable String type,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(name = "sort", required = false, defaultValue = "id") String sort,
                            Model model) {
      Page<Post> paging = this.postService.getSortedPosts(page, sort, type);

      // 페이지에서 포스트 목록을 가져옵니다.
      List<Post> posts = paging.getContent();

      // 각 포스트에 대해 isImageIncluded 값을 설정합니다.
      for (Post post : posts) {
        if (post.getContent() != null && post.getContent().contains("img")) {
          post.setIsImageIncluded(true);
        }
        int commentCount = commentService.countCommentsByPostId(post.getId());
        post.setCommentCount(commentCount);
      }

      model.addAttribute("paging", paging);
      model.addAttribute("sort", sort);
      model.addAttribute("type", type);
//    model.addAttribute("searchPaging", null);

    return "postList";
  }



  // 검색
//  @GetMapping("/list/search")
//  public String getPostSearchList(@RequestParam(defaultValue = "0") int page,
//                                  @RequestParam("option") String type,
//                                  @RequestParam("keyword") String keyword,
//                                  Model model) {
//    System.out.println(type);
//    System.out.println(keyword);
//    Page<Post> paging = postService.searchPosts(type ,keyword, page);
//
//    List<Post> posts = paging.getContent();
//
//    // 각 포스트에 대해 isImageIncluded 값을 설정합니다.
//    for (Post post : posts) {
//      if (post.getContent() != null && post.getContent().contains("img")) {
//        post.setIsImageIncluded(true);
//      }
//      int commentCount = commentService.countCommentsByPostId(post.getId());
//      post.setCommentCount(commentCount);
//    }
//
//    model.addAttribute("searchPaging", paging);
//    model.addAttribute("type", type);
//    model.addAttribute("keyword", keyword);
//
//    System.out.println(paging);
//    return "postSearchList";
//  }
  @GetMapping("/list/search/{type}")
  public String getPostSearchList(@PathVariable String type,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam("option") String option,
                                  @RequestParam("keyword") String keyword,
                                  @RequestParam(name = "sort", required = false, defaultValue = "id") String sort,
                                  Model model) {
    System.out.println(option);
    System.out.println(keyword);
    Page<Post> paging = postService.searchPosts(type, option, keyword, page, sort);

    if (paging != null && paging.hasContent()) {
      List<Post> posts = paging.getContent();

      // 각 포스트에 대해 isImageIncluded 값을 설정합니다.
      for (Post post : posts) {
        if (post.getContent() != null && post.getContent().contains("img")) {
          post.setIsImageIncluded(true);
        }
        int commentCount = commentService.countCommentsByPostId(post.getId());
        post.setCommentCount(commentCount);
      }

      model.addAttribute("searchPaging", paging);
      model.addAttribute("option", option);
      model.addAttribute("keyword", keyword);
      model.addAttribute("have", true); // 검색 결과가 있는 경우
      model.addAttribute("sort", sort);
      model.addAttribute("type", type);

    } else {
      model.addAttribute("searchPaging", paging);
      model.addAttribute("option", option);
      model.addAttribute("keyword", keyword);
      model.addAttribute("have", false); // 검색 결과가 없는 경우
      model.addAttribute("sort", sort);
      model.addAttribute("type", type);
    }

    System.out.println(paging);
    return "postSearchList";
  }


  /* ---------------------------------- readPost 영역 시작 ------------------------------------*/


  @GetMapping("/read/{type}/{postId}")
  public String readPost(@PathVariable String type, @PathVariable Long postId, Model model, HttpServletRequest request, HttpSession session,
                         @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                         @RequestParam(name = "sort", required = false, defaultValue = "id") String sort) {
    // 게시글 조회 시간을 세션에 저장하여 같은 세션에서는 하루에 한 번만 조회 가능하도록 함
    String sessionKey = "postViewTime_" + postId;
    LocalDateTime lastViewTime = (LocalDateTime) session.getAttribute(sessionKey);

// 한국 시간대로 설정
    ZoneId koreaZone = ZoneId.of("Asia/Seoul");
    LocalDateTime currentTime = LocalDateTime.now(koreaZone);

    // 마지막 조회 시간이 없거나 오늘 처음 조회한 경우에만 조회수를 증가시킴
    if (lastViewTime == null || lastViewTime.toLocalDate().isBefore(currentTime.toLocalDate())) {
      // 조회수 증가
      postService.incrementViews(postId);

      // 세션에 마지막 조회 시간 저장
      session.setAttribute(sessionKey, currentTime);
    }

    // postId에 해당하는 게시글 정보를 가져옴
    PostDto postDto = postService.getPostDtoById(postId);

    if (postDto == null) {
      // 게시글이 없을 경우 예외처리
      // 여기서는 단순하게 "게시글이 없습니다."를 반환하도록 하겠습니다.
      return "게시글이 없습니다.";
    }

    // 해당 게시글에 연결된 댓글 정보를 가져옴
    List<Comment> commentDtoList = commentService.getCommentsByPostId(postId);
    List<CommentLikeInfo> commentLikeInfoList = new ArrayList<>();

    String clientIpAddress = getClientIP(request);
    log.info("client IP " + clientIpAddress);
    for (Comment comment : commentDtoList) {
      boolean isLiked = commentLikeService.isCommentLikedByIp(comment.getId(), clientIpAddress);
      commentLikeInfoList.add(new CommentLikeInfo(comment, isLiked));
    }

    // 사용자가 해당 게시글을 좋아요했는지 여부를 가져옴
    boolean isPostLiked = postLikeService.isPostLikedByIp(postId, clientIpAddress);
    boolean isPostHated = postHateService.isPostHatedByIp(postId, clientIpAddress);

    // 모델에 댓글 정보와 게시글 정보를 추가하여 게시글 읽기 페이지로 반환
    model.addAttribute("comment", commentLikeInfoList);
    model.addAttribute("post", postDto);
    model.addAttribute("page", page);
    model.addAttribute("sort", sort);
    model.addAttribute("type", type);


    model.addAttribute("isPostLiked", isPostLiked); // 게시글 좋아요 상태를 모델에 추가
    model.addAttribute("isPostHated", isPostHated); // 게시글 싫어요 상태를 모델에 추가

    if (type.equals("movie")) {
      return "movieRead";
    } else if (type.equals("poll")) {
      return "pollRead";
    } else if (type.equals("simulation")) {
      SimulationDTO simulationDTO = simulationService.getSimulationDtoById(postId);
      System.out.println("simulationDTO ::: "+ simulationDTO);
        model.addAttribute("simulation", simulationDTO);

      return "simulRead";
    } else if (type.equals("roulette")) {
      return "rouletteRead";
    } else {
      return "readPost";
    }
  }

  @GetMapping("/{postId}/like/status")
  public ResponseEntity<Boolean> getLikeStatus(@PathVariable Long postId, HttpServletRequest request) {
    String ipAddress = getClientIP(request);
    boolean isLiked = postLikeService.isPostLikedByIp(postId, ipAddress);
    return ResponseEntity.ok(isLiked);
  }

  @GetMapping("/{postId}/hate/status")
  public ResponseEntity<Boolean> getHateStatus(@PathVariable Long postId, HttpServletRequest request) {
    String ipAddress = getClientIP(request);
    boolean isHated = postHateService.isPostHatedByIp(postId, ipAddress);
    return ResponseEntity.ok(isHated);
  }



  //  @GetMapping("/simulRead/{postId}")
//  public String simulRead(@PathVariable Long postId, Model model, HttpServletRequest request, HttpSession session,
//                             @RequestParam(name = "page", required = false, defaultValue = "0") int page,
//                             @RequestParam(name = "sort", required = false, defaultValue = "id") String sort) {
//    // 게시글 조회 시간을 세션에 저장하여 같은 세션에서는 하루에 한 번만 조회 가능하도록 함
//    String sessionKey = "postViewTime_" + postId;
//    LocalDateTime lastViewTime = (LocalDateTime) session.getAttribute(sessionKey);
//    LocalDateTime currentTime = LocalDateTime.now();
//
//    // 마지막 조회 시간이 없거나 오늘 처음 조회한 경우에만 조회수를 증가시킴
//    if (lastViewTime == null || lastViewTime.toLocalDate().isBefore(currentTime.toLocalDate())) {
//      // 조회수 증가
//      postService.incrementViews(postId);
//
//      // 세션에 마지막 조회 시간 저장
//      session.setAttribute(sessionKey, currentTime);
//    }
//
//    // postId에 해당하는 게시글 정보를 가져옴
//    PostDto postDto = postService.getPostDtoById(postId);
//
//    if (postDto == null) {
//      // 게시글이 없을 경우 예외처리
//      // 여기서는 단순하게 "게시글이 없습니다."를 반환하도록 하겠습니다.
//      return "게시글이 없습니다.";
//    }
//
//    // 해당 게시글에 연결된 댓글 정보를 가져옴
//    List<Comment> commentDtoList = commentService.getCommentsByPostId(postId);
//    List<CommentLikeInfo> commentLikeInfoList = new ArrayList<>();
//
//    String clientIpAddress = getClientIP(request);
//    log.info("client IP " +  clientIpAddress);
//    for (Comment comment : commentDtoList) {
//      boolean isLiked = commentLikeService.isCommentLikedByIp(comment.getId(), clientIpAddress);
//      commentLikeInfoList.add(new CommentLikeInfo(comment, isLiked));
//    }
//
//    // 모델에 댓글 정보와 게시글 정보를 추가하여 게시글 읽기 페이지로 반환
//    model.addAttribute("comment", commentLikeInfoList);
//    model.addAttribute("post", postDto);
//    model.addAttribute("page", page);
//    model.addAttribute("sort", sort);
//    return "simulRead";
//  }


//  @GetMapping("/rouletteRead/{postId}")
//  public String rouletteRead(@PathVariable Long postId, Model model, HttpServletRequest request, HttpSession session,
//                         @RequestParam(name = "page", required = false, defaultValue = "0") int page,
//                         @RequestParam(name = "sort", required = false, defaultValue = "id") String sort) {
//    // 게시글 조회 시간을 세션에 저장하여 같은 세션에서는 하루에 한 번만 조회 가능하도록 함
//    String sessionKey = "postViewTime_" + postId;
//    LocalDateTime lastViewTime = (LocalDateTime) session.getAttribute(sessionKey);
//    LocalDateTime currentTime = LocalDateTime.now();
//
//    // 마지막 조회 시간이 없거나 오늘 처음 조회한 경우에만 조회수를 증가시킴
//    if (lastViewTime == null || lastViewTime.toLocalDate().isBefore(currentTime.toLocalDate())) {
//      // 조회수 증가
//      postService.incrementViews(postId);
//
//      // 세션에 마지막 조회 시간 저장
//      session.setAttribute(sessionKey, currentTime);
//    }
//
//    // postId에 해당하는 게시글 정보를 가져옴
//    PostDto postDto = postService.getPostDtoById(postId);
//
//    if (postDto == null) {
//      // 게시글이 없을 경우 예외처리
//      // 여기서는 단순하게 "게시글이 없습니다."를 반환하도록 하겠습니다.
//      return "게시글이 없습니다.";
//    }
//
//    // 해당 게시글에 연결된 댓글 정보를 가져옴
//    List<Comment> commentDtoList = commentService.getCommentsByPostId(postId);
//    List<CommentLikeInfo> commentLikeInfoList = new ArrayList<>();
//
//    String clientIpAddress = getClientIP(request);
//    log.info("client IP " +  clientIpAddress);
//    for (Comment comment : commentDtoList) {
//      boolean isLiked = commentLikeService.isCommentLikedByIp(comment.getId(), clientIpAddress);
//      commentLikeInfoList.add(new CommentLikeInfo(comment, isLiked));
//    }
//
//    // 모델에 댓글 정보와 게시글 정보를 추가하여 게시글 읽기 페이지로 반환
//    model.addAttribute("comment", commentLikeInfoList);
//    model.addAttribute("post", postDto);
//    model.addAttribute("page", page);
//    model.addAttribute("sort", sort);
//    return "rouletteRead";
//  }


//  @GetMapping("/pollRead/{postId}")
//  public String pollRead(@PathVariable Long postId, Model model, HttpServletRequest request, HttpSession session,
//                             @RequestParam(name = "page", required = false, defaultValue = "0") int page,
//                             @RequestParam(name = "sort", required = false, defaultValue = "id") String sort) {
//    // 게시글 조회 시간을 세션에 저장하여 같은 세션에서는 하루에 한 번만 조회 가능하도록 함
//    String sessionKey = "postViewTime_" + postId;
//    LocalDateTime lastViewTime = (LocalDateTime) session.getAttribute(sessionKey);
//    LocalDateTime currentTime = LocalDateTime.now();
//
//    // 마지막 조회 시간이 없거나 오늘 처음 조회한 경우에만 조회수를 증가시킴
//    if (lastViewTime == null || lastViewTime.toLocalDate().isBefore(currentTime.toLocalDate())) {
//      // 조회수 증가
//      postService.incrementViews(postId);
//
//      // 세션에 마지막 조회 시간 저장
//      session.setAttribute(sessionKey, currentTime);
//    }
//
//    // postId에 해당하는 게시글 정보를 가져옴
//    PostDto postDto = postService.getPostDtoById(postId);
//
//    if (postDto == null) {
//      // 게시글이 없을 경우 예외처리
//      // 여기서는 단순하게 "게시글이 없습니다."를 반환하도록 하겠습니다.
//      return "게시글이 없습니다.";
//    }
//
//    // 해당 게시글에 연결된 댓글 정보를 가져옴
//    List<Comment> commentDtoList = commentService.getCommentsByPostId(postId);
//    List<CommentLikeInfo> commentLikeInfoList = new ArrayList<>();
//
//    String clientIpAddress = getClientIP(request);
//    log.info("client IP " +  clientIpAddress);
//    for (Comment comment : commentDtoList) {
//      boolean isLiked = commentLikeService.isCommentLikedByIp(comment.getId(), clientIpAddress);
//      commentLikeInfoList.add(new CommentLikeInfo(comment, isLiked));
//    }
//
//    // 모델에 댓글 정보와 게시글 정보를 추가하여 게시글 읽기 페이지로 반환
//    model.addAttribute("comment", commentLikeInfoList);
//    model.addAttribute("post", postDto);
//    model.addAttribute("page", page);
//    model.addAttribute("sort", sort);
//    return "pollRead";
//  }

  // search read 작업 type이랑 keyword가 필요해서 따로팜
  @GetMapping("/readSearch/{type}/{postId}")
  public String readSearchPost(@PathVariable String type, @PathVariable Long postId, Model model, HttpServletRequest request, HttpSession session,
                               @RequestParam(name = "page", required = false, defaultValue = "0") int page, @RequestParam("option") String option,
                               @RequestParam("keyword") String keyword) {
    // 게시글 조회 시간을 세션에 저장하여 같은 세션에서는 하루에 한 번만 조회 가능하도록 함
    String sessionKey = "postViewTime_" + postId;
    LocalDateTime lastViewTime = (LocalDateTime) session.getAttribute(sessionKey);

// 한국 시간대로 설정
    ZoneId koreaZone = ZoneId.of("Asia/Seoul");
    LocalDateTime currentTime = LocalDateTime.now(koreaZone);

    // 마지막 조회 시간이 없거나 오늘 처음 조회한 경우에만 조회수를 증가시킴
    if (lastViewTime == null || lastViewTime.toLocalDate().isBefore(currentTime.toLocalDate())) {
      // 조회수 증가
      postService.incrementViews(postId);

      // 세션에 마지막 조회 시간 저장
      session.setAttribute(sessionKey, currentTime);
    }

    // postId에 해당하는 게시글 정보를 가져옴
    PostDto postDto = postService.getPostDtoById(postId);

    if (postDto == null) {
      // 게시글이 없을 경우 예외처리
      // 여기서는 단순하게 "게시글이 없습니다."를 반환하도록 하겠습니다.
      return "게시글이 없습니다.";
    }

    // 해당 게시글에 연결된 댓글 정보를 가져옴
    List<Comment> commentDtoList = commentService.getCommentsByPostId(postId);
    List<CommentLikeInfo> commentLikeInfoList = new ArrayList<>();

    String clientIpAddress = getClientIP(request);
    log.info("client IP " +  clientIpAddress);
    for (Comment comment : commentDtoList) {
      boolean isLiked = commentLikeService.isCommentLikedByIp(comment.getId(), clientIpAddress);
      commentLikeInfoList.add(new CommentLikeInfo(comment, isLiked));
    }


    // 모델에 댓글 정보와 게시글 정보를 추가하여 게시글 읽기 페이지로 반환
    model.addAttribute("comment", commentLikeInfoList);
    model.addAttribute("post", postDto);
    model.addAttribute("page", page);
    model.addAttribute("type", type);
    model.addAttribute("keyword", keyword);
    model.addAttribute("option", option);

//    model.addAttribute("option", option);
    if (type.equals("movie")) {
      return "movieSearchPost";
    } else if (type.equals("poll")) {
      return "pollSearchPost";
    } else if (type.equals("simulation")) {
      SimulationDTO simulationDTO = simulationService.getSimulationDtoById(postId);
      System.out.println("simulationDTO ::: "+ simulationDTO);
      model.addAttribute("simulation", simulationDTO);

      return "simulSearchPost";
    } else if (type.equals("roulette")) {
      return "rouletteSearchPost";
    } else {
      return "readPost";
    }
//    return "readSearchPost";
  }

  // 게시글 좋아요
  @PostMapping("/{postId}/like")
  public ResponseEntity<Boolean> addOrRemoveLike(@PathVariable Long postId, @RequestBody Map<String, String> requestBody) {
    String ipAddress = getClientIP(request);

    Post post = postRepository.findById(postId).orElse(null);

    if (post == null) {
      return ResponseEntity.notFound().build();
    }

    PostLike existingLike = postLikeRepository.findByPostIdAndIpAddress(postId, ipAddress);
    PostHate existingHate = postHateRepository.findByPostIdAndIpAddress(postId, ipAddress);

    if (existingLike != null) {
      // 이미 좋아요를 눌렀으면 삭제
      postLikeRepository.delete(existingLike);
      return ResponseEntity.ok(false); // Like removed
    } else {
      // 좋아요 추가
      PostLike newLike = new PostLike();
      newLike.setPost(post);
      newLike.setIpAddress(ipAddress);
      postLikeRepository.save(newLike);
      return ResponseEntity.ok(true); // Hate added
    }

  }
  // 게시글 싫어요
  @PostMapping("/{postId}/hate")
  public ResponseEntity<Boolean> addOrRemoveHate(@PathVariable Long postId, @RequestBody Map<String, String> requestBody) {
    String ipAddress = getClientIP(request);

    Post post = postRepository.findById(postId).orElse(null);

    if (post == null) {
      return ResponseEntity.notFound().build();
    }

    PostHate existingHate = postHateRepository.findByPostIdAndIpAddress(postId, ipAddress);

    if (existingHate != null) {
      // 이미 싫어요를 눌렀으면 삭제
      postHateRepository.delete(existingHate);
      return ResponseEntity.ok(false); // Like removed
    } else {
      // 싫어요 추가
      PostHate newHate = new PostHate();
      newHate.setPost(post);
      newHate.setIpAddress(ipAddress);
      postHateRepository.save(newHate);
      return ResponseEntity.ok(true); // Like added
    }
  }

  // 게시글 좋아요 수 카운트
  @GetMapping("/{postId}/like/count")
  public ResponseEntity<Integer> getPostLikeCount(@PathVariable Long postId) {
    int likeCount = postService.getPostLikeCount(postId);
    return ResponseEntity.ok(likeCount);
  }

  // 게시글 싫어요 수 카운트
  @GetMapping("/{postId}/hate/count")
  public ResponseEntity<Integer> getPostHateCount(@PathVariable Long postId) {
    int hateCount = postService.getPostHateCount(postId);
    return ResponseEntity.ok(hateCount);
  }




/* ---------------------------------- readPost 영역 끝 ------------------------------------*/




/* ---------------------------------- modify 영역 시작 ------------------------------------*/


  // 게시글 수정 페이지로 이동하는 요청 처리 메소드
  @GetMapping("/modify/{postId}/{type}")
  public ModelAndView getModifyPostPage(@PathVariable("postId") Long postId, @PathVariable String type, Model model, HttpServletRequest request) {
    // 인터넷 주소창에서 직접 입력한 요청인지 확인
    String referer = request.getHeader("referer");
    if (referer == null || referer.isEmpty()) {
      // 인터넷 주소창에서 직접 입력한 요청일 경우에만 404 페이지로 리디렉션
      return new ModelAndView("404");
    } else {

      // 게시글 정보를 가져와서 모델에 담기
      Optional<Post> postOptional = postService.getPostById(postId);

      if (postOptional.isPresent()) {
        Post post = postOptional.get();
        model.addAttribute("post", post);
        model.addAttribute("type", type);

        if(type.equals("movie")){
          return new ModelAndView("movieUpdate");
        } else if (type.equals("poll")) {
          return new ModelAndView("pollUpdate");
        } else if (type.equals("simulation")){
          return new ModelAndView("simulUpdate");
        } else if (type.equals("roulette")) {
          return new ModelAndView("rouletteUpdate");
        } else{
          return new ModelAndView("modifyPost"); // 수정 페이지로 이동
        }
      } else {
        // 게시글이 없을 경우 예외처리
        // 여기서는 단순하게 "게시글이 없습니다."를 반환하도록 하겠습니다.
        return new ModelAndView("게시글이 없습니다.");
      }
    }
  }

  // 게시글 비밀번호 확인
  @PostMapping("/checkPassword")
  @ResponseBody
  public String checkPassword(@RequestBody PasswordRequest request) {
    Long postId = request.getPostId();
    String password = request.getPassword();
    // postId와 password를 이용하여 게시글의 비밀번호를 확인하는 로직 작성
    boolean passwordMatch = postService.checkPassword(postId, password);
    return String.valueOf(passwordMatch);
  }

  // 비밀번호 확인 요청의 요청 본문을 받기 위한 클래스
  @Getter
  @Setter
  static class PasswordRequest {
    private Long postId;
    private String password;
    private String title;
    private String nickname;
  }


  // 게시글 수정 처리 메소드
  @PostMapping("/updatePost/{postId}/{type}")
  public ResponseEntity<String> updatePost(@PathVariable Long postId, @PathVariable String type, @RequestBody PostDto postDto) {
    // 한국 시간대로 설정
    ZoneId koreaZone = ZoneId.of("Asia/Seoul");

    // 글 작성일
    postDto.setCreatetime(LocalDateTime.now(koreaZone));
    postService.updatePost(postId, postDto);
    return ResponseEntity.ok("게시글이 성공적으로 업데이트되었습니다.");
  }



  // 삭제 요청 처리하는 메소드
  @DeleteMapping("/delete/{postId}")
  public ResponseEntity<String> deleteArticle(@PathVariable long postId, @RequestBody Map<String, String> requestBody) {
    String password = requestBody.get("password");

    // 게시글에 연결된 좋아요와 싫어요를 먼저 삭제
    postService.deletePost(postId, password);

    // 삭제가 성공적으로 이루어지면 HTTP 상태 코드 200 OK를 반환합니다.
    return ResponseEntity.ok("게시글이 성공적으로 삭제되었습니다.");
  }


  private Post convertDtoToEntity(PostDto postDto) {
    Post post = new Post();
    post.setTitle(postDto.getTitle());
    post.setContent(postDto.getContent());
    return post;
  }


  // 클라이언트의 실제 IP 주소를 가져오는 메서드
  private String getClientIP(HttpServletRequest request) {
    String ipAddress = request.getHeader("X-Forwarded-For");
    if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
      ipAddress = request.getHeader("Proxy-Client-IP");
    }
    if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
      ipAddress = request.getHeader("WL-Proxy-Client-IP");
    }
    if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
      ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
    }
    if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
      ipAddress = request.getRemoteAddr();
    }
    if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)){
      ipAddress = request.getHeader("HTTP_CLIENT_IP");
    }
    return ipAddress;
  }


};