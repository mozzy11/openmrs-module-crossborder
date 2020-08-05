/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.crossborder.metadata;

import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.encounterType;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.form;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.globalProperty;

import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.springframework.stereotype.Component;

/**
 * Common metadata bundle
 */
@Component
public class CommonMetadata extends AbstractMetadataBundle {
	
	public static final class _EncounterType {
		
		public static final String CROSS_BORDER = "489b11ef-7ebf-4a38-8141-de4c084bca26";
	}
	
	public static final class _Form {
		
		public static final String MOBILITY_SCREENING = "95ab6ce7-01ea-4a14-af12-fe4eada31081";
		
		public static final String INTERFACILITY_REFERRAL = "ec42630e-ac96-45c9-877e-f412793a582f";
	}
	
	public static final class _OrderType {
		
		public static final String DRUG = "131168f4-15f5-102d-96e4-000c29c2a5d7";
	}
	
	/**
	 * @see org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle#install()
	 */
	@Override
	public void install() {
		install(encounterType("Cross border", "Handles data for cross border related activities",
		    _EncounterType.CROSS_BORDER));
		
		install(form("Mobility Screening form",
		    "Form for mobility screening to determine who the cross border populations are.", _EncounterType.CROSS_BORDER,
		    "1", _Form.MOBILITY_SCREENING));
		install(form("Interfacility referral form", "Form for capturing inter-facility referral data",
		    _EncounterType.CROSS_BORDER, "1", _Form.INTERFACILITY_REFERRAL));
		
		install(globalProperty("registrationcore.local_mpi_identifierTypeMap.CCC Number", "MPI Mapping for CCC Number",
		    "b4d66522-11fc-45c7-83e3-39a1af21ae0d:2.25.276946543544871160225835991160192746993:PI"));
		
		install(globalProperty("registrationcore.local_mpi_identifierTypeMap.Kenya National Id",
		    "MPI Mapping for the Kenya National ID Number", "9aedb9ae-1cbd-11e8-accf-0ed5f89f718b:2.16.840.1.113883.4.56:NI"));
		
	}
}
