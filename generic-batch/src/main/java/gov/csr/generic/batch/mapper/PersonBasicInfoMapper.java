package gov.csr.generic.batch.mapper;

import gov.csr.generic.batch.entity.PersonBasicInfoEntity;
import gov.socialdev.nic.model.common.PersonData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface PersonBasicInfoMapper {

    @Mapping(target = "socialId", ignore = true)
    @Mapping(target = "firstNameArabic", source = "name.arabic.firstName")
    @Mapping(target = "secondNameArabic", source = "name.arabic.secondName")
    @Mapping(target = "thirdNameArabic", source = "name.arabic.thirdName")
    @Mapping(target = "familyNameArabic", source = "name.arabic.familyName")
    @Mapping(target = "nationalityCode", source = "nationality.code")
    @Mapping(target = "religionCode", source = "religion.code")
    @Mapping(target = "occupationCode", source = "occupation.code")
    @Mapping(target = "maritalStatusCode", source = "maritalStatus.code")
    @Mapping(target = "maritalStatusDescAr", source = "maritalStatus.description.arabic")
    @Mapping(target = "maritalStatusDescEn", source = "maritalStatus.description.english")
    @Mapping(target = "genderCode", source = "gender.code")
    @Mapping(target = "genderDescAr", source = "gender.description.arabic")
    @Mapping(target = "genderDescEn", source = "gender.description.english")
    @Mapping(target = "lifeStatusCode", source = "lifeStatus.code")
    @Mapping(target = "lifeStatusDescAr", source = "lifeStatus.description.arabic")
    @Mapping(target = "lifeStatusDescEn", source = "lifeStatus.description.english")
    @Mapping(target = "responseBirthDate", source = "birthDetails.birthDate")
    @Mapping(target = "birthCity", source = "birthDetails.birthCity")
    @Mapping(target = "birthDateH", source = "birthDetails.birthDateH")
    void updateEntityFromDto(PersonData dto,
                             @MappingTarget PersonBasicInfoEntity entity);
}
