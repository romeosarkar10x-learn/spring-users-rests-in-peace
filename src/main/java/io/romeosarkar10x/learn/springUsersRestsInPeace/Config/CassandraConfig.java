package io.romeosarkar10x.learn.springUsersRestsInPeace.Config;

import org.jspecify.annotations.NullMarked;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.core.cql.keyspace.CreateKeyspaceSpecification;
import org.springframework.data.cassandra.core.cql.keyspace.KeyspaceOption;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableCassandraRepositories(basePackages="io.romeosarkar10x.learn.springUsersRestsInPeace.Repository")
public class CassandraConfig extends AbstractCassandraConfiguration {
    @Value("${cassandra.keyspace:spring_users_rests_in_peace}")
    private String keyspaceName;

    @Value("${cassandra.contact-points:ip.romeosarkar10x.cfd}")
    private String contactPoints;

    @Value("${cassandra.port:9042}")
    private int port;

    @Value("${cassandra.local-datacenter:datacenter1}")
    private String localDataCenter;


    @Override
    @NullMarked
    protected String getKeyspaceName() {
        return this.keyspaceName;
    }

    @Override
    @NullMarked
    public String getContactPoints() {
        return this.contactPoints;
    }

    @Override
    public int getPort() {
        return this.port;
    }

    @Override
    public String getLocalDataCenter() {
        return this.localDataCenter;
    }

    @Override
    @NullMarked
    public String[] getEntityBasePackages() {
        return new String[]{"io.romeosarkar10x.learn.springUsersRestsInPeace.Model"};
    }

    @Override
    @NullMarked
    public List<CreateKeyspaceSpecification> getKeyspaceCreations() {
        return Collections.singletonList(
                CreateKeyspaceSpecification.createKeyspace(this.keyspaceName)
                        .ifNotExists()
                        .with(KeyspaceOption.DURABLE_WRITES, true)
                        .withSimpleReplication(1)
        );
    }

    /*
    @Override
    protected boolean getMetricsEnabled() {
        return false;
    }
     */

    @Override
    @NullMarked
    public SchemaAction getSchemaAction() {
        return SchemaAction.CREATE_IF_NOT_EXISTS;
    }
}
