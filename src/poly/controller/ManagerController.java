package poly.controller;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import poly.dto.BoardDTO;
import poly.dto.CountDTO;
import poly.dto.PostDTO;
import poly.dto.PostimgDTO;
import poly.dto.UserDTO;
import poly.service.IManagerService;
import poly.service.IPostService;
import poly.service.impl.ManagerService;

@Controller
public class ManagerController {
	private Logger log = Logger.getLogger(this.getClass());

	@Resource(name = "ManagerService")
	private IManagerService managerService;

	@Resource(name = "PostService")
	private IPostService PostService;

	// 관리자 관리페이지 메인
	@RequestMapping(value = "manager/main")
	public String managermain(HttpServletRequest request,HttpSession session, Model model) {
		String userId = (String) session.getAttribute("userId");
		String userAuthor = (String) session.getAttribute("userAuthor");
		
		if (userId != null) {
			if (userAuthor.equals("1")) {
				
			      String[] ten = {"10","20","30","40","50"};
			      String[] ten_nine = {"19","29","39","49","100"};
				
				CountDTO cDTO = new CountDTO();
				
				
				List result= new ArrayList<>();
				int cnt=0;
				for(int i=0; i<5; i++){
					
					cDTO.setTen(ten[i]);
					cDTO.setTen_nine(ten_nine[i]);
					try {
						cnt=managerService.getManagerMain(cDTO);
					} catch (Exception e) {
						e.printStackTrace();
					}
					result.add(cnt);
					
				}
		
				
				model.addAttribute("result", result);

				
				
				ManagerTopMessage(managerService, model);
				return "/manager/main";
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

	// 관리자 회원관리 페이지
	@RequestMapping(value = "manager/crm")
	public String managercrm(HttpServletRequest request, HttpSession session, Model model) {
		String userId = (String) session.getAttribute("userId");
		String userAuthor = (String) session.getAttribute("userAuthor");
		if (userId != null) {
			if (userAuthor.equals("1")) {
				int pageNum = 0;
				String pgNum = request.getParameter("pgNum");
				pageNum = Integer.parseInt(pgNum);

				int cnt = 0;
				try {
					cnt = managerService.getUserCnt();
				} catch (Exception e) {
					e.printStackTrace();
				}
				log.info(cnt);
				List<UserDTO> uList = new ArrayList<>();
				int iNum = (pageNum - 1) * 10;
				try {
					uList = managerService.getUserList(iNum);
				} catch (Exception e) {
					e.printStackTrace();
				}

				int Delcnt = 0;
				if (!uList.isEmpty()) {
					for (int i = 0; i < uList.size(); i++) {
						if (uList.get(i).getUserStat().equals("2")) {
							long lNum = TimeCheck(uList.get(i).getUserDel());
							log.info(uList.get(i).getUserDel());
							if (lNum >= 60) {
								int result = 0;
								try {
									result = managerService.deleteUserInfo(uList.get(i).getUserId());
								} catch (Exception e) {
									e.printStackTrace();
								}
								Delcnt += 1;
							}
							if (Delcnt > 0) {
								model.addAttribute("msg", "탈퇴한지 60일이 지난 회원 " + Delcnt + "명이 삭제되었습니다.");
								model.addAttribute("url", "/manager/crm.do?pgNum=1");
								return "/redirect";
							}
						}
					}
				}

				ManagerTopMessage(managerService, model);
				model.addAttribute("uList", uList);
				model.addAttribute("cnt", cnt);
				model.addAttribute("pgNum", pageNum);
				return "/manager/crm/crm";
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

	// 관리자 회원관리 검색 페이지
	@RequestMapping(value = "manager/crmSearch")
	public String crmSearch(HttpServletRequest request, HttpSession session, Model model) throws Exception {
		String userId = (String) session.getAttribute("userId");
		String userAuthor = (String) session.getAttribute("userAuthor");
		if (userId != null) {
			if (userAuthor.equals("1")) {
				int pageNum = 0;
				String pgNum = request.getParameter("pgNum");
				String searchCont = request.getParameter("searchCont");
				String searchSelect = request.getParameter("searchSelect");
				log.info(searchSelect);

				pageNum = Integer.parseInt(pgNum);
				int cnt = 0;
				List<UserDTO> uList = new ArrayList<>();
				UserDTO uDTO = new UserDTO();
				if (searchSelect.equals("userId")) {
					try {
						cnt = managerService.getCntId(searchCont);
					} catch (Exception e) {
						e.printStackTrace();
					}
					log.info(cnt);

					int iNum = (pageNum - 1) * 10;
					uDTO.setSearchCont(searchCont);
					uDTO.setiNum(iNum);
					try {
						uList = managerService.getSearchIdList(uDTO);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (searchSelect.equals("userName")) {
					try {
						cnt = managerService.getCntName(searchCont);
					} catch (Exception e) {
						e.printStackTrace();
					}
					log.info(cnt);

					int iNum = (pageNum - 1) * 10;
					uDTO.setSearchCont(searchCont);
					uDTO.setiNum(iNum);
					try {
						uList = managerService.getSearchNameList(uDTO);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					try {
						cnt = managerService.getCntStat(searchCont);
					} catch (Exception e) {
						e.printStackTrace();
					}
					log.info(cnt);

					int iNum = (pageNum - 1) * 10;
					if (searchCont.equals("정상")) {
						searchCont = "0";
					} else if (searchCont.equals("정지")) {
						searchCont = "1";
					} else if (searchCont.equals("탈퇴")) {
						searchCont = "2";
					}
					uDTO.setSearchCont(searchCont);
					uDTO.setiNum(iNum);
					try {
						uList = managerService.getSearchStatList(uDTO);
					} catch (Exception e) {
						e.printStackTrace();
					}

					if (searchCont.equals("0")) {
						searchCont = "정상";
					} else if (searchCont.equals("1")) {
						searchCont = "정지";
					} else if (searchCont.equals("2")) {
						searchCont = "탈퇴";
					}
				}

				ManagerTopMessage(managerService, model);
				model.addAttribute("searchCont", searchCont);
				model.addAttribute("searchSelect", searchSelect);
				model.addAttribute("uList", uList);
				model.addAttribute("cnt", cnt);
				model.addAttribute("pgNum", pageNum);
				return "/manager/crm/crmSearch";
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

	// 관리자 회원관리 다음페이지
	@RequestMapping(value = "manager/crmNextPage")
	public String crmNextPage(HttpServletRequest request, HttpSession session, Model model) {
		String userId = (String) session.getAttribute("userId");
		String userAuthor = (String) session.getAttribute("userAuthor");
		if (userId != null) {
			if (userAuthor.equals("1")) {
				int pageNum = 0;
				String pgNum = request.getParameter("pgNum");
				pageNum = Integer.parseInt(pgNum);
				pageNum += 1;
				int cnt = 0;
				try {
					cnt = managerService.getUserCnt();
				} catch (Exception e) {
					e.printStackTrace();
				}
				log.info(cnt);
				List<UserDTO> uList = new ArrayList<>();
				int iNum = (pageNum - 1) * 10;
				try {
					uList = managerService.getUserList(iNum);
				} catch (Exception e) {
					e.printStackTrace();
				}
				ManagerTopMessage(managerService, model);
				model.addAttribute("uList", uList);
				model.addAttribute("cnt", cnt);
				model.addAttribute("pgNum", pageNum);
				return "/manager/crm/crm";
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

	// 관리자 회원관리 검색 다음페이지
	@RequestMapping(value = "manager/crmSearchNextPage")
	public String crmSearchNextPage(HttpServletRequest request, HttpSession session, Model model) throws Exception {
		String userId = (String) session.getAttribute("userId");
		String userAuthor = (String) session.getAttribute("userAuthor");
		if (userId != null) {
			if (userAuthor.equals("1")) {
				int pageNum = 0;
				String pgNum = request.getParameter("pgNum");
				String searchCont = request.getParameter("searchCont");
				String searchSelect = request.getParameter("searchSelect");
				log.info(searchSelect);

				pageNum = Integer.parseInt(pgNum);
				pageNum += 1;
				int cnt = 0;
				List<UserDTO> uList = new ArrayList<>();
				UserDTO uDTO = new UserDTO();
				if (searchSelect.equals("userId")) {
					try {
						cnt = managerService.getCntId(searchCont);
					} catch (Exception e) {
						e.printStackTrace();
					}
					log.info(cnt);

					int iNum = (pageNum - 1) * 10;
					uDTO.setSearchCont(searchCont);
					uDTO.setiNum(iNum);
					try {
						uList = managerService.getSearchIdList(uDTO);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (searchSelect.equals("userName")) {
					try {
						cnt = managerService.getCntName(searchCont);
					} catch (Exception e) {
						e.printStackTrace();
					}
					log.info(cnt);

					int iNum = (pageNum - 1) * 10;
					uDTO.setSearchCont(searchCont);
					uDTO.setiNum(iNum);
					try {
						uList = managerService.getSearchNameList(uDTO);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					try {
						cnt = managerService.getCntStat(searchCont);
					} catch (Exception e) {
						e.printStackTrace();
					}
					log.info(cnt);

					int iNum = (pageNum - 1) * 10;
					if (searchCont.equals("정상")) {
						searchCont = "0";
					} else if (searchCont.equals("정지")) {
						searchCont = "1";
					} else if (searchCont.equals("탈퇴")) {
						searchCont = "2";
					}
					uDTO.setSearchCont(searchCont);
					uDTO.setiNum(iNum);
					try {
						uList = managerService.getSearchStatList(uDTO);
					} catch (Exception e) {
						e.printStackTrace();
					}

					if (searchCont.equals("0")) {
						searchCont = "정상";
					} else if (searchCont.equals("1")) {
						searchCont = "정지";
					} else if (searchCont.equals("2")) {
						searchCont = "탈퇴";
					}
				}

				ManagerTopMessage(managerService, model);
				model.addAttribute("searchCont", searchCont);
				model.addAttribute("searchSelect", searchSelect);
				model.addAttribute("uList", uList);
				model.addAttribute("cnt", cnt);
				model.addAttribute("pgNum", pageNum);
				return "/manager/crm/crmSearch";
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

	// 관리자 회원관리 이전페이지
	@RequestMapping(value = "manager/crmPreviousPage")
	public String crmPreviousPage(HttpServletRequest request, HttpSession session, Model model) {
		String userId = (String) session.getAttribute("userId");
		String userAuthor = (String) session.getAttribute("userAuthor");
		if (userId != null) {
			if (userAuthor.equals("1")) {
				int pageNum = 0;
				String pgNum = request.getParameter("pgNum");
				pageNum = Integer.parseInt(pgNum);
				pageNum -= 1;
				int cnt = 0;
				try {
					cnt = managerService.getUserCnt();
				} catch (Exception e) {
					e.printStackTrace();
				}
				log.info(cnt);
				List<UserDTO> uList = new ArrayList<>();
				int iNum = (pageNum - 1) * 10;
				try {
					uList = managerService.getUserList(iNum);
				} catch (Exception e) {
					e.printStackTrace();
				}
				ManagerTopMessage(managerService, model);
				model.addAttribute("uList", uList);
				model.addAttribute("cnt", cnt);
				model.addAttribute("pgNum", pageNum);
				return "/manager/crm/crm";
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

	// 관리자 회원관리 검색 이전페이지
	@RequestMapping(value = "manager/crmSearchPreviousPage")
	public String crmSearchPreviousPage(HttpServletRequest request, HttpSession session, Model model) throws Exception {
		String userId = (String) session.getAttribute("userId");
		String userAuthor = (String) session.getAttribute("userAuthor");
		if (userId != null) {
			if (userAuthor.equals("1")) {
				int pageNum = 0;
				String pgNum = request.getParameter("pgNum");
				String searchCont = request.getParameter("searchCont");
				String searchSelect = request.getParameter("searchSelect");
				log.info(searchSelect);

				pageNum = Integer.parseInt(pgNum);
				pageNum -= 1;
				int cnt = 0;
				List<UserDTO> uList = new ArrayList<>();
				UserDTO uDTO = new UserDTO();
				if (searchSelect.equals("userId")) {
					try {
						cnt = managerService.getCntId(searchCont);
					} catch (Exception e) {
						e.printStackTrace();
					}
					log.info(cnt);

					int iNum = (pageNum - 1) * 10;
					uDTO.setSearchCont(searchCont);
					uDTO.setiNum(iNum);
					try {
						uList = managerService.getSearchIdList(uDTO);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (searchSelect.equals("userName")) {
					try {
						cnt = managerService.getCntName(searchCont);
					} catch (Exception e) {
						e.printStackTrace();
					}
					log.info(cnt);

					int iNum = (pageNum - 1) * 10;
					uDTO.setSearchCont(searchCont);
					uDTO.setiNum(iNum);
					try {
						uList = managerService.getSearchNameList(uDTO);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					try {
						cnt = managerService.getCntStat(searchCont);
					} catch (Exception e) {
						e.printStackTrace();
					}
					log.info(cnt);

					int iNum = (pageNum - 1) * 10;
					if (searchCont.equals("정상")) {
						searchCont = "0";
					} else if (searchCont.equals("정지")) {
						searchCont = "1";
					} else if (searchCont.equals("탈퇴")) {
						searchCont = "2";
					}
					uDTO.setSearchCont(searchCont);
					uDTO.setiNum(iNum);
					try {
						uList = managerService.getSearchStatList(uDTO);
					} catch (Exception e) {
						e.printStackTrace();
					}

					if (searchCont.equals("0")) {
						searchCont = "정상";
					} else if (searchCont.equals("1")) {
						searchCont = "정지";
					} else if (searchCont.equals("2")) {
						searchCont = "탈퇴";
					}
				}

				ManagerTopMessage(managerService, model);
				model.addAttribute("searchCont", searchCont);
				model.addAttribute("searchSelect", searchSelect);
				model.addAttribute("uList", uList);
				model.addAttribute("cnt", cnt);
				model.addAttribute("pgNum", pageNum);
				return "/manager/crm/crmSearch";
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

	// 관리자 회원관리 페이지 권한 변경
	@RequestMapping(value = "manager/ModifyAuthorProc")
	public String ModifyAuthor(HttpServletRequest request, HttpSession session, Model model) {
		String userId = (String) session.getAttribute("userId");
		String userAuthor = (String) session.getAttribute("userAuthor");
		String userCheck = request.getParameter("userCheck");
		log.info("userCheck 확인" + userCheck);
		if (userCheck.equals(userId)) {
			model.addAttribute("msg", "본인의 권한은 변경하실 수 없습니다.");
			model.addAttribute("url", "/manager/crm.do?pgNum=1");

		} else if (!userCheck.equals("undefined")) {
			if (userId != null) {
				if (userAuthor.equals("1")) {
					UserDTO uDTO = new UserDTO();
					try {
						uDTO = managerService.getUserInfo(userCheck);

					} catch (Exception e) {
						e.printStackTrace();
					}
					if (uDTO.getUserAuthor().equals("0")) {
						int result = 0;
						try {
							result = managerService.alterAuthor(userCheck);
						} catch (Exception e) {
							e.printStackTrace();
						}

						if (result > 0) {
							model.addAttribute("msg", "관리자로 변경되었습니다.");
							model.addAttribute("url", "/manager/crm.do?pgNum=1");
						} else {
							model.addAttribute("msg", "관리자 변경에 실패하였습니다.");
							model.addAttribute("url", "/manager/crm.do?pgNum=1");
						}

					} else {
						int result = 0;
						try {
							result = managerService.alterUser(userCheck);
						} catch (Exception e) {
							e.printStackTrace();
						}

						if (result > 0) {
							model.addAttribute("msg", "사용자로 변경되었습니다.");
							model.addAttribute("url", "/manager/crm.do?pgNum=1");
						} else {
							model.addAttribute("msg", "사용자 변경에 실패하였습니다.");
							model.addAttribute("url", "/manager/crm.do?pgNum=1");
						}
					}
				} else {
					model.addAttribute("msg", "관리자 권한이 필요합니다.");
					model.addAttribute("url", "/mypage/main.do");
				}

			} else {
				model.addAttribute("msg", "로그인이 필요한 서비스 입니다.");
				model.addAttribute("url", "/user/userLogin.do");
			}
		} else {
			model.addAttribute("msg", "사용자를 체크해주세요.");
			model.addAttribute("url", "/manager/crm.do?pgNum=1");
		}

		return "/redirect";

	}

	// 관리자 회원관리 페이지 상태 변경(정상<->정지)
	@RequestMapping(value = "manager/ModifyStatProc")
	public String ModifyStat(HttpServletRequest request, HttpSession session, Model model) {
		String userId = (String) session.getAttribute("userId");
		String userAuthor = (String) session.getAttribute("userAuthor");
		String userCheck = request.getParameter("userCheck");
		log.info("userCheck 확인" + userCheck);
		if (userCheck.equals(userId)) {
			model.addAttribute("msg", "본인의 상태는 변경하실 수 없습니다.");
			model.addAttribute("url", "/manager/crm.do?pgNum=1");

		} else if (!userCheck.equals("undefined")) {
			if (userId != null) {
				if (userAuthor.equals("1")) {
					UserDTO uDTO = new UserDTO();
					try {
						uDTO = managerService.getUserInfo(userCheck);

					} catch (Exception e) {
						e.printStackTrace();
					}
					if (uDTO.getUserStat().equals("0")) {
						int result = 0;
						try {
							result = managerService.alterStop(userCheck);
						} catch (Exception e) {
							e.printStackTrace();
						}

						if (result > 0) {
							model.addAttribute("msg", "유저상태를 활동정지로 변경하였습니다.");
							model.addAttribute("url", "/manager/crm.do?pgNum=1");
						} else {
							model.addAttribute("msg", "유저상태 변경에 실패하였습니다.");
							model.addAttribute("url", "/manager/crm.do?pgNum=1");
						}

					} else if (uDTO.getUserStat().equals("1")) {
						int result = 0;
						try {
							result = managerService.alterNomal(userCheck);
						} catch (Exception e) {
							e.printStackTrace();
						}

						if (result > 0) {
							model.addAttribute("msg", "유저상태를 정상으로 변경하였습니다.");
							model.addAttribute("url", "/manager/crm.do?pgNum=1");
						} else {
							model.addAttribute("msg", "유저상태 변경에 실패하였습니다.");
							model.addAttribute("url", "/manager/crm.do?pgNum=1");
						}
					} else {
						model.addAttribute("msg", "탈퇴 회원의 상태는 변경하실 수 없습니다.");
						model.addAttribute("url", "/manager/crm.do?pgNum=1");
					}
				} else {
					model.addAttribute("msg", "관리자 권한이 필요합니다.");
					model.addAttribute("url", "/mypage/main.do");
				}

			} else {
				model.addAttribute("msg", "로그인이 필요한 서비스 입니다.");
				model.addAttribute("url", "/user/userLogin.do");
			}
		} else {
			model.addAttribute("msg", "사용자를 체크해주세요.");
			model.addAttribute("url", "/manager/crm.do?pgNum=1");
		}

		return "/redirect";

	}

	// 관리자 회원관리 페이지 상태 변경 (정상<->탈퇴)
	@RequestMapping(value = "manager/ModifyDelProc")
	public String ModifyDel(HttpServletRequest request, HttpSession session, Model model) {
		String userId = (String) session.getAttribute("userId");
		String userAuthor = (String) session.getAttribute("userAuthor");
		String userCheck = request.getParameter("userCheck");
		log.info("userCheck 확인" + userCheck);
		if (userCheck.equals(userId)) {
			model.addAttribute("msg", "본인의 상태는 변경하실 수 없습니다.");
			model.addAttribute("url", "/manager/crm.do?pgNum=1");

		} else if (!userCheck.equals("undefined")) {
			if (userId != null) {
				if (userAuthor.equals("1")) {
					UserDTO uDTO = new UserDTO();
					try {
						uDTO = managerService.getUserInfo(userCheck);

					} catch (Exception e) {
						e.printStackTrace();
					}
					if (!uDTO.getUserStat().equals("2")) {
						int result = 0;
						try {
							result = managerService.alterDel(userCheck);
						} catch (Exception e) {
							e.printStackTrace();
						}

						if (result > 0) {
							model.addAttribute("msg", "유저상태를 탈퇴로 변경하였습니다.");
							model.addAttribute("url", "/manager/crm.do?pgNum=1");
						} else {
							model.addAttribute("msg", "유저상태 변경에 실패하였습니다.");
							model.addAttribute("url", "/manager/crm.do?pgNum=1");
						}

					} else {
						int result = 0;
						try {
							result = managerService.alterNomal(userCheck);
						} catch (Exception e) {
							e.printStackTrace();
						}

						if (result > 0) {
							model.addAttribute("msg", "유저상태를 정상으로 변경하였습니다.");
							model.addAttribute("url", "/manager/crm.do?pgNum=1");
						} else {
							model.addAttribute("msg", "유저상태 변경에 실패하였습니다.");
							model.addAttribute("url", "/manager/crm.do?pgNum=1");
						}
					}
				} else {
					model.addAttribute("msg", "관리자 권한이 필요합니다.");
					model.addAttribute("url", "/mypage/main.do");
				}

			} else {
				model.addAttribute("msg", "로그인이 필요한 서비스 입니다.");
				model.addAttribute("url", "/user/userLogin.do");
			}
		} else {
			model.addAttribute("msg", "사용자를 체크해주세요.");
			model.addAttribute("url", "/manager/crm.do?pgNum=1");
		}

		return "/redirect";

	}

	// 관리자 개인정보 수정 페이지
	@RequestMapping(value = "manager/managerModify")
	public String managerModify(HttpServletRequest request, Model model, HttpSession session) {
		String userId = (String) session.getAttribute("userId");
		String userAuthor = (String) session.getAttribute("userAuthor");
		if (userId != null) {
			if (userAuthor.equals("1")) {
				UserDTO uDTO = new UserDTO();
				try {
					uDTO = managerService.getManagerInfo(userId);

				} catch (Exception e) {
					e.printStackTrace();
				}
				ManagerTopMessage(managerService, model);
				model.addAttribute("uDTO", uDTO);
				return "/manager/managerModify";
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

	// 관리자 개인정보 수정 실행
	@RequestMapping(value = "manager/managerModifyProc")
	public String managerModifyProc(HttpServletRequest request, Model model, HttpSession session) throws Exception {

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
			model.addAttribute("url", "/manager/managerModify.do");
		} else if (userPassword.length() < 8) {
			model.addAttribute("msg", "비밀번호는 8~20자 영문 대 소문자,숫자,특수문자를 사용해 주세요.");
			model.addAttribute("url", "/manager/managerModify.do");
		} else {
			int result = 0;
			try {
				result = managerService.updateManagerInfo(uDTO);
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (result > 0) {
				model.addAttribute("msg", "수정되었습니다.");
				model.addAttribute("url", "/manager/managerModify.do");
			} else {
				model.addAttribute("msg", "수정에 실패했습니다.");
				model.addAttribute("url", "/manager/managerModify.do");
			}
		}

		return "/redirect";
	}

	// 자세 관리 페이지 메인
	@RequestMapping(value = "manager/PostureMain")
	public String PostureMain(HttpSession session, Model model) {
		String userId = (String) session.getAttribute("userId");
		String userAuthor = (String) session.getAttribute("userAuthor");

		if (userId != null) {
			if (userAuthor.equals("1")) {
				List<PostDTO> pList = new ArrayList<>();
				try {
					pList = PostService.getPostList();
				} catch (Exception e) {
					e.printStackTrace();
				}
				model.addAttribute("pList", pList);
				ManagerTopMessage(managerService, model);
				return "/manager/Posture/PostureMain";
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

	// 자세등록 첫번째 페이지
	@RequestMapping(value = "manager/PostureReg")
	public String PostReg(HttpServletRequest request, Model model, HttpSession session) {
		String userId = (String) session.getAttribute("userId");
		String seq = request.getParameter("seq");
		String title = request.getParameter("title");
		String content = request.getParameter("content");
		String userAuthor = (String) session.getAttribute("userAuthor");

		if (userId != null) {
			if (userAuthor.equals("1")) {
				model.addAttribute("seq", seq);
				model.addAttribute("title", title);
				model.addAttribute("content", content);
				ManagerTopMessage(managerService, model);
				return "/manager/Posture/PostureReg";
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

	// 자세등록 첫번째 실행버튼
	@RequestMapping(value = "manager/PostProc")
	public String PoseRegProc(HttpServletRequest request, Model model, HttpSession session) throws Exception {
		log.info(this.getClass() + "ImgAction start @@@");

		String post_notic = request.getParameter("post_notic");
		String post_level = request.getParameter("post_level");
		String post_class = request.getParameter("post_class");
		String post_part = request.getParameter("post_part");
		String post_effect = request.getParameter("post_effect");
		String post_name = request.getParameter("post_name");
		String post_img = post_name + ".jpg";
		log.info("post_notic" + post_notic);
		log.info("post_level" + post_level);
		log.info("post_class" + post_class);
		log.info("post_part" + post_part);
		log.info("post_effect" + post_effect);
		log.info("post_name" + post_name);
		log.info("post_img" + post_img);
		log.info("네임:" + post_name);

		PostDTO pDTO = new PostDTO();
		pDTO.setPost_name(post_name);
		pDTO.setPost_img(post_img);
		pDTO.setPost_notic(post_notic);
		pDTO.setPost_level(post_level);
		pDTO.setPost_class(post_class);
		pDTO.setPost_part(post_part);
		pDTO.setPost_effect(post_effect);

		// 파일업로드
		/*
		 * String path =
		 * request.getSession().getServletContext().getRealPath("/WebContent/postimg/");
		 */
		String path = "C:/han/evergreen_2019/SpringPRJ_mysql_YJ/WebContent/postimg/" + post_name + "/";

		log.info("파일이 들어갈 경로: " + path);

		Map returnObject = new HashMap();

		int result = 0;
		try {
			result = PostService.insertPostInfo(pDTO);

			MultipartHttpServletRequest mhsr = (MultipartHttpServletRequest) request;
			Iterator iter = mhsr.getFileNames();

			MultipartFile mfile = null;
			String fieldName = "";
			List resultList = new ArrayList();

			// 디렉토리가 없다면 생성
			File dir = new File(path);
			if (!dir.isDirectory()) {
				dir.mkdirs();
			}

			while (iter.hasNext()) {
				fieldName = (String) iter.next();
				mfile = mhsr.getFile(fieldName);
				String origName;
				origName = new String(mfile.getOriginalFilename().getBytes("8859_1"), "UTF-8");

				if ("".equals(origName)) {
					log.info("파일없음");
					continue;

				}

				// 파일명 변경
				String ext = origName.substring(origName.lastIndexOf('.'));
				String saveFileName = post_name + ext;

				// File serverFile = new File(path + File.pathSeparator + saveFileName);

				File serverFile = new File(path + saveFileName);
				mfile.transferTo(serverFile);
				// log.info("어떤게 :"+ File.pathSeparator);
				log.info("세미 :" + path);
				log.info("콜론? :" + saveFileName);
				log.info("왜 세미콜론 들어감 :" + saveFileName);

				Map file = new HashMap();
				file.put("orgName", origName);
				file.put("sfile", serverFile);
				resultList.add(file);

			}

			returnObject.put("files", resultList);
			returnObject.put("params", mhsr.getParameterMap());

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (result > 0) {
			log.info(this.getClass() + "ImgAction end @@@");
			model.addAttribute("post_name", post_name);
			return "redirect:/manager/PostureSeq.do";

		} else {
			model.addAttribute("msg", "자세 등록에 실패했습니다.");
			model.addAttribute("url", "/manager/Posture/PostureMain");
			return "/redirect";
		}

	}

	// 자세 등록 두번째 페이지
	@RequestMapping(value = "manager/PostureSeq")
	public String PostSeq(HttpServletRequest request, Model model, HttpSession session) {
		String userId = (String) session.getAttribute("userId");
		String userAuthor = (String) session.getAttribute("userAuthor");
		String post_name = request.getParameter("post_name");
		log.info("네임:" + post_name);

		model.addAttribute("post_name", post_name);
		if (userId != null) {
			if (userAuthor.equals("1")) {

				ManagerTopMessage(managerService, model);
				return "/manager/Posture/PostureSeq";
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

	// 자세등록 두번쨰 추가 버튼
	@RequestMapping(value = "manager/PostseqProc")
	public String PostSeqProc(HttpServletRequest request, Model model, HttpSession session) throws Exception {
		log.info(this.getClass() + "PostseqProc start @@@");

		Date from = new Date();

		SimpleDateFormat transFormat = new SimpleDateFormat("MMddHHmmss");

		String to = transFormat.format(from);

		String post_name = request.getParameter("post_name");
		String post_img_deteil = to + ".jpg";
		String post_memo = request.getParameter("post_memo");

		log.info("네임:" + post_name);
		log.info("이미지:" + post_img_deteil);
		log.info("설명:" + post_memo);

		PostimgDTO iDTO = new PostimgDTO();
		iDTO.setPost_name(post_name);
		iDTO.setPost_img_deteil(post_img_deteil);
		iDTO.setPost_memo(post_memo);

		String path = "C:/han/evergreen_2019/SpringPRJ_mysql_YJ/WebContent/postimg/" + post_name + "/";

		log.info("파일이 들어갈 경로: " + path);

		Map returnObject = new HashMap();

		int result = 0;
		try {
			result = PostService.insertPostImgInfo(iDTO);

			MultipartHttpServletRequest mhsr = (MultipartHttpServletRequest) request;
			Iterator iter = mhsr.getFileNames();

			MultipartFile mfile = null;
			String fieldName = "";
			List resultList = new ArrayList();

			// 디렉토리가 없다면 생성
			File dir = new File(path);
			if (!dir.isDirectory()) {
				dir.mkdirs();
			}
			while (iter.hasNext()) {
				fieldName = (String) iter.next();
				mfile = mhsr.getFile(fieldName);
				String origName;
				origName = new String(mfile.getOriginalFilename().getBytes("8859_1"), "UTF-8");
				if ("".equals(origName)) {
					log.info("파일없음");
					continue;

				}

				// 파일명 변경
				String ext = origName.substring(origName.lastIndexOf('.'));

				// 파일명 시간으로 넣음
				String saveFileName = post_img_deteil + ext;
				log.info("saveFileName :" + saveFileName);

				// File serverFile = new File(path + File.pathSeparator + saveFileName);

				File serverFile = new File(path + saveFileName);
				mfile.transferTo(serverFile);
				// log.info("어떤게 :"+ File.pathSeparator);
				log.info("세미 :" + path);
				log.info("콜론? :" + saveFileName);
				log.info("왜 세미콜론 들어감 :" + saveFileName);

				Map file = new HashMap();
				file.put("orgName", origName);
				file.put("sfile", serverFile);
				resultList.add(file);

			}

			returnObject.put("files", resultList);
			returnObject.put("params", mhsr.getParameterMap());

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (result > 0) {
			model.addAttribute("post_name", post_name);
			log.info(this.getClass() + "ImgAction end @@@");
			return "redirect:/manager/PostureSeq.do";

		} else {
			model.addAttribute("msg", "자세 추가에 실패했습니다.");
			model.addAttribute("url", "/manager/PostureReg.do");
		}

		log.info(this.getClass() + "ImgAction end @@@");
		return "/redirect";
	}

	// 자세 삭제 실행
	@RequestMapping(value = "manager/PostDelProc")
	public String PostDelProc(HttpServletRequest request, Model model, HttpSession session) {

		String post_name = request.getParameter("post_name");

		log.info("PostDelProc :" + post_name);
		int result = 0;

		int result2 = 0;

		log.info("result :" + result);

		File file = new File("C:/han/evergreen_2019/SpringPRJ_mysql_YJ/SpringPRJ_mysql_YJ_YJbContent/postimg/" + post_name);

		if (file.exists()) { // 파일존재여부확인
			if (file.isDirectory()) { // 파일이 디렉토리인지 확인
				File[] files = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					if (files[i].delete()) {
						log.info("삭제성공" + files[i].getName());
					} else {
						log.info("삭제실패" + files[i].getName());
					}
				}
			}
			if (file.delete()) {
				log.info("삭제성공");
			} else {
				log.info("삭제실패");
			}
		}

		try {
			result = PostService.deleteImgInfo(post_name);
			result2 = PostService.deleteImgInfo2(post_name);
			log.info("result :" + result);
			log.info("result2 :" + result2);

		} catch (Exception e) {
			e.printStackTrace();
		}
		if (result > 0) {
			log.info("실행됨 :" + result);
			model.addAttribute("msg", "등록된 자세가 삭제 되었습니다.");
			model.addAttribute("url", "/manager/PostureMain.do");

		} else {
			log.info("result2 :" + result2);
			model.addAttribute("msg", "등록된 자세가 삭제 안되었습니다..ㅠㅠ");
			model.addAttribute("url", "/manager/PostureMain.do");

		}
		return "/redirect";
	}

	// 자세등록 2번째에서 마지막으로 넘어가는 실행페이지
	@RequestMapping(value = "manager/PostseqProc2")
	public String PostSeqProc2(HttpServletRequest request, Model model, HttpSession session) throws Exception {
		log.info(this.getClass() + "PostseqProc start @@@");

		Date from = new Date();

		SimpleDateFormat transFormat = new SimpleDateFormat("MMddHHmmss");

		String to = transFormat.format(from);

		String post_name = request.getParameter("post_name");
		String post_img_deteil = to;
		String post_memo = request.getParameter("post_memo");

		log.info("네임:" + post_name);
		log.info("이미지:" + post_img_deteil);
		log.info("설명:" + post_memo);

		PostimgDTO iDTO = new PostimgDTO();
		iDTO.setPost_name(post_name);
		iDTO.setPost_img_deteil(post_img_deteil);
		iDTO.setPost_memo(post_memo);

		String path = "C:/han/evergreen_2019/SpringPRJ_mysql_YJ/WebContent/postimg/" + post_name + "/";

		log.info("파일이 들어갈 경로: " + path);

		Map returnObject = new HashMap();

		int result = 0;
		try {
			result = PostService.insertPostImgInfo(iDTO);

			MultipartHttpServletRequest mhsr = (MultipartHttpServletRequest) request;
			Iterator iter = mhsr.getFileNames();

			MultipartFile mfile = null;
			String fieldName = "";
			List resultList = new ArrayList();

			// 디렉토리가 없다면 생성
			File dir = new File(path);
			if (!dir.isDirectory()) {
				dir.mkdirs();
			}
			while (iter.hasNext()) {
				fieldName = (String) iter.next();
				mfile = mhsr.getFile(fieldName);
				String origName;
				origName = new String(mfile.getOriginalFilename().getBytes("8859_1"), "UTF-8");
				if ("".equals(origName)) {
					log.info("파일없음");
					continue;

				}

				// 파일명 변경
				String ext = origName.substring(origName.lastIndexOf('.'));

				// 파일명 시간으로 넣음
				String saveFileName = post_img_deteil + ext;
				log.info("saveFileName :" + saveFileName);

				// File serverFile = new File(path + File.pathSeparator + saveFileName);

				File serverFile = new File(path + saveFileName);
				mfile.transferTo(serverFile);
				// log.info("어떤게 :"+ File.pathSeparator);
				log.info("세미 :" + path);
				log.info("콜론? :" + saveFileName);
				log.info("왜 세미콜론 들어감 :" + saveFileName);

				Map file = new HashMap();
				file.put("orgName", origName);
				file.put("sfile", serverFile);
				resultList.add(file);

			}

			returnObject.put("files", resultList);
			returnObject.put("params", mhsr.getParameterMap());

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (result > 0) {
			model.addAttribute("post_name", post_name);
			log.info(this.getClass() + "ImgAction end @@@");
			return "redirect:/manager/PostureLast.do";

		} else {
			model.addAttribute("msg", "자세 추가에 실패했습니다.");
			model.addAttribute("url", "/manager/PostureMain.do");
		}

		log.info(this.getClass() + "ImgAction end @@@");
		return "/redirect";
	}

	// 자세등록 마지막페이지
	@RequestMapping(value = "manager/PostureLast")
	public String PostureLast(HttpServletRequest request, Model model, HttpSession session) {
		log.info(this.getClass());

		// top 바인데 없으면 페이지 실행 안됨
		ManagerTopMessage(managerService, model);

		String post_name = request.getParameter("post_name");

		log.info("PostDelProc :" + post_name);
		PostDTO pDTO = new PostDTO();
		List<PostimgDTO> iList = new ArrayList<>();
		try {
			pDTO = PostService.getPostLast(post_name);
			iList = PostService.getPostLast2(post_name);
		} catch (Exception e) {
			e.printStackTrace();
		}
		model.addAttribute("pDTO", pDTO);
		model.addAttribute("iList", iList);

		return "/manager/Posture/PostureLast";

	}

	// 자세 수정페이지
	@RequestMapping(value = "manager/PostureModify")
	public String PostureModify2(HttpServletRequest request, Model model, HttpSession session) {
		log.info(this.getClass());

		// top 바인데 없으면 페이지 실행 안됨
		ManagerTopMessage(managerService, model);

		String post_name = request.getParameter("post_name");

		log.info("자세수정 :" + post_name);
		PostDTO pDTO = new PostDTO();
		List<PostimgDTO> iList = new ArrayList<>();
		try {
			pDTO = PostService.getPostLast(post_name);
			iList = PostService.getPostLast2(post_name);
		} catch (Exception e) {
			e.printStackTrace();
		}
		model.addAttribute("pDTO", pDTO);
		model.addAttribute("iList", iList);

		return "/manager/Posture/PostureModify";

	}

	// 자세 수정 페이지 실행
	@RequestMapping(value = "manager/PostureModifyProc")
	public String PostureModifyProc(HttpServletRequest request, Model model, HttpSession session) {
		log.info(this.getClass());

		// top 바인데 없으면 페이지 실행 안됨
		ManagerTopMessage(managerService, model);

		String post_name = request.getParameter("post_name");
		String post_notic = request.getParameter("post_notic");
		String post_level = request.getParameter("post_level");
		String post_class = request.getParameter("post_class");
		String post_part = request.getParameter("post_part");
		String post_effect = request.getParameter("post_effect");

		log.info("노틱 :" + post_notic);

		PostDTO pDTO = new PostDTO();

		pDTO.setPost_notic(post_notic);
		pDTO.setPost_level(post_level);
		pDTO.setPost_class(post_class);
		pDTO.setPost_part(post_part);
		pDTO.setPost_effect(post_effect);
		pDTO.setPost_name(post_name);

		String[] post_seq = request.getParameterValues("post_seq");
		String[] post_memo = request.getParameterValues("post_memo");

		PostimgDTO iDTO = new PostimgDTO();

		int result = 0;
		int result2 = 0;

		try {

			result = PostService.updatePostInfo(pDTO);

			for (int i = 0; i < post_seq.length; i++) {
				log.info("자세수정 :" + post_seq[i]);

				iDTO.setPost_seq(post_seq[i]);
				iDTO.setPost_memo(post_memo[i]);
				result2 = PostService.updatePostImgInfo(iDTO);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (result > 0) {
			model.addAttribute("msg", "수정되었습니다.");
			model.addAttribute("url", "/manager/PostureMain.do");
		} else {
			model.addAttribute("msg", "수정에 실패했습니다.");
			model.addAttribute("url", "/manager/PostureMain.do");
		}

		return "/redirect";

	}

	// 시간 계산 함수
	public static long TimeCheck(String str) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

		try {
			Date sysdate = new Date();
			Date FirstDate = format.parse(str);

			long calDate = sysdate.getTime() - FirstDate.getTime();
			long calDateDays = calDate / (24 * 60 * 60 * 1000);

			calDateDays = Math.abs(calDateDays);
			return calDateDays;

		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}

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


