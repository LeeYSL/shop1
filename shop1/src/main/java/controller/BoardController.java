package controller;

import java.net.http.HttpRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import exception.LoginException;
import logic.Board;
import logic.ShopService;

@Controller
@RequestMapping("board")
public class BoardController {
	@Autowired
	private ShopService service;

	@GetMapping("*")
	public ModelAndView write() {
		ModelAndView mav = new ModelAndView();
		mav.addObject(new Board());
		return mav;
	}

	/*
	 * 1. 유효성 검증 2. db의 board 테이블에 내용 저장, 파일 업로드 3. 등록 성공 시 list 재요청 등록 실패 시 write
	 * 재요청
	 */
	@PostMapping("write")
	public ModelAndView writePost(@Valid Board board, BindingResult bresult, HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		mav.addObject(new Board());
		if (bresult.hasErrors()) {
			mav.getModel().putAll(bresult.getModel());
			// bresult.reject("error.input.board");
		}
		String boardid = (String) request.getSession().getAttribute("boardid");
		if (boardid == null)
			boardid = "1";
		request.getSession().setAttribute(boardid, boardid);
		board.setBoardid(boardid);
		service.boardWrite(board, request);
		mav.setViewName("redirect:list?boardid=" + boardid);
		return mav;

	}

	@RequestMapping("list")
	/*
	 * @RequestParam : 파라미터 값을 저장하는 Map 객체 파라미터 값과 객체(현재 내 변수명을) 맵핑해주는 기능을 가진 어노테이션
	 * 1.파라미터 이름과 매개변수 이름이 같은 경우 2.Bean 클래스 객체의 프로퍼티 이름과 파라미터 이름이 같은 경우 3. Map 객체를
	 * 이용하는 경우
	 * 
	 * 검색 기능 추가
	 * 
	 */
	public ModelAndView list(@RequestParam Map<String, String> param, HttpSession session) { // 들어오는 모든 객체를 map객체로 전달하라는
																								// 뜻
		ModelAndView mav = new ModelAndView();
		System.out.println(param);
		Integer pageNum = null;
		if (param.get("pageNum") != null)
			pageNum = Integer.parseInt(param.get("pageNum"));
		String boardid = param.get("boardid");
		String searchtype = param.get("searchtype");
		String searchcontent = param.get("searchcontent");
		if (pageNum == null || pageNum.toString().equals("")) {
			pageNum = 1;
		}
		if (boardid == null || boardid.equals("")) {
			boardid = "1";
		}
		session.setAttribute("boardid", boardid);
		if (searchtype == null || searchcontent == null || searchtype.trim().equals("")
				|| searchcontent.trim().equals("")) { // 하나라도 공백이거나 null 이면
			searchtype = null; // 둘 다 null로 하겠다.
			searchcontent = null; // 둘 다 null로 하겠다.

		}
		String boardName = null;
		switch (boardid) {
		case "1":
			boardName = "공지사항";
			break;
		case "2":
			boardName = "자유게시판";
			break;
		case "3":
			boardName = "Q&A";
			break;

		}
		int limit = 10; // 한 페이지에 보여줄 게시물 건 수
		int listcount = service.boardcount(boardid, searchtype, searchcontent); // 등록 된 게시물 건 수
		// boardlist : 현재 페이지에 보여줄 게시물 목록
		List<Board> boardlist = service.boardlist(pageNum, limit, boardid, searchtype, searchcontent); // boardlist : 현재
																										// 조회 조건에 맞는 게시물
																										// 건 수
		// 페이징 처리를 위한 값
		int maxpage = (int) ((double) listcount / limit + 0.95);// 등록 건수에 따른 최대 페이지
		int startpage = (int) ((pageNum / 10.0 + 0.9) - 1) * 10 + 1;// 페이지의 시작 번호
		int endpage = startpage + 9; // 화면에 보여줄 페이지 끝 번호
		if (endpage > maxpage)
			endpage = maxpage; // 페이지의 끝 번호는 최대 페이지 클 수 없다.
		int boardno = listcount - (pageNum - 1) * limit; // boardno : 화면 출력 시 보여지는 게시물 번호
		String today = new SimpleDateFormat("yyyyMMdd").format(new Date()); // 오늘 날짜를 문자열로 저장
		// 20230530이런 식으로 나온다. list에서 오늘 날짜를 - 없이 yyyyMMdd로 표시 했기 때문에 여기서도 똑같이 맞춰 줘야 된다.
		mav.addObject("boardid", boardid);
		mav.addObject("boardName", boardName);
		mav.addObject("pageNum", pageNum);
		mav.addObject("maxpage", maxpage);
		mav.addObject("startpage", startpage);
		mav.addObject("endpage", endpage);
		mav.addObject("listcount", listcount);
		mav.addObject("boardlist", boardlist);
		mav.addObject("boardno", boardno);
		mav.addObject("today", today);
		return mav;

	}

