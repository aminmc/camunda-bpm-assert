package io.arctech.solutions.camunda

import org.camunda.bpm.engine.runtime.ProcessInstance
import org.camunda.bpm.engine.test.Deployment
import spock.lang.Narrative

import static com.github.tomakehurst.wiremock.http.Response.response
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.task


@Narrative('''
A specification that asserts the correct behaviour of a workflow
''')
@Deployment(resources = ['sample.bpmn'])
class SampleBPMNSpec extends BaseProcessModelSpecification {

    def 'can trigger workflow'() {
        given: 'web endpoint'

        wireMockStub.stub {
            request {
                method 'GET'
                url '/api/issues/122324'
            }
            response {
                status: 200
                body '''
                      {
                        "label": "broken table",
                        "code": 200
                         
                      }
                     '''
                headers {
                    "Content-Type" "application/json"
                }
            }
        }
        when: 'process instance is started'
        ProcessInstance processInstance = runtimeService()
                .createProcessInstanceByKey("sample-workflow")
                .execute()

        then: 'process instance should be active'
        assertThat(processInstance).isActive()

        and: 'task with broken table is created'
        assertThat(task()).hasName('Do something with broken table')
    }
}
