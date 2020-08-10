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

	public CohortDefinition withPresumptiveTb() {
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setEncounterTypeList(Collections.singletonList(MetadataUtils.existing(EncounterType.class,
		    CommonMetadata._EncounterType.CROSS_BORDER)));
		return cd;
	}

	public CohortDefinition investigatedForTbByMicroscopy() {
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setEncounterTypeList(Collections.singletonList(MetadataUtils.existing(EncounterType.class,
		    CommonMetadata._EncounterType.CROSS_BORDER)));
		return cd;
	}

	public CohortDefinition investigatedForTbByXray() {
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setEncounterTypeList(Collections.singletonList(MetadataUtils.existing(EncounterType.class,
		    CommonMetadata._EncounterType.CROSS_BORDER)));
		return cd;
	}

	public CohortDefinition investigatedForTbByGeneXpert() {
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setEncounterTypeList(Collections.singletonList(MetadataUtils.existing(EncounterType.class,
		    CommonMetadata._EncounterType.CROSS_BORDER)));
		return cd;
	}

	public CohortDefinition investigatedForTbByCulture() {
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setEncounterTypeList(Collections.singletonList(MetadataUtils.existing(EncounterType.class,
		    CommonMetadata._EncounterType.CROSS_BORDER)));
		return cd;
	}

	public CohortDefinition testingTbPostive() {
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setEncounterTypeList(Collections.singletonList(MetadataUtils.existing(EncounterType.class,
		    CommonMetadata._EncounterType.CROSS_BORDER)));
		return cd;
	}


	public CohortDefinition withDrugTbResistance() {
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setEncounterTypeList(Collections.singletonList(MetadataUtils.existing(EncounterType.class,
		    CommonMetadata._EncounterType.CROSS_BORDER)));
		return cd;
	}

	public CohortDefinition initiatedOnTbTreatment() {
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setEncounterTypeList(Collections.singletonList(MetadataUtils.existing(EncounterType.class,
		    CommonMetadata._EncounterType.CROSS_BORDER)));
		return cd;
	}

	public CohortDefinition clientsActiveOnTbTreatmentOneMonth() {
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setEncounterTypeList(Collections.singletonList(MetadataUtils.existing(EncounterType.class,
		    CommonMetadata._EncounterType.CROSS_BORDER)));
		return cd;
	}

	public CohortDefinition clientsActiveOnTbTreatmentTwoMonths() {
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setEncounterTypeList(Collections.singletonList(MetadataUtils.existing(EncounterType.class,
		    CommonMetadata._EncounterType.CROSS_BORDER)));
		return cd;
	}

	public CohortDefinition clientsActiveOnTbTreatmentThreeMonths() {
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setEncounterTypeList(Collections.singletonList(MetadataUtils.existing(EncounterType.class,
		    CommonMetadata._EncounterType.CROSS_BORDER)));
		return cd;
	}

	public CohortDefinition clientsActiveOnTbTreatmentFourMonths() {
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setEncounterTypeList(Collections.singletonList(MetadataUtils.existing(EncounterType.class,
		    CommonMetadata._EncounterType.CROSS_BORDER)));
		return cd;
	}

	public CohortDefinition clientsActiveOnTbTreatmentFiveMonths() {
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setEncounterTypeList(Collections.singletonList(MetadataUtils.existing(EncounterType.class,
		    CommonMetadata._EncounterType.CROSS_BORDER)));
		return cd;
	}

	public CohortDefinition clientsActiveOnTbTreatmentSixToTwelveMonths() {
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setEncounterTypeList(Collections.singletonList(MetadataUtils.existing(EncounterType.class,
		    CommonMetadata._EncounterType.CROSS_BORDER)));
		return cd;
	}

	public CohortDefinition treatmentOutComeCured() {
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setEncounterTypeList(Collections.singletonList(MetadataUtils.existing(EncounterType.class,
		    CommonMetadata._EncounterType.CROSS_BORDER)));
		return cd;
	}

	public CohortDefinition treatmentOutComeCompletedTreatment() {
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setEncounterTypeList(Collections.singletonList(MetadataUtils.existing(EncounterType.class,
		    CommonMetadata._EncounterType.CROSS_BORDER)));
		return cd;
	}

	public CohortDefinition treatmentOutComeTransferredOut() {
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setEncounterTypeList(Collections.singletonList(MetadataUtils.existing(EncounterType.class,
		    CommonMetadata._EncounterType.CROSS_BORDER)));
		return cd;
	}

	public CohortDefinition treatmentOutComeTreatmentFailure() {
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setEncounterTypeList(Collections.singletonList(MetadataUtils.existing(EncounterType.class,
		    CommonMetadata._EncounterType.CROSS_BORDER)));
		return cd;
	}

	public CohortDefinition treatmentOutComeDead() {
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setEncounterTypeList(Collections.singletonList(MetadataUtils.existing(EncounterType.class,
		    CommonMetadata._EncounterType.CROSS_BORDER)));
		return cd;
	}

	public CohortDefinition treatmentOutComeLtfu() {
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setEncounterTypeList(Collections.singletonList(MetadataUtils.existing(EncounterType.class,
		    CommonMetadata._EncounterType.CROSS_BORDER)));
		return cd;
	}

	public CohortDefinition counsellingVistForFp() {
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setEncounterTypeList(Collections.singletonList(MetadataUtils.existing(EncounterType.class,
		    CommonMetadata._EncounterType.CROSS_BORDER)));
		return cd;
	}

	public CohortDefinition recievingFp() {
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setEncounterTypeList(Collections.singletonList(MetadataUtils.existing(EncounterType.class,
		    CommonMetadata._EncounterType.CROSS_BORDER)));
		return cd;
	}

	public CohortDefinition trainedInMaternalAndChildCare() {
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setEncounterTypeList(Collections.singletonList(MetadataUtils.existing(EncounterType.class,
		    CommonMetadata._EncounterType.CROSS_BORDER)));
		return cd;
	}

	public CohortDefinition indexClients(){
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setEncounterTypeList(Collections.singletonList(MetadataUtils.existing(EncounterType.class,
		    CommonMetadata._EncounterType.CROSS_BORDER)));
		return cd;
	}


	public CohortDefinition agreedForContactElicitation(){
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setEncounterTypeList(Collections.singletonList(MetadataUtils.existing(EncounterType.class,
		    CommonMetadata._EncounterType.CROSS_BORDER)));
		return cd;
	}

	public CohortDefinition contactsElicited(){
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setEncounterTypeList(Collections.singletonList(MetadataUtils.existing(EncounterType.class,
		    CommonMetadata._EncounterType.CROSS_BORDER)));
		return cd;
	}

	public CohortDefinition contactsEligible(){
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setEncounterTypeList(Collections.singletonList(MetadataUtils.existing(EncounterType.class,
		    CommonMetadata._EncounterType.CROSS_BORDER)));
		return cd;
	}
	
	public CohortDefinition contactsElicitedTested(){
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setEncounterTypeList(Collections.singletonList(MetadataUtils.existing(EncounterType.class,
		    CommonMetadata._EncounterType.CROSS_BORDER)));
		return cd;
	}

	public CohortDefinition contactsTestingHiv(){
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setEncounterTypeList(Collections.singletonList(MetadataUtils.existing(EncounterType.class,
		    CommonMetadata._EncounterType.CROSS_BORDER)));
		return cd;
	}

	
	public CohortDefinition PnsYield(){
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setEncounterTypeList(Collections.singletonList(MetadataUtils.existing(EncounterType.class,
		    CommonMetadata._EncounterType.CROSS_BORDER)));
		return cd;
	}
	

	public CohortDefinition contactsInnitatedOnTreatment(){
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setEncounterTypeList(Collections.singletonList(MetadataUtils.existing(EncounterType.class,
		    CommonMetadata._EncounterType.CROSS_BORDER)));
		return cd;
	}

	public CohortDefinition clientsBookedForAppointment(){
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setEncounterTypeList(Collections.singletonList(MetadataUtils.existing(EncounterType.class,
		    CommonMetadata._EncounterType.CROSS_BORDER)));
		return cd;
	}

	public CohortDefinition clientsWhoKeptAppointments(){
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setEncounterTypeList(Collections.singletonList(MetadataUtils.existing(EncounterType.class,
		    CommonMetadata._EncounterType.CROSS_BORDER)));
		return cd;
	}

	public CohortDefinition clientsWhoMissedAppointments(){
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setEncounterTypeList(Collections.singletonList(MetadataUtils.existing(EncounterType.class,
		    CommonMetadata._EncounterType.CROSS_BORDER)));
		return cd;
	}

	public CohortDefinition clientsWhoMissedAppointmentsAndReturnedToCare(){
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setEncounterTypeList(Collections.singletonList(MetadataUtils.existing(EncounterType.class,
		    CommonMetadata._EncounterType.CROSS_BORDER)));
		return cd;
	}

	public CohortDefinition clientsWhoMissedAppointmentsAndSelfTransferred(){
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setEncounterTypeList(Collections.singletonList(MetadataUtils.existing(EncounterType.class,
		    CommonMetadata._EncounterType.CROSS_BORDER)));
		return cd;
	}

	public CohortDefinition clientsWhoStoppedTreatment(){
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setEncounterTypeList(Collections.singletonList(MetadataUtils.existing(EncounterType.class,
		    CommonMetadata._EncounterType.CROSS_BORDER)));
		return cd;
	}

	
	public CohortDefinition clientsWhoMissedAppointmentsDied(){
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setEncounterTypeList(Collections.singletonList(MetadataUtils.existing(EncounterType.class,
		    CommonMetadata._EncounterType.CROSS_BORDER)));
		return cd;
	}

	
	public CohortDefinition  LTFUselfTransferred(){
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setEncounterTypeList(Collections.singletonList(MetadataUtils.existing(EncounterType.class,
		    CommonMetadata._EncounterType.CROSS_BORDER)));
		return cd;
	}

	
	public CohortDefinition reportedAsStopedCurrentlyOnTreatment(){
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setEncounterTypeList(Collections.singletonList(MetadataUtils.existing(EncounterType.class,
		    CommonMetadata._EncounterType.CROSS_BORDER)));
		return cd;
	}

	public CohortDefinition LTFUstoppedTreatment(){
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setEncounterTypeList(Collections.singletonList(MetadataUtils.existing(EncounterType.class,
		    CommonMetadata._EncounterType.CROSS_BORDER)));
		return cd;
	}

	public CohortDefinition LTFUdied(){
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setEncounterTypeList(Collections.singletonList(MetadataUtils.existing(EncounterType.class,
		    CommonMetadata._EncounterType.CROSS_BORDER)));
		return cd;
	}


	public CohortDefinition LTFUtraced(){
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setEncounterTypeList(Collections.singletonList(MetadataUtils.existing(EncounterType.class,
		    CommonMetadata._EncounterType.CROSS_BORDER)));
		return cd;
	}

	
	public CohortDefinition LTFUrestartedArt(){
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setEncounterTypeList(Collections.singletonList(MetadataUtils.existing(EncounterType.class,
		    CommonMetadata._EncounterType.CROSS_BORDER)));
		return cd;
	}

	
	public CohortDefinition newOnTreatment(){
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setEncounterTypeList(Collections.singletonList(MetadataUtils.existing(EncounterType.class,
		    CommonMetadata._EncounterType.CROSS_BORDER)));
		return cd;
	}

	public CohortDefinition transferIn(){
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setEncounterTypeList(Collections.singletonList(MetadataUtils.existing(EncounterType.class,
		    CommonMetadata._EncounterType.CROSS_BORDER)));
		return cd;
	}

	
	public CohortDefinition transferOut(){
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setEncounterTypeList(Collections.singletonList(MetadataUtils.existing(EncounterType.class,
		    CommonMetadata._EncounterType.CROSS_BORDER)));
		return cd;
	}

	public CohortDefinition recievedThreeMonthsSubscription(){
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setEncounterTypeList(Collections.singletonList(MetadataUtils.existing(EncounterType.class,
		    CommonMetadata._EncounterType.CROSS_BORDER)));
		return cd;
	}

	public CohortDefinition clientsTransitionedToTLD(){
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setEncounterTypeList(Collections.singletonList(MetadataUtils.existing(EncounterType.class,
		    CommonMetadata._EncounterType.CROSS_BORDER)));
		return cd;
	}

	public CohortDefinition clientsDueForViralLoad(){
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setEncounterTypeList(Collections.singletonList(MetadataUtils.existing(EncounterType.class,
		    CommonMetadata._EncounterType.CROSS_BORDER)));
		return cd;
	}

	public CohortDefinition VLsamplesCollectedFromClientsAttendingClinic(){
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setEncounterTypeList(Collections.singletonList(MetadataUtils.existing(EncounterType.class,
		    CommonMetadata._EncounterType.CROSS_BORDER)));
		return cd;
	}
	
	public CohortDefinition VLresultsRecieved(){
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setEncounterTypeList(Collections.singletonList(MetadataUtils.existing(EncounterType.class,
		    CommonMetadata._EncounterType.CROSS_BORDER)));
		return cd;
	}

	public CohortDefinition VLresultsSuppressed(){
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.setEncounterTypeList(Collections.singletonList(MetadataUtils.existing(EncounterType.class,
		    CommonMetadata._EncounterType.CROSS_BORDER)));
		return cd;
	}
}
