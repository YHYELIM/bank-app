package com.tenco.mybankapp.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.tenco.mybankapp.dto.DepositFormDto;
import com.tenco.mybankapp.dto.SaveFormDto;
import com.tenco.mybankapp.dto.TransferFormDto;
import com.tenco.mybankapp.dto.WithdrawFormDto;
import com.tenco.mybankapp.handler.exception.CustomPageException;
import com.tenco.mybankapp.handler.exception.CustomRestfullException;
import com.tenco.mybankapp.handler.exception.UnAuthorizedException;
import com.tenco.mybankapp.repository.entity.Account;
import com.tenco.mybankapp.repository.entity.History;
import com.tenco.mybankapp.repository.entity.User;
import com.tenco.mybankapp.service.AccountService;

@Controller
@RequestMapping("/account")
public class AccountController {
	
	@Autowired
	private HttpSession session;
	
	@Autowired
	private AccountService accountService;
	
	//임시 예외 발생 확인 http://localhost:80/account/list
	@GetMapping({"/list" , "/"})
	public String list(Model model) {	
		//인증 검사
		User principal = (User)session.getAttribute("principal");//원래 princial 오브젝트 타입인데 유저로 다운캐스팅
		//인증검사 안하고 있음!!!!!!!!!!
		List<Account> accountList  = accountService.readAccountList(principal.getId());
		System.out.println("accountList: "+accountList.toString());
		if(accountList.isEmpty()) {
			model.addAttribute("accountList", null);
		}else {
			model.addAttribute("accountList", accountList);
		}
		return "account/list";	
	}
	
	
	@GetMapping("/save")
	public String save() {
		
		
		return "account/save";
		
	}	
	@PostMapping("/save")
	public String saveProc(SaveFormDto dto) {
		User principal =(User)session.getAttribute("principal");
	 //1. 인증 검사
		
	//2. 유효성 검사
		if(dto.getNumber() == null || dto.getNumber().isEmpty()) {
			throw new CustomRestfullException("계좌번호를 입력하시오", HttpStatus.BAD_REQUEST);
		}
		
		if(dto.getPassword() == null || dto.getPassword().isEmpty()) {
			throw new CustomRestfullException("계좌비밀번호를 입력하시오", HttpStatus.BAD_REQUEST);
		}
		
		if(dto.getBalance() == null || dto.getBalance()<=0) {
			throw new CustomRestfullException("잘못된 입력 입니다", HttpStatus.BAD_REQUEST);
		}
		//System.out.println("dto들어감??"+dto);
		accountService.createAccount(dto, principal.getId());
 
		return "account/list";
	}

	//출금 페이지 요청
	@GetMapping("/withdraw")
	public String withdraw() {
		
		return "account/withdraw";
	}
	
	@PostMapping("/withdraw")
	public String withdrawProc(WithdrawFormDto dto) {
		User principal = (User)session.getAttribute("principal");//원래 princial 오브젝트 타입인데 유저로 다운캐스팅
		
		if(dto.getAmount()==null) {
			throw new CustomRestfullException("금액을 입력하시오", HttpStatus.BAD_REQUEST);
		}
		if(dto.getAmount().longValue()<=0) {
			throw new CustomRestfullException("출금 금액이 0원 이하일 수 없습니다.", HttpStatus.BAD_REQUEST);
		}
		if(dto.getWAccountNumber()==null || dto.getWAccountNumber().isEmpty()) {
			throw new CustomRestfullException("계좌 번호를  입력하시오", HttpStatus.BAD_REQUEST);
		}
		if(dto.getPassword()==null || dto.getWAccountNumber().isEmpty()) {
			throw new CustomRestfullException("계좌 번호를  입력하시오", HttpStatus.BAD_REQUEST);
		}
		
		//서비스 호출
		accountService.updateAccountWithdraw(dto, principal.getId());
		
		
		return "redirect:/account/list";
	}
	
	@GetMapping("/deposit")
	public String deposit() {
	
		return "account/deposit";
	}
	
