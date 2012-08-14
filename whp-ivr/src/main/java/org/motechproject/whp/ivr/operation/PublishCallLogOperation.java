package org.motechproject.whp.ivr.operation;

import lombok.EqualsAndHashCode;
import org.joda.time.DateTime;
import org.motechproject.decisiontree.FlowSession;
import org.motechproject.decisiontree.model.INodeOperation;
import org.motechproject.whp.ivr.CallStatus;
import org.motechproject.whp.ivr.session.IvrSession;
import org.motechproject.whp.reporting.service.ReportingPublisherService;
import org.motechproject.whp.reports.contract.CallLogRequest;

@EqualsAndHashCode
public class PublishCallLogOperation implements INodeOperation {

    private ReportingPublisherService reportingPublisherService;
    private CallStatus callStatus;
    private DateTime callEndTime;

    public PublishCallLogOperation() {
    }

    public PublishCallLogOperation(ReportingPublisherService reportingPublisherService, CallStatus callStatus, DateTime callEndTime) {
        this.reportingPublisherService = reportingPublisherService;
        this.callStatus = callStatus;
        this.callEndTime = callEndTime;
    }

    @Override
    public void perform(String userInput, FlowSession session) {
        IvrSession ivrSession = new IvrSession(session);
        CallLogRequest callLogRequest = buildCallLog(ivrSession);

        reportingPublisherService.reportCallLog(callLogRequest);
    }

    private CallLogRequest buildCallLog(IvrSession ivrSession) {
        CallLogRequest callLogRequest = new CallLogRequest();
        callLogRequest.setCallStatus(callStatus.value());
        callLogRequest.setCallId(ivrSession.callId());
        callLogRequest.setCalledBy(ivrSession.providerId());
        callLogRequest.setProviderId(ivrSession.providerId());
        callLogRequest.setStartTime(ivrSession.callStartTime().toDate());
        callLogRequest.setEndTime(callEndTime.toDate());
        callLogRequest.setTotalPatients(ivrSession.countOfAllPatients());
        callLogRequest.setAdherenceCaptured(ivrSession.countOfPatientsWithAdherenceRecordedInThisSession());
        callLogRequest.setAdherenceNotCaptured(ivrSession.countOfCurrentPatientsWithoutAdherence());
        return callLogRequest;
    }
}
