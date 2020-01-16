
package db.migration;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import fr.gouv.agriculture.ift.model.Culture;
import fr.gouv.agriculture.ift.model.GroupeCultures;

/**
 *  This class migrate the group_cultures and the cultures table according to the following rules:
 *  OLD(group_cultures.id_metier) ==> NEW(group_cultures.id_metier)
 *  GCU21 						  ==> GCU2
 *  GCU22 						  ==> GCU2
 *  GCU31 						  ==> GCU3
 *  GCU32 						  ==> GCU3
 *  GCU0                          ==> 
 *  The OLD ids are removed after the update.
 *  @author geraudbonou
 */
public class V2__Update_Regroupement_Culture implements SpringJdbcMigration {
	
	@Override
	public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
		
		Map<UUID, String> groupeCulturesIdMetierByUUID = getGroupeCulturesToBeDeteled(jdbcTemplate);
		List<Culture> culturesToBeUpdated = getCulturesToBeUpdated(jdbcTemplate);
		
		// Update culture with new id_metier of group_culture
		for(Culture culture: culturesToBeUpdated) {
			String groupeCultureIdMetier = groupeCulturesIdMetierByUUID.get(culture.getGroupeCultures().getId());
			if(shouldGroupeCulturesBeDeleted(groupeCultureIdMetier)) {
				String newGroupeCultureIdMetier = getNewGroupCultureIdMetier(groupeCultureIdMetier);
				jdbcTemplate.update(
		                "UPDATE \"ift\".culture SET groupe_cultures_id = ? WHERE id = ?", 
		                getGroupeCultureUUIDFromIdMetier(groupeCulturesIdMetierByUUID,newGroupeCultureIdMetier), 
		                culture.getId()
		                );
			}
		}
		// Delete obsolete group_culture
		for(Map.Entry<UUID, String> entry : groupeCulturesIdMetierByUUID.entrySet()) {
			if(shouldGroupeCulturesBeDeleted(entry.getValue())) {
				jdbcTemplate.update(
		                "DELETE FROM \"ift\".groupe_cultures WHERE id = ?", 
		                entry.getKey()
		                );	
			}
		}

		
	}

	/**
	 * @param groupeCultureIdMetier, the id of groupe_culture element
	 * @return the new id_metier which should replace current one after migration.
	 */
	private String getNewGroupCultureIdMetier(String groupeCultureIdMetier) {
		switch(groupeCultureIdMetier) {
		    case "GCU21":
		        return "GCU2";
		    case "GCU22":
		        return "GCU2";
		    case "GCU31":
		        return "GCU3";
		    case "GCU32":
		        return "GCU3";
		    default:
		    	return null;
		}
	}

	/**
	 * @param jdbcTemplate, the jdbcTemplate object.
	 * @return the list of culture object in database.
	 */
	private List<Culture> getCulturesToBeUpdated(JdbcTemplate jdbcTemplate) {
		RowMapper<Culture> cultureRowmaper = new RowMapper<Culture>() {
			@Override
			public Culture mapRow(ResultSet resultSet, int rowNum) throws SQLException {
				Culture culture = new Culture();
				culture.setId(resultSet.getObject("id",java.util.UUID.class));
				culture.setGroupeCultures(
						GroupeCultures.builder()
						.id(resultSet.getObject("groupe_cultures_id", UUID.class))
						.build()
				);
                return culture;
			}
		};
		return jdbcTemplate.query(
				"SELECT * FROM \"ift\".culture", 
				cultureRowmaper	
		);	
	}

	/**
	 * @param jdbcTemplate, the jdbcTemplate object.
	 * @return the list of groupe_cultures in database as a Map<primarykey, id_metier>
	 */
	private Map<UUID, String> getGroupeCulturesToBeDeteled(JdbcTemplate jdbcTemplate) {
		RowMapper<GroupeCultures> groupeCulturesRowMaper = new RowMapper<GroupeCultures>() {
			@Override
			public GroupeCultures mapRow(ResultSet resultSet, int rowNum) throws SQLException {
				GroupeCultures groupeCulture = new GroupeCultures();
                groupeCulture.setId(resultSet.getObject("id",java.util.UUID.class));
                groupeCulture.setIdMetier(resultSet.getString("id_metier"));
                return groupeCulture;
			}
		};
		
		return jdbcTemplate.query(
				"SELECT * FROM \"ift\".groupe_cultures", 
				groupeCulturesRowMaper	
		).stream()
		.collect(Collectors.toMap(GroupeCultures::getId, GroupeCultures::getIdMetier));	
	}
	
	/**
	 * @param idMetier, the id_metier of the group_cultures object.
	 * @return true if this groupe_culture should be deleted after migration.
	 */
	private boolean shouldGroupeCulturesBeDeleted(String idMetier) {
		return (Arrays.asList("GCU21","GCU22","GCU31","GCU32","GCU0")).contains(idMetier);
	}
	
	/**
	 * @param groupeCultureIdMetierById, a map of id_metier by groupe_id primary key.
	 * @param idMetier, the id_metier of group_culture row.
	 * @return the primary key (uuid) corresponding to this id_metier in the group_culture table.
	 */
	private UUID getGroupeCultureUUIDFromIdMetier(Map<UUID, String> groupeCultureIdMetierById, String idMetier) {
		for(Map.Entry<UUID, String> entry : groupeCultureIdMetierById.entrySet()){
		    if(entry.getValue()!= null && entry.getValue().equals(idMetier)) {
		    	return entry.getKey();
		    }
		}
		return null;
	}
	
	
	
	



	

}
