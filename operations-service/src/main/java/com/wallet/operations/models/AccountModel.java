package com.wallet.operations.models;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Table(name = "accounts")
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class AccountModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    @Column(length = 10, nullable = false, unique = true)
    private String accountNumber;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal balance;

    @Column(nullable = false)
    private Boolean blocked;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserModel user;

    public boolean isBlocked() {
        return blocked;
    }
}
