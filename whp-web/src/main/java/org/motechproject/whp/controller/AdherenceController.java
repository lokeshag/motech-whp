package org.motechproject.whp.controller;

import org.motechproject.export.annotation.Report;
import org.motechproject.export.annotation.ReportGroup;
import org.motechproject.security.domain.AuthenticatedUser;
import org.motechproject.whp.adherence.domain.Adherence;
import org.motechproject.whp.adherence.domain.AdherenceSource;
import org.motechproject.whp.adherence.domain.WeeklyAdherence;
import org.motechproject.whp.adherence.service.WHPAdherenceService;
import org.motechproject.whp.patient.domain.Patient;
import org.motechproject.whp.patient.repository.AllPatients;
import org.motechproject.whp.uimodel.WeeklyAdherenceForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static org.motechproject.whp.criteria.UpdateAdherenceCriteria.canUpdate;

@Controller
@RequestMapping(value = "/adherence")
@ReportGroup(name = "adherence")
public class AdherenceController extends BaseController {

    private AllPatients allPatients;
    private WHPAdherenceService adherenceService;

    @Autowired
    public AdherenceController(AllPatients allPatients, WHPAdherenceService adherenceService) {
        this.allPatients = allPatients;
        this.adherenceService = adherenceService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/update/{patientId}")
    public String update(@PathVariable("patientId") String patientId, Model uiModel) {
        Patient patient = allPatients.findByPatientId(patientId);
        WeeklyAdherence adherence = adherenceService.currentWeekAdherence(patient);
        if(adherence == null) adherence = adherenceService.currentWeekAdherenceTemplate(patient);
        prepareModel(patient, uiModel, adherence);
        return "adherence/update";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/update/{patientId}")
    public String update(@PathVariable("patientId") String patientId, WeeklyAdherenceForm weeklyAdherenceForm, HttpServletRequest httpServletRequest) {
        AuthenticatedUser authenticatedUser = loggedInUser(httpServletRequest);
        Patient patient = allPatients.findByPatientId(patientId);
        WeeklyAdherence weeklyAdherence = getAdherenceToSave(weeklyAdherenceForm, patient);
        adherenceService.recordAdherence(patientId, weeklyAdherence, authenticatedUser.getUsername(), AdherenceSource.WEB);
        return "forward:/";
    }

    private WeeklyAdherence getAdherenceToSave(WeeklyAdherenceForm weeklyAdherenceForm, Patient patient) {
        WeeklyAdherence adherence = adherenceService.currentWeekAdherence(patient);
        if(adherence == null)  return weeklyAdherenceForm.weeklyAdherence();
        return weeklyAdherenceForm.updatedWeeklyAdherence();
    }

    @Report
    public List<Adherence> adherenceReport(int pageNumber) {
        return adherenceService.allAdherenceData(pageNumber - 1, 10000);
    }

    private void prepareModel(Patient patient, Model uiModel, WeeklyAdherence adherence) {
        WeeklyAdherenceForm weeklyAdherenceForm = new WeeklyAdherenceForm(adherence, patient.getTreatmentInterruptions());
        uiModel.addAttribute("referenceDate", weeklyAdherenceForm.getReferenceDateString());
        uiModel.addAttribute("adherence", weeklyAdherenceForm);
        uiModel.addAttribute("readOnly", !(canUpdate(patient)));
    }

}
