package com.pwd.pwdproject.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pwd.pwdproject.entity.TransactionDetail;

public interface TransactionDetailRepo extends JpaRepository<TransactionDetail, Integer> {

}
