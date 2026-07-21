package com.aiecommerce.product.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "categories")
public class Category extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @UuidGenerator
    private String id;
    @NotNull
    private String name;


    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Category  parent;

    @OneToMany(mappedBy = "parent")
    private List<Category> child;
    @OneToMany(mappedBy = "category")
    private List<Product> products;

}
