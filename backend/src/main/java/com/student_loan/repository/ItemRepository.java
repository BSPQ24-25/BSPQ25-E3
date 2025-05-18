package com.student_loan.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.student_loan.model.Item;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
	Item findByName(String name);
	List<Item> findByOwner(Long ownerId);

	List<Item> findByStatus(Item.ItemStatus status);
}
