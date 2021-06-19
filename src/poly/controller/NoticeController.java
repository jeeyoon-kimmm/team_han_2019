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
import poly.dto.NoticeDTO;
import poly.service.IManagerService;
import poly.service.INoticeService;

@Controller
public class NoticeController {
	private Logger log = Logger.getLogger(this.getClass());

	@Resource(name = "NoticeService")
	private INoticeService noticeService;
	@Resource(name = "ManagerService")
	private IManagerService managerService;

	// 사용자 공지사항 페이지
	@RequestMapping(value = "main/NoticePage")
	public String NoticePage(HttpServletRequest request, Model model, HttpSession session) {

		log.info(this.getClass());
		int pageNum = 0;
		String pgNum = request.getParameter("pgNum");
		pageNum = Integer.parseInt(pgNum);
		// 전체 공지사항 갯수 확인
		int cnt = 0;
		try {
			cnt = noticeService.getNoticeCnt();
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.info(cnt);
		List<NoticeDTO> nList = new ArrayList<>();

		if (cnt == 0) {
			NoticeDTO nDTO = new NoticeDTO();
			nDTO.setTitle("없음");
			nList.add(nDTO);
		} else {
			int iNum = (pageNum - 1) * 10;

			try {
				nList = noticeService.getNoticeList(iNum);
			} catch (Exception e) {
				e.printStackTrace();
			}
			for (int i = 0; i < nList.size(); i++) {
				nList.get(i).setiNum(cnt - i - iNum);
			}

		}

		model.addAttribute("nList", nList);
		model.addAttribute("cnt", cnt);
		model.addAttribute("pgNum", pageNum);
		return "/main/Notice/NoticeList";
	}

	// 사용자 공지사항 이전 페이지
	@RequestMapping(value = "main/NoticePreviousPage")
	public String NoticePreviousPage(HttpServletRequest request, Model model, HttpSession session) {
		int pageNum = 0;
		String pgNum = request.getParameter("pgNum");
		pageNum = Integer.parseInt(pgNum);
		pageNum -= 1;
		// 전체 공지사항 갯수 확인
		int cnt = 0;
		try {
			cnt = noticeService.getNoticeCnt();
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.info(cnt);
		List<NoticeDTO> nList = new ArrayList<>();

		if (cnt == 0) {
			NoticeDTO nDTO = new NoticeDTO();
			nDTO.setTitle("없음");
			nList.add(nDTO);
		} else {
			int iNum = (pageNum - 1) * 10;

			try {
				nList = noticeService.getNoticeList(iNum);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		model.addAttribute("nList", nList);
		model.addAttribute("cnt", cnt);
		model.addAttribute("pgNum", pageNum);
		return "/main/Notice/NoticeList";

	}

	// 사용자 공지사항 다음 페이지
	@RequestMapping(value = "main/NoticeNextPage")
	public String NoticeNextPage(HttpServletRequest request, Model model, HttpSession session) {
		int pageNum = 0;
		String pgNum = request.getParameter("pgNum");
		pageNum = Integer.parseInt(pgNum);
		pageNum += 1;
		// 전체 공지사항 갯수 확인
		int cnt = 0;
		try {
			cnt = noticeService.getNoticeCnt();
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.info(cnt);
		List<NoticeDTO> nList = new ArrayList<>();

		if (cnt == 0) {
			NoticeDTO nDTO = new NoticeDTO();
			nDTO.setTitle("없음");
			nList.add(nDTO);
		} else {
			int iNum = (pageNum - 1) * 10;

			try {
				nList = noticeService.getNoticeList(iNum);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		model.addAttribute("nList", nList);
		model.addAttribute("cnt", cnt);
		model.addAttribute("pgNum", pageNum);
		return "/main/Notice/NoticeList";
	}

	// 사용자 공지사항 자세히보기
	@RequestMapping(value = "main/NoticeDetail")
	public String NoticeDetailUser(HttpServletRequest request, Model model, HttpSession session) {
		int iNum = 0;
		iNum = Integer.parseInt(request.getParameter("iNum"));
		int cnt = 0;
		try {
			cnt = noticeService.getNoticeCnt();
		} catch (Exception e) {
			e.printStackTrace();
		}
		int pgNum = (cnt - iNum) / 10 + 1;
		int iNum2 = iNum - 1;
		NoticeDTO nDTO = new NoticeDTO();
		try {
			nDTO = noticeService.getNoticeDetail(iNum2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (nDTO == null) {
			model.addAttribute("msg", "공지사항이 없습니다.");
			model.addAttribute("url", "/main/?pgNum=1");
			return "redirect";
		} else {
			model.addAttribute("nDTO", nDTO);
			model.addAttribute("iNum", iNum);
			model.addAttribute("pgNum", pgNum);
			return "/main/Notice/NoticeDetail";
		}
	}

	// 사용자 공지사항 자세히보기 이전버튼
	@RequestMapping(value = "main/NoticePreviousDetail")
	public String NoticePreviousDetail(HttpServletRequest request, Model model, HttpSession session) {
		int iNum = 0;
		iNum = Integer.parseInt(request.getParameter("iNum"));
		int cnt = 0;
		try {
			cnt = noticeService.getNoticeCnt();
		} catch (Exception e) {
			e.printStackTrace();
		}
		int pgNum = (cnt - iNum) / 10 + 1;
		iNum += 1;
		if (iNum > cnt) {
			model.addAttribute("msg", "첫번째 공지사항입니다.");
			model.addAttribute("url", "/main/?pgNum=1");
			return "redirect";
		}
		int iNum2 = iNum - 1;

		NoticeDTO nDTO = new NoticeDTO();
		try {
			nDTO = noticeService.getNoticeDetail(iNum2);
		} catch (Exception e) {
			e.printStackTrace();
		}

		model.addAttribute("nDTO", nDTO);
		model.addAttribute("iNum", iNum);
		model.addAttribute("pgNum", pgNum);
		return "/main/Notice/NoticeDetail";

	}

	// 사용자 공지사항 자세히보기 다음버튼
	@RequestMapping(value = "main/NoticeNextDetail")
	public String NoticeNextDetail(HttpServletRequest request, Model model, HttpSession session) {
		int iNum = 0;
		iNum = Integer.parseInt(request.getParameter("iNum"));
		int cnt = 0;
		try {
			cnt = noticeService.getNoticeCnt();
		} catch (Exception e) {
			e.printStackTrace();
		}
		int pgNum = (cnt - iNum) / 10 + 1;
		iNum -= 1;
		if (iNum <= 0) {
			model.addAttribute("msg", "마지막 공지사항입니다.");
			model.addAttribute("url", "/main/?pgNum=" + pgNum);
			return "redirect";
		}
		int iNum2 = iNum - 1;
		NoticeDTO nDTO = new NoticeDTO();
		try {
			nDTO = noticeService.getNoticeDetail(iNum2);
		} catch (Exception e) {
			e.printStackTrace();
		}

		model.addAttribute("nDTO", nDTO);
		model.addAttribute("iNum", iNum);
		model.addAttribute("pgNum", pgNum);
		return "/main/Notice/NoticeDetail";

	}

	// 관리자 공지사항 목록
	@RequestMapping(value = "manager/NoticeList")
	public String NoticeList(HttpServletRequest request, Model model, HttpSession session) {
		String userId = (String) session.getAttribute("userId");
		String userAuthor = (String) session.getAttribute("userAuthor");
		log.info(this.getClass());

		if (userId != null) {
			if (userAuthor.equals("1")) {
				log.info(this.getClass());
				int pageNum = 0;
				String pgNum = request.getParameter("pgNum");
				pageNum = Integer.parseInt(pgNum);
				// 전체 공지사항 갯수 확인
				int cnt = 0;
				try {
					cnt = noticeService.getNoticeCnt();
				} catch (Exception e) {
					e.printStackTrace();
				}
				log.info(cnt);
				List<NoticeDTO> nList = new ArrayList<>();

				if (cnt == 0) {
					NoticeDTO nDTO = new NoticeDTO();
					nDTO.setTitle("없음");
					nList.add(nDTO);
				} else {
					int iNum = (pageNum - 1) * 10;

					try {
						nList = noticeService.getNoticeList(iNum);
					} catch (Exception e) {
						e.printStackTrace();
					}
					for (int i = 0; i < nList.size(); i++) {
						nList.get(i).setiNum(cnt - i - iNum);
					}

				}
				ManagerTopMessage(managerService, model);
				model.addAttribute("nList", nList);
				model.addAttribute("cnt", cnt);
				model.addAttribute("pgNum", pageNum);
				return "/manager/Notice/NoticeList";
			} else {
				model.addAttribute("msg", "관리자 권한이 필요합니다.");
				model.addAttribute("url", "/mypage/main.do");
				return "/redirect";
			}

		} else {
			model.addAttribute("msg", "로그인이 필요한 서비스 입니다.");
			model.addAttribute("url", "/user/userLogin.do");
			return "/redirect";
		}

	}

	// 관리자 공지사항 목록 다음페이지
	@RequestMapping(value = "/manager/NoticeNextPage")
	public String ManagerNoticeNextPage(HttpServletRequest request, Model model, HttpSession session) {
		String userId = (String) session.getAttribute("userId");
		String userAuthor = (String) session.getAttribute("userAuthor");
		log.info(this.getClass());

		if (userId != null) {
			if (userAuthor.equals("1")) {
				log.info(this.getClass());
				int pageNum = 0;
				String pgNum = request.getParameter("pgNum");
				pageNum = Integer.parseInt(pgNum);
				pageNum += 1;
				// 전체 공지사항 갯수 확인
				int cnt = 0;
				try {
					cnt = noticeService.getNoticeCnt();
				} catch (Exception e) {
					e.printStackTrace();
				}
				log.info(cnt);
				List<NoticeDTO> nList = new ArrayList<>();

				if (cnt == 0) {
					NoticeDTO nDTO = new NoticeDTO();
					nDTO.setTitle("없음");
					nList.add(nDTO);
				} else {
					int iNum = (pageNum - 1) * 10;

					try {
						nList = noticeService.getNoticeList(iNum);
					} catch (Exception e) {
						e.printStackTrace();
					}
					for (int i = 0; i < nList.size(); i++) {
						nList.get(i).setiNum(cnt - i - iNum);
						log.info(nList.get(i).getiNum());
					}

				}
				ManagerTopMessage(managerService, model);
				model.addAttribute("nList", nList);
				model.addAttribute("cnt", cnt);
				model.addAttribute("pgNum", pageNum);
				return "/manager/Notice/NoticeList";
			} else {
				model.addAttribute("msg", "관리자 권한이 필요합니다.");
				model.addAttribute("url", "/mypage/main.do");
				return "/redirect";
			}

		} else {
			model.addAttribute("msg", "로그인이 필요한 서비스 입니다.");
			model.addAttribute("url", "/user/userLogin.do");
			return "/redirect";
		}

	}

	// 관리자 공지사항 목록 이전페이지
	@RequestMapping(value = "/manager/NoticePreviousPage")
	public String ManagerNoticePreviousPage(HttpServletRequest request, Model model, HttpSession session) {
		String userId = (String) session.getAttribute("userId");
		String userAuthor = (String) session.getAttribute("userAuthor");
		log.info(this.getClass());

		if (userId != null) {
			if (userAuthor.equals("1")) {
				log.info(this.getClass());
				int pageNum = 0;
				String pgNum = request.getParameter("pgNum");
				pageNum = Integer.parseInt(pgNum);
				pageNum -= 1;
				// 전체 공지사항 갯수 확인
				int cnt = 0;
				try {
					cnt = noticeService.getNoticeCnt();
				} catch (Exception e) {
					e.printStackTrace();
				}
				log.info(cnt);
				List<NoticeDTO> nList = new ArrayList<>();

				if (cnt == 0) {
					NoticeDTO nDTO = new NoticeDTO();
					nDTO.setTitle("없음");
					nList.add(nDTO);
				} else {
					int iNum = (pageNum - 1) * 10;

					try {
						nList = noticeService.getNoticeList(iNum);
					} catch (Exception e) {
						e.printStackTrace();
					}
					for (int i = 0; i < nList.size(); i++) {
						nList.get(i).setiNum(cnt - i - iNum);
						log.info(nList.get(i).getiNum());
					}

				}
				ManagerTopMessage(managerService, model);
				model.addAttribute("nList", nList);
				model.addAttribute("cnt", cnt);
				model.addAttribute("pgNum", pageNum);
				return "/manager/Notice/NoticeList";
			} else {
				model.addAttribute("msg", "관리자 권한이 필요합니다.");
				model.addAttribute("url", "/mypage/main.do");
				return "/redirect";
			}

		} else {
			model.addAttribute("msg", "로그인이 필요한 서비스 입니다.");
			model.addAttribute("url", "/user/userLogin.do");
			return "/redirect";
		}

	}

	// 관리자 공지사항 등록 페이지
	@RequestMapping(value = "manager/NoticeReg")
	public String NoticeReg(Model model, HttpSession session) {
		String userId = (String) session.getAttribute("userId");
		String userAuthor = (String) session.getAttribute("userAuthor");
		if (userId != null) {
			if (userAuthor.equals("1")) {
				ManagerTopMessage(managerService, model);
				return "/manager/Notice/NoticeReg";
			} else {
				model.addAttribute("msg", "관리자 권한이 필요합니다.");
				model.addAttribute("url", "/mypage/main.do");
				return "/redirect";
			}

		} else {
			model.addAttribute("msg", "로그인이 필요한 서비스 입니다.");
			model.addAttribute("url", "/user/userLogin.do");
			return "/redirect";
		}

	}

	// 관리자 공지사항 등록 실행
	@RequestMapping(value = "manager/NoticeRegProc")
	public String NoticeRegProc(HttpServletRequest request, Model model, HttpSession session) throws Exception {
		log.info(this.getClass());
		String title = request.getParameter("title");
		String content = request.getParameter("content");
		String userId = (String) session.getAttribute("userId");

		NoticeDTO nDTO = new NoticeDTO();
		nDTO.setContent(content);
		nDTO.setTitle(title);
		nDTO.setRegId(userId);

		int result = 0;
		try {
			result = noticeService.insertNoticeInfo(nDTO);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (result > 0) {
			model.addAttribute("msg", "공지사항이 등록되었습니다.");
			model.addAttribute("url", "/manager/NoticeList.do?pgNum=1");
		} else {
			model.addAttribute("msg", "공지사항 등록에 실패했습니다.");
			model.addAttribute("url", "/manager/NoticeList.do?pgNum=1");
		}

		return "/redirect";
	}

	// 관리자 공지사항 자세히보기
	@RequestMapping(value = "manager/NoticeDetail")
	public String NoticeDetail(HttpServletRequest request, Model model, HttpSession session) {
		String userId = (String) session.getAttribute("userId");
		String userAuthor = (String) session.getAttribute("userAuthor");
		if (userId != null) {
			if (userAuthor.equals("1")) {
				int iNum = 0;
				iNum = Integer.parseInt(request.getParameter("iNum"));
				int cnt = 0;
				try {
					cnt = noticeService.getNoticeCnt();
				} catch (Exception e) {
					e.printStackTrace();
				}
				int pgNum = (cnt - iNum) / 10 + 1;
				int iNum2 = iNum - 1;
				NoticeDTO nDTO = new NoticeDTO();
				try {
					nDTO = noticeService.getNoticeDetail(iNum2);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (nDTO == null) {
					model.addAttribute("msg", "공지사항이 없습니다.");
					model.addAttribute("url", "/manager/NoticeList.do?pgNum=1");
					return "redirect";
				} else {
					ManagerTopMessage(managerService, model);
					model.addAttribute("nDTO", nDTO);
					model.addAttribute("iNum", iNum);
					model.addAttribute("pgNum", pgNum);
					return "/manager/Notice/NoticeDetail";
				}
			} else {
				model.addAttribute("msg", "관리자 권한이 필요합니다.");
				model.addAttribute("url", "/mypage/main.do");
				return "/redirect";
			}

		} else {
			model.addAttribute("msg", "로그인이 필요한 서비스 입니다.");
			model.addAttribute("url", "/user/userLogin.do");
			return "/redirect";
		}

	}

	// 관리자 공지사항 수정 페이지
	@RequestMapping(value = "manager/NoticeModify")
	public String NoticeModify(HttpServletRequest request, Model model, HttpSession session) {
		String userId = (String) session.getAttribute("userId");
		String userAuthor = (String) session.getAttribute("userAuthor");
		if (userId != null) {
			if (userAuthor.equals("1")) {
				int iNum = 0;
				iNum = Integer.parseInt(request.getParameter("iNum"));
				int iNum2 = iNum - 1;
				NoticeDTO nDTO = new NoticeDTO();
				try {
					nDTO = noticeService.getNoticeDetail(iNum2);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (nDTO == null) {
					model.addAttribute("msg", "공지사항이 없습니다.");
					model.addAttribute("url", "/manager/NoticeList.do?pgNum=1");
					return "redirect";
				} else {
					ManagerTopMessage(managerService, model);
					model.addAttribute("nDTO", nDTO);
					return "/manager/Notice/NoticeModify";
				}

			} else {
				model.addAttribute("msg", "관리자 권한이 필요합니다.");
				model.addAttribute("url", "/mypage/main.do");
				return "/redirect";
			}

		} else {
			model.addAttribute("msg", "로그인이 필요한 서비스 입니다.");
			model.addAttribute("url", "/user/userLogin.do");
			return "/redirect";
		}
	}

	// 관리자 공지사항 수정 실행
	@RequestMapping(value = "manager/NoticeModifyProc")
	public String NoticeModifyProc(HttpServletRequest request, Model model) throws Exception {

		String title = request.getParameter("title");
		String content = request.getParameter("content");
		String seq = request.getParameter("seq");

		NoticeDTO nDTO = new NoticeDTO();
		nDTO.setContent(content);
		nDTO.setSeq(seq);
		nDTO.setTitle(title);

		int result = 0;
		try {
			result = noticeService.updateNoticeInfo(nDTO);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (result > 0) {
			model.addAttribute("msg", "수정되었습니다.");
			model.addAttribute("url", "/manager/NoticeList.do?pgNum=1");
		} else {
			model.addAttribute("msg", "수정에 실패했습니다.");
			model.addAttribute("url", "/manager/NoticeList.do?pgNum=1");
		}

		return "/redirect";
	}

	// 관리자 공지사항 삭제
	@RequestMapping(value = "manager/NoticeDelete")
	public String NoticeDelete(HttpServletRequest request, Model model, HttpSession session) throws Exception {
		String seq = request.getParameter("seq");
		String userId = (String) session.getAttribute("userId");
		String userAuthor = (String) session.getAttribute("userAuthor");
		if (userId != null) {
			if (userAuthor.equals("1")) {
				int result = 0;

				try {
					result = noticeService.deleteNoticeInfo(seq);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (result > 0) {
					model.addAttribute("url", "/manager/NoticeList.do?pgNum=1");
					model.addAttribute("msg", "삭제되었습니다.");
				} else {
					model.addAttribute("url", "/manager/NoticeList.do?pgNum=1");
					model.addAttribute("msg", "삭제에 실패했습니다.");

				}
			} else {
				model.addAttribute("msg", "관리자 권한이 필요합니다.");
				model.addAttribute("url", "/mypage/main.do");
			}
		} else {
			model.addAttribute("msg", "로그인이 필요한 서비스 입니다.");
			model.addAttribute("url", "/user/userLogin.do");
		}
		return "/redirect";
	}

	
	
	
	
	// 사용자 공지사항 페이지
	@RequestMapping(value = "main/GuidePage")
	public String GuidePage(HttpServletRequest request, Model model, HttpSession session) {

		log.info(this.getClass());
		int pageNum = 0;
		String pgNum = request.getParameter("pgNum");
		pageNum = Integer.parseInt(pgNum);
		// 전체 공지사항 갯수 확인
		int cnt = 0;
		try {
			cnt = noticeService.getGuideCnt();
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.info(cnt);
		List<NoticeDTO> nList = new ArrayList<>();

		if (cnt == 0) {
			NoticeDTO nDTO = new NoticeDTO();
			nDTO.setTitle("없음");
			nList.add(nDTO);
		} else {
			int iNum = (pageNum - 1) * 10;

			try {
				nList = noticeService.getGuideList(iNum);
			} catch (Exception e) {
				e.printStackTrace();
			}
			for (int i = 0; i < nList.size(); i++) {
				nList.get(i).setiNum(cnt - i - iNum);
				log.info(nList.get(i).getiNum());
			}

		}

		model.addAttribute("nList", nList);
		model.addAttribute("cnt", cnt);
		model.addAttribute("pgNum", pageNum);
		return "/main/Guide/GuideList";
	}

	// 사용자 공지사항 이전 페이지
	@RequestMapping(value = "main/GuidePreviousPage")
	public String GuidePreviousPage(HttpServletRequest request, Model model, HttpSession session) {
		int pageNum = 0;
		String pgNum = request.getParameter("pgNum");
		pageNum = Integer.parseInt(pgNum);
		pageNum -= 1;
		// 전체 공지사항 갯수 확인
		int cnt = 0;
		try {
			cnt = noticeService.getGuideCnt();
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.info(cnt);
		List<NoticeDTO> nList = new ArrayList<>();

		if (cnt == 0) {
			NoticeDTO nDTO = new NoticeDTO();
			nDTO.setTitle("없음");
			nList.add(nDTO);
		} else {
			int iNum = (pageNum - 1) * 10;

			try {
				nList = noticeService.getGuideList(iNum);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		model.addAttribute("nList", nList);
		model.addAttribute("cnt", cnt);
		model.addAttribute("pgNum", pageNum);
		return "/main/Guide/GuideList";

	}

	// 사용자 공지사항 다음 페이지
	@RequestMapping(value = "main/GuideNextPage")
	public String GuideNextPage(HttpServletRequest request, Model model, HttpSession session) {
		int pageNum = 0;
		String pgNum = request.getParameter("pgNum");
		pageNum = Integer.parseInt(pgNum);
		pageNum += 1;
		// 전체 공지사항 갯수 확인
		int cnt = 0;
		try {
			cnt = noticeService.getGuideCnt();
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.info(cnt);
		List<NoticeDTO> nList = new ArrayList<>();

		if (cnt == 0) {
			NoticeDTO nDTO = new NoticeDTO();
			nDTO.setTitle("없음");
			nList.add(nDTO);
		} else {
			int iNum = (pageNum - 1) * 10;

			try {
				nList = noticeService.getGuideList(iNum);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		model.addAttribute("nList", nList);
		model.addAttribute("cnt", cnt);
		model.addAttribute("pgNum", pageNum);
		return "/main/Guide/GuideList";
	}

	// 사용자 공지사항 자세히보기
	@RequestMapping(value = "main/GuideDetail")
	public String GuideDetailUser(HttpServletRequest request, Model model, HttpSession session) {
		int iNum = 0;
		iNum = Integer.parseInt(request.getParameter("iNum"));
		int cnt = 0;
		try {
			cnt = noticeService.getGuideCnt();
		} catch (Exception e) {
			e.printStackTrace();
		}
		int pgNum = (cnt - iNum) / 10 + 1;
		int iNum2 = iNum - 1;
		NoticeDTO nDTO = new NoticeDTO();
		try {
			nDTO = noticeService.getGuideDetail(iNum2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (nDTO == null) {
			model.addAttribute("msg", "공지사항이 없습니다.");
			model.addAttribute("url", "/main/GuidePage.do?pgNum=1");
			return "redirect";
		} else {
			model.addAttribute("nDTO", nDTO);
			model.addAttribute("iNum", iNum);
			model.addAttribute("pgNum", pgNum);
			return "/main/Guide/GuideDetail";
		}
	}

	// 사용자 공지사항 자세히보기 이전버튼
	@RequestMapping(value = "main/GuidePreviousDetail")
	public String GuidePreviousDetail(HttpServletRequest request, Model model, HttpSession session) {
		int iNum = 0;
		iNum = Integer.parseInt(request.getParameter("iNum"));
		int cnt = 0;
		try {
			cnt = noticeService.getGuideCnt();
		} catch (Exception e) {
			e.printStackTrace();
		}
		int pgNum = (cnt - iNum) / 10 + 1;
		iNum += 1;
		if (iNum > cnt) {
			model.addAttribute("msg", "첫번째 공지사항입니다.");
			model.addAttribute("url", "/main/GuidePage.do?pgNum=1");
			return "redirect";
		}
		int iNum2 = iNum - 1;

		NoticeDTO nDTO = new NoticeDTO();
		try {
			nDTO = noticeService.getGuideDetail(iNum2);
		} catch (Exception e) {
			e.printStackTrace();
		}

		model.addAttribute("nDTO", nDTO);
		model.addAttribute("iNum", iNum);
		model.addAttribute("pgNum", pgNum);
		return "/main/Guide/GuideDetail";

	}

	// 사용자 공지사항 자세히보기 다음버튼
	@RequestMapping(value = "main/GuideNextDetail")
	public String GuideNextDetail(HttpServletRequest request, Model model, HttpSession session) {
		int iNum = 0;
		iNum = Integer.parseInt(request.getParameter("iNum"));
		int cnt = 0;
		try {
			cnt = noticeService.getGuideCnt();
		} catch (Exception e) {
			e.printStackTrace();
		}
		int pgNum = (cnt - iNum) / 10 + 1;
		iNum -= 1;
		if (iNum <= 0) {
			model.addAttribute("msg", "마지막 공지사항입니다.");
			model.addAttribute("url", "/main/GuidePage.do?pgNum=" + pgNum);
			return "redirect";
		}
		int iNum2 = iNum - 1;
		NoticeDTO nDTO = new NoticeDTO();
		try {
			nDTO = noticeService.getGuideDetail(iNum2);
		} catch (Exception e) {
			e.printStackTrace();
		}

		model.addAttribute("nDTO", nDTO);
		model.addAttribute("iNum", iNum);
		model.addAttribute("pgNum", pgNum);
		return "/main/Guide/GuideDetail";

	}

	// 관리자 공지사항 목록
	@RequestMapping(value = "manager/GuideList")
	public String GuideList(HttpServletRequest request, Model model, HttpSession session) {
		String userId = (String) session.getAttribute("userId");
		String userAuthor = (String) session.getAttribute("userAuthor");

		if (userId != null) {
			if (userAuthor.equals("1")) {
				log.info(this.getClass());
				int pageNum = 0;
				String pgNum = request.getParameter("pgNum");
				pageNum = Integer.parseInt(pgNum);
				// 전체 공지사항 갯수 확인
				int cnt = 0;
				try {
					cnt = noticeService.getGuideCnt();
				} catch (Exception e) {
					e.printStackTrace();
				}
				List<NoticeDTO> nList = new ArrayList<>();

				if (cnt == 0) {
					NoticeDTO nDTO = new NoticeDTO();
					nDTO.setTitle("없음");
					nList.add(nDTO);
				} else {
					int iNum = (pageNum - 1) * 10;

					try {
						nList = noticeService.getGuideList(iNum);
					} catch (Exception e) {
						e.printStackTrace();
					}
					for (int i = 0; i < nList.size(); i++) {
						nList.get(i).setiNum(cnt - i - iNum);
						log.info(nList.get(i).getiNum());
					}

				}
				ManagerTopMessage(managerService, model);
				model.addAttribute("nList", nList);
				model.addAttribute("cnt", cnt);
				model.addAttribute("pgNum", pageNum);
				return "/manager/Guide/GuideList";
			} else {
				model.addAttribute("msg", "관리자 권한이 필요합니다.");
				model.addAttribute("url", "/mypage/main.do");
				return "/redirect";
			}

		} else {
			model.addAttribute("msg", "로그인이 필요한 서비스 입니다.");
			model.addAttribute("url", "/user/userLogin.do");
			return "/redirect";
		}

	}

	// 관리자 공지사항 목록 다음페이지
	@RequestMapping(value = "/manager/GuideNextPage")
	public String ManagerGuideNextPage(HttpServletRequest request, Model model, HttpSession session) {
		String userId = (String) session.getAttribute("userId");
		String userAuthor = (String) session.getAttribute("userAuthor");
		log.info(this.getClass());

		if (userId != null) {
			if (userAuthor.equals("1")) {
				log.info(this.getClass());
				int pageNum = 0;
				String pgNum = request.getParameter("pgNum");
				pageNum = Integer.parseInt(pgNum);
				pageNum += 1;
				// 전체 공지사항 갯수 확인
				int cnt = 0;
				try {
					cnt = noticeService.getGuideCnt();
				} catch (Exception e) {
					e.printStackTrace();
				}
				log.info(cnt);
				List<NoticeDTO> nList = new ArrayList<>();

				if (cnt == 0) {
					NoticeDTO nDTO = new NoticeDTO();
					nDTO.setTitle("없음");
					nList.add(nDTO);
				} else {
					int iNum = (pageNum - 1) * 10;

					try {
						nList = noticeService.getGuideList(iNum);
					} catch (Exception e) {
						e.printStackTrace();
					}
					for (int i = 0; i < nList.size(); i++) {
						nList.get(i).setiNum(cnt - i - iNum);
						log.info(nList.get(i).getiNum());
					}

				}
				ManagerTopMessage(managerService, model);
				model.addAttribute("nList", nList);
				model.addAttribute("cnt", cnt);
				model.addAttribute("pgNum", pageNum);
				return "/manager/Guide/GuideList";
			} else {
				model.addAttribute("msg", "관리자 권한이 필요합니다.");
				model.addAttribute("url", "/mypage/main.do");
				return "/redirect";
			}

		} else {
			model.addAttribute("msg", "로그인이 필요한 서비스 입니다.");
			model.addAttribute("url", "/user/userLogin.do");
			return "/redirect";
		}

	}

	// 관리자 공지사항 목록 이전페이지
	@RequestMapping(value = "/manager/GuidePreviousPage")
	public String ManagerGuidePreviousPage(HttpServletRequest request, Model model, HttpSession session) {
		String userId = (String) session.getAttribute("userId");
		String userAuthor = (String) session.getAttribute("userAuthor");
		log.info(this.getClass());

		if (userId != null) {
			if (userAuthor.equals("1")) {
				log.info(this.getClass());
				int pageNum = 0;
				String pgNum = request.getParameter("pgNum");
				pageNum = Integer.parseInt(pgNum);
				pageNum -= 1;
				// 전체 공지사항 갯수 확인
				int cnt = 0;
				try {
					cnt = noticeService.getGuideCnt();
				} catch (Exception e) {
					e.printStackTrace();
				}
				log.info(cnt);
				List<NoticeDTO> nList = new ArrayList<>();

				if (cnt == 0) {
					NoticeDTO nDTO = new NoticeDTO();
					nDTO.setTitle("없음");
					nList.add(nDTO);
				} else {
					int iNum = (pageNum - 1) * 10;

					try {
						nList = noticeService.getGuideList(iNum);
					} catch (Exception e) {
						e.printStackTrace();
					}
					for (int i = 0; i < nList.size(); i++) {
						nList.get(i).setiNum(cnt - i - iNum);
						log.info(nList.get(i).getiNum());
					}

				}
				ManagerTopMessage(managerService, model);
				model.addAttribute("nList", nList);
				model.addAttribute("cnt", cnt);
				model.addAttribute("pgNum", pageNum);
				return "/manager/Guide/GuideList";
			} else {
				model.addAttribute("msg", "관리자 권한이 필요합니다.");
				model.addAttribute("url", "/mypage/main.do");
				return "/redirect";
			}

		} else {
			model.addAttribute("msg", "로그인이 필요한 서비스 입니다.");
			model.addAttribute("url", "/user/userLogin.do");
			return "/redirect";
		}

	}

	// 관리자 공지사항 등록 페이지
	@RequestMapping(value = "manager/GuideReg")
	public String GuideReg(Model model, HttpSession session) {
		String userId = (String) session.getAttribute("userId");
		String userAuthor = (String) session.getAttribute("userAuthor");
		if (userId != null) {
			if (userAuthor.equals("1")) {
				ManagerTopMessage(managerService, model);
				return "/manager/Guide/GuideReg";
			} else {
				model.addAttribute("msg", "관리자 권한이 필요합니다.");
				model.addAttribute("url", "/mypage/main.do");
				return "/redirect";
			}

		} else {
			model.addAttribute("msg", "로그인이 필요한 서비스 입니다.");
			model.addAttribute("url", "/user/userLogin.do");
			return "/redirect";
		}

	}

	// 관리자 공지사항 등록 실행
	@RequestMapping(value = "manager/GuideRegProc")
	public String GuideRegProc(HttpServletRequest request, Model model, HttpSession session) throws Exception {
		log.info(this.getClass());
		String title = request.getParameter("title");
		String content = request.getParameter("content");
		String userId = (String) session.getAttribute("userId");

		NoticeDTO nDTO = new NoticeDTO();
		nDTO.setContent(content);
		nDTO.setTitle(title);
		nDTO.setRegId(userId);

		int result = 0;
		try {
			result = noticeService.insertGuideInfo(nDTO);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (result > 0) {
			model.addAttribute("msg", "공지사항이 등록되었습니다.");
			model.addAttribute("url", "/manager/GuideList.do?pgNum=1");
		} else {
			model.addAttribute("msg", "공지사항 등록에 실패했습니다.");
			model.addAttribute("url", "/manager/GuideList.do?pgNum=1");
		}

		return "/redirect";
	}

	// 관리자 공지사항 자세히보기
	@RequestMapping(value = "manager/GuideDetail")
	public String GuideDetail(HttpServletRequest request, Model model, HttpSession session) {
		String userId = (String) session.getAttribute("userId");
		String userAuthor = (String) session.getAttribute("userAuthor");
		if (userId != null) {
			if (userAuthor.equals("1")) {
				int iNum = 0;
				iNum = Integer.parseInt(request.getParameter("iNum"));
				int cnt = 0;
				try {
					cnt = noticeService.getGuideCnt();
				} catch (Exception e) {
					e.printStackTrace();
				}
				int pgNum = (cnt - iNum) / 10 + 1;
				int iNum2 = iNum - 1;
				NoticeDTO nDTO = new NoticeDTO();
				try {
					nDTO = noticeService.getGuideDetail(iNum2);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (nDTO == null) {
					model.addAttribute("msg", "공지사항이 없습니다.");
					model.addAttribute("url", "/manager/GuideList.do?pgNum=1");
					return "redirect";
				} else {
					ManagerTopMessage(managerService, model);
					model.addAttribute("nDTO", nDTO);
					model.addAttribute("iNum", iNum);
					model.addAttribute("pgNum", pgNum);
					return "/manager/Guide/GuideDetail";
				}
			} else {
				model.addAttribute("msg", "관리자 권한이 필요합니다.");
				model.addAttribute("url", "/mypage/main.do");
				return "/redirect";
			}

		} else {
			model.addAttribute("msg", "로그인이 필요한 서비스 입니다.");
			model.addAttribute("url", "/user/userLogin.do");
			return "/redirect";
		}

	}

	// 관리자 공지사항 수정 페이지
	@RequestMapping(value = "manager/GuideModify")
	public String GuideModify(HttpServletRequest request, Model model, HttpSession session) {
		String userId = (String) session.getAttribute("userId");
		String userAuthor = (String) session.getAttribute("userAuthor");
		if (userId != null) {
			if (userAuthor.equals("1")) {
				int iNum = 0;
				iNum = Integer.parseInt(request.getParameter("iNum"));
				int iNum2 = iNum - 1;
				NoticeDTO nDTO = new NoticeDTO();
				try {
					nDTO = noticeService.getGuideDetail(iNum2);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (nDTO == null) {
					model.addAttribute("msg", "공지사항이 없습니다.");
					model.addAttribute("url", "/manager/GuideList.do?pgNum=1");
					return "redirect";
				} else {
					ManagerTopMessage(managerService, model);
					model.addAttribute("nDTO", nDTO);
					return "/manager/Guide/GuideModify";
				}

			} else {
				model.addAttribute("msg", "관리자 권한이 필요합니다.");
				model.addAttribute("url", "/mypage/main.do");
				return "/redirect";
			}

		} else {
			model.addAttribute("msg", "로그인이 필요한 서비스 입니다.");
			model.addAttribute("url", "/user/userLogin.do");
			return "/redirect";
		}
	}

	// 관리자 공지사항 수정 실행
	@RequestMapping(value = "manager/GuideModifyProc")
	public String GuideModifyProc(HttpServletRequest request, Model model) throws Exception {

		String title = request.getParameter("title");
		String content = request.getParameter("content");
		String seq = request.getParameter("seq");

		NoticeDTO nDTO = new NoticeDTO();
		nDTO.setContent(content);
		nDTO.setSeq(seq);
		nDTO.setTitle(title);

		int result = 0;
		try {
			result = noticeService.updateGuideInfo(nDTO);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (result > 0) {
			model.addAttribute("msg", "수정되었습니다.");
			model.addAttribute("url", "/manager/GuideList.do?pgNum=1");
		} else {
			model.addAttribute("msg", "수정에 실패했습니다.");
			model.addAttribute("url", "/manager/GuideList.do?pgNum=1");
		}

		return "/redirect";
	}

	// 관리자 공지사항 삭제
	@RequestMapping(value = "manager/GuideDelete")
	public String GuideDelete(HttpServletRequest request, Model model, HttpSession session) throws Exception {
		String seq = request.getParameter("seq");
		String userId = (String) session.getAttribute("userId");
		String userAuthor = (String) session.getAttribute("userAuthor");
		if (userId != null) {
			if (userAuthor.equals("1")) {
				int result = 0;

				try {
					result = noticeService.deleteGuideInfo(seq);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (result > 0) {
					model.addAttribute("url", "/manager/GuideList.do?pgNum=1");
					model.addAttribute("msg", "삭제되었습니다.");
				} else {
					model.addAttribute("url", "/manager/GuideList.do?pgNum=1");
					model.addAttribute("msg", "삭제에 실패했습니다.");

				}
			} else {
				model.addAttribute("msg", "관리자 권한이 필요합니다.");
				model.addAttribute("url", "/mypage/main.do");
			}
		} else {
			model.addAttribute("msg", "로그인이 필요한 서비스 입니다.");
			model.addAttribute("url", "/user/userLogin.do");
		}
		return "/redirect";
	}

	// 관리페이지 탑바 메세지
	public void ManagerTopMessage(IManagerService managerService, Model model) {
		List<BoardDTO> mList = new ArrayList<>();
		try {
			mList = managerService.getMessageList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (mList.isEmpty()) {
			BoardDTO mDTO = new BoardDTO();
			mDTO.setTitle("없음");
			mList.add(mDTO);
		}
		model.addAttribute("mList", mList);
	}

}
