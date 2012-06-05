<#import "/spring.ftl" as spring />
<#import "../layout/default.ftl" as layout>
<#include "../layout/legend.ftl">
<@layout.defaultLayout "Patient List">
    <#if message?exists && (message?length>0)>
        <div class="adherence-message-alert row text-center alert alert-info fade in">
            <button class="close" data-dismiss="alert">&times;</button>
        ${message}
        <#assign message=""/>
        </div>
    </#if>
<@legend key1="paused" value1="Current Treatment Paused" span="span13"/>
<div class="row">
    <div>
        <table id="patientList" class="table table-bordered table-condensed">
            <thead>
            <tr>
                <th>Patient ID</th>
                <th>TB-ID</th>
                <th>Name</th>
                <th>Age</th>
                <th>Gender</th>
                <th>Village</th>
                <th>Treatment Category</th>
                <th>Treatment Start Date</th>
                <th>Adherence</th>
            </tr>
            </thead>
            <tbody>
                <#if patientList?size == 0>
                <tr>
                    <td style="text-align: center" colspan="9">No patients to show</td>
                </tr>
                <#else>
                    <#list patientList as patient>
                    <tr id="patientList_${patient.patientId}" class="<#if patient.currentTreatmentPaused>paused</#if>">
                        <td class="patientId"><b>${patient.patientId}</b></td>
                        <td class="tbId">${patient.currentTreatment.tbId}</td>
                        <td class="name">${patient.firstName?cap_first} ${patient.lastName?cap_first}</td>
                        <td>${patient.currentTreatment.therapy.patientAge!}</td>
                        <td id="patient_${patient.patientId}_Gender">${patient.gender}</td>
                        <td id="patient_${patient.patientId}_Village">${patient.currentTreatment.patientAddress.address_village}</td>
                        <td id="patient_${patient.patientId}_TreatmentCategory">${patient.currentTreatment.therapy.treatmentCategory.name}</td>
                        <td id="patient_${patient.patientId}_TreatmentStartDate">
                            <#if patient.currentTreatment.therapy.startDate?? >
                                ${patient.currentTreatment.therapy.startDate?date?string("dd/mm/yyyy") }
                            </#if>
                        </td>
                        <td class="updateAdherenceLink">
                            <#if !patient.currentTreatmentClosed>
                                <a href="<@spring.url '/adherence/update/${patient.patientId}' />">Edit</a>
                            </#if>
                        </td>
                    </tr>
                    </#list>
                </#if>
            </tbody>
        </table>
    </div>
</div>
<script type="text/javascript">
    createAutoClosingAlert(".adherence-message-alert", 5000)
</script>
</@layout.defaultLayout>