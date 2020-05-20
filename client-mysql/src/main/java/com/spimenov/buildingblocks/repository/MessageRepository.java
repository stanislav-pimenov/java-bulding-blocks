package com.spimenov.buildingblocks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Stanislav Pimenov
 */
@Repository
public interface MessageRepository extends JpaRepository<Integer, Message> {

}
