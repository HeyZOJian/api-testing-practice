package exercises;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;


public class RestAssuredExercises2Test {

	private static RequestSpecification requestSpec;

	@BeforeAll
	public static void createRequestSpecification() {

		requestSpec = new RequestSpecBuilder().
				setBaseUri("http://localhost").
				setPort(9876).
				setBasePath("/api/f1").
				build();
	}

	/*******************************************************
	 * Use junit-jupiter-params for @ParameterizedTest that
	 * specifies in which country
	 * a specific circuit can be found (specify that Monza 
	 * is in Italy, for example) 
	 ******************************************************/

	static Stream<Arguments> localityDataProvider() {
		return Stream.of(
				Arguments.of("monza", "Italy"),
				Arguments.of("spa", "Belgium")
		);
	}

	@ParameterizedTest
	@MethodSource("localityDataProvider")
	public void checkCountryNameForLocality(String localityName, String countryName) {

		given().
				spec(requestSpec).
				pathParam("locality", localityName).
				when().
				get("/circuits/{locality}.json").
				then().
				log().
				all().body("MRData.CircuitTable.Circuits.Location.country", hasItems(countryName));
	}

	/*******************************************************
	 * Use junit-jupiter-params for @ParameterizedTest that specifies for all races
	 * (adding the first four suffices) in 2015 how many  
	 * pit stops Max Verstappen made
	 * (race 1 = 1 pitstop, 2 = 3, 3 = 2, 4 = 2)
	 ******************************************************/

	static Stream<Arguments> pitstopsRaceDataProvider() {
		return Stream.of(
				Arguments.of("1", "1"),
				Arguments.of("2", "3"),
				Arguments.of("3", "2"),
				Arguments.of("4", "2")
		);
	}

	@ParameterizedTest
	@MethodSource("pitstopsRaceDataProvider")
	public void checkpitStopsNumberForRace(String raceName, String pitStopNumber) {

		given().
				spec(requestSpec).
				pathParam("raceName", raceName).
				when().
				get("/2015/{raceName}/drivers/max_verstappen/pitstops.json").
				then().
				log().
				all().body("MRData.total", is(pitStopNumber));
	}

	/*******************************************************
	 * Request data for a specific circuit (for Monza this 
	 * is /circuits/monza.json)
	 * and check the country this circuit can be found in
	 ******************************************************/

	@Test
	public void checkCountryForCircuit() {

		given().
				spec(requestSpec).
				when().
				get("/circuits/monza.json").
				then().
				body("MRData.CircuitTable.Circuits.Location.country", not(isEmptyOrNullString()));
	}

	/*******************************************************
	 * Request the pitstop data for the first four races in
	 * 2015 for Max Verstappen (for race 1 this is
	 * /2015/1/drivers/max_verstappen/pitstops.json)
	 * and verify the number of pit stops made
	 ******************************************************/

	@Test
	public void checkNumberOfPitstopsForMaxVerstappenIn2015() {

		given().
				spec(requestSpec).
				when().
				then();
	}
}