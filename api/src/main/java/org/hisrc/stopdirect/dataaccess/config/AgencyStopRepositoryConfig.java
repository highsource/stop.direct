package org.hisrc.stopdirect.dataaccess.config;

import org.hisrc.stopdirect.dataccess.AgencyStopRepository;
import org.hisrc.stopdirect.dataccess.impl.CsvAgencyStopRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AgencyStopRepositoryConfig {

	@Bean
	public AgencyStopRepository agencyStopRepository() {
		return new CsvAgencyStopRepository();
	}

}
