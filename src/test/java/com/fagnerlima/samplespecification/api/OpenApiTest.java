package com.fagnerlima.samplespecification.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class OpenApiTest {

    @Autowired
    private MockMvcTester mvc;

    @Test
    void shouldDocumentFiltersAndPaginationAsQueryParameters() {
        assertThat(mvc.get().uri("/v3/api-docs"))
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.paths['/tasks'].get.parameters[*].name")
                .asArray()
                .contains("id", "date", "period.startDate", "period.endDate", "description", "status", "tagId",
                        "descriptionOrTag.description", "descriptionOrTag.tagDescription", "page", "size", "sort");
    }

    @Test
    void shouldServeSwaggerUi() {
        assertThat(mvc.get().uri("/swagger-ui/index.html")).hasStatusOk();
    }

}
