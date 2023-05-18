package controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import logic.Item;
import logic.ShopService;

@Controller //@Componet + Controller(요청을 받아주는) 기능 
@RequestMapping("item") // http://localhost:8080/shop1/item/list/*
public class ItemController {
       @Autowired //ShopService 객체를 주입
       private ShopService service;
       @RequestMapping("list") // list 라는 요청이 들어오면 메서드 호출
       public ModelAndView list() {
    	   //ModelAndView : Model + view 
    	   //               view에 전송할 데이터 +  view 설정
    	   // view 설정이 안 된 경우 : url 과 동일 => item/list 뷰로 설정
    	   ModelAndView mav = new ModelAndView();
    	   //itemList : item 테이블의 모든 정보를 item 객체에 list로 저장
    	   List<Item> itemList = service.itemList();
    	   mav.addObject("itemList",itemList); //데이터 저장
    	                                       //view : item/list
    	   return mav; // mav : item.list
       }
       
      // 요청 url : http://localhost:8080/shop1/item/detail?id=1
    @RequestMapping("detail")  
	public ModelAndView detail(Integer id) { // 파라미터 id 값이 있으면 자동으로 들어온다. 매개변수가 파라미터. midel2 처럼 request.get-- 이런 거 필요 없음 여기 id랑 아래 id가 같아야 됨?
		// id =id 파라미터 값 
    	// 매개변수 이름과 같은 이름의 파라미터 값을 자동으로 저장함.
		ModelAndView mav = new ModelAndView();
		Item item = service.getItem(id);
		mav.addObject("item",item);
		return mav;
	}
    @RequestMapping("create")
    public ModelAndView create() {
    	ModelAndView mav = new ModelAndView();
    	mav.addObject(new Item());
    	return mav;
    }
    @RequestMapping("register") //유효성 검사
    public ModelAndView register(@Valid Item item,  BindingResult bresult) {  //@Valid 후 반드시 BindingResult 넣어야 됨 bresult : 유효성 검사 한 결과
    	//item의 프로퍼티와 파라미터 값 비교하여 같은 이름의 값을 item 객체에 저장한다.
    	//@Valid : item 객체에 입력 된 내용을 유효성 검사 
    	ModelAndView mav = new ModelAndView("item/create"); //view 이름을 넣어서 살정
    	if(bresult.hasErrors()) { //@Vaild 프로세서에 의해서 입력 데이터 오류가 있는 경우 mav에 있는 모든 결과를 bresult로 넣어라
    		mav.getModel().putAll(bresult.getModel());
    		return mav;
    	}
    	return mav;
    }
	
 }
