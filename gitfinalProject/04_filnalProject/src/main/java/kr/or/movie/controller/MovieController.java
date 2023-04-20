package kr.or.movie.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.SessionScope;

import com.google.gson.Gson;

import kr.or.member.model.vo.Member;
import kr.or.movie.model.service.MovieService;
import kr.or.movie.model.vo.Movie;
import kr.or.movie.model.vo.MovieFile;
import kr.or.movie.model.vo.MoviePost;
import kr.or.movie.model.vo.MovieVideo;
import kr.or.movie.model.vo.Review;
import kr.or.movie.model.vo.ReviewLike;
import kr.or.movie.model.vo.ReviewPageData;
import kr.or.movie.model.vo.ReviewWatch;
import kr.or.movie.model.vo.WatchPoint;

@Controller
public class MovieController {
	@Autowired
	private MovieService service;
	
	@RequestMapping(value="/allMovieList.do")
	public String allMovieList(Model model) {
		
		ArrayList<Movie> list = service.selectMovieAll();
		model.addAttribute("list", list);
		return "movie/movieAllList";
	}
	
	@RequestMapping(value="/movieDetail.do")
	public String detailMovie(int movieNo,int reqPage, Model model) {
		Movie mov = service.selectOneMovie(movieNo);		//1, 7(1.영화정보 / 7.영화포스터file)
		model.addAttribute("mov", mov);
		ReviewPageData rpd=service.selectReviewList(movieNo,reqPage);//(2.페이징된 리뷰리스트)
		model.addAttribute("pageList",rpd.getList());
		model.addAttribute("pageNavi",rpd.getPageNavi());
		//3번없음
	
		//4번없음
		
		//모든 관람평(review)조회하기()
		ArrayList<Review> reviewList = service.oneMovieAllReview(movieNo);//(5.관람포인트 / 리뷰 수정 위한 리뷰조회)
		model.addAttribute("reviewList",reviewList);
		//관람포인트별 합산점수조회
		WatchPoint watchPointSum=service.watchPointSum(movieNo);//(5-1.관람포인트 합산)
		model.addAttribute("watchPointSum", watchPointSum);
		
		//영화 비디오
		ArrayList<MovieVideo> mvList = service.selectOneMovieVideo(movieNo);//(6.영화비디오조회)
		model.addAttribute("mvList", mvList);
		
		//8.리뷰좋아요 없음
		//9.예매율 없음
		//10.누적관객수 없음
		//11.영화 좋아요 없음
		
		//실관람평점산출위한 watchPoint조회
		Review watchPointAvg = service.onlyWatchPointAvg(movieNo);//(12.실관람평점영화평점)
		model.addAttribute("watchPointAvg",watchPointAvg);
		
		//영화별 관람평 갯수를 위한 조회
		int reviewListCount= service.selectReviewListCount(movieNo);
		model.addAttribute("reviewListCount", reviewListCount);
		//페이징 처리
		
		
		
		
		return "movie/movieDetail";
	}
	//무비포스트 작성 폼으로 이동
	@RequestMapping(value="/moviePostFrm.do")
	public String moviePostFrm(int movieNo,Model model) {
		//movie_file조회해오기
		ArrayList<MovieFile> movieFileAll=service.selectMovieFileAll(movieNo);
		model.addAttribute("movieFileAll",movieFileAll);
		ArrayList<MovieVideo> mvList = service.selectOneMovieVideo(movieNo);
		model.addAttribute("mvList", mvList);
		Movie mov = service.selectOneMovie(movieNo);
		model.addAttribute("mov", mov);
		
		return "movie/moviePostFrm";
	}
	//실관람평 인서트
	@RequestMapping(value="/watchPointInsert.do")
	public String reviewInsert(Review rev,WatchPoint wPoint) {
		int result = service.reviewInsert(rev,wPoint);

		return "redirect:/movieDetail.do?movieNo="+rev.getMovieNo()+"&reqPage=1";
	}
	//실관람평 수정하기
	@RequestMapping(value="/watchPointUpdate.do")
	public String reviewUpdate(Review rev, WatchPoint wPoint) {
		int result=service.reviewUpdate(rev);
		int wresult=service.watchPointUpdate(wPoint); 
		return "redirect:/movieDetail.do?movieNo="+rev.getMovieNo()+"&reqPage=1";
		
		
	}
	//실관람평 삭제하기
	@RequestMapping(value="/deleteReview.do")
	public String reviewDelete(int reviewCommentNo,int movieNo) {
		int result = service.deleteReview(reviewCommentNo);
		int wresult = service.deleteWatchPoint(reviewCommentNo);
		return  "redirect:/movieDetail.do?movieNo="+movieNo+"&reqPage=1";
	}
	/*
	@ResponseBody
	@RequestMapping(value="/reviewLikeInsert.do", produces = "application/json;charset=utf-8")
	public String reviewLikeInsert(int reviewCommentNo, String memberId) {
		int result = service.reviewLikeInsert();
		return memberId;
		
	}
	 */
	//무비포스트 등록
	@RequestMapping(value="/moviePostInsert.do")
	public String moviePostInsert(MoviePost post) {
		System.out.println(post);
		int result = service.postInsert(post);
		
		return "redirect:/movieDetail.do?movieNo="+post.getMovieNo()+"&reqPage=1";
		
	}
	
	
}
