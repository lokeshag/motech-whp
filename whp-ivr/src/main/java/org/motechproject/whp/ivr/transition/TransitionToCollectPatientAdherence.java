package org.motechproject.whp.ivr.transition;

import org.motechproject.decisiontree.model.ITransition;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.util.DateUtil;
import org.motechproject.whp.ivr.CallStatus;
import org.motechproject.whp.ivr.WhpIvrMessage;
import org.motechproject.whp.ivr.operation.PublishCallLogOperation;
import org.motechproject.whp.ivr.session.IvrSession;
import org.motechproject.whp.reporting.service.ReportingPublisherService;
import org.springframework.beans.factory.annotation.Autowired;

import static org.motechproject.whp.ivr.prompts.CallCompletionPrompts.callCompletionPromptsAfterCapturingAdherence;
import static org.motechproject.whp.ivr.prompts.CaptureAdherencePrompts.captureAdherencePrompts;

public abstract class TransitionToCollectPatientAdherence implements ITransition {

    @Autowired
    protected WhpIvrMessage whpIvrMessage;

    @Autowired
    protected ReportingPublisherService reportingPublisherService;

    public TransitionToCollectPatientAdherence() {
    }

    public TransitionToCollectPatientAdherence(WhpIvrMessage whpIvrMessage, ReportingPublisherService reportingPublisherService) {
        this.whpIvrMessage = whpIvrMessage;
        this.reportingPublisherService = reportingPublisherService;
    }

    protected void addTransitionsAndPromptsForNextPatient(IvrSession ivrSession, Node nextNode) {
        if (ivrSession.hasNextPatient()) {
            ivrSession.nextPatient();
            addTransitionsAndPromptsForCurrentPatient(nextNode, ivrSession);
        } else {
            nextNode.addPrompts(callCompletionPromptsAfterCapturingAdherence(whpIvrMessage, ivrSession.countOfAllPatients(), ivrSession.countOfCurrentPatientsWithAdherence()));
            nextNode.addOperations(new PublishCallLogOperation(reportingPublisherService, CallStatus.VALID_ADHERENCE_CAPTURE, DateUtil.now()));
        }
    }

    protected void addTransitionsAndPromptsForCurrentPatient(Node node, IvrSession ivrSession) {
        node.addPrompts(captureAdherencePrompts(whpIvrMessage,
                ivrSession.currentPatientId(),
                ivrSession.currentPatientNumber()));
        node.addTransition("?", new AdherenceCaptureTransition());
    }
}
