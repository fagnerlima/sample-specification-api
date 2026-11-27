package br.pro.fagnerlima.samplespecification.api.application.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MockMvcTester.MockMvcRequestBuilder;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import br.pro.fagnerlima.samplespecification.api.TestcontainersConfiguration;

/**
 * Acceptance tests written by QA from the acceptance criteria of the Spring Boot 4 upgrade, against the seed data of
 * {@code V001__init.sql} (see {@link TaskControllerTest} for the table).
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class TaskAcceptanceTest {

    // Tasks 3 and 4 start on the same day, so the id breaks the tie and keeps the order deterministic.
    private static final String POSTMAN_PAGINATION = "page=0&size=10&sort=period.startDate,asc&sort=id,asc";

    private static final List<Integer> ALL_BY_START_DATE = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

    @Autowired
    private MockMvcTester mvc;

    /** AC1: every request of the Postman collection of master, with its enabled query parameters. */
    static Stream<Arguments> postmanRequests() {
        return Stream.of(
                Arguments.of("/tasks", ALL_BY_START_DATE),
                Arguments.of("/tasks/jpa-criteria", ALL_BY_START_DATE),
                Arguments.of("/tasks/has-tags", List.of(1, 2, 3, 5, 6, 7, 8, 9, 10)),
                Arguments.of("/tasks/status/PENDING", List.of(4, 5, 6, 7, 8, 9, 10)));
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("postmanRequests")
    void ac1PostmanCollectionRequestsShouldStillWork(String uri, List<Integer> expectedIds) {
        TaskPage page = search(uri, POSTMAN_PAGINATION);

        assertThat(page.ids()).isEqualTo(expectedIds);
        assertThat(page.size()).isEqualTo(10);
        assertThat(page.number()).isZero();
    }

    /** AC1 and AC2: the disabled query parameters of the Postman collection, one at a time and all together. */
    static Stream<Arguments> postmanFilters() {
        return Stream.of(
                Arguments.of("id=1", List.of(1)),
                Arguments.of("date=2020-09-03", List.of(6)),
                Arguments.of("period.startDate=2020-09-05&period.endDate=2020-09-10", List.of(6, 7)),
                Arguments.of("description=birthday", List.of(1, 10)),
                Arguments.of("status=PENDING", List.of(4, 5, 6, 7, 8, 9, 10)),
                Arguments.of("tagId=3,2", List.of(6, 7, 8, 9)),
                Arguments.of("descriptionOrTag.description=freelancer", List.of(2, 4)),
                Arguments.of("descriptionOrTag.tagDescription=study", List.of(7, 8, 9)),
                Arguments.of("descriptionOrTag.description=freelancer&descriptionOrTag.tagDescription=study",
                        List.of(2, 7, 8, 9)),
                Arguments.of("id=1&date=2020-09-03&period.startDate=2020-09-05&period.endDate=2020-09-10"
                        + "&description=birthday&status=PENDING&tagId=3,2&descriptionOrTag.description=freelancer"
                        + "&descriptionOrTag.tagDescription=study", List.of()));
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("postmanFilters")
    void ac1PostmanCollectionFiltersShouldReturnTheSameTasksOnBothEndpoints(String filter,
            List<Integer> expectedIds) {
        String query = POSTMAN_PAGINATION + "&" + filter;

        assertThat(search("/tasks", query).ids()).as("SpecBuilder").isEqualTo(expectedIds);
        assertThat(search("/tasks/jpa-criteria", query).ids()).as("JPA Criteria").isEqualTo(expectedIds);
    }

    static Stream<String> pagedEndpoints() {
        return Stream.of("/tasks", "/tasks/jpa-criteria", "/tasks/has-tags", "/tasks/status/PENDING");
    }

    /** AC3: a page beyond the last one is an empty page, not an error. */
    @ParameterizedTest
    @MethodSource("pagedEndpoints")
    void ac3PageBeyondRangeShouldReturnAnEmptyPage(String uri) {
        TaskPage page = search(uri, "page=99&size=5");

        assertThat(page.ids()).isEmpty();
        assertThat(page.number()).isEqualTo(99);
        assertThat(page.size()).isEqualTo(5);
        assertThat(page.totalElements()).isPositive();
    }

    /** AC3: invalid pagination values fall back to the defaults instead of failing. */
    @ParameterizedTest
    @MethodSource("pagedEndpoints")
    void ac3InvalidPaginationValuesShouldFallBackToDefaults(String uri) {
        for (String query : List.of("size=0", "size=-5", "size=abc", "page=-1", "page=abc")) {
            TaskPage page = search(uri, query);

            assertThat(page.size()).as("size for %s", query).isEqualTo(20);
            assertThat(page.number()).as("number for %s", query).isZero();
        }
    }

    @ParameterizedTest
    @MethodSource("pagedEndpoints")
    void ac3PageSizeShouldBeCappedAtTheDefaultMaximum(String uri) {
        assertThat(search(uri, "size=100000").size()).isEqualTo(2000);
    }

    static Stream<Arguments> invalidFilterValues() {
        return Stream.of("date=2020-13-45", "date=22/09/2020", "period.startDate=xx&period.endDate=2020-09-10",
                "period.startDate=2020-09-01&period.endDate=2020-02-30", "id=abc", "tagId=a,b", "status=pending")
                .flatMap(query -> Stream.of(Arguments.of("/tasks", query), Arguments.of("/tasks/jpa-criteria", query)));
    }

    /** AC4: malformed filter values are a client error (400), not a server error. */
    @ParameterizedTest(name = "[{index}] {0}?{1}")
    @MethodSource("invalidFilterValues")
    void ac4MalformedFilterValuesShouldReturnBadRequest(String uri, String query) {
        assertThat(request(uri, query).exchange()).hasStatus(400);
    }

    /** AC4: the Swagger UI path given in the README redirects to the UI. */
    @Test
    void ac4SwaggerUiPathFromReadmeShouldRedirectToTheUi() {
        assertThat(mvc.get().uri("/swagger-ui.html"))
                .hasStatus3xxRedirection()
                .hasRedirectedUrl("/swagger-ui/index.html");
    }

    private MockMvcRequestBuilder request(String uri, String query) {
        MockMvcRequestBuilder request = mvc.get().uri(uri);

        for (String parameter : query.split("&")) {
            if (parameter.isEmpty()) {
                continue;
            }

            String[] nameAndValue = parameter.split("=", 2);
            request.param(nameAndValue[0], nameAndValue.length > 1 ? nameAndValue[1] : "");
        }

        return request;
    }

    private TaskPage search(String uri, String query) {
        MvcTestResult result = request(uri, query).exchange();
        assertThat(result).as("GET %s?%s", uri, query).hasStatusOk();
        DocumentContext json = JsonPath.parse(contentOf(result));

        return new TaskPage(json.read("$.content[*].id"), json.read("$.page.totalElements"), json.read("$.page.size"),
                json.read("$.page.number"));
    }

    private static String contentOf(MvcTestResult result) {
        try {
            return result.getResponse().getContentAsString();
        } catch (UnsupportedEncodingException exception) {
            throw new IllegalStateException(exception);
        }
    }

    private record TaskPage(List<Integer> ids, int totalElements, int size, int number) {
    }

}
