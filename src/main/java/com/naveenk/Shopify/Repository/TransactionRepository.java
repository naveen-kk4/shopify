package com.naveenk.Shopify.Repository;

import com.naveenk.Shopify.Model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction,Integer> {


}