	@PostMapping("/deposit")
	public String depositProc(DepositFormDto dto) {
		User principal = (User)session.getAttribute("principal");//원래 princial 오브젝트 타입인데 유저로 다운캐스팅
		
		if(dto.getAmount() == null) {
			throw new UnAuthorizedException("금액을 입력 해주세요", HttpStatus.UNAUTHORIZED);
		}
		if(dto.getAmount().longValue()<=0) {
			throw new UnAuthorizedException("입금 금액이 0원 이하일 수 없습니다", HttpStatus.UNAUTHORIZED);
		}
		if(dto.getDAccountNumber()==null || dto.getDAccountNumber().isEmpty()) {
			throw new UnAuthorizedException("계좌번호를 입력 해주세요", HttpStatus.UNAUTHORIZED);
		}
		//서비스 호출
		accountService.updateAccountDeposit(dto, principal.getId());
		return "redirect:/account/list";
	}
	
	@GetMapping("/transfer")
	public String transfer() {
			
		return "account/transfer";
	}
	
	
	@PostMapping("/transfer")
	public String transferProc(TransferFormDto dto) {
		User principal = (User)session.getAttribute("principal");//원래 princial 오브젝트 타입인데 유저로 다운캐스팅
	
		if(dto.getAmount() == null) {
			throw new UnAuthorizedException("금액을 입력 해주세요", HttpStatus.UNAUTHORIZED);
		}
		if(dto.getAmount().longValue()<=0) {
			throw new UnAuthorizedException("입금 금액이 0원 이하일 수 없습니다", HttpStatus.UNAUTHORIZED);
		}
		if(dto.getWAccountNumber()==null || dto.getWAccountNumber().isEmpty()) {
			throw new UnAuthorizedException("출금 계좌 번호를 입력 해주세요", HttpStatus.UNAUTHORIZED);
		}
		if(dto.getDAccountNumber()==null || dto.getDAccountNumber().isEmpty()) {
			throw new UnAuthorizedException("이체 계좌 번호를 입력 해주세요", HttpStatus.UNAUTHORIZED);
		}
		
		if(dto.getPassword()==null || dto.getPassword().isEmpty()) {
			throw new CustomRestfullException("출금 계좌 비밀번호를  입력하시오", HttpStatus.BAD_REQUEST);
		}
		
		
		//서비스 호출
		//1. dto 넘겨주기
		accountService.updateAccountTransfer(dto, principal.getId());
		
		
		return "redirect:/account/list";
	}
	
	//계좌 상세보기 화면 요청 처리 - 데이터를 입력 받는 방법 정리
	//http://localhost/account/detail/1
	//http://localhost/account/detail/1?type=deposit
	//http://localhost/account/detail/1
	@GetMapping("/detail/{accountId}")
	public String detail(@PathVariable Integer accountId, @RequestParam(name="type", defaultValue = "all", required = false) String type, Model model ) {
		//1. 화면에서 넘겨받은 주소를 맵핑하고 파싱하는 방법 -> pathvariable
		//첫번째 주소에는 파람이 안들어옴 -> 기본값 세팅 가능 @RequestParam(name="type")
		System.out.println("++++deposit"+type);
		//인증 검사 //유효성 검사
		User principal = (User)session.getAttribute("principal");//원래 princial 오브젝트 타입인데 유저로 다운캐스팅
	
		
		//상세 보기 화면 요청시 --> 데이터 내려주어야한다.
		//account 데이터, 접근주체, 거래내역 정보 
		Account account = accountService.findById(accountId);
		System.out.println("들어왔니"+account);
		List<History> historyList = accountService.readHistoryListByAccount(type, accountId);  
		//히스토리에 보낸이 받는이 컬럼이 없음 -> dto를 새로 생성해도 되고 재활용 해도됨
		
		model.addAttribute("principal", principal);
		model.addAttribute("account", account);
		model.addAttribute("historyList",historyList);
		return "account/detail";
	}
}
