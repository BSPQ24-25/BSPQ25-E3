package com.student_loan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.student_loan.model.Item;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
}