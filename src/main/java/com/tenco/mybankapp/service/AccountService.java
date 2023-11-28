package com.tenco.mybankapp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tenco.mybankapp.dto.DepositFormDto;
import com.tenco.mybankapp.dto.SaveFormDto;
import com.tenco.mybankapp.dto.TransferFormDto;
import com.tenco.mybankapp.dto.WithdrawFormDto;
import com.tenco.mybankapp.handler.exception.CustomRestfullException;
import com.tenco.mybankapp.handler.exception.UnAuthorizedException;
import com.tenco.mybankapp.repository.entity.Account;
import com.tenco.mybankapp.repository.entity.History;
import com.tenco.mybankapp.repository.interfaces.AccountRepository;
import com.tenco.mybankapp.repository.interfaces.HistoryRepository;

@Service  //IoC 대상 싱글톤으로 관리
public class AccountService {
	
	//추상클래스로 주입 받아야함 구현 클래스로 주입을 하면 구현클래스가 변경이 되면 주입받은 클래스들도 다 변경 되어야함
	@Autowired 
	private AccountRepository accountRepository;
	
	@Autowired
	private HistoryRepository historyRepository;
	
	/*
	 * 계좌 생성 기능
	 * @param dto
	 * @param pricipalId
	 * */
	@Transactional
	public void createAccount(SaveFormDto dto, Integer principalId) {
		
		//계좌 중복 여부 확인
	
		Account account = Account.builder().number(dto.getNumber())
				.password(dto.getPassword())
				.balance(dto.getBalance())
				.userId(principalId)
				.build();
		
		
		int resultaRowCount= accountRepository.insert(account);
		if(resultaRowCount != 1) {
			throw new CustomRestfullException("계좌 생성 실패", HttpStatus.BAD_GATEWAY);
		}
	}
	
	//계좌 목록 보기 기능
	public List<Account> readAccountList(Integer userId){
		List<Account> list = accountRepository.findByUserId(userId);
		return list;
	}

	//출금 기능 로직 고민해보기
	//1. 계좌 존재 여부 확인 -> select
	//2. 본인 계좌 여부 확인 -> select
	//3. 계좌 비번 확인
	//4. 잔액 여부 확인
	//5. 출금 처리 --> update
	//6. 거래 내역 등록 --> insert
	//7. 트랜잭션 처리
	
	@Transactional
	public void updateAccountWithdraw(WithdrawFormDto dto, Integer principalId) {
	//1.account에 대한 select 쿼리
		Account accountEntity = accountRepository.findByNumber(dto.getWAccountNumber());
		
		//1. 해당 계좌가 없음 
		if(accountEntity == null) {
			throw new CustomRestfullException("해당 계좌가 없습니다", HttpStatus.BAD_REQUEST);
		}
		//2. 컨트롤러에서 프린시플아이디 받을거임
		if(accountEntity.getUserId() != principalId) {
			throw new CustomRestfullException("본인 소유 계좌가 아닙니다",HttpStatus.UNAUTHORIZED);
		}
		//3.
		if(accountEntity.getPassword().equals(dto.getPassword())==false){
			throw new CustomRestfullException("출금계좌 비밀번호가 틀렸습니다", HttpStatus.BAD_REQUEST);
		}
		//4
		if(accountEntity.getBalance() <dto.getAmount()) {
			 throw new CustomRestfullException("계좌 잔액이 부족합니다", HttpStatus.BAD_REQUEST);
		}
		//5. (객체 모델 상태값 변경 처리) 계좌의 잔액을 업데이트
		accountEntity.withdraw(dto.getAmount());
		accountRepository.updateById(accountEntity);
		
		
		//6. 거래 내역 등록
		History history = new History();
		history.setAmount(dto.getAmount());
		
		//출금 거래 내역에서는 사용자가 출금 후에 잔액을 입력합니다 잔액의 history insert
		history.setWBalance(accountEntity.getBalance());// accountEntity의 변경된 상태값을 넣어주면됨
		history.setDBalance(null);
		history.setWAccountId(accountEntity.getId());
		history.setDAccountId(null);
		
		int resultRowCount = historyRepository.insert(history);
		if(resultRowCount !=1) {
			throw new  CustomRestfullException("정상처리 되지 않았습니다", HttpStatus.BAD_REQUEST);
		}
	}

