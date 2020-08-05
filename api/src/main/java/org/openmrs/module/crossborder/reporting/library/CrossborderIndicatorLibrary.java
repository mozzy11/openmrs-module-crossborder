package org.openmrs.module.crossborder.reporting.library;

import static org.openmrs.module.kenyacore.report.ReportUtils.map;
import static org.openmrs.module.kenyaemr.reporting.EmrReportingUtils.cohortIndicator;

import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Library of TB related indicator definitions. All indicators require parameters ${startDate} and
 * ${endDate}
 */
@Component
public class CrossborderIndicatorLibrary {
	
	@Autowired
	private CrossborderCohortLibrary crossborderCohorts;
	
	public CohortIndicator cbOtherNationalities() {
		return cohortIndicator("Number of other nationalities accessing specified services",
		    map(crossborderCohorts.cbOtherNationalities(), "onOrAfter=${startDate},onOrBefore=${endDate}"));
	}
	
	public CohortIndicator cbResidentOtherCounties() {
		return cohortIndicator("Number of residents of other districts/counties accessing services at the border facility",
		    map(crossborderCohorts.cbResidentOtherCounties(), "onOrAfter=${startDate},onOrBefore=${endDate}"));
	}
	
	public CohortIndicator cbTravelledOtherCountry() {
		return cohortIndicator("Number reporting travelled to another country last 3/6/12 months",
		    map(crossborderCohorts.cbTravelledOtherCountry(), "onOrAfter=${startDate},onOrBefore=${endDate}"));
	}
	
	public CohortIndicator receivedHivTestResults() {
		return cohortIndicator(
		    "TXT-HTS: # of individuals who received HIV testing services (HTS) and received their test results, disaggregated by HIV result",
		    map(crossborderCohorts.cbReceivedHivTestResults(), "onOrAfter=${startDate},onOrBefore=${endDate}"));
	}
	
	public CohortIndicator initiatedOnTreatment() {
		return cohortIndicator("TX-New:# of individuals newly initiated on Treatment",
		    map(crossborderCohorts.initiatedOnTreatment(), "onOrAfter=${startDate},onOrBefore=${endDate}"));
	}
	
	public CohortIndicator testedHivPositive() {
		return cohortIndicator("TX-POS: # of individuals who tested HIV positive",
		    map(crossborderCohorts.testedHivPositive(), "onOrAfter=${startDate},onOrBefore=${endDate}"));
	}
	
	public CohortIndicator returnVisits() {
		return cohortIndicator("TX-SV:  who returned for second visit",
		    map(crossborderCohorts.returnVisits(), "onOrAfter=${startDate},onOrBefore=${endDate}"));
	}
	
	public CohortIndicator receivingTreatment() {
		return cohortIndicator("TX-CURR: of adults and children currently receiving ART",
		    map(crossborderCohorts.receivingTreatment(), "onOrAfter=${startDate},onOrBefore=${endDate}"));
	}
	
	public CohortIndicator virallySuppressed() {
		return cohortIndicator(
		    "TX-PLVS: % of ART patients with a viral load result documented in the medical record and/or laboratory information systems (LIS) within the past 12 months with a suppressed viral load (<400 copies/ml)",
		    map(crossborderCohorts.virallySuppressed(), "onOrAfter=${startDate},onOrBefore=${endDate}"));
	}
	
	public CohortIndicator retainedOnTreatmentAfterOneMonth() {
		return cohortIndicator("% of adults and children known to be on treatment 1 months after initiation of ART",
		    map(crossborderCohorts.retainedOnTreatmentAfterOneMonth(), "onOrAfter=${startDate},onOrBefore=${endDate}"));
	}
	
	public CohortIndicator retainedOnTreatmentAfter6Months() {
		return cohortIndicator("% of adults and children known to be on treatment 6 months after initiation of ART",
		    map(crossborderCohorts.retainedOnTreatmentAfter6Months(), "onOrAfter=${startDate},onOrBefore=${endDate}"));
	}
	
	public CohortIndicator retainedOnTreatmentAfter3Months() {
		return cohortIndicator("% of adults and children known to be on treatment 3 months after initiation of ART",
		    map(crossborderCohorts.retainedOnTreatmentAfter3Months(), "onOrAfter=${startDate},onOrBefore=${endDate}"));
	}
	
	public CohortIndicator retainedOnTreatmentAfter12Months() {
		return cohortIndicator("% of adults and children known to be on treatment 12 months after initiation of ART",
		    map(crossborderCohorts.retainedOnTreatmentAfter12Months(), "onOrAfter=${startDate},onOrBefore=${endDate}"));
	}
	
}
