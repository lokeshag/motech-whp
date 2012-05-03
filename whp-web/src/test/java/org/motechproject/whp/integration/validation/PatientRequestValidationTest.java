package org.motechproject.whp.integration.validation;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.internal.matchers.Contains;
import org.motechproject.util.DateUtil;
import org.motechproject.whp.builder.PatientWebRequestBuilder;
import org.motechproject.whp.builder.ProviderRequestBuilder;
import org.motechproject.whp.patient.domain.Gender;
import org.motechproject.whp.patient.domain.Provider;
import org.motechproject.whp.patient.exception.WHPException;
import org.motechproject.whp.patient.repository.AllProviders;
import org.motechproject.whp.patient.repository.SpringIntegrationTest;
import org.motechproject.whp.request.PatientWebRequest;
import org.motechproject.whp.request.ProviderWebRequest;
import org.motechproject.whp.validation.RequestValidator;
import org.motechproject.whp.validation.ValidationScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

@ContextConfiguration(locations = "classpath*:META-INF/spring/applicationContext.xml")
public class PatientRequestValidationTest extends SpringIntegrationTest {

    @Rule
    public ExpectedException exceptionThrown = ExpectedException.none();
    
    @Autowired
    private RequestValidator validator;

    @Autowired
    AllProviders allProviders;

    @Before
    public void setUpDefaultProvider() {
        PatientWebRequest patientWebRequest = new PatientWebRequestBuilder().withDefaults().build();
        String defaultProviderId = patientWebRequest.getProvider_id();
        Provider defaultProvider = new Provider(defaultProviderId, "1234567890", "chambal", DateUtil.now());
        allProviders.add(defaultProvider);
    }

    @Test
    public void shouldNotThrowException_WhenCaseIdIs10Characters() {
        PatientWebRequest webRequest = new PatientWebRequestBuilder().withDefaults().withCaseId("1234567890").build();
        validator.validate(webRequest, ValidationScope.create);
    }

    @Test
    public void shouldNotThrowException_WhenCaseIdIs11Characters() {
        PatientWebRequest webRequest = new PatientWebRequestBuilder().withDefaults().withCaseId("12345678901").build();
        validator.validate(webRequest, ValidationScope.create);
    }

    @Test
    public void shouldThrowExceptionWhenGenderNotEnumerated() {
        expectException("field:gender:The value should be one of : [M, F, O]");
        allProviders.add(new Provider("12345", "1234567890", "chambal", DateUtil.now()));
        PatientWebRequest webRequest = new PatientWebRequestBuilder().withDefaults().withProviderId("12345").withGender("H").build();
        validator.validate(webRequest, ValidationScope.create);
    }

    @Test
    public void shouldNotThrowExceptionWhenGenderIsMale() {
        allProviders.add(new Provider("12345", "1234567890", "chambal", DateUtil.now()));
        PatientWebRequest webRequest = new PatientWebRequestBuilder().withDefaults().withProviderId("12345").withGender(Gender.M.getValue()).build();
        validator.validate(webRequest, ValidationScope.create);
    }

    @Test
    public void shouldNotThrowExceptionWhenGenderIsFemale() {
        allProviders.add(new Provider("12345", "1234567890", "chambal", DateUtil.now()));
        PatientWebRequest webRequest = new PatientWebRequestBuilder().withDefaults().withProviderId("12345").withGender(Gender.F.getValue()).build();
        validator.validate(webRequest, ValidationScope.create);
    }

    @Test
    public void shouldNotThrowExceptionWhenGenderIsOther() {
        allProviders.add(new Provider("12345", "1234567890", "chambal", DateUtil.now()));
        PatientWebRequest webRequest = new PatientWebRequestBuilder().withDefaults().withProviderId("12345").withGender(Gender.O.getValue()).build();
        validator.validate(webRequest, ValidationScope.create);
    }

    @Test
    public void shouldNotThrowException_WhenLastModifiedDateFormatIsCorrect() {
        PatientWebRequest webRequest = new PatientWebRequestBuilder().withDefaults().withLastModifiedDate("03/04/2012 02:20:30").build();
        validator.validate(webRequest, ValidationScope.create);
    }

