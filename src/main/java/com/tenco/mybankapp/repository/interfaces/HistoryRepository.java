package com.tenco.mybankapp.repository.interfaces;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.tenco.mybankapp.repository.entity.Account;
import com.tenco.mybankapp.repository.entity.History;

@Mapper
public interface HistoryRepository {
	
	//거래내역 등록
	public int insert(History history);
	public int updateById(History history);
	public int deleteById(Integer id);
	public List<History> findAll();
	public List<History> findByIdAndDynamicType(@Param("type") String type, @Param("accountId")Integer accountId);
	
	//거래내역 조회
	//public List< History> findByAccountNumber( String id);
	//동적 쿼리 생성 : 하나의 쿼리로 다양하게 조회함 
	//입금 / 출금 / 전체 : 구분이 되는 키값이 필요함 
	//반드시 두개 이상에 파라미터 사용시 @Param를 사용해야 한다.
	

	

}