	@RequestMapping("detail") // get으로 해도 됨
	public ModelAndView detail(Integer num) {
		ModelAndView mav = new ModelAndView();
		Board board = service.getBoard(num); // num 게시판 내용 조회
		service.addReadcnt(num);
		mav.addObject("board", board);
		if (board.getBoardid() == null || board.getBoardid().equals("1"))
			mav.addObject("boardName", "공지사항");
		else if (board.getBoardid().equals("2"))
			mav.addObject("boardName", "자유게시판");
		else if (board.getBoardid().equals("3"))
			mav.addObject("boardName", "QNA");
		return mav;
	}

	@GetMapping("reply")
	public ModelAndView reply(Integer num, HttpSession session) {
		ModelAndView mav = new ModelAndView();
		String boardid = (String) session.getAttribute("boardid");
		Board board = service.getBoard(num);
		mav.addObject("board", board);

		if (boardid == null || boardid.equals("1"))
			mav.addObject("boardName", "공지사항");
		else if (board.getBoardid().equals("2"))
			mav.addObject("boardName", "자유게시판");
		else if (board.getBoardid().equals("3"))
			mav.addObject("boardName", "QNA");
		return mav;

	}

	/*
	 * 1.유효성 검사하기-파라미터 값 저장 - 원글 정보 : num,grp,grplevel,grpstep,boardid - 답글 정보 :
	 * writer,pass,subject,content 2.db에 insert => service.boardReply() -원글의 grpstep
	 * 보다 큰 이미 등록 된 답글의 grpstep 값을 + 1 => boardDao.grpStepAdd() -num :maxNum() + 1
	 * -db에 insert => boardDao.insert() grp : 원글과 동일 grplevel : 원글의 grplevel + 1
	 * grpstep : 원글의 grpstep + 1
	 * 
	 * 3. 등록 성공 : list 페이지 이동 등록 실패 : "답변 등록 시 오류 발생" reply 페이지 이동
	 */

	@PostMapping("reply") 
    public ModelAndView reply(@Valid Board board,BindingResult bresult) {
	ModelAndView mav = new ModelAndView();
	//유효성 검사
	if (bresult.hasErrors()) {
		Board dbboard = service.getBoard(board.getNum()); //원글 정보를 db에서 읽기
		Map<String,Object> map = bresult.getModel();
		Board b = (Board)map.get("board"); // 화면에서 입력 받은 값을 저장한 board 객체
		b.setTitle(dbboard.getTitle());//원글의 제목으로 변경
		mav.getModel().putAll(bresult.getModel());
		return mav;
	}
	try {
		service.boardReply(board);
		mav.setViewName("redirect:list?boardid=" + board.getBoardid());
	}catch (Exception e) {
		e.printStackTrace();
		throw new LoginException("답변 등록시 오류 발생","reply?num="+board.getNum());
	}
	
	
	return mav;
	
  }
}