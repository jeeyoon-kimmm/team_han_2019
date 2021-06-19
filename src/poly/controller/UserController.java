package poly.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import poly.dto.UserDTO;
import poly.service.IUserService;
import poly.util.CmmUtil;

@Controller
public class UserController {

	private Logger log = Logger.getLogger(this.getClass());
	@Resource(name = "UserService")
	private IUserService userService;

	// 로그인 버튼
	@RequestMapping(value = "Loginbtn")
	public String Loginbtn(HttpServletRequest request, Model model, HttpSession session) throws Exception {
		String userId = request.getParameter("userId");
		String userPassword = request.getParameter("userPassword");

		log.info("userId 확인 :" + userId);
		log.info("userPassword 확인 :" + userPassword);

		UserDTO uDTO = new UserDTO();
		uDTO.setUserId(userId);
		uDTO.setUserPassword(userPassword);
		uDTO = userService.getLogin(uDTO);

		if (uDTO == null) {
			model.addAttribute("msg", "로그인에 실패하셨습니다.");
			model.addAttribute("url", "/user/userLogin.do");
		} else if (uDTO.getUserStat().equals("1")) {
			model.addAttribute("msg", "활동정지된 회원입니다. 전화 또는 Email로 문의해주세요.");
			model.addAttribute("url", "/user/userLogin.do");
		} else if (uDTO.getUserStat().equals("2")) {
			model.addAttribute("msg", "탈퇴된 회원입니다. 탈퇴를 취소 하시려면 전화 또는 Email로 문의해주세요.");
			model.addAttribute("url", "/user/userLogin.do");
		} else if (uDTO.getUserAuthor().equals("1")) {
			model.addAttribute("msg", "관리자 로그인에 성공하셨습니다.");
			model.addAttribute("url", "/index.do");
			log.info("userAuthor 확인 :" + uDTO.getUserAuthor());
			session.setAttribute("userAuthor", uDTO.getUserAuthor());
			session.setAttribute("userId", uDTO.getUserId());
			session.setAttribute("userName", uDTO.getUserName());
		} else {
			model.addAttribute("msg", "로그인에 성공하셨습니다.");
			model.addAttribute("url", "/index.do");
			log.info("userAuthor 확인 :" + uDTO.getUserAuthor());
			session.setAttribute("userAuthor", uDTO.getUserAuthor());
			session.setAttribute("userId", uDTO.getUserId());
			session.setAttribute("userName", uDTO.getUserName());
		}

		return "/redirect";
	}

	// 로그아웃 버튼
	@RequestMapping(value = "Logoutbtn")
	public String Logoutbtn(HttpSession session, Model model) throws Exception {
		session.invalidate();

		model.addAttribute("msg", "로그아웃 하셨습니다.");
		model.addAttribute("url", "/index.do");
		return "/redirect";
	}

	// 메인화면
	@RequestMapping(value = "index")
	public String index() {
		log.info(this.getClass());

		return "/main/index";
	}

	// 문의 페이지
	@RequestMapping(value = "main/Contact")
	public String Contact() {
		log.info(this.getClass());

		return "/main/Contact";
	}

	// 오픈소스 페이지
	@RequestMapping(value = "main/openSource")
	public String openSource() {
		log.info(this.getClass());

		return "/main/openSource";
	}

	// 개인정보취급방침 페이지
	@RequestMapping(value = "main/privatePolicy")
	public String privatePolicy() {
		log.info(this.getClass());

		return "/main/privatePolicy";
	}

	// 이용자약관 페이지
	@RequestMapping(value = "main/userTerms")
	public String userTerms() {
		log.info(this.getClass());

		return "/main/userTerms";
	}

	// 로그인 페이지
	@RequestMapping(value = "user/userLogin")
	public String userLogin(HttpSession session, Model model) {
		String userId = (String) session.getAttribute("userId");
		log.info(this.getClass());
		if (userId == null) {
			return "/user/userLogin";

		} else {
			model.addAttribute("msg", "이미 로그인 하셨습니다.");
			model.addAttribute("url", "/index.do");
			return "/redirect";
		}
	}

	// 회원가입 페이지
	@RequestMapping(value = "user/userReg")
	public String userReg() throws Exception {
		log.info(this.getClass());

		return "/user/userReg";
	}

	// 아이디 중복검사
	@RequestMapping(value = "user/idCheck.do", method = RequestMethod.POST)
	public @ResponseBody String idCheck(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String userid = CmmUtil.nvl(request.getParameter("userid"));
		log.info("userid : " + userid);
		int count = userService.idCheck(userid);
		// String test ="ok";
		log.info("count : " + count);
		// return test;

		if (count == 0) {
			return "0";
		} else {
			return "1";
		}
	}

