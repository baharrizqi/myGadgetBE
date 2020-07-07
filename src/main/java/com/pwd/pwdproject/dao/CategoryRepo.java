package com.pwd.pwdproject.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pwd.pwdproject.entity.Category;

public interface CategoryRepo extends JpaRepository<Category, Integer> {

}
