package org.motechproject.whp.adherence.audit;

import org.motechproject.whp.adherence.domain.WeeklyAdherence;
import org.motechproject.whp.patient.domain.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdherenceAuditService {

    private AllAuditLogs allAuditLogs;

    @Autowired
    public AdherenceAuditService(AllAuditLogs allAuditLogs) {
        this.allAuditLogs = allAuditLogs;
    }

    public void log(Patient patient, WeeklyAdherence weeklyAdherence, AuditParams auditParams) {
        AuditLog auditLog = new AuditLog()
                .numberOfDosesTaken(weeklyAdherence.numberOfDosesTaken())
                .providerId(patient.providerId())
                .remark(auditParams.getRemarks())
                .user(auditParams.getUser())
                .sourceOfChange(auditParams.getSourceOfChange().name())
                .patientId(weeklyAdherence.getPatientId())
                .tbId(patient.tbId());

        allAuditLogs.add(auditLog);
    }

    public List<AuditLog> fetchAuditLogs() {
        return allAuditLogs.getAll();
    }
}
