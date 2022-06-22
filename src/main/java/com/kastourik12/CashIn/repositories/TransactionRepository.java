package com.kastourik12.CashIn.repositories;

import com.kastourik12.CashIn.models.CustomUser;
import com.kastourik12.CashIn.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Long> {
    List<Transaction> findAllByFromId(Long id);

    List<Transaction> findAllByToId(Long id);
}
