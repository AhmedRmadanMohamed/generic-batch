package gov.csr.generic.batch.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "API_NIC_PERSON_BASIC_INFO")
public class PersonBasicInfoEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "SOCIAL_ID", length = 500)
    private String socialId;

    @Column(name = "BIRTH_DATE", length = 500)
    private String birthDate;

    @Column(name = "OPERATOR_SOCIAL_ID", length = 500)
    private String operatorSocialId;

    @Column(name = "FIRST_NAME_ARABIC", length = 500)
    private String firstNameArabic;

    @Column(name = "SECOND_NAME_ARABIC", length = 500)
    private String secondNameArabic;

    @Column(name = "THIRD_NAME_ARABIC", length = 500)
    private String thirdNameArabic;

    @Column(name = "FAMILY_NAME_ARABIC", length = 500)
    private String familyNameArabic;

    @Column(name = "NATIONALITY_CODE", length = 500)
    private String nationalityCode;

    @Column(name = "RELIGION_CODE", length = 500)
    private String religionCode;

    @Column(name = "OCCUPATION_CODE", length = 500)
    private String occupationCode;

    @Column(name = "MARITAL_STATUS_CODE", length = 500)
    private String maritalStatusCode;

    @Column(name = "MARITAL_STATUS_DESC_AR", length = 500)
    private String maritalStatusDescAr;

    @Column(name = "MARITAL_STATUS_DESC_EN", length = 500)
    private String maritalStatusDescEn;

    @Column(name = "GENDER_CODE", length = 500)
    private String genderCode;

    @Column(name = "GENDER_DESC_AR", length = 500)
    private String genderDescAr;

    @Column(name = "GENDER_DESC_EN", length = 500)
    private String genderDescEn;

    @Column(name = "LIFE_STATUS_CODE", length = 500)
    private String lifeStatusCode;

    @Column(name = "LIFE_STATUS_DESC_AR", length = 500)
    private String lifeStatusDescAr;

    @Column(name = "LIFE_STATUS_DESC_EN", length = 500)
    private String lifeStatusDescEn;

    @Column(name = "BIRTH_DATE_H", length = 500)
    private String birthDateH;

    @Column(name = "BIRTH_CITY", length = 500)
    private String birthCity;

    @Column(name = "SUB_TRIBE_NAME", length = 500)
    private String subTribeName;

    @Column(name = "REQUEST_TIMESTAMP", length = 500)
    private String requestTimestamp;

    @Column(name = "RESPONSE_TIMESTAMP", length = 500)
    private String responseTimestamp;

    @Column(name = "RESPONSE_BIRTH_DATE", length = 500)
    private String responseBirthDate;
}
