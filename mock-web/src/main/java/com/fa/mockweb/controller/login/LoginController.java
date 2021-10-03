package com.fa.mockweb.controller.login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.fa.mockweb.model.LoginRequest;
import com.fa.mockweb.service.LoginService;

@Controller
public class LoginController {
	
	@Autowired
	private LoginService loginService;
	
	@GetMapping("/login")
	public String showLogin(Model theModel) {
		theModel.addAttribute("loginRequest", new LoginRequest());
		
		return "login";
	}
	
	
	@PostMapping("/authentication-user")
	public String processLogin(@ModelAttribute("loginRequest") LoginRequest loginRequest,
								Model theModel, HttpServletRequest request) {
		try {
			String authorization = loginService.excecuteLogin(loginRequest);
			
			if (authorization != null) {
				HttpSession session = request.getSession();
				session.setAttribute("authorization", authorization);
			}
		} catch (RuntimeException e) {
			theModel.addAttribute("error", e.getMessage());
			return "login";
		}
		
		return "redirect:/admin";
	}
	
	
	@GetMapping("/logout")
	public String processLogout(HttpServletRequest request) {
		HttpSession session = request.getSession();
		
		session.invalidate();

		return "redirect:/login?logout";
	}
	
	@GetMapping("/access-denied")
	public String showAccessDenied() {
		
		return "error/access_denied";
	}
}