    @Test
    public void shouldThrowException_WhenLastModifiedDateFormatIsNotTheCorrectDateTimeFormat() {
        expectException("03-04-2012\" is malformed at \"-04-2012");
        PatientWebRequest webRequest = new PatientWebRequestBuilder().withDefaults().withLastModifiedDate("03-04-2012").build();
        validator.validate(webRequest, ValidationScope.create);
    }

    @Test
    public void shouldThrowException_WhenLastModifiedDateFormatDoesNotHaveTimeComponent() {
        expectException("field:date_modified:Invalid format: \"03/04/2012\" is too short");
        PatientWebRequest webRequest = new PatientWebRequestBuilder().withDefaults().withLastModifiedDate("03/04/2012").build();
        validator.validate(webRequest, ValidationScope.create);
    }

    @Test
    public void shouldThrowAnExceptionIfDateIsEmpty() {
        expectException("field:date_modified:Invalid format: \"\"");
        PatientWebRequest webRequest = new PatientWebRequestBuilder().withDefaults().withLastModifiedDate("").build();
        validator.validate(webRequest, ValidationScope.create);
    }

    @Test(expected = WHPException.class)
    public void shouldThrowException_WhenSmearTest1DateFormatIsIncorrect() {
        PatientWebRequest webRequest = new PatientWebRequestBuilder().withDefaults().withSmearTestDate1("03/04/2012  11:23:40").build();
        validator.validate(webRequest, ValidationScope.create);
    }

    @Test(expected = WHPException.class)
    public void shouldThrowException_WhenSmearTest2DateFormatIsIncorrect() {
        PatientWebRequest webRequest = new PatientWebRequestBuilder().withDefaults().withSmearTestDate2("03/04/2012  11:23:40").build();
        validator.validate(webRequest, ValidationScope.create);
    }

    @Test
    public void shouldThrowExceptionWhenTreatmentCategoryIsValid() {
        validate(new PatientWebRequestBuilder().withDefaults().withTreatmentCategory("01").build());
        validate(new PatientWebRequestBuilder().withDefaults().withTreatmentCategory("02").build());
        validate(new PatientWebRequestBuilder().withDefaults().withTreatmentCategory("11").build());
        validate(new PatientWebRequestBuilder().withDefaults().withTreatmentCategory("12").build());
    }

    @Test
    public void shouldNotValidateTreatmentCategoryOnSimpleUpdate() {
        PatientWebRequest webRequest = new PatientWebRequestBuilder().withDefaults().withTreatmentCategory(null).withLastModifiedDate("03/04/2012 02:20:30").build();
        validator.validate(webRequest, ValidationScope.simpleUpdate);
    }

    @Test
    public void shouldThrowException_WhenTreatmentCategoryIsNotValid() {
        expectException("field:treatment_category:must match \"[0|1][1|2]\"");
        PatientWebRequest webRequest = new PatientWebRequestBuilder().withDefaults().withTreatmentCategory("99").build();
        validator.validate(webRequest, ValidationScope.create);
    }

    @Test
    public void shouldNotThrowException_WhenMobileNumberIsEmpty() {
        PatientWebRequest webRequest = new PatientWebRequestBuilder().withDefaults().withMobileNumber("").build();
        validator.validate(webRequest, ValidationScope.create);
    }

    @Test
    public void shouldNotThrowException_WhenMobileNumberIs10Digits() {
        PatientWebRequest webRequest = new PatientWebRequestBuilder().withDefaults().withMobileNumber("1234567890").build();
        validator.validate(webRequest, ValidationScope.create);
    }

    @Test
    public void shouldThrowException_WhenMobileNumberIsLessThan10Digits() {
        expectException("field:mobile_number:Mobile number should be empty or should have 10 digits");
        PatientWebRequest webRequest = new PatientWebRequestBuilder().withDefaults().withMobileNumber("123456789").build();
        validator.validate(webRequest, ValidationScope.create);
    }

    @Test
    public void shouldThrowException_WhenMobileNumberIsMoreThan10Digits() {
        expectException("field:mobile_number:Mobile number should be empty or should have 10 digits");
        PatientWebRequest webRequest = new PatientWebRequestBuilder().withDefaults().withMobileNumber("12345678901").build();
        validator.validate(webRequest, ValidationScope.create);
    }

