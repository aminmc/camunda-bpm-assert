package io.arctech.solutions.camunda

import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.github.tomjankes.wiremock.WireMockGroovy
import org.camunda.bpm.engine.test.ProcessEngineRule
import org.camunda.bpm.engine.test.mock.Mocks
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageProcessEngineRuleBuilder
import org.junit.ClassRule
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.repositoryService
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService

@Ignore
class BaseProcessModelSpecification extends Specification {

    def static wmPort = 8012

    @ClassRule
    @Shared
    WireMockRule wireMockRule = new WireMockRule(wmPort)

    public wireMockStub = new WireMockGroovy(wmPort)


    @Shared
    @ClassRule
    public ProcessEngineRule processEngineRule = TestCoverageProcessEngineRuleBuilder
            .create()
            .withDetailedCoverageLogging()
            .build()


    def cleanupSpec() {
        repositoryService().createDeploymentQuery().list().each { it ->
            repositoryService().deleteDeployment(it.getId(), true)
        }
    }

    def setup() {
        runtimeService().createProcessInstanceQuery()
                .list().each { it -> runtimeService().deleteProcessInstance(it.id, 'deleted') }
    }

    def cleanup() {
        Mocks.reset()
    }
}
