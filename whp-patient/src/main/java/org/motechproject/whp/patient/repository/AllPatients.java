package org.motechproject.whp.patient.repository;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.GenerateView;
import org.ektorp.support.View;
import org.motechproject.dao.MotechBaseRepository;
import org.motechproject.whp.patient.domain.Patient;
import org.motechproject.whp.patient.domain.Therapy;
import org.motechproject.whp.patient.domain.Treatment;
import org.motechproject.whp.patient.exception.WHPErrorCode;
import org.motechproject.whp.patient.exception.WHPRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class AllPatients extends MotechBaseRepository<Patient> {

    AllTherapies allTherapies;

    @Autowired
    public AllPatients(@Qualifier("whpDbConnector") CouchDbConnector dbCouchDbConnector, AllTherapies allTherapies) {
        super(Patient.class, dbCouchDbConnector);
        this.allTherapies = allTherapies;
    }

    @Override
    public void add(Patient patient) {
        Patient savedPatient = findByPatientId(patient.getPatientId());
        if (savedPatient != null) {
            throw new WHPRuntimeException(WHPErrorCode.DUPLICATE_CASE_ID);
        }
        ArrayList<WHPErrorCode> errorCodes = new ArrayList<WHPErrorCode>();
        if (!patient.isValid(errorCodes)) {
            throw new WHPRuntimeException(errorCodes);
        }
        super.add(patient);
    }

    @Override
    public void update(Patient patient) {
        allTherapies.update(patient.latestTherapy());
        ArrayList<WHPErrorCode> errorCodes = new ArrayList<WHPErrorCode>();
        if (!patient.isValid(errorCodes)) {
            throw new WHPRuntimeException(errorCodes);
        }
        super.update(patient);
    }

    @GenerateView
    public Patient findByPatientId(String patientId) {
        if (patientId == null)
            return null;
        ViewQuery find_by_patientId = createQuery("by_patientId").key(patientId.toLowerCase()).includeDocs(true);
        Patient patient = singleResult(db.queryView(find_by_patientId, Patient.class));
        if (patient != null)
            loadPatientDependencies(patient);
        return patient;
    }

    @View(name = "find_by_providerId", map = "function(doc) {if (doc.type ==='Patient' && doc.currentTreatment) {emit([doc.currentTreatment.providerId, doc.firstName], doc._id);}}")
    public List<Patient> findByCurrentProviderId(String providerId) {
        if (providerId == null)
            return new ArrayList<Patient>();
        String keyword = providerId.toLowerCase();
        ComplexKey startKey = ComplexKey.of(keyword, null);
        ComplexKey endKey = ComplexKey.of(keyword, ComplexKey.emptyObject());
        ViewQuery q = createQuery("find_by_providerId").startKey(startKey).endKey(endKey).includeDocs(true).inclusiveEnd(true);
        List<Patient> patients = db.queryView(q, Patient.class);
        for (Patient patient : patients) {
            loadPatientDependencies(patient);
        }
        return patients;
    }

    @View(name = "find_by_provider_having_active_treatment", map = "function(doc) {if (doc.type ==='Patient' && doc.currentTreatment && doc.onActiveTreatment === true) {emit([doc.currentTreatment.providerId, doc.firstName], doc._id);}}")
    public List<Patient> getAllWithActiveTreatmentFor(String providerId) {
        if (providerId == null)
            return new ArrayList<Patient>();
        String keyword = providerId.toLowerCase();
        ComplexKey startKey = ComplexKey.of(keyword, null);
        ComplexKey endKey = ComplexKey.of(keyword, ComplexKey.emptyObject());
        ViewQuery q = createQuery("find_by_provider_having_active_treatment").startKey(startKey).endKey(endKey).includeDocs(true).inclusiveEnd(true);
        List<Patient> patients = db.queryView(q, Patient.class);
        for (Patient patient : patients) {
            loadPatientDependencies(patient);
        }
        return patients;
    }

    @View(name = "having_active_treatment", map = "function(doc) {if (doc.type ==='Patient' && doc.currentTreatment && doc.onActiveTreatment === true) {emit(doc.firstName, doc._id);}}")
    public List<Patient> getAllWithActiveTreatment() {
        ViewQuery q = createQuery("having_active_treatment").includeDocs(true).inclusiveEnd(true);
        List<Patient> patients = db.queryView(q, Patient.class);
        for (Patient patient : patients) {
            loadPatientDependencies(patient);
        }
        return patients;
    }

    private void loadPatientDependencies(Patient patient) {
        Treatment latestTreatment = patient.getCurrentTreatment();
        Therapy latestTherapy = allTherapies.get(latestTreatment.getTherapyDocId());
        latestTreatment.setTherapy(latestTherapy);

        for (Treatment treatment : patient.getTreatments()) {
            Therapy therapy = allTherapies.get(treatment.getTherapyDocId());
            treatment.setTherapy(therapy);
        }
    }
}
