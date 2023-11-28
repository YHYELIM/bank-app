package com.tenco.mybankapp.repository.interfaces;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.tenco.mybankapp.dto.SignInFormDto;
import com.tenco.mybankapp.repository.entity.User;

@Mapper
public interface UserRepository {
	
	// 사용자 등록
	public int insert(User user);
	// 사용자 수정
	public int updateById(User user);
	// 사용자 삭제
	public int deleteById(Integer id);
	// 사용자 한명 조회
	public User findById(Integer id);
	// 사용자 전체 조회
	public List<User>findAll();// 전체 조회라서 매개변수 받을 필요 없음
	
	//사용자에 이름과 비번으로 조회
	public User findByUsernameAndPassword(	SignInFormDto dto);
	

}
