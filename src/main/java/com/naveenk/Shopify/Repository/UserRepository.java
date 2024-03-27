package com.naveenk.Shopify.Repository;

import com.naveenk.Shopify.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<User,Integer> {


}
