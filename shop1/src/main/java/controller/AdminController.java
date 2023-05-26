package controller;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.websocket.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import exception.LoginException;
import logic.ShopService;
import logic.User;

/*
 * AdminController의 모든 메서서는 관리자 로그인이 필요함
 *  AOP로 설정 AdminLoginAspect.adminCheck() 메서드 
 *   1. 로그아웃 상태 => 로그인 하세여. login 페이지 이동
 *   2.관리자 로그인이 아닌 경우   
 * 
 */

@Controller
@RequestMapping("admin") // 요청이 admin으로 들어오면 실행해라
public class AdminController {
	@Autowired
	private ShopService service;

//	
//	@GetMapping("*") 
//	public ModelAndView join() {
//		ModelAndView mav = new ModelAndView();
//		mav.addObject(new User());
//		return mav;
//	}
	@RequestMapping("list")
	public ModelAndView adminChecklist(String sort, HttpSession Session) {
		ModelAndView mav = new ModelAndView();
		// list : db에 등록 된 모든 회원 정보를 저장하고 있는 목록
		List<User> list = service.list(); // 회원 목록을 조회해라
		if (sort != null) {
			switch (sort) {
			case "10":
				Collections.sort(list, new Comparator<User>() {
					@Override
					public int compare(User u1, User u2) {
						return u1.getUserid().compareTo(u2.getUserid()); // 결과가 음수가 나오면 u1, 양수가 나오면 u2
					}
				}); // 기존 방식 코등
				break;

			case "11":
				Collections.sort(list, (u1, u2) -> u2.getUserid().compareTo(u1.getUserid())); // 람다식
				break;

			case "20":
				Collections.sort(list, (u1, u2) -> u1.getUsername().compareTo(u2.getUsername())); // 람다식
				break;

			case "21":
				Collections.sort(list, (u1, u2) -> u2.getUsername().compareTo(u1.getUsername())); // 람다식
				break;
			case "30":
				Collections.sort(list, (u1, u2) -> u1.getUsername().compareTo(u2.getPhoneno())); // 람다식
				break;
			case "31":
				Collections.sort(list, (u1, u2) -> u2.getPhoneno().compareTo(u1.getPhoneno())); // 람다식
				break;
			case "40":
				Collections.sort(list, (u1, u2) -> u1.getBirthday().compareTo(u2.getBirthday())); // 람다식
				break;
			case "41":
				Collections.sort(list, (u1, u2) -> u2.getBirthday().compareTo(u1.getBirthday())); // 람다식
				break;
			case "50":
				Collections.sort(list, (u1, u2) -> u1.getEmail().compareTo(u2.getEmail())); // 람다식
				break;
			case "51":
				Collections.sort(list, (u1, u2) -> u2.getEmail().compareTo(u1.getEmail())); // 람다식
				break;
			}
		}
		mav.addObject("list", list);
		return mav;
	}
	@RequestMapping("mailForm") 
	public ModelAndView mailForm(String[] idchks, HttpSession session) {
		//String[] idchks : idchks 파라미터 값 여러가 가능 . request.getParamaterValues("파라미터")
		//String로 받으면 에러는 안나지만 , 로 받아옴?
		ModelAndView mav = new ModelAndView("admin/mail");
		if(idchks == null || idchks.length == 0) {
			throw new LoginException("메일을 보낼 대상자를 선택하세요","list");
		}
		List<User> list = service.getUserList(idchks);
		mav.addObject("list", list);
		return mav;
	}
 
}