    @Test
    public void shouldThrowException_WhenMobileNumberIsNotNumeric() {
        expectException("field:mobile_number:Mobile number should be empty or should have 10 digits");
        PatientWebRequest webRequest = new PatientWebRequestBuilder().withDefaults().withMobileNumber("123456789a").build();
        validator.validate(webRequest, ValidationScope.create);
    }

    @Test
    public void shouldThrowException_WhenTbIdFieldIsNotElevenDigits() {
        expectException("field:tb_id:size must be between 11 and 11");
        PatientWebRequest webRequest = new PatientWebRequestBuilder().withDefaults().withTBId("").build();
        validator.validate(webRequest, ValidationScope.create);
    }

    @Test
    public void shouldThrowExceptionWhenAgeIsNotNumeric() {
        expectException("field:age:Age must be numeric");
        PatientWebRequest webRequest = new PatientWebRequestBuilder().withDefaults().withAge("A").build();
        validator.validate(webRequest, ValidationScope.create);
    }

    @Test
    public void shouldThrowExceptionWhenAgeIsAFraction() {
        expectException("field:age:Age must be numeric");
        PatientWebRequest webRequest = new PatientWebRequestBuilder().withDefaults().withAge("10.1").build();
        validator.validate(webRequest, ValidationScope.create);
    }

    @Test
    public void shouldNotThrowExceptionWhenAgeIsNumeric() {
        PatientWebRequest webRequest = new PatientWebRequestBuilder().withDefaults().withAge("10").build();
        validator.validate(webRequest, ValidationScope.create);
    }

    @Test
    public void shouldThrowExceptionWhenWeightIsNotNumeric() {
        expectException("field:weight:Weight must be a real number");
        PatientWebRequest webRequest = new PatientWebRequestBuilder().withDefaults().withWeight("A").build();
        validator.validate(webRequest, ValidationScope.create);
    }

    @Test
    public void shouldThrowExceptionWhenWeightStartWithANumberWithAndHasAnAlphabet() {
        expectException("field:weight:Weight must be a real number");
        PatientWebRequest webRequest = new PatientWebRequestBuilder().withDefaults().withWeight("1A").build();
        validator.validate(webRequest, ValidationScope.create);
    }

    @Test
    public void shouldThrowExceptionWhenProviderIdIsNotFound() {
        expectException("No provider is found with id:providerId");
        PatientWebRequest webRequest = new PatientWebRequestBuilder().withDefaults().withProviderId("providerId").build();
        validator.validate(webRequest, ValidationScope.create);
    }

    @Test
    public void shouldThrowSingleExceptionWhenProviderIdIsNull() {
        try{
            PatientWebRequest webRequest = new PatientWebRequestBuilder().withDefaults().withProviderId(null).build();
            validator.validate(webRequest, ValidationScope.create);
        } catch (WHPException e){
            if(e.getMessage().contains("field:provider_id:may not be null")){
                fail("Not Null validation is not required.");
            }
            assertTrue(e.getMessage().contains("field:provider_id:Provider Id cannot be null"));
        }
    }

    @Test
    public void shouldThrowSingleException_WhenTreatmentCategoryIsNull() {
        try{
            PatientWebRequest webRequest = new PatientWebRequestBuilder().withDefaults().withTreatmentCategory(null).build();
            validator.validate(webRequest, ValidationScope.create);
        } catch (WHPException e){
            if(e.getMessage().contains("field:treatment_category:Treatment Category cannot be null")){
                fail("Not Null validation is not required. Validator implements null validation.");
            }
            assertTrue(e.getMessage().contains("field:treatment_category:may not be null"));
        }
    }

    @Test
    public void shouldNotThrowException_WhenMobileNumberIsNull() {
        PatientWebRequest webRequest = new PatientWebRequestBuilder().withDefaults().withMobileNumber(null).build();
        validator.validate(webRequest, ValidationScope.create);
    }

