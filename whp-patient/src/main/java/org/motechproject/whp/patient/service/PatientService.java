package org.motechproject.whp.patient.service;

import org.motechproject.util.DateUtil;
import org.motechproject.whp.patient.contract.PatientRequest;
import org.motechproject.whp.patient.contract.TreatmentUpdateRequest;
import org.motechproject.whp.patient.domain.Patient;
import org.motechproject.whp.patient.domain.ProvidedTreatment;
import org.motechproject.whp.patient.domain.Treatment;
import org.motechproject.whp.patient.domain.criteria.CriteriaErrors;
import org.motechproject.whp.patient.domain.criteria.UpdateTreatmentCriteria;
import org.motechproject.whp.patient.exception.WHPDomainException;
import org.motechproject.whp.patient.repository.AllPatients;
import org.motechproject.whp.patient.repository.AllTreatments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.motechproject.util.DateUtil.today;
import static org.motechproject.whp.patient.mapper.PatientMapper.*;

@Service
public class PatientService {

    private AllTreatments allTreatments;
    private AllPatients allPatients;
    private UpdateTreatmentCriteria updateTreatmentCriteria;
    private final String CANNOT_OPEN_NEW_TREATMENT = "Cannot open new treatment for this case: ";
    private final String CANNOT_CLOSE_CURRENT_TREATMENT = "Cannot close current treatment for case: ";

    @Autowired
    public PatientService(AllPatients allPatients, AllTreatments allTreatments, UpdateTreatmentCriteria updateTreatmentCriteria) {
        this.allPatients = allPatients;
        this.allTreatments = allTreatments;
        this.updateTreatmentCriteria = updateTreatmentCriteria;
    }

    public void createPatient(PatientRequest patientRequest) {
        Patient patient = mapBasicInfo(patientRequest);
        Treatment treatment = createTreatment(patientRequest);

        ProvidedTreatment providedTreatment = mapProvidedTreatment(patientRequest, treatment);
        patient.addProvidedTreatment(providedTreatment);
        allPatients.add(patient);
    }

    public void simpleUpdate(PatientRequest patientRequest) {
        Patient patient = allPatients.findByPatientId(patientRequest.getCase_id());
        if (patient == null) {
            throw new WHPDomainException("Invalid case-id. No such patient.");
        }

        Patient updatedPatient = mapUpdates(patientRequest, patient);
        allPatients.update(updatedPatient);
    }

    public void startOnTreatment(String patientId) {
        Patient patient = allPatients.findByPatientId(patientId);
        patient.getCurrentProvidedTreatment().getTreatment().setDoseStartDate(DateUtil.today());
        allPatients.update(patient);
    }

    public void performTreatmentUpdate(TreatmentUpdateRequest treatmentUpdateRequest) {
        Patient patient = allPatients.findByPatientId(treatmentUpdateRequest.getCase_id());
        if (patient == null) {
            throw new WHPDomainException("Invalid case-id. No such patient.");
        }
        CriteriaErrors criteriaErrors = new CriteriaErrors();
        switch (treatmentUpdateRequest.getTreatment_update()) {
            case NewTreatment:
                if (updateTreatmentCriteria.canOpenNewTreatment(treatmentUpdateRequest, criteriaErrors)) {
                    addNewTreatmentForCategoryChange(treatmentUpdateRequest, patient);
                } else {
                    throw new WHPDomainException(CANNOT_OPEN_NEW_TREATMENT + criteriaErrors);
                }
                break;
            case CloseTreatment:
                if (updateTreatmentCriteria.canCloseCurrentTreatment(treatmentUpdateRequest, criteriaErrors)){
                    closeCurrentTreatment(patient, treatmentUpdateRequest);
                } else {
                    throw new WHPDomainException(CANNOT_CLOSE_CURRENT_TREATMENT + criteriaErrors);
                }
        }
    }

    private Treatment createTreatment(PatientRequest patientRequest) {
        Treatment treatment = mapTreatmentInfo(patientRequest);
        allTreatments.add(treatment);
        return treatment;
    }

    private void addNewTreatmentForCategoryChange(TreatmentUpdateRequest treatmentUpdateRequest, Patient patient) {
        Treatment newTreatment = createNewTreatmentFrom(patient, treatmentUpdateRequest);
        allTreatments.add(newTreatment);
        ProvidedTreatment newProvidedTreatment = createNewProvidedTreatmentForTreatmentCategoryChange(patient, treatmentUpdateRequest, newTreatment);
        patient.addProvidedTreatment(newProvidedTreatment);
        patient.setLastModifiedDate(treatmentUpdateRequest.getDate_modified());
        allPatients.update(patient);
    }

    private void closeCurrentTreatment(Patient patient, TreatmentUpdateRequest treatmentUpdateRequest) {
        ProvidedTreatment currentProvidedTreatment = patient.getCurrentProvidedTreatment();
        currentProvidedTreatment.setEndDate(today());
        Treatment treatment = currentProvidedTreatment.getTreatment();
        treatment.setEndDate(today());
        treatment.setReasonForClosure(treatmentUpdateRequest.getReason_for_closure());
        treatment.setTreatmentComplete(treatmentUpdateRequest.getTreatment_complete());
        patient.setLastModifiedDate(treatmentUpdateRequest.getDate_modified());
        allTreatments.update(treatment);
        allPatients.update(patient);
    }
}
