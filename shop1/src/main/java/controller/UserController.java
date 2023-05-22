package controller;

import javax.security.auth.message.callback.PrivateKeyCallback.Request;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.websocket.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import logic.ShopService;
import logic.User;

@Controller
@RequestMapping("user")
public class UserController {
	@Autowired
	private ShopService service;

	@GetMapping("*") // @PostMapping("*") => 컨트롤러에 설정되지 않은 모든 요청시 호출 되는 메서드
	public ModelAndView join() {
		ModelAndView mav = new ModelAndView();
		mav.addObject(new User());
		return mav;

	}

	@PostMapping("join")
	public ModelAndView ueseAdd(@Valid User user, BindingResult bresult) {
		ModelAndView mav = new ModelAndView();
		if (bresult.hasErrors()) {
			mav.getModel().putAll(bresult.getModel());
			// reject 메서드 : global error에 추가
			bresult.reject("error.input.user");
			bresult.reject("error.input.check");
			return mav;
		}
		// 정상 입력갑 : 회원 가입 하기 => db의 useraccount 테이블에 저장
		try {
			service.userInsert(user); // user : 화면에서 입력 되고 있는 내용
			mav.addObject("user", user);
		} catch (DataIntegrityViolationException e) {
			// DataIntegrityViolationException : db에서 중복 key 오류시 발생되는 예외 객체
			e.printStackTrace();
			bresult.reject("error.duplicate.user"); // global 오류 등록
			mav.getModel().putAll(bresult.getModel());
			return mav;
		}
		mav.setViewName("redirect:login");
		return mav;
	}

	@PostMapping("login")
	public ModelAndView login(@Valid User user, BindingResult bresult, HttpSession session) {
		ModelAndView mav = new ModelAndView();
		
		if (bresult.hasErrors()) {
			mav.getModel().putAll(bresult.getModel());
			bresult.reject("error.login.input");
			return mav;
		}
		/*
		 * 1.userid에 맞는 user를 db에서 조회하기
		 */
		try {
			User dbUser = service.selectUserOne(user.getUserid());
		} catch (EmptyResultDataAccessException e) { // 조회된 데이터 없는 경우 발생
			e.printStackTrace();
			bresult.reject("error.login.id"); // id를 확인하세요.
			mav.getModel().putAll(bresult.getModel());
			return mav;
		}
		/*
		 * 2. 비밀번호 검증 일치 : session.setAttribute("loginUser",dbUser) = > 로그인 정보
		 *    불일치 : 비밀번호를 확인하세요. 출력(error.login.password)
		 * 
		 * 3.mypage로 페이지 이동 => 현재는 404오류 발생
		 */
		
		
	User dbUser = service.selectUserOne(user.getUserid());
	if (dbUser.getPassword().equals(user.getPassword())) {
		session.setAttribute("loginUser", dbUser);
//				System.out.println("db:" + dbUser.getPassword());
//			    System.out.println("user:"+user.getPassword());
//			    System.out.println(dbUser.getPassword().equals(user.getPassword()));
//			  
	}else {
			bresult.reject("error.login.pass");
			mav.getModel().putAll(bresult.getModel());
			return mav;

		}
		mav.setViewName("redirect:mypage");
		return mav;
		
		
//		try {
//			User dbUser = service. selectUserpass(user.getUserid(),user.getPassword());
//			User loginUser=(User)session.getAttribute("loginUser");
//			session.setAttribute("loginUser", dbUser);
//		
//		} catch (EmptyResultDataAccessException e) {
//			e.printStackTrace();
//			bresult.reject("error.login.pass");
//			mav.getModel().putAll(bresult.getModel());
//			return mav;
//
//		}
//		mav.setViewName("redirect:mypage");
//		return mav;
//	}
    }
}