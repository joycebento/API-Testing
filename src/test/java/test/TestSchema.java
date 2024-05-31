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

    //End point a ser testada:
    String api = "https://gorest.co.in/public/v2/todos";

    @Test
    public void responseSchemaValidation(){
        given()
         // Entrada na API
        .when().get(api)
        .then().log().all()

            //Validação do Schema da response
            .assertThat().body(matchesJsonSchemaInClasspath("schema.json"))

            //Validação de status code e tipo de conteúdo da response
            .statusCode(200).contentType(ContentType.JSON);
    }

    @Test
    public void validateStatusCompleted() {
        given()

        //Entrada na API
        .when().get(api)

        //Validação de status dos usuários
        .then().log().all()
                .assertThat().body("status",not(hasItem("pending")));

        /* Os objetos retornados por esta API podem ter 2 valores no campo status: pending e completed
        Se este teste falhar, significa que nem todos os objetos tem status completed */
    }

    @Test
    public void validateDueOn() {

        //Captura de valores do campo "due_on"
        List<String> dueOns = get(api).jsonPath().getList("due_on");

        //Interpretação dos valores de "due_on" e conversão de formato
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        List<LocalDateTime> dueDates = dueOns.stream().map(d -> LocalDateTime.parse(d, formatter)).toList();

        //Verificação se todas as datas de "due_on" são posteriores à data atual
        assertThat("All dates should be after today",
           dueDates.stream().allMatch(dd -> dd.isAfter(LocalDateTime.now())),
           is(true)
        );
    }

}