	// 데이터베이스에 회원을 등록시키기 위한 요청을 받아옴
	@RequestMapping("/userRegProc")
	public String userRgeProc(HttpServletRequest request, Model model) throws Exception {

		String userName = request.getParameter("userName");
		String userId = request.getParameter("userId");
		String userPassword = request.getParameter("userPassword");
		String userEmail = request.getParameter("userEmail");
		String userDate = request.getParameter("userDate");
		String userTel = request.getParameter("userTel");
		String userGen = request.getParameter("userGen");
		String userMarketing = request.getParameter("userMarketing");
		if(request.getParameter("userMarketing")==null) {
			userMarketing = "0";
		}
		

		log.info("userName 확인 : " + userName);
		log.info("userId 확인 : " + userId);
		log.info("userPassword 확인 : " + userPassword);
		log.info("userEmail 확인 : " + userEmail);
		log.info("userDate 확인 : " + userDate);
		log.info("userTel 확인 : " + userTel);
		log.info("userGen 확인 : " + userGen);
		log.info("userMarketing 확인 : " + userMarketing);

		UserDTO uDTO = new UserDTO();
		uDTO.setUserName(userName);
		uDTO.setUserId(userId);
		uDTO.setUserPassword(userPassword);
		uDTO.setUserEmail(userEmail);
		uDTO.setUserDate(userDate);
		uDTO.setUserTel(userTel);
		uDTO.setUserGen(userGen);
		uDTO.setUserMarketing(userMarketing);

		int result = 0;

		result = userService.userRegProc(uDTO);
		log.info("결과값 : " + result);

		if (result == 1) {
			model.addAttribute("msg", "회원가입 성공하셨습니다.");
			model.addAttribute("url", "/index.do");
		} else {
			model.addAttribute("msg", "회원가입 실패하셨습니다.");
			model.addAttribute("url", "/index.do");
		}
		return "/redirect";
	}

	// 아이디,비밀번호 찾기 페이지
	@RequestMapping(value = "userFind")
	public String password() {

		return "/user/userFind";
	}

	// ID찾기 버튼 기능
	@RequestMapping(value = "findID")
	public String findId(HttpServletRequest request, Model model) throws Exception {
		String userName = request.getParameter("IdUserName");// userName 은 DTO와 같게 지정 parameter값은 jsp의 name값이랑 같게 지정
		String userDate = request.getParameter("IdUserDate");
		String userEmail = request.getParameter("IdUserEmail");

		log.info(userName);
		log.info(userDate);
		log.info(userEmail);
		UserDTO uDTO = new UserDTO(); // 보내는 통
		uDTO.setUserName(userName);
		uDTO.setUserDate(userDate);
		uDTO.setUserEmail(userEmail);

		log.info(uDTO.getUserName());
		log.info(uDTO.getUserDate());
		log.info(uDTO.getUserEmail());

		uDTO = userService.getIdInfo(uDTO);

		if (uDTO == null) {
			model.addAttribute("msg", "가입된 아이디가 없습니다.");
			model.addAttribute("url", "/userFind.do");
		} else {
			model.addAttribute("msg", "가입된 아이디는 " + uDTO.getUserId() + " 입니다.");
			model.addAttribute("url", "/userFind.do");
		}

		return "/redirect";
	}

	// PW찾기 버튼 기능/
	@RequestMapping(value = "findPW")
	public String findPW(HttpServletRequest request, Model model) throws Exception {
		String userName = request.getParameter("PwUserName");
		String userDate = request.getParameter("PwUserDate");
		String userId = request.getParameter("PwUserId");
		String userEmail = request.getParameter("PwUserEmail");

		UserDTO uDTO = new UserDTO();
		uDTO.setUserName(userName);
		uDTO.setUserDate(userDate);
		uDTO.setUserId(userId); // setUserEmail()이 2번 실행되어서 이 부분은 setUserId로 바꿔주었음. 콘솔에 log.info가 어떻게 찍히는지 보면서
								// 변수가 제대로 나오나 안 나오나 보면 됨
		uDTO.setUserEmail(userEmail);

		uDTO = userService.getPwInfo(uDTO);

		if (uDTO == null) {
			model.addAttribute("msg", "회원정보가 존재하지 않습니다.");
			model.addAttribute("url", "/userFind.do");
			return "/redirect";
		} else {

			// 10글자 짜리 비밀번호가 NewPw에 들어감
			String NewPw = getNewPw();
			uDTO.setUserId(userId);
			uDTO.setUserPassword(NewPw);
			int result = 0;
			try {
				result = userService.updatePwInfo(uDTO);
			} catch (Exception e) {
				e.printStackTrace();
			}
			model.addAttribute("userEmail", userEmail);
			model.addAttribute("NewPw", NewPw);
			return "redirect:/PwMailSend.do";
		}

	}

	public String getNewPw() throws Exception {
		// 비밀번호 배열을 생성
		char[] charSet = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
				's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

		// 비밀번호를 받기 위한 문자열 버퍼 생성
		// 비밀번호를 담는 틀 생성
		StringBuffer newKey = new StringBuffer();

		// 10번 반복
		for (int i = 0; i < 10; i++) {
			// 비밀번호 배열 길이*랜덤으로 생성된 숫자
			// random() 난수가 복잡하지 않기 떄문에 숫자를 더 복잡하게 해줌
			int idx = (int) (charSet.length * Math.random());
			// 문자열에다가 한글자씩 담는것
			newKey.append(charSet[idx]);
		}

		// 스트링 버퍼를 스트링형태로 바꿔서 반환해줌
		return newKey.toString();
	}

}
