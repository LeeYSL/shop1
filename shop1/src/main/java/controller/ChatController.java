package controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import logic.ShopService;

@Controller
@RequestMapping("chat")
public class ChatController {
  @RequestMapping("*") 
  public String chat() {
	  return null;
  }
 }