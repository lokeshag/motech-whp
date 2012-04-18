package org.motechproject.whp.patient.domain;

import lombok.Data;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;

import java.util.ArrayList;
import java.util.List;

@Data
@TypeDiscriminator("doc.type == 'Patient'")
public class Patient extends MotechBaseDataObject {

    private String patientId;
    private String firstName;
    private String lastName;
    private Gender gender;
    private PatientType patientType;
    private String phoneNumber;

    private SmearTestResult smearTestResult;

    List<Treatment> treatments = new ArrayList<Treatment>();

    public Patient(){
    }

    public Patient(String patientId, String firstName, String lastName, Gender gender, PatientType patientType, String phoneNumber) {
        this.patientId = patientId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.patientType = patientType;
        this.phoneNumber = phoneNumber;
    }

    public void addTreatment(Treatment treatment) {
        treatments.add(treatment);
    }

}
