package com.oas.osmsbackend.domain;

import com.oas.osmsbackend.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * 客户。
 *
 * @author askar882
 * @date 2022/05/16
 */
@Entity
@Table(name = "customers")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private String name;

    @NotNull
    @Enumerated(EnumType.ORDINAL)
    private Gender gender;

    private String email;

    private String phone;

    private Date birthDate;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> addresses;
}