	//입금 처리 기능
	//트랜잭션 처리 
	//1. 계좌 존재 여부 확인
	//2. 입금 처리 -> update
	//3. 거래 내역 등록 처리 -> insert
	@Transactional
	public void updateAccountDeposit(DepositFormDto dto, Integer principalId ) {
		Account accountEntity = accountRepository.findByNumber(dto.getDAccountNumber());
		if(accountEntity == null) {
			throw new UnAuthorizedException("해당 계좌는 존재하지 않습니다", HttpStatus.UNAUTHORIZED);
		}
		//객체 상태값 변경
		accountEntity.deposit(dto.getAmount());
		accountRepository.updateById(accountEntity);
		
		//거래 내역 등록
		History history = new History();
		history.setAmount(dto.getAmount());
		history.setWBalance(null);
		history.setDBalance(accountEntity.getBalance());
		history.setDAccountId(null);;
		history.setDAccountId(accountEntity.getId());
		
		int resultRowCount = historyRepository.insert(history);
		if(resultRowCount !=1) {
			throw new  CustomRestfullException("정상처리 되지 않았습니다", HttpStatus.BAD_REQUEST);
		}
		
		
	}
	
	@Transactional
	public void updateAccountTransfer(TransferFormDto dto,  Integer principalId) {//컨트롤러에서 넘겨받는 세션 아이디
		//1. 
		Account withdrawAccountEntity = accountRepository.findByNumber(dto.getWAccountNumber());
		if(withdrawAccountEntity == null) {
			throw new UnAuthorizedException("해당 출금 계좌는 존재하지 않습니다", HttpStatus.UNAUTHORIZED);
		}
		
		//2.
		Account dipositAccountEntity = accountRepository.findByNumber(dto.getDAccountNumber());
		if(dipositAccountEntity == null) {
			throw new UnAuthorizedException("해당 입금 계좌는 존재하지 않습니다", HttpStatus.UNAUTHORIZED);
		}
		
		//3.
		withdrawAccountEntity.checkOwner(principalId);
		//4.
		withdrawAccountEntity.checkPassword(dto.getPassword());
		//5.
		withdrawAccountEntity.checkBalance(dto.getAmount());

		//6. 객체 상태 변경
		withdrawAccountEntity.withdraw(dto.getAmount());
		//update 처리
		accountRepository.updateById(withdrawAccountEntity);
		
		//입금 처리
		dipositAccountEntity.deposit(dto.getAmount());
		accountRepository.updateById(dipositAccountEntity);
		
		
		//거래 내역 등록
		
		History history = new History();
		history.setAmount(dto.getAmount());
		history.setWBalance(withdrawAccountEntity.getBalance());
		history.setDBalance(dipositAccountEntity.getBalance());
		history.setWAccountId(withdrawAccountEntity.getId());
		history.setDAccountId(dipositAccountEntity.getId());
		
		int resultRowCount = historyRepository.insert(history);
		if(resultRowCount !=1) {
			throw new  CustomRestfullException("정상처리 되지 않았습니다", HttpStatus.BAD_REQUEST);
		}
		
	}
	
	/*
	 * 단일 계좌 조회
	 * @param type =[all, deposit, withdraw]
	 * @param accountId
	 * @return  입금 내역, 출금 내역, 입출금 내역(3가지 타입)
	 * */
	public Account findById(Integer accountId) {
		Account accountEntity = accountRepository.findById(accountId);
		if(accountEntity == null) {
			throw new CustomRestfullException("해당 계좌를 찾을 수 없습니다", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return accountEntity;
	}

	public List<History> readHistoryListByAccount(String type, Integer accountId) {
		List<	History> historyEntity = historyRepository.findByIdAndDynamicType(type, accountId);
		return historyEntity;
	}

	

}
