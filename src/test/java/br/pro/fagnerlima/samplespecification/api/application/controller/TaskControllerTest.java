package br.pro.fagnerlima.samplespecification.api.application.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
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
 * Runs against the seed data of {@code V001__init.sql}:
 *
 * <pre>
 * id | period                  | description        | status   | tags
 *  1 | 2020-08-08 - 2020-08-08 | My wife's birthday | DONE     | 5 Event
 *  2 | 2020-08-09 - 2020-08-10 | Freelancer 1       | DONE     | 1 Work
 *  3 | 2020-08-11 - 2020-08-11 | Running            | CANCELED | 6 Training
 *  4 | 2020-08-11 - 2020-08-13 | Freelancer 2       | PENDING  | (none)
 *  5 | 2020-08-14 - 2020-08-14 | Swimming           | PENDING  | 6 Training
 *  6 | 2020-09-01 - 2020-09-05 | Workshop           | PENDING  | 2 Dev
 *  7 | 2020-09-10 - 2020-09-25 | Spring course      | PENDING  | 2 Dev, 3 Study
 *  8 | 2020-09-16 - 2020-09-20 | Angular Course     | PENDING  | 2 Dev, 3 Study
 *  9 | 2020-09-21 - 2020-09-25 | Flutter couse      | PENDING  | 2 Dev, 3 Study
 * 10 | 2020-10-15 - 2020-10-15 | My birthday        | PENDING  | 5 Event
 * </pre>
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class TaskControllerTest {

    private static final String SPECIFICATION_URI = "/tasks";

    private static final String JPA_CRITERIA_URI = "/tasks/jpa-criteria";

    private static final String SORTED_BY_ID = "sort=id,asc&size=20";

    @Autowired
    private MockMvcTester mvc;

    static Stream<Arguments> filters() {
        return Stream.of(
                Arguments.of("", List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)),
                Arguments.of("unknown=ignored", List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)),
                Arguments.of("id=3", List.of(3)),
                Arguments.of("id=99", List.of()),
                Arguments.of("date=2020-08-10", List.of(2)),
                Arguments.of("date=2020-08-11", List.of(3, 4)),
                Arguments.of("date=2020-09-22", List.of(7, 9)),
                Arguments.of("date=2020-07-01", List.of()),
                Arguments.of("period.startDate=2020-08-10&period.endDate=2020-08-12", List.of(2, 3, 4)),
                Arguments.of("period.startDate=2020-09-01&period.endDate=2020-09-30", List.of(6, 7, 8, 9)),
                Arguments.of("period.startDate=2020-08-14&period.endDate=2020-08-14", List.of(5)),
                // A period with only one of its dates is ignored by both implementations.
                Arguments.of("period.startDate=2020-09-01", List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)),
                // Known limitation, shared by both implementations: the period matches tasks that start or end
                // inside the range, not tasks that fully contain it (7 and 8 cover 2020-09-17 to 2020-09-19).
                Arguments.of("period.startDate=2020-09-17&period.endDate=2020-09-19", List.of()),
                Arguments.of("description=BÍRTHDAY", List.of(1, 10)),
                Arguments.of("description=wife's", List.of(1)),
                Arguments.of("description=spring course", List.of(7)),
                Arguments.of("description=cóurse", List.of(7, 8)),
                Arguments.of("description=freelancer", List.of(2, 4)),
                Arguments.of("description=nothing like this", List.of()),
                Arguments.of("description=", List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)),
                Arguments.of("status=PENDING", List.of(4, 5, 6, 7, 8, 9, 10)),
                Arguments.of("status=DONE", List.of(1, 2)),
                Arguments.of("status=PROGRESS", List.of()),
                Arguments.of("tagId=2", List.of(6, 7, 8, 9)),
                Arguments.of("tagId=2,3", List.of(6, 7, 8, 9)),
                Arguments.of("tagId=5&tagId=6", List.of(1, 3, 5, 10)),
                Arguments.of("tagId=4", List.of()),
                // An empty tagId binds to an empty list, which both implementations turn into an IN that matches
                // nothing.
                Arguments.of("tagId=", List.of()),
                Arguments.of("descriptionOrTag.description=birthday&descriptionOrTag.tagDescription=training",
                        List.of(1, 3, 5, 10)),
                Arguments.of("descriptionOrTag.description=angular&descriptionOrTag.tagDescription=WÓRK",
                        List.of(2, 8)),
                // Known limitation, shared by both implementations: the tag join inside the OR group is an INNER
                // join, so task 4 ("Freelancer 2", without tags) is not matched by its description.
                Arguments.of("descriptionOrTag.description=freelancer&descriptionOrTag.tagDescription=study",
                        List.of(2, 7, 8, 9)),
                Arguments.of("descriptionOrTag.description=freelancer", List.of(2, 4)),
                Arguments.of("descriptionOrTag.tagDescription=dev", List.of(6, 7, 8, 9)),
                // Blank values, as sent by a form that submits every field, are ignored like absent ones.
                Arguments.of("descriptionOrTag.tagDescription=", List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)),
                Arguments.of("descriptionOrTag.tagDescription= ", List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)),
                Arguments.of("descriptionOrTag.description=&descriptionOrTag.tagDescription=",
                        List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)),
                Arguments.of("descriptionOrTag.description=freelancer&descriptionOrTag.tagDescription=",
                        List.of(2, 4)),
                Arguments.of("descriptionOrTag.description=&descriptionOrTag.tagDescription=study",
                        List.of(7, 8, 9)),
                Arguments.of("status=PENDING&tagId=3&description=course", List.of(7, 8)),
                Arguments.of("date=2020-09-18&tagId=2&descriptionOrTag.description=angular"
                        + "&descriptionOrTag.tagDescription=work", List.of(8)),
                Arguments.of("period.startDate=2020-08-01&period.endDate=2020-08-31&status=PENDING", List.of(4, 5)),
                Arguments.of("description=birthday&tagId=5&status=DONE", List.of(1)));
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("filters")
    void specificationAndJpaCriteriaShouldReturnTheSameTasks(String filter, List<Integer> expectedIds) {
        TaskPage specificationPage = search(SPECIFICATION_URI, join(filter, SORTED_BY_ID));
        TaskPage jpaCriteriaPage = search(JPA_CRITERIA_URI, join(filter, SORTED_BY_ID));

        assertThat(specificationPage.ids()).as("SpecBuilder ids").isEqualTo(expectedIds);
        assertThat(specificationPage.totalElements()).as("SpecBuilder totalElements").isEqualTo(expectedIds.size());
        assertThat(jpaCriteriaPage.ids()).as("JPA Criteria ids").isEqualTo(expectedIds);
        assertThat(jpaCriteriaPage.totalElements()).as("JPA Criteria totalElements").isEqualTo(expectedIds.size());
    }

    @ParameterizedTest
    @MethodSource("bothEndpoints")
    void shouldPaginateAndSortByNestedProperty(String uri) {
        TaskPage page = search(uri, "page=1&size=3&sort=period.startDate,desc&sort=id,desc");

        assertThat(page.ids()).containsExactly(7, 6, 5);
        assertThat(page.totalElements()).isEqualTo(10);
        assertThat(page.totalPages()).isEqualTo(4);
    }

    @ParameterizedTest
    @MethodSource("bothEndpoints")
    void shouldPaginateDistinctTasksWhenFilteringByManyTags(String uri) {
        TaskPage firstPage = search(uri, "tagId=2,3&page=0&size=3&sort=period.startDate,asc");
        TaskPage lastPage = search(uri, "tagId=2,3&page=1&size=3&sort=period.startDate,asc");

        assertThat(firstPage.ids()).containsExactly(6, 7, 8);
        assertThat(lastPage.ids()).containsExactly(9);
        assertThat(firstPage.totalElements()).isEqualTo(4);
        assertThat(firstPage.totalPages()).isEqualTo(2);
    }

    /**
     * Documents a difference between the implementations: SpecBuilder ignores a {@code @SpecPeriod} whose start date
     * is after its end date, while the hand-written query keeps it and matches nothing.
     */
    @Test
    void shouldDifferOnPeriodWithStartDateAfterEndDate() {
        String filter = join("period.startDate=2020-08-12&period.endDate=2020-08-10", SORTED_BY_ID);

        assertThat(search(SPECIFICATION_URI, filter).ids()).containsExactly(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        assertThat(search(JPA_CRITERIA_URI, filter).ids()).isEmpty();
    }

    @Test
    void shouldReturnTaskWithIsoDatesAndStablePageJson() {
        assertThat(mvc.get().uri(SPECIFICATION_URI).param("id", "7"))
                .hasStatusOk()
                .bodyJson()
                .isStrictlyEqualTo("""
                        {
                          "content": [
                            {
                              "id": 7,
                              "period": { "startDate": "2020-09-10", "endDate": "2020-09-25" },
                              "description": "Spring course",
                              "status": "PENDING",
                              "tags": [ { "id": 2, "description": "Dev" }, { "id": 3, "description": "Study" } ]
                            }
                          ],
                          "page": { "size": 20, "number": 0, "totalElements": 1, "totalPages": 1 }
                        }
                        """);
    }

    @Test
    void shouldNotExposePageImplInternals() {
        DocumentContext json = JsonPath.parse(contentOf(mvc.get().uri(SPECIFICATION_URI).exchange()));

        assertThat(json.read("$", Map.class)).containsOnlyKeys("content", "page");
        assertThat(json.read("$.page", Map.class)).containsOnlyKeys("size", "number", "totalElements", "totalPages");
    }

    @Test
    void shouldFindTasksThatHaveTags() {
        TaskPage page = search("/tasks/has-tags", SORTED_BY_ID);

        assertThat(page.ids()).containsExactly(1, 2, 3, 5, 6, 7, 8, 9, 10);
        assertThat(page.totalElements()).isEqualTo(9);
    }

    @Test
    void shouldFindTasksByStatus() {
        assertThat(search("/tasks/status/PENDING", SORTED_BY_ID).ids()).containsExactly(4, 5, 6, 7, 8, 9, 10);
        assertThat(search("/tasks/status/CANCELED", SORTED_BY_ID).ids()).containsExactly(3);
        assertThat(search("/tasks/status/PROGRESS", SORTED_BY_ID).ids()).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("invalidStatusRequests")
    void shouldRejectInvalidStatus(String uri) {
        assertThat(mvc.get().uri(uri)).hasStatus(400);
    }

    static Stream<String> bothEndpoints() {
        return Stream.of(SPECIFICATION_URI, JPA_CRITERIA_URI);
    }

    static Stream<String> invalidStatusRequests() {
        return Stream.of("/tasks/status/UNKNOWN", "/tasks/status/pending", "/tasks?status=UNKNOWN",
                "/tasks/jpa-criteria?status=UNKNOWN");
    }

    private TaskPage search(String uri, String query) {
        MockMvcRequestBuilder request = mvc.get().uri(uri);

        for (String parameter : query.split("&")) {
            if (parameter.isEmpty()) {
                continue;
            }

            String[] nameAndValue = parameter.split("=", 2);
            request.param(nameAndValue[0], nameAndValue[1]);
        }

        MvcTestResult result = request.exchange();
        assertThat(result).as("GET %s?%s", uri, query).hasStatusOk();
        DocumentContext json = JsonPath.parse(contentOf(result));

        return new TaskPage(json.read("$.content[*].id"), json.read("$.page.totalElements"),
                json.read("$.page.totalPages"));
    }

    private static String contentOf(MvcTestResult result) {
        try {
            return result.getResponse().getContentAsString();
        } catch (UnsupportedEncodingException exception) {
            throw new IllegalStateException(exception);
        }
    }

    private static String join(String filter, String query) {
        return filter.isEmpty() ? query : filter + "&" + query;
    }

    private record TaskPage(List<Integer> ids, int totalElements, int totalPages) {
    }

}
