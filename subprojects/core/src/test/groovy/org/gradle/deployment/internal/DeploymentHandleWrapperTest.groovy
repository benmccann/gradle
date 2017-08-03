/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.deployment.internal

import org.gradle.testing.internal.util.Specification

class DeploymentHandleWrapperTest extends Specification {
    def delegate = Mock(DeploymentHandle)
    def id = "id"
    def wrapper = new DeploymentHandleWrapper(id, delegate)
    def failure = new Throwable()

    def "delegates to delegate"() {
        delegate.running >> true

        when:
        wrapper.buildSucceeded()
        then:
        1 * delegate.buildSucceeded()

        when:
        wrapper.buildFailed(failure)
        then:
        1 * delegate.buildFailed(failure)

        when:
        wrapper.pendingChanges(true)
        then:
        1 * delegate.pendingChanges(true)

        when:
        wrapper.pendingChanges(false)
        then:
        1 * delegate.pendingChanges(false)

        when:
        wrapper.stop()
        then:
        1 * delegate.stop()
    }

    def "handle must be running when receiving build status updates"() {
        delegate.running >> false
        when:
        wrapper.pendingChanges(true)
        then:
        thrown(IllegalStateException)

        when:
        wrapper.buildSucceeded()
        then:
        thrown(IllegalStateException)

        when:
        wrapper.buildFailed(failure)
        then:
        thrown(IllegalStateException)
    }

    def "only stops handle when underlying handle is running"() {
        when:
        wrapper.stop()
        then:
        1 * delegate.running >> false
        0 * _
    }
}