    //Any enum field
    @Test
    public void shouldThrowSingleExceptionWhenGenderIsNull() {
        try{
            PatientWebRequest webRequest = new PatientWebRequestBuilder().withDefaults().withGender(null).build();
            validator.validate(webRequest, ValidationScope.create);
        } catch (WHPException e){
            if(e.getMessage().contains("field:gender:may not be null")){
                fail("Not Null validation is not required.");
            }
            assertTrue(e.getMessage().contains("field:gender:The value should be one of : [M, F, O]"));
        }
    }

    @Test
    public void shouldThrowSingleExceptionWhenWeightIsNull() {
        try{
            PatientWebRequest webRequest = new PatientWebRequestBuilder().withDefaults().withWeight(null).build();
            validator.validate(webRequest, ValidationScope.create);
        } catch (WHPException e){
            if(e.getMessage().contains("field:weight:Weight cannot be null")){
                fail("Not Null validation is not required. Validator implements null validation.");
            }
            assertTrue(e.getMessage().contains("field:weight:may not be null"));
        }
    }

    @Test
    public void shouldThrowSingleException_WhenLastModifiedDateFormatIsNull() {
        try{
            PatientWebRequest webRequest = new PatientWebRequestBuilder().withDefaults().withLastModifiedDate(null).build();
            validator.validate(webRequest, ValidationScope.create);
        } catch (WHPException e){
            if(e.getMessage().contains("field:date_modified:Date Modified cannot be null")){
                fail("Not Null validation is not required. Validator implements null validation.");
            }
            assertTrue(e.getMessage().contains("field:date_modified:null"));
        }
    }

    @Test
    public void shouldThrowException_WhenTbIdFieldIsNull() {
        try{
            PatientWebRequest webRequest = new PatientWebRequestBuilder().withDefaults().withTBId(null).build();
            validator.validate(webRequest, ValidationScope.create);
        } catch (WHPException e){
            if(e.getMessage().contains("field:tb_id:TB ID cannot be null")){
                fail("Not Null validation is not required. Validator implements null validation.");
            }
            assertTrue(e.getMessage().contains("field:tb_id:may not be null"));
        }
    }

    @Test
    public void shouldThrowExceptionWhenAgeIsNull() {
        try{
            PatientWebRequest webRequest = new PatientWebRequestBuilder().withDefaults().withAge(null).build();
            validator.validate(webRequest, ValidationScope.create);
        } catch (WHPException e){
            if(e.getMessage().contains("field:age:Age cannot be null")){
                fail("Not Null validation is not required. Validator implements null validation.");
            }
            assertTrue(e.getMessage().contains("field:age:may not be null"));
        }
    }

    @Test
    public void shouldNotThrowExceptionWhenProviderIdIsFound() {
        Provider defaultProvider = new Provider("providerId", "1231231231", "chambal", DateUtil.now());
        allProviders.add(defaultProvider);
        PatientWebRequest webRequest = new PatientWebRequestBuilder().withDefaults().withProviderId("providerId").build();
        validator.validate(webRequest, ValidationScope.create);
    }

    @Test
    public void shouldNotThrowExceptionWhenWeightIsAFraction() {
        PatientWebRequest webRequest = new PatientWebRequestBuilder().withDefaults().withWeight("10.1").build();
        validator.validate(webRequest, ValidationScope.create);
    }

    @Test
    public void shouldNotThrowExceptionWhenWeightIsAnInteger() {
        PatientWebRequest webRequest = new PatientWebRequestBuilder().withDefaults().withWeight("10").build();
        validator.validate(webRequest, ValidationScope.create);
    }

    @Test
    public void shouldNotThrowExceptionWhenWeightIsAFractionWithZeroInFractionPart() {
        PatientWebRequest webRequest = new PatientWebRequestBuilder().withDefaults().withWeight("10.0").build();
        validator.validate(webRequest, ValidationScope.create);
    }

    private void expectException(String message) {
        exceptionThrown.expect(WHPException.class);
        exceptionThrown.expectMessage(new Contains(message));
    }

    private void validate(PatientWebRequest patientWebRequest) {
        validator.validate(patientWebRequest, ValidationScope.create);
    }

    @After
    public void tearDown() {
        markForDeletion(allProviders.getAll().toArray());
    }
}