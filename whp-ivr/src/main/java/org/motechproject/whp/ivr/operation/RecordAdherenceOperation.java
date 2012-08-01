package org.motechproject.whp.ivr.operation;

import org.motechproject.decisiontree.FlowSession;
import org.motechproject.decisiontree.model.INodeOperation;
import org.motechproject.whp.adherence.audit.contract.AuditParams;
import org.motechproject.whp.adherence.domain.AdherenceSource;
import org.motechproject.whp.adherence.domain.WeeklyAdherenceSummary;
import org.motechproject.whp.applicationservice.orchestrator.TreatmentUpdateOrchestrator;
import org.motechproject.whp.ivr.util.IvrSession;
import org.motechproject.whp.reporting.service.ReportingPublisherService;
import org.motechproject.whp.reports.webservice.request.AdherenceCaptureRequest;

import static org.motechproject.whp.common.domain.TreatmentWeekInstance.currentWeekInstance;
import static org.motechproject.whp.ivr.builder.AdherenceCaptureBuilder.adherenceCapture;

public class RecordAdherenceOperation implements INodeOperation {

    private String currentPatientId;
    private TreatmentUpdateOrchestrator treatmentUpdateOrchestrator;
    private ReportingPublisherService reportingService;

    public RecordAdherenceOperation(String currentPatientId, TreatmentUpdateOrchestrator treatmentUpdateOrchestrator, ReportingPublisherService reportingService) {
        this.treatmentUpdateOrchestrator = treatmentUpdateOrchestrator;
        this.reportingService = reportingService;
        this.currentPatientId = currentPatientId;
    }

    @Override
    public void perform(String userInput, FlowSession session) {
        IvrSession ivrSession = new IvrSession(session);
        recordAdherence(ivrSession);
    }

    private void recordAdherence(IvrSession ivrSession) {
        AuditParams auditParams = new AuditParams(ivrSession.providerId(), AdherenceSource.IVR, "");
        WeeklyAdherenceSummary weeklyAdherenceSummary = new WeeklyAdherenceSummary(currentPatientId, currentWeekInstance());
        weeklyAdherenceSummary.setDosesTaken(ivrSession.adherenceInputForCurrentPatient());
        treatmentUpdateOrchestrator.recordAdherence(currentPatientId, weeklyAdherenceSummary, auditParams);
        publishAdherenceSubmissionEvent(ivrSession.providerId(), ivrSession.getMobileNumber(), ivrSession.adherenceInputForCurrentPatient(), ivrSession.callId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RecordAdherenceOperation that = (RecordAdherenceOperation) o;

        if (currentPatientId != null ? !currentPatientId.equals(that.currentPatientId) : that.currentPatientId != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return currentPatientId != null ? currentPatientId.hashCode() : 0;
    }

    private void publishAdherenceSubmissionEvent(String providerId, String mobileNumber, Integer userInput, String callId) {
        AdherenceCaptureRequest request = adherenceCapture()
                .withInput(userInput)
                .byProvider(providerId)
                .forPatient(currentPatientId)
                .throughMobile(mobileNumber)
                .onCall(callId)
                .build();
        reportingService.reportAdherenceCapture(request);
    }
}
