package org.openmrs.module.crossborder.reporting.library;

import java.util.Collections;
import java.util.Date;

import org.openmrs.EncounterType;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.reporting.library.moh731.Moh731CohortLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.common.CommonCohortLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.hiv.HivCohortLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.hiv.art.ArtCohortLibrary;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Library of TB related cohort definitions
 */
@Component
public class CrossborderCohortLibrary {
	
	@Autowired
	private CommonCohortLibrary commonCohorts;
	
	@Autowired
	private HivCohortLibrary hivCohortLibrary;
	
	@Autowired
	private ArtCohortLibrary artCohortLibrary;
	
	@Autowired
	private Moh731CohortLibrary moh731CohortLibrary;
	
	public CohortDefinition cbOtherNationalities() {
		String sqlQuery = "" + "SELECT " + "	DISTINCT patient_id " + "FROM "
		        + "	kenyaemr_etl.etl_crossborder_mobility_screening " + "WHERE " + "	country <> 'kenya' AND "
		        + "	visit_date BETWEEN :startDate AND :endDate";
		
		SqlCohortDefinition cd = new SqlCohortDefinition();
		cd.setName("patientsOfOtherNationalities");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("Patients of other nationalities");
		return cd;
	}
	
	public CohortDefinition cbResidentOtherCounties() {
		String sqlQuery = "" + " SELECT " + "	p.patient_id " + " FROM " + "	person_address pa "
		        + " INNER JOIN patient p ON pa.person_id = p.patient_id" + " WHERE " + "	p.voided =0 AND "
		        + "	pa.voided = 0 AND " + "	pa.state_province <> 'Busia'" + " ";
		
		SqlCohortDefinition cd = new SqlCohortDefinition();
		cd.setName("residentsOfOtherCounties");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("Residents of other counties");
		return cd;
	}
	
	public CohortDefinition cbTravelledOtherCountry() {
		String sqlQuery = ""
		        + "SELECT "
		        + "	DISTINCT patient_id "
		        + "FROM "
		        + "	kenyaemr_etl.etl_crossborder_mobility_screening "
		        + "WHERE "
		        + "	(`travelled_in_last_3_months` = 'Yes' OR `travelled_in_last_6_months` = 'Yes' OR `travelled_in_last_12_months` = 'Yes') AND "
		        + "	visit_date BETWEEN :startDate AND :endDate";
		
		SqlCohortDefinition cd = new SqlCohortDefinition();
		cd.setName("travelledOtherCountry");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("Travelled to other country");
		return cd;
		
	}
	
	public CohortDefinition crossborderPatients() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Cross border patient");
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("otherNationality",
		    ReportUtils.map(cbOtherNationalities(), "startDate=${onOrAfter},endDate=${onOrBefore}"));
		cd.addSearch("residentOtherCounty",
		    ReportUtils.map(cbResidentOtherCounties(), "startDate=${onOrAfter},endDate=${onOrBefore}"));
		cd.addSearch("travelledOtherCountry",
		    ReportUtils.map(cbTravelledOtherCountry(), "startDate=${onOrAfter},endDate=${onOrBefore}"));
		cd.setCompositionString("otherNationality OR residentOtherCounty OR travelledOtherCountry");
		return cd;
	}
	
	public CohortDefinition receivedHivTestResults() {
		String sqlQuery = "" + " SELECT " + "	DISTINCT patient_id " + " FROM " + "	kenyaemr_etl.etl_hts_test t" + " WHERE "
		        + "	t.test_type IN(1,2) AND" + "	t.`patient_given_result` = 'Yes' AND"
		        + "	t.visit_date BETWEEN :startDate AND :endDate ";
		
		SqlCohortDefinition cd = new SqlCohortDefinition();
		cd.setName("received HIV Test Results");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("Received HIV Test Resulsts");
		return cd;
	}
	
	public CohortDefinition cbReceivedHivTestResults() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Cross border patient Received HIV Results");
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("crossborder", ReportUtils.map(crossborderPatients(), "startDate=${onOrAfter},endDate=${onOrBefore}"));
		cd.addSearch("receivedHivTestResults",
		    ReportUtils.map(receivedHivTestResults(), "startDate=${onOrAfter},endDate=${onOrBefore}"));
		cd.setCompositionString("crossborder AND receivedHivTestResults");
		return cd;
	}
	
	public CohortDefinition initiatedOnTreatment() {
		String sqlQuery = "" + "SELECT " + "	c.patient_id " + "FROM " + "	kenyaemr_etl.`etl_current_in_care` c"
		        + "INNER JOIN " + "	patient p ON c.patient_id = p.patient_id" + "WHERE " + "	p.voided = 0 AND "
		        + "	c.`started_on_drugs` = 'Yes'";
		
		SqlCohortDefinition cd = new SqlCohortDefinition();
		cd.setName("Initiated on Treatment");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("Initiated On Treatment");
		return cd;
		
	}
	
	public CohortDefinition cbInitiatedOnTreatment() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Cross border patient initit");
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("crossborder", ReportUtils.map(crossborderPatients(), "startDate=${onOrAfter},endDate=${onOrBefore}"));
		cd.addSearch("initiatedTreatment",
		    ReportUtils.map(initiatedOnTreatment(), "startDate=${onOrAfter},endDate=${onOrBefore}"));
		cd.setCompositionString("crossborder AND initiatedTreatment");
		return cd;
		
	}
	
	public CohortDefinition testedHivPositive() {
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setEncounterTypeList(Collections.singletonList(MetadataUtils.existing(EncounterType.class,
		    CommonMetadata._EncounterType.CROSS_BORDER)));
		return cd;
	}
	
	public CohortDefinition returnVisits() {
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setEncounterTypeList(Collections.singletonList(MetadataUtils.existing(EncounterType.class,
		    CommonMetadata._EncounterType.CROSS_BORDER)));
		return cd;
	}
	
	public CohortDefinition receivingTreatment() {
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setEncounterTypeList(Collections.singletonList(MetadataUtils.existing(EncounterType.class,
		    CommonMetadata._EncounterType.CROSS_BORDER)));
		return cd;
	}
	
	public CohortDefinition virallySuppressed() {
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setEncounterTypeList(Collections.singletonList(MetadataUtils.existing(EncounterType.class,
		    CommonMetadata._EncounterType.CROSS_BORDER)));
		return cd;
	}
	
	public CohortDefinition retainedOnTreatmentAfterOneMonth() {
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setEncounterTypeList(Collections.singletonList(MetadataUtils.existing(EncounterType.class,
		    CommonMetadata._EncounterType.CROSS_BORDER)));
		return cd;
	}
	
	public CohortDefinition retainedOnTreatmentAfter6Months() {
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setEncounterTypeList(Collections.singletonList(MetadataUtils.existing(EncounterType.class,
		    CommonMetadata._EncounterType.CROSS_BORDER)));
		return cd;
	}
	
	public CohortDefinition retainedOnTreatmentAfter3Months() {
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setEncounterTypeList(Collections.singletonList(MetadataUtils.existing(EncounterType.class,
		    CommonMetadata._EncounterType.CROSS_BORDER)));
		return cd;
	}
	
	public CohortDefinition retainedOnTreatmentAfter12Months() {
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setEncounterTypeList(Collections.singletonList(MetadataUtils.existing(EncounterType.class,
		    CommonMetadata._EncounterType.CROSS_BORDER)));
		return cd;
	}
	
}
