/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.crossborder.reporting.builder;

import static org.openmrs.module.kenyacore.report.ReportUtils.map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.module.crossborder.reporting.library.CrossborderIndicatorLibrary;
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.AbstractReportBuilder;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.reporting.ColumnParameters;
import org.openmrs.module.kenyaemr.reporting.EmrReportingUtils;
import org.openmrs.module.kenyaemr.reporting.library.moh731.Moh731IndicatorLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.common.CommonDimensionLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.hiv.HivIndicatorLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.hiv.art.ArtIndicatorLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.tb.TbIndicatorLibrary;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * MOH 711 report
 */
@Component
@Builds({ "kenyaemr.etl.common.report.crossborder" })
public class CrossborderIndicatorReportBuilder extends AbstractReportBuilder {
	
	protected static final Log log = LogFactory.getLog(CrossborderIndicatorReportBuilder.class);
	
	@Autowired
	private CommonDimensionLibrary commonDimensions;
	
	@Autowired
	private HivIndicatorLibrary hivIndicators;
	
	@Autowired
	private ArtIndicatorLibrary artIndicators;
	
	@Autowired
	private TbIndicatorLibrary tbIndicators;
	
	@Autowired
	private Moh731IndicatorLibrary moh731Indicators;
	
	@Autowired
	private CrossborderIndicatorLibrary crossborderIndicators;
	
	/**
	 * @see org.openmrs.module.kenyacore.report.builder.AbstractReportBuilder#getParameters(org.openmrs.module.kenyacore.report.ReportDescriptor)
	 */
	@Override
	protected List<Parameter> getParameters(ReportDescriptor descriptor) {
		return Arrays.asList(new Parameter("startDate", "Start Date", Date.class), new Parameter("endDate", "End Date",
		        Date.class), new Parameter("dateBasedReporting", "", String.class));
	}
	
