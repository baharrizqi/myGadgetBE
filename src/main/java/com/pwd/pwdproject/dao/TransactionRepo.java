package com.pwd.pwdproject.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.PathVariable;

import com.pwd.pwdproject.entity.Transaction;

public interface TransactionRepo extends JpaRepository<Transaction, Integer> {

	@Query(value = "SELECT * FROM transaction tr join transaction_detail td on tr.id = td.transaction_id where user_id= :userId group by transaction_id order by transaction_id desc",nativeQuery = true)
	public Iterable<Transaction> fillTransaction(@PathVariable int userId);
}
