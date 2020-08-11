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
import org.openmrs.module.kenyaemr.reporting.cohort.definition.HTSClientsCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.HTSPositiveResultsCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.ETLNewHivEnrollmentCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.ETLCurrentOnARTCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.library.ETLReports.RevisedDatim.DatimCohortLibrary;
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

    @Autowired
	DatimCohortLibrary datimCohortLibrary ;
	
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

	public CohortDefinition otherNationalitiesAccesingFpServices() {
		String sqlQuery = "SELECT DISTINCT patient_id " 
		                 + "FROM kenyaemr_etl.elt_crossborder_mobiilty_screening" 
		                 + "WHERE service = 'FP' "
		                 + "AND visit_date BETWEEN :startDate AND :endDate";
		
		SqlCohortDefinition cd = new SqlCohortDefinition();
		cd.setName("patientsOfOtherNationalitiesFP");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("Patients of other nationalities Accesing FP services");
		return cd;
	}
	

	public CohortDefinition cbOtherNationalitiesAccesingFpServices() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("CrossborderpatientAccesingFPservices");
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("crossborder", ReportUtils.map(crossborderPatients(), "startDate=${onOrAfter},endDate=${onOrBefore}"));
		cd.addSearch("AccesFPservices",
		    ReportUtils.map(otherNationalitiesAccesingFpServices(), "startDate=${onOrAfter},endDate=${onOrBefore}"));
		cd.setCompositionString("crossborder AND AccesFPservices");
		return cd;
	}
	


	
	public CohortDefinition otherNationalitiesAccesingMCHServices() {
		String sqlQuery = "SELECT DISTINCT patient_id " 
		                 + "FROM kenyaemr_etl.elt_crossborder_mobiilty_screening" 
		                 + "WHERE service = 'MCH' "
		                 + "AND visit_date BETWEEN :startDate AND :endDate";
		
		SqlCohortDefinition cd = new SqlCohortDefinition();
		cd.setName("patientsOfOtherNationalitiesMCH");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("Patients of other nationalities");
		return cd;
	}

	public CohortDefinition cbOtherNationalitiesAccesingMCHServices() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("CrossBorderPatientAccesingMCHServices");
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("crossborder", ReportUtils.map(crossborderPatients(), "startDate=${onOrAfter},endDate=${onOrBefore}"));
		cd.addSearch("AccesMCHservices",
		    ReportUtils.map(otherNationalitiesAccesingMCHServices(), "startDate=${onOrAfter},endDate=${onOrBefore}"));
		cd.setCompositionString("crossborder AND AccesMCHservices");
		return cd;
	}

	public CohortDefinition otherNationalitiesAccesingHIVServices() {
		String sqlQuery = "SELECT DISTINCT patient_id " 
		                 + "FROM kenyaemr_etl.elt_crossborder_mobiilty_screening" 
		                 + "WHERE service = 'HIV' "
		                 + "AND visit_date BETWEEN :startDate AND :endDate";
		
		SqlCohortDefinition cd = new SqlCohortDefinition();
		cd.setName("patientsOfOtherNationalitiesHIV");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("Patients of other nationalities");
		return cd;
	}


	public CohortDefinition cbOtherNationalitiesAccesingHIVServices() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("CrossborderPatientsAccesingHIVServices");
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("crossborder", ReportUtils.map(crossborderPatients(), "startDate=${onOrAfter},endDate=${onOrBefore}"));
		cd.addSearch("AccesHIVservices",
		    ReportUtils.map(otherNationalitiesAccesingHIVServices(), "startDate=${onOrAfter},endDate=${onOrBefore}"));
		cd.setCompositionString("crossborder AND AccesHIVservices");
		return cd;
	}


	public CohortDefinition otherNationalitiesAccesingTBServices() {
		String sqlQuery = "SELECT DISTINCT patient_id " 
		                 + "FROM kenyaemr_etl.elt_crossborder_mobiilty_screening" 
		                 + "WHERE service = 'TB' "
		                 + "AND visit_date BETWEEN :startDate AND :endDate";
		
		SqlCohortDefinition cd = new SqlCohortDefinition();
		cd.setName("patientsOfOtherNationalitiesTB");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("Patients of other nationalities");
		return cd;
	}

	public CohortDefinition cbOtherNationalitiesAccesingTBServices() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("CrossborderpatientAccesingTBservices");
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("crossborder", ReportUtils.map(crossborderPatients(), "startDate=${onOrAfter},endDate=${onOrBefore}"));
		cd.addSearch("AccesTBservices",
		    ReportUtils.map(otherNationalitiesAccesingHIVServices(), "startDate=${onOrAfter},endDate=${onOrBefore}"));
		cd.setCompositionString("crossborder AND AccesTBservices");
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

	public CohortDefinition travelledOtherCountryLastThreeMonths() {
		String sqlQuery = ""
		        + "SELECT 	DISTINCT patient_id"
		        + "FROM	kenyaemr_etl.etl_crossborder_mobility_screening "
		        + "WHERE `travelled_in_last_3_months` = 'Yes'"
		        + "AND visit_date BETWEEN :startDate AND :endDate";
		
		SqlCohortDefinition cd = new SqlCohortDefinition();
		cd.setName("travelledOtherCountryLastThreeMonths");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("Travelled to other country Last Three Months");
		return cd;
		
	}

	public CohortDefinition cbTravelledOtherCountryLastThreeMonths() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("travelledOtherCountryLastThreeMonths");
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("crossborder", ReportUtils.map(crossborderPatients(), "startDate=${onOrAfter},endDate=${onOrBefore}"));
		cd.addSearch("TravelledLastThreeMonths",
		    ReportUtils.map(travelledOtherCountryLastThreeMonths(), "startDate=${onOrAfter},endDate=${onOrBefore}"));
		cd.setCompositionString("crossborder AND TravelledLastThreeMonths");
		return cd;
	}

	public CohortDefinition travelledOtherCountryLastSixMonths() {
		String sqlQuery = ""
		        + "SELECT 	DISTINCT patient_id"
		        + "FROM	kenyaemr_etl.etl_crossborder_mobility_screening "
		        + "WHERE `travelled_in_last_6_months` = 'Yes'"
		        + "AND visit_date BETWEEN :startDate AND :endDate";
		
		SqlCohortDefinition cd = new SqlCohortDefinition();
		cd.setName("travelledOtherCountryLstSixMonths");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("Travelled to other country Last Six Months");
		return cd;
		
	}

	public CohortDefinition cbTravelledOtherCountryLastSixMonths() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("travelledOtherCountryLastSixMonths");
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("crossborder", ReportUtils.map(crossborderPatients(), "startDate=${onOrAfter},endDate=${onOrBefore}"));
		cd.addSearch("TravelledLastSixMonths",
		    ReportUtils.map(travelledOtherCountryLastSixMonths(), "startDate=${onOrAfter},endDate=${onOrBefore}"));
		cd.setCompositionString("crossborder AND TravelledLastSixMonths");
		return cd;
	}

	public CohortDefinition travelledOtherCountryLastTwelveMonths() {
		String sqlQuery = ""
		        + "SELECT 	DISTINCT patient_id"
		        + "FROM	kenyaemr_etl.etl_crossborder_mobility_screening "
		        + "WHERE `travelled_in_last_12_months` = 'Yes'"
		        + "AND visit_date BETWEEN :startDate AND :endDate";
		
		SqlCohortDefinition cd = new SqlCohortDefinition();
		cd.setName("travelledOtherCountryLastTwelveMonths");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("Travelled to other country Last 12 Months");
		return cd;
		
	}

	public CohortDefinition cbTravelledOtherCountryLastTwelveMonths() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("travelledOtherCountryLastTwelveMonths");
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("crossborder", ReportUtils.map(crossborderPatients(), "startDate=${onOrAfter},endDate=${onOrBefore}"));
		cd.addSearch("TravelledLastTwelveMonths",
		    ReportUtils.map(travelledOtherCountryLastTwelveMonths(), "startDate=${onOrAfter},endDate=${onOrBefore}"));
		cd.setCompositionString("crossborder AND TravelledLastTwelveMonths");
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
	
	public CohortDefinition cbReceivedHivTestResults() {
		HTSClientsCohortDefinition htsClientsdefiniton = new HTSClientsCohortDefinition();
		htsClientsdefiniton.setName("Recieved HV TestResults");
		
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Cross border patient Received HIV Results");
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("crossborder", ReportUtils.map(crossborderPatients(), "startDate=${onOrAfter},endDate=${onOrBefore}"));
		cd.addSearch("receivedHivTestResults",
		    ReportUtils.map(htsClientsdefiniton, ""));
		cd.setCompositionString("crossborder AND receivedHivTestResults");
		return cd;
	}
	
	public CohortDefinition cbInitiatedOnTreatment() {
        ETLNewHivEnrollmentCohortDefinition newHivEnrollmentCohortDefinition  = new ETLNewHivEnrollmentCohortDefinition(); 
		newHivEnrollmentCohortDefinition.setName("innitiated On Treatment");

		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Cross border patient initit");
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("crossborder", ReportUtils.map(crossborderPatients(), "startDate=${onOrAfter},endDate=${onOrBefore}"));
		cd.addSearch("initiatedTreatment",
		    ReportUtils.map(newHivEnrollmentCohortDefinition, ""));
		cd.setCompositionString("crossborder AND initiatedTreatment");
		return cd;
		
	}
	
	public CohortDefinition cbTestedHivPositive() {
		HTSPositiveResultsCohortDefinition htsPositiveResultsCorhortDefiniton = new HTSPositiveResultsCohortDefinition();
		htsPositiveResultsCorhortDefiniton.setName("HV Positive Results");
		
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Cross border patient Received HIV Results");
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("crossborder", ReportUtils.map(crossborderPatients(), "startDate=${onOrAfter},endDate=${onOrBefore}"));
		cd.addSearch("hivPostiveResults",
		    ReportUtils.map(htsPositiveResultsCorhortDefiniton, ""));
		cd.setCompositionString("crossborder AND hivPostiveResults");
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
	
	public CohortDefinition cbReceivingTreatment() {
		ETLCurrentOnARTCohortDefinition currentOnArtCohortDefinition  = new ETLCurrentOnARTCohortDefinition(); 
		currentOnArtCohortDefinition.setName("Cuurent On ART");

		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Cross border patient initit");
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("crossborder", ReportUtils.map(crossborderPatients(), "startDate=${onOrAfter},endDate=${onOrBefore}"));
		cd.addSearch("currentOnArt",
		    ReportUtils.map(currentOnArtCohortDefinition, ""));
		cd.setCompositionString("crossborder AND currentOnArt");
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
		String sqlQuery = "SELECT patient_id"
						  + "FROM kenyaemr_datatools.tb_screening "
						  +"WHERE resulting_tb_status = 'Presumed TB' "
						  +"AND visit_date BETWEEN :startDate and :endDate";
		
		SqlCohortDefinition cd = new SqlCohortDefinition();
		cd.setName("with Presumptive tb Treatment");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("with Presumptive tb Treatment");
		return cd;
	}

	public CohortDefinition cbWithPresumptiveTb() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("presumptive TB");
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("crossborder", ReportUtils.map(crossborderPatients(), "startDate=${onOrAfter},endDate=${onOrBefore}"));
		cd.addSearch("presumptiveTB",
		    ReportUtils.map(withPresumptiveTb(), "startDate=${onOrAfter},endDate=${onOrBefore}"));
		cd.setCompositionString("crossborder AND presumptiveTB");
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
		String sqlQuery = "SELECT patient_id "
						  +"FROM kenyaemr_datatools.tb_enrollment "
						  +"WHERE date_treatment_started BETWEEN :startDate and :endDate";
		
		SqlCohortDefinition cd = new SqlCohortDefinition();
		cd.setName("innitiated on tb Treatment");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("innitiated on tb Treatmentt");
		return cd;
	}

	public CohortDefinition cbInitiatedOnTbTreatment() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Innitiated on TB Treatment");
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("crossborder", ReportUtils.map(crossborderPatients(), "startDate=${onOrAfter},endDate=${onOrBefore}"));
		cd.addSearch("innitiatedTBTreatment",
		    ReportUtils.map(initiatedOnTbTreatment(), "startDate=${onOrAfter},endDate=${onOrBefore}"));
		cd.setCompositionString("crossborder AND innitiatedTBTreatment");
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

	public CohortDefinition cbIndexClients(){
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Index Clients");
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("crossborder", ReportUtils.map(crossborderPatients(), "startDate=${onOrAfter},endDate=${onOrBefore}"));
		cd.addSearch("indexClients",
		    ReportUtils.map(datimCohortLibrary.offeredIndexServices(), "startDate=${onOrAfter},endDate=${onOrBefore}"));
		cd.setCompositionString("crossborder AND indexClients");
		return cd;
	}


	public CohortDefinition cbAgreedForContactElicitation(){
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Agrred For Contact Eliciation");
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("crossborder", ReportUtils.map(crossborderPatients(), "startDate=${onOrAfter},endDate=${onOrBefore}"));
		cd.addSearch("contactEliciation",
		    ReportUtils.map(datimCohortLibrary.acceptedIndexServices(), "startDate=${onOrAfter},endDate=${onOrBefore}"));
		cd.setCompositionString("crossborder AND contactEliciation");
		return cd;
	}

	public CohortDefinition contactsElicited(){
		String sqlQuery = "SELECT c.id from kenyaemr_hiv_testing_patient_contact c "
		+"WHERE c.relationship_type in(971, 972, 1528, 162221, 163565, 970, 5617) "
		+"AND c.voided = 0 "
		+"AND date(c.date_created) BETWEEN date_sub( date(:endDate), INTERVAL  3 MONTH )and date(:endDate)";
		
		SqlCohortDefinition cd = new SqlCohortDefinition();
		cd.setName("Contacts Elicited");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("Contact Elicited");
		return cd;
	}

	public CohortDefinition cbContactsElicited(){
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Contact Elicited");
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("crossborder", ReportUtils.map(crossborderPatients(), "startDate=${onOrAfter},endDate=${onOrBefore}"));
		cd.addSearch("ContactElicited",
		    ReportUtils.map(contactsElicited(), "startDate=${onOrAfter},endDate=${onOrBefore}"));
		cd.setCompositionString("crossborder AND ContactElicited");
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

	public CohortDefinition cbContactsTestingHiv(){
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Contacts Testing Hiv");
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("crossborder", ReportUtils.map(crossborderPatients(), "startDate=${onOrAfter},endDate=${onOrBefore}"));
		cd.addSearch("contactTestingHIV",
		    ReportUtils.map(datimCohortLibrary.knownPositiveContact(), "startDate=${onOrAfter},endDate=${onOrBefore}"));
		cd.setCompositionString("crossborder AND contactTestingHIV");
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
