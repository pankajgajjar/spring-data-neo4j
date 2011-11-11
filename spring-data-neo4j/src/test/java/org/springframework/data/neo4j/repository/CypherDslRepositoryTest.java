/**
 * Copyright 2011 the original author or authors.
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
package org.springframework.data.neo4j.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.cypherdsl.CypherQuery;
import org.neo4j.cypherdsl.OrderBy;
import org.neo4j.cypherdsl.query.ReturnExpression;
import org.neo4j.cypherdsl.query.StartExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.neo4j.model.Person;
import org.springframework.data.neo4j.template.Neo4jOperations;
import org.springframework.test.context.CleanContextCacheTestExecutionListener;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertThat;
import static org.junit.internal.matchers.IsCollectionContaining.hasItems;
import static org.neo4j.helpers.collection.MapUtil.map;

/**
 * @author mh
 * @since 11.11.11
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:org/springframework/data/neo4j/repository/GraphRepositoryTest-context.xml"})
@TestExecutionListeners({CleanContextCacheTestExecutionListener.class, DependencyInjectionTestExecutionListener.class, TransactionalTestExecutionListener.class})
@Transactional
public class CypherDslRepositoryTest {

    @Autowired PersonRepository personRepository;
    @Autowired Neo4jOperations template;
    private TestTeam team;
    private Map<String,Object> peopleParams;
    private OrderBy query = CypherQuery.start(StartExpression.node("n", "people")).returns(ReturnExpression.nodes("n"));

    @Before
    public void setUp() throws Exception {
        team = new TestTeam().createSDGTeam(template);
        peopleParams = map("people", asList(team.michael.getId(), team.david.getId(), team.emil.getId()));
    }

    @Test
    public void testQueryPaged() throws Exception {
        final Page<Person> result = personRepository.query(query, peopleParams, new PageRequest(0, 2));
        assertThat(result.getContent(), hasItems(team.michael,team.david));
    }

    @Test
    public void testQuery() throws Exception {
        final List<Person> result = personRepository.query(query, peopleParams).as(List.class);
        assertThat(result, hasItems(team.michael, team.david, team.emil));
    }
}
