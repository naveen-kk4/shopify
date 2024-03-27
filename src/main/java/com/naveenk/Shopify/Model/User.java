package com.naveenk.Shopify.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    private Integer userId;

   private  boolean isOffFiveUsed;

   private boolean isOffTenUsed;
   @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<Order> orderList = new ArrayList<>();


}
