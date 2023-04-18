package kr.or.admin.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import common.FileManager;
import kr.or.admin.model.service.AdminService;
import kr.or.admin.model.vo.Schedule;
import kr.or.admin.model.vo.Theater;
import kr.or.movie.model.service.MovieService;
import kr.or.movie.model.vo.Movie;
import kr.or.movie.model.vo.MovieFile;
import kr.or.movie.model.vo.MovieVideo;

@Controller
public class AdminController {
	@Autowired
	private AdminService service;
	@Autowired
	private MovieService movieServie;
	@Autowired
	private FileManager fileManager;

	@RequestMapping("/adminPage.do")
	public String adminPage() {
		return "admin/adminPage";
	}

	@RequestMapping(value = "/registerMovieFrm.do")
	public String registerMovieFrm() {
		return "admin/registerMovieFrm";
	}

	@RequestMapping(value = "/registerMovie.do")
	public String registerMovie(Movie movie, String movieVideo[], MultipartFile movieMain, MultipartFile[] moviePoster,
			HttpServletRequest request) {
		MovieFile mainFile = new MovieFile();
		ArrayList<MovieFile> postList = new ArrayList<MovieFile>();

		// 파일 서버에 업로드
		if (movieMain != null && !moviePoster[0].isEmpty()) {
			String savePath = request.getSession().getServletContext().getRealPath("/resources/upload/movie/");

			String mainFilename = movieMain.getOriginalFilename();
			String mainFilepath = fileManager.upload(savePath, movieMain);

			mainFile.setMovieFileName(mainFilename);
			mainFile.setMovieFilePath(mainFilepath);

			for (MultipartFile file : moviePoster) {
				String filename = file.getOriginalFilename();
				String filepath = fileManager.upload(savePath, file);

				MovieFile postFile = new MovieFile();

				postFile.setMovieFileName(filename);
				postFile.setMovieFilePath(filepath);

				postList.add(postFile);
			}

		}

		ArrayList<MovieVideo> videoList = new ArrayList<MovieVideo>();

		for (String link : movieVideo) {
			MovieVideo video = new MovieVideo();

			video.setVideoLink(link);

			videoList.add(video);
		}

		int result = service.insertMovie(movie, mainFile, postList, videoList);

		if (result == (postList.size() + 2 + videoList.size())) { // mainfile insert + postfile insert + board insert +
																	// insert movieVideo[]
			return "admin/registerMovieFrm";
		} else {
			return "redirect:/";
		}
	}

	@RequestMapping(value = "/MovieList.do")
	public String MovieList(Model model) {
		ArrayList<Movie> list = service.selectMovieList();

		model.addAttribute("list", list);

		return "admin/movieList";
	}

	@RequestMapping(value = "/updateMovieFrm.do")
	public String updateMovieFrm(int movieNo, Model model) {
		Movie movie = service.selectOneUpdateMovie(movieNo);

		model.addAttribute("movie", movie);

		return "admin/updateMovieFrm";
	}

