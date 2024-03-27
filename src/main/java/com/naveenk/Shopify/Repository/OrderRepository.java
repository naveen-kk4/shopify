package com.naveenk.Shopify.Repository;

import com.naveenk.Shopify.Model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order,Integer> {
}
