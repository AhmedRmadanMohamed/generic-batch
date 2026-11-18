package gov.csr.generic.batch.mapper;

import gov.csr.generic.batch.entity.PersonBasicInfoEntity;
import gov.socialdev.nic.model.common.BirthDetails;
import gov.socialdev.nic.model.common.Description;
import gov.socialdev.nic.model.common.Gender;
import gov.socialdev.nic.model.common.LifeStatus;
import gov.socialdev.nic.model.common.MaritalStatus;
import gov.socialdev.nic.model.common.Nationality;
import gov.socialdev.nic.model.common.Occupation;
import gov.socialdev.nic.model.common.PersonData;
import gov.socialdev.nic.model.common.PersonName;
import gov.socialdev.nic.model.common.PersonNameDetails;
import gov.socialdev.nic.model.common.Religion;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PersonBasicInfoMapperTest {

    private final PersonBasicInfoMapper mapper = Mappers.getMapper(PersonBasicInfoMapper.class);

    @Test
    void mapsNestedResponseFieldsToEntity() {
        PersonData response = new PersonData();
        response.setName(new PersonName(
                new PersonNameDetails("محمد", "أحمد", "علي", "السالم"), null));
        response.setNationality(new Nationality("SA", null));
        response.setReligion(new Religion("1", null));
        response.setOccupation(new Occupation("100", null));
        response.setMaritalStatus(new MaritalStatus(
                "2", new Description("متزوج", "Married")));
        response.setGender(new Gender(
                "M", new Description("ذكر", "Male")));
        response.setLifeStatus(new LifeStatus(
                "1", new Description("حي", "Alive")));
        response.setBirthDetails(new BirthDetails("1990-01-01", "الرياض", "1410-06-04"));
        response.setSocialId("9876543210");
        response.setRequestTimestamp("2026-09-27T10:00:00");
        response.setResponseTimestamp("2026-09-27T10:00:01");

        PersonBasicInfoEntity entity = new PersonBasicInfoEntity();
        entity.setSocialId("1234567890");
        entity.setBirthDate("1990-01-01");
        entity.setSubTribeName("السالم");
        mapper.updateEntityFromDto(response, entity);

        assertEquals("1234567890", entity.getSocialId());
        assertEquals("1990-01-01", entity.getBirthDate());
        assertEquals("محمد", entity.getFirstNameArabic());
        assertEquals("أحمد", entity.getSecondNameArabic());
        assertEquals("علي", entity.getThirdNameArabic());
        assertEquals("السالم", entity.getFamilyNameArabic());
        assertEquals("SA", entity.getNationalityCode());
        assertEquals("1", entity.getReligionCode());
        assertEquals("100", entity.getOccupationCode());
        assertEquals("2", entity.getMaritalStatusCode());
        assertEquals("متزوج", entity.getMaritalStatusDescAr());
        assertEquals("Married", entity.getMaritalStatusDescEn());
        assertEquals("M", entity.getGenderCode());
        assertEquals("ذكر", entity.getGenderDescAr());
        assertEquals("Male", entity.getGenderDescEn());
        assertEquals("1", entity.getLifeStatusCode());
        assertEquals("حي", entity.getLifeStatusDescAr());
        assertEquals("Alive", entity.getLifeStatusDescEn());
        assertEquals("1990-01-01", entity.getResponseBirthDate());
        assertEquals("1410-06-04", entity.getBirthDateH());
        assertEquals("الرياض", entity.getBirthCity());
        assertEquals("السالم", entity.getSubTribeName());
    }
}
