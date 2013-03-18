/*
 * Copyright 2011 JBoss Inc
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

package org.drools.benchmark.benchmarks;

import org.drools.benchmark.BenchmarkDefinition;
import org.kie.internal.KnowledgeBase;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.rule.FactHandle;

public class InsertAllAndRetract extends AbstractBenchmark {

    private static StatefulKnowledgeSession ksession;

    private String[] drlFiles;

    private final int objectsNumber;
    private FactHandle[] facts;

    public InsertAllAndRetract(int objectsNumber) {
        this.objectsNumber = objectsNumber;
    }

    public InsertAllAndRetract(int objectsNumber, String drlFile) {
        this(objectsNumber);
        this.drlFiles = drlFile.split(",");
    }

    @Override
    public void init(BenchmarkDefinition definition, boolean isFirst) {
        if (isFirst) {
            KnowledgeBase kbase = createKnowledgeBase(createKnowledgeBuilder(drlFiles));
            ksession = kbase.newStatefulKnowledgeSession();
        }
        facts = new FactHandle[objectsNumber];
    }

    public void execute(int repNr) {
        for (int i = 0; i < objectsNumber; i++) {
            facts[i] = ksession.insert(new Integer(i));
        }
        ksession.fireAllRules();
        for (FactHandle fact : facts) {
            ksession.retract(fact);
        }
        ksession.fireAllRules();
    }

    @Override
    public void terminate(boolean isLast) {
        if (isLast) {
            ksession.dispose(); // Stateful rule session must always be disposed when finished
        }
    }

    public InsertAllAndRetract clone() {
        InsertAllAndRetract clone = new InsertAllAndRetract(objectsNumber);
        clone.drlFiles = drlFiles;
        return clone;
    }
}
