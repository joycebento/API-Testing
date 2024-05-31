package test;

import io.restassured.http.ContentType;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static io.restassured.RestAssured.*;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class TestSchema {
    
    String api = "https://gorest.co.in/public/v2/todos";

    @Test
    public void testSchema(){
        given()
        .when().get(api)
        .then().log().all()
                .assertThat().body(matchesJsonSchemaInClasspath("schema.json"))
                .statusCode(200).contentType(ContentType.JSON);
    }

    @Test
    public void validateStatusCompleted() {
        //Entrada na API
        get(api)

        //Validação de status dos usuários
        .then().log().all()
                .assertThat().body("status",not(hasItem("pending")));

        /* Os objetos retornados por esta API podem ter 2 valores no campo status: pending e completed
        Se este teste falhar, quer dizer que nem todos os objetos tem status completed */
    }

    @Test
    public void validateDueOn() {
        List<String> dueOns = get(api).jsonPath().getList("due_on");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        List<LocalDateTime> dueDates = dueOns.stream().map(d -> LocalDateTime.parse(d, formatter)).toList();

        assertThat("All dates should be after today",
           dueDates.stream().allMatch(dd -> dd.isAfter(LocalDateTime.now())),
           is(true)
        );
    }

}