	/**
	 * @see org.openmrs.module.kenyacore.report.builder.AbstractReportBuilder#buildDataSets(org.openmrs.module.kenyacore.report.ReportDescriptor,
	 *      org.openmrs.module.reporting.report.definition.ReportDefinition)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected List<Mapped<DataSetDefinition>> buildDataSets(ReportDescriptor descriptor, ReportDefinition report) {
		return Arrays.asList(ReportUtils.map(createCrossborderDataset(), "startDate=${startDate},endDate=${endDate}"));
	}
	
	/**
	 * Created the Cross border data set
	 * 
	 * @return DataSetDefinition
	 */
	private DataSetDefinition createCrossborderDataset() {
		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		dsd.setName("CB");
		dsd.setDescription("Crossborder");
		dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
		dsd.addDimension("age", map(commonDimensions.standardAgeGroups(), "onDate=${endDate}"));
		dsd.addDimension("gender", map(commonDimensions.gender()));
		
		List<ColumnParameters> columns = new ArrayList<ColumnParameters>();
		columns.add(new ColumnParameters("FP", "0-14 years, female", "gender=F|age=<15"));
		columns.add(new ColumnParameters("MP", "0-14 years, male", "gender=M|age=<15"));
		columns.add(new ColumnParameters("FA", ">14 years, female", "gender=F|age=15+"));
		columns.add(new ColumnParameters("MA", ">14 years, male", "gender=M|age=15+"));
		columns.add(new ColumnParameters("T", "total", ""));
		
		String indParams = "startDate=${startDate},endDate=${endDate}";
		
		EmrReportingUtils.addRow(dsd, "CB1", "Number of other nationalities accessing FP services",
			ReportUtils.map(crossborderIndicators.cbOtherNationalitiesAccesingFPServices(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "CB2", "Number of other nationalities accessing MCH services",
			ReportUtils.map(crossborderIndicators.cbOtherNationalitiesAccesingMCHServices(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "CB3", "Number of other nationalities accessing HIV services",
			ReportUtils.map(crossborderIndicators.cbOtherNationalitiesAccesingHIVServices(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "CB4", "Number of other nationalities accessing TB services",
		    ReportUtils.map(crossborderIndicators.cbOtherNationalitiesAccesingTBServices(), indParams), columns);	
		EmrReportingUtils.addRow(dsd, "CB5","Number of residents of other districts/counties accessing services at the border facility",
		    ReportUtils.map(crossborderIndicators.cbResidentOtherCounties(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "CB6-3", "Number reporting travelled to another country last 3 months",
			ReportUtils.map(crossborderIndicators.cbTravelledOtherCountryLastThreeMonths(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "CB6-6", "Number reporting travelled to another country last 6 months",
			ReportUtils.map(crossborderIndicators.cbTravelledOtherCountryLastSixMonths(), indParams), columns);	
		EmrReportingUtils.addRow(dsd, "CB6-12", "Number reporting travelled to another country last 12 months",
		    ReportUtils.map(crossborderIndicators.cbTravelledOtherCountryLastTwelveMonths(), indParams), columns);	
		EmrReportingUtils.addRow( dsd, "CB7", "TXT-HTS: # of individuals who received HIV testing services (HTS) and received their test results, disaggregated by HIV result",
		    ReportUtils.map(crossborderIndicators.cbReceivedHivTestResults(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "CB8", "TX-POS: # of individuals who tested HIV positive",
		    ReportUtils.map(crossborderIndicators.cbTestedHivPositive(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "CB9", "TX-New: # of individuals newly initiated on Treatment",
		    ReportUtils.map(crossborderIndicators.cbInitiatedOnTreatment(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "CB10", "TX-SV:  who returned for second visit",
		    ReportUtils.map(crossborderIndicators.cbReturnVisits(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "CB11", "TX-CURR: of adults and children currently receiving ART",
		    ReportUtils.map(crossborderIndicators.cbReceivingTreatment(), indParams), columns);
		EmrReportingUtils.addRow(dsd,"CB12","TX-PLVS: % of ART patients with a viral load result documented in the medical record and/or laboratory information systems (LIS) within the past 12 months with a suppressed viral load (<400 copies/ml)",
		    ReportUtils.map(crossborderIndicators.cbVirallySuppressed(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "CB13-1","% of adults and children known to be on treatment 1 months after initiation of ART",
		    ReportUtils.map(crossborderIndicators.cbRetainedOnTreatmentAfterOneMonth(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "CB13-3","% of adults and children known to be on treatment 3 months after initiation of ART",
		    ReportUtils.map(crossborderIndicators.cbRetainedOnTreatmentAfter3Months(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "CB13-6","% of adults and children known to be on treatment 6 months after initiation of ART",
		    ReportUtils.map(crossborderIndicators.cbRetainedOnTreatmentAfter6Months(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "CB13-12","% of adults and children known to be on treatment 12 months after initiation of ART",
			ReportUtils.map(crossborderIndicators.cbRetainedOnTreatmentAfter12Months(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "CB14","% of individuals with presumptive TB",
			ReportUtils.map(crossborderIndicators.cbWithPresumptiveTb(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "CB15-1","Number investigated for TB (Disaggregataed by method - Microscopy)",
			ReportUtils.map(crossborderIndicators.cbInvestigatedForTbByMicroscopy(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "CB15-2","Number investigated for TB (Disaggregataed by method - X-ray)",
			ReportUtils.map(crossborderIndicators.cbInvestigatedForTbByXray(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "CB15-3","Number investigated for TB (Disaggregataed by method - Gene-Xpert)",
			ReportUtils.map(crossborderIndicators.cbInvestigatedForTbByGeneXpert(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "CB15-3","Number investigated for TB (Disaggregataed by method - Culture)",
			ReportUtils.map(crossborderIndicators.cbInvestigatedForTbByCulture(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "CB16","% of individuals Testing TB positive",
			ReportUtils.map(crossborderIndicators.cbTestingTbPostive(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "CB17","%  of individuals with TB drug resistance",
			ReportUtils.map(crossborderIndicators.cbWithDrugTbResistance(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "CB18","% of individuals Initiated on TB treatment",
			ReportUtils.map(crossborderIndicators.cbInitiatedOnTbTreatment(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "CB19-1","% of Clients active on TB treatment 1 month",
			ReportUtils.map(crossborderIndicators.cbClientsActiveOnTbTreatmentOneMonth(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "CB19-2","% of Clients active on TB treatment 2 months",
			ReportUtils.map(crossborderIndicators.cbClientsActiveOnTbTreatmentTwoMonths(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "CB19-3","% of Clients active on TB treatment 3 months",
			ReportUtils.map(crossborderIndicators.cbClientsActiveOnTbTreatmentThreeMonths(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "CB19-4","% of Clients active on TB treatment 4 months",
			ReportUtils.map(crossborderIndicators.cbClientsActiveOnTbTreatmentFourMonths(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "CB19-5","% of Clients active on TB treatment 5 months",
			ReportUtils.map(crossborderIndicators.cbClientsActiveOnTbTreatmentFiveMonths(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "CB19-6","% of Clients active on TB treatment 6-12 months",
			ReportUtils.map(crossborderIndicators.cbClientsActiveOnTbTreatmentSixToTwelveMonths(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "CB20-1","% of individuals with TreatMent OutCome Cured",
			ReportUtils.map(crossborderIndicators.cbTreatmentOutComeCured(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "CB20-2","% of individuals with TreatMent OutCome  Completed Treatment",
			ReportUtils.map(crossborderIndicators.cbTreatmentOutComeCompletedTreatment(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "CB20-3","% of individuals with TreatMent OutCome  Transferred Out",
			ReportUtils.map(crossborderIndicators.cbTreatmentOutComeTransferredOut(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "CB20-4","% of individuals with TreatMent OutCome  Treatment Failure",
			ReportUtils.map(crossborderIndicators.cbTreatmentOutComeTreatmentFailure(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "CB20-5","% of individuals with TreatMent OutCome  Dead",
			ReportUtils.map(crossborderIndicators.cbTreatmentOutComeDead(), indParams), columns);	
		EmrReportingUtils.addRow(dsd, "CB20-6","% of individuals with TreatMent OutCome  LTFU",
			ReportUtils.map(crossborderIndicators.cbTreatmentOutComeLtfu(), indParams), columns);	
		EmrReportingUtils.addRow(dsd, "CB21","% of individuals for counselling visits for FP/RH as a result of USG assistance",
			ReportUtils.map(crossborderIndicators.cbCounsellingVistForFp(), indParams), columns);	
		EmrReportingUtils.addRow(dsd, "CB22","% of individuals receiving FP counselling services and were given any method",
			ReportUtils.map(crossborderIndicators.cbRecievingFp(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "CB23","% of individuals trained in maternal and child health care",
			ReportUtils.map(crossborderIndicators.cbTrainedInMaternalAndChildCare(), indParams), columns);	
		EmrReportingUtils.addRow(dsd, "CB24","% of index clients (positives)",
			ReportUtils.map(crossborderIndicators.cbIndexClients(), indParams), columns);	
		EmrReportingUtils.addRow(dsd, "CB24","% of index clients (positives)",
			ReportUtils.map(crossborderIndicators.cbIndexClients(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "CB25","% of individuals agreed for contact elicitation",
			ReportUtils.map(crossborderIndicators.cbAgreedForContactElicitation(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "CB26","%  of contacts elicited",
			ReportUtils.map(crossborderIndicators.cbContactsElicited(), indParams), columns);	
		EmrReportingUtils.addRow(dsd, "CB27","%  of contacts eligible",
			ReportUtils.map(crossborderIndicators.cbContactsEligible(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "CB28","%  of contacts elicited Tested",
			ReportUtils.map(crossborderIndicators.cbContactsElicitedTested(), indParams), columns);	
		EmrReportingUtils.addRow(dsd, "CB29","%  of contacts testing HIV (PNS +)",
			ReportUtils.map(crossborderIndicators.cbContactsTestingHiv(), indParams), columns);	
		EmrReportingUtils.addRow(dsd, "CB30","PNS yield",
			ReportUtils.map(crossborderIndicators.cbPnsYield(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "CB31","% of contacts initiated on treatment",
			ReportUtils.map(crossborderIndicators.cbContactsInnitatedOnTreatment(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "CB32","% of clients booked for appointments",
			ReportUtils.map(crossborderIndicators.cbClientsBookedForAppointment(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "CB33","% of clients who kept appointments",
			ReportUtils.map(crossborderIndicators.cbClientsWhoKeptAppointments(), indParams), columns);	
		EmrReportingUtils.addRow(dsd, "CB34","% of clients who missed appointments traced",
			ReportUtils.map(crossborderIndicators.cbClientsWhoMissedAppointments(), indParams), columns);	
		EmrReportingUtils.addRow(dsd, "CB35","% of clients who missed appointments And returned to Care",
			ReportUtils.map(crossborderIndicators.cbClientsWhoMissedAppointmentsAndReturnedToCare(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "CB36","%  of clients who missed appointments who self-transferred",
			ReportUtils.map(crossborderIndicators.cbClientsWhoMissedAppointmentsAndSelfTransferred(), indParams), columns);	
		EmrReportingUtils.addRow(dsd, "CB37","% of clients who stopped treatment",
			ReportUtils.map(crossborderIndicators.cbClientsWhoStoppedTreatment(), indParams), columns);	
		EmrReportingUtils.addRow(dsd, "CB38","% of clients who missed appointments who died",
			ReportUtils.map(crossborderIndicators.cbClientsWhoMissedAppointmentsDied(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "CB39","%  of LTFU who self-transferred",
			ReportUtils.map(crossborderIndicators.cbLTFUselfTransferred(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "CB40","%  of clients reported as stopped who currently are on treatment in the facility (data error)",
		    ReportUtils.map(crossborderIndicators.cbReportedAsStopedCurrentlyOnTreatment(), indParams), columns);					
		EmrReportingUtils.addRow(dsd, "CB41","% of LTFU who Stopped Treatment",
			ReportUtils.map(crossborderIndicators.cbLTFUstoppedTreatment(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "CB42","% of LTFU who died",
			ReportUtils.map(crossborderIndicators.cbLTFUdied(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "CB43","% of LTFU traced",
			ReportUtils.map(crossborderIndicators.cbLTFUtraced(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "CB44","% of LTFU restarted ART",
			ReportUtils.map(crossborderIndicators.cbLTFUrestartedArt(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "CB45","Number new on treatment",
			ReportUtils.map(crossborderIndicators.cbNewOnTreatment(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "CB46","Transfer In",
			ReportUtils.map(crossborderIndicators.cbTransferIn(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "CB47","Transfer Out",
			ReportUtils.map(crossborderIndicators.cbTransferOut(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "CB48","No. of patients who received 3 months prescription (for Stable clients attending clinic today)",
			ReportUtils.map(crossborderIndicators.cbRecievedThreeMonthsSubscription(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "CB49","No. of clients transitioned to TLD",
			ReportUtils.map(crossborderIndicators.cbClientsTransitionedToTLD(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "CB50","No. of clients due for viral load (from clients attending clinic today)",
			ReportUtils.map(crossborderIndicators.cbClientsDueForViralLoad(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "CB51","No. of VL samples collected from clients attending clinic",
			ReportUtils.map(crossborderIndicators.VLsamplesCollectedFromClientsAttendingClinic(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "CB52","No. of VL results received",
			ReportUtils.map(crossborderIndicators.VLresultsRecieved(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "CB53","No. of VL suppressed",
			ReportUtils.map(crossborderIndicators.VLresultsSuppressed(), indParams), columns);
			return dsd;
	}
	
	/**
	 * Creates the ART data set
	 * 
	 * @return the data set
	 */
	private DataSetDefinition createTbDataSet() {
		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		dsd.setName("G");
		dsd.setDescription("TB");
		dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
		dsd.addDimension("age", map(commonDimensions.standardAgeGroups(), "onDate=${endDate}"));
		dsd.addDimension("gender", map(commonDimensions.gender()));
		
		List<ColumnParameters> columns = new ArrayList<ColumnParameters>();
		columns.add(new ColumnParameters("FP", "0-14 years, female", "gender=F|age=<15"));
		columns.add(new ColumnParameters("MP", "0-14 years, male", "gender=M|age=<15"));
		columns.add(new ColumnParameters("FA", ">14 years, female", "gender=F|age=15+"));
		columns.add(new ColumnParameters("MA", ">14 years, male", "gender=M|age=15+"));
		columns.add(new ColumnParameters("T", "total", ""));
		
		String indParams = "startDate=${startDate},endDate=${endDate}";
		
		EmrReportingUtils.addRow(dsd, "G1", "No. of detected cases (who have new Tb detected cases)",
		    ReportUtils.map(tbIndicators.tbNewDetectedCases(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "G2", "No. of Pulmonary smear positive (who have pulmonary TB and smear positive)",
		    ReportUtils.map(tbIndicators.pulmonaryTbSmearPositive(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "G3", "No. of Pulmonary smear negative (who have pulmonary TB and smear negative)",
		    ReportUtils.map(tbIndicators.pulmonaryTbSmearNegative(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "G4", "No. of Extra pulmonary TB (who have extra pulmonary TB)",
		    ReportUtils.map(tbIndicators.extraPulmonaryTbPatients(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "G5", "No. of TB Re-treatments (who are in Tb re-treatments)",
		    ReportUtils.map(tbIndicators.tbRetreatmentsPatients(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "G6", "No. of TB and Tested for HIV (who are in Tb program and tested for HIV)",
		    ReportUtils.map(tbIndicators.inTbAndTestedForHiv(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "G7", "No. of TB and Tested for HIV (whose HIV result is positive)",
		    ReportUtils.map(tbIndicators.inTbAndTestedForHivPositive(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "G8", "No. of TB and HIV (who are both in TB and HIV and are on CPT)",
		    ReportUtils.map(tbIndicators.inTbAndHivProgramsAndOnCtxProphylaxis(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "G9", "No. of TB defaulters (who defaulted or missed appointments)",
		    ReportUtils.map(tbIndicators.defaulted(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "G10", "No. of TB completes (who Completed Tb Treatment)",
		    ReportUtils.map(tbIndicators.completedTbTreatment(), indParams), columns);
		EmrReportingUtils.addRow(dsd, "G11", "No. of TB deaths (who started tx this month last year)",
			ReportUtils.map(tbIndicators.diedAndStarted12MonthsAgo(), indParams), columns);
		
		return dsd;
	}
	
	/**
	 * Creates the ART data set
	 * 
	 * @return the data set
	 */
	private DataSetDefinition createArtDataSet() {
		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		dsd.setName("K");
		dsd.setDescription("ART");
		dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
		dsd.addDimension("age", ReportUtils.map(commonDimensions.standardAgeGroups(), "onDate=${endDate}"));
		dsd.addDimension("gender", ReportUtils.map(commonDimensions.gender()));
		
		ColumnParameters colFPeds = new ColumnParameters("FP", "0-14 years, female", "gender=F|age=<15");
		ColumnParameters colMPeds = new ColumnParameters("MP", "0-14 years, male", "gender=M|age=<15");
		ColumnParameters colFAdults = new ColumnParameters("FA", ">14 years, female", "gender=F|age=15+");
		ColumnParameters colMAdults = new ColumnParameters("MA", ">14 years, male", "gender=M|age=15+");
		ColumnParameters colFTotal = new ColumnParameters("F", "totals, female", "gender=F");
		ColumnParameters colMTotal = new ColumnParameters("M", "totals, male", "gender=M");
		ColumnParameters colTotal = new ColumnParameters("T", "grand total", "");
		
		Concept pmtct = Dictionary.getConcept(Dictionary.PMTCT_PROGRAM);
		Concept vct = Dictionary.getConcept(Dictionary.VCT_PROGRAM);
		Concept tb = Dictionary.getConcept(Dictionary.TUBERCULOSIS_TREATMENT_PROGRAM);
		Concept[] inpatient = { Dictionary.getConcept(Dictionary.PEDIATRIC_INPATIENT_SERVICE),
		        Dictionary.getConcept(Dictionary.ADULT_INPATIENT_SERVICE) };
		Concept cwc = Dictionary.getConcept(Dictionary.UNDER_FIVE_CLINIC);
		Concept[] all = { pmtct, vct, tb, inpatient[0], inpatient[1], cwc };
		
		List<ColumnParameters> allColumns = Arrays.asList(colFPeds, colMPeds, colFAdults, colMAdults, colFTotal, colMTotal,
		    colTotal);
		List<ColumnParameters> femaleColumns = Arrays.asList(colFPeds, colFAdults, colFTotal);
		List<ColumnParameters> pedsColumns = Arrays.asList(colFPeds, colMPeds, colFTotal, colMTotal, colTotal);
		
		String indParams = "startDate=${startDate},endDate=${endDate}";
		
		EmrReportingUtils.addRow(dsd, "K1-1", "New enrollments - PMTCT",
		    ReportUtils.map(hivIndicators.enrolledExcludingTransfersAndReferredFrom(pmtct), indParams), femaleColumns);
		EmrReportingUtils.addRow(dsd, "K1-2", "New enrollments - VCT",
		    ReportUtils.map(hivIndicators.enrolledExcludingTransfersAndReferredFrom(vct), indParams), allColumns);
		EmrReportingUtils.addRow(dsd, "K1-3", "New enrollments - TB",
		    ReportUtils.map(hivIndicators.enrolledExcludingTransfersAndReferredFrom(tb), indParams), allColumns);
		EmrReportingUtils.addRow(dsd, "K1-4", "New enrollments - In Patient",
		    ReportUtils.map(hivIndicators.enrolledExcludingTransfersAndReferredFrom(inpatient), indParams), allColumns);
		EmrReportingUtils.addRow(dsd, "K1-5", "New enrollments - CWC",
		    ReportUtils.map(hivIndicators.enrolledExcludingTransfersAndReferredFrom(cwc), indParams), pedsColumns);
		EmrReportingUtils.addRow(dsd, "K1-6", "New enrollments - All others",
		    ReportUtils.map(hivIndicators.enrolledExcludingTransfersAndNotReferredFrom(all), indParams), allColumns);
		EmrReportingUtils.addRow(dsd, "K1-7", "New enrollments - Sub-total",
		    ReportUtils.map(hivIndicators.enrolledExcludingTransfers(), indParams), allColumns);
		EmrReportingUtils.addRow(dsd, "K2", "Cumulative enrolled",
		    ReportUtils.map(hivIndicators.enrolledCumulative(), indParams), allColumns);
		EmrReportingUtils.addRow(dsd, "K3-1", "Starting ARVs - WHO stage 1",
		    ReportUtils.map(artIndicators.startedArtWithWhoStage(1), indParams), allColumns);
		EmrReportingUtils.addRow(dsd, "K3-2", "Starting ARVs - WHO stage 2",
		    ReportUtils.map(artIndicators.startedArtWithWhoStage(2), indParams), allColumns);
		EmrReportingUtils.addRow(dsd, "K3-3", "Starting ARVs - WHO stage 3",
		    ReportUtils.map(artIndicators.startedArtWithWhoStage(3), indParams), allColumns);
		EmrReportingUtils.addRow(dsd, "K3-4", "Starting ARVs - WHO stage 4",
		    ReportUtils.map(artIndicators.startedArtWithWhoStage(4), indParams), allColumns);
		EmrReportingUtils.addRow(dsd, "K3-5", "Starting ARVs - Sub-total",
		    ReportUtils.map(artIndicators.startedArt(), indParams), allColumns);
		EmrReportingUtils.addRow(dsd, "K4", "Cumulative started ARV",
		    ReportUtils.map(artIndicators.startedArtCumulative(), indParams), allColumns);
		EmrReportingUtils.addRow(dsd, "K5-1", "Currently on ARVs - Pregnant women",
		    ReportUtils.map(artIndicators.onArtAndPregnant(), indParams), femaleColumns);
		EmrReportingUtils.addRow(dsd, "K5-2", "Currently on ARVs - All others",
		    ReportUtils.map(artIndicators.onArtAndNotPregnant(), indParams), allColumns);
		EmrReportingUtils.addRow(dsd, "K5-3", "Currently on ARVs - Sub-total",
		    ReportUtils.map(moh731Indicators.currentlyOnArt(), indParams), allColumns);
		EmrReportingUtils.addRow(dsd, "K6", "Eligible for ART", ReportUtils.map(artIndicators.eligibleForArt(), indParams),
		    allColumns);
		//EmrReportingUtils.addRow(dsd, "K7-1", "Post-exposure prophylaxis..", map(???, indParams), allColumns);
		//EmrReportingUtils.addRow(dsd, "K7-2", "Post-exposure prophylaxis..", map(???, indParams), allColumns);
		//EmrReportingUtils.addRow(dsd, "K7-3", "Post-exposure prophylaxis..", map(???, indParams), allColumns);
		//EmrReportingUtils.addRow(dsd, "K7-4", "Post-exposure prophylaxis..", map(???, indParams), allColumns);
		EmrReportingUtils.addRow(dsd, "K8-1", "On prophylaxis - Cotrimoxazole",
		    ReportUtils.map(hivIndicators.onCotrimoxazoleProphylaxis(), indParams), allColumns);
		EmrReportingUtils.addRow(dsd, "K8-2", "On prophylaxis - Fluconazole",
		    ReportUtils.map(hivIndicators.onFluconazoleProphylaxis(), indParams), allColumns);
		EmrReportingUtils.addRow(dsd, "K8-3", "On prophylaxis - Sub-total",
		    ReportUtils.map(hivIndicators.onProphylaxis(), indParams), allColumns);
		
		return dsd;
	}
}