	@RequestMapping(value = "/updateMovie.do")
	public String updateMovie(Movie movie, int[] fileNo, String[] filepath, int[] videoNo, MultipartFile movieMain,
			MultipartFile[] moviePoster, String movieVideo[], HttpServletRequest request) {
		MovieFile mainFile = new MovieFile();
		ArrayList<MovieFile> postList = new ArrayList<MovieFile>();
		ArrayList<MovieVideo> videoList = new ArrayList<MovieVideo>();

		String savePath = request.getSession().getServletContext().getRealPath("/resources/upload/movie/");

		// 파일 서버에 업로드
		if (movieMain != null && !moviePoster[0].isEmpty()) {
			String mainFilename = movieMain.getOriginalFilename();
			String mainFilepath = fileManager.upload(savePath, movieMain);

			mainFile.setMovieFileName(mainFilename);
			mainFile.setMovieFilePath(mainFilepath);

			for (MultipartFile file : moviePoster) {
				String filename = file.getOriginalFilename();
				String upfilepath = fileManager.upload(savePath, file);

				MovieFile postFile = new MovieFile();

				postFile.setMovieFileName(filename);
				postFile.setMovieFilePath(upfilepath);

				postList.add(postFile);
			}
		}

		if (movieVideo != null) {
			for (String link : movieVideo) {
				MovieVideo video = new MovieVideo();

				video.setVideoLink(link);

				videoList.add(video);
			}
		}

		// board: board 내용 업데이트, fileList 사진 업데이트, fileNo 삭제
		int result = service.movieUpdate(movie, mainFile, postList, fileNo, videoList, videoNo);
		// 파일도 삭제 비디오도 삭제
		if (fileNo != null && videoNo != null && (result == (postList.size() + fileNo.length + 2 + videoList.size()))) {
			for (String delFile : filepath) {
				boolean delResult = fileManager.deleteFile(savePath, delFile);

				if (delResult) {
					System.out.println("파일 삭제 성공");
				} else {
					System.out.println("파일 삭제 실패");
				}
			}

			return "redirect:/updateMovieFrm.do?movieNo=" + movie.getMovieNo();
			// 파일만 삭제
		} else if (fileNo != null && (result == (postList.size() + fileNo.length + 2))) {
			for (String delFile : filepath) {
				boolean delResult = fileManager.deleteFile(savePath, delFile);

				if (delResult) {
					System.out.println("파일 삭제 성공");
				} else {
					System.out.println("파일 삭제 실패");
				}
			}

			return "redirect:/updateMovieFrm.do?movieNo=" + movie.getMovieNo();
		}

		// 파일이 DB에 삭제 되야 실제 물리적 파일도 제거
		if (fileNo != null && (result == (postList.size() + fileNo.length + 2))) { // 2는 movieFile +
																					// movie
			for (String delFile : filepath) {
				boolean delResult = fileManager.deleteFile(savePath, delFile);

				if (delResult) {
					System.out.println("파일 삭제 성공");
				} else {
					System.out.println("파일 삭제 실패");
				}
			}

			return "redirect:/updateMovieFrm.do?movieNo=" + movie.getMovieNo();
		} else if (fileNo == null && (result == (postList.size() + 1 + videoList.size()))) { // 파일을 삭제안하고 파일만 첨부하거나 첨부
																								// 안하면
			return "redirect:/updateMovieFrm.do?movieNo=" + movie.getMovieNo();
		} else {
			return "ridirect:/";
		}
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////승훈

	@RequestMapping(value = "/deleteMovie.do")
	public String deleteMovie(int movieNo, HttpServletRequest request) {
		// db 삭제하고, 서버에 업로드 되있는 파일을 지우기위해 파일 목록을 가져옴
		ArrayList<MovieFile> list = service.delteMovie(movieNo);

		if (list == null) {
			return "redirect:/";
		} else { // 파일 목록 가져오면
			String savePath = request.getSession().getServletContext().getRealPath("/resources/upload/movie/");

			for (MovieFile file : list) {
				boolean deleteResult = fileManager.deleteFile(savePath, file.getMovieFilePath());

				if (deleteResult) {
					System.out.println("파일 삭제 성공");
				} else {
					System.out.println("파일 삭제 실패");
				}
			}
		}

		return "redirect:/MovieList.do";
	}

	@RequestMapping(value = "/registerTheaterFrm.do")
	public String registerTheaterFrm(Model model) {
		ArrayList<Theater> list = service.selectTheaterList();
//
		model.addAttribute("list", list);

		return "admin/registerTheaterFrm";
	}

	@RequestMapping(value = "/registerTheater.do")
	public String registerTheater(Theater theater) {
		int result = service.insertTheater(theater);

		if (result > 0) {
			return "admin/registerTheaterFrm";
		} else {
			return "redirect:/";
		}
	}

	@RequestMapping(value = "/theaterList.do")
	public String theaterList(Model model) {
		ArrayList<Theater> list = service.selectTheaterList();

		model.addAttribute("list", list);

		return "/admin/theaterList";
	}

	@RequestMapping(value = "/deleteTheater.do")
	public String deleteTheater(int theaterNo) {
		int result = service.deleteTheater(theaterNo);

		if (result > 0) {
			return "redirect:/theaterList.do";
		} else {
			return "redirect:/";
		}
	}

	@RequestMapping(value = "/updateTheaterFrm.do")
	public String updateTheaterFrm(int theaterNo, Model model) {
		Theater theater = service.selectOntTheater(theaterNo);

		model.addAttribute("theater", theater);

		return "admin/updateTheaterFrm";
	}

	@RequestMapping(value = "/updateTheater.do")
	public String updateTheater(Theater theater) {
		int result = service.updateTheater(theater);

		if (result > 0) {
			return "redirect:/theaterList.do";
		} else {
			return "redirect:/";
		}
	}

	@RequestMapping(value = "/screenSchedule.do")
	public String screenSchedule() {
		return "admin/screenSchedule";
	}

	@RequestMapping(value = "/allTheater.do")
	public String allTheater() {
		return "admin/allTheater";
	}

	@RequestMapping(value = "/selectOneTheater.do") // 임시 (no줘서 이동할거)

	public String selectOneTheater(int theaterNo, Model model) {
		Theater theater = service.selectOntTheater(theaterNo);
		model.addAttribute("theater", theater);
		return "admin/detailTheater";
	}
	/*
	 * @RequestMapping(value = "/detailTheater.do") // 임시 (no줘서 이동할거) public String
	 * detailTheater() { return "admin/detailTheater"; }
	 */

	@ResponseBody
	@RequestMapping(value = "/selectBranchList.do", produces = "application/json;charset=utf-8")
	public String selectBranchList(String theaterLocal) {
		ArrayList<Theater> list = new ArrayList<Theater>();
		list = service.selectBranchList(theaterLocal);
		return new Gson().toJson(list);
	}

	@ResponseBody
	@RequestMapping(value = "/selectScheduleCalendar.do", produces = "application/json;charset=utf-8")
	public String selectScheduleCalendar() {
		List<Schedule> list = service.selectScheduleCalendar();

		Gson gson = new Gson();
		JsonArray jsonArray = new JsonArray();

		for (Schedule schedule : list) {
			JsonObject jsonObject = new JsonObject();

			jsonObject.addProperty("title", schedule.getMovieTitle());
			jsonObject.addProperty("start", schedule.getScheduleStart());
			jsonObject.addProperty("end", schedule.getScheduleEnd());
			jsonObject.addProperty("description", schedule.getTheaterBranch());

			jsonArray.add(jsonObject);
		}

		return gson.toJson(jsonArray);
	}
}
