package poly.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import poly.dto.BoardDTO;
import poly.dto.FavorDTO;
import poly.dto.PostDTO;
import poly.dto.PostimgDTO;
import poly.dto.UserDTO;
import poly.service.IMypageService;
import poly.service.IPostService;

@Controller
public class MypageController {
	private Logger log = Logger.getLogger(this.getClass());

	@Resource(name = "MypageService")
	private IMypageService mypageService;

	@Resource(name = "PostService")
	private IPostService PostService;
	
	// 사용자 개인정보 수정 페이지
	@RequestMapping(value = "mypage/userModify")
	public String userModify(HttpServletRequest request, Model model, HttpSession session) {
		String userId = (String) session.getAttribute("userId");

		if (userId != null) {
			UserDTO uDTO = new UserDTO();
			try {
				uDTO = mypageService.getUserInfo(userId);

			} catch (Exception e) {
				e.printStackTrace();
			}
			MypageTopMessage(mypageService,model,userId);

			model.addAttribute("uDTO", uDTO);
			return "/mypage/userModify";
		} else {
			model.addAttribute("msg", "로그인이 필요한 서비스 입니다.");
			model.addAttribute("url", "/user/userLogin.do");
			return "/redirect";
		}
	}

	// 사용자 개인정보 수정 실행
	@RequestMapping(value = "mypage/userModifyProc")
	public String userModifyProc(HttpServletRequest request, Model model, HttpSession session) throws Exception {

		String userId = request.getParameter("userId");
		String userPassword = request.getParameter("userPassword");
		String PasswordRepeat = request.getParameter("PasswordRepeat");
		String userName = request.getParameter("userName");
		String userdate = request.getParameter("userDate");
		String userEmail = request.getParameter("userEmail");
		String userGen = request.getParameter("userGen");
		String userTel = request.getParameter("userTel");
		UserDTO uDTO = new UserDTO();

		uDTO.setUserId(userId);
		uDTO.setUserPassword(userPassword);
		uDTO.setUserDate(userdate);
		uDTO.setUserEmail(userEmail);
		uDTO.setUserGen(userGen);
		uDTO.setUserName(userName);
		uDTO.setUserTel(userTel);

		if (!userPassword.equals(PasswordRepeat)) {
			model.addAttribute("msg", "비밀번호 입력이 잘못되었습니다.");
			model.addAttribute("url", "/mypage/userModify.do");
		} else if (userPassword.length() < 8) {
			model.addAttribute("msg", "비밀번호는 8~20자 영문 대 소문자,숫자,특수문자를 사용해 주세요.");
			model.addAttribute("url", "/mypage/userModify.do");
		} else {
			int result = 0;
			try {
				result = mypageService.updateUserInfo(uDTO);
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (result > 0) {
				model.addAttribute("msg", "수정되었습니다.");
				model.addAttribute("url", "/mypage/userModify.do");
			} else {
				model.addAttribute("msg", "수정에 실패했습니다.");
				model.addAttribute("url", "/mypage/userModify.do");
			}
		}

		return "/redirect";
	}

	// 사용자 회원 탈퇴
	@RequestMapping(value = "mypage/userDelete")
	public String userDelete(HttpServletRequest request, Model model) throws Exception {
		String userId = request.getParameter("userId");

		int result = 0;

		try {
			result = mypageService.deleteUserInfo(userId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (result > 0) {
			model.addAttribute("url", "/Logoutbtn.do");
			model.addAttribute("msg", "회원탈퇴에 성공하셨습니다. 60일 이내에 취소 하실 수 있습니다. 취소하시려면 전화 또는 Email로 문의해주세요.");
		} else {
			model.addAttribute("url", "/mypage/main.do");
			model.addAttribute("msg", "삭제에 실패했습니다.");

		}

		return "/redirect";
	}

	// 사용자 마이페이지 메인
	@RequestMapping(value = "mypage/main")
	public String mypageMain(HttpSession session, Model model) {
		String userId = (String) session.getAttribute("userId");
		if (userId != null) {
			MypageTopMessage(mypageService,model,userId);
			return "/mypage/main";
		} else {
			model.addAttribute("msg", "로그인이 필요한 서비스 입니다.");
			model.addAttribute("url", "/user/userLogin.do");
			return "/redirect";
		}

	}

	
	//사용자 탑바 메세지
		public void MypageTopMessage(IMypageService mypageService , Model model, String userId) {
			List<BoardDTO> mList = new ArrayList<>();
			try {
				mList = mypageService.getMessageList(userId);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(mList.isEmpty()) {
				BoardDTO mDTO = new BoardDTO();
				mDTO.setTitle("없음");
				mList.add(mDTO);
			}
			model.addAttribute("mList",mList);
		}
		
		// 사용자 자세 페이지 리스트
		@RequestMapping(value = "MainStart")
		public String MainStart(HttpSession session, Model model) {
			String userId = (String) session.getAttribute("userId");
			if (userId != null) {
				
				List<PostDTO> pList = new ArrayList<>();
				try {
					pList = PostService.getPostList();
				} catch (Exception e) {
					e.printStackTrace();
				}
				model.addAttribute("pList", pList);
				
				MypageTopMessage(mypageService,model,userId);
				return "/main/MainStart";
			} else {
				model.addAttribute("msg", "로그인이 필요한 서비스 입니다.");
				model.addAttribute("url", "/user/userLogin.do");
				return "/redirect";
			}

		}
		
		// 사용자 자세 페이지 상세
		@RequestMapping(value = "MainStart2")
		public String MainStart2(HttpServletRequest request,HttpSession session, Model model) {
			String userId = (String) session.getAttribute("userId");
			String post_name = request.getParameter("post_name");
			
			log.info("자세이름 :" + post_name);
			
	
			
			if (userId != null) {
				PostDTO pDTO = new PostDTO();
				List<PostimgDTO> iList = new ArrayList<>();
				List<PostDTO> pList = new ArrayList<>();
				FavorDTO fDTO = new FavorDTO();
				fDTO.setFavor_id(userId);
				fDTO.setFavor_postname(post_name);			
				
				try {
					pList = PostService.getPostList();
					pDTO = PostService.getPostLast(post_name);
					iList = PostService.getPostLast2(post_name);
					fDTO = PostService.selectFavorInfo(fDTO);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if(fDTO != null) {
					model.addAttribute("pList", pList);
					model.addAttribute("pDTO", pDTO);
					model.addAttribute("iList", iList);
					model.addAttribute("chec", "1");
				}else {
				model.addAttribute("chec", "0");
				model.addAttribute("pList", pList);
				model.addAttribute("pDTO", pDTO);
				model.addAttribute("iList", iList);
				}
				
				MypageTopMessage(mypageService,model,userId);
				return "/main/MainStart2";
			} else {
				model.addAttribute("msg", "로그인이 필요한 서비스 입니다.");
				model.addAttribute("url", "/user/userLogin.do");
				return "/redirect";
			}

		}
		// 사용자 자세 즐겨찾기 추가 버튼
		@RequestMapping(value = "Favoriteadd")
		public String Favoriteadd(HttpServletRequest request,HttpSession session, Model model) {
			String userId = (String) session.getAttribute("userId");
			String post_name = request.getParameter("post_name");
			
			
			FavorDTO fDTO = new FavorDTO();
			
			fDTO.setFavor_id(userId);
			fDTO.setFavor_postname(post_name);
			fDTO.setFavor_confirm("1");
			
			log.info("즐겨찾기 추가 아이디 :" + userId);
			log.info("즐겨찾기 추가 자세이름 :" + post_name);
			
			
			
			int result = 0;
			
			try {
				result = PostService.insertFavorInfo(fDTO);
			}catch (Exception e) {
				e.printStackTrace();
			}
			if (result > 0) {
				model.addAttribute("post_name", post_name);
				return "redirect:/MainStart2.do";
				
			} else {
				model.addAttribute("msg","즐겨찾기가 실패하였습니다.");
				model.addAttribute("url", "/MainStart.do");
				return "/redirect";
			}
		}
		
		// 사용자 자세 즐겨찾기 해제 버튼
		@RequestMapping(value = "Favoriterelease")
		public String Favoriterelease(HttpServletRequest request,HttpSession session, Model model) {		
	
		String userId = (String) session.getAttribute("userId");
		String post_name = request.getParameter("post_name");
			
		FavorDTO fDTO = new FavorDTO();
		
		fDTO.setFavor_id(userId);
		fDTO.setFavor_postname(post_name);			
	
		log.info("즐겨찾기 해제 ID :" + userId);
		log.info("즐겨찾기 해제 자세이름 :" + post_name);
			
		
		int result = 0;
		
		try {
			result = PostService.deleteFavorInfo(fDTO);	
		}
		catch (Exception e){
			e.printStackTrace();
		}
			model.addAttribute("post_name", post_name);
			return "redirect:/MainStart2.do";
			
	}	
			
		// 사용자 자세 즐겨찾기 페이지
		@RequestMapping(value = "Favorite")
		public String Favorite(HttpServletRequest request,HttpSession session, Model model) {	
			String userId = (String) session.getAttribute("userId");
			String favor_id = userId;
			
			if (userId != null) {
				
				List<PostDTO> pList = new ArrayList<>();
			
				try {
					pList = PostService.FavoriteList(favor_id);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				model.addAttribute("pList", pList);
				
				MypageTopMessage(mypageService,model,userId);
				return "/main/Favorite";
			} else {
				model.addAttribute("msg", "로그인이 필요한 서비스 입니다.");
				model.addAttribute("url", "/user/userLogin.do");
				return "/redirect";
		
			
			}

		}
		@RequestMapping(value = "Favorite2")
		public String Favorite2(HttpServletRequest request,HttpSession session, Model model) {
			String userId = (String) session.getAttribute("userId");
			String post_name = request.getParameter("post_name");
			
			log.info("자세이름 :" + post_name);
			
	
			
			if (userId != null) {
				PostDTO pDTO = new PostDTO();
				List<PostimgDTO> iList = new ArrayList<>();
				List<PostDTO> pList = new ArrayList<>();
				FavorDTO fDTO = new FavorDTO();
				fDTO.setFavor_id(userId);
				fDTO.setFavor_postname(post_name);			
				
				try {
					pList = PostService.getPostList();
					pDTO = PostService.getPostLast(post_name);
					iList = PostService.getPostLast2(post_name);
					fDTO = PostService.selectFavorInfo(fDTO);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if(fDTO != null) {
					model.addAttribute("pList", pList);
					model.addAttribute("pDTO", pDTO);
					model.addAttribute("iList", iList);
					model.addAttribute("chec", "1");
				}else {
				model.addAttribute("chec", "0");
				model.addAttribute("pList", pList);
				model.addAttribute("pDTO", pDTO);
				model.addAttribute("iList", iList);
				}
				
				MypageTopMessage(mypageService,model,userId);
				return "/main/Favorite2";
			} else {
				model.addAttribute("msg", "로그인이 필요한 서비스 입니다.");
				model.addAttribute("url", "/user/userLogin.do");
				return "/redirect";
			}

		}
		
			
}
