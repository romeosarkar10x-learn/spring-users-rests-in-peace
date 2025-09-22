package io.romeosarkar10x.learn.springusersrestsinpeace.Config;

import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.beans.factory.annotation.Value;

public class CassandraConfig extends AbstractCassandraConfiguration {
    @Value("${cassandra.keyspace:spring_users_rests_in_peace}")
    private String keyspaceName;

    @Value("${cassandra.contact-points:ip.romeosarkar10x.cfd")
    private String contactPoints;

    @Value("${cassandra.port:9042")
    private int port;

    @Value("${cassandra.local-datacenter:datacenter0")
    private String localDatacenter;


    @Override
    protected String getKeyspaceName() {
        return this.keyspaceName;
    }
}
